package com.cordys.coe.tools.useradmin.util;

import com.eibus.xml.nom.Node;

/**
 * Class to represent a flat XMLObject - underlying a NOM XML structure. It expects all elements directly under the root node.
 * 
 * @author kekema
 *
 */
public class FlatXMLObject extends XMLObject
{
	
	public FlatXMLObject(int rootNode)
	{
		super(rootNode);
	}
	
	/**
	 * Get the string data value as per the element name
	 * 
	 * @param elementName
	 * @param defaultValue
	 * @return
	 */
	public String getStringValue(String elementName, String defaultValue)
	{
		String result = Node.getDataElement(rootNode, elementName, defaultValue);
		return result;
	}
	
	public void setStringValue(String elementName, String value)
	{
		Node.setDataElement(rootNode, elementName, value);
	}
	
	public boolean getBooleanValue(String elementName, boolean defaultValue)
	{
		String result = Node.getDataElement(rootNode, elementName, Boolean.toString(defaultValue));
		return Boolean.parseBoolean(result);
	}
	
	public void setBooleanValue(String elementName, boolean value)
	{
		Node.setDataElement(rootNode, elementName, Boolean.toString(value));
	}
}