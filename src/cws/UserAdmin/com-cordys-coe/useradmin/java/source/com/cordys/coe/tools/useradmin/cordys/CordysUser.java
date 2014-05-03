package com.cordys.coe.tools.useradmin.cordys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.cordys.coe.tools.useradmin.cordys.exception.CordysException;
import com.cordys.coe.tools.useradmin.util.PasswordHashAndDigest;
import com.cordys.coe.tools.useradmin.util.Util;
import com.cordys.cpc.bsf.busobject.BSF;
import com.cordys.cpc.bsf.soap.SOAPRequestObject;
import com.eibus.directory.soap.DN;
import com.eibus.directory.soap.EntryToXML;
import com.eibus.directory.soap.LDAPUtil;
import com.eibus.directory.soap.XMLToEntry;
import com.eibus.xml.nom.Node;
import com.eibus.xml.xpath.XPath;
import com.eibus.xml.xpath.XPathMetaInfo;
import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPEntry;

/**
 * Class to represent a cordys user as can be maintained in CARS.
 * It's attributes relate to both attributes from the authenticated user as well as the organizational user.
 * 
 * @author kekema
 *
 */
public class CordysUser
{
	public static final String AT_CUSTOM = "custom";
	public static final String AT_DOMAIN = "domain";
	public static final String AT_CERTIFIED = "certified";
	
	private String cn;					// corresponds to "User Name" (ldap: cn part in the auth user / org user dn)
	private String description;			// display name; corresponds to "Full User Name" (ldap: org user description) 
	private String osidentity;			// corresponds to "User ID" (ldap: auth user osidentity)
	private String authenticationtype;	// custom or domain
	private String userpassword;		// applicable when creating a new user and auth type is custom
	private boolean enable = true;
	private String defaultcontext;	
	private String company;
	private String registeredAddress;
	private String telephoneNumber;
	private String telephoneNumber2;
	private String facsimileTelephoneNumber;
	private String email;
	private String labeleduri;
	private ArrayList<String> roleDNs;
	private String busextension;			
	
	private String authUserDN = null;
	private String orgUserDN = null;
	
	/**
	 * Common name
	 * @param cn
	 */
	public void setCN(String cn)
	{
		this.cn = cn;
	}
	
	/**
	 * OrgUserDN
	 * @param cn
	 */
	public void setOrgUserDN(String dn)
	{
		this.orgUserDN = dn;
	}
	
	/**
	 * Auth user DN
	 * @param authUserDN
	 */
	public void setAuthUserDN(String authUserDN)
	{
		this.authUserDN = authUserDN;
	}
	
	/**
	 * id in the os
	 * @param osidentity
	 */
	public void setOSIdentity(String osidentity)
	{
		this.osidentity = osidentity;
	}
	
	/**
	 * Used for full name (display name)
	 * @param description
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	/**
	 * default context
	 * @param osidentity
	 */
	public void setDefaultcontext(String defaultcontext)
	{
		this.defaultcontext = defaultcontext;
	}
	
	/**
	 * Set the company
	 * @param company
	 */
	public void setCompany(String company)
	{
		this.company = company;
	}
	
	/**
	 * Set the address
	 * @param registeredAddress
	 */
	public void setRegisteredAddress(String registeredAddress)
	{
		this.registeredAddress = registeredAddress;
	}
	
	/**
	 * Set the telephone number
	 * @param telephoneNumber
	 */
	public void setTelephoneNumber(String telephoneNumber)
	{
		this.telephoneNumber = telephoneNumber;
	}
	
	/**
	 * Set the telephone number2
	 * @param telephoneNumber2
	 */
	public void setTelephoneNumber2(String telephoneNumber2)
	{
		this.telephoneNumber2 = telephoneNumber2;
	}
	
	/**
	 * Set the facsimile telephone number
	 * @param facsimileTelephoneNumber
	 */
	public void setFacsimileTelephoneNumber(String facsimileTelephoneNumber)
	{
		this.facsimileTelephoneNumber = facsimileTelephoneNumber;
	}
	
	/**
	 * Set the email
	 * @param email
	 */
	public void setEmail(String email)
	{
		this.email = email;
	}
	
	/**
	 * Set the website url
	 * @param labeleduri
	 */
	public void setLabeleduri(String labeleduri)
	{
		this.labeleduri = labeleduri;
	}
	
	/**
	 * Set the authentication type (custom or domain)
	 * @param authenticationType
	 */
	public void setAuthenticationType(String authenticationType)
	{
		this.authenticationtype = authenticationType;
	}
	
	/**
	 * Set the enable flag
	 * @param enable
	 */
	public void setEnable(boolean enable)
	{
		this.enable = enable;
	}
	
	/**
	 * Set the user pw (applicable in case of custom auth)
	 * @param userPassword
	 */
	public void setUserPassword(String userPassword)
	{
		this.userpassword = userPassword;
	}
	
	/**
	 * Set the busextension
	 * @param busextension
	 */
	public void setBusextension(String busextension)
	{
		this.busextension = busextension;
	}
	
	/**
	 * Get the role DN's
	 * @return
	 */
	public ArrayList<String> getRoleDNs()
	{
		return this.roleDNs;
	}
	
	/**
	 * Set the role DN's
	 * @param roleDNs
	 */
	public void setRoleDNs(ArrayList<String> roleDNs)
	{
		this.roleDNs = roleDNs;
	}
	
	/**
	 * Common name (User Name)
	 * @return
	 */
	public String getCN()
	{
		return this.cn;
	}
	
	/**
	 * OrgUserDN
	 * @return
	 */
	public String getOrgUserDN()
	{
		return this.orgUserDN;
	}
	
	/**
	 * AuthUserDN
	 * @return
	 */
	public String getAuthUserDN()
	{
		return this.authUserDN;
	}
	
	/**
	 * OSIdentity
	 * @return
	 */
	public String getOSIdentity()
	{
		return this.osidentity;
	}
	
	/**
	 * Description (Full User Name)
	 * @return
	 */
	public String getDescription()
	{
		return this.description;
	}
	
