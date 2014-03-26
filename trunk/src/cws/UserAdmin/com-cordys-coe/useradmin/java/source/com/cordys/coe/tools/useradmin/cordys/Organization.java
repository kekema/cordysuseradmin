package com.cordys.coe.tools.useradmin.cordys;

import java.util.ArrayList;

import com.cordys.coe.tools.useradmin.cordys.exception.CordysException;
import com.cordys.coe.tools.useradmin.util.Util;
import com.cordys.cpc.bsf.soap.SOAPRequestObject;
import com.eibus.xml.nom.Node;
import com.eibus.xml.xpath.XPath;

/**
 * Class representing a Cordys Organization
 * 
 * @author kekema
 *
 */

public class Organization
{
	private String dn;
	private String description;
	private boolean defaultOrg = false;
	
	public String getDN()
	{
		return this.dn;
	}
	
	public String getDescription()
	{
		return this.description;
	}
	
	public boolean getDefaultOrg()
	{
		return this.defaultOrg;
	}
	
	/**
	 * Check multiple admin for org
	 * 
	 * @param orgDN
	 * @return
	 */
	public static boolean hasMultipleAdmins(String orgDN)
	{
		String ldaproot = LDAP.getRoot();
		String adminRole = "cn=Administrator,cn=Cordys@Work," + ldaproot;
		String orgAdminRole = "cn=organizationalAdmin,cn=Cordys ESBServer," + ldaproot;
		ArrayList<String> adminRoles = new ArrayList<String>();
		adminRoles.add(adminRole);
		adminRoles.add(orgAdminRole);
		int numberUsers = CordysUser.getNumberOfUsersWithRoles(orgDN, adminRoles);
		return (numberUsers > 1);
	}
	
	/**
	 * Get all organizations for given user
	 * 
	 * @param orgUserDN
	 * @return
	 */
	public static ArrayList<Organization> getOrganizationsForUser(String orgUserDN)
	{
		ArrayList<Organization> result = new ArrayList<Organization>();
		
		if (Util.isSet(orgUserDN))
		{
			String namespace = "http://schemas.cordys.com/1.0/ldap";
	        String methodName = "GetUserDetails";
	        String[] paramNames = new String[] { "dn" };
	        Object[] paramValues = new Object[] { orgUserDN };
	        
	        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, paramNames, paramValues);
	
	        int response = 0;
	        try
	        {
	        	response = sro.execute();
				if (response != 0)
				{
			        int[] orgNodes = XPath.getMatchingNodes(".//tuple/old/user/organization", null, response);
			        for (int orgNode : orgNodes) 
			        {
			        	String dn = Node.getDataElement(orgNode, "dn", "");
			        	String description = Node.getDataElement(orgNode, "description", "");
			        	boolean defaultOrg = Boolean.parseBoolean(Node.getAttribute(orgNode, "default", "false"));
			        	Organization organization = new Organization();
			        	organization.dn = dn;
			        	organization.description = description;
			        	organization.defaultOrg = defaultOrg;
			        	result.add(organization);
			        }
				}
	        }
	        catch (Exception e)
	        {
	        	throw new CordysException("Not able to read organizations for user " + orgUserDN, e);
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