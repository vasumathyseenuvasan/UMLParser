
public class Attributes {

	private String name="";
	private int modifier=0;
	private String dataType="";
	private String attributeTypeCount="";
	private String attributeTypeDisplay="";
	private boolean isAttributeDataTypeClass=false;
	private String attributeClassTypeName=""; 
	
	public boolean isAttributeDataTypeClass() {
		return isAttributeDataTypeClass;
	}
	public void setAttributeDataTypeClass(boolean isAttributeDataTypeClass) {
		this.isAttributeDataTypeClass = isAttributeDataTypeClass;
	}
	public String getAttributeClassTypeName() {
		return attributeClassTypeName;
	}
	public void setAttributeClassTypeName(String attributeClassTypeName) {
		this.attributeClassTypeName = attributeClassTypeName;
	}
	public String getAttributeTypeCount() {
		return attributeTypeCount;
	}
	public void setAttributeTypeCount(String attributeTypeCount) {
		this.attributeTypeCount = attributeTypeCount;
	}
	public String getAttributeTypeDisplay() {
		return attributeTypeDisplay;
	}
	public void setAttributeTypeDisplay(String attributeTypeDisplay) {
		this.attributeTypeDisplay = attributeTypeDisplay;
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
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
}
