package com.cordys.coe.tools.useradmin.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.cordys.cpc.bsf.busobject.BSF;
import com.cordys.cpc.bsf.soap.SOAPRequestObject;

public class Util
{
    /**
     * Holds the format of a Cordys Date String
     */    
	public static final String CORDYS_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	
    /**
     * Returns whether a string is set with a value unequal empty
     *
     * @param string String to be checked
     *
     * @return boolean true/false
     */
    public static boolean isSet(String string)
    {
        return ((string != null) && (!string.trim().equals("")));
    }
    
    /**
     * Returns the current date in Cordys format.
     * @return
     */
    public static String getCurrentDate()
    {
        String returnValue = null;

        SimpleDateFormat targetFormat = new SimpleDateFormat(CORDYS_DATE_FORMAT);
        returnValue = targetFormat.format(new Date());

        return returnValue;
    }
    
    /**
     * Return name from DN (cn part)
     * 
     * @param DN
     * @return
     */
    public static String getNameFromDN(String DN)
    {
    	String result = "";
    	int ci = DN.indexOf(',');		
    	int cei = DN.indexOf("cn=");	
    	if (cei == -1)
    	{
    		cei = DN.indexOf("CN=");
    	}
    	if ((ci > -1) && (cei == 0))
    	{
    		result = DN.substring(3, DN.indexOf(','));
    	}
    	else
    	{
    		result = DN;
    	}
    	return result;
    }
    
    /**
     * Set the request user to an Administrator user
     * 
     * @param sro  soaprequest object 
     */
    public static void setRequestUserToAdministrator(SOAPRequestObject sro)
    {
    	sro.setUser("cn=SYSTEM,cn=organizational users," + BSF.getOrganization());
    }
    
    /**
     * Return name of current organization
     * 
     * @return
     */
    public static String getCurrentOrgName()
    {
    	String result = "";
    	String DN = BSF.getOrganization();
    	int ci = DN.indexOf(',');		
    	int cei = DN.indexOf("o=");	
    	if (cei == -1)
    	{
    		cei = DN.indexOf("O=");	
    	}
    	if ((ci > -1) && (cei == 0))
    	{
    		result = DN.substring(2, DN.indexOf(','));
    	}
    	else
    	{
    		result = DN;
    	}
    	return result;
    }    
}