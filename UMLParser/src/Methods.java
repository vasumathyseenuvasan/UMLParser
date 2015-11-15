
import java.util.ArrayList;
import java.util.List;


public class Methods {
	private String name="";
	private int modifier=0;
	private List<MethodParameter> parameters= new ArrayList<MethodParameter>();
	private String returnType="";
	//private String returnTypeCount="";
	private String returnTypeDisplay="";
	private BodyStmts bodyStatements= new BodyStmts();
	public boolean isDependencySet=false;
	private boolean isSetMethod=false;
	private boolean isGetMethod=false;
	private String accessAttrName = "";
	public boolean isMainMethod=false;
	
	public String getAccessAttrName() {
		return accessAttrName;
	}
	public void setAccessAttrName(String accessAttrName) {
		this.accessAttrName = accessAttrName;
	}
	public String getReturnTypeDisplay() {
		return returnTypeDisplay;
	}
	public boolean isDependencySet() {
		return isDependencySet;
	}
	public void setDependencySet(boolean isDependencySet) {
		this.isDependencySet = isDependencySet;
	}
	public boolean isSetMethod() {
		return isSetMethod;
	}
	public void setSetMethod(boolean isSetMethod) {
		this.isSetMethod = isSetMethod;
	}
	public boolean isGetMethod() {
		return isGetMethod;
	}
	public void setGetMethod(boolean isGetMethod) {
		this.isGetMethod = isGetMethod;
	}
	public boolean isMainMethod() {
		return isMainMethod;
	}
	public void setMainMethod(boolean isMainMethod) {
		this.isMainMethod = isMainMethod;
	}
	public void setReturnTypeDisplay(String returnTypeDisplay) {
		this.returnTypeDisplay = returnTypeDisplay;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getModifier() {
		return modifier;
	}
	public void setModifier(int modifier) {
		this.modifier = modifier;
	}
	public List<MethodParameter> getParameters() {
		return parameters;
	}
	public void setParameters(List<MethodParameter> parameters) {
		this.parameters = parameters;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	public BodyStmts getBodyStatements() {
		return bodyStatements;
	}
	public void setBodyStatements(BodyStmts bodyStatements) {
		this.bodyStatements = bodyStatements;
	}
}
