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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.processing.Messager;

import tendril.annotationprocessor.exception.InvalidConfigurationException;
import tendril.annotationprocessor.exception.TendrilException;
import tendril.bean.Inject;
import tendril.bean.InjectAll;
import tendril.bean.PostConstruct;
import tendril.bean.qualifier.Descriptor;
import tendril.bean.recipe.AbstractRecipe;
import tendril.bean.recipe.Applicator;
import tendril.bean.recipe.Injector;
import tendril.codegen.VisibilityType;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.JParameter;
import tendril.codegen.classes.method.JConstructor;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.JField;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.context.Engine;

/**
 * Generator for Recipes which are derived directly from the class defining the bean (i.e.: bean is injected from its own class).
 */
abstract class ClassRecipeGenerator extends AbstractRecipeGenerator<JClass> {
    /** Logger for the processor */
    private static final Logger LOGGER = Logger.getLogger(ClassRecipeGenerator.class.getSimpleName());

    /**
     * CTOR
     * 
     * @param beanType {@link ClassType} of the bean
     * @param creator  {@link JClass} which defined and creates the bean
     * @param messager {@link Messager} that is used by the annotation processor
     */
    ClassRecipeGenerator(ClassType beanType, JClass creator, Messager messager) {
        super(beanType, creator, messager);
    }

    /**
     * @see tendril.processor.recipe.RecipeGenerator#validateCreator()
     */
    protected void validateCreator() throws InvalidConfigurationException {
        // This can't actually happen, but just to be sure
        if (creator.isStatic())
            throwValidationException("static");

        // Valid states that still shouldn't be allowed happen
        if (creator.isAbstract())
            throwValidationException("abstract");
        if (creator.isInterface())
            throwValidationException("interface");
    }

    /**
     * Helper to throw an exception if class validation fails
     * 
     * @param reason {@link String} cause of the failure
     * @throws InvalidConfigurationException
     */
    private void throwValidationException(String reason) throws InvalidConfigurationException {
        throw new InvalidConfigurationException(creatorType.getFullyQualifiedName() + " cannot be a bean because it is " + reason);
    }

    /**
     * Generate the constructor for the recipe
     * 
     * @param builder {@link ClassBuilder} where the recipe class is being defined
     * @throws InvalidConfigurationException if an issue is encountered preparing the constructor
     */
    protected void generateConstructor(ClassBuilder builder) throws InvalidConfigurationException {
        // CTOR contents
        List<String> ctorCode = new ArrayList<>();
        ctorCode.add("super(engine, " + creatorType.getSimpleName() + ".class);");
        generateFieldConsumers(ctorCode);
        generateMethodConsumers(ctorCode);

        builder.buildConstructor().setVisibility(VisibilityType.PUBLIC).buildParameter(new ClassType(Engine.class), "engine").finish().addCode(ctorCode.toArray(new String[ctorCode.size()])).finish();
    }

    /**
     * Generate the appropriate code for consumers that are fields within the bean.
     * 
     * @param ctorLines {@link List} of {@link String} lines that are already present in the recipe constructor
     * @throws InvalidConfigurationException if there is an issue encountered generating the fields
     */
    private void generateFieldConsumers(List<String> ctorLines) throws InvalidConfigurationException {
        generateInjectJFieldDependencyConsumer(ctorLines);
        generateInjectAllJFieldDepdendencyConsumers(ctorLines);
    }

    /**
     * Generate the injectors which inject individual beans into fields
     * 
     * @param ctorLines {@link List} of {@link String}s containing the code of the recipe constructor
     */
    private void generateInjectJFieldDependencyConsumer(List<String> ctorLines) {
        for (JField<?> field : creator.getFields(Inject.class)) {
            Type fieldType = field.getType();
            String fieldTypeName = fieldType.getSimpleName();

            // Add imports
            addImport(fieldType);
            addImport(Applicator.class);
            addImport(Descriptor.class);

            ctorLines.add("registerDependency(" + getDependencyDescriptor(field) + ", new " + Applicator.class.getSimpleName() + "<" + creatorType.getSimpleName() + ", " + fieldTypeName + ">() {");
            ctorLines.add("    @Override");
            ctorLines.add("    public void apply(" + creatorType.getSimpleName() + " consumer, " + fieldTypeName + " bean) {");
            ctorLines.add("        consumer." + field.getName() + " = bean;");
            ctorLines.add("    }");
            ctorLines.add("});");
        }
    }

