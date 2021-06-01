package org.werk2.core.config.cycles.tarjan;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.werk2.core.config.cycles.GraphNode;

public class TarjanTest {
	static void addNode(Map<String, GraphNode> graph, String id, String[] links) {
		GraphNode node = new GraphNode(id, links);
		graph.put(id, node);
	}
	
	protected void check(List<List<String>> res, String[][] check) {
		assertEquals(res.size(), check.length);
		for (int i = 0; i < check.length; i++) {
			assertEquals(res.get(i).size(), check[i].length);

			for (int j = 0; j < check[i].length; j++)
				assertTrue(res.get(i).contains(check[i][j]));
		}
	}
	
	@Test
	public void Test1() {
		Map<String, GraphNode> g1 = new HashMap<>();
		addNode(g1, "1", new String[] { "0" });
		addNode(g1, "0", new String[] { "2", "3" });
		addNode(g1, "2", new String[] { "1" });
		addNode(g1, "3", new String[] { "4" });
		addNode(g1, "4", new String[] { });

		List<List<String>> res = new Tarjan().scc(g1);
		check(res, new String[][] { 
			new String[] { "4" },
			new String[] { "3" },
			new String[] { "1", "2", "0" }
		});
	}
	
	@Test
	public void Test2() {
		Map<String, GraphNode> g2 = new HashMap<>();
		addNode(g2, "0", new String[] { "1" });
		addNode(g2, "1", new String[] { "2" });
		addNode(g2, "2", new String[] { "3" });
		addNode(g2, "3", new String[] { });

		List<List<String>> res = new Tarjan().scc(g2);
		check(res, new String[][] { 
			new String[] { "3" },
			new String[] { "2" },
			new String[] { "1" },
			new String[] { "0" }
		});
	}
	
	@Test
	public void Test3() {
		Map<String, GraphNode> g3 = new HashMap<>();
		addNode(g3, "0", new String[] { "1" });
		addNode(g3, "1", new String[] { "2", "3", "4", "6" });
		addNode(g3, "2", new String[] { "0" });
		addNode(g3, "3", new String[] { "5" });
		addNode(g3, "4", new String[] { "5" });
		addNode(g3, "5", new String[] { });
		addNode(g3, "6", new String[] { });

		List<List<String>> res = new Tarjan().scc(g3);
		check(res, new String[][] { 
			new String[] { "5" },
			new String[] { "3" },
			new String[] { "4" },
			new String[] { "6" },
			new String[] { "2", "1", "0" }
		});
	}
	
	@Test
	public void Test4() {
		Map<String, GraphNode> g4 = new HashMap<>();
		addNode(g4, "0", new String[] { "1", "3" });
		addNode(g4, "1", new String[] { "2", "4" });
		addNode(g4, "2", new String[] { "0", "6" });
		addNode(g4, "3", new String[] { "2" });
		addNode(g4, "4", new String[] { "5", "6" });
		addNode(g4, "5", new String[] { "6", "7", "8", "9" });
		addNode(g4, "6", new String[] { "4" });
		addNode(g4, "7", new String[] { "9" });
		addNode(g4, "8", new String[] { "9" });
		addNode(g4, "9", new String[] { "8" });
		addNode(g4, "10", new String[] { });

		List<List<String>> res = new Tarjan().scc(g4);
		check(res, new String[][] { 
			new String[] { "8", "9" },
			new String[] { "7" },
			new String[] { "5", "4", "6" },
			new String[] { "3", "2", "1", "0" },
			new String[] { "10" }
		});
	}
	
	@Test
	public void Test5() {
		Map<String, GraphNode> g5 = new HashMap<>();
		addNode(g5, "0", new String[] { "1" });
		addNode(g5, "1", new String[] { "2" });
		addNode(g5, "2", new String[] { "3", "4" });
		addNode(g5, "3", new String[] { "0" });
		addNode(g5, "4", new String[] { "2" });

		List<List<String>> res = new Tarjan().scc(g5);
		check(res, new String[][] { 
			new String[] { "4", "3", "2", "1", "0" }
		});
	}
	
	@Test
	public void Test6() {
		Map<String, GraphNode> g6 = new HashMap<>();
		addNode(g6, "1", new String[] { "2" });
		addNode(g6, "2", new String[] { "1" });

		List<List<String>> res = new Tarjan().scc(g6);
		check(res, new String[][] { 
			new String[] { "1", "2" }
		});
	}
	
	@Test
	public void Test7() {
		Map<String, GraphNode> g7 = new HashMap<>();
		addNode(g7, "5", new String[] { "6" });
		addNode(g7, "6", new String[] { });

		List<List<String>> res = new Tarjan().scc(g7);
		check(res, new String[][] {
			new String[] { "6" },
			new String[] { "5" }
		});
	}
	
	@Test
	public void Test8() {
		Map<String, GraphNode> g8 = new HashMap<>();
		addNode(g8, "5", new String[] { "6" });
		addNode(g8, "6", new String[] { "7" });
		addNode(g8, "7", new String[] { "8" });
		addNode(g8, "8", new String[] { "5" });

		List<List<String>> res = new Tarjan().scc(g8);
		check(res, new String[][] { 
			new String[] { "5", "6", "7", "8" }
		});
	}
	
	@Test
	public void Test9() {
		Map<String, GraphNode> g9 = new HashMap<>();
		addNode(g9, "5", new String[] { "6" });
		addNode(g9, "6", new String[] { "5" });
		addNode(g9, "7", new String[] { "8" });
		addNode(g9, "8", new String[] { "7" });

		List<List<String>> res = new Tarjan().scc(g9);
		check(res, new String[][] { 
			new String[] { "5", "6" },
			new String[] { "7", "8" }
		});
	}
	
	@Test
	public void Test10() {
		Map<String, GraphNode> g10 = new HashMap<>();
		addNode(g10, "1", new String[] { "2" });
		addNode(g10, "2", new String[] { "3", "4" });
		addNode(g10, "3", new String[] { "2", "4" });
		addNode(g10, "4", new String[] { "3" });

		List<List<String>> res = new Tarjan().scc(g10);
		check(res, new String[][] { 
			new String[] { "2", "3", "4" },
			new String[] { "1" }
		});
	}
	
	@Test
	public void Test11() {
		Map<String, GraphNode> g11 = new HashMap<>();
		addNode(g11, "6", new String[] { "0", "2", "4" });
		addNode(g11, "3", new String[] { "4", "7" });
		addNode(g11, "2", new String[] { "0" });
		addNode(g11, "0", new String[] { "1" });
		addNode(g11, "4", new String[] { "5" });
		addNode(g11, "5", new String[] { "6", "0" });
		addNode(g11, "7", new String[] { "5", "3" });
		addNode(g11, "1", new String[] { "2" });

		List<List<String>> res = new Tarjan().scc(g11);
		check(res, new String[][] { 
			new String[] { "0", "1", "2" },
			new String[] { "4", "5", "6" },
			new String[] { "3", "7" }
		});
	}
}
