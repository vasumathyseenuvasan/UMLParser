import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;

import net.sourceforge.plantuml.SourceStringReader;

public class Parser {

	private FileInputStream fis;
	private CompilationUnit compUnit;
	private Map classDetailsMap;
	public String test = "hai";

	public Parser(FileInputStream fis) {
		this.fis = fis;
	}

	public void parse() throws ParseException{
		
		if (fis != null) {
			compUnit = JavaParser.parse(fis);
			// System.out.println(compUnit.toString());
			getClassDetails(compUnit);
		}

		// Return a null string if no generation
	}

	// Get the details a class
	public void getClassDetails(CompilationUnit compUnit) {

		ClassTypeDetails objClassType = new ClassTypeDetails();
		List<String> interfaces = new ArrayList<String>();
		List<String> inheritances = new ArrayList<String>();
		List<Attributes> attributesList = new ArrayList<Attributes>();
		List<Methods> methodsList = new ArrayList<Methods>();

		List<TypeDeclaration> typesList = compUnit.getTypes();
		//System.out.println("TypeList length:" + typesList.size());
		
		boolean isCurrentClassAnInterface = false;

		for (Iterator<TypeDeclaration> it = typesList.iterator(); it.hasNext();) {
			
			classDetailsMap = new HashMap();

			TypeDeclaration temp = it.next();

			if (temp.getClass().isInstance(new ClassOrInterfaceDeclaration())) {
				ClassOrInterfaceDeclaration field = (ClassOrInterfaceDeclaration) temp;

				// Handle only public classes.
				// For any other class (private,protected) return
//				if (field.getModifiers() != 1)
//					return;

				objClassType.setClassName(field.getName());
				// System.out.println(field.getName());
				
				//check if it is class or interface
				if(field.isInterface()){
					objClassType.setType(Global.INTERFACE);
					isCurrentClassAnInterface = true;
				}
				else{
					objClassType.setType(Global.CLASS);
				}

				// System.out.println(field.getExtends());
				if (field.getExtends() != null && field.getExtends().size() > 0) {
					for (ClassOrInterfaceType extendsObj : field.getExtends()) {
						inheritances.add(extendsObj.getName());
					}
					objClassType.setInheritances(inheritances);
				}

				// System.out.println(field.getImplements());
				if (field.getImplements() != null
						&& field.getImplements().size() > 0) {
					for (ClassOrInterfaceType implementsObj : field
							.getImplements()) {
						interfaces.add(implementsObj.getName());
					}
					objClassType.setInterfaces(interfaces);
				}
			}
			else 
				continue;

			// Method and member variable Details
			List<BodyDeclaration> l2 = temp.getMembers();
			if(temp.getMembers()!=null){
			for (BodyDeclaration mem : l2) {
				// Attributes details
				if (mem.getClass().isInstance(new FieldDeclaration())) {

					FieldDeclaration attribute = (FieldDeclaration) mem;
					if (attribute.getVariables() != null) {
						for (VariableDeclarator objVariables : attribute
								.getVariables()) {
							Attributes attr = getAttributeDetails(attribute,
									objVariables);

							if (attr != null)
								attributesList.add(attr);
						}

					}
				}
				// Method details
				if (mem.getClass().isInstance(new MethodDeclaration())) {
					
					MethodDeclaration method = (MethodDeclaration) mem;
					method.getTypeParameters();
					
					// Set mehtod scope as private for interfaces
					if(method.getModifiers()==0 && isCurrentClassAnInterface){
						method.setModifiers(1);
					}
					
					Methods methodObj = getMethodDetails(method);
					
					if(methodObj != null && methodObj.getName()!=null && !methodObj.getName().isEmpty())
						methodsList.add(methodObj);
					
				}
				if (mem.getClass().isInstance(new ConstructorDeclaration())) {
					
					ConstructorDeclaration constructor = (ConstructorDeclaration) mem;
					constructor.getTypeParameters();
					Methods methodObj = getConstructorDetails(constructor);
					
					if(methodObj != null)
						methodsList.add(methodObj);
					
				}
			}
			}
			classDetailsMap.put(Global.CLASS_TYPE, objClassType);
			classDetailsMap.put(Global.ATTRIBUTES, attributesList);
			classDetailsMap.put(Global.METHODS, methodsList);

			if(UMLParser.getUMLParserMap() != null)
			{
				UMLParser.getUMLParserMap().put(objClassType.getClassName(), classDetailsMap);
			}
		}
	}

