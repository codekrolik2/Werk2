package org.werk2.core.config.cycles.dfs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.javatuples.Pair;
import org.werk2.common.WerkConfigException;
import org.werk2.core.config.cycles.GraphNode;

public class DFSCycleFinder {
	protected Pair<Boolean, Boolean> findCycle(PathStructure path, Map<String, DFSNode> vertexMap, DFSNode v) throws WerkConfigException {
		if (v.visited)
			return new Pair<>(false, false);
		
		v.entered = true;
		
		for (String nextName : v.getLinks()) {
			DFSNode next = vertexMap.get(nextName);
			if (next == null) {
				throw new WerkConfigException(
						String.format("Vertex not found: name [%s]", v.getName())
					);
			} else {
				if (!next.visited) {
					if (next.entered) {
						//cycle found at this point
						path.addNode(next.getName());
						return new Pair<>(true, !path.addNode(v.getName()));
					} else {
						Pair<Boolean, Boolean> res = findCycle(path, vertexMap, next);
						if (res.getValue0()) {
							if (res.getValue1()) {
								return res;
							} else {
								return new Pair<>(true, !path.addNode(v.getName()));
							}
						}
					}
				}	
			}
		}
		
		v.visited = true;
		
		return new Pair<>(false, false);
	}

	public Optional<PathStructure> findCyclePath(Iterable<GraphNode> graph) throws WerkConfigException {
		List<DFSNode> list = new ArrayList<>();
		for (GraphNode graphNode : graph)
			list.add(new DFSNode(graphNode));
		return findCyclePath0(list);
	}
	
	protected Optional<PathStructure> findCyclePath0(Iterable<DFSNode> graph) throws WerkConfigException {
		Map<String, DFSNode> vertexMap = new HashMap<>();
		for (DFSNode v : graph)
			vertexMap.put(v.getName(), v);
		
		PathStructure path = new PathStructure();
		for (DFSNode v : graph)
			if (findCycle(path, vertexMap, v).getValue0())
				return Optional.of(path);
			
		return Optional.empty();
	}
	
	public void validate(List<GraphNode> graph) throws WerkConfigException {
		List<DFSNode> list = new ArrayList<>();
		for (GraphNode graphNode : graph)
			list.add(new DFSNode(graphNode));
		validate0(list);
	}

	protected void validate0(List<DFSNode> graph) throws WerkConfigException {
		Optional<PathStructure> path = findCyclePath0(graph);
		
		if (!path.isEmpty())
			throw new WerkConfigException(
				String.format("Call indirection graph: cycle detected: [%s]", path.get().toString())	
			);
	}
}
