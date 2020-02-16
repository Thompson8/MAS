package hu.mas.core.path.dft;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DFTGraph {

	// No. of vertices in graph
	private int vertices;

	// adjacency list
	private ArrayList<Integer>[] adjList;

	// Constructor
	public DFTGraph(int vertices) {

		// initialise vertex count
		this.vertices = vertices;

		// initialise adjacency list
		initAdjList();
	}

	// utility method to initialise
	// adjacency list
	@SuppressWarnings("unchecked")
	private void initAdjList() {
		adjList = new ArrayList[vertices];

		for (int i = 0; i < vertices; i++) {
			adjList[i] = new ArrayList<>();
		}
	}

	// add edge from u to v
	public void addEdge(int u, int v) {
		// Add v to u's list.
		adjList[u].add(v);
	}

	public List<List<Integer>> getAllPaths(int s, int d) {
		boolean[] isVisited = new boolean[vertices];
		ArrayList<Integer> pathList = new ArrayList<>();

		// add source to path[]
		pathList.add(s);

		List<List<Integer>> collector = new ArrayList<>();
		// Call recursive utility
		getAllPathsUtil(s, d, isVisited, pathList, collector);
		return collector;
	}

	// A recursive function to print
	// all paths from 'u' to 'd'.
	// isVisited[] keeps track of
	// vertices in current path.
	// localPathList<> stores actual
	// vertices in the current path
	@SuppressWarnings("deprecation")
	private void getAllPathsUtil(Integer u, Integer d, boolean[] isVisited, List<Integer> localPathList,
			List<List<Integer>> collector) {

		// Mark the current node
		isVisited[u] = true;

		if (u.equals(d)) {
			collector.add(localPathList.stream().map(Integer::new).collect(Collectors.toList()));
			// if match found then no need to traverse more till depth
			isVisited[u] = false;
			return;
		}

		// Recur for all the vertices
		// adjacent to current vertex
		for (Integer i : adjList[u]) {
			if (!isVisited[i]) {
				// store current node
				// in path[]
				localPathList.add(i);
				getAllPathsUtil(i, d, isVisited, localPathList, collector);

				// remove current node
				// in path[]
				localPathList.remove(i);
			}
		}

		// Mark the current node
		isVisited[u] = false;
	}
}
