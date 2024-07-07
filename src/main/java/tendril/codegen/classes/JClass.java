package tendril.codegen.classes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Generated;

import tendril.codegen.BaseElement;
import tendril.codegen.CodeBuilder;
import tendril.codegen.Utilities;
import tendril.codegen.VisibilityType;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.JValueFactory;
import tendril.codegen.field.type.TypeData;
import tendril.codegen.field.type.TypeDataFactory;
import tendril.dom.type.Type;
import tendril.dom.type.core.ClassType;
import tendril.dom.type.core.PoDType;
import tendril.dom.type.core.VoidType;

/**
 * Representation of a class, the core construct of any generated code
 */
public abstract class JClass extends BaseElement {
	/** The imports that this class requires, assembled/collected as part of code generation */
	private final Set<ClassType> imports = new HashSet<>();
	/** The methods that this class is composed of */
	private final List<JMethod<?>> methods = new ArrayList<>();
	/** The visibility of this class */
	private final VisibilityType visibility;
	/** The name of the package in which this class appears */
	private final String pkg;

	/**
	 * CTOR
	 * 
	 * @param visibility {@link VisibilityType} what the visibility of the class is
	 * @param data       {@link ClassType} the information about the class
	 */
	protected JClass(VisibilityType visibility, ClassType data) {
		super(data.getClassName());
		this.visibility = visibility;
		this.pkg = data.getPackageName();

		annotate(Generated.class, Map.of("value", JValueFactory.from("tendril"), "date", JValueFactory.from(Utilities.iso8061TimeStamp())));
	}

	/**
	 * Create a method builder through which to add a new void method to the class.
	 * 
	 * @param name {@link String} the name of the method
	 * @return {@link MethodBuilder} for creating a the method
	 */
	public MethodBuilder<VoidType> buildMethod(String name) {
		return createMethodBuilder(TypeDataFactory.create(), name);
	}

	/**
	 * Create a method builder through which to add a new method which returns a plain ol' data type to the class.
	 * 
	 * @param returnType {@link PoDType} representing which PoD to return
	 * @param name       {@link String} the name of the method
	 * @return {@link MethodBuilder} for creating a the method
	 */
	public MethodBuilder<PoDType> buildMethod(PoDType returnType, String name) {
		return createMethodBuilder(TypeDataFactory.create(returnType), name);
	}

	/**
	 * Create a method builder through which to add a new method which returns a Class object.
	 * 
	 * @param returnType {@link Class} of the object that is to be returned
	 * @param name       {@link String} the name of the method
	 * @return {@link MethodBuilder} for creating a the method
	 */
	public MethodBuilder<ClassType> buildMethod(Class<?> returnType, String name) {
		return createMethodBuilder(TypeDataFactory.create(returnType), name);
	}

	/**
	 * Create a method builder through which to add a new method which returns a Class object.
	 * 
	 * @param returnType {@link ClassType} representing the class that is to be returned
	 * @param name       {@link String} the name of the method
	 * @return {@link MethodBuilder} for creating a the method
	 */
	public MethodBuilder<ClassType> buildMethod(ClassType returnType, String name) {
		return createMethodBuilder(TypeDataFactory.create(returnType), name);
	}

	/**
	 * Creates the {@link MethodBuilder} instance that is to be used for the purpose of defining methods
	 * 
	 * @param <RETURN_TYPE> extends {@link Type} indicating what type is to be returned by the method
	 * @param returnType {@link TypeData} representing what the method is to return
	 * @param name {@link String} the name of the method to build
	 * @return {@link MethodBuilder}
	 */
	protected abstract <RETURN_TYPE extends Type> MethodBuilder<RETURN_TYPE> createMethodBuilder(TypeData<RETURN_TYPE> returnType, String name);

	/**
	 * Add a new method to the class. It is intended for this to be used by the {@link MethodBuilder}.
	 * 
	 * @param method {@link JMethod} to add to the class
	 */
	void addMethod(JMethod<?> method) {
		methods.add(method);
	}

	/**
	 * Generate the code which represents this class.
	 * 
	 * @return {@link String} the code for the class
	 */
	public String generateCode() {
		// Generate the class body
		CodeBuilder body = new CodeBuilder();
		generate(body, imports);

		// Generate the package and import statements
		CodeBuilder preamble = new CodeBuilder();
		preamble.append("package " + pkg + ";");
		preamble.blankLine();
		addImports(preamble);
		preamble.blankLine();

		// Combine it to build the whole class
		return preamble.get() + body.get();
	}

	/**
	 * Process the registered imports to generate the import code
	 * 
	 * @param builder {@link CodeBuilder} where to populate the import statements
	 */
	private void addImports(CodeBuilder builder) {
		List<ClassType> sortedImports = new ArrayList<>(imports);
		sortedImports.sort((l, r) -> l.getFullyQualifiedName().compareTo(r.getFullyQualifiedName()));
		for (ClassType toImport : sortedImports) {
			String importPkg = toImport.getPackageName();
			if (importPkg == null || importPkg.isBlank() || importPkg.equals(pkg) || importPkg.equals("java.lang"))
				continue;
			builder.append("import " + toImport.getFullyQualifiedName() + ";");
		}
	}

	/**
	 * @see tendril.codegen.BaseElement#generateSelf(tendril.codegen.CodeBuilder, java.util.Set)
	 */
	@Override
	protected void generateSelf(CodeBuilder builder, Set<ClassType> imports) {
		builder.append(visibility + " " + classType() + " " + name + " {");
		builder.blankLine();
		builder.indent();

		// Process methods
		for (JMethod<?> m : methods) {
			m.generate(builder, imports);
			builder.blankLine();
		}

		builder.deIndent();
		builder.append("}");
		builder.blankLine();
	}

	/**
	 * Produce the {@link String} name/representation for this class
	 * 
	 * @return {@link String} declaration construct for the class
	 */
	protected abstract String classType();
}