	/**
	 * Authentication Type
	 * @return
	 */
	public String getAuthenticationType()
	{
		String authType = this.authenticationtype;
		if (!Util.isSet(authType))
		{
			authType = AT_CUSTOM;
		}
		return authType;
	}
	
	/**
	 * Enable flag
	 * @return
	 */
	public boolean getEnable()
	{
		return this.enable;
	}
		
	/**
	 * User password
	 * @return
	 */
	public String getUserPassword()
	{
		return this.userpassword;
	}
	
	/**
	 * Default Context
	 * @return
	 */
	public String getDefaultcontext()
	{
		return this.defaultcontext;
	}
	
	/**
	 * Registered Address
	 * @return
	 */
	public String getRegisteredAddress()
	{
		return this.registeredAddress;
	}
	
	/**
	 * Company
	 * @return
	 */
	public String getCompany()
	{
		return this.company;
	}
	
	/**
	 * Telephone Number
	 * @return
	 */
	public String getTelephoneNumber()
	{
		return this.telephoneNumber;
	}
	
	/**
	 * Telephone Number2
	 * @return
	 */
	public String getTelephoneNumber2()
	{
		return this.telephoneNumber2;
	}
	
	/**
	 * Facsimile TelephoneNumber
	 * @return
	 */
	public String getFacsimileTelephoneNumber()
	{
		return this.facsimileTelephoneNumber;
	}
	
	/**
	 * Email
	 * @return
	 */
	public String getEmail()
	{
		return this.email;
	}
	
	/**
	 * labeleduri
	 * @return
	 */
	public String getLabeledURI()
	{
		return this.labeleduri;
	}
	
	/**
	 * Maintain the constructed cordys user in CARS
	 * 
	 * @param onlyInsert
	 */
	public void maintainUser(boolean onlyInsert)
	{
		maintainCordysUser(onlyInsert, true, true);
	}
	
	private void maintainCordysUser(boolean onlyInsert, boolean maintainAuthUser, boolean maintainOrgUser)
	{
		// in case the CordysUser.cn (username) is updated, it will only effect the orgUserDN
		// and not the authUserDN (as in other organizations, the org user will have a reference
		// to the authUserDN. This is same behavior as in the regular Cordys User Manager
		if (!Util.isSet(this.authUserDN))
		{
			// insert new user situation
			// String instanceRoot = DN.getDN(BSF.getOrganization()).getParent().toRFCString();
			// Above statement produced a lower-case instance root. Now using the (overwritten) toString().
			String instanceRoot = DN.getDN(BSF.getOrganization()).getParent().toString();
			this.authUserDN = "cn=" + this.cn + ",cn=authenticated users," + instanceRoot;
		}
		if (!Util.isSet(this.orgUserDN))
		{		
			this.orgUserDN = "cn=" + this.cn + ",cn=organizational users," + BSF.getOrganization();
		}
		
		if (!Util.isSet(this.authenticationtype))
		{
			this.authenticationtype = AT_CUSTOM;
		}
		if (!Util.isSet(this.defaultcontext))
		{
			this.defaultcontext = BSF.getOrganization();
		}
		
		maintainAuthenticatedUser(onlyInsert);
		maintainOrganizationalUser(onlyInsert);
	}
	
	/**
	 * Maintain authenticated user - either insert or update.
	 * 
	 * @param onlyInsert
	 */
    private void maintainAuthenticatedUser(boolean onlyInsert)
    {
    	int ldapAction = LDAP.LA_NOACTION;
    	LDAPAttributeSet attrs = null;
    	LDAPEntry newEntry = null;
    	LDAPEntry oldEntry = LDAP.getEntry(this.authUserDN);
        if (oldEntry != null && !onlyInsert)
        {
            // create new entry for updating
            newEntry = LDAPUtil.cloneEntry(oldEntry);
            
            // remove editable attributes as to reconstruct them lateron from actual values
            attrs = newEntry.getAttributeSet();
            LDAPAttribute osidentity = attrs.getAttribute("osidentity");
            if (osidentity != null)
            {
            	attrs.remove(osidentity);
            }            
            LDAPAttribute authenticationtype = attrs.getAttribute("authenticationtype");
            if (authenticationtype != null)
            {
            	attrs.remove(authenticationtype);
            }           
            LDAPAttribute defaultcontext = attrs.getAttribute("defaultcontext");
            if (defaultcontext != null)
            {
            	attrs.remove(defaultcontext);
            }
            LDAPAttribute description = attrs.getAttribute("description");
            if (description != null)
            {
            	attrs.remove(description);
            }
            LDAPAttribute company = attrs.getAttribute("o");
            if (company != null)
            {
            	attrs.remove(company);
            }
            LDAPAttribute registeredAddress = attrs.getAttribute("registeredAddress");
            if (registeredAddress != null)
            {
            	attrs.remove(registeredAddress);
            }
            LDAPAttribute telephoneNumber = attrs.getAttribute("telephoneNumber");
            if (telephoneNumber != null)
            {
            	attrs.remove(telephoneNumber);
            }
            LDAPAttribute facsimileTelephoneNumber = attrs.getAttribute("facsimileTelephoneNumber");
            if (facsimileTelephoneNumber != null)
            {
            	attrs.remove(facsimileTelephoneNumber);
            }
            LDAPAttribute mail = attrs.getAttribute("mail");
            if (mail != null)
            {
            	attrs.remove(mail);
            }
            LDAPAttribute labeleduri = attrs.getAttribute("labeleduri");
            if (labeleduri != null)
            {
            	attrs.remove(labeleduri);
            }
            
            // set ldap action to update
            ldapAction = LDAP.LA_UPDATE;
        }
        if (oldEntry == null)
        {
            // the authenticated user does not exist - create it
            newEntry = new LDAPEntry(this.authUserDN);
            attrs = newEntry.getAttributeSet();

            LDAPAttribute attr = new LDAPAttribute("objectclass", "top");
            attr.addValue("busauthenticationuser");

            attrs.add(attr);
            attrs.add(new LDAPAttribute("cn", this.cn));
            
            if (Util.isSet(this.busextension))
            {
            	attrs.add(new LDAPAttribute("busextension", this.busextension));
            }
            
            // set ldap action to insert
            ldapAction = LDAP.LA_INSERT;
        }
        if (ldapAction != LDAP.LA_NOACTION)
        {
        	boolean setPW = false;
        	// below attributes to be set both for insert/update scenario
            attrs.add(new LDAPAttribute("osidentity", this.osidentity));
           	attrs.add(new LDAPAttribute("authenticationtype", this.authenticationtype));
            if (this.authenticationtype.equals(AT_CUSTOM))
            {
            	String pw = this.userpassword;
            	if (!Util.isSet(pw))
            	{
            		pw = this.cn;
            	}
            	if (!PasswordHashAndDigest.pwIsHashed(pw))
            	{
            		setPW = true;
            	}
            }          
           	attrs.add(new LDAPAttribute("defaultcontext", this.defaultcontext));           
            if (Util.isSet(this.description))
            {
            	attrs.add(new LDAPAttribute("description", this.description));
            }
            if (Util.isSet(this.company))
            {
            	attrs.add(new LDAPAttribute("o", this.company));
            }
            if (Util.isSet(this.registeredAddress))
            {
            	attrs.add(new LDAPAttribute("registeredAddress", this.registeredAddress));
            }
            if (Util.isSet(this.telephoneNumber) || Util.isSet(this.telephoneNumber2))
            {
            	LDAPAttribute attr = new LDAPAttribute("telephoneNumber");
            	if (Util.isSet(this.telephoneNumber))
            	{
            		attr.addValue(telephoneNumber);
            	}
            	if (Util.isSet(this.telephoneNumber2))
            	{
            		attr.addValue(telephoneNumber2);
            	}            			
            	attrs.add(attr);
            }
            if (Util.isSet(this.facsimileTelephoneNumber))
            {
            	attrs.add(new LDAPAttribute("facsimileTelephoneNumber", this.facsimileTelephoneNumber));
            }
            if (Util.isSet(this.email))
            {
            	attrs.add(new LDAPAttribute("mail", this.email));
            }
            if (Util.isSet(this.labeleduri))
            {
            	attrs.add(new LDAPAttribute("labeleduri", this.labeleduri));
            }
            
            // Send the request to LDAP.
            if (ldapAction == LDAP.LA_UPDATE)
            {
            	LDAP.updateEntry(oldEntry, newEntry);
            }
            else if (ldapAction == LDAP.LA_INSERT)
            {
                LDAP.insertEntry(newEntry);
            }
            if (setPW)
            {
            	String authUserName = Util.getNameFromDN(this.authUserDN);
            	String hashedPW = maintainPasswordForUser(authUserName, this.userpassword);
            	this.setUserPassword(hashedPW);
            }
        }
    }
    
