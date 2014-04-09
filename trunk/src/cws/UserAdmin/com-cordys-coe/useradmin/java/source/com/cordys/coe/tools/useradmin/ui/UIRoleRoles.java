/*
  This class has been generated by the Code Generator
*/

package com.cordys.coe.tools.useradmin.ui;

import java.util.ArrayList;
import java.util.HashSet;

import com.cordys.coe.tools.useradmin.cordys.Role;
import com.cordys.coe.tools.useradmin.util.Util;
import com.cordys.cpc.bsf.busobject.BusObjectConfig;
import com.cordys.cpc.bsf.busobject.BusObjectIterator;

/**
 * UI class to support role based subrole assignments.
 * 
 * @author kekema
 *
 */
public class UIRoleRoles extends UIRoleRolesBase
{
    public UIRoleRoles()
    {
        this((BusObjectConfig)null);
    }

    public UIRoleRoles(BusObjectConfig config)
    {
        super(config);
    }

    /**
     * Get all roles incl internal roles plus indicator whether the role is a subrole of the input role.
     * 
     * @param roleDN
     * @return
     */
    public static com.cordys.coe.tools.useradmin.ui.UIRoleRoles getUIRoleRoles(String roleDN)
    {
    	UIRoleRoles uiRoleRoles = new UIRoleRoles();
    	uiRoleRoles.makeTransient();
    	uiRoleRoles.setRoleDN(roleDN);
    	UIRoleRoles.Roles roleRoles = new UIRoleRoles.Roles();
    	uiRoleRoles.setRolesObject(roleRoles);
    	
    	int seqNo = 0;
    	
    	ArrayList<String> subroleDNs = Role.getSubroles(roleDN);
    	
    	ArrayList<String> instRoleDNs = Role.getAllRoles(true);
    	for (String instRoleDN : instRoleDNs)
    	{
    		if ((!instRoleDN.equals(roleDN)) && (!instRoleDN.startsWith("cn=everyone")))
    		{
	    		seqNo++;
	    		UIRoleRoles.Roles.Role roleRole = new UIRoleRoles.Roles.Role();
	    		roleRole.makeTransient();
	    		roleRole.setSeqNo(seqNo);
	    		roleRole.setRoleDN(instRoleDN);
	    		roleRole.setRoleName(Util.getNameFromDN(instRoleDN));
	    		roleRole.setAssigned(subroleDNs.contains(instRoleDN));
	    		roleRoles.addRoleObject(roleRole);
    		}
    	}
    	
        return uiRoleRoles;
    }

    public void onInsert()
    {
    	// N.A.
    }

    /**
     * Process any assigned/unassigned subrole wrt the role
     */
    public void onUpdate()
    {
    	String mainRoleDN = this.getRoleDN();
    	// only organizational roles can be updated
    	if (mainRoleDN.indexOf("cn=organizational roles") != -1)
    	{
	    	// compose hashset of roles who currently are assigned to the main role
	    	HashSet<String> currentAssignedRoles = new HashSet<String>();
	    	UIRoleRoles origUIRoleRoles = (UIRoleRoles)this.getOriginalObject();
	    	UIRoleRoles.Roles origRoles = origUIRoleRoles.getRolesObject();
	   		BusObjectIterator<UIRoleRoles.Roles.Role> origRoleObjects = origRoles.getRoleObjects();
	   		while (origRoleObjects.hasMoreElements())
	   		{
	   			UIRoleRoles.Roles.Role origRole = (UIRoleRoles.Roles.Role)origRoleObjects.nextElement();
	   			if (origRole.getAssigned())
	   			{
	   				currentAssignedRoles.add(origRole.getRoleDN());
	   			}
	   		}
	   		// find out any required updates from the tuple/new
	   		ArrayList<String> addedSubroleDNs = new ArrayList<String>();
	   		ArrayList<String> removedSubroleDNs = new ArrayList<String>();
	    	UIRoleRoles.Roles roles = this.getRolesObject();
	   		BusObjectIterator<UIRoleRoles.Roles.Role> roleObjects = roles.getRoleObjects();
	   		while (roleObjects.hasMoreElements())
	   		{
	   			UIRoleRoles.Roles.Role role = (UIRoleRoles.Roles.Role)roleObjects.nextElement();
	   			boolean currentlyAssigned = (currentAssignedRoles.contains(role.getRoleDN()));
	   			if (role.getAssigned() && (!currentlyAssigned))
	   			{
	   				Role toBeAddedRole = new Role(role.getRoleDN());
	   				// check for circular
	   				if (!toBeAddedRole.hasSubrole(mainRoleDN, true))
	   				{
	   					addedSubroleDNs.add(role.getRoleDN());
	   				}
	   			}
	   			else if (!role.getAssigned() && (currentlyAssigned))
	   			{
	   				removedSubroleDNs.add(role.getRoleDN());
	   			}
	   		}
	   		if ((addedSubroleDNs.size() > 0) || (removedSubroleDNs.size() > 0))
	   		{
	   			Role.maintainSubroles(mainRoleDN, addedSubroleDNs, removedSubroleDNs);
	   		}
    	}
        UIRoleRoles actualRoleRoles = UIRoleRoles.getUIRoleRoles(mainRoleDN);
        if (actualRoleRoles != null)
        {
        	UIRoleRoles.Roles actualRoles = actualRoleRoles.getRolesObject();
        	UIRoleRoles.Roles uiRoles = new UIRoleRoles.Roles(new BusObjectConfig(actualRoles, BusObjectConfig.TRANSIENT_OBJECT));
        	this.setRolesObject(uiRoles);
        }   		    	
    }

    public void onDelete()
    {
    	// N.A.
    }

    public static class Roles extends UIRoleRoles.RolesBase
    {
        public Roles()
        {
            this((BusObjectConfig)null);
        }

        public Roles(BusObjectConfig config)
        {
            super(config);
        }

        public void onInsert()
        {
        }

        public void onUpdate()
        {
        }

        public void onDelete()
        {
        }

        public static class Role extends UIRoleRoles.Roles.RoleBase
        {
            public Role()
            {
                this((BusObjectConfig)null);
            }

            public Role(BusObjectConfig config)
            {
                super(config);
            }

            public void onInsert()
            {
            }

            public void onUpdate()
            {
            }

            public void onDelete()
            {
            }

        }
    }
}