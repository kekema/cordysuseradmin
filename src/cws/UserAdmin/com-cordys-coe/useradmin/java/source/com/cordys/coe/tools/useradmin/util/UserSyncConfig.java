package com.cordys.coe.tools.useradmin.util;

import java.util.ArrayList;

import com.cordys.coe.tools.useradmin.cordys.XMLStore;
import com.cordys.coe.tools.useradmin.cordys.XMLStoreObject;
import com.cordys.coe.tools.useradmin.exception.UserAdminException;

/**
 * Class to get config settings for user synchronization.
 * 
 * @author kekema
 *
 */
public class UserSyncConfig
{
	private static final String CONFIG_XMLSTORE_KEY = "/com-cordys/coe/useradmin/UserSync.xml";
	private XMLStoreObject configXMLObject;
	
	private UserSyncConfig(XMLStoreObject configXMLObject)
	{
		this.configXMLObject = configXMLObject;
	}
	
	/**
	 * Load the config from XMLStore
	 * 
	 * @return
	 */
	public static UserSyncConfig loadConfig()
	{
		UserSyncConfig userSyncConfig = loadConfig(false);
		return userSyncConfig;
	}
	
	/**
	 * Load the config from XMLStore
	 * 
	 * @return
	 */
	public static UserSyncConfig loadConfig(boolean isvVersion)
	{
		UserSyncConfig userSyncConfig = null;
		XMLStoreObject configXMLObject = XMLStore.getXMLObject(CONFIG_XMLSTORE_KEY, isvVersion);
		if (configXMLObject != null)
		{
			userSyncConfig = new UserSyncConfig(configXMLObject);
		}
		else
		{
			throw new UserAdminException("Not able to load UserSync configuration file.");
		}
		return userSyncConfig;
	}
	
	/**
	 * Get empty config (default config) as to prepare for an update.
	 * 
	 * @return
	 */
	public static UserSyncConfig getEmptyConfig()
	{	
		// take the isvVersion as starting point; this will support situations in which
		// the structure has changed between 2 releases
		boolean isvVersion = true;
		UserSyncConfig userSyncConfig = null;
		try
		{
			userSyncConfig = loadConfig(isvVersion);
		}
		catch (Exception e)
		{
			// ignore; isv version not found; continue to read org version
		}
		if (userSyncConfig == null)
		{
			// no isv loaded; dev env
			isvVersion = false;
			userSyncConfig = loadConfig(isvVersion);
		}
		userSyncConfig.setIncludeSubgroups(false);  // default value
		userSyncConfig.removeGroups();
		userSyncConfig.setOnlyInsert(true); 		// default value
		userSyncConfig.setAuthenticationType("");
		userSyncConfig.setDefaultPassword("");
		userSyncConfig.setDeleteObsoleteUsers(false);	// default value
		
		return userSyncConfig;
	}
	
	/**
	 * Get the boolean IncludeSubgroups
	 * 
	 * @param defaultIncludeSubgroups
	 * @return
	 */
	public boolean getIncludeSubgroups(boolean defaultIncludeSubgroups)
	{
		boolean includeSubgroups = configXMLObject.getBooleanValue(".//IncludeSubgroups", defaultIncludeSubgroups);
		return includeSubgroups;
	}
	
	/**
	 * Set the IncludeSubgroups
	 * 
	 * @param includeSubgroups
	 */
	public void setIncludeSubgroups(boolean includeSubgroups)
	{
		configXMLObject.setBooleanValue(".//IncludeSubgroups", includeSubgroups);
	}
	
	/**
	 * Get DNs of all defined groups
	 * 
	 * @return
	 */
	public ArrayList<String> getGroupDNs()
	{
		ArrayList<String> groupDNs = configXMLObject.getStringValues(".//Groups/Group/dn");
		return groupDNs;
	}
	
	/**
	 * Get the default auth type
	 * 
	 * @param defaultAuthenticationType
	 * @return
	 */
	public String getAuthenticationType(String defaultAuthenticationType)
	{
		String authenticationType = configXMLObject.getStringValue(".//NewUserConfig/AuthenticationType", defaultAuthenticationType);
		return authenticationType;
	}
	
	/**
	 * Set the auth type
	 * 
	 * @param authenticationType
	 */
	public void setAuthenticationType(String authenticationType)
	{
		configXMLObject.setStringValue(".//NewUserConfig/AuthenticationType", authenticationType);
	}
	
	
	/**
	 * Get the default for the defaultpassword
	 * 
	 * @param defaultDefaultPassword
	 * @return
	 */
	public String getDefaultPassword(String defaultDefaultPassword)
	{
		String defaultPassword = configXMLObject.getStringValue(".//NewUserConfig/DefaultPassword", defaultDefaultPassword);
		return defaultPassword;
	}
	