    /**
     * Maintain organizational user - either insert or update
     * For roles, only (new) roles are added; existing roles are left untouched. For full role functionality, see the Role class.
     * 
     * @param onlyInsert
     */
    private void maintainOrganizationalUser(boolean onlyInsert)
    { 	
    	if (roleDNs == null)
    	{
    		roleDNs = new ArrayList<String>();
    	}
        LDAPEntry oldEntry = LDAP.getEntry(this.orgUserDN);
        if (oldEntry != null && !onlyInsert)
        {
            // create new entry for updating
        	boolean updateEntry = false;
        	boolean renameEntry = false;
        	LDAPEntry newEntry = LDAPUtil.cloneEntry(oldEntry);
        	LDAPAttributeSet attrs = newEntry.getAttributeSet();
            LDAPAttribute cn = attrs.getAttribute("cn");
            if (cn != null)
            {
            	String currentValue = cn.getStringValue();
            	if (currentValue == null)
            	{
            		currentValue = "";
            	}
            	String newValue = this.cn;
            	if (newValue == null)
            	{
            		newValue = "";
            	}
            	if (!newValue.equals(currentValue))
            	{
            		attrs.remove(cn);
            		attrs.add(new LDAPAttribute("cn", this.cn));
            		this.orgUserDN = "cn=" + this.cn + ",cn=organizational users," + BSF.getOrganization();
            		renameEntry = true;
            	}
            }        	
            LDAPAttribute description = attrs.getAttribute("description");
            if (description != null)
            {
            	String currentValue = description.getStringValue();
            	if (currentValue == null)
            	{
            		currentValue = "";
            	}
            	String newValue = this.description;
            	if (newValue == null)
            	{
            		newValue = "";
            	}
            	if (!newValue.equals(currentValue))
            	{
            		attrs.remove(description);
            		attrs.add(new LDAPAttribute("description", this.description));
            		updateEntry = true;
            	}
            }
            String currentEnableValue = "";
            LDAPAttribute enable = attrs.getAttribute("enable");
            if (enable != null)
            {
            	currentEnableValue = enable.getStringValue();
            	if (currentEnableValue == null)
            	{
            		currentEnableValue = "";
            	}
            }
            String newEnableValue = Boolean.toString(this.enable);
            if (!newEnableValue.equalsIgnoreCase(currentEnableValue))
            {
            	if (enable != null)
            	{
            		attrs.remove(enable);
                	updateEntry = true;
            	}
            	// only add attribute if disabled
            	if (!this.enable)
            	{
            		attrs.add(new LDAPAttribute("enable", "false"));
                	updateEntry = true;
            	}
            }            
        	LDAPAttribute role = attrs.getAttribute("role");
        	if (role != null)
        	{
        		// update existing roles with any newly configured roles
            	String[] oldRoles = role.getStringValueArray();
        		ArrayList<String> newRoles = new ArrayList<String>(Arrays.asList(oldRoles));
            	boolean updateRoleAttr = false;
                for (String roleDN : roleDNs)
                {
                	if (!newRoles.contains(roleDN))
                	{
                		newRoles.add(roleDN);
                		updateRoleAttr = true;
                	}
                }
                if (updateRoleAttr)
                {
                	attrs.remove(role);
                	role = new LDAPAttribute("role");
	                for (String roleDN : newRoles)
	                {
	                	role.addValue(roleDN);
	                }
	                attrs.add(role);
	                updateEntry = true;
                }
        	}
        	else
        	{
        		// add any newly configured roles
        		if (!roleDNs.isEmpty())
        		{
            		role = new LDAPAttribute("role");
                    for (String roleDN : roleDNs)
                    {
                        role.addValue(roleDN);
                    }
                    attrs.add(role);
                    updateEntry = true;
        		}
        	}
        	if (renameEntry)
        	{
        		LDAP.renameEntry(this.orgUserDN, oldEntry, newEntry);
        	}
        	else if (updateEntry)
        	{
        		LDAP.updateEntry(oldEntry, newEntry);
       		}        		
        }
        if (oldEntry == null)
        {
            // The organizational user does not exist. So it needs to be created.
            LDAPEntry le = new LDAPEntry(this.orgUserDN);
            LDAPAttributeSet attrs = le.getAttributeSet();

            LDAPAttribute attr = new LDAPAttribute("objectclass", "top");
            attr.addValue("busorganizationaluser");
            attr.addValue("busorganizationalobject");

            attrs.add(attr);

            attrs.add(new LDAPAttribute("cn", this.cn));
            attrs.add(new LDAPAttribute("description", this.description));
            attrs.add(new LDAPAttribute("authenticationuser", this.authUserDN));
            if (!this.enable)
            {
            	 attrs.add(new LDAPAttribute("enable", "false"));
            }

            LDAPAttribute role = new LDAPAttribute("role");

        	String defaultRole = "cn=everyoneIn" + Util.getCurrentOrgName() + ",cn=organizational roles," + BSF.getOrganization();
        	if (!roleDNs.contains(defaultRole))
        	{
        		roleDNs.add(defaultRole);
        	}
            for (String roleDN : roleDNs)
            {
                role.addValue(roleDN);
            }

            le.getAttributeSet().add(role);

            LDAP.insertEntry(le);
        }
    }
    
