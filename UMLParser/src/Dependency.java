
public class Dependency {
	
	private String dependingClassName = "";
	private String dependencyType = "";
	private String dependencyCount = "";
	private String dependencyObjName= "";
	private String dependencyLevel="";
	private String dependencyInterfaceName="";
	public boolean isAlreadyadded= false;
	
	
	public String getDependencyLevel() {
		return dependencyLevel;
	}
	public void setDependencyLevel(String dependencyLevel) {
		this.dependencyLevel = dependencyLevel;
	}
	public String getDependencyObjName() {
		return dependencyObjName;
	}
	public void setDependencyObjName(String dependencyObjName) {
		this.dependencyObjName = dependencyObjName;
	}
	
	public String getDependencyCount() {
		return dependencyCount;
	}
	public void setDependencyCount(String dependencyCount) {
		this.dependencyCount = dependencyCount;
	}
	public String getDependingClassName() {
		return dependingClassName;
	}
	public void setDependingClassName(String dependingClassName) {
		this.dependingClassName = dependingClassName;
	}
	public String getDependencyType() {
		return dependencyType;
	}
	public void setDependencyType(String dependencyType) {
		this.dependencyType = dependencyType;
	}
	public String getDependencyInterfaceName() {
		return dependencyInterfaceName;
	}
	public void setDependencyInterfaceName(String dependencyInterfaceName) {
		this.dependencyInterfaceName = dependencyInterfaceName;
	}
	
}
