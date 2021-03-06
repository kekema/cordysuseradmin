/*
  This class has been generated by the Code Generator
*/

package com.cordys.coe.tools.useradmin;

import com.cordys.cpc.bsf.busobject.BusObjectConfig;
import com.cordys.cpc.bsf.busobject.BusObjectIterator;
import com.cordys.cpc.bsf.classinfo.ClassInfo;
import com.cordys.cpc.bsf.classinfo.RelationInfo_Composite;


public abstract class TeamsBase extends com.cordys.cpc.bsf.busobject.CustomBusObject
{
    // tags used in the XML document
    private final static String REL_Team = "AGGR:Team";
    private static ClassInfo s_classInfo = null;
    public static ClassInfo _getClassInfo()//NOPMD framework ensures this is thread safe
    {
        if ( s_classInfo == null )//NOPMD
        {
            s_classInfo = newClassInfo(Teams.class);
            s_classInfo.setUIDElements(new String[]{});
            {
                RelationInfo_Composite ri = new RelationInfo_Composite(REL_Team);
                ri.setName("Team");
                ri.setMultiOcc(true);
                ri.setRelatedClass(com.cordys.coe.tools.useradmin.Team.class);
                s_classInfo.addRelationInfo(ri);
            }
        }
        return s_classInfo;
    }

    public TeamsBase(BusObjectConfig config)
    {
        super(config);
    }

    public BusObjectIterator<Team> getTeamObjects()
    {
        return getMultiRelationObjects(REL_Team);
    }

    public Team addTeamObject(Team a_Team)
    {
        return (Team)_getMultiRelation(REL_Team, true).addObject(a_Team);
    }

    public void removeTeamObject(Team a_Team)
    {
        _getMultiRelation(REL_Team, true).removeObject(a_Team);
    }

}