	public Attributes getAttributeDetails(FieldDeclaration fieldAttribute,
			VariableDeclarator objVariable) {

		if (fieldAttribute.getModifiers() != 1 && fieldAttribute.getModifiers() != 2
			&& !(fieldAttribute.toString().contains("private ") || fieldAttribute.toString().contains("public ")))
			return null;

		Attributes attribute = new Attributes();
		String expression = "";

		if(fieldAttribute.getModifiers()==1 || fieldAttribute.getModifiers()==2){
		attribute.setModifier(fieldAttribute.getModifiers());}
		else if(fieldAttribute.toString().contains("private ")){
			attribute.setModifier(2);
		}
		else if(fieldAttribute.toString().contains("public ")){
			attribute.setModifier(1);
		}
		
		if(objVariable.getId()!=null)
			attribute.setName(objVariable.getId().toString());
		
		if(objVariable.getInit() !=null && objVariable.getInit().toString()!=null)
			expression =objVariable.getInit().toString(); 
						
		if (fieldAttribute.getType() != null)
		{
			attribute.setDataType(fieldAttribute.getType().toString());
			attribute.setAttributeTypeDisplay(multipleValuesToDisplay(fieldAttribute.getType().toString(),expression));
			
			if(!expression.isEmpty()){
				attribute.setAttributeTypeCount(multipleValuesCount(expression));
			}
			else{
				attribute.setAttributeTypeCount(multipleValuesCount(fieldAttribute.getType().toString()));
			}
		}
		
		return attribute;
	}
	
