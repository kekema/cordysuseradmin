package com.cordys.coe.tools.useradmin.cordys;

import java.util.ArrayList;
import java.util.HashMap;

import com.cordys.coe.tools.useradmin.cordys.exception.CordysException;
import com.cordys.coe.tools.useradmin.exception.UserAdminException;
import com.cordys.coe.tools.useradmin.util.FlatXMLObject;
import com.cordys.coe.tools.useradmin.util.Util;
import com.cordys.cpc.bsf.busobject.BSF;
import com.cordys.cpc.bsf.soap.SOAPRequestObject;
import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;
import com.eibus.xml.xpath.XPath;

/**
 * Class to represent a Cordys team assignment. An instance wraps around an assignment xml.
 * The class abstracts team assignment related functionality in the Cordys platform.
 * 
 * @author kekema
 *
 */
public class TeamAssignment extends CordysObject
{
	private final static String NS_USER_ASSIGNMENT = "http://schemas.cordys.com/userassignment/UserAssignmentService/1.0";
	private static HashMap<String, String> unitNamesByID;
	private FlatXMLObject assignmentXMLObject;				// the underlying xml data for the assignment
	private Document xmlDoc = BSF.getXMLDocument();
	private TeamAssignment newObject = null;				// pointer to any object for new data
	
	/**
	 * Construct new assignment with empty data
	 */
	public TeamAssignment()
	{
		// prepare the top node
		int assignmentNode = xmlDoc.createElement("Assignment");
		Node.setNSDefinition(assignmentNode, null, NS_USER_ASSIGNMENT);
		assignmentXMLObject = new FlatXMLObject(assignmentNode);
	}
	
	/**
	 * Construct team assignment on top of existing xml structure
	 * @param rootNode
	 */
	public TeamAssignment(int rootNode)
	{
		assignmentXMLObject = new FlatXMLObject(rootNode);
	}
	
	/**
	 * Prepare a cloned team assignment object
	 */
	public TeamAssignment clone()
	{
		TeamAssignment newTeamAssignment = new TeamAssignment(Node.clone(assignmentXMLObject.getNOMNode(), true));
		return (newTeamAssignment);
	}
	
	/**
	 * Prepare a team assignment object for new data, cloning initially the existing object,
	 * and point to the new object in the existing one.
	 * 
	 * @return
	 */
	public TeamAssignment prepareNew()
	{
		if (newObject == null)
		{
			newObject = clone();
		}
		return newObject;
	}
	
	/**
	 * Get the new object
	 * 
	 * @return
	 */
	public TeamAssignment getNew()
	{
		return newObject;
	}
	
	/**
	 * Cleanup the underlying nom xml
	 */
	public void cleanup()
	{
		assignmentXMLObject.freeXMLMemory();
		assignmentXMLObject = null;
		if (getNew() != null)
		{
			getNew().cleanup();
		}
	}
	
	/**
	 * Get a copy of the underlying NOM xml
	 * 
	 * @return
	 */
	public int getNOMCopy()
	{
		return Node.clone(assignmentXMLObject.getNOMNode(), true);
	}
	
	/**
	 * Set class variable (hashmap) on unitNamesByID, used in getting a TeamAssignment description.
	 * 
	 * @param unitNamesByID
	 */
	public static void setUnitNamesByID(HashMap<String, String> unitNamesByID)
	{
		TeamAssignment.unitNamesByID = unitNamesByID;
	}
	
	/**
	 * Get the assignment ID
	 * @return
	 */
	public String getID()
	{
		return(assignmentXMLObject.getStringValue("ID", null));
	}
	
	/**
	 * Set the assignment ID
	 * @param ID
	 */
	public void setID(String ID)
	{
		assignmentXMLObject.setStringValue("ID", ID);
	}
	
	public String getDescription()
	{
		if (unitNamesByID == null)
		{
			unitNamesByID = Unit.getUnitNamesByID();
		}
		String unitName = unitNamesByID.get(getUnitID());
		String roleName = Util.getNameFromDN(getRoleDN());
		return (unitName + "/" + roleName);
	}
	
