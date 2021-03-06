/*
  This class has been generated by the Code Generator
*/

package com.cordys.coe.tools.useradmin.ui;

import java.util.ArrayList;
import java.util.HashSet;

import com.cordys.coe.tools.useradmin.cordys.CordysObject;
import com.cordys.coe.tools.useradmin.cordys.CordysObjectList;
import com.cordys.coe.tools.useradmin.cordys.LDAP;
import com.cordys.coe.tools.useradmin.cordys.Role;
import com.cordys.coe.tools.useradmin.cordys.Task;
import com.cordys.cpc.bsf.busobject.BusObjectConfig;
import com.cordys.cpc.bsf.busobject.BusObjectIterator;


/**
 * UI class to support role based task assignments.
 * 
 * @author kekema
 *
 */
public class UIRoleTasks extends UIRoleTasksBase
{
    public UIRoleTasks()
    {
        this((BusObjectConfig)null);
    }

    public UIRoleTasks(BusObjectConfig config)
    {
        super(config);
    }

    /**
     * Get all tasks incl indicator whether the task is assigned to the role
     * 
     * @param roleDN
     * @return
     */
    public static UIRoleTasks getUIRoleTasks(String roleDN, boolean firstRequest)
    {
    	UIRoleTasks uiRoleTasks = new UIRoleTasks();
    	uiRoleTasks.makeTransient();
    	uiRoleTasks.setRoleDN(roleDN);
    	UIRoleTasks.RoleTasks roleTasks = new UIRoleTasks.RoleTasks();
    	uiRoleTasks.setRoleTasksObject(roleTasks);
    	
    	int seqNo = 0;
    	
    	CordysObjectList allTasks = null;
    	CordysObjectList assignedTasks = null;
    	try
    	{
	        allTasks = Task.getAllTasks(firstRequest);
	        assignedTasks = Task.getTasksForRole(roleDN);
	        HashSet<String> assignedTaskIDs = assignedTasks.getKeyHashSet();
	    	for (CordysObject cordysObject : allTasks.getList())
	    	{
	    		Task task = (Task)cordysObject;
	    		seqNo++;
	    		UITask uiTask = new UITask();
	    		uiTask.makeTransient();
	    		uiTask.setSeqNo(seqNo);
	    		uiTask.setTaskID(task.getID());
	    		uiTask.setName(task.getName());
	    		uiTask.setTaskType(task.getType());
	    		uiTask.setAssigned(assignedTaskIDs.contains(task.getID()));
	    		roleTasks.addRoleTaskObject(uiTask);
	        }
    	}
    	finally
    	{
    		if (allTasks != null)
    		{
    			allTasks.cleanup();
    		}
    		if (assignedTasks != null)
    		{
    			assignedTasks.cleanup();
    		}    		
    	}    	
        return uiRoleTasks;
    }

    public void onInsert()
    {
    	// N.A.
    }

    public void onUpdate()
    {
    	String roleDN = this.getRoleDN();
    	// only organizational roles can be updated
    	if (roleDN.indexOf("cn=organizational roles") != -1)
    	{
	    	// compose hashset of tasks who currently are assigned to the role
	    	HashSet<String> currentAssignedTasks = new HashSet<String>();
	    	UIRoleTasks origUIRoleTasks = (UIRoleTasks)this.getOriginalObject();
	    	UIRoleTasks.RoleTasks origRoleTasks = origUIRoleTasks.getRoleTasksObject();
	   		BusObjectIterator<UITask> origRoleTaskObjects = origRoleTasks.getRoleTaskObjects();
	   		while (origRoleTaskObjects.hasMoreElements())
	   		{
	   			UITask origTask = (UITask)origRoleTaskObjects.nextElement();
	   			if (origTask.getAssigned())
	   			{
	   				currentAssignedTasks.add(origTask.getTaskID());
	   			}
	   		}
	   		// find out any required updates from the tuple/new
	   		ArrayList<String> addedTaskIDs = new ArrayList<String>();
	   		ArrayList<String> removedTaskIDs = new ArrayList<String>();
	    	UIRoleTasks.RoleTasks tasks = this.getRoleTasksObject();
	   		BusObjectIterator<UITask> taskObjects = tasks.getRoleTaskObjects();
	   		while (taskObjects.hasMoreElements())
	   		{
	   			UITask uiTask = (UITask)taskObjects.nextElement();
	   			boolean currentlyAssigned = (currentAssignedTasks.contains(uiTask.getTaskID()));
	   			if (uiTask.getAssigned() && (!currentlyAssigned))
	   			{
   					addedTaskIDs.add(uiTask.getTaskID());
	   			}
	   			else if (!uiTask.getAssigned() && (currentlyAssigned))
	   			{
	   				removedTaskIDs.add(uiTask.getTaskID());
	   			}
	   		}
	   		if ((addedTaskIDs.size() > 0) || (removedTaskIDs.size() > 0))
	   		{
	   			Task.maintainTasks(roleDN, addedTaskIDs, removedTaskIDs);
	   		}
    	}
        // read back the actual list
        UIRoleTasks actualUIRoleTasks = UIRoleTasks.getUIRoleTasks(roleDN, false);
        if (actualUIRoleTasks != null)
        {
        	UIRoleTasks.RoleTasks actualRoleTasks = actualUIRoleTasks.getRoleTasksObject();
        	UIRoleTasks.RoleTasks uiTasks = new UIRoleTasks.RoleTasks(new BusObjectConfig(actualRoleTasks, BusObjectConfig.TRANSIENT_OBJECT));
        	this.setRoleTasksObject(uiTasks);
        }       	
    }

    public void onDelete()
    {
    	// N.A.
    }

    public static class RoleTasks extends UIRoleTasks.RoleTasksBase
    {
        public RoleTasks()
        {
            this((BusObjectConfig)null);
        }

        public RoleTasks(BusObjectConfig config)
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
