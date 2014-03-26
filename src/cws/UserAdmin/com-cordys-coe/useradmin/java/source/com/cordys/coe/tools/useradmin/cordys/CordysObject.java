package com.cordys.coe.tools.useradmin.cordys;

/**
 * To abstract cordys entities like assignments, tasks, etc
 * 
 * @author kekema
 * 
 */
public abstract class CordysObject
{
	public abstract String getDescription();	// for sorting purposes
	public abstract void cleanup();
}