/*
  This class has been generated by the Code Generator
*/

package com.cordys.coe.tools.useradmin.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.cordys.coe.tools.useradmin.cordys.LDAP;
import com.cordys.coe.tools.useradmin.cordys.Role;
import com.cordys.coe.tools.useradmin.cordys.Task;
import com.cordys.coe.tools.useradmin.util.Util;
import com.cordys.cpc.bsf.busobject.BusObjectConfig;
import com.cordys.cpc.bsf.busobject.BusObjectIterator;


/**
 * UI class to support task based role assignments.
 * 
 * @author kekema
 *
 */
public class UITaskRoles extends UITaskRolesBase
{
    public UITaskRoles()
    {
        this((BusObjectConfig)null);
    }

    public UITaskRoles(BusObjectConfig config)
    {
        super(config);
    }

    /**
     * Get all organizational roles plus indicator whether the given task is assigned.
     * 
     * @param taskID
     * @return
     */
    public static UITaskRoles getUITaskRoles(String taskID)
    {
    	UITaskRoles uiTaskRoles = new UITaskRoles();
    	uiTaskRoles.makeTransient();
    	uiTaskRoles.setTaskID(taskID);
    	UITaskRoles.Roles taskRoles = new UITaskRoles.Roles();
    	uiTaskRoles.setRolesObject(taskRoles);
    	
    	int seqNo = 0;
    	
    	ArrayList<String> instRoleDNs = Role.getOrganizationalRoles();
		HashMap<String, ArrayList<String>> tasksByRole = Task.getAssignedTasksByDN(instRoleDNs);
    	for (String instRoleDN : instRoleDNs)
    	{
    		seqNo++;
    		UITaskRoles.Roles.Role taskRole = new UITaskRoles.Roles.Role();
    		taskRole.makeTransient();
    		taskRole.setSeqNo(seqNo);
    		taskRole.setRoleDN(instRoleDN);
    		taskRole.setRoleName(Util.getNameFromDN(instRoleDN));
    		taskRole.setAssigned(tasksByRole.get(instRoleDN).contains(taskID));
    		taskRoles.addRoleObject(taskRole);
    	}
        return uiTaskRoles;
    }

    public void onInsert()
    {
    	// N.A.
    }

    /**
     * Process any assigned/unassigned roles wrt the task
     */
    public void onUpdate()
    {
    	String taskID = this.getTaskID();
    	// compose hashset of roles who currently do have the task assigned
    	HashSet<String> currentAssignedRoles = new HashSet<String>();
    	UITaskRoles origUITaskRoles = (UITaskRoles)this.getOriginalObject();
    	UITaskRoles.Roles origRoles = origUITaskRoles.getRolesObject();
   		BusObjectIterator<UITaskRoles.Roles.Role> origRoleObjects = origRoles.getRoleObjects();
   		while (origRoleObjects.hasMoreElements())
   		{
   			UITaskRoles.Roles.Role origRole = (UITaskRoles.Roles.Role)origRoleObjects.nextElement();
   			if (origRole.getAssigned())
   			{
   				currentAssignedRoles.add(origRole.getRoleDN());
   			}
   		}
   		// find out any required updates from the tuple/new
   		UITaskRoles.Roles roles = this.getRolesObject();
   		BusObjectIterator<UITaskRoles.Roles.Role> roleObjects = roles.getRoleObjects();
   		while (roleObjects.hasMoreElements())
   		{
   			UITaskRoles.Roles.Role role = (UITaskRoles.Roles.Role)roleObjects.nextElement();
   			boolean currentlyAssigned = (currentAssignedRoles.contains(role.getRoleDN()));
   			if (role.getAssigned() && (!currentlyAssigned))
   			{
   				ArrayList<String> addedTaskIDs = new ArrayList<String>();
   				addedTaskIDs.add(taskID);
   				Task.maintainTasks(role.getRoleDN(), addedTaskIDs, null);
   			}
   			else if (!role.getAssigned() && (currentlyAssigned))
   			{
   				ArrayList<String> removedTaskIDs = new ArrayList<String>();
   				removedTaskIDs.add(taskID);
   				Task.maintainTasks(role.getRoleDN(), null, removedTaskIDs);
   			}
   		}
        // read back the actual list
   		UITaskRoles actualTaskRoles = UITaskRoles.getUITaskRoles(taskID);
        if (actualTaskRoles != null)
        {
        	UITaskRoles.Roles actualRoles = actualTaskRoles.getRolesObject();
        	UITaskRoles.Roles uiRoles = new UITaskRoles.Roles(new BusObjectConfig(actualRoles, BusObjectConfig.TRANSIENT_OBJECT));
        	this.setRolesObject(uiRoles);
        }      	
    }

    public void onDelete()
    {
    	// N.A.    	
    }

    public static class Roles extends UITaskRoles.RolesBase
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

        public static class Role extends UITaskRoles.Roles.RoleBase
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
