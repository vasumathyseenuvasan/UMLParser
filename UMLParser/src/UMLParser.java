import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.html.HTMLDocument.Iterator;
import javax.xml.bind.annotation.XmlElementDecl.GLOBAL;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.Parameter;

public class UMLParser {

	private static String outputFile;
	private static File[] filesList;
	private static File inputUMLGenerator;
	private static Map sourcesDetailsMap = new HashMap();
	private static Parser objParser;

	public static File getInputUMLGenerator() {
		return inputUMLGenerator;
	}

	public static void setInputUMLGenerator(File inputUMLGenerator) {
		UMLParser.inputUMLGenerator = inputUMLGenerator;
	}

	public static Map getUMLParserMap() {
		return sourcesDetailsMap;
	}

	public static String getOutputFile() {
		return outputFile;
	}

	public static void setOutputFile(String outputFilePath, String outputFile) {
		//UMLParser.outputFile = "C:/Users/vasumathy/Documents/vasu/"+ outputFile;
		String s = File.separator;
		UMLParser.outputFile = outputFilePath+s+outputFile;
	}

	public static void main(String args[]) throws FileNotFoundException,
			ParseException {
		//if (args != null && args.length == 2) {
			try {
				boolean dirExists = true;
				
				//args = new String[2];
				//args[0]="C:\\Users\\vasumathy\\Documents\\Vasu\\UMLParserFiles";
				//args[1]="Sample.png";
				
				UMLParser umlParser = new UMLParser();
				// Read files one by one from the given directory
				if(args[0]!=null && args[0].length() > 0)
				{
					/*if(args[0].contains("\\"))
					{
					args[0].replace("\\", "/");
					UMLParser.setOutputFile(args[0],args[1]);
					}
					else*/
						UMLParser.setOutputFile(args[0],args[1]);
				}
				else
				{
					dirExists = false;
				}
				
				File directory = new File(args[0]);
				System.out.println(args[0]);
				if(!directory.exists() || !dirExists
						|| !directory.isDirectory()) { 
					System.out.println("Directory not found or path is invalid");
					return;
				}
				
				if (directory.listFiles() != null)
				{
					filesList = directory.listFiles();
					umlParser.navigateFiles(filesList);
				}
					
			} catch (FileNotFoundException f) {
				System.out.println(f);
			} catch (ParseException p) {
				System.out.println(p);
			} catch (Exception e) {
				System.out.println("please enter file path and output file name");
				System.out.println(e);
			}
		//} 
//		else {
//			System.out.println("Please Enter valid directory path and output file name");
//		}
	}

