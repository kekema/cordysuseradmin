package com.cordys.coe.tools.useradmin.cordys;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import com.cordys.coe.tools.useradmin.cordys.exception.CordysException;
import com.cordys.coe.tools.useradmin.cordys.exception.CordysValidationException;
import com.cordys.coe.tools.useradmin.util.Util;
import com.cordys.cpc.bsf.busobject.BSF;
import com.cordys.cpc.bsf.soap.SOAPRequestObject;
import com.eibus.directory.soap.LDAPUtil;
import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;
import com.eibus.xml.nom.XMLException;
import com.eibus.xml.xpath.XPath;
import com.eibus.xml.xpath.XPathMetaInfo;
import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPEntry;

/**
 * Class to support role related functionality in the Cordys platform.
 * Getting/updating user roles, getting all defined roles, etc.
 * 
 * @author kekema
 *
 */
public class Role
{
	private String roleDN;
	
	public Role(String roleDN)
	{
		this.roleDN = roleDN;
	}
	
	public String getRoleDN()
	{
		return this.roleDN;
	}
	
	/**
	 * Get list of user assigned roles (sorted)
	 * 
	 * @param orgUserDN
	 * @return
	 */
    public static ArrayList<String> getAssignedRoles(String orgUserDN)
    {
    	ArrayList<String> result = new ArrayList<String>();
    	try
    	{
	    	LDAPEntry oldEntry = LDAP.getEntry(orgUserDN);
	        if (oldEntry != null)
	        {
	        	LDAPAttributeSet attrs = oldEntry.getAttributeSet();
	        	LDAPAttribute role = attrs.getAttribute("role");
	        	if (role != null)
	        	{
	        		String[] oldRolesArray = role.getStringValueArray();
	        		result = new ArrayList<String>(Arrays.asList(oldRolesArray));
	        		Collections.sort(result);
	        	}
	        }
    	}
    	catch (Exception e)
    	{
    		throw new CordysException("Not able to read role assignments for " + orgUserDN, e);
    	}
        return result;
    }
    
	/**
	 * Get list of roles which are not assigned to the user (sorted)
	 * 
	 * @param orgUserDN
	 * @return
	 */    
    public static ArrayList<String> getUnassignedRoles(String orgUserDN)
    {
    	ArrayList<String> unassignedRoles = new ArrayList<String>();
		ArrayList<String> userRoles = null;
		
		String ldapRoot = LDAP.getRoot();    	
		try 
		{
			userRoles = getAssignedRoles(orgUserDN);

			// get all roles as defined in LDAP (excl internal roles)
			LDAPEntry[] ldapresult = LDAP.searchLDAPEntries(ldapRoot, 2, "(&(objectclass=busorganizationalrole)(|(busorganizationalroletype=Application)(busorganizationalroletype=Functional)))", null, false);
			for (LDAPEntry ldapEntry:ldapresult)
			{
				String dn = ldapEntry.getDN();
				boolean include = true;
				// if organizational role, only include if defined in current organization
				if (dn.indexOf("organizational roles") > 0)
				{
					include = (dn.indexOf(BSF.getOrganization()) > 0);
				}
				if (include)
				{
					int pIndex = dn.indexOf("cn=packages,o=");
					if (pIndex > 0)
					{
						// role deployed in an organizational space
						include = (dn.indexOf(BSF.getOrganization()) > 0);
						dn = dn.substring(0, pIndex) + ldapRoot;
					}
				}
				if (include)
				{
					// only include if not assigned yet to user
					include = ((userRoles != null) && !(userRoles.contains(dn)));
				}				
				if (include)
				{
					if (!unassignedRoles.contains(dn))
					{
						unassignedRoles.add(dn);
					}
				}
			}
			Collections.sort(unassignedRoles);			
		} 
		catch (Exception e)
		{
			throw new CordysException("Not able to determine unassigned roles for " + orgUserDN, e);
		}
		return unassignedRoles;
    }
    
