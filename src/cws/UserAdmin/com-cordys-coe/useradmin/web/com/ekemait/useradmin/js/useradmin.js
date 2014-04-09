/*
 * UserAdmin lib
 */

function getFirstTasksRequest()
{
    if (!system.data.useradmin)
    {
        var useradmin = new Object();
        useradmin.firstTasksRequest = true;
        system.data.useradmin = useradmin;
    }
    if (typeof(system.data.useradmin.firstTasksRequest) == "undefined")
    {
        system.data.useradmin.firstTasksRequest = true;
    }
    return (system.data.useradmin.firstTasksRequest);
}

function setFirstTasksRequest(firstTasksRequest)
{
    if (!system.data.useradmin)
    {
        var useradmin = new Object();
        system.data.useradmin = useradmin;
    }    
    system.data.useradmin.firstTasksRequest = firstTasksRequest;
}

/**
 * Gets the value of a property in a Business Object (like the current bussines object in an XForm model)
 * 
 * @param Business Object (busObject)
 * @param Name of the entity (objectName)
 * @param Name of the property (propName)
 * @return The property value
 */   
function getBOpropertyValue(busObject, objectName, propName)
{
    var propValue = null;
    var propNode = cordys.selectXMLNode(busObject, "//*[local-name()='tuple']/*[local-name()='new']/*[local-name()='"+objectName+"']/*[local-name()='"+propName+"']");
    if (propNode == null)
    {
        propNode = cordys.selectXMLNode(busObject, "//*[local-name()='tuple']/*[local-name()='old']/*[local-name()='"+objectName+"']/*[local-name()='"+propName+"']");
    }
    if (propNode == null)
    {
        propNode = cordys.selectXMLNode(busObject, "//*[local-name()='"+objectName+"']/*[local-name()='"+propName+"']");
    }
    if (propNode == null)
    {
        propNode = cordys.selectXMLNode(busObject, "//*[local-name()='"+propName+"']");
    }    
    if (propNode)
    {
        propValue = cordys.getTextContent(propNode);
    }
    return propValue;
}

/**
 * Sets the value of a property in a Business Object.
 * 
 * @param Business Object (busObject)
 * @param Namespace of the entity
 * @param Name of the entity (objectName)
 * @param Name of the property (propName)
 * @param Value to be set (value)
 */  
function setBOpropertyValue(busObject, boNS, objectName, propName, value)
{
    var propNode = cordys.selectXMLNode(busObject, ".//*[local-name()='"+propName+"']");
    if (!propNode)
    {
        // create xml element
        // busObject can have either tuple/new/<busobject> node or just the <busobject> node
        var boNode = cordys.selectXMLNode(busObject, "//*[local-name()='tuple']//*[local-name()='new']//*[local-name()='"+objectName+"']");
        if (boNode == null)
        {
            boNode = cordys.selectXMLNode(busObject, "//*[local-name()='"+objectName+"']");
        }
        if (boNode)
        {
            var document = boNode.ownerDocument;
            propNode = cordys.createElementNS(document, boNS, propName);
            cordys.appendXMLNode(propNode, boNode);
        }
    }
    if (propNode)
    {
        cordys.setTextContent(propNode, value);
        var nullAttr = propNode.getAttribute("null");
        if (nullAttr != null)
        {
            propNode.removeAttribute("null");
        }
        var nilAttr = propNode.getAttribute("xsi:nil");
        if (nilAttr != null)
        {
            propNode.removeAttribute("xsi:nil");
        }
    }
}