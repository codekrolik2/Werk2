package org.werk2.core.config.cycles;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import lombok.Getter;

@Getter
public class PathStructure {
	protected Set<String> nodeSet = new HashSet<>();
	protected Deque<String> nodeOrder = new LinkedList<>();
	
	public boolean addNode(String node) {
		nodeOrder.add(node);
		return nodeSet.add(node);
	}

	@Override
	public String toString() {
		StringBuilder path = new StringBuilder();
		while (!nodeOrder.isEmpty()) {
			path.append(nodeOrder.pollLast());
			if (!nodeOrder.isEmpty())
				path.append("->");
		}
		
		
		return path.toString();
	}
}
