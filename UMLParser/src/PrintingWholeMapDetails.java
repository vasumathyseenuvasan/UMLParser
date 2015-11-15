import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.body.Parameter;


public class PrintingWholeMapDetails {
	
	Map sourcesDetailsMap = UMLParser.getUMLParserMap();
	
	public void printParsedDetails() {

		if (sourcesDetailsMap != null) {
			for (Object sourceDetailskey : sourcesDetailsMap.keySet()) {
				System.out.println("Class Name:" + sourceDetailskey.toString());

				if (sourcesDetailsMap.get(sourceDetailskey) instanceof HashMap) {
					// if there are no details in class return to next class in
					// the sourceDetailsMap
					Map classDetailsMap = (HashMap)sourcesDetailsMap.get(sourceDetailskey);
					if (classDetailsMap.size() == 0) {
						return;
					} else {
						for (Object classDetailsMapKey : classDetailsMap.keySet()) {
							if (classDetailsMapKey != null) {
								switch (classDetailsMapKey.toString()) {
									case Global.CLASS_TYPE:
										//printClassSpecificDetails(classDetailsMap.get(Global.CLASS_TYPE));
										break;
									case Global.ATTRIBUTES:
										//printAttributeDetails(classDetailsMap.get(Global.ATTRIBUTES));
										break;
									case Global.METHODS:
										//printMethodDetails(classDetailsMap.get(Global.METHODS));
										break;
									case Global.DEPENDECY:
										printDependencyDetails(classDetailsMap.get(Global.DEPENDECY));
										break;
									default:
										break;
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void printClassSpecificDetails(Object classSpecificDetail){
		if(classSpecificDetail != null && classSpecificDetail instanceof ClassTypeDetails)
		{
			System.out.println("Class Specific Details:");
			System.out.print("Class Name: "+((ClassTypeDetails)classSpecificDetail).getClassName());
			System.out.print("Type: "+((ClassTypeDetails)classSpecificDetail).getType());
			
			if(((ClassTypeDetails)classSpecificDetail).getInterfaces() != null){
				for(String interfaceName:((ClassTypeDetails)classSpecificDetail).getInterfaces()){
					System.out.println(" Interface Name: "+interfaceName);
				}
			}
			if(((ClassTypeDetails)classSpecificDetail).getInheritances() != null){
				for(String inheritanceName:((ClassTypeDetails)classSpecificDetail).getInheritances()){
					System.out.println(" Parent Class Name: "+inheritanceName);
				}
			}
		}
		else
		{
			System.out.println("No Class Specific Details");
			return;
		}
	}
	
	public void printAttributeDetails(Object attributesDetailsList){
		if(attributesDetailsList != null && attributesDetailsList instanceof ArrayList
				&& ((ArrayList)attributesDetailsList).size()>0){
			System.out.println("Attribute Details:");
			List<Attributes> attributeList = new ArrayList<Attributes>();
			attributeList = (ArrayList)attributesDetailsList;
			for(Attributes attribute:attributeList){
				System.out.println(" Attribute Name: "+attribute.getName());
				System.out.println(" Attribute Data Type: "+attribute.getDataType());
			}
		}
		else{
			System.out.println("No attributes Details");
		}
	}
	
	public void printMethodDetails(Object methodsDetailsList){
		if(methodsDetailsList != null && methodsDetailsList instanceof ArrayList
				&& ((ArrayList)methodsDetailsList).size()>0){
			System.out.println(" Method Details:");
			List<Methods> methodlist = new ArrayList<Methods>();
			methodlist = (ArrayList)methodsDetailsList;
			for(Methods method:methodlist){
				System.out.print(" Method Name:"+method.getName());
				System.out.println(" Method return Type:"+method.getReturnType());
				
				for(MethodParameter parameterReturnType:method.getParameters())
				{
					System.out.println(" Paramter return type: "+parameterReturnType.getParamType());
				}
				
//				for (String statement:method.getBodyStatements()){
//					System.out.println("Method body statements:"+statement);
//				}
			}
		}
		else{
			System.out.println("No method Details");
		}
	}
	
	public void printDependencyDetails(Object dependencyList){
		if(dependencyList != null && dependencyList instanceof ArrayList
				&& ((ArrayList)dependencyList).size()>0){
			System.out.println(" Method Details:");
			List<Dependency> dependenciesList = new ArrayList<Dependency>();
			dependenciesList = (ArrayList)dependencyList;
			for(Dependency objDependency:dependenciesList){
				System.out.print(" Dependency object Name:"+objDependency.getDependencyObjName());
				System.out.println(" Dependency class Name:"+objDependency.getDependingClassName());
				System.out.print(" Dependency Type:"+objDependency.getDependencyType());
				System.out.println(" Dependency count:"+objDependency.getDependencyCount());
				System.out.println(" Dependency level:"+objDependency.getDependencyLevel());
			}
		}
		else{
			System.out.println("No dependency Details");
		}
	}


}
