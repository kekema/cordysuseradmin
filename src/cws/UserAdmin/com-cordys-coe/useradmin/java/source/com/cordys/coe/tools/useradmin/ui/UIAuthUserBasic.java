/*
  This class has been generated by the Code Generator
*/

package com.cordys.coe.tools.useradmin.ui;

import java.util.Vector;

import com.cordys.coe.tools.useradmin.cordys.LDAP;
import com.cordys.coe.tools.useradmin.exception.UserAdminException;
import com.cordys.cpc.bsf.busobject.BusObjectArray;
import com.cordys.cpc.bsf.busobject.BusObjectConfig;
import com.cordys.cpc.bsf.busobject.BusObjectIterator;
import com.cordys.cpc.bsf.soap.SOAPRequestObject;
import com.eibus.web.util.Util;
import com.eibus.xml.nom.Node;
import com.eibus.xml.xpath.XPath;
import com.eibus.xml.xpath.XPathMetaInfo;

/**
 * Class to support a UI entry for an authenticated user with some basic details.
 * 
 * @author kekema
 *
 */
public class UIAuthUserBasic extends UIAuthUserBasicBase
{
    public UIAuthUserBasic()
    {
        this((BusObjectConfig)null);
    }

    public UIAuthUserBasic(BusObjectConfig config)
    {
        super(config);
    }

    /**
     * Get list of authenticated users
     * 
     * @param Filter	for future use
     * @return
     */
    public static BusObjectIterator<UIAuthUserBasic> getUIAuthUserBasicObjects(String filter)
    {
    	if (filter != null)
    	{
    		filter = filter.replaceAll("[\\n\\t]", "");
    	}
		int response = 0;
		Vector<UIAuthUserBasic> result = new Vector<UIAuthUserBasic>();

		// determine the LDAP root
		String ldapRoot = LDAP.getRoot();
		
		// compose list of system users as to exclude them from the result list
		String exclUsers = "-anonymous-SYSTEM-wcpLicUser-Replica Manager-";
		
		// fire soap request for getting auth users from LDAP
        String namespace = "http://schemas.cordys.com/1.1/ldap";
        String methodName = "GetAuthenticatedUsers";
        
        String[] paramNames = new String[] { "dn", "filter", "sort" };
        Object[] paramValues = new Object[] { ldapRoot, "*", "asc" };
        
        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, paramNames, paramValues);
        
        try 
        {
            response = sro.execute();
			if (response != 0)
			{
		        int[] resultNodes = XPath.getMatchingNodes(".//tuple/old/entry", null, response);
		        for (int resultNode : resultNodes) 
		        {
		        	String dn = Node.getAttribute(resultNode, "dn");
		        	// get the full name
		        	int nNode = XPath.getFirstMatch("./description/string", new XPathMetaInfo(), resultNode);
		        	String fullName = Node.getData(nNode);
		        	// get the os identity
		        	nNode = XPath.getFirstMatch("./osidentity/string", new XPathMetaInfo(), resultNode);
		        	String osidentity = Node.getData(nNode);
		        	if (exclUsers.indexOf(osidentity) == -1)
		        	{
		        		// compose entry
			        	UIAuthUserBasic authUserBasic = new UIAuthUserBasic();
			        	authUserBasic.setDN(dn);
			        	// compose description from full name and os identity
			        	String description = "";
			        	if (!Util.isEmpty(fullName))
			        	{
			        		description = description + fullName + " ";
			        	}
			        	if (!Util.isEmpty(osidentity))
			        	{
			        		description = description + "(" + osidentity + ")";
			        	}
			        	authUserBasic.setDescription(description);
			        	result.add(authUserBasic);
		        	}
		        }
			}
        }
        catch (Exception e)
        {
        	throw new UserAdminException("Not able to read auth users from Cordys LDAP.", e);
        }
		finally
		{
			if (response > 0)
			{
				Node.delete(response);
				response = 0;
			}
		}
        return new BusObjectArray<UIAuthUserBasic>(result);
    }

    public void onInsert()
    {
    }

    public void onUpdate()
    {
    }

    public void onDelete()
    {
    }

}
