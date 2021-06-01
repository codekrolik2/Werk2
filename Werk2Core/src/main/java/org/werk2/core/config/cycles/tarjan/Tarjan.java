package org.werk2.core.config.cycles.tarjan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.werk2.core.config.cycles.GraphNode;

//This class represents a directed graph
//using adjacency list representation
public class Tarjan {
	protected int time = 0;

	protected void sccUtil(Map<String, GraphNode> graph, String u, Map<String, Integer> low, Map<String, Integer> disc,
			Map<String, Boolean> stackMember, Stack<String> st, List<List<String>> ret) {
		// Initialize discovery time and low value
		disc.put(u, time);
		low.put(u, time);
		time += 1;
		stackMember.put(u, true);
		st.push(u);

		GraphNode node = graph.get(u);

		// Go through all vertices adjacent to this
		for (String n : node.getLinks()) {
			if (disc.get(n) == -1) {
				sccUtil(graph, n, low, disc, stackMember, st, ret);

				// Check if the subtree rooted with v
				// has a connection to one of the
				// ancestors of u
				// Case 1 (per above discussion on
				// Disc and Low value)
				low.put(u, Math.min(low.get(u), low.get(n)));
			} else if (stackMember.get(n)) {
				// Update low value of 'u' only if 'v' is
				// still in stack (i.e. it's a back edge,
				// not cross edge).
				// Case 2 (per above discussion on Disc
				// and Low value)
				low.put(u, Math.min(low.get(u), disc.get(n)));
			}
		}

		// head node found, pop the stack and print an SCC
		// To store stack extracted vertices
		String w = null;
		if (low.get(u).equals(disc.get(u))) {
			List<String> newScc = new ArrayList<>();
			while (!u.equals(w)) {
				w = st.pop();
				newScc.add(w);
				stackMember.put(w, false);
			}
			ret.add(newScc);
		}
	}

	// The function to do DFS traversal.
	// It uses SCCUtil()
	public List<List<String>> scc(Map<String, GraphNode> graph) {
		List<List<String>> ret = new ArrayList<>();

		// Mark all the vertices as not visited
		// and Initialize parent and visited,
		// and ap(articulation point) arrays
		Map<String, Integer> disc = new HashMap<>();
		Map<String, Integer> low = new HashMap<>();
		Map<String, Boolean> stackMember = new HashMap<>();
		for (String nodeId : graph.keySet()) {
			disc.put(nodeId, -1);
			low.put(nodeId, -1);
			stackMember.put(nodeId, false);
		}

		Stack<String> st = new Stack<String>();

		// Call the recursive helper function
		// to find articulation points
		// in DFS tree rooted with vertex 'i'
		for (String nodeId : graph.keySet()) {
			if (disc.get(nodeId) == -1)
				sccUtil(graph, nodeId, low, disc, stackMember, st, ret);
		}

		return ret;
	}
}