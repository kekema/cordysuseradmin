package com.cordys.coe.tools.useradmin.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import com.cordys.coe.tools.useradmin.cordys.CordysObject;
import com.cordys.coe.tools.useradmin.cordys.CordysObjectList;
import com.cordys.coe.tools.useradmin.cordys.CordysUtil;
import com.cordys.coe.tools.useradmin.cordys.Role;
import com.cordys.coe.tools.useradmin.cordys.TeamAssignment;
import com.cordys.coe.tools.useradmin.cordys.Unit;
import com.cordys.coe.tools.useradmin.cordys.exception.CordysValidationException;
import com.cordys.coe.tools.useradmin.exception.UserAdminException;
import com.cordys.coe.tools.useradmin.util.Util;
import com.cordys.cpc.bsf.busobject.BSF;
import com.cordys.cpc.bsf.busobject.BusObjectConfig;
import com.cordys.cpc.bsf.busobject.BusObjectIterator;
import com.cordys.cpc.bsf.soap.SOAPRequestObject;
import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;
import com.eibus.xml.xpath.XPath;


/**
 * Class to support UI on assigning a user to teams (via roles).
 * It includes the actual assignment plus the potential additional assignments.
 * So in one tuple, it has one list of team/role combinations, for each combination a 
 * flag to indicate whether the user has been assigned that team/role.
 * 
 * @author kekema
 *
 */
public class UIUserTeams extends UIUserTeamsBase
{
	private boolean readBackOnUpdate = true;		// whether to readback the actual content after updating
													// can be set to false for batch processes
	
    public UIUserTeams()
    {
        this((BusObjectConfig)null);
    }

    public UIUserTeams(BusObjectConfig config)
    {
        super(config);
    }
    
	/**
	 * set readBack optoin
	 * 
	 * @param readBackOnUpdate
	 */
	public void setReadBackOnUpdate(boolean readBackOnUpdate)
	{
		this.readBackOnUpdate = readBackOnUpdate;
	}

