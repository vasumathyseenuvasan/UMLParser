import java.util.ArrayList;
import java.util.List;


public class ClassTypeDetails {

	private String className="";
	private String type="";
	private List<String> interfaces = new ArrayList<String>();
	private List<String> inheritances = new ArrayList<String>();
		
	public String getType() {
		return type;
	}
	public void setType(String classType) {
		this.type = classType;
	}
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public List<String> getInterfaces() {
		return interfaces;
	}
	public void setInterfaces(List<String> interfaces) {
		this.interfaces = interfaces;
	}
	public List<String> getInheritances() {
		return inheritances;
	}
	public void setInheritances(List<String> inheritances) {
		this.inheritances = inheritances;
	}
}
