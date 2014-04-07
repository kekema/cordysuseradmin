package com.cordys.coe.tools.useradmin.util;

import java.util.ArrayList;

import com.cordys.cpc.bsf.busobject.BSF;
import com.eibus.xml.nom.Node;
import com.eibus.xml.xpath.XPath;
import com.eibus.xml.xpath.XPathMetaInfo;

/**
 * Class to represent an XMLObject - underlying a NOM XML structure
 * 
 * @author kekema
 *
 */
public class NestedXMLObject extends XMLObject
{
	
	public NestedXMLObject(int rootNode)
	{
		super(rootNode);
	}
	
	/**
	 * Get the string data value as per the xpath
	 * 
	 * @param xpathExpression
	 * @param defaultValue
	 * @return
	 */
	public String getStringValue(String xpathExpression, String defaultValue)
	{
		String result = null;
		int datanode = 0;

		datanode = XPath.getFirstMatch(xpathExpression, new XPathMetaInfo(), rootNode);
		if (datanode > 0)
		{
			result = Node.getData(datanode);
		}

		if (!Util.isSet(result))
		{
			result = defaultValue;
		}
		return result;
	}
	
	/**
	 * Set the string value to the text node as under the node (text parent node) resulting from xpath expression.
	 * This text parent node should exist - not created.
	 * 
	 * @param xpathExpression
	 * @param value
	 */
	public void setStringValue(String xpathExpression, String value)
	{
		int textParentNode = 0;
		
		if (value == null)
		{
			value = "";
		}
		textParentNode = XPath.getFirstMatch(xpathExpression, new XPathMetaInfo(), rootNode);
		if (textParentNode > 0)
		{
			int textNode = Node.getFirstDataNode(textParentNode);
			if (textNode > 0)
			{
				// text node already present - set data
				Node.setData(textNode, value);
			}
			else
			{
				if (!value.equals(""))
				{
					// create text node
					Node.getDocument(textParentNode).createText(value, textParentNode);
				}
			}
		}
	}	
	
	/**
	 * Get the boolean data value as per the xpath
	 * 
	 * @param xpathExpression
	 * @param defaultValue
	 * @return
	 */
	public boolean getBooleanValue(String xpathExpression, boolean defaultValue)
	{
		String result = getStringValue(xpathExpression, Boolean.toString(defaultValue));
		return Boolean.parseBoolean(result);
	}
	
	/**
	 * Set the boolean value to the text node as under the node (text parent node) resulting from xpath expression.
	 * This text parent node should exist - not created.
	 * 
	 * @param xpathExpression
	 * @param value
	 */
	public void setBooleanValue(String xpathExpression, boolean value)
	{
		setStringValue(xpathExpression, Boolean.toString(value));
	}
	
	/**
	 * Get the string attribute value (root node)
	 * 
	 * @param attributeName
	 * @param defaultValue
	 * @return
	 */
	public String getAttributeStringValue(String attributeName, String defaultValue)
	{
		String result = Node.getAttribute(rootNode, attributeName, defaultValue);
		return result;
	}
	
	/**
	 * Get the string data values - multiple nodes as per the xpath
	 * 
	 * @param xpathExpression
	 * @return
	 */
	public ArrayList<String> getStringValues(String xpathExpression)
	{
		ArrayList<String> result = new ArrayList<String>();
		int[] nodes = XPath.getMatchingNodes(xpathExpression, new XPathMetaInfo(), rootNode);
		for (int node : nodes)
		{
			String value = Node.getData(node);
			result.add(value);
		}
		return result;
	}
	
	/**
	 * Set string values. It will first remove any existing ones, and then recreate as per the given tagName/stringValues.
	 * 
	 * @param xpathExpression	refering to parent node
	 * @param tagName			tagName for elements to create
	 * @param stringValues		values
	 */
	public void setStringValues(String xpathExpression, String tagName, ArrayList<String> stringValues)
	{
		// first remove old ones
		int[] nodes = XPath.getMatchingNodes(xpathExpression + "/" + tagName, new XPathMetaInfo(), rootNode);
		for (int node : nodes)
		{
			Node.delete(node);
		}
		// insert new ones
		int parentNode = XPath.getFirstMatch(xpathExpression, new XPathMetaInfo(), rootNode);
		for (String stringValue : stringValues)
		{
			Node.createTextElement(tagName, stringValue, parentNode);
		}
	}
	
	/**
	 * Remove nodes as found per the given xpathExpression
	 * 
	 * @param xpathExpression
	 */
	public void removeElements(String xpathExpression)
	{
		int[] nodes = XPath.getMatchingNodes(xpathExpression, new XPathMetaInfo(), rootNode);
		for (int node : nodes)
		{
			Node.delete(node);
		}
	}
	
	/**
	 * Add an xml fragment under the node as pointed by the xpathExpression
	 * 
	 * @param xpathExpression
	 * @param xml
	 */
	public void addXML(String xpathExpression, String xml)
	{
		int nNode = 0;
		try
		{
			nNode = BSF.getXMLDocument().parseString(xml);
			int parentNode = XPath.getFirstMatch(xpathExpression, new XPathMetaInfo(), rootNode);
			if (parentNode > 0)
			{
				Node.appendToChildren(nNode, parentNode);
			}
		}
		catch (Exception e)
		{
			//
		}
	}
}