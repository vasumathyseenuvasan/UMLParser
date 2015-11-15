
public class MethodCall {
	
	private String methodName;
	private String methodCallingClass;
	private boolean isClass;
	
	public boolean isClass() {
		return isClass;
	}
	public void setClass(boolean isClass) {
		this.isClass = isClass;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getMethodCallingClass() {
		return methodCallingClass;
	}
	public void setMethodCallingClass(String methodCallingClass) {
		this.methodCallingClass = methodCallingClass;
	}
	

}
