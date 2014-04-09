package com.cordys.coe.tools.useradmin.cordys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.cordys.coe.tools.useradmin.cordys.exception.CordysException;
import com.cordys.coe.tools.useradmin.util.NestedXMLObject;
import com.cordys.coe.tools.useradmin.util.Util;
import com.cordys.cpc.bsf.busobject.BSF;
import com.cordys.cpc.bsf.soap.SOAPRequestObject;
import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;
import com.eibus.xml.xpath.XPath;

/**
 * Class to represent a Cordys task (in summary: id, name, type, dn).
 * It's about tasks which can be assigned in User Manager (so not representing inbox tasks).
 * The class abstracts all kind of tasks related functionality in the Cordys platform.
 * 
 * @author kekema
 *
 */
public class Task extends CordysObject
{
	private NestedXMLObject taskXMLObject;
	private static int taskCollectionCache = 0;			// cache for all tasks as the query to xds is a bit slow
	
	/**
	 * Construct new task
	 * 
	 * @param rootNode
	 */
	public Task(int rootNode)
	{
		taskXMLObject = new NestedXMLObject(rootNode);
	}
	

	/**
	 * Free the xml behind if needed.
	 */
	public void cleanup()
	{
		taskXMLObject.freeXMLMemory();
		taskXMLObject = null;
	}
	
	/**
	 * Get the task ID.
	 * Sometimes the task ID is descriptive (eg 'cordys_wsappserver_manageDBSchemaTask'); or it is a GUID.
	 * 
	 * @return
	 */
	public String getID()
	{
		return(taskXMLObject.getAttributeStringValue("id", null));
	}
	
	/**
	 * Get the task name.
	 * 
	 * @return
	 */
	public String getName()
	{
		return(taskXMLObject.getAttributeStringValue("name", null));
	}
	
	/**
	 * Get the task description (for sorting purposes; implements CordysObject.getDescription()).
	 * 
	 * @return
	 */
	public String getDescription()
	{
		return(getName());
	}
	
	/**
	 * Get the task type. Eg 'UI_TASK'.
	 * 
	 * @return
	 */
	public String getType()
	{
		return(taskXMLObject.getAttributeStringValue("type", null));
	}
	
	/**
	 * Get a copy of the XML representation.
	 * 
	 * @return
	 */
	public int getNOMCopy()
	{
		return Node.clone(taskXMLObject.getNOMNode(), true);
	}
	
	/**
	 * Get all tasks which are (indirectly) assigned to the user via roles.
	 * The caller is responsible to use Tasks.cleanup().
	 * 
	 * @param orgUserDN
	 * @return
	 */
	public static CordysObjectList getAssignedRoleTasks(String orgUserDN)
	{
		CordysObjectList tasks = new CordysObjectList();
		HashSet<String> taskID = new HashSet<String>();
		 
		String namespace = "http://schemas.cordys.com/task/1.0/runtime/";
        String methodName = "GetTasksForUser";
        
        String[] paramNames = new String[] { "User" };
        Object[] paramValues = new Object[] { orgUserDN };
        
        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, paramNames, paramValues);
        
