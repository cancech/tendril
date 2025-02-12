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

import java.util.ArrayList;
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

import tendril.bean.Consumer;
import tendril.bean.Provider;
import tendril.bean.recipe.Applicator;
import tendril.bean.recipe.Descriptor;
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

/**
 * Processor for the {@link Provider} annotation, which will generate the appropriate Recipe for the specified Provider
 */
@SupportedAnnotationTypes("tendril.bean.Provider")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class ProviderProcessor extends AbstractTendrilProccessor {
    
    /** Flag for whether the generated recipe is to be annotated with @{@link Registry} */
    private final boolean annotateRegistry;
    
    /**
     * CTOR - will be annotated as a {@link Registry}
     */
    public ProviderProcessor() {
        this(true);
    }
    
    /**
     * CTOR
     * 
     * @param annotateRegistry boolean true if it is to be annotated with @{@link Registry}
     */
    protected ProviderProcessor(boolean annotateRegistry) {
        this.annotateRegistry = annotateRegistry;
    }

    /**
     * @see tendril.processor.AbstractTendrilProccessor#processType(tendril.codegen.field.type.ClassType)
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
        
        List<String> ctorCode = new ArrayList<>();
        ctorCode.add("super(engine, " + bean.getSimpleName() + ".class);");
        Map<ElementKind, List<Element>> consumers = getEnclosedElements(Consumer.class);
        generateFieldConsumers(externalImports, bean, ctorCode, consumers.get(ElementKind.FIELD));
        
        JClass parent = ClassBuilder.forConcreteClass(SingletonRecipe.class).addGeneric(GenericFactory.create(bean)).build();
        ClassBuilder clsBuilder = ClassBuilder.forConcreteClass(recipe).setVisibility(VisibilityType.PUBLIC).extendsClass(parent)
                .buildConstructor().setVisibility(VisibilityType.PUBLIC)
                    .buildParameter(new ClassType(Engine.class), "engine").finish()
                    .addCode(ctorCode.toArray(new String[ctorCode.size()])).finish();
        if (annotateRegistry)
            clsBuilder.addAnnotation(JAnnotationFactory.create(Registry.class));
        JClass cls = clsBuilder.build();
        return cls.generateCode(externalImports);
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
            
            ctorLines.add("registerDependency(new " + Descriptor.class.getSimpleName() + "<>(" + varType.getSimpleName() + ".class), new " +
                    Applicator.class.getSimpleName() + "<" + bean.getSimpleName() + ", " + varType.getSimpleName() + ">() {");
            ctorLines.add("    @Override");
            ctorLines.add("    public void apply(" + bean.getSimpleName() + " consumer, " + varType.getSimpleName() + " bean) {");
            ctorLines.add("        consumer." + e.getSimpleName() + " = bean;");
            ctorLines.add("    }");
            ctorLines.add("});");
        }
    }

    /**
     * @see tendril.processor.AbstractTendrilProccessor#processMethod(tendril.codegen.field.type.ClassType, tendril.codegen.classes.method.JMethod)
     */
    @Override
    protected ClassDefinition processMethod(ClassType classData, JMethod<?> methodData) {
        throw new NotImplementedException();
    }
}
