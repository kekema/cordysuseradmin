package com.cordys.coe.tools.useradmin.cordys.XMLStoreCache;

import com.cordys.coe.tools.useradmin.cordys.XMLStore;
import com.cordys.coe.tools.useradmin.cordys.XMLStoreObject;
import com.eibus.util.cache.Cache;
import com.eibus.util.cache.CacheKey;
import com.eibus.util.cache.CacheValue;
import com.eibus.util.cache.exception.CacheLoadingException;

/**
 * Caches (static) xmlStore objects. 
 * 
 * @author kekema
 *
 */
public class XSCache extends Cache
{
	private final static long CACHE_SIZE = 50L;			// 50 xmlStoreObjects
	private final static int MAX_IDLE_TIME = 1800;		// 0.5 hour
	
	public XSCache()
	{
		super(CACHE_SIZE);
		setMaxIdleTime(MAX_IDLE_TIME * 1000L);
	}

	@Override
	protected void cleanCacheValueInternal(CacheKey cacheKey, CacheValue cacheValue) 
	{
		XMLStoreObject xmlStoreObject = ((XSCacheValue)cacheValue).getXMLStoreObject();
		if (xmlStoreObject != null)
		{
			xmlStoreObject.setCached(false);
			xmlStoreObject.freeXMLMemory();
		}
	}

	@Override
	protected CacheValue loadCacheValueInternal(CacheKey cacheKey) throws CacheLoadingException 
	{
		XSCacheKey xsCacheKey = (XSCacheKey)cacheKey;
		String key = xsCacheKey.getKey();
		XMLStoreObject xmlStoreObject = null;
		try
		{
			xmlStoreObject = XMLStore.getXMLObject(key);
			xmlStoreObject.setCached(true);
		}
		catch (Exception e)
		{
			throw new CacheLoadingException("Not able to load cache for xmlstore object: " + key, e);
		}
		return new XSCacheValue(xmlStoreObject);
	}
	
	public XMLStoreObject getXMLStoreObject(String key)
	{
		CacheKey cacheKey = new XSCacheKey(key);
		XSCacheValue xsCacheValue = null;
		XMLStoreObject xmlStoreObject = null;
		try
		{
			xsCacheValue = (XSCacheValue)get(cacheKey);
			xmlStoreObject = xsCacheValue.getXMLStoreObject();
		}
		catch (CacheLoadingException e)
		{
			Throwable t = e.getCause();
			if (t == null)
			{
				throw new RuntimeException("Could not load xmlstore object for key " + key, e);
			}
			throw ((RuntimeException)new RuntimeException(t.getMessage()).initCause(t));
		}
		finally
		{
			if (xsCacheValue != null)
			{
				releaseValue(xsCacheValue);
			}
		}
		return xmlStoreObject;
	}
	
	public void invalidate(String key)
	{
		CacheKey cacheKey = new XSCacheKey(key);
		invalidate(cacheKey);
	}
}