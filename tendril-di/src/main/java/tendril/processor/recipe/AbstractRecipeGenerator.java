/*
 * Copyright 2025 Jaroslav Bosak
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
package tendril.processor.recipe;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Messager;

import tendril.annotationprocessor.ClassDefinition;
import tendril.annotationprocessor.exception.InvalidConfigurationException;
import tendril.annotationprocessor.exception.TendrilException;
import tendril.bean.Configuration;
import tendril.bean.Factory;
import tendril.bean.InjectAll;
import tendril.bean.Singleton;
import tendril.bean.qualifier.Descriptor;
import tendril.bean.qualifier.EnumQualifier;
import tendril.bean.qualifier.Named;
import tendril.bean.qualifier.Qualifier;
import tendril.bean.recipe.AbstractRecipe;
import tendril.bean.recipe.ConfigurationRecipe;
import tendril.bean.recipe.FactoryRecipe;
import tendril.bean.recipe.Registry;
import tendril.bean.recipe.SingletonRecipe;
import tendril.bean.requirement.Requirement;
import tendril.bean.requirement.RequiresEnv;
import tendril.bean.requirement.RequiresNotEnv;
import tendril.codegen.JBase;
import tendril.codegen.VisibilityType;
import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.EnumerationEntry;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.JParameter;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.JType;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.value.JValue;
import tendril.codegen.generics.GenericFactory;
import tendril.context.Engine;
import tendril.context.launch.Runner;
import tendril.util.TendrilStringUtil;

/**
 * The base class for recipe generation. This provides the core, common, and reusable elements which can then be leverages by specific generators
 * 
 * @param <CREATOR> extending {@link JBase} indicating the type of element for which the recipe is being generated
 */
public abstract class AbstractRecipeGenerator<CREATOR extends JBase> {
    
    /** Mapping of the types of life cycle annotations that are supported to the recipe that implements it */
    @SuppressWarnings("rawtypes")
    private static final Map<Class<? extends Annotation>, Class<? extends AbstractRecipe>> recipeTypeMap = 
        Map.of(Singleton.class, SingletonRecipe.class,
               Factory.class, FactoryRecipe.class,
               Runner.class, SingletonRecipe.class,
               Configuration.class, ConfigurationRecipe.class);
    

    /** The type of the creator */
    protected final ClassType creatorType;
    /** The element which is triggering the creation of the bean */
    protected final CREATOR creator;
    /** Messager through which to provide "proper" feedback */
    protected final Messager messager;
    
    /** 
     * {@link Set} of the {@link ClassType}s that the class being generated needs to import in order to compile.
     * Items only need to be added if they are being added within the code of generated methods and not if they are
     * part of the class/method/field signature.
     */
    protected final Set<ClassType> externalImports = new HashSet<>();
    
    /**
     * CTOR
     * 
     * @param creatorType {@link ClassType} which is triggering the creation
     * @param creator {@link JBase} which is performing the creation
     * @param messager {@link Messager} that is used by the annotation processor
     */
    AbstractRecipeGenerator(ClassType creatorType, CREATOR creator, Messager messager) {
        this.creatorType = creatorType;
        this.creator = creator;
        this.messager = messager;
    }

    /**
     * Add the type as an import, if it is a {@link ClassType}
     * 
     * @param type {@link Type} to potentially import
     */
    protected void addImport(Type type) {
        if (type instanceof ClassType)
            externalImports.add((ClassType) type);
    }

    /**
     * Add the specified {@link Class} as an import
     * 
     * @param klass {@link Class} which is to be imported
     */
    protected void addImport(Class<?> klass) {
        externalImports.add(new ClassType(klass));
    }
    