    /**
     * Check if the user was imported from an external directory.
     * 
     * @param authUserDN
     * @return
     */
    public static boolean originatesFromExtDirectory(String authUserDN)
    {
    	boolean originatesFromExtDir = false;
    	// read the auth user
    	LDAPEntry authUserEntry = LDAP.getEntry(authUserDN);
        // check the busextension
        LDAPAttribute busExtAttribute = authUserEntry.getAttribute("busextension");
        if (busExtAttribute != null)
        {
        	String busextension = busExtAttribute.getStringValue();
        	if (Util.isSet(busextension))
        	{
        		originatesFromExtDir = (busextension.indexOf("<SynchronizedFromExtDirectory>true") > 0);
        	}
        }
    	return originatesFromExtDir;
    }
    
    /**
     * Returns the number of organizations a user is configured as organizational user.
     * 
     * @param orgUserDN
     * @return
     */
    public static int numberOrganizationsMemberOf(String orgUserDN)
    {
		int response = 0;
		int result = -1;

        String namespace = "http://schemas.cordys.com/1.0/ldap";
        String methodName = "GetOrganizationsByUser";
        
        String[] paramNames = new String[] { "dn" };
        Object[] paramValues = new Object[] { orgUserDN };
        
        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName,paramNames, paramValues);
        