	/**
	 * Get the UnitID for the assignment
	 * 
	 * @return
	 */
	public String getUnitID()
	{
		return(assignmentXMLObject.getStringValue("UnitID", null));
	}
	
	/**
	 * Set the unitID for the assignment
	 * 
	 * @param unitID
	 */
	public void setUnitID(String unitID)
	{
		assignmentXMLObject.setStringValue("UnitID", unitID);
	}
	
	/**
	 * Get the RoleDN for the assignment
	 * 
	 * @return
	 */
	public String getRoleDN()
	{
		return(assignmentXMLObject.getStringValue("RoleDN", null));
	}
	
	/**
	 * Set the RoleDN for the assignment
	 * 
	 * @param roleDN
	 */
	public void setRoleDN(String roleDN)
	{
		assignmentXMLObject.setStringValue("RoleDN", roleDN);
	}
	
	/**
	 * Get the UserDN for the assignment
	 * 
	 * @return
	 */
	public String getUserDN()
	{
		return(assignmentXMLObject.getStringValue("UserDN", null));
	}
	
	/**
	 * Set the userDN for the assignment
	 * 
	 * @param userDN
	 */
	public void setUserDN(String userDN)
	{
		assignmentXMLObject.setStringValue("UserDN", userDN);
	}
	
	/**
	 * Get the isPrincipleUnit flag for the assignment
	 * 
	 * @return
	 */
	public boolean getIsPrincipalUnit()
	{
		return(assignmentXMLObject.getBooleanValue("IsPrincipalUnit", false));
	}
	
	/**
	 * Set the isPrincipleUnit flag for the assignment
	 * 
	 * @param isPrincipalUnit
	 */	
	public void setIsPrincipalUnit(boolean isPrincipalUnit)
	{
		assignmentXMLObject.setBooleanValue("IsPrincipalUnit", isPrincipalUnit);
	}
	
	public String getEffectiveDate()
	{
		return(assignmentXMLObject.getStringValue("EffectiveDate", null));
	}
	
	public void setEffectiveDate(String effectiveDate)
	{
		assignmentXMLObject.setStringValue("EffectiveDate", effectiveDate);
	}
	
	public String getFinishDate()
	{
		return(assignmentXMLObject.getStringValue("FinishDate", null));
	}
	
	public void setFinishDate(String finishDate)
	{
		assignmentXMLObject.setStringValue("FinishDate", finishDate);
	}
	
	/**
	 * Get a tuple/old xml representation for the current assignment
	 * 
	 * @return
	 */
	public int getTupleOld()
	{
		int tupleNode = 0;
		int assignmentNode = assignmentXMLObject.getNOMNode();
		if (assignmentNode > 0)
		{
	    	tupleNode = xmlDoc.createElement("tuple");
	    	int oldNode = xmlDoc.createElement("old", tupleNode);
	    	Node.appendToChildren(Node.clone(assignmentNode, true), oldNode);
		}
	    return tupleNode;	
	}
	
	/**
	 * Get a tuple/new xml representation for the added/updated assignment
	 * 
	 * @return
	 */
	public int getTupleNew()
	{
		int tupleNode = 0;
		int assignmentNode = 0;
		if (getNew() != null)
		{
			assignmentNode = getNew().assignmentXMLObject.getNOMNode();
		}
		else
		{
			assignmentNode = assignmentXMLObject.getNOMNode();
		}
		if (assignmentNode > 0)
		{
	    	tupleNode = xmlDoc.createElement("tuple");
	    	int newNode = xmlDoc.createElement("new", tupleNode);
	    	Node.appendToChildren(Node.clone(assignmentNode, true), newNode);
		}
	    return tupleNode;	
	}
	