    /**
     * Get all user assigned and unassigned roles, where for each role, the whole tree is given, so including
     * the subroles, it's subroles, etc.
     * The result comes as a NOM xml.
     * 
     * @param orgUserDN
     * @return
     */
    public static int getUserRoleTrees(String orgUserDN)
    {
    	Document xmlDoc = BSF.getXMLDocument();
    	int treeNode = xmlDoc.createElement("tree");
    	int assignedNode = xmlDoc.createElement("assigned", treeNode);
    	int userNode = getAssignedRolesTree(orgUserDN);
    	Node.appendToChildren(userNode, assignedNode);
    	int unassignedNode = xmlDoc.createElement("unassigned", treeNode);
    	int rolesNode = getUnassignedRolesTree(orgUserDN);
    	Node.appendToChildren(rolesNode, unassignedNode);
    	return treeNode;
    }
    
    private static int getAssignedRolesTree(String orgUserDN)
    {
    	int result = 0;
    	
    	int response = 0;
    	try
    	{
    		response = getRoleTree(orgUserDN, null);
    		if (response > 0)
    		{
				result = XPath.getFirstMatch(".//user", new XPathMetaInfo(), response);
				if (result > 0)
				{
					result = Node.unlink(result);
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
    
    private static int getUnassignedRolesTree(String orgUserDN)
    {
		int result = 0;
   	
		try 
		{
			result = BSF.getXMLDocument().parseString("<roles/>");
			// get all unassigned roles; 
			// per role, get the role tree 
	        ArrayList<String> unassignedRoles = getUnassignedRoles(orgUserDN);
			for (String roleDN : unassignedRoles)
			{
				int response = 0;
				try
				{
					response = getRoleTree(roleDN, null);
					if (response > 0)
					{
						int roleNode = XPath.getFirstMatch(".//role", new XPathMetaInfo(), response);
						if (roleNode > 0)
						{
							roleNode = Node.unlink(roleNode);
							Node.appendToChildren(roleNode, result);
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
			}			
		} 
		catch (Exception e)
		{
			throw new CordysException("Not able to compose unassigned roles tree for user " + orgUserDN, e);
		}
		return result;    	
    }
    
    /**
     * Get roles tree for either a user (assigned role tree) or a role (role tree)
     * 
     * @param dn		DN of a user or a role
     * @param depth
     * @return
     */
    public static int getRoleTree(String dn, String depth)
    {
    	if (!Util.isSet(depth))
    	{
    		depth = "10";
    	}
        String namespace = "http://schemas.cordys.com/1.0/ldap";
        String methodName = "GetRoles";
        
        String[] paramNames = new String[] { "dn", "depth" };
        Object[] paramValues = new Object[] { dn, depth };
        
        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, paramNames, paramValues);
        
        int response = 0;
        try 
        {
            response = sro.execute();
        }
        catch (Exception e)
        {
        	throw new CordysException("Not able to read roles from LDAP for " + dn, e);
        }
        return response;
    }
    
    /**
     * Update the user roles as per the given list.
     * The update is only executed when there is a change compared to the current roles in LDAP.
     * 
     * @param orgUserDN
     * @param assignedRoleDNs
     */
    public static void updateAssignedRoles(String orgUserDN, ArrayList<String> assignedRoleDNs)
    {
        Collections.sort(assignedRoleDNs);
        
		try 
		{
			// real user from LDAP and get the role attributes
			LDAPEntry oldEntry = LDAP.getEntry(orgUserDN);
			boolean updateUser = false;
            if (oldEntry != null)
            {
            	LDAPEntry newEntry = LDAPUtil.cloneEntry(oldEntry);
	        	LDAPAttributeSet attrs = newEntry.getAttributeSet();
	        	LDAPAttribute role = attrs.getAttribute("role");
	        	ArrayList<String> oldRoles = null;
	        	if (role != null)
	        	{
	        		String[] oldRolesArray = role.getStringValueArray();
            		oldRoles = new ArrayList<String>(Arrays.asList(oldRolesArray));
            		Collections.sort(oldRoles);
            		// check if user roles has changed
            		if (assignedRoleDNs.size() == oldRoles.size())
            		{
            			for (String roleDN : assignedRoleDNs)
            			{
            				if (!oldRoles.contains(roleDN))
            				{
            					updateUser = true;
            					break;
            				}
            			}
            		}
            		else
            		{
            			updateUser = true;
            		}
            		if (updateUser)
            		{
            			attrs.remove(role);
            		}
	        	}
	        	else
	        	{
	        		updateUser = (assignedRoleDNs.size() > 0);
	        	}
	        	if (updateUser)
	        	{
	        		String validationMessage = validateUpdate(oldRoles, assignedRoleDNs);
	        		if (!Util.isSet(validationMessage))
	        		{
		        		if (assignedRoleDNs.size() > 0)
		        		{
			            	role = new LDAPAttribute("role");
			                for (String roleDN : assignedRoleDNs)
			                {
			                	role.addValue(roleDN);
			                }
			                attrs.add(role);
		        		}
		                LDAP.updateEntry(oldEntry, newEntry);
	        		}
	        		else
	        		{
	        			throw new CordysValidationException(validationMessage);
	        		}
	        	}
            }
		} 
		catch (CordysValidationException e)
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new CordysException("Not able to update roles for user " + orgUserDN, e);
		}
    }
    
    /**
     * Check for any validation errors on roles update
     * 
     * @param oldRoles
     * @param assignedRoles
     * @return
     */
    private static String validateUpdate(ArrayList<String> oldRoles, ArrayList<String> assignedRoles) 
    {
    	String validationMessage = null;
    	// check for condition that at least one user has administrator role
    	if ((oldRoles != null) && (oldRoles.size() > 0))
    	{
    		String ldaproot = LDAP.getRoot();
    		String adminRole = "cn=Administrator,cn=Cordys@Work," + ldaproot;
    		String orgAdminRole = "cn=organizationalAdmin,cn=Cordys ESBServer," + ldaproot;
    		if ((oldRoles.contains(adminRole) && (!assignedRoles.contains(adminRole)  && (!assignedRoles.contains(orgAdminRole)))) ||
    			(oldRoles.contains(orgAdminRole) && (!assignedRoles.contains(adminRole)  && (!assignedRoles.contains(orgAdminRole)))))
    		{
        		if (!Organization.hasMultipleAdmins(BSF.getOrganization()))		
        		{
        			validationMessage = "Atleast one user must have organizationalAdmin role or Administrator role.";
        		}
    		}
    	}
    	return validationMessage;
    }
    
    /**
     * Assign additional roles and/or unassign user roles
     * 
     * @param orgUserDN
     * @param addedRoleDNs			assign these roles in addition to existing roles
     * @param removedRoleDNs		unassign these roles from existing roles
     */
    public static void maintainUserRoles(String orgUserDN, ArrayList<String> addedRoleDNs, ArrayList<String> removedRoleDNs)
    {
		try 
		{
			LDAPEntry oldEntry = LDAP.getEntry(orgUserDN);
            if (oldEntry != null)
            {
            	ArrayList<String> roleDNs = null;
            	LDAPEntry newEntry = LDAPUtil.cloneEntry(oldEntry);
	        	LDAPAttributeSet attrs = newEntry.getAttributeSet();
	        	LDAPAttribute role = attrs.getAttribute("role");
	        	if (role != null)
	        	{
	        		String[] currentRolesArray = role.getStringValueArray();
	        		roleDNs = new ArrayList<String>(Arrays.asList(currentRolesArray));
           			attrs.remove(role);
	        	}
	        	else
	        	{
	        		roleDNs = new ArrayList<String>();
	        	}
	        	ArrayList<String> oldRoleDNs = new ArrayList<String>(roleDNs);
	        	if ((removedRoleDNs != null) && (removedRoleDNs.size() > 0))
	        	{
	        		roleDNs.removeAll(removedRoleDNs);
	        	}
	        	if ((addedRoleDNs != null) && (addedRoleDNs.size() > 0))
	        	{
	        		// remove duplicates
	        		roleDNs.removeAll(addedRoleDNs);
	        		// add new roles
	        		roleDNs.addAll(addedRoleDNs);
	        	}
    			Collections.sort(roleDNs);
    			if (!roleDNs.equals(oldRoleDNs))
    			{
	        		String validationMessage = validateUpdate(oldRoleDNs, roleDNs);
	        		if (!Util.isSet(validationMessage))
	        		{
		        		if (roleDNs.size() > 0)
		        		{
			            	role = new LDAPAttribute("role");
			                for (String roleDN : roleDNs)
			                {
			                	role.addValue(roleDN);
			                }
			                attrs.add(role);
		        		}
		                LDAP.updateEntry(oldEntry, newEntry);
	        		}
	        		else
	        		{
	        			throw new CordysValidationException(validationMessage);
	        		}
    			}
            }
		} 
		catch (CordysValidationException e)
		{
			throw e;
		}		
		catch (Exception e) 
		{
			throw new CordysException("Not able to update roles for user " + orgUserDN, e);
		}
    }
    
    /**
     * Get all isv roles plus roles of current organization.
     * 
     * @return
     */
    public static ArrayList<String> getAllRoles(boolean inclInternal)
    {				
		return (getRoles(inclInternal, false));		
    }
    
    /**
     * Get org roles of current organization.
     * 
     * @return
     */
    public static ArrayList<String> getOrganizationalRoles()
    {				
		return (getRoles(false, true));		
    }
    
    /**
     * Get isv and/or organizational roles
     * 
     * @return
     */
    private static ArrayList<String> getRoles(boolean inclInternal, boolean onlyOrganizational)
    {
    	ArrayList<String> result = new ArrayList<String>();
		String ldapRoot = LDAP.getRoot();    	
		try 
		{
			String filter = "(&(objectclass=busorganizationalrole)(|(busorganizationalroletype=Application)(busorganizationalroletype=Functional)";
			if (inclInternal)
			{
				filter = filter + "(busorganizationalroletype=Internal)";
			}
			filter = filter + "))";
			LDAPEntry[] ldapresult = LDAP.searchLDAPEntries(ldapRoot, 2, filter, null, false);
			for (LDAPEntry ldapEntry:ldapresult)
			{
				String dn = ldapEntry.getDN();
				// String description = ldapEntry.getAttribute("description").getStringValue();	// not used for now; role description rarely different from role name
				boolean include = false;
				if (dn.indexOf("organizational roles") > 0)
				{
					include = (dn.indexOf(BSF.getOrganization()) > 0);
				} 
				else
				{
					include = (!onlyOrganizational);
				}
				if (!onlyOrganizational)
				{
					int pIndex = dn.indexOf("cn=packages,o=");
					if (pIndex > 0)
					{
						// role deployed in an organizational space
						include = (dn.indexOf(BSF.getOrganization()) > 0);
						dn = dn.substring(0, pIndex) + ldapRoot;
					}
				}
				if (include)
				{
					// in case the role was also deployed in shared space, the result array might already contain the roleDN
					if (!result.contains(dn))
					{	
						result.add(dn);
					}
				}
			}
			Collections.sort(result, String.CASE_INSENSITIVE_ORDER);			
		} 
		catch (Exception e)
		{
			throw new CordysException("Not able to read all roles", e);
		}				
		return result;    	
    }
    
    /**
     * Gives a HashMap of <role name, role DN>
     * @return
     */
    public static HashMap<String, String> getAllRolesByName()
    {
    	HashMap<String, String> result = new HashMap<String, String>();
    	ArrayList<String> allRoleDNs = getAllRoles(false);
    	for (String roleDN : allRoleDNs)
    	{
    		String roleName = Util.getNameFromDN(roleDN);
    		result.put(roleName, roleDN);
    	}
    	return result;
    }
    
    /**
     * Get (first level) subroles for a role
     * 
     * @param roleDN
     * @return
     */
    public static ArrayList<String> getSubroles(String roleDN)
    {
    	ArrayList<String> result = new ArrayList<String>();
    	try
    	{
    		LDAPEntry le = getLDAPRoleEntry(roleDN);
	    	LDAPAttributeSet attrs = le.getAttributeSet();
	    	LDAPAttribute role = attrs.getAttribute("role");
	    	if (role != null)
	    	{
	        	String[] roleRoles = role.getStringValueArray();
	    		result = new ArrayList<String>(Arrays.asList(roleRoles));		
	    	}
		}
	    catch (Exception e)
	    {
	    	throw new CordysException("Not able to read role from LDAP.", e);
	    }    	
    	Collections.sort(result, String.CASE_INSENSITIVE_ORDER);
    	return result;
    }
    
    /**
     * Get role entry from LDAP
     * @param roleDN
     * @return
     */
    public static LDAPEntry getLDAPRoleEntry(String roleDN)
    {
    	LDAPEntry result = null;
		if (roleDN.indexOf("cn=organizational roles") == -1)
		{
    		// try role in org space first
			String ldapRoot = LDAP.getRoot();
			int lrIndex = roleDN.indexOf(ldapRoot);
    		String orgSpaceRoleDN = roleDN.substring(0, lrIndex) + "cn=packages," + BSF.getOrganization();
	    	result = LDAP.getEntry(orgSpaceRoleDN);
		}
    	if (result == null)
    	{
    		result = LDAP.getEntry(roleDN);
    	}		
		return result;
    }
    	
    /**
     * Assign additional subroles and/or unassign subroles.
     * Checks for circular assignments to be done in calling method.
     * 
     * @param roleDN
     * @param addedSubroleDNs			assign these subroles in addition to existing roles
     * @param removedSubroleDNs			unassign these subroles from existing roles
     */
    public static void maintainSubroles(String mainRoleDN, ArrayList<String> addedSubroleDNs, ArrayList<String> removedSubroleDNs)
    {
		// only organizational roles can be updated
		if (mainRoleDN.indexOf("cn=organizational roles") != -1)
		{
			try 
			{
				LDAPEntry oldEntry = LDAP.getEntry(mainRoleDN);
	            if (oldEntry != null)
	            {
	            	ArrayList<String> roleDNs = null;
	            	LDAPEntry newEntry = LDAPUtil.cloneEntry(oldEntry);
		        	LDAPAttributeSet attrs = newEntry.getAttributeSet();
		        	LDAPAttribute role = attrs.getAttribute("role");
		        	if (role != null)
		        	{
		        		String[] currentRolesArray = role.getStringValueArray();
		        		roleDNs = new ArrayList<String>(Arrays.asList(currentRolesArray));
	           			attrs.remove(role);
		        	}
		        	else
		        	{
		        		roleDNs = new ArrayList<String>();
		        	}
		        	if ((removedSubroleDNs != null) && (removedSubroleDNs.size() > 0))
		        	{
		        		roleDNs.removeAll(removedSubroleDNs);
		        	}
		        	if ((addedSubroleDNs != null) && (addedSubroleDNs.size() > 0))
		        	{
		        		// remove duplicates
		        		roleDNs.removeAll(addedSubroleDNs);
		        		// add new roles
		        		roleDNs.addAll(addedSubroleDNs);
		        	}
	        		if (roleDNs.size() > 0)
	        		{
	        			Collections.sort(roleDNs);
		            	role = new LDAPAttribute("role");
		                for (String roleDN : roleDNs)
		                {
		                	role.addValue(roleDN);
		                }
		                attrs.add(role);
	        		}
	                LDAP.updateEntry(oldEntry, newEntry);
	            }
			} 
			catch (CordysValidationException e)
			{
				throw e;
			}		
			catch (Exception e) 
			{
				throw new CordysException("Not able to update subroles for role " + mainRoleDN, e);
			}
		}
    }    
    
    /**
     * Add subrole to mainrole
     * 
     * @param mainRoleDN
     * @param subroleDN
     */
    public static void addRole(String mainRoleDN, String subroleDN)
    {
    	ArrayList<String> addedSubroleDNs = new ArrayList<String>();
    	addedSubroleDNs.add(subroleDN);
    	maintainSubroles(mainRoleDN, addedSubroleDNs, null);
    }
    
    /**
     * Remove subrole from mainrole
     * 
     * @param mainRoleDN
     * @param subroleDN
     */
    public static void removeRole(String mainRoleDN, String subroleDN)
    {
    	ArrayList<String> removedSubroleDNs = new ArrayList<String>();
    	removedSubroleDNs.add(subroleDN);
    	maintainSubroles(mainRoleDN, null, removedSubroleDNs);    	
    }
    
    /**
     * Check if the role has a certain subrole
     * 
     * @param subroleDN
     * @param multilevel  if false, only first level is checked; else the subroletree (10 levels)
     * @return
     */
    public boolean hasSubrole(String subroleDN, boolean multilevel)
    {
    	boolean result = false;
    	if (!multilevel)
    	{
    		ArrayList<String> subroleDNs = getSubroles(this.getRoleDN());
    		result = (subroleDNs.contains(subroleDNs));
    	}
    	else
    	{
    		int subroletree = 0;
    		try
    		{
    			subroletree = getRoleTree(this.getRoleDN(), "10");
        		if (subroletree > 0)
        		{
        			String treeString = Node.writeToString(subroletree, false);
        			result = (treeString.indexOf(subroleDN) != -1);
        		}
    		}
    		finally
    		{
        		if (subroletree > 0)
        		{
        			Node.delete(subroletree);
        		}
    		}

    	}
    	return result;
    }
    
    /**
     * Add a (functional) role to current organization
     * 
     * @param roleName
     */
    public static void addRole(String roleName)
    {
    	LDAPAttributeSet attrs = null;
    	LDAPEntry newEntry = null;
    	String roleDN = "cn=" + roleName + ",cn=organizational roles," + BSF.getOrganization();
    	if (!LDAP.entryExists(roleDN))
    	{
            // not existing yet, so create
            newEntry = new LDAPEntry(roleDN);
            attrs = newEntry.getAttributeSet();
            LDAPAttribute attr = new LDAPAttribute("objectclass", "top");
            attr.addValue("busorganizationalrole");
            attr.addValue("busorganizationalobject");
            attrs.add(attr);
            attrs.add(new LDAPAttribute("cn", roleName));
            attrs.add(new LDAPAttribute("description", roleName));
            attrs.add(new LDAPAttribute("busorganizationalroletype", "Functional"));
            LDAPAttribute role = new LDAPAttribute("role");
        	String defaultRole = "cn=everyoneIn" + Util.getCurrentOrgName() + ",cn=organizational roles," + BSF.getOrganization();
            role.addValue(defaultRole);
            attrs.add(role);                
            LDAP.insertEntry(newEntry);
        }
    }
    
    /**
     * Delete org role from LDAP
     * @param roleDN
     */
    public static void deleteRole(String roleDN)
    {
    	if (Util.isSet(roleDN) && (roleDN.indexOf("cn=organizational roles") != -1))
    	{
	    	try
	    	{
		    	if (LDAP.entryExists(roleDN))
		    	{
		    		LDAP.deleteEntriesRecursive(roleDN);
		    	}
	    	}
	    	catch (Exception e)
	    	{
	    		throw new CordysException("Not able to remove role from LDAP: " + roleDN, e);
	    	}
    	}
    }
}