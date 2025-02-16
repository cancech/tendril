/*
 * Copyright 2024 Jaroslav Bosak
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/license/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tendril.processor;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;

import org.apache.commons.lang3.NotImplementedException;

import com.google.auto.service.AutoService;

import tendril.annotationprocessor.AbstractTendrilProccessor;
import tendril.annotationprocessor.ClassDefinition;
import tendril.annotationprocessor.ProcessingException;
import tendril.bean.Inject;
import tendril.bean.Singleton;
import tendril.bean.qualifier.Named;
import tendril.bean.Bean;
import tendril.bean.Factory;
import tendril.bean.recipe.AbstractRecipe;
import tendril.bean.recipe.Applicator;
import tendril.bean.recipe.Descriptor;
import tendril.bean.recipe.FactoryRecipe;
import tendril.bean.recipe.Registry;
import tendril.bean.recipe.SingletonRecipe;
import tendril.codegen.VisibilityType;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.type.VoidType;
import tendril.codegen.generics.GenericFactory;
import tendril.context.Engine;
import tendril.util.TendrilStringUtil;

/**
 * Processor for the {@link Bean} annotation, which will generate the appropriate Recipe for the specified Provider
 */
@SupportedAnnotationTypes("tendril.bean.Bean")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class BeanProcessor extends AbstractTendrilProccessor {
    
    /** Flag for whether the generated recipe is to be annotated with @{@link Registry} */
    private final boolean annotateRegistry;
    /** Mapping of the types of life cycle annotations that are supported to the recipe that implements it */
    @SuppressWarnings("rawtypes")
    protected final Map<Class<? extends Annotation>, Class<? extends AbstractRecipe>> recipeTypeMap = new HashMap<>();
    
    /**
     * CTOR - will be annotated as a {@link Registry}
     */
    public BeanProcessor() {
        this(true);
    }
    
    /**
     * CTOR
     * 
     * @param annotateRegistry boolean true if it is to be annotated with @{@link Registry}
     */
    protected BeanProcessor(boolean annotateRegistry) {
        this.annotateRegistry = annotateRegistry;
        registerAvailableRecipeTypes();
    }
    
    /**
     * Register the life cycle annotations that are to be supported, to the type of recipe that is to be used when it is employed. By default Singleton and Factory are registered
     * and supported.
     */
    protected void registerAvailableRecipeTypes() {
        recipeTypeMap.put(Singleton.class, SingletonRecipe.class);
        recipeTypeMap.put(Factory.class, FactoryRecipe.class);
    }

    /**
     * @see tendril.annotationprocessor.AbstractTendrilProccessor#processType(tendril.codegen.field.type.ClassType)
     */
    @Override
    protected ClassDefinition processType(ClassType data) {
        ClassType providerClass = data.generateFromClassSuffix("Recipe");
        return new ClassDefinition(providerClass, generateCode(providerClass, data));
    }

    /**
     * Generate the code for the recipe that is to act as the provider for the bean
     * 
     * @param recipe {@link ClassType} for the recipe that is to be generated
     * @param bean {@link ClassType} for the bean that is to be provided
     * @return {@link String} containing the code for the recipe
     */
    private String generateCode(ClassType recipe, ClassType bean) {
        Set<ClassType> externalImports = new HashSet<>();
        
        // The parent class
        JClass parent = ClassBuilder.forConcreteClass(getRecipeClass()).addGeneric(GenericFactory.create(bean)).build();
        
        // CTOR contents
        List<String> ctorCode = new ArrayList<>();
        ctorCode.add("super(engine, " + bean.getSimpleName() + ".class);");
        Map<ElementKind, List<Element>> consumers = getEnclosedElements(Inject.class);
        generateFieldConsumers(externalImports, bean, ctorCode, consumers.get(ElementKind.FIELD));
        
        // Bean descriptor
        ClassType descriptorClass = new ClassType(Descriptor.class);
        descriptorClass.addGeneric(GenericFactory.create(bean));
        
        ClassBuilder clsBuilder = ClassBuilder.forConcreteClass(recipe).setVisibility(VisibilityType.PUBLIC).extendsClass(parent)
                .buildConstructor().setVisibility(VisibilityType.PUBLIC)
                    .buildParameter(new ClassType(Engine.class), "engine").finish()
                    .addCode(ctorCode.toArray(new String[ctorCode.size()])).finish()
                .buildMethod("setupDescriptor").addAnnotation(JAnnotationFactory.create(Override.class)).setVisibility(VisibilityType.PUBLIC)
                    .buildParameter(descriptorClass, "descriptor").finish()
                    .addCode(getBeanDescriptorContents("descriptor")).finish();
        if (annotateRegistry)
            clsBuilder.addAnnotation(JAnnotationFactory.create(Registry.class));
        JClass cls = clsBuilder.build();
        return cls.generateCode(externalImports);
    }
    
    /**
     * Get the recipe class that is to be employed for the indicated bean.
     * 
     * @return {@link Class} extending {@link AbstractRecipe} representing the concrete recipe that is to be used for the bean
     */
    @SuppressWarnings("rawtypes")
    protected Class<? extends AbstractRecipe> getRecipeClass() {
        List<Class<? extends Annotation>> foundTypes = new ArrayList<>();
        
        for(Class<? extends Annotation> annonClass: recipeTypeMap.keySet()) {
            if (!getElementAnnotations(annonClass).isEmpty())
                foundTypes.add(annonClass);
        }
        
        if (foundTypes.isEmpty())
            throw new ProcessingException(getCurrentElement() + " must have a single life cycle indicated");
        if (foundTypes.size() > 1)
            throw new ProcessingException(getCurrentElement() + "has multiple life cycles indicated [" + TendrilStringUtil.join(foundTypes) + "]");
        
        return recipeTypeMap.get(foundTypes.get(0));
    }
    
    /**
     * Generate the appropriate code for consumers that are fields within the bean.
     * 
     * @param externalImports {@link Set} where the {@link ClassType}s to be imported by the generated recipe class are stored
     * @param bean {@link ClassType} of the bean which contains the destination consumers
     * @param ctorLines {@link List} of {@link String} lines that are already present in the recipe constructor
     * @param elements {@link List} of {@link Element}s that have been annotated as consumers
     */
    private void generateFieldConsumers(Set<ClassType> externalImports, ClassType bean, List<String> ctorLines, List<Element> elements) {
        if (elements == null)
            return;
        
        for (Element e: elements) {
            Type varType = VoidType.INSTANCE;
            
            if (e instanceof VariableElement)
                varType = variableType((VariableElement) e);
            
            if (varType instanceof ClassType)
                externalImports.add((ClassType) varType);
            externalImports.add(new ClassType(Applicator.class));
            externalImports.add(new ClassType(Descriptor.class));
            
            ctorLines.add("registerDependency(" + getDependencyDescriptor(e, varType.getSimpleName()) + ", new " +
                    Applicator.class.getSimpleName() + "<" + bean.getSimpleName() + ", " + varType.getSimpleName() + ">() {");
            ctorLines.add("    @Override");
            ctorLines.add("    public void apply(" + bean.getSimpleName() + " consumer, " + varType.getSimpleName() + " bean) {");
            ctorLines.add("        consumer." + e.getSimpleName() + " = bean;");
            ctorLines.add("    }");
            ctorLines.add("});");
        }
    }
    
    /**
     * Get the code for the descriptor that is to be applied to a dependency of the bean defined by this recipe
     * 
     * @param e {@link Element} which defines the dependency
     * @param depClassName {@link String} the class name of the dependency (i.e.: single name)
     * @return {@link String} containing the code defining the dependency
     */
    private String getDependencyDescriptor(Element e, String depClassName) {
        String desc = "new " + Descriptor.class.getSimpleName() + "<>(" + depClassName + ".class)";
        desc += appendInline(getDescriptorName(e, getElementAnnotations(e, Named.class)));
        return desc;
    }
    
    /**
     * Helper which converts the text to append to the appropriate in-line code 
     * 
     * @param toAppend {@link String} which is to be appended
     * @return {@link String} that is to be appended
     */
    private String appendInline(String toAppend) {
        // Nothing needs to be done with it is blank
        if (toAppend.isBlank())
            return "";

        // Otherwise move the text to the next line with the appropriate indentation spacing
        return "\n            " + toAppend;
    }
    
    /**
     * Get the code through which the name is applied to the Descriptor
     * 
     * @param e {@link Element} whose name it being determined
     * @param names {@link List} of {@link Named} annotation that have been applied to the element
     * @return {@link String} containing the code with the appropriate descriptor update
     * @throws ProcessingException if more than one name is applied to the bean
     */
    private String getDescriptorName(Element e, List<Named> names) {
        if (names.isEmpty())
            return "";
        if (names.size() > 1)
            // This situation is prevented by the compiler
            throw new ProcessingException(e + " cannot have more than one name");
        
        return ".setName(\"" + names.get(0).value() + "\")";
    }
    
    /**
     * Get the code which applies the defined description of the bean to the descriptor under the specified variable name
     * 
     * @param varName {@link String} the variable name of the descriptor which is to be populated
     * @return {@link String}[] containing all of the lines of code which apply the defined bean description
     */
    private String[] getBeanDescriptorContents(String varName) {
        return new String[] {createDescriptorContentsLine(varName, getDescriptorName(getCurrentElement(), getElementAnnotations(Named.class)))};
    }
    
    /**
     * Create the line of code which applies the customization to the descriptor
     * 
     * @param varName {@link String} the name of the variable of the descriptor to which the details are applied
     * @param toAppend {@link String} the code which is to be applied to the descriptor
     * @return {@link String} the final line of code
     */
    private String createDescriptorContentsLine(String varName, String toAppend) {
        if (toAppend.isBlank())
            return "";
        
        return varName + toAppend + ";";
    }

    /**
     * @see tendril.annotationprocessor.AbstractTendrilProccessor#processMethod(tendril.codegen.field.type.ClassType, tendril.codegen.classes.method.JMethod)
     */
    @Override
    protected ClassDefinition processMethod(ClassType classData, JMethod<?> methodData) {
        // TODO allow for the creation of configuration/factory classes
        throw new NotImplementedException();
    }
}