    /**
     * Generate the injectors which inject all matching beans into fields
     * 
     * @param ctorLines {@link List} of {@link String}s containing the code of the recipe constructor
     */
    private void generateInjectAllJFieldDepdendencyConsumers(List<String> ctorLines) throws InvalidConfigurationException {
        for (JField<?> field : creator.getFields(InjectAll.class)) {
            Type beanType = getInjectAllType(field);

            // Add imports
            addImport(beanType);
            addImport(Injector.class);
            addImport(Descriptor.class);

            ctorLines.add("registerInjector(new Injector<" + creatorType.getSimpleName() + ">() {");
            ctorLines.add("    @Override");
            ctorLines.add("    public void inject(" + creatorType.getSimpleName() + " consumer, Engine engine) {");
            ctorLines.add("        consumer." + field.getName() + " = engine.getAllBeans(" + getDependencyDescriptor(field, beanType) + ");");
            ctorLines.add("    }");
            ctorLines.add("});");
        }
    }

    /**
     * Generate the appropriate code for consumers that are methods within the bean.
     * 
     * @param ctorLines {@link List} of {@link String} lines that are already present in the recipe constructor
     * @throws InvalidConfigurationException if the annotate code is improperly configured
     */
    private void generateMethodConsumers(List<String> ctorLines) throws InvalidConfigurationException {
        boolean isFirst = true;
        for (JMethod<?> method : creator.getMethods(Inject.class)) {
            // Only include the import, if it's actually used
            if (isFirst) {
                externalImports.add(new ClassType(Injector.class));
                isFirst = false;
            }

            if (!method.getType().isVoid())
                LOGGER.warning(creatorType.getSimpleName() + "::" + method.getName() + " consumer has a non-void return type");

            ctorLines.add("registerInjector(new Injector<" + creatorType.getSimpleName() + ">() {");
            ctorLines.add("    @Override");
            ctorLines.add("    public void inject(" + creatorType.getSimpleName() + " consumer, Engine engine) {");

            List<JParameter<?>> params = method.getParameters();
            if (params.isEmpty())
                messager.printWarning(creatorType.getFullyQualifiedName() + "::" + method.getName() + " has no parameters, this is a meaningless injection. Use @" + 
                        PostConstruct.class.getSimpleName() + " instead");

            addParameterInjection(ctorLines, method.getParameters(), "        ", "        consumer." + method.getName());
            ctorLines.add("    }");
            ctorLines.add("});");
        }
    }    

    /**
     * Generate the createInstance(Engine engine) method where the recipe create the instance for the recipe to provide after it has been processed.
     * 
     * @param builder {@link ClassBuilder} where the recipe is being defined
     * @throws InvalidConfigurationException
     */
    protected void generateCreateInstance(ClassBuilder builder) throws TendrilException {
        // First check if there are any @Inject annotated constructors
        if (!attemptGenerateCreateInstanceFromConstructor(builder, creator.getConstructors(Inject.class), " annotated with @" + Inject.class.getSimpleName(), true)) {
            // If not, then check any non-annotated constructors
            if (!attemptGenerateCreateInstanceFromConstructor(builder, creator.getConstructors(), ", the one to be used must be annotated with @" + Inject.class.getSimpleName(), false))
                // Still not, therefore there are no viable constructors
                throw new InvalidConfigurationException(creatorType.getFullyQualifiedName() + " has no viable constructors. At least one must be available (and not private).");
        }
    }