        try 
        {
            response = sro.execute();
            if (response > 0)
            {
            	result = XPath.getMatchingNodes(".//tuple/old/entry/objectclass[string=\"organization\"]", new XPathMetaInfo(), response).length;
            }
        } 
        catch (Exception e)
        {
        	throw new CordysException("Not able to read number of organizations for user "+orgUserDN, e);
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
     * Check if auth user is an organization user in the given organization
     * 
     * @param authUserDN
     * @param orgDN
     * @return
     */
    public static boolean isUserInOrganization(String authUserDN, String orgDN)
    {
		int response = 0;
		boolean result = false;

        String namespace = "http://schemas.cordys.com/1.1/ldap";
        String methodName = "GetOrganizationalUsers";
        
        String[] paramNames = new String[] { "dn", "filter" };
        Object[] paramValues = new Object[] { orgDN, "*" };
        
        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName,paramNames, paramValues);
        
        try 
        {
            response = sro.execute();
            if (response > 0)
            {
            	result = (XPath.getMatchingNodes(".//tuple/old/entry/authenticationuser[string=\""+authUserDN+"\"]", new XPathMetaInfo(), response).length > 0);
            }
        }
        catch (Exception e)
        {
        	throw new CordysException("Not able to read organizational users for organization "+orgDN, e);
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
     * Delete cordys user.
     * Delete the org user and in case not a user in any other org, also deletes the auth user.
     * 
     * @param orgUserDN
     */
    public static void deleteUser(String orgUserDN)
    {
    	if (!BSF.getUser().equals(orgUserDN))
    	{
    		int orgUserNode = 0;
    		try
    		{
		    	orgUserNode = getLDAPUserEntry(orgUserDN);
		    	if (orgUserNode > 0)
		    	{
		        	int nNode = XPath.getFirstMatch("./authenticationuser/string", null, orgUserNode);
		        	String authUserDN = Node.getData(nNode);
					int numberOrganizationsMemberOf = CordysUser.numberOrganizationsMemberOf(orgUserDN);
					if (numberOrganizationsMemberOf == 0)
					{
						// only the auth user is left (no usual scenario)
						LDAP.deleteEntriesRecursive(authUserDN);
					}
					else if (numberOrganizationsMemberOf == 1)
					{
						// only 1 org, so both auth as wel as org user can be deleted
						LDAP.deleteEntriesRecursive(authUserDN, orgUserDN);
					}
					else if (numberOrganizationsMemberOf > 1)
					{
						// only remove the org user
						LDAP.deleteEntriesRecursive(orgUserDN);
					}    
		    	}
    		}
    		finally
    		{
    			if (orgUserNode > 0)
    			{
    				Node.delete(orgUserNode);
    				orgUserNode = 0;
    			}
    		}
    	}
    }
    
    /**
     * Get organizational users as arraylist<CordysUser> with basic user data.
     * 
     * @param searchString
     * @param inclAnonymous
     * @param inclRoles
     * @return
     */
    public static ArrayList<CordysUser> getOrgUsersList(String searchString, boolean inclAnonymous, boolean inclRoles)
    {
    	ArrayList<CordysUser> result = new ArrayList<CordysUser>();
    	
    	// get hashmap of <auth userdn, osidentity>
    	HashMap<String, String> osIdentities = getUsersOSIdentities();
    	int orgUsersResponse = 0;
    	try
    	{
    		// get org users from LDAP as soap response
    		orgUsersResponse = getOrganizationalUsers(searchString, inclAnonymous);
    		if (orgUsersResponse > 0)
    		{
		        int[] resultNodes = XPath.getMatchingNodes(".//tuple/old/entry", null, orgUsersResponse);
		        for (int resultNode : resultNodes) 
		        {
		        	// determine attributes and create a CordysUser object
		        	String dn = Node.getAttribute(resultNode, "dn");
		        	int nNode = XPath.getFirstMatch("./authenticationuser/string", null, resultNode);
		        	String authUserDN = Node.getData(nNode);
		        	String osIdentity = osIdentities.get(authUserDN);
		        	nNode = XPath.getFirstMatch("./description/string", null, resultNode);
		        	String fullUserName = Node.getData(nNode);		
		        	CordysUser cordysUser = new CordysUser();
		        	cordysUser.setOrgUserDN(dn);
		        	cordysUser.setAuthUserDN(authUserDN);
		        	cordysUser.setDescription(fullUserName);
		        	cordysUser.setOSIdentity(osIdentity);
		        	if (inclRoles)
		        	{
			        	// get roles
			        	LDAPEntry le = XMLToEntry.getEntry(resultNode);
		            	LDAPAttributeSet attrs = le.getAttributeSet();
		            	LDAPAttribute role = attrs.getAttribute("role");
		            	if (role != null)
		            	{
		                	String[] userRoles = role.getStringValueArray();
		            		cordysUser.roleDNs = new ArrayList<String>(Arrays.asList(userRoles));		
		            	}
		        	}
		        	result.add(cordysUser);
		        }
    		}
    	}
		finally
		{
			if (orgUsersResponse > 0)
			{
				Node.delete(orgUsersResponse);
				orgUsersResponse = 0;
			}
		}     	
    	return result;
    }
    
    /**
     * Get Cordys users for current organization as arraylist<CordysUser> with full user data.
     * 
     * @param searchString
     * @return
     */
    public static ArrayList<CordysUser> getCordysUsers(String searchString)
    {
    	ArrayList<CordysUser> result = new ArrayList<CordysUser>();
    	
    	int orgUsersResponse = 0;
    	int authUsersResponse = 0;
    	try
    	{
    		// get org users from LDAP as soap response
    		boolean inclAnonymous = false;
    		orgUsersResponse = getOrganizationalUsers(searchString, inclAnonymous);
    		// get auth users from LDAP as soap response
    		authUsersResponse = getAuthenticatedUsers(null, null);
    		if ((orgUsersResponse > 0) && (authUsersResponse > 0))
    		{
		        int[] orgUserNodes = XPath.getMatchingNodes(".//tuple/old/entry", null, orgUsersResponse);
		        for (int orgUserNode : orgUserNodes) 
		        {
		        	int nNode = XPath.getFirstMatch("./authenticationuser/string", null, orgUserNode);
		        	String authUserDN = Node.getData(nNode);
		        	int authUserNode = getAuthenticatedUserNode(authUserDN, authUsersResponse);
		        	CordysUser cordysUser = constructCordysUser(orgUserNode, authUserNode);
		        	result.add(cordysUser);
		        }
    		}
    	}
		finally
		{
			if (orgUsersResponse > 0)
			{
				Node.delete(orgUsersResponse);
				orgUsersResponse = 0;
			}
			if (authUsersResponse > 0)
			{
				Node.delete(authUsersResponse);
				authUsersResponse = 0;
			}			
		}     	
    	return result;
    }
    
    public static CordysUser getCordysUser(String orgUserDN)
    {
    	CordysUser cordysUser = null;
    	int orgUserNode = 0;
    	int authUserNode = 0;
    	try
    	{
	    	orgUserNode = getLDAPUserEntry(orgUserDN);
	    	if (orgUserNode > 0)
	    	{
	        	int nNode = XPath.getFirstMatch("./authenticationuser/string", null, orgUserNode);
	        	String authUserDN = Node.getData(nNode);
	        	authUserNode = getLDAPUserEntry(authUserDN);
	        	cordysUser = constructCordysUser(orgUserNode, authUserNode);
	    	}
    	}
    	finally
    	{
    		if (orgUserNode > 0)
    		{
    			Node.delete(orgUserNode);
    			orgUserNode = 0;
    		}
    		if (authUserNode > 0)
    		{
    			Node.delete(authUserNode);
    			authUserNode = 0;
    		}    		
    	}
    	return cordysUser;
    }
    
    private static CordysUser constructCordysUser(int orgUserNode, int authUserNode)
    {
    	// determine attributes and create a CordysUser object
    	int nNode = XPath.getFirstMatch("./authenticationuser/string", null, orgUserNode);
    	String authUserDN = Node.getData(nNode);    	
    	String dn = Node.getAttribute(orgUserNode, "dn");
    	nNode = XPath.getFirstMatch("./cn/string", null, orgUserNode);
    	String cn = Node.getData(nNode);		        	
    	//String osIdentity = osIdentities.get(authUserDN);
    	nNode = XPath.getFirstMatch("./description/string", null, orgUserNode);
    	String fullUserName = Node.getData(nNode);	
    	nNode = XPath.getFirstMatch("./defaultcontext/string", null, authUserNode);
    	String defaultcontext = Node.getData(nNode);	
    	nNode = XPath.getFirstMatch("./authenticationtype/string", null, authUserNode);
    	String authenticationType = null;
    	if (nNode > 0)
    	{
    		authenticationType = Node.getData(nNode);	
    	}
    	else
    	{
    		authenticationType = AT_CUSTOM;
    	}
    	String osIdentity = null;
    	if (AT_CERTIFIED.equals(authenticationType))
    	{
    		osIdentity = "Certificate Based";
    	}
    	else
    	{
    		nNode = XPath.getFirstMatch("./osidentity/string", null, authUserNode);
    		osIdentity = Node.getData(nNode);	
    	}
    	nNode = XPath.getFirstMatch("./userPassword/string", null, authUserNode);
    	String userPassword = Node.getData(nNode);
    	nNode = XPath.getFirstMatch("./enable/string", null, orgUserNode);
    	boolean enable = true;
    	if (nNode > 0)
    	{
    		enable = Boolean.valueOf(Node.getData(nNode));
    	}
    	String company = null;
    	nNode = XPath.getFirstMatch("./o/string", null, authUserNode);
    	if (nNode > 0)
    	{
    		company = Node.getData(nNode);	
    	}
    	String registeredAddress = null;
    	nNode = XPath.getFirstMatch("./registeredAddress/string", null, authUserNode);
    	if (nNode > 0)
    	{
    		registeredAddress = Node.getData(nNode);	
    	}	
    	String telephoneNumber = null;
    	nNode = XPath.getFirstMatch("./telephoneNumber/string[1]", null, authUserNode);
    	if (nNode > 0)
    	{
    		telephoneNumber = Node.getData(nNode);	
    	}	
    	String telephoneNumber2 = null;
    	nNode = XPath.getFirstMatch("./telephoneNumber/string[2]", null, authUserNode);
    	if (nNode > 0)
    	{
    		telephoneNumber2 = Node.getData(nNode);	
    	}
    	String facsimileTelephoneNumber = null;
    	nNode = XPath.getFirstMatch("./facsimileTelephoneNumber/string", null, authUserNode);
    	if (nNode > 0)
    	{
    		facsimileTelephoneNumber = Node.getData(nNode);	
    	}	
    	String mail = null;
    	nNode = XPath.getFirstMatch("./mail/string", null, authUserNode);
    	if (nNode > 0)
    	{
    		mail = Node.getData(nNode);	
    	}	
    	String labeleduri = null;
    	nNode = XPath.getFirstMatch("./labeleduri/string", null, authUserNode);
    	if (nNode > 0)
    	{
    		labeleduri = Node.getData(nNode);	
    	}			        	
    	// construct cordysUser
    	CordysUser cordysUser = new CordysUser();
    	cordysUser.setOrgUserDN(dn);
    	cordysUser.setAuthUserDN(authUserDN);
    	cordysUser.setCN(cn);
    	cordysUser.setDescription(fullUserName);
    	cordysUser.setDefaultcontext(defaultcontext);
    	cordysUser.setOSIdentity(osIdentity);
    	cordysUser.setAuthenticationType(authenticationType);
    	cordysUser.setUserPassword(userPassword);
    	cordysUser.setEnable(enable);
    	cordysUser.setCompany(company);
    	cordysUser.setRegisteredAddress(registeredAddress);
    	cordysUser.setTelephoneNumber(telephoneNumber);
    	cordysUser.setTelephoneNumber2(telephoneNumber2);
    	cordysUser.setFacsimileTelephoneNumber(facsimileTelephoneNumber);
    	cordysUser.setEmail(mail);
    	cordysUser.setLabeleduri(labeleduri);   
       	// get roles
       	LDAPEntry le = XMLToEntry.getEntry(orgUserNode);
       	LDAPAttributeSet attrs = le.getAttributeSet();
       	LDAPAttribute role = attrs.getAttribute("role");
       	if (role != null)
       	{
           	String[] userRoles = role.getStringValueArray();
       		cordysUser.roleDNs = new ArrayList<String>(Arrays.asList(userRoles));		
       	}	
    	return cordysUser;
    }
    
    private static int getAuthenticatedUserNode(String authUserDN, int authUsersResponse)
    {
    	return (XPath.getFirstMatch(".//tuple/old/entry[@dn=\"" + authUserDN + "\"]", null, authUsersResponse));
    }
    
    /**
     * Get organizational user names by DN
     * 
     * @return
     */
    public static HashMap<String, String> getOrgUserNamesByDN(boolean inclOSIdentity)
    {
    	HashMap<String, String> result = new HashMap<String, String>();
    	
    	ArrayList<CordysUser> cordysUsers = getOrgUsersList(null, true, false);
		for (CordysUser cordysUser : cordysUsers)
		{
			String orgUserDN = cordysUser.getOrgUserDN();
			String description = cordysUser.getDescription();
			if (inclOSIdentity && (!"anonymous".equals(description)))
			{
				description = description + "(" + cordysUser.getOSIdentity() + ")";
			}
			result.put(orgUserDN, description);
		}
    	return result;
    }
    
    /**
     * Get org users from LDAP as soap response
     * Excludes system defined users (SYSTEM, wcpLicUser); optionally anonymous user is included
     * 
     * @param searchString
     * @param inclAnonymous
     * @return
     */
    public static int getOrganizationalUsers(String searchString, boolean inclAnonymous)
    {
    	int ldapResponse = 0;
    	try
    	{
    		String dn = "cn=organizational users," + BSF.getOrganization();
    		String filter = "";
    		if (searchString == null)
    		{
    			searchString = "";
    		}
   			filter = "&(objectclass=busorganizationaluser)(&(!(cn=SYSTEM))";
   			if (!inclAnonymous)
   			{
   				filter = filter + "(!(cn=anonymous))";
   			}
   			filter = filter + "(!(cn=wcpLicUser)))(|(description="+searchString+"*)(description=*"+searchString+")(description=*"+searchString+"*)(&(!(description=*))(|(cn=*"+searchString+")(cn=*"+searchString+"*)(cn=*"+searchString+"*))))";
   			String sort = "ascending";
   			ldapResponse = LDAP.searchLDAP(dn, 2, filter, sort, false);
    	}
        catch (Exception e)
        {
        	throw new CordysException("Not able to read organizational users from LDAP.", e);
        }
    	return ldapResponse;
    }
    
    /**
     * Get LDAP entry for user (organizational or authenticated user)
     * 
     * @param userDN
     * @return
     */
    public static int getLDAPUserEntry(String userDN)
    {
    	int result = 0;
    	int dummy = 0;
    	try
    	{
    		dummy = BSF.getXMLDocument().parseString("<OrganizationalUser/>");
    		LDAPEntry le = LDAP.getEntry(userDN);
    		if (le != null)
    		{
    			EntryToXML.appendToChildren(le, dummy);
    			result = Node.unlink(Node.getFirstChild(dummy));
    		}
    	}
        catch (Exception e)
        {
        	throw new CordysException("Not able to read user from LDAP, user " + Util.getNameFromDN(userDN), e);
        }
    	finally
    	{
    		if (dummy > 0)
    		{
    			Node.delete(dummy);
    		}
    	}
    	return result;
    }
    
    /**
     * Get all authenticated users, returning as a soap response.
     * 
     * @param filter
     * @param sort
     * @return
     */
    public static int getAuthenticatedUsers(String filter, String sort)
    {	
		// determine the LDAP root
		String ldapRoot = LDAP.getRoot();
		if (!Util.isSet(filter))
		{
			filter = "*";
		}
		if (!Util.isSet(sort))
		{
			sort = "asc";
		}
		
        String namespace = "http://schemas.cordys.com/1.1/ldap";
        String methodName = "GetAuthenticatedUsers";
        
        String[] paramNames = new String[] { "dn", "filter", "sort" };
        Object[] paramValues = new Object[] { ldapRoot, filter, sort };
        
        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, paramNames, paramValues);
        
        int response = 0;
        try 
        {
            response = sro.execute();    	
        }
        catch (Exception e)
        {
        	throw new CordysException("Not able to read auth users from LDAP.", e);
        }
        return response;
    }
    
    /**
     * Get auth users and there os identity by a hashmap of <auth userdn, osidentity>
     * 
     * @return
     */
    public static HashMap<String, String> getUsersOSIdentities()
    {
    	// define hashmap of <auth userdn, osidentity>
    	HashMap<String, String> result = new HashMap<String, String>();
    	
    	int authUsersResponse = 0;
    	try
    	{
    		// get auth users as a soap response
    		authUsersResponse = getAuthenticatedUsers(null, null);
			if (authUsersResponse != 0)
			{
				String exclUsers = "-anonymous-SYSTEM-wcpLicUser-Replica Manager-";
		        int[] resultNodes = XPath.getMatchingNodes(".//tuple/old/entry", null, authUsersResponse);
		        for (int resultNode : resultNodes) 
		        {
		        	String dn = Node.getAttribute(resultNode, "dn");
		        	int nNode = XPath.getFirstMatch("./authenticationtype/string", null, resultNode);
		        	String authenticationtype = null;
		        	if (nNode > 0)
		        	{
		        		authenticationtype = Node.getData(nNode);
		        	}
		        	else
		        	{
		        		authenticationtype = AT_CUSTOM;
		        	}
		        	if (AT_CERTIFIED.equals(authenticationtype))
		        	{
		        		result.put(dn, "Certificate Based");
		        	}
		        	else
		        	{
			        	// get the os identity and exclude the system defined users
			        	nNode = XPath.getFirstMatch("./osidentity/string", null, resultNode);
			        	String osidentity = Node.getData(nNode);
			        	if (exclUsers.indexOf(osidentity) == -1)
			        	{
			        		result.put(dn, osidentity);
			        	}
		        	}
		        }	
			}         	
    	}
    	finally
    	{
    		if (authUsersResponse > 0)
    		{
    			Node.delete(authUsersResponse);
    			authUsersResponse = 0;
    		}
    	}
    	return result;
    }
    
    /**
     * Check if given user is an existing organizational user
     * 
     * @param orgUserDN
     * @return
     */
    public static boolean organizationalUserExists(String orgUserDN)
    {
   		LDAPEntry le = LDAP.getEntry(orgUserDN);
    	return (le != null);
    }
    
    /**
     * Get number of org users who do have 1 or more of the given roles attached
     * 
     * @param orgDN
     * @param roleDNs
     * @return
     */
    public static int getNumberOfUsersWithRoles(String orgDN, ArrayList<String> roleDNs)
    {
    	int result = 0;
    	if (roleDNs.size() > 0)
    	{
        	int ldapResponse = 0;
	    	try
	    	{
	    		String dn = "cn=organizational users," + BSF.getOrganization();
	    		String filter = "";
	   			filter = "&(objectclass=busorganizationaluser)(&(!(cn=SYSTEM)))(|";
	   			for (String roleDN : roleDNs)
	   			{
	   				filter = filter + "(role=" + roleDN + ")";
	   			}
	   			filter = filter + ")";
	   			String sort = "ascending";
	   			ldapResponse = LDAP.searchLDAP(dn, 2, filter, sort, false);
	   			if (ldapResponse > 0)
	   			{
	   				String exp = "count(.//tuple/old/entry)";
	   				double re = XPath.evaluateExpression(exp, null, ldapResponse).getNumberResult();
	   		    	result =  (int)re;
	   			}
	    	}
	        catch (Exception e)
	        {
	        	throw new CordysException("Not able to count organizational users from LDAP.", e);
	        } 
	    	finally
	    	{
	    		if (ldapResponse > 0)
	    		{
	    			Node.delete(ldapResponse);
	    		}
	    	}
    	}
    	return result;
    }
    
    /**
     * Get Cordys user for id (osidentity)
     * 
     * @param osidentity
     * @return
     */
    public static CordysUser getCordysUserForID(String osidentity)
    {
    	CordysUser result = null;
    	int authUserResponse = 0;
    	int orgUserResponse = 0;
    	try
    	{
    		String authUserDN = null;
    		String dn = "cn=authenticated users," + LDAP.getRoot();
    		String filter = "&(objectclass=busauthenticationuser)(osidentity="+osidentity+")";
   			String sort = "";
   			authUserResponse = LDAP.searchLDAP(dn, 1, filter, sort, true);
   			int authUserNode = 0;
   			if (authUserResponse > 0)
   			{
   				authUserNode = XPath.getFirstMatch(".//tuple/old/entry", null, authUserResponse);
   				if (authUserNode > 0)
   				{
   					authUserDN = Node.getAttribute(authUserNode, "dn");
   				}
   			}
   			if (Util.isSet(authUserDN))
   			{
   				dn = "cn=organizational users," + BSF.getOrganization();
   	    		filter = "&(objectclass=busorganizationaluser)(authenticationuser=" + authUserDN + ")";
   	   			sort = "";
   	   			orgUserResponse = LDAP.searchLDAP(dn, 1, filter, sort, true);
   	   			if (orgUserResponse > 0)
   	   			{
   	   				int orgUserNode =  XPath.getFirstMatch(".//tuple/old/entry", null, orgUserResponse);
   	   				result = constructCordysUser(orgUserNode, authUserNode);
   	   			}
   			}
    	}
        catch (Exception e)
        {
        	throw new CordysException("Not able to read user for ID from LDAP (ID " + osidentity + ")", e);
        } 
    	finally
    	{
    		if (authUserResponse > 0)
    		{
    			Node.delete(authUserResponse);
    		}
    		if (orgUserResponse > 0)
    		{
    			Node.delete(orgUserResponse);
    		}    		
    	}
    	return result;
    }
    
    /**
     * Add given role to the user
     * 
     * @param orgUserDN
     * @param roleDN
     */
    public static void addRole(String orgUserDN, String roleDN)
    {
    	ArrayList<String> addedRoleDNs = new ArrayList<String>();
    	addedRoleDNs.add(roleDN);
    	Role.maintainUserRoles(orgUserDN, addedRoleDNs, null);  	
    }
    
    /**
     * Remove given role from the user
     * 
     * @param orgUserDN
     * @param roleDN
     */
    public static void removeRole(String orgUserDN, String roleDN)
    {
    	ArrayList<String> removedRoleDNs = new ArrayList<String>();
    	removedRoleDNs.add(roleDN);
    	Role.maintainUserRoles(orgUserDN, null, removedRoleDNs);  	
    }
       
    /**
     * Check if given user is an organizational admin.
     * 
     * @param orgUserDN
     * @return
     */
    public static boolean userIsOrgAdmin(String orgUserDN)
    {
    	boolean result = false;
    	int rolesNode = 0;
    	try
    	{
    		// check for 2 levels, so up to level "1" (0, 1)
    		rolesNode = Role.getRoleTree(orgUserDN, "1");
    		if (rolesNode > 0)
    		{
    			String ldapRoot = LDAP.getRoot();
    			String rolesString = Node.writeToString(rolesNode, false);
    			result = ((rolesString.indexOf("cn=Administrator,cn=Cordys@Work," + ldapRoot) != -1) ||
    					  (rolesString.indexOf("cn=organizationalAdmin,cn=Cordys ESBServer," + ldapRoot) != -1));
    		}
    	}
    	finally
    	{
    		if (rolesNode > 0)
    		{
    			Node.delete(rolesNode);
    		}
    	}
    	return result;
    }
    
    /**
     * Check if given user has a certain role
     * 
     * @param orgUserDN
     * @param role				either full DN or rolename,package (eg cn=Administrator,cn=Cordys@Work)
     * @param levels			number of levels to check in the user role tree
     * @return
     */
    public static boolean userHasRole(String orgUserDN, String role, int levels)
    {
    	boolean result = false;
    	if (Util.isSet(orgUserDN) && (Util.isSet(role)))
    	{
	    	if (levels < 1)
	    	{
	    		levels = 1;
	    	}
	    	int rolesNode = 0;
	    	try
	    	{
	    		rolesNode = Role.getRoleTree(orgUserDN, String.valueOf(levels-1));
	    		if (rolesNode > 0)
	    		{
	    			int commaOcc = (role.length() - role.replace(",", "").length());
	    			// if one comma in role, add the ldap root or organization	    			
	    			if (commaOcc == 1)
	    			{
	    				if (role.indexOf("organizational roles") != -1)
	    				{
	    					role = role + "," + BSF.getOrganization();
	    				}
	    				else
	    				{
	    					role = role + "," + LDAP.getRoot();
	    				}
	    			}
	    			else if (commaOcc == 0)
	    			{
	    				if (role.indexOf("cn=") == -1)
	    				{
	    					role = "cn=" + role;
	    				}
	    				role = role + ",cn=organizational roles," + BSF.getOrganization();
	    			}
	    			String rolesString = Node.writeToString(rolesNode, false);
	    			result = ((rolesString.indexOf(role) != -1));
	    		}
	    	}
	    	finally
	    	{
	    		if (rolesNode > 0)
	    		{
	    			Node.delete(rolesNode);
	    		}
	    	}
    	}
    	return result;
    }   
    
    /**
     * Set the user password (as per webservice operation setPasswordForUser, from Cordys 4.3 onwards)
     * 
     * @param authUserName		(can be different from org user name!)
     * @param newPassword
     * @return
     */
    public static String maintainPasswordForUser(String authUserName, String newPassword)
    {
    	String hashedPW = null;
    	String ldapRoot = LDAP.getRoot();
    	
        String namespace = "http://schemas.cordys.com/user/password/1.0";
        String methodName = "SetPasswordForUser";
        
        String[] paramNames = new String[] { "Username", "NewPassword" };
        Object[] paramValues = new Object[] { authUserName, newPassword };
        
        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, paramNames, paramValues);
        
        int response = 0;
        int authUserNode = 0;
        try 
        {
            response = sro.execute();   
            String authUserDN = "cn=" + authUserName + ",cn=authenticated users," + ldapRoot;
	    	authUserNode = getLDAPUserEntry(authUserDN);
	    	if (authUserNode > 0)
	    	{
	        	int nNode = XPath.getFirstMatch("./userPassword/string", null, authUserNode);
	        	if (nNode > 0)
	        	{
	        		hashedPW = Node.getData(nNode);  
	        	}	
	    	}	
        }
        catch (Exception e)
        {
        	throw new CordysException("Not able to set or readback the password for user " + authUserName, e);
        }
		finally
		{
			if (response > 0)
			{
				Node.delete(response);
				response = 0;
			}
			if (authUserNode > 0)
			{
				Node.delete(authUserNode);
				authUserNode = 0;
			}			
		} 
        return hashedPW;
    }
}