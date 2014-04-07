package com.cordys.coe.tools.useradmin.util;

import com.eibus.xml.nom.Node;

/**
 * Abstract class to represent an XMLObject - underlying a NOM XML structure
 * 
 * @author kekema
 *
 */
public abstract class XMLObject
{
	// handle to the root of the NOM xml 
	protected int rootNode = 0;
	private boolean cached = false;			// cached by time phased cache
	
	public XMLObject(int rootNode)
	{
		this.rootNode = rootNode;
	}
	
	public int getNOMNode()
	{
		return rootNode;
	}
	
	/**
	 * Get cached flag
	 * 
	 * @return
	 */
	public boolean getCached()
	{
		return this.cached;
	}
	
	/**
	 * Set cached flag
	 * 
	 * @param cached
	 */
	public void setCached(boolean cached)
	{
		this.cached = cached;
	}
	
	/**
	 * Return XML String representation; no pretty print
	 */
	@Override public String toString()
	{
		return (toString(false));
	}
	
	/**
	 * Return XML String representation
	 * 
	 * @param prettyPrint
	 * @return
	 */
	public String toString(boolean prettyPrint)
	{
		String result = null;
		if (rootNode > 0)
		{
			result = (Node.writeToString(rootNode, prettyPrint));
		}
		return result;
	}
	
	/**
	 * Free up NOM memory.
	 */
	public void freeXMLMemory()
	{
		if ((!cached) && (rootNode > 0))
		{
			Node.delete(rootNode);
			rootNode = 0;
		}
	}
}