package com.cordys.coe.tools.useradmin.cordys;

import com.cordys.coe.tools.useradmin.cordys.exception.CordysException;
import com.cordys.cpc.bsf.soap.SOAPRequestObject;
import com.eibus.xml.nom.Node;

/**
 * Get info on the Cordys Installation
 * 
 * @author kekema
 *
 */
public class CordysInstallation
{
	public static CordysVersion getVersion()
	{
		CordysVersion result = null;
		
		String namespace = "http://schemas.cordys.com/1.0/monitor";
        String methodName = "GetVersion";
        
        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, null, null);

        int response = 0;
        try
        {
        	response = sro.execute();
			if (response != 0)
			{
				result = new CordysVersion(response);
			}
        }
        catch (Exception e)
        {
        	throw new CordysException("Not able to determine the Cordys version.", e);
        }
		finally
		{
			if (response != 0)
			{
				Node.delete(response);
			}
		}
        return result;
	}
}