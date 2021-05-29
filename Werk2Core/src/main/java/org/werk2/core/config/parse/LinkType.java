package org.werk2.core.config.parse;

public enum LinkType {
	PROJECTED_LISTENER,
	OWN_LISTENER,
	PROJECTED_CALL,
	NEW_CONTEXT_CALL;
	
    public String value() {
        if (equals(LinkType.PROJECTED_LISTENER))
        	return "-.-x";
        else if (equals(LinkType.PROJECTED_CALL))
        	return "==>";
        else if (equals(LinkType.OWN_LISTENER))
        	return "-.-o";
        else //if (equals(LinkType.NEW_CONTEXT_CALL))
        	return "--x";
    }

    public String legend() {
        if (equals(LinkType.PROJECTED_LISTENER))
        	return "ProjectedListenerCall";
        else if (equals(LinkType.PROJECTED_CALL))
        	return "CallWithProjection";
        else if (equals(LinkType.OWN_LISTENER))
        	return "OwnListenerCall";
        else //if (equals(LinkType.NEW_CONTEXT_CALL))
        	return "CallInNewProjectionContext";
    }
}
