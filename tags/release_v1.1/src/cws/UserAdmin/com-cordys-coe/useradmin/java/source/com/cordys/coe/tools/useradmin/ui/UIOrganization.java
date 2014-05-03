/*
  This class has been generated by the Code Generator
*/

package com.cordys.coe.tools.useradmin.ui;

import java.util.Vector;

import com.cordys.coe.tools.useradmin.cordys.CordysUser;
import com.cordys.coe.tools.useradmin.cordys.LDAP;
import com.cordys.coe.tools.useradmin.exception.UserAdminException;
import com.cordys.coe.tools.useradmin.util.Util;
import com.cordys.cpc.bsf.busobject.BusObjectArray;
import com.cordys.cpc.bsf.busobject.BusObjectConfig;
import com.cordys.cpc.bsf.busobject.BusObjectIterator;
import com.cordys.cpc.bsf.soap.SOAPRequestObject;
import com.eibus.xml.nom.Node;
import com.eibus.xml.xpath.XPath;
import com.eibus.xml.xpath.XPathMetaInfo;


/**
 * UI supportive class to enable showing the list of organizations. 
 * OrgUser attribute (boolean) indicates whether the auth user is org user in the organization.
 * AdminRights attribute (boolean) indicates whether the admin user has admin rights in the organization.
 * 
 * @author kekema
 *
 */
public class UIOrganization extends UIOrganizationBase
{
    public UIOrganization()
    {
        this((BusObjectConfig)null);
    }

    public UIOrganization(BusObjectConfig config)
    {
        super(config);
    }

    /**
     * Get list of organizations.
     * 
     * @param authUserDN
     * @return
     */
    public static BusObjectIterator<UIOrganization> getUIOrganizationObjects(String authUserDN)
    {
    	if (authUserDN != null)
    	{
    		authUserDN = authUserDN.replaceAll("[\\n\\t]", "");
    	}    	
		int response = 0;
		Vector<UIOrganization> result = new Vector<UIOrganization>();

		// determine the LDAP root
		String ldapRoot = LDAP.getRoot();
		
		// fire soap request for getting organizations from LDAP
        String namespace = "http://schemas.cordys.com/1.0/ldap";
        String methodName = "GetOrganizations";
        
        String[] paramNames = new String[] { "dn", "sort" };
        Object[] paramValues = new Object[] { ldapRoot, "asc" };
        
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
		        	int nNode = XPath.getFirstMatch("./description/string", new XPathMetaInfo(), resultNode);
		        	String description = Node.getData(nNode);
	        		// compose entry
	        	    UIOrganization org = new UIOrganization();
		        	org.setDN(dn);
		        	org.setDescription(description);
		        	if (Util.isSet(authUserDN))
		        	{
		        		org.setOrgUser(CordysUser.isUserInOrganization(authUserDN, dn));
		        	}
		        	result.add(org);
		        }
			}
        }
        catch (Exception e)
        {
        	throw new UserAdminException("Not able to read organizations from Cordys LDAP.", e);
        }
		finally
		{
			if (response > 0)
			{
				Node.delete(response);
				response = 0;
			}
		}
        return new BusObjectArray<UIOrganization>(result);
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
