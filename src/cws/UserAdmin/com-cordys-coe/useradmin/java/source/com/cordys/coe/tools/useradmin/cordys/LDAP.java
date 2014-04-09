package com.cordys.coe.tools.useradmin.cordys;

import java.util.ArrayList;

import com.cordys.coe.tools.useradmin.cordys.exception.CordysException;
import com.cordys.cpc.bsf.busobject.BSF;
import com.cordys.cpc.bsf.soap.SOAPRequestObject;
import com.eibus.directory.soap.DirectoryException;
import com.eibus.directory.soap.EntryToXML;
import com.eibus.directory.soap.LDAPDirectory;
import com.eibus.directory.soap.XMLToEntry;
import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;
import com.eibus.xml.xpath.XPath;
import com.eibus.xml.xpath.XPathMetaInfo;
import com.novell.ldap.LDAPEntry;

/**
 * Class to represent the LDAP (CARS) with method to get/search/insert/update/delete entries, etc
 * All ldap update/query methods are by direct soap request to LDAP service container instead of 
 * using LDAPDirectory / LDAPManipulator. This to prevent (ldap cache) issues when the user is using 
 * UserManager and UserAdmin subsequently (some delay in ldap cache validation).
 * 
 * @author kekema
 *
 */
public class LDAP
{
	// LDAP action
	public static final int LA_NOACTION = 0;
	public static final int LA_UPDATE = 1;
	public static final int LA_INSERT = 2;
	
	/**
	 * Get a handle to the LDAP instance (only used for some utility methods; updates and
	 * queries are by soap requests to the LDAP container).
	 * 
	 * @return
	 */
	private static LDAPDirectory getDirectoryDefaultInstance()
	{
		LDAPDirectory ldir = null;
		try 
		{
			ldir = LDAPDirectory.getDefaultInstance();
		} 
		catch (DirectoryException e) 
		{
			throw new CordysException("Not able to get the default LDAP instance.", e);
		}
		return ldir;
	}
	
	/**
	 * Get the LDAP root
	 * 
	 * @return
	 */
	public static String getRoot()
	{
		LDAPDirectory ldir = getDirectoryDefaultInstance();
		return ldir.getDirectorySearchRoot();
	}
	
	/**
	 * Get the current context
	 * 
	 * @return
	 */
	public static String getOrganization()
	{
		LDAPDirectory ldir = getDirectoryDefaultInstance();
		return ldir.getOrganization();
	}
	
	/**
	 * Check if entry exists for given DN
	 * 
	 * @param dn
	 * @return
	 */
	public static boolean entryExists(String dn)
	{
       	LDAPEntry le = getEntry(dn);
        return (le != null);
	}
	
