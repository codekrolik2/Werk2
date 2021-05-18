package org.werk2.core.config.cycles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.javatuples.Pair;
import org.werk2.common.WerkConfigException;

public class CycleFinder {
	protected Pair<Boolean, Boolean> findCycle(PathStructure path, Map<String, GraphNode> vertexMap, GraphNode v) throws WerkConfigException {
		if (v.visited)
			return new Pair<>(false, false);
		
		v.entered = true;
		
		for (String nextName : v.links) {
			GraphNode next = vertexMap.get(nextName);
			if (next == null) {
				throw new WerkConfigException(
						String.format("Vertex not found: name [%s]", v.name)
					);
			} else {
				if (!next.visited) {
					if (next.entered) {
						//cycle found at this point
						path.addNode(next.name);
						return new Pair<>(true, !path.addNode(v.name));
					} else {
						Pair<Boolean, Boolean> res = findCycle(path, vertexMap, next);
						if (res.getValue0()) {
							if (res.getValue1()) {
								return res;
							} else {
								return new Pair<>(true, !path.addNode(v.name));
							}
						}
					}
				}	
			}
		}
		
		v.visited = true;
		
		return new Pair<>(false, false);
	}
	
	public Optional<PathStructure> findCyclePath(List<GraphNode> graph) throws WerkConfigException {
		Map<String, GraphNode> vertexMap = new HashMap<>();
		for (GraphNode v : graph)
			vertexMap.put(v.name, v);
		
		PathStructure path = new PathStructure();
		for (GraphNode v : graph)
			if (findCycle(path, vertexMap, v).getValue0())
				return Optional.of(path);
			
		return Optional.empty();
	}
	
	public void validate(List<GraphNode> graph) throws WerkConfigException {
		Optional<PathStructure> path = findCyclePath(graph);
		
		if (!path.isEmpty())
			throw new WerkConfigException(
				String.format("Call indirection graph: cycle detected: [%s]", path.get().toString())	
			);
	}
}