    /**
     * Generate the recipe class
     * 
     * @param recipeType {@link ClassType} which is to become the recipe
     * @param annotateRegistry boolean true if the recipe is to be annotated for registration
     * @return {@link ClassDefinition} of the recipe
     * @throws TendrilException if an issue is encountered
     */
    ClassDefinition generate(ClassType recipeType, boolean annotateRegistry) throws TendrilException {
        validateCreator();
        
        // The parent class
        JClass parent = ClassBuilder.forConcreteClass(getRecipeClass()).addGeneric(GenericFactory.create(creatorType)).build();

        // Configure the basic information about the recipe
        ClassBuilder clsBuilder = ClassBuilder.forConcreteClass(recipeType).setVisibility(VisibilityType.PUBLIC).extendsClass(parent);
        if (annotateRegistry)
            clsBuilder.addAnnotation(JAnnotationFactory.create(Registry.class));
        
        populateBuilder(clsBuilder);
        return new ClassDefinition(recipeType, clsBuilder.build().generateCode(externalImports));
    }
    
    /**
     * Validate the creator, to make sure that it can actually be used by/for the recipe
     * 
     * @throws TendrilException if an issues is encountered during validation
     */
    protected abstract void validateCreator() throws TendrilException;
    
    /**
     * Populate the builder with the specifics of what the recipe requires. The details of the class will be applied to the builder before this is called,
     * so this only needs to focus on the internals of the recipe.
     * 
     * @param builder {@link ClassBuilder} where the recipe is being defined
     * @throws TendrilException if an issue is encountered
     */
    protected abstract void populateBuilder(ClassBuilder builder) throws TendrilException;
    
    /**
     * Get the recipe class that is to be employed for the indicated bean.
     * 
     * @return {@link Class} extending {@link AbstractRecipe} representing the concrete recipe that is to be used for the bean
     * @throws InvalidConfigurationException 
     */
    @SuppressWarnings("rawtypes")
    private Class<? extends AbstractRecipe> getRecipeClass() throws InvalidConfigurationException {
        List<Class<? extends Annotation>> foundTypes = new ArrayList<>();

        for (Class<? extends Annotation> annonClass : recipeTypeMap.keySet()) {
            if (creator.hasAnnotation(annonClass))
                foundTypes.add(annonClass);
        }

        if (foundTypes.isEmpty())
            throw new InvalidConfigurationException(creatorType.getFullyQualifiedName() + " must have a single life cycle indicated");
        if (foundTypes.size() > 1)
            throw new InvalidConfigurationException(creatorType.getFullyQualifiedName() + "has multiple life cycles indicated [" + TendrilStringUtil.join(foundTypes) + "]");

        return recipeTypeMap.get(foundTypes.get(0));
    }
    
    /**
     * Generate the necessary code to load the parameters to be injected into separate variables and pass them into the injectee (i.e.: method or constructor).
     * 
     * @param code {@link List} of {@link String}s where the generated code is to be placed
     * @param params {@link List} of {@link JParameter}s that are to be processed
     * @param retrievePrefix {@link String} prefix to place before each line of dependency retrieval (i.e.: indentation)
     * @param applyPrefix {@link String} prefix to apply before the parameters (i.e.: the generated application is "applyPrefix(params);")
     * @throws InvalidConfigurationException if the annotate code is improperly configured
     */
    protected void addParameterInjection(List<String> code, List<JParameter<?>> params, String retrievePrefix, String applyPrefix) throws InvalidConfigurationException {
        for (JParameter<?> p : params) {
            Type pType = p.getType();
            if (pType instanceof ClassType)
                externalImports.add((ClassType)pType);
            
            String engineCall = "engine.";
            if (p.hasAnnotation(InjectAll.class)) {
                Type beanType = getInjectAllType(p);
                addImport(beanType);
                engineCall += "getAllBeans" + "(" + getDependencyDescriptor(p, beanType) + ");";
            } else {
                engineCall += "getBean" + "(" + getDependencyDescriptor(p) + ");";
            }
            code.add(retrievePrefix + pType.getSimpleName() + p.getGenericsApplicationKeyword(true) + p.getName() + " = " + engineCall);
        }
        code.add(applyPrefix + "(" + TendrilStringUtil.join(params, ", ", p -> p.getName()) + ");");
    }
    