        int response = 0;
        try 
        {
            response = sro.execute();
			if (response != 0)
			{
		        int[] resultNodes = XPath.getMatchingNodes(".//tuple/old/Task", null, response);
		        for (int resultNode : resultNodes) 
		        {
		        	// from the DN, we can see if the task is directly assigned (dn = org user dn), or
		        	// the task is indirectly assigned via a role (dn = role dn, eg 'cn=developer,cn=Cordys ESBServer,cn=cordys,cn=defaultInst,o=Cordys')
		        	String dn = Node.getAttribute(resultNode, "dn");
		        	if (!(dn.equals(orgUserDN)))
		        	{
		        		// so it's a role task
		        		String id = Node.getAttribute(resultNode, "id");
		        		// prevent duplicates via several roles having the same task
		        		if (!(taskID.contains(id)))
		        		{        			
		        			taskID.add(id);
		        			Task task = new Task(Node.unlink(resultNode));
		        			tasks.add(task);
		        		}
		        	}
		        }
			}
        }
        catch (Exception e)
        {
        	throw new CordysException("Not able to read role tasks for user " + orgUserDN, e);
        }	
		finally
		{
			if (response > 0)
			{
				Node.delete(response);
				response = 0;
			}
		}  
        tasks.sort();
        return tasks;
	}
	
	/**
	 * Get all tasks which are directly assigned to the user or role.
	 * 
	 * @param DN	can be a orgUserDN or roleDN
	 * @return
	 */
	public static CordysObjectList getAssignedTasks(String DN)
	{
		CordysObjectList tasks = new CordysObjectList();
		
		String namespace = "http://schemas.cordys.com/task/1.0/runtime/";
        String methodName = "GetConfiguredTasks";
        
        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, null, null);
        
        int response = 0;
        int paramNode = 0;
        try 
        {
        	Document xmlDoc = BSF.getXMLDocument();
        	paramNode = xmlDoc.parseString("<ConfiguredTasks dn=\""+DN+"\"/>");
        	sro.addParameterAsXml(paramNode);
            response = sro.execute();
			if (response != 0)
			{
		        int[] resultNodes = XPath.getMatchingNodes(".//tuple/old/ConfiguredTasks/Task", null, response);
		        for (int resultNode : resultNodes) 
		        {
		        	String taskID = Node.getAttribute(resultNode, "id");
		        	Task task = new Task(Node.unlink(resultNode));
			        tasks.add(taskID, task);
		        }
			}
        }
        catch (Exception e)
        {
        	throw new CordysException("Not able to read assigned tasks for user/role " + DN, e);
        }
		finally
		{
			if (response > 0)
			{
				Node.delete(response);
				response = 0;
			}
			if (paramNode > 0)
			{
				Node.delete(paramNode);
				paramNode = 0;
			}
		}		
        tasks.sort();
		return tasks;
	}
	
	/**
	 * Get all defined tasks.
	 * As the query behind in Cordys is a bit slow, a cache is used.
	 * Because of the cache usage, no cleanup after using this method required.
	 * 
	 * @param refreshCache		can be used to refresh the cache when user has started a new UI session
	 * 							in a production env with multiple ws-apps instances, this concept 
	 * 							is not 100%, but in production, the list of all tasks is stable
	 * @return
	 */
	public static CordysObjectList getAllTasks(boolean refreshCache)
	{
		CordysObjectList tasks = new CordysObjectList();
		
        int responseTaskCollection = 0;
        try 
        {
        	checkTaskCollectionCache(refreshCache);
	        responseTaskCollection = taskCollectionCache;
			if (responseTaskCollection != 0)
			{
				int[] resultNodes = XPath.getMatchingNodes(".//tuple/old/Task", null, responseTaskCollection);
		        for (int resultNode : resultNodes) 
		        {
        			// skip internal tasks like 'tagdefinitionsetpropertyview'
        			String taskXML = Node.writeToString(resultNode, false);
        			if (!(taskXML.contains("http://schemas.cordys.com/cap/artifact")))
        			{
        				Task task = new Task(Node.clone(resultNode, true));
				        tasks.add(task);
        			}
		        }
			}
        }
        catch (Exception e)
        {
        	throw new CordysException("Not able to read all tasks.", e);
        }
        tasks.sort();
        return tasks;
	}
	
	/**
	 * If needed, read/refresh the taskCollectionCache.
	 * 
	 * @param refreshCache
	 */
	private static void checkTaskCollectionCache(boolean refreshCache)
	{
        synchronized (Task.class)
        {
	        if ((taskCollectionCache == 0) || refreshCache)
	        {
				if (taskCollectionCache > 0)
				{
					Node.delete(taskCollectionCache);
					taskCollectionCache = 0;
				}
				String namespace = "http://schemas.cordys.com/task/1.0/runtime/";
		        String methodName = "GetTaskCollection ignoreInternals=\"true\"";
		        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, null, null);
		        taskCollectionCache = sro.execute();
	        }
        }
	}
	
	/**
	 * Get hashmap of all tasksIDs by task name
	 * 
	 * @param refreshCache		see getAllTasks
	 * @return
	 */
	public static HashMap<String, String> getAllTaskIDsByName(boolean refreshCache)
	{
		HashMap<String, String> result = new HashMap<String, String>();
		CordysObjectList tasks = getAllTasks(refreshCache);
		for (CordysObject cordysObject : tasks.getList())
		{
			Task task = (Task)cordysObject;
			result.put(task.getName(), task.getID());
		}
		tasks.cleanup();
		return result;
	}
	
	/**
	 * Get the list of unassigned tasks for the user.
	 * 
	 * @param orgUserDN
	 * @param refreshCache		see getAllTasks method
	 * 
	 * @return
	 */
	public static CordysObjectList getUnassignedTasks(String orgUserDN, boolean refreshCache)
	{
		CordysObjectList tasks = new CordysObjectList();
		
		HashSet<String> assignedTaskID = new HashSet<String>();
		 
		// first get the assigned task id's
		String namespace = "http://schemas.cordys.com/task/1.0/runtime/";
        String methodName = "GetTasksForUser";
        
        String[] paramNames = new String[] { "User" };
        Object[] paramValues = new Object[] { orgUserDN };
        
        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, paramNames, paramValues);
        
        int responseTasksForUser = 0;
        int responseTaskCollection = 0;
        try 
        {
        	responseTasksForUser = sro.execute();
			if (responseTasksForUser != 0)
			{
		        int[] resultNodes = XPath.getMatchingNodes(".//tuple/old/Task", null, responseTasksForUser);
		        for (int resultNode : resultNodes) 
		        {
	        		String id = Node.getAttribute(resultNode, "id");
	        		if (!(assignedTaskID.contains(id)))
	        		{
	        			assignedTaskID.add(id);
		        	}
		        }
		        checkTaskCollectionCache(refreshCache);
		        responseTaskCollection = taskCollectionCache;
				if (responseTaskCollection != 0)
				{
			        resultNodes = XPath.getMatchingNodes(".//tuple/old/Task", null, responseTaskCollection);
			        for (int resultNode : resultNodes) 
			        {
			        	String id = Node.getAttribute(resultNode, "id");
	        			// check if this task is not assigned yet
		        		if (!(assignedTaskID.contains(id)))
		        		{
		        			// skip internal tasks like 'tagdefinitionsetpropertyview'
		        			String taskXML = Node.writeToString(resultNode, false);
		        			if (!(taskXML.contains("http://schemas.cordys.com/cap/artifact")))
		        			{
		        				Task task = new Task(Node.clone(resultNode, true));
						        tasks.add(task);
		        			}
		        		}
			        }
				}
			}
        }
        catch (Exception e)
        {
        	throw new CordysException("Not able to read unassigned tasks for user " + orgUserDN, e);
        }
		finally
		{
			if (responseTasksForUser > 0)
			{
				Node.delete(responseTasksForUser);
				responseTasksForUser = 0;
			}
		}	
        tasks.sort();
		return tasks;
	}
	
	/**
	 * Assign additional tasks and/or unassign tasks
	 * 
	 * @param DN
	 * @param addedTaskIDs
	 * @param removedTaskIDs
	 */
	public static void maintainTasks(String DN, ArrayList<String> addedTaskIDs, ArrayList<String> removedTaskIDs)
	{
		ArrayList<String> taskIDs = new ArrayList<String>();
		CordysObjectList currentTasks = null;
		try
		{
			currentTasks = getAssignedTasks(DN);
			for (CordysObject cordysObject : currentTasks.getList())
			{
				Task task = (Task)cordysObject;
				taskIDs.add(task.getID());
			}
			if ((removedTaskIDs != null) && (removedTaskIDs.size() > 0))
			{
				taskIDs.removeAll(removedTaskIDs);
			}
			if ((addedTaskIDs != null) && (addedTaskIDs.size() > 0))
			{
				// remove duplicates
				taskIDs.removeAll(addedTaskIDs);
				// add new ones
				taskIDs.addAll(addedTaskIDs);
			}
			updateAssignedTasks(DN, taskIDs);
		}
		finally
		{
			if (currentTasks != null)
			{
				currentTasks.cleanup();
			}
		}
	}
	
	/**
	 * Update the user or role assigned tasks to the given set.
	 * 
	 * @param DN	can be a orgUserDN or roleDN
	 * @param assignedTaskIDs
	 */
    public static void updateAssignedTasks(String DN, ArrayList<String> assignedTaskIDs)
    {
		int taskCollectionResponse = 0;
		try
		{
			// get the tasks for the assignedTasksIDs as to be able to populate the tuple/new
			String namespace = "http://schemas.cordys.com/task/1.0/runtime/";
			String methodName = "GetTaskCollection";
	        
	        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, null, null);
	        
	        ArrayList<Integer> paramNodes = new ArrayList<Integer>();
	        Document xmlDoc = BSF.getXMLDocument();
	        try 
	        {
		        for (String taskID : assignedTaskIDs) 
		        {
		        	int paramNode = xmlDoc.parseString("<Task id=\"" + taskID + "\" detail=\"false\" acl=\"false\" />");
		        	paramNodes.add(paramNode);
		        	sro.addParameterAsXml(paramNode);
		        }
		        if (assignedTaskIDs.size() > 0)
		        {
		        	taskCollectionResponse = sro.execute();
		        }
	        }
	        catch (Exception e)
	        {
	        	throw new CordysException("Not able to read tasks data.", e);
	        }
			finally
			{
				for (int paramNode : paramNodes)
				{
					if (paramNode != 0)
					{
						Node.delete(paramNode);
					}
				}
			}
        	// get current configured tasks as to be able to populate the tuple/old section
        	int configuredTasksResponse = 0;
        	int publishConfiguredTasksResponse = 0;
        	int configuredTasksNode = 0;
	        namespace = "http://schemas.cordys.com/task/1.0/runtime/";
			methodName = "GetConfiguredTasks";
	        sro = new SOAPRequestObject(namespace, methodName, null, null);
	        int paramNode = 0;
	        int paramNodePublish = 0;
	        try
	        {
		        paramNode = xmlDoc.parseString("<ConfiguredTasks dn=\""+DN+"\"/>");
	        	sro.addParameterAsXml(paramNode);
	        	configuredTasksResponse = sro.execute();
	        	if (configuredTasksResponse > 0)
	        	{
	        		configuredTasksNode = XPath.getFirstMatch(".//ConfiguredTasks", null, configuredTasksResponse);
	        		boolean existingTasks = (XPath.getMatchingNodes(".//ConfiguredTasks/Task", null, configuredTasksResponse).length > 0);
	        		if (configuredTasksNode > 0)
	        		{
	        			// compose the PublishConfiguredTasks with tuple old/new
	    		        namespace = "http://schemas.cordys.com/task/1.0/designtime/";
	    				methodName = "PublishConfiguredTasks";
	    		        sro = new SOAPRequestObject(namespace, methodName, null, null);
	    		        String paramString = "<tuple>";
	    		        if (existingTasks)
	    		        {
	    		        	paramString = paramString + "<old/>";
	    		        }
	    		        paramString = paramString + "<new><ConfiguredTasks xmlns=\"http://schemas.cordys.com/task/1.0/\" dn=\"" + DN + "\"/></new></tuple>";
	    		        paramNodePublish = xmlDoc.parseString(paramString);
	    		        if (existingTasks)
	    		        {
	    		        	int oldNode = XPath.getFirstMatch(".//old", null, paramNodePublish);
	    		        	if (oldNode > 0)
	    		        	{
	    		        		Node.appendToChildren(configuredTasksNode, oldNode);
	    		        	}
	    		        }
    		        	int newConfiguredTasksNode = XPath.getFirstMatch(".//new/ConfiguredTasks", null, paramNodePublish);
    		        	if (newConfiguredTasksNode > 0)
    		        	{
    		        		if (taskCollectionResponse > 0)
    		        		{
	    				        int[] taskCollectionNodes = XPath.getMatchingNodes(".//tuple/old/Task", null, taskCollectionResponse);
	    				        for (int taskCollectionNode : taskCollectionNodes) 
	    				        {
	    				        	// check if valid task - name should be there
	    				        	String taskName = Node.getAttribute(taskCollectionNode, "name");
	    				        	if (Util.isSet(taskName))
	    				        	{
		    				        	int taskNode = Node.clone(taskCollectionNode, false);
		    				        	Node.appendToChildren(taskNode, newConfiguredTasksNode);
	    				        	}
	    				        }
    		        		}
    		        	}
	    		        sro.addParameterAsXml(paramNodePublish);
	    		        publishConfiguredTasksResponse = sro.execute();
	        		}
	        	}
	        }
	        catch (Exception e)
	        {
	        	throw new CordysException("Not able to read/update configured tasks for " + DN, e);
	        }
			finally
			{
				if (configuredTasksResponse > 0)
				{
					Node.delete(configuredTasksResponse);
					configuredTasksResponse = 0;
				}
				if (publishConfiguredTasksResponse > 0)
				{
					Node.delete(publishConfiguredTasksResponse);
					publishConfiguredTasksResponse = 0;
				}
				if (paramNode > 0)
				{
					Node.delete(paramNode);
					paramNode = 0;
				}
				if (paramNodePublish > 0)
				{
					Node.delete(paramNodePublish);
					paramNode = 0;
				}
			}
		}
		finally
		{
			if (taskCollectionResponse > 0)
			{
				Node.delete(taskCollectionResponse);
				taskCollectionResponse = 0;
			}
		}
    }
    
	/**
	 * Delete the current tasks as assigned to a user or org role.
	 * 
	 * @param DN	can be a orgUserDN or orgRoleDN
	 */
    public static void deleteAllAssignedTasksForDN(String DN)
    {
    	// get current configured tasks as to be able to populate the tuple/old section
    	int configuredTasksResponse = 0;
    	int publishConfiguredTasksResponse = 0;
    	int configuredTasksNode = 0;
        String namespace = "http://schemas.cordys.com/task/1.0/runtime/";
		String methodName = "GetConfiguredTasks";
		SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, null, null);
        int paramNode = 0;
        int paramNodePublish = 0;
        Document xmlDoc = BSF.getXMLDocument();
        try
        {
	        paramNode = xmlDoc.parseString("<ConfiguredTasks dn=\""+DN+"\"/>");
        	sro.addParameterAsXml(paramNode);
        	configuredTasksResponse = sro.execute();
        	if (configuredTasksResponse > 0)
        	{
        		configuredTasksNode = XPath.getFirstMatch(".//ConfiguredTasks", null, configuredTasksResponse);
        		boolean existingTasks = (XPath.getMatchingNodes(".//ConfiguredTasks/Task", null, configuredTasksResponse).length > 0);
        		if (existingTasks && (configuredTasksNode > 0))
        		{
        			// compose the PublishConfiguredTasks with tuple old
    		        namespace = "http://schemas.cordys.com/task/1.0/designtime/";
    				methodName = "PublishConfiguredTasks";
    		        sro = new SOAPRequestObject(namespace, methodName, null, null);
    		        String paramString = "<tuple><old/><new><ConfiguredTasks xmlns=\"http://schemas.cordys.com/task/1.0/\" dn=\"" + DN + "\"/></new></tuple>";
    		        paramNodePublish = xmlDoc.parseString(paramString);
		        	int oldNode = XPath.getFirstMatch(".//old", null, paramNodePublish);
		        	Node.appendToChildren(configuredTasksNode, oldNode);
    		        sro.addParameterAsXml(paramNodePublish);
    		        publishConfiguredTasksResponse = sro.execute();
        		}
        	}
        }
        catch (Exception e)
        {
        	throw new CordysException("Not able to delete configured tasks for " + DN, e);
        }
		finally
		{
			if (configuredTasksResponse > 0)
			{
				Node.delete(configuredTasksResponse);
				configuredTasksResponse = 0;
			}
			if (publishConfiguredTasksResponse > 0)
			{
				Node.delete(publishConfiguredTasksResponse);
				publishConfiguredTasksResponse = 0;
			}
			if (paramNode > 0)
			{
				Node.delete(paramNode);
				paramNode = 0;
			}
			if (paramNodePublish > 0)
			{
				Node.delete(paramNodePublish);
				paramNode = 0;
			}
		}
    }
    
	/**
	 * Get all tasks as assigned to the given role
	 * 
	 * @param roleDN
	 * @return
	 */
	public static CordysObjectList getTasksForRole(String roleDN)
	{
		CordysObjectList tasks = new CordysObjectList();
		 
		String namespace = "http://schemas.cordys.com/task/1.0/runtime/";
        String methodName = "GetTasksForRoles";
        
        String[] paramNames = new String[] { "Role" };
        Object[] paramValues = new Object[] { roleDN };
        
        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, paramNames, paramValues);
        
        int response = 0;
        int paramNode = 0;
        try 
        {
        	Document xmlDoc = BSF.getXMLDocument();
        	paramNode = xmlDoc.parseString("<Role dn=\""+roleDN+"\"/>");
        	sro.addParameterAsXml(paramNode);
            response = sro.execute();
			if (response != 0)
			{
		        int[] resultNodes = XPath.getMatchingNodes(".//tuple/old/Role/Task", null, response);
		        for (int resultNode : resultNodes) 
		        {
		        	Task task = new Task(Node.unlink(resultNode));
		        	tasks.add(task.getID(), task);
		        }
			}
        }
        catch (Exception e)
        {
        	throw new CordysException("Not able to read role assigned tasks for role " + roleDN, e);
        }	
		finally
		{
			if (response > 0)
			{
				Node.delete(response);
				response = 0;
			}
			if (paramNode > 0)
			{
				Node.delete(paramNode);
				paramNode = 0;
			}
		}     
        tasks.sort();
        return tasks;
	}    
	
	/**
	 * Get tasks which are directly assigned to users/roles.
	 * 
	 * @param DNs	can be a orgUserDN or roleDN
	 * @return
	 */
	public static HashMap<String, ArrayList<String>> getAssignedTasksByDN(ArrayList<String> DNs)
	{
		HashMap<String, ArrayList<String>> result = new HashMap<String, ArrayList<String>>();
		
		if (DNs.size() > 0)
		{
			String namespace = "http://schemas.cordys.com/task/1.0/runtime/";
	        String methodName = "GetConfiguredTasks";
	        
	        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, null, null);
	        
	        int response = 0;
	        ArrayList<Integer> paramNodes = new ArrayList<Integer>();
	        try 
	        {
	        	Document xmlDoc = BSF.getXMLDocument();
	        	for (String DN : DNs)
	        	{
		        	int paramNode = xmlDoc.parseString("<ConfiguredTasks dn=\""+DN+"\"/>");
		        	sro.addParameterAsXml(paramNode);
		        	paramNodes.add(paramNode);
	        	}
	            response = sro.execute();
				if (response != 0)
				{
			        int[] resultNodes = XPath.getMatchingNodes(".//tuple/old/ConfiguredTasks", null, response);
			        for (int resultNode : resultNodes) 
			        {
			        	String DN = Node.getAttribute(resultNode, "dn");
			        	ArrayList<String> taskIDs = new ArrayList<String>();
				        int[] taskNodes = XPath.getMatchingNodes("./Task", null, resultNode);
				        for (int taskNode : taskNodes) 
				        {
				        	String taskID = Node.getAttribute(taskNode, "id");
				        	taskIDs.add(taskID);
				        }
				        result.put(DN, taskIDs);
			        }
				}
	        }
	        catch (Exception e)
	        {
	        	throw new CordysException("Not able to read assigned tasks for users/roles ", e);
	        }
			finally
			{
				if (response > 0)
				{
					Node.delete(response);
					response = 0;
				}
				for (int paramNode : paramNodes)
				{
					Node.delete(paramNode);
				}
				paramNodes.clear();
			}
		}
		return result;
	}	
}