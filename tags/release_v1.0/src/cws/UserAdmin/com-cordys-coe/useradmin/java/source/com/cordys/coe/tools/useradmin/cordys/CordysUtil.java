package com.cordys.coe.tools.useradmin.cordys;

import java.util.HashMap;

import com.cordys.coe.tools.useradmin.cordys.exception.CordysException;
import com.cordys.coe.tools.useradmin.util.Util;
import com.cordys.cpc.bsf.busobject.BSF;
import com.cordys.cpc.bsf.soap.SOAPRequestObject;
import com.eibus.xml.nom.Node;
import com.eibus.xml.xpath.XPath;

/**
 * Class with functionalities which can not be assigned to a specific class.
 * 
 * @author kekema
 *
 */
public class CordysUtil
{
	private static HashMap<String, String> cachedRoots = new HashMap<String, String>();
	
	/**
	 * Get the root ID for assignments.
	 * A cache is maintained to optimize for performance.
	 * 
	 * @param runtime		true/false: runtime/staging
	 * @param create
	 * 
	 * @return
	 */
    public static String getAssignmentRoot(boolean runtime, boolean create)
    {
    	String result = null;
    	
    	String workspaceID = "";
    	Integer workspaceIndex = 0;
    	if (!runtime)
    	{
    		workspaceID = "__Organization Staging__";
    		workspaceIndex = 1;
    	}
    	
    	String orgDN = BSF.getOrganization();
    	if (cachedRoots.containsKey(orgDN+workspaceIndex))
    	{
    		result = cachedRoots.get(orgDN+workspaceIndex);		// workspaceIndex represents runtime / staging
    	}
    	
    	if (!Util.isSet(result))
    	{
			String namespace = "http://schemas.cordys.com/userassignment/UserAssignmentService/1.0";
	        String methodName = "InitializeAssignmentRoot";
	        String[] paramNames = new String[] { "WorkspaceID", "AssignmentRoot", "Create" };
	        Object[] paramValues = new Object[] { workspaceID, "CIUIRoot", Boolean.toString(create) };
	        
	        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, paramNames, paramValues);
	
	        int response = 0;
	        try
	        {
	        	response = sro.execute();
				if (response != 0)
				{
					int rootNode = XPath.getFirstMatch(".//InitializeAssignmentRoot", null, response);
					if (rootNode > 0)
					{
						result = Node.getData(rootNode);
						if (Util.isSet(result))
						{
							cachedRoots.put(orgDN+workspaceIndex, result);
						}
					}
				}
	        }
	        catch (Exception e)
	        {
	        	throw new CordysException("Not able to determine the assignmentroot.", e);
	        }
			finally
			{
				if (response != 0)
				{
					Node.delete(response);
				}
			}
    	}
    	
        return result;
    }
}