package org.werk2.core.config.parse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.werk2.config.entities.Event;
import org.werk2.core.config.cycles.GraphNode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GraphNodeWLst extends GraphNode {
	protected Map<Event, List<String>> listeners = new HashMap<>();

	public GraphNodeWLst(GraphNode node) {
		super(node.getName(), node.getLinks());
	}
	
	public GraphNodeWLst(String name) {
		super(name);
	}
}
