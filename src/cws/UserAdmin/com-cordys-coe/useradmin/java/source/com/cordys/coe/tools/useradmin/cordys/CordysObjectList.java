package com.cordys.coe.tools.useradmin.cordys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

/**
 * Generic construct for a list of tasks, teamassignments enabling cleanup entry on the list
 * 
 * @author kekema
 * 
 */
public class CordysObjectList
{
	private ArrayList<CordysObject> cordysObjects = new ArrayList<CordysObject>();
	private ArrayList<String> keyList = new ArrayList<String>();
	
	/**
	 * Add a CordysObject to the list
	 * 
	 * @param cordysObject
	 */
	public void add(CordysObject cordysObject)
	{
		cordysObjects.add(cordysObject);
	}
	
	/**
	 * Add a CordysObject to the list
	 * and add key to keylist
	 * 
	 * @param cordysObject
	 */
	public void add(String key, CordysObject cordysObject)
	{
		cordysObjects.add(cordysObject);
		keyList.add(key);
	}
	
	/**
	 * Get list of cordys objects
	 * 
	 * @return
	 */
	public ArrayList<CordysObject> getList()
	{
		return cordysObjects;
	}
	
	/**
	 * Get list of object keys
	 * 
	 * @return
	 */
	public ArrayList<String> getKeyList()
	{
		return this.keyList;
	}
	
	/**
	 * Get hashset of object keys
	 * 
	 * @return
	 */
	public HashSet<String> getKeyHashSet()
	{
		return new HashSet<String>(this.keyList);
	}
	
	/**
	 * Sort the list of Cordys Objects as per the object description.
	 * 
	 */
	public void sort()
	{
		Collections.sort(cordysObjects, new Comparator<CordysObject>()
		{
			@Override public int compare(CordysObject co1, CordysObject co2)
			{
			    String desc1 = (co1 != null) ? co1.getDescription() : "";
			    String desc2 = (co2 != null) ? co2.getDescription() : "";
			
			    return desc1.compareToIgnoreCase(desc2);
			}
		});
	}
	
	/**
	 * Cleanup underlying XML
	 * 
	 */
	public void cleanup()
	{
    	for (CordysObject cordysObject : cordysObjects)
    	{
    		cordysObject.cleanup();
    	}	
	}
}