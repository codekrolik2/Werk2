package org.werk2.core.config.cycles.dfs;

import java.util.List;

import org.werk2.core.config.cycles.GraphNode;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DFSNode {
	@NonNull protected GraphNode node;
	
	public DFSNode(String name) {
		this.node = new GraphNode(name);
	}

	protected boolean visited = false;
	protected boolean entered = false;

	public List<String> getLinks() {
		return node.getLinks();
	}
	
	public String getName() {
		return node.getName();
	}
	
	@Override
	public String toString() {
		return node.toString();
	}
}
