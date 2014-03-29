package com.cordys.coe.tools.useradmin.cordys;

import com.cordys.coe.tools.useradmin.util.FlatXMLObject;
import com.cordys.coe.tools.useradmin.util.Util;

/**
 * Data class for Cordys Version information.
 * 
 * @author kekema
 *
 */
public class CordysVersion
{
	private String version;
	private String build;
	private String versionAndBuild;
	private String major;
	private String minor;
	private String cu = "";
	
	/**
	 * Constructor
	 * 
	 * @param versionDataNode	result from GetVersion webservice operation
	 */
	public CordysVersion(int versionDataNode)
	{
		FlatXMLObject xmlObject = new FlatXMLObject(versionDataNode);
		this.version = xmlObject.getStringValue("version", "");
		this.build = xmlObject.getStringValue("build", "");
		this.versionAndBuild = xmlObject.getStringValue("string", "");
		setVersionParts();
	}
	
	private void setVersionParts()
	{
		String[] tokens = this.version.split("\\.");
		this.major = tokens[0];
		this.minor = tokens[1];
		if (tokens.length > 2)
		{
			this.cu = tokens[2];
		}
	}
	
	/**
	 * Constructor
	 * 
	 * @param version	major.minor.cu
	 */
	public CordysVersion(String version)
	{
		this.version = version;
		setVersionParts();
	}
	
	public String getVersion()
	{
		return this.version;
	}
	
	public String getBuild()
	{
		return this.build;
	}
	
	public String getFormattedInternal()
	{
		return this.versionAndBuild;
	}
	
	public String getMajor()
	{
		return this.major;
	}

	public int getIntMajor()
	{
		int result = 0;
		if ("D1".equals(this.major))
		{
			result = 4;
		}
		return result;
	}
	
	public String getMinor()
	{
		return this.minor;
	}
	
	public int getIntMinor()
	{
		int result = 0;
		if (Util.isSet(this.minor))
		{
			result = new Integer(this.minor).intValue();
		}
		return result;
	}
	
	public String getMajorMinor()
	{
		return getMajor() + "." + getMinor();
	}
	
	public String getCU()
	{
		return this.cu;
	}	
	
	public int getIntCU()
	{
		int result = 0;
		if (Util.isSet(this.cu))
		{
			result = new Integer(this.cu).intValue();
		}
		return result;
	}	
	
	public String getFormattedExternal()
	{
		String result = "";
		int major = getIntMajor();
		int minor = getIntMinor();
		if ((major <= 4) && (minor <= 3))
		{
			result = "Cordys " + getIntMajor() + "." + getIntMinor();
			if (getIntCU() != 0)
			{
				result = result + " CU" + getIntCU();
			}
		}
		else
		{
			if ((major == 4) && (minor == 4))
			{
				result = "OpenText Cordys 10.5";
				if (getIntCU() != 0)
				{
					result = result + " CU" + getIntCU();
				}				
			}
		}
		return result;
	}
}