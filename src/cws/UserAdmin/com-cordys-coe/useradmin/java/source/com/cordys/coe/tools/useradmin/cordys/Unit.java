package com.cordys.coe.tools.useradmin.cordys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.cordys.coe.tools.useradmin.cordys.exception.CordysException;
import com.cordys.coe.tools.useradmin.util.FlatXMLObject;
import com.cordys.coe.tools.useradmin.util.Util;
import com.cordys.cpc.bsf.soap.SOAPRequestObject;
import com.eibus.xml.nom.Node;
import com.eibus.xml.xpath.XPath;


/**
 * Class representing a unit which can be used for assignments to users.
 * It abstracts unit related functionality in the Cordys platform.
 * 
 * @author kekema
 *
 */
public class Unit extends CordysObject
{
	private FlatXMLObject unitXMLObject;
	
	public Unit(int rootNode)
	{
		unitXMLObject = new FlatXMLObject(rootNode);
	}
	
	/**
	 * Unit ID
	 * @return
	 */
	public String getID()
	{
		return(unitXMLObject.getStringValue("ID", null));
	}
	
	/**
	 * Unit Name
	 * @return
	 */
	public String getName()
	{
		return(unitXMLObject.getStringValue("Name", null));
	}
	
	/**
	 * Unit Fully Qualified Name
	 * @return
	 */
	public String getQName()
	{
		return(unitXMLObject.getStringValue("QName", null));
	}
	
	/**
	 * Unit Description
	 */
	public String getDescription()
	{
		return(unitXMLObject.getStringValue("Description", null));
	}
	
	/**
	 * Deployment space, e.g. 'isv'
	 * @return
	 */
	public String getSpace()
	{
		return(unitXMLObject.getStringValue("Space", null));
	}
	
	/**
	 * Free the xml behind.
	 */
	public void cleanup()
	{
		unitXMLObject.freeXMLMemory();
		unitXMLObject = null;
	}
	
	
	/**
	 * Get all deployed units.
	 * The caller is responsible to use cleanup for each object in the 
	 * result (each object wraps underlying NOM xml).
	 * 
	 * @return
	 */
	public static CordysObjectList getAllUnits()
	{
    	String assignmentRoot = CordysUtil.getAssignmentRoot(false, true);
    	CordysObjectList result = new CordysObjectList();
		String namespace = "http://schemas.cordys.com/userassignment/UserAssignmentService/1.0";
        String methodName = "GetUnitsForAssignments";
        String[] paramNames = new String[] { "WorkspaceID", "AssignmentRoot", "Filter", "UseRegEx" };
        Object[] paramValues = new Object[] { "__Organization Staging__", assignmentRoot, "", "false" };
        
        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, paramNames, paramValues);

        int response = 0;
        try
        {
        	Util.setRequestUserToAdministrator(sro);
        	response = sro.execute();
			if (response != 0)
			{
		        int[] unitNodes = XPath.getMatchingNodes(".//dataset/tuple/old/Unit", null, response);
		        for (int unitNode : unitNodes) 
		        {
		        	String deleted = Node.getDataElement(unitNode, "Deleted", "");
		        	// only include if the unit is deployed
		        	if ("false".equalsIgnoreCase(deleted))
		        	{
			        	result.add(new Unit(Node.unlink(unitNode)));
		        	}
		        }
			}
        }
        catch (Exception e)
        {
        	throw new CordysException("Not able to read all units", e);
        }
		finally
		{
			if (response != 0)
			{
				Node.delete(response);
			}
		}
        result.sort();
        return result;		
	}
	
	/**
	 * Get the roles as they exist for a Unit
	 * 
	 * @param unitID
	 * @return
	 */
    public static ArrayList<String> getUnitRoles(String unitID)
    {
    	ArrayList<String> result = new ArrayList<String>();
    	
		String namespace = "http://schemas.cordys.com/userassignment/UserAssignmentService/1.0";
        String methodName = "GetUnitRoles";
        String[] paramNames = new String[] { "WorkspaceID", "UnitID" };
        Object[] paramValues = new Object[] { "__Organization Staging__", unitID };
        
        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, paramNames, paramValues);

        int response = 0;
        try
        {
        	response = sro.execute();
			if (response != 0)
			{
		        int[] unitRoleNodes = XPath.getMatchingNodes(".//dataset/tuple/old/UnitRole", null, response);
		        for (int unitRoleNode : unitRoleNodes) 
		        {
		        	String roleDN = Node.getDataElement(unitRoleNode, "RoleDN", "");
		        	result.add(roleDN);
		        }
			}
        }
        catch (Exception e)
        {
        	throw new CordysException("Not able to read unit roles for unit " + unitID, e);
        }
		finally
		{
			if (response != 0)
			{
				Node.delete(response);
			}
		}
        Collections.sort(result, String.CASE_INSENSITIVE_ORDER);
        return result;    	
    }
    
    /**
     * Get hashmap <id, name>
     * 
     * @return
     */
    public static HashMap<String, String> getUnitNamesByID()
    {
    	HashMap<String, String> result = null;
    	CordysObjectList allUnits = null;
    	try
    	{
    		allUnits = getAllUnits();
			result = getUnitNamesByID(allUnits);
    	}
    	finally
    	{
    		if (allUnits != null)
    		{
    			allUnits.cleanup();
    		}
    	}
        return result;
    }
    
    /**
     * Get hashmap <id, name>
     * 
     * @param allUnits
     * @return
     */
    public static HashMap<String, String> getUnitNamesByID(CordysObjectList allUnits)
    {
    	HashMap<String, String> result = new HashMap<String, String>();
		for (CordysObject cordysObject : allUnits.getList())
		{
			Unit unit = (Unit)cordysObject;
			result.put(unit.getID(), unit.getName());
		}
        return result;
    }
    
    /**
     * Get hashmap <id, qname>
     * 
     * @param allUnits
     * @return
     */
    public static HashMap<String, String> getUnitQNamesByID(CordysObjectList allUnits)
    {
    	HashMap<String, String> result = new HashMap<String, String>();
		for (CordysObject cordysObject : allUnits.getList())
		{
			Unit unit = (Unit)cordysObject;
			result.put(unit.getID(), unit.getQName());
		}
        return result;
    }
    
    /**
     * Get hashmap <qname, id>
     * 
     * @return
     */
    public static HashMap<String, String> getUnitIDsByQName()
    {
    	HashMap<String, String> result = new HashMap<String, String>();
    	CordysObjectList allUnits = null;
    	try
    	{
    		allUnits = getAllUnits();
			for (CordysObject cordysObject : allUnits.getList())
			{
				Unit unit = (Unit)cordysObject;
				result.put(unit.getQName(), unit.getID());
			}
    	}
    	finally
    	{
    		if (allUnits != null)
    		{
    			allUnits.cleanup();
    		}
    	}			
        return result;
    }
    
}