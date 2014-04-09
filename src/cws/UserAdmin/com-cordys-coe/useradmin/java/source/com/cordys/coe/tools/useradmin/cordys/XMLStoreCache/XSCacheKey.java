package com.cordys.coe.tools.useradmin.cordys.XMLStoreCache;

import com.eibus.util.cache.CacheKey;

/**
 * XMLStoreObject Cache key.
 * 
 * @author kekema
 *
 */
public class XSCacheKey extends CacheKey
{
	private final String key;
	
	public XSCacheKey(String key)
	{
		this.key = key;
	}

	@Override
	public boolean equals(Object otherKeyObject) 
	{
		return (this.toString().equals(otherKeyObject.toString()));
	}

	@Override
	public int hashCode() 
	{
		return this.key.hashCode();
	}

	@Override
	public String toString() 
	{
		return this.key;
	}
	
	public String getKey()
	{
		return this.key;
	}
}