    /**
     * Get actual assignments plus additional team/role combinations from which new assignments can be made.
     * 
     * @param orgUserDN
     * @return
     */
    public static UIUserTeams getUIUserTeams(String orgUserDN)
    {
    	//orgUserDN = "cn=cordysadm,cn=organizational users,o=EkemaIT,cn=cordys,cn=defaultInst,o=Cordys";
    	UIUserTeams uiUserTeams = new UIUserTeams();
    	uiUserTeams.makeTransient();
    	uiUserTeams.setOrgUserDN(orgUserDN);
    	UIUserTeams.TeamRoles teamRoles = new UIUserTeams.TeamRoles();
    	uiUserTeams.setTeamRolesObject(teamRoles);
    	
    	HashSet<String> currentAssignments = new HashSet<String>();
    	CordysObjectList allUnits = null;
    	try
    	{
	    	allUnits = Unit.getAllUnits();
	    	HashMap<String, String> unitNamesByID = Unit.getUnitNamesByID(allUnits);
	    	HashMap<String, String> unitQNamesByID = Unit.getUnitQNamesByID(allUnits);
	    	TeamAssignment.setUnitNamesByID(unitNamesByID);
	    	
	    	// get current assignments
	    	CordysObjectList teamAssignments = null;
	    	int seqNo = 0;
	    	try
	    	{
		    	teamAssignments = TeamAssignment.getAssignments(orgUserDN, "", true);
		    	for (CordysObject cordysObject : teamAssignments.getList())
		    	{
		    		TeamAssignment teamAssignment = (TeamAssignment)cordysObject;
		    		// the seqNo sequence number can be used client side as an index to the team/role
		    		seqNo++;
		    		UIUserTeams.TeamRoles.TeamRole teamRole = new UIUserTeams.TeamRoles.TeamRole();
		    		teamRole.makeTransient();
		    		teamRole.setSeqNo(seqNo);
		    		String unitID = teamAssignment.getUnitID();
		    		teamRole.setUnitID(unitID);
		    		// unit name can be used client side to compose the list of units for principal unit selection
		    		String unitName = unitNamesByID.get(unitID);
		    		teamRole.setUnitName(unitName);
		    		String unitQName = unitQNamesByID.get(unitID);
		    		teamRole.setUnitQName(unitQName);
		    		String roleDN = teamAssignment.getRoleDN();
		    		teamRole.setRoleDN(roleDN);
		    		teamRole.setAssigned(true);
		    		teamRole.setAssignmentID(teamAssignment.getID());
		    		teamRole.setDescription(teamAssignment.getDescription());
		    		boolean isPrincipalUnit = teamAssignment.getIsPrincipalUnit();
		    		if (isPrincipalUnit)
		    		{
		    			uiUserTeams.setPrincipleUnitID(unitID);
		    		}
		    		teamRoles.addTeamRoleObject(teamRole);
		    		currentAssignments.add(unitID+roleDN);
		    	}
	    	}
	    	finally
	    	{
	    		// teamAssignments do wrap NOM xml structures, so cleanup
	    		if (teamAssignments != null)
	    		{
	    	    	teamAssignments.cleanup();
	    		}
	    	}
	    	
	    	// get user roles
	       	HashSet<String> userRoles = new HashSet<String>(Role.getAssignedRoles(orgUserDN));
	        
	        // for each unit/role combination which is not assigned yet and for which the role is 
	        // assigned to the user, add that combination as not assigned (potential new assignment in UI)
	        for (CordysObject cordysObject : allUnits.getList())
	        {
	        	Unit unit = (Unit)cordysObject;
	        	String unitID = unit.getID();
	        	
	        	ArrayList<String> unitRoles = Unit.getUnitRoles(unitID);
	        	for (String roleDN : unitRoles)
	        	{
	        		if (userRoles.contains(roleDN) && (!(currentAssignments.contains(unitID+roleDN))))
	        		{
	            		seqNo++;
	            		UIUserTeams.TeamRoles.TeamRole teamRole = new UIUserTeams.TeamRoles.TeamRole();
	            		teamRole.makeTransient();
	            		teamRole.setSeqNo(seqNo);
	            		teamRole.setUnitID(unitID);
	            		String unitName = unitNamesByID.get(unitID);
	            		teamRole.setUnitName(unitName);
	            		String unitQName = unitQNamesByID.get(unitID);
	            		teamRole.setUnitQName(unitQName);
	            		teamRole.setRoleDN(roleDN);
	            		teamRole.setAssigned(false);
	            		String roleName = Util.getNameFromDN(roleDN);
	            		teamRole.setDescription(unitName + "/" + roleName);
	            		teamRoles.addTeamRoleObject(teamRole);
	        		}
	        	}
	        }
    	}
    	finally
    	{
    		if (allUnits != null)
    		{
    			allUnits.cleanup();
    		}    		
    	}
        return uiUserTeams;
    }

    public void onInsert()
    {
    	// N.A.
    }
    
