package com.cordys.coe.tools.useradmin.cordys;

import java.util.ArrayList;

import com.cordys.coe.tools.useradmin.util.NestedXMLObject;
import com.eibus.xml.nom.Node;
import com.eibus.xml.xpath.XPath;


/**
 * Class respresenting user from an external directory (as connected to by Cordys generic ldap connector)
 * 
 * @author kekema
 *
 */
public class ExtUser extends CordysObject
{
	private NestedXMLObject userXMLObject;
	
	/**
	 * Construct ExtUser by wrapping xml data
	 * @param rootNode
	 */
	public ExtUser(int rootNode)
	{
		userXMLObject = new NestedXMLObject(rootNode);
	}
	
	/**
	 * Cleanup underlying xml
	 */
	public void cleanup()
	{
		userXMLObject.freeXMLMemory();
		userXMLObject = null;
	}
	
	/**
	 * Get ext user DN
	 * 
	 * @return
	 */
	public String getDN()
	{
		return(userXMLObject.getAttributeStringValue("dn", null));
	}
	
	/**
	 * Get ext user CN
	 * 
	 * @return
	 */
	public String getCN()
	{
		return(userXMLObject.getStringValue("cn/string", null));
	}
	
	/**
	 * Get ext user display name
	 * 
	 * @return
	 */
	public String getDisplayName()
	{
		return(userXMLObject.getStringValue("displayName/string", null));
	}
	
	/**
	 * Get description (implements CordysObject.getDescription())
	 * 
	 * @return
	 */
	public String getDescription()
	{
		return(getDisplayName());
	}	
	
	/**
	 * Get ext user distinguished name
	 * 
	 * @return
	 */
	public String getDistinguishedName()
	{
		return(userXMLObject.getStringValue("distinguishedName/string", null));
	}
	
	/**
	 * Get ext user postal address
	 * @return
	 */
	public String getPostalAddress()
	{
		return(userXMLObject.getStringValue("postalAddress/string", null));
	}
	
	/**
	 * Get ext user company
	 * 
	 * @return
	 */
	public String getCompany()
	{
		return(userXMLObject.getStringValue("company/string", null));
	}
	
	/**
	 * Get ext user telephone
	 * 
	 * @return
	 */
	public String getTelephoneNumber()
	{
		return(userXMLObject.getStringValue("telephoneNumber/string", null));
	}
	
	/**
	 * Get ext user email
	 * 
	 * @return
	 */
	public String getMail()
	{
		return(userXMLObject.getStringValue("mail/string", null));
	}
	
	/**
	 * Get all groups the user is member of
	 * 
	 * @return
	 */
	public ArrayList<String> getMemberOfList()
	{
		return(userXMLObject.getStringValues("memberOf/string"));
	}
	
	/**
	 * Get ext user object
	 * 
	 * @param extUserDN
	 * @return
	 */
	public static ExtUser getExtUser(String extUserDN)
	{
		ExtUser extUser = null;
		ArrayList<String> returnAttributes = new ArrayList<String>();
		returnAttributes.add("cn");
		returnAttributes.add("displayName");
		returnAttributes.add("distinguishedName");
		returnAttributes.add("postalAddress");
		returnAttributes.add("company");
		returnAttributes.add("telephoneNumber");
		returnAttributes.add("mail");
		int ldapResponse = 0;
		try
		{
			ldapResponse = GenLDAP.getLDAPObject(extUserDN, returnAttributes);
			if (ldapResponse > 0)
			{
		        int entryNode = XPath.getFirstMatch(".//tuple/old/entry", null, ldapResponse);
        		extUser = new ExtUser(Node.unlink(entryNode));
			}
		}
		finally
		{
			if (ldapResponse > 0)
			{
				Node.delete(ldapResponse);
				ldapResponse = 0;
			}
		}
		return extUser;
	}
	
	/**
	 * Get all users as belonging to the given group; optionally including users from all subgroups
	 * 
	 * @param groupDN
	 * @param userSearchRoot
	 * @param includeSubgroupUsers
	 * @return
	 */
	public static CordysObjectList getGroupUsers(String groupDN, String userSearchRoot, boolean includeSubgroupUsers)
	{
		CordysObjectList extUsers = new CordysObjectList();
		
		String filter = "(&(objectClass=User)(memberof";
		if (includeSubgroupUsers)
		{
			filter = filter + ":1.2.840.113556.1.4.1941";
		}
		filter = filter + ":=" + groupDN + "))";
		ArrayList<String> returnAttributes = new ArrayList<String>();
		returnAttributes.add("dn");
		returnAttributes.add("cn");
		returnAttributes.add("displayName");
		returnAttributes.add("memberOf");
		int ldapResponse = 0;
		try
		{
			ldapResponse = GenLDAP.searchLDAP(userSearchRoot, 2, filter, returnAttributes);
			if (ldapResponse > 0)
			{
		        int[] entryNodes = XPath.getMatchingNodes(".//tuple/old/entry", null, ldapResponse);
		        for (int entryNode : entryNodes) 
		        {
        			ExtUser extUser = new ExtUser(Node.unlink(entryNode));
        			extUsers.add(extUser);
		        }
			}
		}
		finally
		{
			if (ldapResponse > 0)
			{
				Node.delete(ldapResponse);
				ldapResponse = 0;
			}
		}
		return extUsers;
	}
}