	/**
	 * Get a tuple old/new xml representation for the assignment and it's new data
	 * 
	 * @return
	 */
	public int getTupleOldNew()
	{
		int tupleNode = 0;
		if (getNew() != null)
		{
			int assignmentNode = assignmentXMLObject.getNOMNode();
			if (assignmentNode > 0)
			{
		    	tupleNode = xmlDoc.createElement("tuple");
		    	int oldNode = xmlDoc.createElement("old", tupleNode);
		    	Node.appendToChildren(Node.clone(assignmentNode, true), oldNode);
			}
			assignmentNode = getNew().assignmentXMLObject.getNOMNode();
			if (assignmentNode > 0)
			{
		    	int newNode = xmlDoc.createElement("new", tupleNode);
		    	Node.appendToChildren(Node.clone(assignmentNode, true), newNode);
			}			
		}
	    return tupleNode;	
	}
	
	/**
	 * Get list of team assignments for the given user.
	 * 
	 * @param orgUserDN			optional
	 * @param unitID			optional
	 * @param effectiveOnly
	 * @return
	 */
	public static CordysObjectList getAssignments(String orgUserDN, String unitID, boolean effectiveOnly)
	{
		CordysObjectList result = new CordysObjectList();
		
    	String assignmentRoot = CordysUtil.getAssignmentRoot(false, true);

		String namespace = "http://schemas.cordys.com/userassignment/UserAssignmentService/1.0";
        String methodName = "GetAssignments";
        String[] paramNames = new String[] { "WorkspaceID", "AssignmentRoot", "UnitID", "UserDN", "EffectiveOnly" };
        Object[] paramValues = new Object[] { "__Organization Staging__", assignmentRoot, unitID, orgUserDN, Boolean.toString(effectiveOnly) };
        
        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, paramNames, paramValues);