	public Methods getMethodDetails(MethodDeclaration method){
		Methods methodObj= new Methods();
		try{
		List<MethodParameter> methodParameters = new ArrayList<MethodParameter>();

		List<String> statements = new ArrayList<String>();
		
		//System.out.println("Complete method declartion"+method.getDeclarationAsString(true,false));
		
		if(method.getModifiers()!=1 && method.getDeclarationAsString(true,false)!=null
				&& !method.getDeclarationAsString(true,false).isEmpty() && !method.getDeclarationAsString(true,false).contains("public "))
		{
		return null;
		}
		
		methodObj.setName(method.getName());
		
		if(method.getName()!=null && method.getName().equals("main"))
			methodObj.isMainMethod=true;
		
		if(method.getModifiers()!=1 && method.getDeclarationAsString(true,false)!=null && method.getDeclarationAsString(true,false).contains("public "))
			methodObj.setModifier(1);
		else
			methodObj.setModifier(method.getModifiers());
		
		
		if(method.getType()!=null)
		{
			methodObj.setReturnType(method.getType().toString());
			methodObj.setReturnTypeDisplay(multipleValuesToDisplay(method.getType().toString(),""));
		}
		
		if(method.getParameters()!=null && method.getParameters().size()>0)
		{
			for(Parameter objParam :method.getParameters()){
				MethodParameter param = new MethodParameter();
				if(objParam.getType()!=null)
				{
					param.setParamType(objParam.getType().toString());
					param.setParamTypeDisplay(multipleValuesToDisplay(objParam.getType().toString(),""));
				}
				if(objParam.getId()!=null)
				{
					param.setParamName(objParam.getId().toString());
				}
				methodParameters.add(param);
			}
			methodObj.setParameters(methodParameters);
		}
		
		if(method.getBody()!=null && method.getBody() instanceof BlockStmt){
			
			BodyStmts bodyStmt = new  BodyStmts();
			BlockStmt blockstmt = (BlockStmt)method.getBody();
			List<VariableDecl> varDeclList = new ArrayList<VariableDecl>();
			List<MethodCall> methodCallList = new ArrayList<MethodCall>();			
			if(blockstmt.getStmts()!=null && blockstmt.getStmts().size() > 0){
				
				// To identify getter setter 
				if(method.getName()!=null
						&& method.getName().length() > 3
						&& (method.getName().contains("set")
								|| method.getName().contains("get")))
				{
					String methodLowerCase = method.getName().toLowerCase();
					if(methodLowerCase.contains("get") && methodLowerCase.length()>3)
					{
						methodObj.setAccessAttrName(methodLowerCase.substring(methodLowerCase.indexOf("get")+3));
						methodObj.setGetMethod(true);
					}
					else if(method.getName().contains("set") && methodLowerCase.length()>3)
					{
						methodObj.setAccessAttrName(methodLowerCase.substring(methodLowerCase.indexOf("set")+3));
						methodObj.setSetMethod(true);
					}
				}
				
				// To analyze and store method body details
				for(Statement stmt:blockstmt.getStmts()){
						
						if(stmt instanceof ExpressionStmt || stmt instanceof ReturnStmt){
						
							Expression expr = new Expression() {
								
								@Override
								public <A> void accept(VoidVisitor<A> arg0, A arg1) {
									// TODO Auto-generated method stub
									
								}
								
								@Override
								public <R, A> R accept(GenericVisitor<R, A> arg0, A arg1) {
									// TODO Auto-generated method stub
									return null;
								}
							};
							
						if(stmt instanceof ExpressionStmt)
								 expr=((ExpressionStmt) stmt).getExpression();
							else if (stmt instanceof ReturnStmt)
								expr = ((ReturnStmt)stmt).getExpr();
							
						if(expr == null)
							continue;
						
						// If the statement has a method Call 
						if(expr instanceof MethodCallExpr)
						{
							MethodCall methodCallObj = new MethodCall();
							MethodCallExpr methodExpr =  (MethodCallExpr)expr;
							if(methodExpr.getNameExpr()!=null)
							{
								methodCallObj.setMethodName(methodExpr.getNameExpr().toString());
							}
							if(methodExpr.getScope()!=null && methodExpr.getScope() instanceof FieldAccessExpr){
								FieldAccessExpr fExp = (FieldAccessExpr)methodExpr.getScope();
								String s = "";
								String se= (recursiveMethodCall(fExp,s));
								
								if(se!=null && se.isEmpty()){
									se =methodExpr.getScope().toString(); 
								}
								
								methodCallObj.setMethodCallingClass(se);
							}
							else if(methodExpr.getScope()!=null && methodExpr.getScope() instanceof NameExpr){
								methodCallObj.setClass(true);
								methodCallObj.setMethodCallingClass(methodExpr.getScope().toString());
							}
							methodCallList.add(methodCallObj);
						}
						
						// If the statement creates objects
						if(expr instanceof VariableDeclarationExpr)
						{
							VariableDecl varDecl = new VariableDecl();
							VariableDeclarationExpr varExpr =((VariableDeclarationExpr)expr); 
							
							if(varExpr.getType()!=null)
							{
								varDecl.setVariableParentType(varExpr.getType().toString());
							}
							if(varExpr.getVars()!=null && varExpr.getVars().size()>0)
							{
								for(VariableDeclarator variable:varExpr.getVars())
								{
									if(variable.getId()!=null){
										varDecl.setVariableName(variable.getId().toString());
									}
									if(variable.getInit()  !=null && variable.getInit() instanceof Expression)
									{
										Expression varExression = variable.getInit();
										
										if(varExression instanceof ObjectCreationExpr && ((ObjectCreationExpr)varExression).getType()!=null)
											varDecl.setVariableClassType(((ObjectCreationExpr)varExression).getType().toString());
										else if(varExression instanceof MethodCallExpr){
											MethodCall methodCallObj = new MethodCall();
											MethodCallExpr methodExpr =  (MethodCallExpr)varExression;
											if(methodExpr.getNameExpr()!=null)
											{
												methodCallObj.setMethodName(methodExpr.getNameExpr().toString());
											}
											if(methodExpr.getScope()!=null && methodExpr.getScope() instanceof FieldAccessExpr){
												FieldAccessExpr fExp = (FieldAccessExpr)methodExpr.getScope();
												String s = "";
												String se= (recursiveMethodCall(fExp,s));
												methodCallObj.setMethodCallingClass(se);
											}
											else if(methodExpr.getScope()!=null && methodExpr.getScope() instanceof NameExpr){
												methodCallObj.setMethodCallingClass(methodExpr.getScope().toString());
											}
											methodCallList.add(methodCallObj);
										}
									}
								}
							}

							varDeclList.add(varDecl);
						}
						
						if(expr instanceof AssignExpr)
						{
							AssignExpr assignExpr = (AssignExpr)expr;
							
							if(assignExpr.getValue()!=null && assignExpr.getValue() instanceof MethodCallExpr){
								MethodCall methodCallObj = new MethodCall();
								MethodCallExpr methodExpr =  (MethodCallExpr)assignExpr.getValue();
								if(methodExpr.getNameExpr()!=null)
								{
									methodCallObj.setMethodName(methodExpr.getNameExpr().toString());
								}
								if(methodExpr.getScope()!=null && methodExpr.getScope() instanceof FieldAccessExpr){
									FieldAccessExpr fExp = (FieldAccessExpr)methodExpr.getScope();
									String s = "";
									String se= (recursiveMethodCall(fExp,s));
									methodCallObj.setMethodCallingClass(se);
								}
								else if(methodExpr.getScope()!=null && methodExpr.getScope() instanceof NameExpr){
									methodCallObj.setMethodCallingClass(methodExpr.getScope().toString());
								}
								methodCallList.add(methodCallObj);
							}
							
							// added for setter 
//							if(method.getName()!=null && !method.getName().isEmpty() && method.getName().toLowerCase().contains("set")
//									&& ((assignExpr.getTarget() instanceof FieldAccessExpr) || (assignExpr.getTarget() instanceof NameExpr))){
//								if(assignExpr.getTarget() instanceof FieldAccessExpr){
//									FieldAccessExpr fExp = (FieldAccessExpr)(assignExpr.getTarget());
//									if(fExp.getScope() instanceof ThisExpr && fExp.getFieldExpr() instanceof NameExpr 
//											){
//										methodObj.setAccessAttrName(fExp.getField());
//										methodObj.setSetMethod(true);
//									}
//								}
//								else if(assignExpr.getTarget() instanceof NameExpr){
//									methodObj.setAccessAttrName(assignExpr.getTarget().toString());
//									methodObj.setSetMethod(true);
//								}
//							}
						}
						
//						if(stmt instanceof ReturnStmt && method.getName()!=null && !method.getName().isEmpty() 
//								&& method.getName().toLowerCase().contains("get")){
//							if(expr instanceof NameExpr){
//								methodObj.setAccessAttrName(((NameExpr) expr).getName());
//								methodObj.setGetMethod(true);
//							}
//							else if(expr instanceof FieldAccessExpr && ((FieldAccessExpr) expr).getScope() instanceof ThisExpr){
//								methodObj.setAccessAttrName(((FieldAccessExpr) expr).getField());
//								methodObj.setGetMethod(true);
//							}
//						}
					}
				}
				bodyStmt.setMethodCalls(methodCallList);
				bodyStmt.setVarDeclarations(varDeclList);
				
			}
			
			methodObj.setBodyStatements(bodyStmt);
		}
		}catch(Exception e){
			e.printStackTrace();
			return methodObj = new Methods();
		}
		return methodObj;
	}
	
	
	public Methods getConstructorDetails(ConstructorDeclaration method){
		Methods methodObj= new Methods();
		List<MethodParameter> methodParameters = new ArrayList<MethodParameter>();

		List<String> statements = new ArrayList<String>();
		
		//System.out.println("Complete method declartion"+method.getDeclarationAsString(true,false));
		
		if(method.getModifiers()!=1 && method.getDeclarationAsString(true,false)!=null
				&& !method.getDeclarationAsString(true,false).isEmpty() && !method.getDeclarationAsString(true,false).contains("public "))
		{
		return null;
		}
		
		methodObj.setName(method.getName());
		methodObj.setModifier(method.getModifiers());
		
		
		if(method.getParameters()!=null && method.getParameters().size()>0)
		{
			for(Parameter objParam :method.getParameters()){
				MethodParameter param = new MethodParameter();
				if(objParam.getType()!=null)
				{
					param.setParamType(objParam.getType().toString());
					param.setParamTypeDisplay(multipleValuesToDisplay(objParam.getType().toString(),""));
				}
				if(objParam.getId()!=null)
				{
					param.setParamName(objParam.getId().toString());
				}
				methodParameters.add(param);
			}
			methodObj.setParameters(methodParameters);
		}
		
		if(method.getBlock()!=null && method.getBlock() instanceof BlockStmt){
			
			BodyStmts bodyStmt = new  BodyStmts();
			BlockStmt blockstmt = (BlockStmt)method.getBlock();
			List<VariableDecl> varDeclList = new ArrayList<VariableDecl>();
			List<MethodCall> methodCallList = new ArrayList<MethodCall>();			
			if(blockstmt.getStmts()!=null && blockstmt.getStmts().size() > 0){
				
				
				// To analyze and store method body details
				for(Statement stmt:blockstmt.getStmts()){
						
						if(stmt instanceof ExpressionStmt){
						
						Expression expr=((ExpressionStmt) stmt).getExpression();
						
						// If the statement has a method Call 
						if(expr instanceof MethodCallExpr)
						{
							MethodCall methodCallObj = new MethodCall();
							MethodCallExpr methodExpr =  (MethodCallExpr)expr;
							if(methodExpr.getNameExpr()!=null)
							{
								methodCallObj.setMethodName(methodExpr.getNameExpr().toString());
							}
							if(methodExpr.getScope()!=null && methodExpr.getScope() instanceof FieldAccessExpr){
								FieldAccessExpr fExp = (FieldAccessExpr)methodExpr.getScope();
								String s = "";
								String se= (recursiveMethodCall(fExp,s));
								
								if(se!=null && se.isEmpty()){
									se =methodExpr.getScope().toString(); 
								}
								
								methodCallObj.setMethodCallingClass(se);
							}
							else if(methodExpr.getScope()!=null && methodExpr.getScope() instanceof NameExpr){
								methodCallObj.setMethodCallingClass(methodExpr.getScope().toString());
							}
							methodCallList.add(methodCallObj);
						}
						
						// If the statement creates objects
						if(expr instanceof VariableDeclarationExpr)
						{
							VariableDecl varDecl = new VariableDecl();
							VariableDeclarationExpr varExpr =((VariableDeclarationExpr)expr); 
							
							if(varExpr.getType()!=null)
							{
								varDecl.setVariableParentType(varExpr.getType().toString());
							}
							if(varExpr.getVars()!=null && varExpr.getVars().size()>0)
							{
								for(VariableDeclarator variable:varExpr.getVars())
								{
									if(variable.getId()!=null){
										varDecl.setVariableName(variable.getId().toString());
									}
									if(variable.getInit()  !=null && variable.getInit() instanceof Expression)
									{
										Expression varExression = variable.getInit();
										
										if(varExression instanceof ObjectCreationExpr && ((ObjectCreationExpr)varExression).getType()!=null)
											varDecl.setVariableClassType(((ObjectCreationExpr)varExression).getType().toString());
										else if(varExression instanceof MethodCallExpr){
											MethodCall methodCallObj = new MethodCall();
											MethodCallExpr methodExpr =  (MethodCallExpr)varExression;
											if(methodExpr.getNameExpr()!=null)
											{
												methodCallObj.setMethodName(methodExpr.getNameExpr().toString());
											}
											if(methodExpr.getScope()!=null && methodExpr.getScope() instanceof FieldAccessExpr){
												FieldAccessExpr fExp = (FieldAccessExpr)methodExpr.getScope();
												String s = "";
												String se= (recursiveMethodCall(fExp,s));
												methodCallObj.setMethodCallingClass(se);
											}
											else if(methodExpr.getScope()!=null && methodExpr.getScope() instanceof NameExpr){
												methodCallObj.setMethodCallingClass(methodExpr.getScope().toString());
											}
											methodCallList.add(methodCallObj);
										}
									}
								}
							}

							varDeclList.add(varDecl);
						}
						
						if(expr instanceof AssignExpr)
						{
							AssignExpr assignExpr = (AssignExpr)expr;
							
							if(assignExpr.getValue()!=null && assignExpr.getValue() instanceof MethodCallExpr){
								MethodCall methodCallObj = new MethodCall();
								MethodCallExpr methodExpr =  (MethodCallExpr)assignExpr.getValue();
								if(methodExpr.getNameExpr()!=null)
								{
									methodCallObj.setMethodName(methodExpr.getNameExpr().toString());
								}
								if(methodExpr.getScope()!=null && methodExpr.getScope() instanceof FieldAccessExpr){
									FieldAccessExpr fExp = (FieldAccessExpr)methodExpr.getScope();
									String s = "";
									String se= (recursiveMethodCall(fExp,s));
									methodCallObj.setMethodCallingClass(se);
								}
								else if(methodExpr.getScope()!=null && methodExpr.getScope() instanceof NameExpr){
									methodCallObj.setMethodCallingClass(methodExpr.getScope().toString());
								}
								methodCallList.add(methodCallObj);
							}
							
						}
					}
				}
				bodyStmt.setMethodCalls(methodCallList);
				bodyStmt.setVarDeclarations(varDeclList);
				
			}
			methodObj.setBodyStatements(bodyStmt);
		}
		
		return methodObj;

	}
	