    /**
     * From the revised assignments, execute the required deletes, additions and updates to the user assignments.
     */
    public void onUpdate()
    {
    	String orgUserDN = this.getOrgUserDN();
    	String assignmentRoot = CordysUtil.getAssignmentRoot(false, true);
        Document xmlDoc = BSF.getXMLDocument();
        
        boolean assignmentsToBeAdded = false;
        boolean assignmentsToBeRemoved = false;
        boolean assignmentsToBeUpdated = false;
        
        HashMap<String, UIUserTeams.TeamRoles.TeamRole> teamRolesByAssignmentID = new HashMap<String, UIUserTeams.TeamRoles.TeamRole>();
        HashMap<String, UIUserTeams.TeamRoles.TeamRole> teamRolesByUnitRole = new HashMap<String, UIUserTeams.TeamRoles.TeamRole>();
        HashSet<String> existingAssignmentsByUnitRole = new HashSet<String>();
        
        ArrayList<TeamAssignment> assignmentsForDelete = new ArrayList<TeamAssignment>();
        ArrayList<TeamAssignment> assignmentsForAddition = new ArrayList<TeamAssignment>();
        ArrayList<TeamAssignment> assignmentsForUpdate = new ArrayList<TeamAssignment>();
        
        // find out which assignments to be deleted, added, updated.
        CordysObjectList existingTeamAssignments = null;
        try
        {
	        if (this.getTeamRolesObject() != null)
	        {
	        	TeamAssignment currentPrincipleUnitAssignment = null;
	        	TeamAssignment newPrincipleUnitAssignment = null;
	        	TeamAssignment addedPrincipleUnitAssignment = null;
	        	String currentPrincipleUnitID = null;
	        	String newPrincipleUnitID = this.getPrincipleUnitID();
	        	// compose hashset for existing assignments
    	    	existingTeamAssignments = TeamAssignment.getAssignments(orgUserDN, "", true);
    	    	for (CordysObject cordysObject : existingTeamAssignments.getList())
    	    	{
    	    		TeamAssignment teamAssignment = (TeamAssignment)cordysObject;
		        	String unitID = teamAssignment.getUnitID();
		        	String roleDN = teamAssignment.getRoleDN();
		        	existingAssignmentsByUnitRole.add(unitID+roleDN);
		        	boolean isPrincipleUnit = teamAssignment.getIsPrincipalUnit();
		        	if ((currentPrincipleUnitAssignment == null) && isPrincipleUnit)
		        	{
		        		currentPrincipleUnitAssignment = teamAssignment;
		        		currentPrincipleUnitID = unitID;
		        	}
		        	if ((newPrincipleUnitAssignment == null) && (unitID.equals(newPrincipleUnitID) && (!isPrincipleUnit)))
		        	{
		        		newPrincipleUnitAssignment = teamAssignment;
		        	}
		        }
    	    	HashSet<String> assignedTeams = new HashSet<String>();
	        	// iterate the inner teamroles as to fill hashmaps and to
	        	// compose list of assignments to be added
		        BusObjectIterator<UIUserTeams.TeamRoles.TeamRole> teamRoles = this.getTeamRolesObject().getTeamRoleObjects();
		        while (teamRoles.hasMoreElements())
		        {
		        	UIUserTeams.TeamRoles.TeamRole teamRole = teamRoles.nextElement();
		        	String assignmentID = teamRole.getAssignmentID();
		        	if (Util.isSet(assignmentID))
		        	{
		        		teamRolesByAssignmentID.put(assignmentID, teamRole);	
		        	}
		        	String unitID = teamRole.getUnitID();
		        	String roleDN = teamRole.getRoleDN();
		        	teamRolesByUnitRole.put(unitID+roleDN, teamRole);
		        	// check if to be added
		        	boolean assigned = teamRole.getAssigned();
		        	if (assigned)
		        	{
		        		assignedTeams.add(unitID);
			        	if (!Util.isSet(assignmentID))
			        	{
			        		if (!existingAssignmentsByUnitRole.contains(unitID+roleDN))
			        		{
			        			TeamAssignment newTeamAssignment = new TeamAssignment();
			        			newTeamAssignment.setUnitID(unitID);
			        			newTeamAssignment.setRoleDN(roleDN);
			        			newTeamAssignment.setUserDN(orgUserDN);
			        			newTeamAssignment.setIsPrincipalUnit(false);
				        		assignmentsForAddition.add(newTeamAssignment);
				        		assignmentsToBeAdded = true;
				        		if (unitID.equals(newPrincipleUnitID))
				        		{
				        			addedPrincipleUnitAssignment = newTeamAssignment;
				        		}
			        		}
			        	}
		        	}
		        }
		        
		        if (!Util.isSet(this.getPrincipleUnitID()))
		        {
		        	if (assignedTeams.size() > 1)
		        	{
		        		throw new CordysValidationException("Cannot update team assignments for user " + Util.getNameFromDN(orgUserDN) + " - Principle Unit not set");
		        	}
		        }
	        
		        // compose list of which ones to be removed
    	    	for (CordysObject cordysObject : existingTeamAssignments.getList())
    	    	{
    	    		TeamAssignment teamAssignment = (TeamAssignment)cordysObject;
    	    		String id = teamAssignment.getID();
		        	String unitID = teamAssignment.getUnitID();
		        	String roleDN = teamAssignment.getRoleDN();
		        	UIUserTeams.TeamRoles.TeamRole teamRole = teamRolesByAssignmentID.get(id);
		        	if (teamRole == null)
		        	{
		        		// timeout situation (as long as no check for 'tuple changed by other user')
		        		teamRole = teamRolesByUnitRole.get(unitID+roleDN);
		        	}
		        	boolean stillAssigned = false;
		        	if (teamRole != null)
		        	{
		        		stillAssigned = (teamRole.getAssigned());
		        	}
		        	if (!stillAssigned)
		        	{
		        		if (teamAssignment == currentPrincipleUnitAssignment)
		        		{
		        			currentPrincipleUnitAssignment = null;
		        		}
		        		if (teamAssignment == newPrincipleUnitAssignment)
		        		{
		        			newPrincipleUnitAssignment = null;
		        		}
		        		assignmentsForDelete.add(teamAssignment);
		        		assignmentsToBeRemoved = true;
		        	}
		        }
		        
		        // determine any change wrt the principle unit
		        if ((currentPrincipleUnitAssignment != null) && (currentPrincipleUnitID.equals(newPrincipleUnitID)))
		        {
		        	// no action
		        }
		        else
		        {
		        	if (currentPrincipleUnitAssignment != null)
		        	{
		        		TeamAssignment newObject = currentPrincipleUnitAssignment.prepareNew();
		        		newObject.setIsPrincipalUnit(false);
		        		assignmentsForUpdate.add(currentPrincipleUnitAssignment);
		        		assignmentsToBeUpdated = true;
		        	}
	        		if (Util.isSet(newPrincipleUnitID))
	        		{
	        			if (newPrincipleUnitAssignment != null)
	        			{
			        		TeamAssignment newObject = newPrincipleUnitAssignment.prepareNew();
			        		newObject.setIsPrincipalUnit(true);
			        		assignmentsForUpdate.add(newPrincipleUnitAssignment);
			        		assignmentsToBeUpdated = true;
	        			}
	        			else if (addedPrincipleUnitAssignment != null)
		        		{
	        				addedPrincipleUnitAssignment.setIsPrincipalUnit(true);
		        		}				        			
	        		}
		        }

		        // compose the soap requests
		        int removeAssignmentsNode = 0;
		        int addAssignmentsNode = 0;
		        int updateAssignmentsNode = 0;
		        SOAPRequestObject sroRemove = null;
		        SOAPRequestObject sroAdd = null;
		        SOAPRequestObject sroUpdate = null;
		        try
		        {
			        if (assignmentsToBeRemoved)
			        {
			        	// RemoveAssignments soap request
			        	String namespace = "http://schemas.cordys.com/userassignment/UserAssignmentService/1.0";
				        String methodName = "RemoveAssignments";
				        String[] paramNames = new String[] { "WorkspaceID", "AssignmentRoot", "DoPublish" };
				        Object[] paramValues = new Object[] { "__Organization Staging__", assignmentRoot, "true" };

				        removeAssignmentsNode = xmlDoc.parseString("<Assignments><dataset/></Assignments>");
				        int datasetNode = XPath.getFirstMatch(".//dataset", null, removeAssignmentsNode);
				        for (TeamAssignment teamAssignment : assignmentsForDelete)
				        {
				        	Node.appendToChildren(teamAssignment.getTupleOld(), datasetNode);
				        }
				        sroRemove = new SOAPRequestObject(namespace, methodName, paramNames, paramValues);
				        sroRemove.addParameterAsXml(removeAssignmentsNode);
			        }
			        if (assignmentsToBeAdded)
			        {
			        	String namespace = "http://schemas.cordys.com/userassignment/UserAssignmentService/1.0";
			        	String methodName = "AddAssignmentsWithForcePrincipal";
			        	String[] paramNames = new String[] { "WorkspaceID", "AssignmentRoot", "DoPublish", "ForceSetPrincipal" };
				        Object[] paramValues = new Object[] { "__Organization Staging__", assignmentRoot, "true", "true" };

			    		addAssignmentsNode = xmlDoc.parseString("<Assignments><dataset/></Assignments>");
			    		int datasetNode = XPath.getFirstMatch(".//dataset", null, addAssignmentsNode);
				        for (TeamAssignment teamAssignment : assignmentsForAddition)
				        {
				        	Node.appendToChildren(teamAssignment.getTupleNew(), datasetNode);
				        }
				        sroAdd = new SOAPRequestObject(namespace, methodName, paramNames, paramValues);
				        sroAdd.addParameterAsXml(addAssignmentsNode);
			        }
			        if (assignmentsToBeUpdated)
			        {
			        	String namespace = "http://schemas.cordys.com/userassignment/UserAssignmentService/1.0";
			        	String methodName = "UpdateAssignmentsWithForcePrincipal";
			        	String[]  paramNames = new String[] { "WorkspaceID", "AssignmentRoot", "DoPublish", "ForceSetPrincipal" };
				        Object[] paramValues = new Object[] { "__Organization Staging__", assignmentRoot, "true", "true" };

			    		updateAssignmentsNode = xmlDoc.parseString("<Assignments><dataset/></Assignments>");
			    		int datasetNode = XPath.getFirstMatch(".//dataset", null, updateAssignmentsNode);
				        for (TeamAssignment teamAssignment : assignmentsForUpdate)
				        {
				        	Node.appendToChildren(teamAssignment.getTupleOldNew(), datasetNode);
				        }
				        sroUpdate= new SOAPRequestObject(namespace, methodName, paramNames, paramValues);
				        sroUpdate.addParameterAsXml(updateAssignmentsNode);
			        }				        
			        if (assignmentsToBeAdded && (assignmentsToBeRemoved || assignmentsToBeUpdated))
			        {
			        	// one soap transaction
			        	if (assignmentsToBeUpdated)
			        	{
			        		sroAdd.chain(sroUpdate);
			        	}
			        	if (assignmentsToBeRemoved)
			        	{
			        		sroAdd.chain(sroRemove);
			        	}
			        	sroAdd.execute();
			        }
			        else if (assignmentsToBeUpdated && assignmentsToBeRemoved)
			        {
			        	// one soap transaction
			        	sroUpdate.chain(sroRemove);
			        	sroUpdate.execute();
			        }
			        else if (assignmentsToBeRemoved)
			        {
			        	sroRemove.execute();
			        }
			        else if (assignmentsToBeAdded)
			        {
			        	sroAdd.execute();
			        }
			        else if (assignmentsToBeUpdated)
			        {
			        	sroUpdate.execute();
			        }
			        if (readBackOnUpdate)
			        {
				        // read back the actual content
				        UIUserTeams uiUserTeamsDB = UIUserTeams.getUIUserTeams(orgUserDN);
				        if (uiUserTeamsDB != null)
				        {
				        	UIUserTeams.TeamRoles teamRolesDB= uiUserTeamsDB.getTeamRolesObject();
				        	UIUserTeams.TeamRoles teamRolesUpdated = new UIUserTeams.TeamRoles(new BusObjectConfig(teamRolesDB, BusObjectConfig.TRANSIENT_OBJECT));
				        	this.setTeamRolesObject(teamRolesUpdated);
				        	this.setPrincipleUnitID(uiUserTeamsDB.getPrincipleUnitID());
				        }
			        }
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
					if (sroAdd != null)
					{
						int response = sroAdd.getResult();
						if (response > 0)
						{
							Node.delete(response);
							response = 0;
						}
					}	
					if (sroUpdate != null)
					{
						int response = sroUpdate.getResult();
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
					if (addAssignmentsNode > 0)
					{
						Node.delete(addAssignmentsNode);
						addAssignmentsNode = 0;
					}	
					if (updateAssignmentsNode > 0)
					{
						Node.delete(updateAssignmentsNode);
						updateAssignmentsNode = 0;
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
    		// assignmentsForDelete/assignmentsForUpdate: no need to cleanup as those assignment objects are a (sub)set of existingTeamAssignments
	        for (TeamAssignment teamAssignment : assignmentsForAddition)
	        {
	        	teamAssignment.cleanup();
	        }
        }
    }
    
    public void onDelete()
    {
    	// N.A.
    }

    /**
     * Inner class for team roles
     *
     */
    public static class TeamRoles extends UIUserTeams.TeamRolesBase
    {
        public TeamRoles()
        {
            this((BusObjectConfig)null);
        }

        public TeamRoles(BusObjectConfig config)
        {
            super(config);
        }

        public void onInsert()
        {
        	// N.A.
        }

        public void onUpdate()
        {
        	// N.A.
        }

        public void onDelete()
        {
        	// N.A.
        }

        /**
         * Inner class for team role
         *
         */
        public static class TeamRole extends UIUserTeams.TeamRoles.TeamRoleBase
        {
            public TeamRole()
            {
                this((BusObjectConfig)null);
            }

            public TeamRole(BusObjectConfig config)
            {
                super(config);
            }

            public void onInsert()
            {
            	// N.A.
            }

            public void onUpdate()
            {
            	// N.A.
            }

            public void onDelete()
            {
            	// N.A.
            }

        }
    }
}
