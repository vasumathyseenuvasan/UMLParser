import java.util.ArrayList;


public class Global {

	public static final String CLASS_TYPE = "class";
	public static final String ATTRIBUTES = "attributes";
	public static final String METHODS = "methods";
	public static final String INTERFACE = "interface";
	public static final String CLASS = "class";
	
	// Dependency Instance
	public static final String DEPENDECY = "dependency";
	public static final String DEPENDENCY_TYPE_INSTANCE = "instance dependency";
	public static final String DEPENDENCY_TYPE_INTERFACE = "interface Dependency";
	public static final String DEPENDENCY_TYPE_USES = "uses interface Dependency";
	public static final String DEPENDENCY_CLASS_LEVEL = "class level";
	public static final String DEPENDENCY_METHOD_LEVEL = "method level";
	public static final String DEPENDENCY_ATTRIBUTE_LEVEL = "attribute level";
	public static final String DEPENDENCY_COUNT_ONE = "1";
	public static final String DEPENDENCY_COUNT_MANY = "*";
	
	public static final ArrayList<String> primitiveTypes = new ArrayList<String>(){{
		add("byte");
		add("float");
		add("double");
		add("boolean");
		add("long");
		add("char");
		add("short");
		add("int");
		add("char");
		}}; 
}