	public String multipleValuesToDisplay(String value,String expression){
		try {
			if(value.contains("<") && value.contains(">"))
			{
				if(value.contains(","))
				{
					return value+"[*]";
				}
				else
					return value.substring(value.lastIndexOf("<")+1,value.indexOf(">")) + "[*]";
			}
			else if(value.contains("[") && value.contains("]")){
				if(!expression.isEmpty() && expression.contains("[") && expression.contains("]")
					&& !expression.substring(expression.indexOf("[")+1, expression.indexOf("]")).isEmpty()){
						return  value.substring(0,value.indexOf("[")+1)+"1..."+expression.substring(expression.indexOf("[")+1, expression.indexOf("]")+1);
				}
				return value.substring(0,value.indexOf("[")) + "[*]";
			}
		}catch (Exception e){
			e.printStackTrace();
			return value;	
		}
		return value;	
	}
	
	public String recursiveMethodCall(FieldAccessExpr fieldExp, String s){
		
		if(fieldExp.getField()!=null)
			s+= fieldExp.getField().toString()+",";
	
		if(fieldExp.getChildrenNodes()!=null 
				&& fieldExp.getChildrenNodes().get(0) instanceof FieldAccessExpr)
		{
				return recursiveMethodCall((FieldAccessExpr)fieldExp.getChildrenNodes().get(0), s);
		}
		else{
			if(fieldExp.getScope()!=null)
				s+= fieldExp.getScope().toString()+",";
		}
		
		return s;
	}
	
	public String multipleValuesCount(String value){
		try{
			if(value.contains("<") && value.contains(">"))
			{
				if(value.contains(","))
				{
					return "*";
				}
				else
					return "*";
			}
			else if(value.contains("[") && value.contains("]")){
				if(!value.substring(value.indexOf("[")+1, value.indexOf("]")).isEmpty()){
					return  value.substring(value.indexOf("[")+1, value.indexOf("]"));
				}
				return "*";
			}
		}catch(Exception e){
			e.printStackTrace();
			return " ";
		}
		return "1";
	}
}