    /**
     * Attempts to generate the createInstance(Engine engine) method using the list of constructors available. For this to be successful there must be exactly one viable constructor in the list. A
     * viable constructor is considered to be one, which is not private. There can be any number of private constructors, so long as there is exactly one which is not.
     * 
     * @param builder            {@link ClassBuilder} in which the recipe is being defined
     * @param ctors              {@link List} of {@link JConstructor} to try and make use of
     * @param errorMessageDetail {@link String} additional error message details to provide in the exception if multiple constructor are deemed viable
     * @param printWarning       boolean true if a warning is to be printed when detecting a private @Inject constructor
     * 
     * @return boolean true if a proper constructor was found and the code was generated (false if no constructor and no code generated)
     * @throws InvalidConfigurationException if there is more than just a single viable constructor
     */
    private boolean attemptGenerateCreateInstanceFromConstructor(ClassBuilder builder, List<JConstructor> ctors, String errorMessageDetail, boolean printWarning) throws InvalidConfigurationException {
        // Determine which constructors can actually be used
        List<JConstructor> viable = new ArrayList<>();
        for (JConstructor c : ctors) {
            if (c.getVisibility() != VisibilityType.PRIVATE)
                viable.add(c);
            else if (printWarning && c.hasAnnotation(Inject.class))
                messager.printWarning(c.getType().getFullyQualifiedName() + " has a private @" + Inject.class.getSimpleName() + " constructor");
        }

        // If there are too many, throw an exception
        if (viable.size() > 1)
            throw new InvalidConfigurationException(creatorType.getFullyQualifiedName() + " has " + ctors.size() + " constructors (" + viable.size() + " viable)" + errorMessageDetail);
        else if (viable.size() == 1) {
            // If there is only one viable, then make use of it
            generateCreateInstanceFromConstructor(builder, viable.get(0));
            return true;
        }

        return false;
    }

    /**
     * Generates the createInstance(Engine engine) method using the specified constructor
     * 
     * @param builder {@link ClassBuilder} where the recipe is being defined
     * @param ctor    {@link JConstructor} which is to be used to create the instance
     * @throws InvalidConfigurationException if the annotate code is improperly configured
     */
    private void generateCreateInstanceFromConstructor(ClassBuilder builder, JConstructor ctor) throws InvalidConfigurationException {
        // Build the internals of the method
        List<String> lines = new ArrayList<>();
        addParameterInjection(lines, ctor.getParameters(), "", "return new " + creatorType.getSimpleName());

        // Add the method to the recipe
        builder.buildMethod(creatorType, "createInstance").setVisibility(VisibilityType.PROTECTED).addAnnotation(JAnnotationFactory.create(Override.class))
                .buildParameter(new ClassType(Engine.class), "engine").finish().addCode(lines.toArray(new String[lines.size()])).finish();
    }

    /**
     * Process the {@link PostConstruct} methods that are in the bean. If at least one is present, the override the postConstruct() method from {@link AbstractRecipe} and add a call of bean.method(),
     * where method() has the {@link PostConstruct} annotation applied to it. The method must however follow a few rules:
     * <ol>
     * <li>The method must not take any parameters</li>
     * <li>The method must be void</li>
     * <li>The method must not be private</li>
     * </ol>
     * 
     * A {@link TendrilException} is thrown if one of the above is violated
     * 
     * @param builder {@link ClassBuilder} where the recipe for the bean is being defined
     * @throws InvalidConfigurationException if one of the {@link PostConstruct} annotated method violates {@link PostConstruct} rules
     */
    protected void processPostConstruct(ClassBuilder builder) throws InvalidConfigurationException {
        List<JMethod<?>> postConstructs = creator.getMethods(PostConstruct.class);
        // Don't do anything if there aren't any
        if (postConstructs.isEmpty())
            return;

        // Generate the code for calling the PostConstruct annotated methods
        List<String> code = new ArrayList<>();
        for (JMethod<?> m : postConstructs) {
            if (m.getVisibility() == VisibilityType.PRIVATE)
                throwPostConstructError(m, " cannot be private");
            if (!m.getType().isVoid())
                throwPostConstructError(m, " must be void");
            if (!m.getParameters().isEmpty())
                throwPostConstructError(m, " cannot take any parameters");

            code.add("bean." + m.getName() + "();");
        }

        // Add the method to the recipe
        builder.buildMethod("postConstruct").addAnnotation(JAnnotationFactory.create(Override.class)).setVisibility(VisibilityType.PROTECTED).buildParameter(creatorType, "bean").finish()
                .addCode(code.toArray(new String[code.size()])).finish();
    }

    /**
     * Helper which generates an error message to be reported by {@link TendrilException}, triggered by {@link PostConstruct} processing.
     * 
     * @param method {@link JMethod} where {@link PostConstruct} failed
     * @param reason {@link String} the cause of the failure
     * @throws InvalidConfigurationException indicating why {@link PostConstruct} processing failed
     */
    private void throwPostConstructError(JMethod<?> method, String reason) throws InvalidConfigurationException {
        throw new InvalidConfigurationException("@" + PostConstruct.class.getSimpleName() + " method " + creatorType.getFullyQualifiedName() + "::" + method.getName() + "() " + reason);
    }

}
