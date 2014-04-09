package com.cordys.coe.tools.useradmin.cordys;

import java.util.ArrayList;

import com.cordys.coe.tools.useradmin.cordys.exception.CordysException;
import com.cordys.cpc.bsf.busobject.BSF;
import com.cordys.cpc.bsf.soap.SOAPRequestObject;
import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;

/**
 * Class to abstract functionality in Generic LDAP connector
 * 
 * @author kekema
 *
 */
public class GenLDAP
{
	private static Document xmlDoc = BSF.getXMLDocument();

	/**
	 * Get ext LDAP object
	 * 
	 * @param dn
	 * @param returnAttributes
	 * @return
	 */
	public static int getLDAPObject (String dn, ArrayList<String> returnAttributes)
	{	
        String namespace = "http://genldap.coe.cordys.com/1.2/methods";
        String methodName = "GetLDAPObject";
        
        String[] paramNames = new String[] { "dn"};
        Object[] paramValues = new Object[] { dn };
        
        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, paramNames, paramValues);
        
        int response = 0;
        int paramNode = 0;
        try 
        {
        	if (returnAttributes.size() > 0)
        	{
        		String paramString = "<return><include>";
        		for (String returnAttribute : returnAttributes)
        		{
        			paramString = paramString + "<attribute>" + returnAttribute + "</attribute>";
        		}
        		paramString = paramString + "</include></return>";
        		paramNode = xmlDoc.parseString(paramString);
        		sro.addParameterAsXml(paramNode);
        	}
            response = sro.execute();   
        }
        catch (Exception e)
        {
        	throw new CordysException("Not able to get object from external LDAP (" + dn + ")", e);
        }
        finally
        {
        	if (paramNode > 0)
        	{
        		Node.delete(paramNode);
        	}
        }
        return response;
	}
	
	/**
	 * Search ext LDAP
	 * 
	 * @param dn
	 * @param scope
	 * @param filter
	 * @param returnAttributes
	 * @return
	 */
	public static int searchLDAP(String dn, int scope, String filter, ArrayList<String> returnAttributes)
	{	
        String namespace = "http://genldap.coe.cordys.com/1.2/methods";
        String methodName = "SearchLDAP";
        
        String[] paramNames = new String[] { "dn", "scope", "filter"};
        Object[] paramValues = new Object[] { dn, String.valueOf(scope), filter };
        
        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, paramNames, paramValues);
        
        int response = 0;
        int paramNode = 0;
        try 
        {
        	if (returnAttributes.size() > 0)
        	{
        		String paramString = "<return><include>";
        		for (String returnAttribute : returnAttributes)
        		{
        			paramString = paramString + "<attribute>" + returnAttribute + "</attribute>";
        		}
        		paramString = paramString + "</include></return>";
        		paramNode = xmlDoc.parseString(paramString);
        		sro.addParameterAsXml(paramNode);
        	}
            response = sro.execute();   
        }
        catch (Exception e)
        {
        	throw new CordysException("Not able to search external LDAP (" + dn + ")", e);
        }
        finally
        {
        	if (paramNode > 0)
        	{
        		Node.delete(paramNode);
        		paramNode = 0;
        	}
        }
        return response;
	}
}