	public void navigateFiles(File[] filesList) throws FileNotFoundException,
			ParseException {
		if (filesList.length == 0) {
			System.out.println("No files in the directory");
			return;
		}
		for (int i = 0; i < filesList.length; i++) {
			
			String fileName = filesList[i].getPath();
			 if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0
				 && (fileName.substring(fileName.lastIndexOf(".")+1).equalsIgnoreCase("java")))
				 {
						FileInputStream fis = new FileInputStream(filesList[i].getPath());
						objParser = new Parser(fis);
						objParser.parse();
				 }
		}
		calculateDependency();
		removeRedundantDependency();
		UmlGeneratorInput umlGenerator = new UmlGeneratorInput();
		umlGenerator.generateUMLTextInput();
	}

	public void calculateDependency() {
		try{
		if (sourcesDetailsMap != null) {
			for (Object sourceDetailskey : sourcesDetailsMap.keySet()) {
				//System.out.println("Class Name:" + sourceDetailskey.toString());
				String currentClassName= sourceDetailskey.toString();
				List<Dependency> dependencies = new ArrayList<Dependency>();
				
				List<Attributes> attributeList = new ArrayList<Attributes>();
				List<Methods> methodList = new ArrayList<Methods>();
				
				Dependency objDependency;
				if (sourcesDetailsMap.get(sourceDetailskey) instanceof HashMap) {
					// if there are no details in class return to next class in
					// the sourceDetailsMap
					Map classDetailsMap = (HashMap) sourcesDetailsMap.get(sourceDetailskey);
					if (classDetailsMap.size() == 0) {
						return;
					} else {

						// Store class or interface dependencies
						if (classDetailsMap.containsKey(Global.CLASS_TYPE)) {
							Object classSpecificDetail = classDetailsMap
									.get(Global.CLASS_TYPE);
							if (classSpecificDetail != null
									&& classSpecificDetail instanceof ClassTypeDetails) {
								if (((ClassTypeDetails) classSpecificDetail)
										.getInterfaces() != null) {
									for (String interfaceName : ((ClassTypeDetails) classSpecificDetail)
											.getInterfaces()) {
										objDependency = new Dependency();
										objDependency.setDependingClassName(interfaceName);
										objDependency.setDependencyType(Global.DEPENDENCY_TYPE_INTERFACE);
										objDependency.setDependencyLevel(Global.DEPENDENCY_CLASS_LEVEL);
										objDependency.setDependencyCount("1");
										dependencies.add(objDependency);
									}
								}
								if (((ClassTypeDetails) classSpecificDetail)
										.getInheritances() != null) {
									for (String inheritanceName : ((ClassTypeDetails) classSpecificDetail)
											.getInheritances()) {
										objDependency = new Dependency();
										objDependency.setDependingClassName(inheritanceName);
										objDependency.setDependencyType(Global.DEPENDENCY_TYPE_INSTANCE);
										objDependency.setDependencyLevel(Global.DEPENDENCY_CLASS_LEVEL);
										objDependency.setDependencyCount(Global.DEPENDENCY_COUNT_ONE);
										dependencies.add(objDependency);
									}
								}
							}
						}

						// Set dependencies from attributes
						if (classDetailsMap.containsKey(Global.ATTRIBUTES)) {
							Object attributesDetailsList = classDetailsMap.get(Global.ATTRIBUTES);
							if (attributesDetailsList != null && attributesDetailsList instanceof ArrayList && ((ArrayList) attributesDetailsList).size() > 0) {
								attributeList = (ArrayList) attributesDetailsList;

								for (final java.util.Iterator<Attributes> iterator = attributeList.iterator(); iterator.hasNext();) {
									Attributes attribute = iterator.next();
									if (attribute.getDataType() != null) {
										if(attribute.getDataType().equals(currentClassName))
											continue;
										String dependantClass;
										Set classSet = sourcesDetailsMap.keySet();
										for (Object className : classSet) {
											if (className != null
													&& className instanceof String) {
												if (attribute.getDataType().contains("<")&& attribute.getDataType().contains(">")) {
													if (attribute.getDataType().contains(",")) {
														String firstpart = attribute.getDataType().substring(attribute.getDataType().indexOf("<") + 1,attribute.getDataType().indexOf(","));
														if(!firstpart.isEmpty())
															firstpart=firstpart.trim();
														String secondPart = attribute.getDataType().substring(attribute.getDataType().indexOf(",") + 1,attribute.getDataType().length()-1);
														if(!secondPart.isEmpty())
															secondPart=secondPart.trim();
														if (firstpart.equals(className)) {
															dependantClass = firstpart;
														} else if (secondPart.equals(className)) {
															dependantClass = secondPart;
														} else {
															dependantClass = attribute.getDataType();
														}
													} else
														dependantClass = attribute.getDataType().substring(attribute.getDataType().lastIndexOf("<") + 1,attribute.getDataType().indexOf(">"));
												} else if (attribute.getDataType().contains("[") && attribute.getDataType().contains("]")) {
													dependantClass = attribute.getDataType().substring(0,attribute.getDataType().indexOf("["));
												} else {
													dependantClass = attribute.getDataType();
												}

												if (dependantClass.equals(className)) {
													objDependency = new Dependency();
													String dependantClassName = (String) className;
													objDependency.setDependencyCount(attribute.getAttributeTypeCount());
													objDependency.setDependingClassName(className.toString());
													objDependency.setDependencyLevel(Global.DEPENDENCY_ATTRIBUTE_LEVEL);
													objDependency.setDependencyObjName(attribute.getName());
													
													attribute.setAttributeDataTypeClass(true);
													attribute.setAttributeClassTypeName(dependantClass);

													if (sourcesDetailsMap.containsKey(dependantClassName)) {
														Map objClass = (HashMap) sourcesDetailsMap.get(dependantClassName);
														if (objClass.containsKey(Global.CLASS_TYPE)) {
															Object classSpecificDetail = objClass.get(Global.CLASS_TYPE);
															
															if (classSpecificDetail != null
																	&& classSpecificDetail instanceof ClassTypeDetails) {
																if (((ClassTypeDetails) classSpecificDetail).getType().equals(Global.INTERFACE))
																{
																	objDependency.setDependencyType(Global.DEPENDENCY_TYPE_INTERFACE);
																	
																	if(!isDependencyPresent(dependencies, dependantClassName, Global.DEPENDENCY_ATTRIBUTE_LEVEL)){
																	dependencies.add(objDependency);
//																		
																	iterator.remove();
																	continue;}
																		
																}
																else
																	objDependency.setDependencyType(Global.DEPENDENCY_TYPE_INSTANCE);
															}
														}
													}
													
													if(!isDependencyPresent(dependencies, dependantClassName, Global.DEPENDENCY_ATTRIBUTE_LEVEL)){
													dependencies.add(objDependency);
													iterator.remove();
													break;}
												}
											}

										}
									}
								}
							}
						}
						// Method dependencies
						if (classDetailsMap.containsKey(Global.METHODS)) {
							Object methodsDetailsList = classDetailsMap.get(Global.METHODS);
							if (methodsDetailsList != null
									&& methodsDetailsList instanceof ArrayList
									&& ((ArrayList) methodsDetailsList).size() > 0) {
								methodList = (ArrayList) methodsDetailsList;
								
								for (final java.util.Iterator<Methods> iterator = methodList.iterator(); iterator.hasNext();) {
									Methods method = iterator.next();
									String dependantClass="";
									String dependantObjName="";
									
									if(methodIsGetterSetter(methodList,method, currentClassName)){
										method.isDependencySet=true;
										continue;
									}
									
									if(method.getParameters()!=null && method.getParameters().size()>0){
										for(MethodParameter param:method.getParameters()){
											
											boolean isUsesDependency = false;
											
											Set classSet = sourcesDetailsMap.keySet();
											
											if (param.getParamType().contains("<")&& param.getParamType().contains(">")) {
												if (param.getParamType().contains(",")) {
													String firstpart = param.getParamType().substring(param.getParamType().indexOf("<") + 1,param.getParamType().indexOf(","));
													String secondPart = param.getParamType().substring(param.getParamType().indexOf(",") + 1,param.getParamType().length());
													if (sourcesDetailsMap.containsKey(firstpart)) {
														dependantClass = firstpart;
													} else if (sourcesDetailsMap.containsKey(secondPart)) {
														dependantClass = secondPart;
													} else {
														dependantClass = param.getParamType();
													}
												} else
													dependantClass = param.getParamType().substring(param.getParamType().lastIndexOf("<") + 1,param.getParamType().indexOf(">"));
											} else if (param.getParamType().contains("[") && param.getParamType().contains("]")) {
												dependantClass = param.getParamType().substring(0,param.getParamType().indexOf("["));
											} else {
												dependantClass = param.getParamType();
											}
											
											if(!dependantClass.isEmpty())
											dependantObjName = param.getParamName();
											
											if (sourcesDetailsMap.containsKey(dependantClass)) {
												Dependency objdependency = createDependency(dependantClass,dependantObjName);
												if(objdependency!=null && objdependency.getDependencyCount()!=""
														&& !objdependency.getDependencyCount().isEmpty()
														&& !isDependencyPresent(dependencies,dependantClass,Global.DEPENDENCY_METHOD_LEVEL)){
													if(!dependantClass.equals(currentClassName))
														dependencies.add(objdependency);
													isUsesDependency = true;
												}
											}
										}
									}
									//Added dependecies from method body statements
									if(method.getBodyStatements() != null ){
										// Method calls dependencies
										for(MethodCall methodCallObj : method.getBodyStatements().getMethodCalls())
										{
											Dependency objdependency;
											if(methodCallObj.getMethodCallingClass()!=null){
												String[] recClasses= methodCallObj.getMethodCallingClass().split(",");
												ArrayList<String> arrLisMethodCallClasses = new ArrayList<String>(Arrays.asList(recClasses)); 
												Collections.reverse(arrLisMethodCallClasses);
												
												if(arrLisMethodCallClasses.size()==1){
													String objName = arrLisMethodCallClasses.get(0);
													String objClassName="";
													
													//check if you can get the object class from parameter.
												if(method.getParameters()!=null && method.getParameters().size()>0){
														for(MethodParameter param:method.getParameters()){
															if(param.getParamName().equals(objName) && param.getParamType()!=""
																	&& sourcesDetailsMap.containsKey(param.getParamType())){
															objdependency = createDependency(param.getParamType(),"");
															// If there exists same dependency already do not add it
															if(!isDependencyPresent(dependencies,param.getParamType(),Global.DEPENDENCY_METHOD_LEVEL))	
																dependencies.add(objdependency);
															
															//continue;
														}
													}
												}//check if you can get the object class from variables declared inside method.
												if(method.getBodyStatements().getVarDeclarations()!=null
														&& method.getBodyStatements().getVarDeclarations().size()>0){
													for(VariableDecl variableDecl:method.getBodyStatements().getVarDeclarations()){
														if(variableDecl.getVariableName()!=null && !variableDecl.getVariableName().isEmpty()){
															if(variableDecl.getVariableName().equals(objName) 
																	&& sourcesDetailsMap.containsKey(variableDecl.getVariableParentType())){
																objdependency = createDependency(variableDecl.getVariableParentType(),"");
																// If there exists same dependency already do not add it
																if(objdependency!=null && objdependency.getDependingClassName()!="" && !objdependency.getDependingClassName().isEmpty() 
																		&& !isDependencyPresent(dependencies,variableDecl.getVariableParentType(),Global.DEPENDENCY_METHOD_LEVEL))	
																	dependencies.add(objdependency);
																//continue;
															}
														}
													}
												}
												// check if it is a direct method call to the class (similar to static call)
												if(methodCallObj.isClass() && methodCallObj.getMethodCallingClass()!=null && !methodCallObj.getMethodCallingClass().isEmpty()
														&& sourcesDetailsMap.containsKey(methodCallObj.getMethodCallingClass())){
													objdependency = createDependency(methodCallObj.getMethodCallingClass(),"");
													if(objdependency!=null && objdependency.getDependingClassName()!="" && !objdependency.getDependingClassName().isEmpty() 
															&& !isDependencyPresent(dependencies,methodCallObj.getMethodCallingClass(),Global.DEPENDENCY_METHOD_LEVEL))	
														dependencies.add(objdependency);
													//continue;
												}
												//check if you can get the object class from attributes declared in class.
												if(attributeList!=null && attributeList.size()>0){
													for(Attributes attrObj:attributeList){
															if(!attrObj.getName().isEmpty() && attrObj.getName().equals(objName)
																	&& sourcesDetailsMap.containsKey(attrObj.getDataType())){
																objdependency = createDependency(attrObj.getDataType(),"");
																// If there exists same dependency already do not add it
																if(objdependency!=null && objdependency.getDependingClassName()!="" && !objdependency.getDependingClassName().isEmpty() 
																		&& !isDependencyPresent(dependencies,attrObj.getDataType(),Global.DEPENDENCY_METHOD_LEVEL))	
																	dependencies.add(objdependency);
															//	continue;
															}
														}
													}
												}
												else
												{
													continue;
												}
												
												
											}
										}
										// Check for vairable decl inside method and add dependencies (if not added already)
										if(method.getBodyStatements().getVarDeclarations()!=null && method.getBodyStatements().getVarDeclarations().size()>0){
											for(VariableDecl varDeclaration:method.getBodyStatements().getVarDeclarations()){
												
												if(!isDependencyPresent(dependencies, varDeclaration.getVariableParentType(), Global.DEPENDENCY_METHOD_LEVEL))
												{
													Dependency objdependency = createDependency(varDeclaration.getVariableParentType(),varDeclaration.getVariableName());
													if(objdependency!=null && objdependency.getDependingClassName()!="" && !objdependency.getDependingClassName().isEmpty())
														dependencies.add(objdependency);
												}
											}
										}
									}
								}

							}
						}
						
						
					}
					classDetailsMap.put(Global.DEPENDECY, dependencies);
				}
			}
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public void removeRedundantDependency(){
		if (sourcesDetailsMap != null) {
			for (Object sourceDetailskey : sourcesDetailsMap.keySet()) {
				String currentClass = sourceDetailskey.toString();
				if (sourcesDetailsMap.get(sourceDetailskey) instanceof HashMap) {
					Map classDetailsMap = (HashMap)sourcesDetailsMap.get(sourceDetailskey);
					if (classDetailsMap.size() == 0) {
						return;
					} else {
						
							// To check if one interface "uses" another interface and see if their implementing classes are connected together.
							if (classDetailsMap.containsKey(Global.CLASS_TYPE)) {
								Object classSpecificDetail = classDetailsMap.get(Global.CLASS_TYPE);
								if (classSpecificDetail != null
										&& classSpecificDetail instanceof ClassTypeDetails) {
									ClassTypeDetails classDetailsObj = (ClassTypeDetails)classDetailsMap.get(Global.CLASS_TYPE);
									if ((classDetailsObj.getType()!="") &&
											classDetailsObj.getType().equals(Global.INTERFACE))
									{
										String currentInterfaceName = classDetailsObj.getClassName();
										if(classDetailsMap.containsKey(Global.DEPENDECY)){ 
											List<Dependency> dependencyList = (List<Dependency>)classDetailsMap.get(Global.DEPENDECY);
											if(dependencyList != null && dependencyList instanceof ArrayList
													&& ((ArrayList)dependencyList).size()>0){
												for(final java.util.Iterator<Dependency> strIterator = dependencyList.iterator(); strIterator.hasNext();){
													Dependency dependObj = strIterator.next();
													if(dependObj.getDependencyLevel()!=null 
															&& dependObj.getDependencyLevel().equals(Global.DEPENDENCY_METHOD_LEVEL)){
														String dependantClassName=dependObj.getDependingClassName();
														if(sourcesDetailsMap.containsKey(dependantClassName)){
															Map objClass = (HashMap) sourcesDetailsMap.get(dependantClassName);
															if (objClass.containsKey(Global.CLASS_TYPE)) {
																Object classDetail = objClass.get(Global.CLASS_TYPE);
																//Check interface "classDetailsObj" is dependant on another interface "classDetail"
																if (classDetail != null
																		&& classDetail instanceof ClassTypeDetails) {
																	if (((ClassTypeDetails) classDetail).getType()!=null
																			&&((ClassTypeDetails) classDetail).getType().equals(Global.INTERFACE))
																	{
																		String dependantInterfaceName = ((ClassTypeDetails) classDetail).getClassName();
																		// Check if their implementing classes are associated with each other
																		//if(isInterfaceHasAssociation(currentInterfaceName,dependantInterfaceName))
																		//{
																			strIterator.remove();
																		//}
																	}
																}
															}
														}
													}
												}
											}
										}
									}
							}	
						}
							
						//check if a class using another interface has an "association" relationship with the classes implementing the interface
						/*if (classDetailsMap.containsKey(Global.CLASS_TYPE)) {
							Object classSpecificDetail = classDetailsMap.get(Global.CLASS_TYPE);
							if (classSpecificDetail != null
									&& classSpecificDetail instanceof ClassTypeDetails) {
								ClassTypeDetails classDetailsObj = (ClassTypeDetails)classDetailsMap.get(Global.CLASS_TYPE);
								if ((classDetailsObj.getType()!="") &&
										classDetailsObj.getType().equals(Global.CLASS))
								{
									if(classDetailsMap.containsKey(Global.DEPENDECY)){ 
										List<Dependency> dependencyList = (List<Dependency>)classDetailsMap.get(Global.DEPENDECY);
										if(dependencyList != null && dependencyList instanceof ArrayList
												&& ((ArrayList)dependencyList).size()>0){
											for(final java.util.Iterator<Dependency> strIterator = dependencyList.iterator(); strIterator.hasNext();){
												boolean isDependencyRemoved = false;
												Dependency dependObj = strIterator.next();
												if(dependObj.getDependencyLevel()!=null 
														&& dependObj.getDependencyLevel().equals(Global.DEPENDENCY_METHOD_LEVEL)){
													String dependantInterfaceName=dependObj.getDependingClassName();
													for (Object innerSourceDetailskey : sourcesDetailsMap.keySet()) {
														String innerCurrentClass = innerSourceDetailskey.toString();
														if (sourcesDetailsMap.get(innerSourceDetailskey) instanceof HashMap) {
															Map innerClassDetailsMap = (HashMap)sourcesDetailsMap.get(innerSourceDetailskey);
															//check if it is class and see all its implementing things
															ArrayList<String> implClasses = new ArrayList<String>(getInterfaceImplClassName(dependantInterfaceName,sourceDetailskey));
															
															if(implClasses!=null && implClasses.size()>0){
																for(String implClass:implClasses)
																{
																	if(findIfCurrentClassHasAssociation(classDetailsObj.getClassName(),implClass)){
																		strIterator.remove();
																		isDependencyRemoved = true;
																		break;
																	}
																}
															}
																
														}
														if(isDependencyRemoved)
														break;
													}
												}
												//if(isDependencyRemoved)
												//break;
											}
										}
									}
								}
							}
						}*/
					}
				}
			}
		}
	}
	
	//check with existing dependencies
	public boolean isDependencyPresent(List<Dependency> dependencies, String dependencyClassName, String dependencyLevel){
			if(dependencies!=null && dependencies.size()>0)
			{
				for(Dependency dependency:dependencies){
					if(dependency.getDependingClassName()!=null
							&& dependency.getDependingClassName().equals(dependencyClassName)
							&& (dependency.getDependencyLevel().equals(dependencyLevel))){
						if(dependencyLevel!=null && dependencyLevel!=""&& dependencyLevel.equals(Global.DEPENDENCY_ATTRIBUTE_LEVEL))
							dependency.setDependencyCount("*");
						return true;
					}
				}
			}
			return false;
	}
	
// Commented because both association and dependency has to been shown
//	public boolean isDependencyAssocPresent(List<Dependency> dependencies, String dependencyClassName, String interfaceName){
//		if(dependencies!=null && dependencies.size()>0)
//		{
//			for(Dependency dependency:dependencies){
//				if(dependency.getDependingClassName()!=null
//						&& dependency.getDependingClassName().equals(dependencyClassName)
//						&& dependency.getDependencyInterfaceName()!=null
//						&& dependency.getDependencyInterfaceName().equals(interfaceName)){
//					dependency.setDependencyCount("*");
//					return true;
//				}
//			}
//		}
//		return false;
//}
	
	public Dependency createDependency(String dependantClass, String dependentObjName){
		//check if the dependantClass is an interface and return dependency object. No need to show class dependencies
		Dependency objDependency = new Dependency();
		if(sourcesDetailsMap.containsKey(dependantClass) && sourcesDetailsMap.get(dependantClass) instanceof HashMap){
		Map objClass = (HashMap) sourcesDetailsMap.get(dependantClass);
		if (objClass.containsKey(Global.CLASS_TYPE)) {
			Object classSpecificDetail = objClass.get(Global.CLASS_TYPE);
			if (classSpecificDetail != null
					&& classSpecificDetail instanceof ClassTypeDetails) {
				if ((((ClassTypeDetails) classSpecificDetail).getType()!="") &&
						((ClassTypeDetails) classSpecificDetail).getType().equals(Global.INTERFACE))
				{
					objDependency.setDependencyCount(Global.DEPENDENCY_COUNT_ONE);
					objDependency.setDependingClassName(dependantClass);
					objDependency.setDependencyType(Global.DEPENDENCY_TYPE_USES);
					objDependency.setDependencyLevel(Global.DEPENDENCY_METHOD_LEVEL);
					objDependency.setDependencyObjName(dependentObjName);
				}
					
			}
		}
		}
		return objDependency;
	}
	
	public boolean isInterfaceDependencyPresent(String dependantClass, Object sourceDetailskey){
		try{
		if(sourcesDetailsMap.containsKey(dependantClass) && sourcesDetailsMap.get(dependantClass) instanceof HashMap){
			
		Map classDetailsMap = (HashMap) sourcesDetailsMap.get(sourceDetailskey);
		if(classDetailsMap.containsKey(Global.CLASS_TYPE)){
			Object classSpecificDetail = classDetailsMap.get(Global.CLASS_TYPE);
			if (classSpecificDetail != null
				&& classSpecificDetail instanceof ClassTypeDetails) {
				if ((((ClassTypeDetails) classSpecificDetail).getInterfaces()!=null) &&
					((ClassTypeDetails) classSpecificDetail).getInterfaces().size()>0)
				{
					ClassTypeDetails classDetails =((ClassTypeDetails)classSpecificDetail);
					for(final java.util.Iterator<String> strIterator = classDetails.getInterfaces().iterator(); strIterator.hasNext();)
					{
						String interfaceName = strIterator.next();
						if(interfaceName!=null && !interfaceName.isEmpty()
								&& interfaceName.equals(dependantClass))
						{
							//strIterator.remove();
							return true;
						}
					}
				}
			}
		}
		}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	// Get the list of class names that are implementing the "dependant class"
	public ArrayList<String> getInterfaceImplClassName(String dependantClass, Object sourceDetailskey) {

		ArrayList	<String> implementingClasses = new ArrayList<String>();
		try{
		for (Object sourcesDetailskey : sourcesDetailsMap.keySet()){
		Map classDetailsMap = (HashMap) sourcesDetailsMap.get(sourcesDetailskey);
		if(classDetailsMap.containsKey(Global.CLASS_TYPE)){
			Object classSpecificDetail = classDetailsMap.get(Global.CLASS_TYPE);
			if (classSpecificDetail != null
				&& classSpecificDetail instanceof ClassTypeDetails) {
				if ((((ClassTypeDetails) classSpecificDetail).getInterfaces()!=null) &&
					((ClassTypeDetails) classSpecificDetail).getInterfaces().size()>0)
				{
					ClassTypeDetails classDetails =((ClassTypeDetails)classSpecificDetail);
					for(final java.util.Iterator<String> strIterator = classDetails.getInterfaces().iterator(); strIterator.hasNext();)
					{
						String interfaceName = strIterator.next();
						if(interfaceName!=null && !interfaceName.isEmpty()
								&& interfaceName.equals(dependantClass))
						{
							implementingClasses.add(sourcesDetailskey.toString());
						}
					}
				}
			}
		}
		}
		}catch(Exception e){
			e.printStackTrace();
			return implementingClasses;
		}
		return implementingClasses;
	}
	
	// to check if a method is getter setter and change the attribute value to "public"
	public boolean methodIsGetterSetter(List<Methods> methodList, Methods currentMethod, String currentClassName){
		boolean isGetSetMethodAttr= false;
		try{
		for(Methods method:methodList){
		if(method!=null && (method.isGetMethod() || method.isSetMethod())
				&& (currentMethod.isGetMethod() || currentMethod.isSetMethod())){
			
			// Update attribute modifier to public
			if(sourcesDetailsMap.containsKey(currentClassName) && sourcesDetailsMap.get(currentClassName) instanceof HashMap){
				
			Map classDetailsMap = (HashMap)sourcesDetailsMap.get(currentClassName);
				
			Object attributesDetailsList = classDetailsMap.get(Global.ATTRIBUTES);
				
				if (attributesDetailsList != null
						&& attributesDetailsList instanceof ArrayList
						&& ((ArrayList) attributesDetailsList).size() > 0) {
					List<Attributes> attributeList;
					attributeList = (ArrayList) attributesDetailsList;
					for (final java.util.Iterator<Attributes> iterator = attributeList.iterator(); iterator.hasNext();) {
						Attributes attribute = iterator.next();
						
						if(currentMethod.isGetMethod() && method.isSetMethod() && currentMethod.getAccessAttrName() !=null
								&& method.getAccessAttrName()!=null && currentMethod.getAccessAttrName().equals(method.getAccessAttrName())
								&& attribute.getName()!=null && attribute.getName().equalsIgnoreCase(method.getAccessAttrName()))
						{
							attribute.setModifier(1);
							return true;
						}
						if(currentMethod.isSetMethod() && method.isGetMethod() && currentMethod.getAccessAttrName() !=null
								&& method.getAccessAttrName()!=null && currentMethod.getAccessAttrName().equals(method.getAccessAttrName())
								&& attribute.getName()!=null && attribute.getName().equalsIgnoreCase(method.getAccessAttrName())){
							attribute.setModifier(1);
							return true;
						}
					}
				}
			}
		}
		}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	public boolean isInterfaceHasAssociation(String currentInterfaceName, String dependantInterfaceName){
		if (sourcesDetailsMap != null) {
			try{
			for (Object sourceDetailskey : sourcesDetailsMap.keySet()) {
				String currentClass = sourceDetailskey.toString();
				if (sourcesDetailsMap.get(sourceDetailskey) instanceof HashMap) {
					Map objClass = (HashMap)sourcesDetailsMap.get(sourceDetailskey);
					if (objClass.size() == 0) {
						return false;
					} else {
						if (objClass.containsKey(Global.CLASS_TYPE)) {
							Object classSpecificDetail = objClass.get(Global.CLASS_TYPE);
							if (classSpecificDetail != null
									&& classSpecificDetail instanceof ClassTypeDetails) {
								ClassTypeDetails classTypeDetail = (ClassTypeDetails)classSpecificDetail;
									if ((classTypeDetail.getType()!="") &&
										(classTypeDetail.getType().equals(Global.CLASS)))
									{
										boolean interfaceFound = false;
										if(classTypeDetail.getInterfaces()!=null && classTypeDetail.getInterfaces().size()>0){
											for(String interfaceName:classTypeDetail.getInterfaces()){
												if(interfaceName.equals(currentInterfaceName))
												{
													interfaceFound = true;
													break;
												}
											}
										}
										else
											continue;
										if(interfaceFound && objClass.containsKey(Global.DEPENDECY)){ 
											List<Dependency> dependencyList = (List<Dependency>)objClass.get(Global.DEPENDECY);
											if(dependencyList != null && dependencyList instanceof ArrayList
													&& ((ArrayList)dependencyList).size()>0){
												for(final java.util.Iterator<Dependency> strIterator = dependencyList.iterator(); strIterator.hasNext();){
													Dependency dependObj = strIterator.next();
													if(dependObj.getDependencyLevel()!=null 
															&& dependObj.getDependencyLevel().equals(Global.DEPENDENCY_ATTRIBUTE_LEVEL)
															&& dependObj.getDependencyInterfaceName()!=null
															&& dependObj.getDependencyInterfaceName().length()>0){
															if(dependObj.getDependencyInterfaceName().equals(dependantInterfaceName))
																return true;
													}}}}
										else
											continue;
									}
									
								}
							}	
					}
				}
			}
			}catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	public boolean findIfCurrentClassHasAssociation(String currentClass, String associatingClass){
		if(sourcesDetailsMap!=null && sourcesDetailsMap.containsKey(currentClass)){
			if (sourcesDetailsMap.get(currentClass) instanceof HashMap) {
				Map objCurrentClass = (HashMap)sourcesDetailsMap.get(currentClass);
				if(objCurrentClass.containsKey(Global.DEPENDECY)){ 
					List<Dependency> dependencyList = (List<Dependency>)objCurrentClass.get(Global.DEPENDECY);
					if(dependencyList != null && dependencyList instanceof ArrayList
							&& ((ArrayList)dependencyList).size()>0){
						for(final java.util.Iterator<Dependency> strIterator = dependencyList.iterator(); strIterator.hasNext();){
							Dependency dependObj = strIterator.next();
							if(dependObj.getDependencyLevel()!=null 
									&& dependObj.getDependencyLevel().equals(Global.DEPENDENCY_ATTRIBUTE_LEVEL)
									&& dependObj.getDependingClassName()!=null){
									if(dependObj.getDependingClassName().equals(associatingClass))
										return true;
							}}}}
			}
		}
		return false;
	}
}
