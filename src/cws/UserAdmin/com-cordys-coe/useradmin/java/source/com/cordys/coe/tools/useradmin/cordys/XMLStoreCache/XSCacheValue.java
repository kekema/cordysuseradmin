package com.cordys.coe.tools.useradmin.cordys.XMLStoreCache;

import com.cordys.coe.tools.useradmin.cordys.XMLStoreObject;
import com.eibus.util.cache.CacheValue;

/**
 * XMLStoreObject Cache value.
 * 
 * @author kekema
 *
 */
public class XSCacheValue extends CacheValue
{
	 private XMLStoreObject xmlStoreObject;
	 
	 public XSCacheValue(XMLStoreObject xmlStoreObject)
	 {
		 this.xmlStoreObject = xmlStoreObject;	 
	 }
	 
	 public XMLStoreObject getXMLStoreObject()
	 {
		 return this.xmlStoreObject;
	 }
}