        int response = 0;
        try
        {
        	Util.setRequestUserToAdministrator(sro);
        	response = sro.execute();
			if (response != 0)
			{
		        int[] assignmentNodes = XPath.getMatchingNodes(".//dataset/tuple/old/Assignment", null, response);
		        for (int assignmentNode : assignmentNodes) 
		        {
		        	// wrap the assignment xml in a new teamAssignment object
		        	TeamAssignment teamAssignment = new TeamAssignment(Node.unlink(assignmentNode));
		        	result.add(teamAssignment);
		        }
			}
        }
        catch (Exception e)
        {
        	throw new CordysException("Not able to read teamassignments", e);
        }
		finally
		{
			if (response != 0)
			{
				Node.delete(response);
				response = 0;
			}
		}
        result.sort();
		return result;
	}
	
	/**
	 * Check if there are existing assignments for the given user.
	 * 
	 * @param orgUserDN
	 * @param effectiveOnly
	 * @return
	 */
	public static boolean assignmentsExist(String orgUserDN, boolean effectiveOnly)
	{
		boolean result = false;
    	String assignmentRoot = CordysUtil.getAssignmentRoot(false, true);

		String namespace = "http://schemas.cordys.com/userassignment/UserAssignmentService/1.0";
        String methodName = "GetAssignments";
        String[] paramNames = new String[] { "WorkspaceID", "AssignmentRoot", "UserDN", "EffectiveOnly" };
        Object[] paramValues = new Object[] { "__Organization Staging__", assignmentRoot, orgUserDN, Boolean.toString(effectiveOnly) };
        
        SOAPRequestObject sro = new SOAPRequestObject(namespace, methodName, paramNames, paramValues);

        int response = 0;
        try
        {
        	response = sro.execute();
			if (response != 0)
			{
		        int[] assignmentNodes = XPath.getMatchingNodes(".//dataset/tuple/old/Assignment", null, response);
		        result = (assignmentNodes.length > 0);
			}
        }
        catch (Exception e)
        {
        	throw new CordysException("Not able to check for existing teamassignments for " + orgUserDN, e);
        }
		finally
		{
			if (response != 0)
			{
				Node.delete(response);
				response = 0;
			}
		}
        
		return result;
	}
	
	/**
	 * Add the given teamAssignments to the user.
	 * 
	 * @param orgUserDN
	 * @param teamAssignments
	 */
	public static void addAssignments(String orgUserDN, ArrayList<CordysObject> teamAssignments)
	{
		String assignmentRoot = CordysUtil.getAssignmentRoot(false, true);
		Document xmlDoc = BSF.getXMLDocument();
		
    	String namespace = "http://schemas.cordys.com/userassignment/UserAssignmentService/1.0";
    	String methodName = "AddAssignmentsWithForcePrincipal";
    	String[] paramNames = new String[] { "WorkspaceID", "AssignmentRoot", "DoPublish", "ForceSetPrincipal" };
        Object[] paramValues = new Object[] { "__Organization Staging__", assignmentRoot, "true", "true" };
        int response = 0;
        int addAssignmentsNode = 0;
        try
        {
			addAssignmentsNode = xmlDoc.parseString("<Assignments><dataset/></Assignments>");
			int datasetNode = XPath.getFirstMatch(".//dataset", null, addAssignmentsNode);
			// add the teamAssignments as tuple/new's
	        for (CordysObject cordysObject : teamAssignments)
	        {
	        	TeamAssignment teamAssignment = (TeamAssignment)cordysObject;
	        	Node.appendToChildren(teamAssignment.getTupleNew(), datasetNode);
	        }
	        SOAPRequestObject sroAdd = new SOAPRequestObject(namespace, methodName, paramNames, paramValues);
	        sroAdd.addParameterAsXml(addAssignmentsNode);		
	        response = sroAdd.execute();
        }
        catch (Exception e)
        {
        	throw new CordysException("Not able to insert team assignments for user " + orgUserDN, e);
        }
        finally
        {
			if (response != 0)
			{
				Node.delete(response);
				response = 0;
			}
			if (addAssignmentsNode != 0)
			{
				Node.delete(addAssignmentsNode);
				addAssignmentsNode = 0;
			}
        }
	}
	
	/**
	 * Delete all team assignments for given user.
	 * 
	 * @param orgUserDN
	 */
	public static void deleteAllAssignmentsForUser(String orgUserDN)
	{
		String assignmentRoot = CordysUtil.getAssignmentRoot(false, true);
        Document xmlDoc = BSF.getXMLDocument();
        
        int removeAssignmentsNode = 0;
        SOAPRequestObject sroRemove = null;
        CordysObjectList existingTeamAssignments = null;
        try
        {
	    	existingTeamAssignments = TeamAssignment.getAssignments(orgUserDN, "", false);
	    	if (existingTeamAssignments.getList().size() > 0)
	    	{
		        try
		        {
			    	// RemoveAssignments soap request
			    	String namespace = "http://schemas.cordys.com/userassignment/UserAssignmentService/1.0";
			        String methodName = "RemoveAssignments";
			        String[] paramNames = new String[] { "WorkspaceID", "AssignmentRoot", "DoPublish" };
			        Object[] paramValues = new Object[] { "__Organization Staging__", assignmentRoot, "true" };
			
			        removeAssignmentsNode = xmlDoc.parseString("<Assignments><dataset/></Assignments>");
			        int datasetNode = XPath.getFirstMatch(".//dataset", null, removeAssignmentsNode);
			    	for (CordysObject cordysObject : existingTeamAssignments.getList())
			    	{
			    		TeamAssignment teamAssignment = (TeamAssignment)cordysObject;
			        	Node.appendToChildren(teamAssignment.getTupleOld(), datasetNode);
			        }
			        sroRemove = new SOAPRequestObject(namespace, methodName, paramNames, paramValues);
			        sroRemove.addParameterAsXml(removeAssignmentsNode);	
			        sroRemove.execute();
		        }
		        catch (Exception e)
		        {
		        	throw new UserAdminException("Not able to add/remove teamassignments for " + Util.getNameFromDN(orgUserDN), e);
		        }
				finally
				{
					if (sroRemove != null)
					{
						int response = sroRemove.getResult();
						if (response > 0)
						{
							Node.delete(response);
							response = 0;
						}
					}
					if (removeAssignmentsNode > 0)
					{
						Node.delete(removeAssignmentsNode);
						removeAssignmentsNode = 0;
					}  
				}
	    	}
        }
        finally
        {
    		// teamAssignments do wrap NOM xml structures, so cleanup
    		if (existingTeamAssignments != null)
    		{
    			existingTeamAssignments.cleanup();
    		}
        }
	}
}