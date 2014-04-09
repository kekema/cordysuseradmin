package com.cordys.coe.tools.useradmin.cordys;

import com.cordys.coe.tools.useradmin.cordys.XMLStoreCache.XSCache;
import com.cordys.coe.tools.useradmin.cordys.exception.CordysException;
import com.cordys.cpc.bsf.busobject.BSF;
import com.cordys.cpc.bsf.soap.SOAPRequestObject;
import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;
import com.eibus.xml.xpath.XPath;
import com.eibus.xml.xpath.XPathMetaInfo;

/**
 * Class to represent XMLStore with methods to read XMLObject, etc
 * 
 * @author kekema
 *
 */
public class XMLStore
{
	static XSCache xsCache = new XSCache();
	
	public static XMLStoreObject getXMLObject(String key)
	{
		return (getXMLObject(key, false));
	}
	
	public static XMLStoreObject getXMLObjectByCache(String key)
	{
		return (xsCache.getXMLStoreObject(key));
	}
	
	/**
	 * Get an XMLObject from XMLStore
	 * 
	 * @param key
	 * @param isvVersion 	either load the isv version or the version as available (user/org/isv)
	 * 
	 * @return XMLObject instance
	 */
	public static XMLStoreObject getXMLObject(String key, boolean isvVersion)
	{
		int response = 0;
		int resultNode = 0;
		
		String level = null;
		String lastModified = null;

        String namespace = "http://schemas.cordys.com/1.0/xmlstore";
        String methodName = "GetXMLObject";
        
        String[] paramNames = new String[] { "key" };
        Object[] paramValues = new Object[] { key };
        
        SOAPRequestObject sro = null;
        int paramNode = 0;
        try 
        {
        	if (isvVersion)
        	{
        		sro = new SOAPRequestObject(namespace, methodName, null, null);
            	Document nomDocument = BSF.getXMLDocument();
            	paramNode = nomDocument.parseString("<key version=\"isv\">"+key+"</key>");
            	sro.addParameterAsXml(paramNode);
        	}
        	else
        	{
        		sro = new SOAPRequestObject(namespace, methodName, paramNames, paramValues);
        	}
            response = sro.execute();
			if (response != 0)
			{
				// get level/lastmodified
				int nNode = XPath.getFirstMatch("//tuple", new XPathMetaInfo(), response);
				if (nNode > 0)
				{
					level = Node.getAttribute(nNode, "level");
					lastModified = Node.getAttribute(nNode, "lastModified");
				}
				// get tuple/old
				nNode = XPath.getFirstMatch("//old", new XPathMetaInfo(), response);
				if (nNode >0)
				{
					nNode = Node.getFirstChildElement(nNode);
					if (nNode > 0)
					{
						resultNode = Node.clone(nNode, true);
					}
				}
			}
        }
        catch (Exception e)
        {
        	throw new CordysException("Not able to read xml " + key + " from XMLStore.", e);
        }
		finally
		{
			if (response > 0)
			{
				Node.delete(response);
				response = 0;
			}
			if (paramNode > 0)
			{
				Node.delete(paramNode);
				paramNode = 0;
			}
		}
        // compose resulting XMLObject
        XMLStoreObject xmlObject = null;
		if (resultNode > 0)
		{
			xmlObject = new XMLStoreObject(resultNode);
			xmlObject.setXmlStoreLastModified(lastModified);
			xmlObject.setXmlStoreLevel(level);
		}
		return xmlObject;
	}
	
	/**
	 * Update XMLStore Object
	 * 
	 * @param key
	 * @param oldObject
	 * @param newObject
	 */
	public static void updateXMLObject(String key, XMLStoreObject oldObject, XMLStoreObject newObject)
	{
		int response = 0;

        String namespace = "http://schemas.cordys.com/1.0/xmlstore";
        String methodName = "UpdateXMLObject";
        
        int tupleNode = 0;
        try
        {
	        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, null, null);
	        Document nomDocument = BSF.getXMLDocument();
	        // compose payload with tuple old/new
	        String payload = "<tuple key=\""+key+"\" lastModified=\""+oldObject.getXmlStoreLastModified()+"\"><old>" + oldObject.toString() + "</old><new>" + newObject.toString() + "</new></tuple>";
	        tupleNode = nomDocument.parseString(payload);
	        Node.removeAttributesRecursive(tupleNode, "xmlns:SOAP", "xmlns", null, null);
	        sro.addParameterAsXml(tupleNode);
	        response = sro.execute();
        }
        catch (Exception e)
        {
        	throw new CordysException("Not able to update xml " + key + " in XMLStore.", e);
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
	 * Delete XMLStore Object
	 * 
	 * @param key
	 * @param lastModified
	 * @param level
	 * @param original
	 * @param name
	 */
	public static void deleteXMLObject(String key, String lastModified, String level, String original, String name)
	{
		int response = 0;

        String namespace = "http://schemas.cordys.com/1.0/xmlstore";
        String methodName = "UpdateXMLObject";
        
        int tupleNode = 0;
        try
        {
	        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, null, null);
	        Document nomDocument = BSF.getXMLDocument();
	        // compose payload with tuple
	        String payload = "<tuple key=\"" + key + "\" lastModified=\"" + lastModified + "\" level=\"" + level + "\" original=\"" + original + "\" name=\"" + name +"\"/>";
	        tupleNode = nomDocument.parseString(payload);
	        Node.removeAttributesRecursive(tupleNode, "xmlns:SOAP", "xmlns", null, null);
	        sro.addParameterAsXml(tupleNode);
	        response = sro.execute();
        }
        catch (Exception e)
        {
        	throw new CordysException("Not able to delete xml " + key + " in XMLStore.", e);
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
			// invalidate any entry in the XMLStore cache
			xsCache.invalidate(key);
        }		
	}
}