	/**
	 * Set the defaultpassword
	 * 
	 * @param defaultpassword
	 */
	public void setDefaultPassword(String defaultPassword)
	{
		configXMLObject.setStringValue(".//NewUserConfig/DefaultPassword", defaultPassword);
	}
	
	/**
	 * Get the user searchroot as defined for a group
	 * 
	 * @param groupDN
	 * @param defaultSearchRoot
	 * @return
	 */
	public String getUserSearchRoot(String groupDN, String defaultSearchRoot)
	{
		return(configXMLObject.getStringValue(".//Groups/Group[dn=\""+groupDN+"\"]/UserSearchRoot", defaultSearchRoot));
	}
	
	/**
	 * Set the user searchroot as defined for a group
	 * 
	 * @param groupDN
	 * @param searchRoot
	 * @return
	 */
	public void setUserSearchRoot(String groupDN, String searchRoot)
	{
		configXMLObject.setStringValue(".//Groups/Group[dn=\""+groupDN+"\"]/UserSearchRoot", searchRoot);
	}
	
	/**
	 * Get the assign roles
	 * 
	 * @return
	 */
	public ArrayList<String> getAssignRoles(String groupDN)
	{
		ArrayList<String> assignRoles = configXMLObject.getStringValues(".//Groups/Group[dn=\""+groupDN+"\"]/AssignRoles/Role");
		return assignRoles;
	}
	
	/**
	 * Set the assign roles
	 * 
	 * @param roleStrings
	 */
	public void setAssignRoles(String groupDN, ArrayList<String> roleStrings)
	{
		configXMLObject.setStringValues(".//Groups/Group[dn=\""+groupDN+"\"]/AssignRoles", "Role", roleStrings);
	}
	
	/**
	 * Get the boolean OnlyInsert
	 * @param defaultOnlyInsert
	 * @return
	 */
	public boolean getOnlyInsert(boolean defaultOnlyInsert)
	{
		boolean onlyInsert = configXMLObject.getBooleanValue(".//MaintainUsers/OnlyInsert", defaultOnlyInsert);
		return onlyInsert;
	}
	
	/**
	 * Set the OnlyInsert
	 * 
	 * @param onlyInsert
	 */
	public void setOnlyInsert(boolean onlyInsert)
	{
		configXMLObject.setBooleanValue(".//MaintainUsers/OnlyInsert", onlyInsert);
	}
	
	/**
	 * Get the role DN's
	 * @param groupDN
	 * @return
	 */
	public ArrayList<String> getRoleDNs(String groupDN)
	{
		ArrayList<String> roleDNs = configXMLObject.getStringValues(".//Groups/Group[dn=\""+groupDN+"\"]/AssignRoles/Role");
		return roleDNs;
	}
	
	/**
	 * Get the boolean DeleteObsoleteUsers
	 * 
	 * @param defaultDeleteObsoleteUsers
	 * @return
	 */
	public boolean getDeleteObsoleteUsers(boolean defaultDeleteObsoleteUsers)
	{
		boolean deleteObsoleteUsers = configXMLObject.getBooleanValue(".//DeleteObsoleteUsers", defaultDeleteObsoleteUsers);
		return deleteObsoleteUsers;
	}
	
	/**
	 * Set the DeleteObsoleteUsers
	 * 
	 * @param deleteObsoleteUsers
	 */
	public void setDeleteObsoleteUsers(boolean deleteObsoleteUsers)
	{
		configXMLObject.setBooleanValue(".//DeleteObsoleteUsers", deleteObsoleteUsers);
	}
	
	/**
	 * Add a group under the groups
	 * 
	 * @param groupDN
	 */
	public void addGroup(String groupDN)
	{
		String groupXML = "<Group><dn>" + groupDN + "</dn><UserSearchRoot></UserSearchRoot><AssignRoles></AssignRoles></Group>";
		configXMLObject.addXML(".//Groups", groupXML);
	}
	
	/**
	 * Remove the groups
	 */
	private void removeGroups()
	{
		configXMLObject.removeElements(".//Groups/Group");
	}
	
	/**
	 * Update this UserSyncConfig
	 */
	public void update()
	{
		// load existing for tuple/old
		UserSyncConfig oldConfig = loadConfig(false);
		try
		{
			XMLStore.updateXMLObject(CONFIG_XMLSTORE_KEY, oldConfig.configXMLObject, this.configXMLObject);
		}
		finally
		{
			oldConfig.clearConfig();
		}
	}
	
	/**
	 * clear up the config (free underlying nom memory)
	 */
	public void clearConfig()
	{
		configXMLObject.freeXMLMemory();
		configXMLObject = null;
	}

}