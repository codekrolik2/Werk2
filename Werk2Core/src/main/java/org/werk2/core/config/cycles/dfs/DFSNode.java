package org.werk2.core.config.cycles.dfs;

import org.werk2.core.config.cycles.GraphNode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DFSNode extends GraphNode {
	protected boolean visited = false;
	protected boolean entered = false;

	public DFSNode(GraphNode node) {
		super(node.getName(), node.getLinks());
	}
	
	public DFSNode(String name) {
		super(name);
	}
}
