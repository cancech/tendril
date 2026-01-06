package tendril.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

import com.google.auto.service.AutoService;

import tendril.annotationprocessor.ClassDefinition;
import tendril.annotationprocessor.exception.TendrilException;
import tendril.bean.duplicate.Blueprint;
import tendril.bean.duplicate.GeneratedBlueprint;
import tendril.codegen.VisibilityType;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.JClass;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.TypeFactory;
import tendril.codegen.field.value.JValueFactory;
import tendril.codegen.generics.GenericFactory;

/**
 * Processor for enumerations annotated with @{@link Blueprint}. This will generated qualifiers for the enumeration (akin to {@link QualifierEnumProcessor} as well as the 
 * blueprint annotation which can then be employed on beans to indicate that they are to be duplicated
 */
@SupportedAnnotationTypes("tendril.bean.duplicate.Blueprint")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class BlueprintProcessor extends QualifierEnumProcessor {
	/** Map of all annotation class types that have been generated (so far) */
	private static final Map<ClassType, List<ClassType>> generatedAnnotations = new HashMap<>();
	/** The blueprint class that is generated */
	private ClassType generatedBlueprintClass;
	
	/**
	 * Get a list of qualifier annotation class types that have been generated from the specified blueprint enumeration
	 * 
	 * @param from {@link ClassType} of the blueprint which drives the duplication process
	 * @return {@link List} of {@link ClassType}s which were generated for the blueprint enumeration
	 * @throws TendrilException if the annotations cannot be retrieved
	 */
	public static List<ClassType> getGeneratedAnnotations(ClassType from) throws TendrilException {
		if (!generatedAnnotations.containsKey(from))
			throw new TendrilException("No annotations generated for Blueprint " + from.getFullyQualifiedName());
		return generatedAnnotations.get(from);
	}

	/**
	 * CTOR
	 */
	public BlueprintProcessor() {
		super(Blueprint.class);
	}

	/**
	 * @see tendril.processor.QualifierEnumProcessor#processType()
	 */
	@Override
	protected ClassDefinition processType() throws TendrilException {
		generatedBlueprintClass = TypeFactory.createClassType(currentClassType, "Blueprint");
        
		// Generate qualifiers for each of the entries
		super.processType();
		
		// Generate the bean duplication annotation for the type
		ClassDefinition definition = new ClassDefinition(generatedBlueprintClass, generateBlueprintCode(generatedBlueprintClass));
		generatedBlueprintClass = null;
		return definition;
	}
	
	/**
	 * @see tendril.processor.QualifierEnumProcessor#generateCode(tendril.codegen.field.type.ClassType)
	 */
	@Override
	protected String generateCode(ClassType type) {
		if (!generatedAnnotations.containsKey(currentClassType))
			generatedAnnotations.put(currentClassType, new ArrayList<>());
		generatedAnnotations.get(currentClassType).add(type);
		
		return super.generateCode(type);
	}
	
    /**
     * Generate the code for the {@link Blueprint} {@link Enum}, where each entry in the {@link Enum} is the template for a copy of a bean, which is annotated as one
     * which is to be duplicated with this {@link Blueprint}.
     * 
     * @param qualifier {@link ClassType} representing the qualifier annotation that is to be created
     * @param sourceEnum {@link ClassType} representing the {@link Enum} that is to be used as the ID
     * @return {@link String} containing the generated code
     * @throws ClassNotFoundException if the sourceEnum representing as unknown type
     */
    private String generateBlueprintCode(ClassType qualifier) {
    	ClassType type = TypeFactory.createClassType(Class.class);
    	type.addGeneric(GenericFactory.create(currentClass));
    	
        JClass cls = ClassBuilder.forAnnotation(qualifier).setVisibility(VisibilityType.PUBLIC)
                .addAnnotation(JAnnotationFactory.create(Retention.class, JValueFactory.create(RetentionPolicy.RUNTIME)))
                .addAnnotation(JAnnotationFactory.create(Target.class, JValueFactory.createArray(ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER)))
                .addAnnotation(JAnnotationFactory.create(GeneratedBlueprint.class))
                .buildMethod(type, "enumClass").setDefaultValue(JValueFactory.create(currentClassType)).finish()
                .build();
        return cls.generateCode();
    }

}