	/**
	 * Insert new LDAP entry
	 * 
	 * @param le
	 */
	public static void insertEntry(LDAPEntry le)
	{
		int response = 0;
		// construct method with tuple/new/entry
        String namespace = "http://schemas.cordys.com/1.0/ldap";
        String methodName = "Update";
        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, null, null);
        Document xmlDoc = BSF.getXMLDocument();
        int tupleNode = xmlDoc.createElement("tuple");
        int newNode = xmlDoc.createElement("new");
        Node.appendToChildren(newNode, tupleNode);
        EntryToXML.appendToChildren(le, newNode);
        sro.addParameterAsXml(tupleNode);
        try 
        {
            response = sro.execute();
        }
        catch (Exception e)
        {
        	throw new CordysException("Not able to insert new entry into LDAP.", e);
        }
		finally
		{
			if (response > 0)
			{
				Node.delete(response);
				response = 0;
			}
			if (tupleNode > 0)
			{
				Node.delete(tupleNode);
				tupleNode = 0;
			}
		}
	}
	
	/**
	 * Update LDAP entry
	 * 
	 * @param oldEntry
	 * @param newEntry
	 */
	public static void updateEntry(LDAPEntry oldEntry, LDAPEntry newEntry)
	{
		int response = 0;
		// construct method with tuple old/new entries
        String namespace = "http://schemas.cordys.com/1.0/ldap";
        String methodName = "Update";
        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, null, null);
        Document xmlDoc = BSF.getXMLDocument();
        int tupleNode = xmlDoc.createElement("tuple");
        int oldNode = xmlDoc.createElement("old");
        Node.appendToChildren(oldNode, tupleNode);
        EntryToXML.appendToChildren(oldEntry, oldNode);
        int newNode = xmlDoc.createElement("new");
        Node.appendToChildren(newNode, tupleNode);
        EntryToXML.appendToChildren(newEntry, newNode);
        sro.addParameterAsXml(tupleNode);
        try 
        {
            response = sro.execute();
        }
        catch (Exception e)
        {
        	throw new CordysException("Not able to update entry in LDAP.", e);
        }
		finally
		{
			if (response > 0)
			{
				Node.delete(response);
				response = 0;
			}
			if (tupleNode > 0)
			{
				Node.delete(tupleNode);
				tupleNode = 0;
			}
		}
	}
	
	/**
	 * Rename LDAP entry
	 * 
	 * @param newDN			
	 * @param oldEntry
	 * @param newEntry
	 */
	public static void renameEntry(String newDN, LDAPEntry oldEntry, LDAPEntry newEntry)
	{
		int response = 0;
		// construct method with tuple old/new entries
        String namespace = "http://schemas.cordys.com/1.0/ldap";
        String methodName = "Rename";
        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, null, null);
        Document xmlDoc = BSF.getXMLDocument();
        int tupleNode = xmlDoc.createElement("tuple");
        int oldNode = xmlDoc.createElement("old");
        Node.appendToChildren(oldNode, tupleNode);
        EntryToXML.appendToChildren(oldEntry, oldNode);
        int newNode = xmlDoc.createElement("new");
        Node.appendToChildren(newNode, tupleNode);
        int newEntryNode = EntryToXML.appendToChildren(newEntry, newNode);
        Node.setAttribute(newEntryNode, "dn", newDN);
        sro.addParameterAsXml(tupleNode);
        try 
        {
            response = sro.execute();
        }
        catch (Exception e)
        {
        	throw new CordysException("Not able to rename entry in LDAP.", e);
        }
		finally
		{
			if (response > 0)
			{
				Node.delete(response);
				response = 0;
			}
			if (tupleNode > 0)
			{
				Node.delete(tupleNode);
				tupleNode = 0;
			}
		}
	}
	
	/**
	 * Delete one or more LDAP entries
	 * 
	 * @param dns
	 */
	public static void deleteEntriesRecursive(String... dns)
	{
		int response = 0;
		ArrayList<Integer> tupleNodeList = new ArrayList<Integer>();
		// construct method with tuple/old/entry
        String namespace = "http://schemas.cordys.com/1.0/ldap";
        String methodName = "DeleteRecursive";
        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, null, null);
        Document xmlDoc = BSF.getXMLDocument();
        for (String dn : dns)
        {
	        int tupleNode = xmlDoc.createElement("tuple");
	        int oldNode = xmlDoc.createElement("old");
	        Node.appendToChildren(oldNode, tupleNode);
	        int entryNode = xmlDoc.createElement("entry");
	        Node.appendToChildren(entryNode, oldNode);
	        Node.setAttribute(entryNode, "dn", dn);
	        sro.addParameterAsXml(tupleNode);
	        tupleNodeList.add(new Integer(tupleNode));
        }
        try 
        {
            response = sro.execute();
            // Check the response
            if (XPath.getFirstMatch("//*[local-name()='tuple']/*/*[local-name()='entry']", new XPathMetaInfo(), response) == 0)
            {
                throw new CordysException("Not able to delete entries from LDAP (first entry: "+dns[0]+")");
            }
        }
        catch (Exception e)
        {
        	throw new CordysException("Not able to delete entries from LDAP (first entry: "+dns[0]+")", e);
        }
		finally
		{
			if (response > 0)
			{
				Node.delete(response);
				response = 0;
			}
			for (Integer tupleNode : tupleNodeList)
			{
				if (tupleNode.intValue() > 0)
				{
					Node.delete(tupleNode.intValue());
				}
			}
		}	
	}
	
	/**
	 * Execute searchLDAP soap request
	 * 
	 * @param dn
	 * @param scope
	 * @param filter
	 * @param sort
	 * @param returnValues
	 * @return
	 */
	public static int searchLDAP(String dn, int scope, String filter, String sort, boolean returnValues)
	{
        String namespace = "http://schemas.cordys.com/1.0/ldap";
        String methodName = "SearchLDAP";
        
        String[] paramNames = new String[] { "dn", "scope", "filter", "sort", "returnValues", "return" };
        Object[] paramValues = new Object[] { dn, String.valueOf(scope), filter, sort, String.valueOf(returnValues), ""};
        
        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, paramNames, paramValues);
        
        int response = 0;
        try 
        {
            response = sro.execute();    	
        }
        catch (Exception e)
        {
        	throw new CordysException("Not able to search LDAP", e);
        }
        return response;
	}
	
	/**
	 * Execute searchLDAP and return result as LDAPEntries
	 * 
	 * @param dn
	 * @param scope
	 * @param filter
	 * @param sort
	 * @param returnValues
	 * @return LDAPEntry[]
	 */
	public static LDAPEntry[] searchLDAPEntries(String dn, int scope, String filter, String sort, boolean returnValues)
	{	
		LDAPEntry[] result = null;
		int response = 0;
		try
		{
			response = searchLDAP(dn, scope, filter, sort, returnValues);
			if (response > 0)
			{
		        int[] resultNodes = XPath.getMatchingNodes(".//tuple/old/entry", null, response);
		        if (resultNodes.length > 0)
		        {
		        	result = new LDAPEntry[resultNodes.length];
		        	int iIndex = 0;
			        for (int resultNode : resultNodes) 
			        {
			        	LDAPEntry le = XMLToEntry.getEntry(resultNode);
			        	result[iIndex] = le;
			        	iIndex++;
			        }
		        }
			}
		}
		finally
		{
			if (response > 0)
			{
				Node.delete(response);
				response = 0;
			}
		}
		return result;
	}
	
	/**
	 * Get LDAP entry
	 * 
	 * @param dn	full DN
	 * @return	LDAPEntry
	 */
	public static LDAPEntry getEntry(String dn) 
	{
    	LDAPEntry le = null;
    	
        String namespace = "http://schemas.cordys.com/1.0/ldap";
        String methodName = "GetLDAPObject";
        
        String[] paramNames = new String[] { "dn"};
        Object[] paramValues = new Object[] { dn };
        
        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, paramNames, paramValues);
        int response = 0;
        try 
        {
            response = sro.execute();    
    		if (response > 0)
    		{
		        int entryNode = XPath.getFirstMatch(".//tuple/old/entry", null, response);
		        if (entryNode > 0)
		        {
		        	le = XMLToEntry.getEntry(entryNode);
		        }
    		}
        }
        catch (Exception e)
        {
        	throw new CordysException("Not able to read entry from LDAP (" + dn + ")", e);
        }
        finally
        {
        	if (response > 0)
        	{
        		Node.delete(response);
        	}
        }
    	return le;
	}
	
	//public static void clearCache()
	//{
    //	LDAPDirectory ldir = getDirectoryDefaultInstance();
    //	ldir.clearCache();
	//}
}