    /**
     * Perform validation of the item to make sure that it can be used for InjectAll injection and determines what exact type is to be injected
     * 
     * @param item {@link JType} to verify
     * @return {@link ClassType} that the {@link InjectAll} is to look for
     * @throws InvalidConfigurationException if a problem is determined with the injection 
     */
    protected Type getInjectAllType(JType<?> item) throws InvalidConfigurationException {
        // InjectAll must be applied to a List
        Type itemType = item.getType();
        if (!(itemType instanceof ClassType))
            throw new InvalidConfigurationException("@" + InjectAll.class.getSimpleName() + " can be applied to " + itemType.getSimpleName() + ", it must be applied to classes");
        ClassType classType = (ClassType) itemType;
        if (!classType.equals(new ClassType(List.class)))
            throw new InvalidConfigurationException("@" + InjectAll.class.getSimpleName() + " applied to " + classType.getSimpleName() + ", it must be a " + List.class.getSimpleName());
        
        // The bean type is the first generic applied to it
        return classType.getGenerics().getFirst();
    }
    
    /**
     * Generate the descriptor for the recipe
     * 
     * @param builder {@link ClassBuilder} where the recipe is being defined
     */
    protected void generateRecipeDescriptor(ClassBuilder builder) {
        ClassType descriptorClass = new ClassType(Descriptor.class);
        descriptorClass.addGeneric(GenericFactory.create(creatorType));
        
        builder.buildMethod("setupDescriptor").addAnnotation(JAnnotationFactory.create(Override.class)).setVisibility(VisibilityType.PROTECTED)
            .buildParameter(descriptorClass, "descriptor").finish()
            .addCode(wrapLines(getDescriptorLines(creator), "descriptor.", ";"))
            .finish();
    }
    
    /**
     * Generate the requirement for the recipe
     * 
     * @param builder {@link ClassBuilder} where the recipe is being defined
     */
    protected void generateRecipeRequirements(ClassBuilder builder) {
        ClassType recipeClass = new ClassType(Requirement.class);
        
        builder.buildMethod("setupRequirement").addAnnotation(JAnnotationFactory.create(Override.class)).setVisibility(VisibilityType.PROTECTED)
        .buildParameter(recipeClass, "requirement").finish()
        .addCode(wrapLines(getRequirementLines(creator), "requirement.", ";"))
        .finish();
    }
    
    /**
     * Wrap the provided lines with the specified prefix and suffix
     * 
     * @param lines {@link List} of {@link String} indicating the lines to be wrapped
     * @param prefix {@link String} to place before each line
     * @param suffix {@link String} to place after each line
     * 
     * @return {@link String}[] containing each of the wrapped lines
     */
    private String[] wrapLines(List<String> lines, String prefix, String suffix) {
        String[] updated = new String[lines.size()];
        for (int i = 0; i < lines.size(); i++)
            updated[i] = prefix + lines.get(i) + suffix;
        return updated;
    }
    
    /**
     * Generate the createInstance(Engine engine) method where the bean is defined within a configuration method
     * 
     * @param creator {@link JMethod} within the configuration to call
     * @param builder {@link ClassBuilder} where the recipe is being defined
     * @throws InvalidConfigurationException if the annotate code is improperly configured
     */
    protected void generateCreateInstance(JMethod<?> creator, ClassBuilder builder) throws InvalidConfigurationException {
        // Build the internals of the method
        List<String> lines = new ArrayList<>();
        addParameterInjection(lines, creator.getParameters(), "", "return config.get()." + creator.getName());
        
        // Add the method to the recipe
        builder.buildMethod(creatorType, "createInstance").setVisibility(VisibilityType.PROTECTED).addAnnotation(JAnnotationFactory.create(Override.class))
            .buildParameter(new ClassType(Engine.class), "engine").finish()
            .addCode(lines.toArray(new String[lines.size()])).finish();
    }

    /**
     * Get the code for the descriptor that is to be applied to a dependency of the bean defined by this recipe.
     * 
     * @param field {@link JType} which defines the dependency
     * @return {@link String} containing the code defining the dependency
     */
    protected String getDependencyDescriptor(JType<?> field) {
        return getDependencyDescriptor(field, field.getType());
    }

