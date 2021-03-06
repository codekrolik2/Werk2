package org.werk2.generics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.werk2.common.WerkConfigException;
import org.werk2.core.config.cycles.GraphNode;
import org.werk2.core.config.cycles.dfs.DFSCycleFinder;
import org.werk2.core.config.cycles.dfs.PathStructure;

public class TestDFSCycleFinder {
	@Test(expected = WerkConfigException.class)
	public void test() throws WerkConfigException {
		DFSCycleFinder finder = new DFSCycleFinder();
		
		GraphNode v0 = new GraphNode("0");
		GraphNode v1 = new GraphNode("1");
		GraphNode v2 = new GraphNode("2");
		GraphNode v3 = new GraphNode("3");
		
		v0.getLinks().add("1");
		v0.getLinks().add("2");
		
		v1.getLinks().add("2");
		
		v2.getLinks().add("3");
		v2.getLinks().add("0");
		
		v3.getLinks().add("3");
		
		GraphNode[] graph = new GraphNode[] { v0, v1, v2, v3 };
		
		finder.validate(Arrays.asList(graph));
	}
	
	@Test(expected = WerkConfigException.class)
	public void test0() throws WerkConfigException {
		DFSCycleFinder finder = new DFSCycleFinder();
		
		GraphNode v0 = new GraphNode("0");
		GraphNode v1 = new GraphNode("1");
		GraphNode v2 = new GraphNode("2");
		GraphNode v3 = new GraphNode("3");
		
		v0.getLinks().add("1");
		v0.getLinks().add("2");
		
		v1.getLinks().add("2");
		
		v2.getLinks().add("3");
		//Causes error - no vertex 4 in the Graph
		v2.getLinks().add("4");
		
		GraphNode[] graph = new GraphNode[] { v0, v1, v2, v3 };
		
		assertTrue(finder.findCyclePath(Arrays.asList(graph)).isEmpty());
	}
	
	@Test
	public void test1() throws WerkConfigException {
		DFSCycleFinder finder = new DFSCycleFinder();
		
		GraphNode v0 = new GraphNode("0");
		GraphNode v1 = new GraphNode("1");
		GraphNode v2 = new GraphNode("2");
		GraphNode v3 = new GraphNode("3");
		
		v0.getLinks().add("1");
		v0.getLinks().add("2");
		
		v1.getLinks().add("2");
		
		v2.getLinks().add("3");
		v2.getLinks().add("0");
		
		v3.getLinks().add("3");
		
		GraphNode[] graph = new GraphNode[] { v0, v1, v2, v3 };
		
		PathStructure path = finder.findCyclePath(Arrays.asList(graph)).get();
		assertEquals(path.getNodeSet().size(), 1);
		assertTrue(path.getNodeSet().contains("3"));
		
		assertEquals(path.getNodeOrder().pollLast(), "3");
		assertEquals(path.getNodeOrder().pollLast(), "3");
	}
	
	@Test
	public void test2() throws WerkConfigException {
		DFSCycleFinder finder = new DFSCycleFinder();
		
		GraphNode v0 = new GraphNode("0");
		GraphNode v1 = new GraphNode("1");
		GraphNode v2 = new GraphNode("2");
		GraphNode v3 = new GraphNode("3");
		
		v0.getLinks().add("1");
		v0.getLinks().add("2");
		
		v1.getLinks().add("2");
		
		v2.getLinks().add("3");
		//TODO: Causes error
		//v2.getLinks().add("4");
		
		GraphNode[] graph = new GraphNode[] { v0, v1, v2, v3 };
		
		assertTrue(finder.findCyclePath(Arrays.asList(graph)).isEmpty());
	}
	
	@Test
	public void test3() throws WerkConfigException {
		DFSCycleFinder finder = new DFSCycleFinder();
		
		GraphNode v0 = new GraphNode("0");
		GraphNode v1 = new GraphNode("1");
		GraphNode v2 = new GraphNode("2");
		GraphNode v3 = new GraphNode("3");
		
		v0.getLinks().add("1");
		
		v1.getLinks().add("2");
		
		v2.getLinks().add("3");
		
		v3.getLinks().add("0");
		
		GraphNode[] graph = new GraphNode[] { v0, v1, v2, v3 };
		
		PathStructure path = finder.findCyclePath(Arrays.asList(graph)).get();
		assertEquals(path.getNodeSet().size(), 4);
		assertTrue(path.getNodeSet().contains("0"));
		assertTrue(path.getNodeSet().contains("1"));
		assertTrue(path.getNodeSet().contains("2"));
		assertTrue(path.getNodeSet().contains("3"));
		
		assertEquals(path.getNodeOrder().pollLast(), "0");
		assertEquals(path.getNodeOrder().pollLast(), "1");
		assertEquals(path.getNodeOrder().pollLast(), "2");
		assertEquals(path.getNodeOrder().pollLast(), "3");
		assertEquals(path.getNodeOrder().pollLast(), "0");
	}
	
	@Test
	public void test4() throws WerkConfigException {
		DFSCycleFinder finder = new DFSCycleFinder();
		
		GraphNode v0 = new GraphNode("0");
		GraphNode v1 = new GraphNode("1");
		GraphNode v2 = new GraphNode("2");
		GraphNode v3 = new GraphNode("3");
		
		v0.getLinks().add("1");
		v0.getLinks().add("2");
		
		v1.getLinks().add("2");
		
		v2.getLinks().add("3");
		
		v3.getLinks().add("1");
		
		GraphNode[] graph = new GraphNode[] { v0, v1, v2, v3 };
		
		PathStructure path = finder.findCyclePath(Arrays.asList(graph)).get();
		assertEquals(path.getNodeSet().size(), 3);
		assertTrue(path.getNodeSet().contains("1"));
		assertTrue(path.getNodeSet().contains("2"));
		assertTrue(path.getNodeSet().contains("3"));
		
		assertEquals(path.getNodeOrder().pollLast(), "1");
		assertEquals(path.getNodeOrder().pollLast(), "2");
		assertEquals(path.getNodeOrder().pollLast(), "3");
		assertEquals(path.getNodeOrder().pollLast(), "1");
	}
	
	@Test
	public void test5() throws WerkConfigException {
		DFSCycleFinder finder = new DFSCycleFinder();
		
		GraphNode v0 = new GraphNode("0");
		GraphNode v1 = new GraphNode("1");
		GraphNode v2 = new GraphNode("2");
		GraphNode v3 = new GraphNode("3");
		
		v0.getLinks().add("1");
		v2.getLinks().add("3");
		
		GraphNode[] graph = new GraphNode[] { v0, v1, v2, v3 };
		
		assertTrue(finder.findCyclePath(Arrays.asList(graph)).isEmpty());
	}
	
	@Test
	public void test6() throws WerkConfigException {
		DFSCycleFinder finder = new DFSCycleFinder();
		
		GraphNode v0 = new GraphNode("0");
		GraphNode v1 = new GraphNode("1");
		GraphNode v2 = new GraphNode("2");
		GraphNode v3 = new GraphNode("3");
		
		GraphNode[] graph = new GraphNode[] { v0, v1, v2, v3 };
		
		assertTrue(finder.findCyclePath(Arrays.asList(graph)).isEmpty());
	}
}
