import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.expr.ArrayAccessExpr;


public class BodyStmts {
	private String returnValue;
	private List<VariableDecl> varDeclarations = new ArrayList<VariableDecl>();  
	private List<MethodCall> methodCalls = new ArrayList<MethodCall>();
	
	public List<VariableDecl> getVarDeclarations() {
		return varDeclarations;
	}
	public void setVarDeclarations(List<VariableDecl> varDeclarations) {
		this.varDeclarations = varDeclarations;
	}
	public List<MethodCall> getMethodCalls() {
		return methodCalls;
	}
	public void setMethodCalls(List<MethodCall> methodCalls) {
		this.methodCalls = methodCalls;
	}
	public String getReturnValue() {
		return returnValue;
	}
	public void setReturnValue(String returnValue) {
		this.returnValue = returnValue;
	}
	
}
