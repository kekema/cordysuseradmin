package com.cordys.coe.tools.useradmin.cordys;

import com.cordys.coe.tools.useradmin.util.NestedXMLObject;

/**
 * Class to represent an XML object from XMLStore
 * 
 * @author kekema
 *
 */
public class XMLStoreObject extends NestedXMLObject
{
	private String xmlStoreLevel = null;
	private String xmlStoreLastModified = null;
	
	public XMLStoreObject(int rootNode) 
	{
		super(rootNode);
	}
	
	/**
	 * Get xmlstore last modified
	 * @return
	 */
	public String getXmlStoreLastModified()
	{
		return this.xmlStoreLastModified;
	}
	
	/**
	 * Set xmlstore last modified
	 * 
	 * @param lastModified
	 */
	public void setXmlStoreLastModified(String lastModified)
	{
		this.xmlStoreLastModified = lastModified;
	}
	
	/**
	 * Get xmlstore level
	 * @return
	 */
	public String getXmlStoreLevel()
	{
		return this.xmlStoreLevel;
	}
	
	/**
	 * Set xmlstore level
	 * @param level
	 */
	public void setXmlStoreLevel(String level)
	{
		this.xmlStoreLevel = level;
	}
	
}