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
import java.util.logging.Logger;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

import com.google.auto.service.AutoService;

import tendril.annotationprocessor.AbstractTendrilProccessor;
import tendril.annotationprocessor.ClassDefinition;
import tendril.annotationprocessor.ProcessingException;
import tendril.bean.Bean;
import tendril.bean.Configuration;
import tendril.bean.Factory;
import tendril.bean.Inject;
import tendril.bean.PostConstruct;
import tendril.bean.Singleton;
import tendril.bean.qualifier.Named;
import tendril.bean.recipe.AbstractRecipe;
import tendril.bean.recipe.Applicator;
import tendril.bean.recipe.Descriptor;
import tendril.bean.recipe.FactoryRecipe;
import tendril.bean.recipe.Injector;
import tendril.bean.recipe.Registry;
import tendril.bean.recipe.SingletonRecipe;
import tendril.codegen.JBase;
import tendril.codegen.VisibilityType;
import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.JParameter;
import tendril.codegen.classes.method.JConstructor;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.JField;
import tendril.codegen.field.JType;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
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
    /** Logger for the processor */
    private static final Logger LOGGER = Logger.getLogger(BeanProcessor.class.getSimpleName());

    /** Flag for whether the generated recipe is to be annotated with @{@link Registry} */
    private final boolean annotateRegistry;
    /** Mapping of the types of life cycle annotations that are supported to the recipe that implements it */
    @SuppressWarnings("rawtypes")
    protected final Map<Class<? extends Annotation>, Class<? extends AbstractRecipe>> recipeTypeMap = new HashMap<>();
    /** 
     * {@link Set} of the {@link ClassType}s that the class being generated needs to import in order to compile.
     * Items only need to be added if they are being added within the code of generated methods and not if they are
     * part of the class/method/field signature.
     */
    protected final Set<ClassType> externalImports = new HashSet<>();

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
     * Register the life cycle annotations that are to be supported, to the type of recipe that is to be used when it is employed. By default Singleton and Factory are registered and supported.
     */
    protected void registerAvailableRecipeTypes() {
        recipeTypeMap.put(Singleton.class, SingletonRecipe.class);
        recipeTypeMap.put(Factory.class, FactoryRecipe.class);
    }
    
    /**
     * @see tendril.annotationprocessor.AbstractTendrilProccessor#validateClass()
     */
    @Override
    protected void validateClass() {
        // This can't actually happen, but just to be sure
        if (currentClass.isStatic())
            throwValidationException("static");
        
        // Valid states that still shouldn't be allowed happen
        if (currentClass.isAbstract())
            throwValidationException("abstract");
        if (currentClass.isInterface())
            throwValidationException("interface");
    }
    
    /**
     * Helper to throw an exception if class validation fails
     * 
     * @param reason {@link String} cause of the failure
     */
    private void throwValidationException(String reason) {
        throw new ProcessingException(currentClassType.getFullyQualifiedName() + " cannot be a bean because it is " + reason);
    }

    /**
     * @see tendril.annotationprocessor.AbstractTendrilProccessor#processType()
     */
    @Override
    protected ClassDefinition processType() {
        ClassType providerClass = currentClassType.generateFromClassSuffix("Recipe");
        return new ClassDefinition(providerClass, generateCode(providerClass));
    }

    /**
     * Generate the code for the recipe that is to act as the provider for the bean
     * 
     * @param recipe {@link ClassType} for the recipe that is to be generated
     * @param bean   {@link ClassType} for the bean that is to be provided
     * @return {@link String} containing the code for the recipe
     */
    private String generateCode(ClassType recipe) {
        // Reset to have a clean slate
        externalImports.clear();
        
        // The parent class
        JClass parent = ClassBuilder.forConcreteClass(getRecipeClass()).addGeneric(GenericFactory.create(currentClass)).build();

        // Configure the basic information about the recipe
        ClassBuilder clsBuilder = ClassBuilder.forConcreteClass(recipe).setVisibility(VisibilityType.PUBLIC).extendsClass(parent);
        if (annotateRegistry)
            clsBuilder.addAnnotation(JAnnotationFactory.create(Registry.class));
        
        // Build up the contents of the recipe
        generateConstructor(clsBuilder);
        generateRecipeDescriptor(clsBuilder);
        generateCreateInstance(clsBuilder);
        processPostConstruct(clsBuilder);
        return clsBuilder.build().generateCode(externalImports);
    }

    /**
     * Get the recipe class that is to be employed for the indicated bean.
     * 
     * @return {@link Class} extending {@link AbstractRecipe} representing the concrete recipe that is to be used for the bean
     */
    @SuppressWarnings("rawtypes")
    protected Class<? extends AbstractRecipe> getRecipeClass() {
        List<Class<? extends Annotation>> foundTypes = new ArrayList<>();

        for (Class<? extends Annotation> annonClass : recipeTypeMap.keySet()) {
            if (!getElementAnnotations(annonClass).isEmpty())
                foundTypes.add(annonClass);
        }

        if (foundTypes.isEmpty())
            throw new ProcessingException(currentClassType.getFullyQualifiedName() + " must have a single life cycle indicated");
        if (foundTypes.size() > 1)
            throw new ProcessingException(currentClassType.getFullyQualifiedName() + "has multiple life cycles indicated [" + TendrilStringUtil.join(foundTypes) + "]");

        return recipeTypeMap.get(foundTypes.get(0));
    }
    
    /**
     * Generate the constructor for the recipe
     * 
     * @param builder {@link ClassBuilder} where the recipe class is being defined
     */
    private void generateConstructor(ClassBuilder builder) {
        // CTOR contents
        List<String> ctorCode = new ArrayList<>();
        ctorCode.add("super(engine, " + currentClassType.getSimpleName() + ".class);");
        generateFieldConsumers(ctorCode);
        generateMethodConsumers(ctorCode);
        
        builder.buildConstructor().setVisibility(VisibilityType.PUBLIC)
            .buildParameter(new ClassType(Engine.class), "engine").finish()
            .addCode(ctorCode.toArray(new String[ctorCode.size()]))
            .finish();
    }

    /**
     * Generate the appropriate code for consumers that are fields within the bean.
     * 
     * @param ctorLines       {@link List} of {@link String} lines that are already present in the recipe constructor
     */
    private void generateFieldConsumers(List<String> ctorLines) {
        for (JField<?> field : currentClass.getFields(Inject.class)) {
            Type fieldType = field.getType();
            if (fieldType instanceof ClassType)
                externalImports.add((ClassType) fieldType);

            externalImports.add(new ClassType(Applicator.class));
            externalImports.add(new ClassType(Descriptor.class));

            ctorLines.add("registerDependency(" + getDependencyDescriptor(field) + ", new " + Applicator.class.getSimpleName() + "<" + currentClassType.getSimpleName() + ", "
                    + fieldType.getSimpleName() + ">() {");
            ctorLines.add("    @Override");
            ctorLines.add("    public void apply(" + currentClassType.getSimpleName() + " consumer, " + fieldType.getSimpleName() + " bean) {");
            ctorLines.add("        consumer." + field.getName() + " = bean;");
            ctorLines.add("    }");
            ctorLines.add("});");
        }
    }

    /**
     * Generate the appropriate code for consumers that are methods within the bean.
     * 
     * @param ctorLines       {@link List} of {@link String} lines that are already present in the recipe constructor
     */
    private void generateMethodConsumers(List<String> ctorLines) {
        boolean isFirst = true;
        for (JMethod<?> method : currentClass.getMethods(Inject.class)) {
            // Only include the import, if it's actually used
            if (isFirst) {
                externalImports.add(new ClassType(Injector.class));
                isFirst = false;
            }

            if (!method.getType().isVoid())
                LOGGER.warning(currentClassType.getSimpleName() + "::" + method.getName() + " consumer has a non-void return type");

            ctorLines.add("registerInjector(new Injector<" + currentClassType.getSimpleName() + ">() {");
            ctorLines.add("    @Override");
            ctorLines.add("    public void inject(" + currentClassType.getSimpleName() + " consumer, Engine engine) {");

            List<JParameter<?>> params = method.getParameters();
            if (params.isEmpty())
                LOGGER.warning(currentClassType.getFullyQualifiedName() + "::" + method.getName() + " has no parameters, this is a meaningless injection. Use @" + PostConstruct.class.getSimpleName()
                        + " instead");

            addParameterInjection(ctorLines, method.getParameters(), "        ", "        consumer." + method.getName());
            ctorLines.add("    }");
            ctorLines.add("});");
        }
    }
    
    /**
     * Generate the necessary code to load the parameters to be injected into separate variables and pass them into the injectee (i.e.: method or constructor).
     * 
     * @param code {@link List} of {@link String}s where the generated code is to be placed
     * @param params {@link List} of {@link JParameter}s that are to be processed
     * @param retrievePrefix {@link String} prefix to place before each line of dependency retrieval (i.e.: indentation)
     * @param applyPrefix {@link String} prefix to apply before the parameters (i.e.: the generated application is "applyPrefix(params);")
     */
    private void addParameterInjection(List<String> code, List<JParameter<?>> params, String retrievePrefix, String applyPrefix) {
        for (JParameter<?> p : params) {
            Type pType = p.getType();
            if (pType instanceof ClassType)
                externalImports.add((ClassType)pType);
            code.add(retrievePrefix + pType.getSimpleName() + p.getGenericsApplicationKeyword(true) + p.getName() + " = " + "engine.getBean(" + getDependencyDescriptor(p) + ");");
        }
        code.add(applyPrefix + "(" + TendrilStringUtil.join(params, ", ", p -> p.getName()) + ");");
    }
    
    /**
     * Generate the descriptor for the recipe
     * 
     * @param builder {@link ClassBuilder} where the recipe is being defined
     */
    private void generateRecipeDescriptor(ClassBuilder builder) {
        ClassType descriptorClass = new ClassType(Descriptor.class);
        descriptorClass.addGeneric(GenericFactory.create(currentClass));
        
        builder.buildMethod("setupDescriptor").addAnnotation(JAnnotationFactory.create(Override.class)).setVisibility(VisibilityType.PUBLIC)
            .buildParameter(descriptorClass, "descriptor").finish()
            .addCode(joinLines(getDescriptorLines(currentClass), "descriptor.", ";", "\n"))
            .finish();
    }
    
    /**
     * Generate the createInstance(Engine engine) method where the recipe create the instance for the recipe to provide after it has been processed.
     * 
     * @param builder {@link ClassBuilder} where the recipe is being defined
     */
    private void generateCreateInstance(ClassBuilder builder) {
        // First check if there are any @Inject annotated constructors
        if (!attemptGenerateCreateInstance(builder, currentClass.getConstructors(Inject.class), " annotated with @" + Inject.class.getSimpleName())) {
            // If not, then check any non-annotated constructors
            if (!attemptGenerateCreateInstance(builder, currentClass.getConstructors(), ", the one to be used must be annotated with @" + Inject.class.getSimpleName()))
                    // Still not, therefore there are no viable constructors
                    throw new ProcessingException(currentClass.getType().getFullyQualifiedName() + " has no viable constructors. At least one must be available (and not private).");
        }
    }
    
    /**
     * Attempts to generate the createInstance(Engine engine) method using the list of constructors available. For this to be successful there must be exactly one viable
     * constructor in the list. A viable constructor is considered to be one, which is not private. There can be any number of private constructors, so long as there is
     * exactly one which is not.
     * 
     * @param builder {@link ClassBuilder} in which the recipe is being defined
     * @param ctors {@link List} of {@link JConstructor} to try and make use of
     * @param errorMessageDetail {@link String} additional error message details to provide in the exception if multiple constructor are deemed viable
     * 
     * @throws ProcessingException if there is more than just a single viable constructor
     * @return boolean true if a proper constructor was found and the code was generated (false if no constructor and no code generated)
     */
    private boolean attemptGenerateCreateInstance(ClassBuilder builder, List<JConstructor> ctors, String errorMessageDetail) {
        // Determine which constructors can actually be used
        List<JConstructor> viable = new ArrayList<>();
        for (JConstructor c: ctors) {
            if (c.getVisibility() != VisibilityType.PRIVATE)
                viable.add(c);
        }
        
        // If there are too many, throw an exception
        if (viable.size() > 1)
            throw new ProcessingException(currentClassType.getFullyQualifiedName() + " has " + ctors.size() + " constructors (" + viable.size() + " viable)" + errorMessageDetail);
        else if (viable.size() == 1) {
            // If there is only one viable, then make use of it
            generateCreateInstance(builder, viable.get(0));
            return true;
        }
        
        return false;
    }
    
    /**
     * Generates the createInstance(Engine engine) method using the specified constructor
     * 
     * @param builder {@link ClassBuilder} where the recipe is being defined
     * @param ctor {@link JConstructor} which is to be used to create the instance
     */
    private void generateCreateInstance(ClassBuilder builder, JConstructor ctor) {
        // Build the internals of the method
        List<String> lines = new ArrayList<>();
        addParameterInjection(lines, ctor.getParameters(), "", "return new " + currentClassType.getSimpleName());
        
        // Add the method to the recipe
        builder.buildMethod(currentClassType, "createInstance").setVisibility(VisibilityType.PROTECTED).addAnnotation(JAnnotationFactory.create(Override.class))
            .buildParameter(new ClassType(Engine.class), "engine").finish()
            .addCode(lines.toArray(new String[lines.size()])).finish();
    }
    
    /**
     * Process the {@link PostConstruct} methods that are in the bean. If at least one is present, the override the postConstruct() method from {@link AbstractRecipe}
     * and add a call of bean.method(), where method() has the {@link PostConstruct} annotation applied to it. The method must however follow a few rules:
     * <ol>
     *      <li>The method must not take any parameters</li>
     *      <li>The method must be void</li>
     *      <li>The method must not be private</li>
     * </ol>
     * 
     * A {@link ProcessingException} is thrown if one of the above is violated
     * 
     * @param builder {@link ClassBuilder} where the recipe for the bean is being defined
     * @throws ProcessingException if one of the {@link PostConstruct} annotated method violates {@link PostConstruct} rules
     */
    private void processPostConstruct(ClassBuilder builder) {
        List<JMethod<?>> postConstructs = currentClass.getMethods(PostConstruct.class);
        // Don't do anything if there aren't any
        if (postConstructs.isEmpty())
            return;
        
        // Generate the code for calling the PostConstruct annotated methods
        List<String> code = new ArrayList<>();
        for (JMethod<?> m: postConstructs) {
            if (m.getVisibility() == VisibilityType.PRIVATE)
                throwPostConstructError(m, " cannot be private");
            if (!m.getType().isVoid())
                throwPostConstructError(m, " must be void");
            if (!m.getParameters().isEmpty())
                throwPostConstructError(m, " cannot take any parameters");
            
            code.add("bean." + m.getName() + "();");
        }
        
        // Add the method to the recipe
        builder.buildMethod("postConstruct").addAnnotation(JAnnotationFactory.create(Override.class)).setVisibility(VisibilityType.PROTECTED)
            .buildParameter(currentClass.getType(), "bean").finish()
            .addCode(code.toArray(new String[code.size()])).finish();
    }
    
    /**
     * Helper which generates an error message to be reported by {@link ProcessingException}, triggered by {@link PostConstruct} processing.
     * 
     * @param method {@link JMethod} where {@link PostConstruct} failed
     * @param reason {@link String} the cause of the failure
     * @throws ProcessingException indicating why {@link PostConstruct} processing failed
     */
    private void throwPostConstructError(JMethod<?> method, String reason) {
        throw new ProcessingException("@" + PostConstruct.class.getSimpleName() + " method " + currentClass.getType().getFullyQualifiedName() + "::" + 
                method.getName() + "() " + reason);
    }

    /**
     * Get the code for the descriptor that is to be applied to a dependency of the bean defined by this recipe.
     * 
     * @param field {@link JType} which defines the dependency
     * @return {@link String} containing the code defining the dependency
     */
    private String getDependencyDescriptor(JType<?> field) {
        String desc = "new " + Descriptor.class.getSimpleName() + "<>(" + field.getType().getSimpleName() + ".class)";
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
     * @throws ProcessingException if more than one name is applied to the bean
     */
    private List<String> getDescriptorLines(JBase element) {
        List<String> lines = new ArrayList<>();

        for (JAnnotation a : element.getAnnotations()) {
            if (a.getType().equals(new ClassType(Named.class))) {
                lines.add("setName(\"" + a.getValue(a.getAttributes().get(0)).getValue() + "\")");
            }
        }

        return lines;
    }

    /**
     * @see tendril.annotationprocessor.AbstractTendrilProccessor#processMethod()
     */
    @Override
    protected ClassDefinition processMethod() {
        // A separate processor handles configurations, this "merely" acts to ensure that there isn't a Bean method that is outside of a configuration
        if (currentClass.hasAnnotation(Configuration.class))
            return null;
        
        throw new ProcessingException(currentClassType.getFullyQualifiedName() + "::" + currentMethod.getName() + 
                "() - Bean methods cannot be outside of a configuration");
    }
}
