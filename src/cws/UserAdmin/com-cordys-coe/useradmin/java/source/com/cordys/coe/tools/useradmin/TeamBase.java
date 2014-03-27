/*
  This class has been generated by the Code Generator
*/

package com.cordys.coe.tools.useradmin;

import com.cordys.cpc.bsf.busobject.BusObjectConfig;
import com.cordys.cpc.bsf.classinfo.AttributeInfo;
import com.cordys.cpc.bsf.classinfo.ClassInfo;
import com.cordys.cpc.bsf.classinfo.RelationInfo_Composite;


public abstract class TeamBase extends com.cordys.cpc.bsf.busobject.CustomBusObject
{
    // tags used in the XML document
    public final static String ATTR_UnitID = "UnitID";
    public final static String ATTR_UnitQN = "UnitQN";
    public final static String ATTR_IsPrinciple = "IsPrinciple";
    private final static String REL_Role = "AGGR:Role";
    private static ClassInfo s_classInfo = null;
    public static ClassInfo _getClassInfo()//NOPMD framework ensures this is thread safe
    {
        if ( s_classInfo == null )//NOPMD
        {
            s_classInfo = newClassInfo(Team.class);
            s_classInfo.setUIDElements(new String[]{});
            {
                AttributeInfo ai = new AttributeInfo(ATTR_UnitID);
                ai.setJavaName(ATTR_UnitID);
                ai.setAttributeClass(String.class);
                s_classInfo.addAttributeInfo(ai);
            }
            {
                AttributeInfo ai = new AttributeInfo(ATTR_UnitQN);
                ai.setJavaName(ATTR_UnitQN);
                ai.setAttributeClass(String.class);
                s_classInfo.addAttributeInfo(ai);
            }
            {
                AttributeInfo ai = new AttributeInfo(ATTR_IsPrinciple);
                ai.setJavaName(ATTR_IsPrinciple);
                ai.setAttributeClass(boolean.class);
                s_classInfo.addAttributeInfo(ai);
            }
            {
                RelationInfo_Composite ri = new RelationInfo_Composite(REL_Role);
                ri.setName("Role");
                ri.setMultiOcc(false);
                ri.setRelatedClass(com.cordys.coe.tools.useradmin.Role.class);
                s_classInfo.addRelationInfo(ri);
            }
        }
        return s_classInfo;
    }

    public TeamBase(BusObjectConfig config)
    {
        super(config);
    }

    public String getUnitID()
    {
        return getStringProperty(ATTR_UnitID);
    }

    public void setUnitID(String value)
    {
        setProperty(ATTR_UnitID, value, 0);
    }

    public String getUnitQN()
    {
        return getStringProperty(ATTR_UnitQN);
    }

    public void setUnitQN(String value)
    {
        setProperty(ATTR_UnitQN, value, 0);
    }

    public boolean getIsPrinciple()
    {
        return getBooleanProperty(ATTR_IsPrinciple);
    }

    public void setIsPrinciple(boolean value)
    {
        setProperty(ATTR_IsPrinciple, value, 0);
    }

    public Role getRoleObject()
    {
        return (Role)getSingleRelationObject(REL_Role);
    }

    public Role setRoleObject(Role a_Role)
    {
        return(Role)_getSingleRelation(REL_Role, true).setLocalObject(a_Role);
    }

}