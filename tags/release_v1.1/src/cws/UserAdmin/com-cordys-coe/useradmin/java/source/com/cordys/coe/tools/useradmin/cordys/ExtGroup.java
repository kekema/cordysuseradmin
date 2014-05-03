package com.cordys.coe.tools.useradmin.cordys;

import java.util.ArrayList;
import java.util.HashSet;

import com.cordys.coe.tools.useradmin.cordys.exception.CordysException;
import com.cordys.cpc.bsf.busobject.BSF;
import com.cordys.cpc.bsf.soap.SOAPRequestObject;
import com.eibus.xml.nom.Node;
import com.eibus.xml.xpath.XPath;

/**
 * Class representing a group from external directory
 * 
 * @author kekema
 *
 */
public class ExtGroup
{
	/**
	 * Get display name of group in ext directory
	 * 
	 * @param groupDN
	 * @return
	 */
	public static String getGroupDisplayName(String groupDN)
	{
		String result = null;
		
		int ldapResponse = 0;
		try
		{
			ArrayList<String> returnAttributes = new ArrayList<String>();
			returnAttributes.add("displayName");
			ldapResponse = GenLDAP.getLDAPObject(groupDN, returnAttributes);
			if (ldapResponse > 0)
			{
				int displayNameStringNode = XPath.getFirstMatch("./tuple/old/entry/displayName/string", null, ldapResponse);
				if (displayNameStringNode > 0)
				{
					result = Node.getData(displayNameStringNode);
				}
			}
		}
        catch (Exception e)
        {
        	throw new CordysException("Not able to read group from external LDAP (" + groupDN + ")", e);
        }
        finally
        {
        	if (ldapResponse > 0)
        	{
        		Node.delete(ldapResponse);
        	}
        }		
		return result;
	}
	
	/**
	 * Get members of a group - this can be users as well as subgroups
	 * 
	 * @param groupDN
	 * @return
	 */
	public static HashSet<String> getGroupMembers(String groupDN)
	{
		HashSet<String> result = new HashSet<String>();
		
        String namespace = "http://genldap.coe.cordys.com/1.2/methods";
        String methodName = "GetLDAPObject";
        
        String[] paramNames = new String[] { "dn"};
        Object[] paramValues = new Object[] { groupDN };
        
        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, paramNames, paramValues);
        
        int response = 0;
        int paramNode = 0;
        try 
        {
        	paramNode = BSF.getXMLDocument().parseString("<return><include><attribute>member</attribute></include></return>");
        	sro.addParameterAsXml(paramNode);
            response = sro.execute();    
    		if (response > 0)
    		{
		        int[] memberStringNodes = XPath.getMatchingNodes(".//tuple/old/entry/member/string", null, response);
		        for (int memberStringNode : memberStringNodes) 
		        {
		        	String member = Node.getData(memberStringNode);
		        	result.add(member);
		        }
    		}
        }
        catch (Exception e)
        {
        	throw new CordysException("Not able to read group from external LDAP (" + groupDN + ")", e);
        }
        finally
        {
        	if (response > 0)
        	{
        		Node.delete(response);
        	}
        	if (paramNode > 0)
        	{
        		Node.delete(paramNode);
        	}
        }
        
		return result;
	}
}