    /**
     * Get the code for the descriptor that is to be applied to a dependency of the bean defined by this recipe.
     * 
     * @param field {@link JType} which defines the dependency
     * @param beanType {@link Type} representing the bean the descriptor is to describe
     * @return {@link String} containing the code defining the dependency
     */
    protected String getDependencyDescriptor(JType<?> field, Type beanType) {
        externalImports.add(new ClassType(Descriptor.class));
        String desc = "new " + Descriptor.class.getSimpleName() + "<>(" + beanType.getSimpleName() + ".class)";
        return desc + joinLines(getDescriptorLines(field), ".", "", "\n            ");
    }

    /**
     * Helper which converts the text to append to the appropriate in-line code
     * 
     * @param lines     {@link String} which are to be joined
     * @param prefix    {@link String} which is to be placed before each element to be appended
     * @param suffix    {@link String} which is to be placed after each element to be appended
     * @param delimiter {@link String} which is to be placed between two consecutive elements
     * @return {@link List} of {@link String}s that is to be appended
     */
    private String joinLines(List<String> lines, String prefix, String suffix, String delimiter) {
        List<String> converted = new ArrayList<>();
        // Each line with the appropriate indentation spacing
        for (String s : lines) {
            if (s.isBlank())
                continue;
            converted.add(prefix + s + suffix);
        }

        return TendrilStringUtil.join(converted, delimiter);
    }

    /**
     * Get the code through which the name is applied to the Descriptor
     * 
     * @param element {@link JBase} whose name it being determined
     * @param names   {@link List} of {@link Named} annotation that have been applied to the element
     * @return {@link List} of {@link String}s containing the code with the appropriate descriptor update
     * @throws TendrilException if more than one name is applied to the bean
     */
    private List<String> getDescriptorLines(JBase element) {
        List<String> lines = new ArrayList<>();

        ClassType namedType = new ClassType(Named.class);
        for (JAnnotation a : element.getAnnotations()) {
            if (a.getType().equals(namedType)) {
                lines.add("setName(\"" + a.getValue(a.getAttributes().get(0)).getValue() + "\")");
            } else if (a.hasAnnotation(EnumQualifier.class)) {
                EnumerationEntry entry = (EnumerationEntry) a.getValue(a.getAttributes().get(0)).getValue();
                externalImports.add(entry.getEnclosingClass());
                lines.add("addEnumQualifier(" + entry.getEnclosingClass().getClassName() + "." + entry.getName() + ")");
            } else if (a.hasAnnotation(Qualifier.class)) {
                externalImports.add(a.getType());
                lines.add("addQualifier(" + a.getType().getSimpleName() + ".class)");
            } else {
                lines.addAll(getDescriptorLines(a));
            }
        }

        return lines;
    }
    
    /**
     * Generate the code for tracking the needed requirements
     * 
     * @param element {@link JBase} on which the requirements are placed
     */
    private List<String> getRequirementLines(JBase element) {
        List<String> lines = new ArrayList<>();

        // Account for any required environments
        populateEnvironmentReqs(lines, element, RequiresEnv.class, "addRequiredEnvironment");
        populateEnvironmentReqs(lines, element, RequiresNotEnv.class, "addRequiredNotEnvironment");
        return lines;
    }
    
    /**
     * Generate the code to account for environmental requirements for the element
     * 
     * @param lines {@link List} of {@link String}s where the code is stored as separate lines
     * @param element {@link JBase} for whom the requirements to be generated
     * @param annotation {@link Class} extending {@link Annotation} representing the annotation to search for
     * @param methodName {@link String} the name of the method to use to apply the requirement
     */
    private void populateEnvironmentReqs(List<String> lines, JBase element, Class<? extends Annotation> annotation, String methodName) {
        // Account for any requirements
        ClassType reqAnnotation = new ClassType(annotation);
        for (JAnnotation a: element.getAnnotations()) {
            if (a.getType().equals(reqAnnotation)) {
                @SuppressWarnings("unchecked")
                List<JValue<?, ?>> envs = (List<JValue<?, ?>>) a.getValue(a.getAttributes().get(0)).getValue();
                envs.forEach(e -> lines.add(methodName + "(\"" + e.getValue() + "\")"));
            } else
                populateEnvironmentReqs(lines, a, annotation, methodName);
        }
    }
    
}
