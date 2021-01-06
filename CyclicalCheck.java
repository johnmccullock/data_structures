package cycleDetection1;

import java.util.ArrayList; 
import java.util.LinkedList; 
import java.util.List;

/**
 * Array-based utility for finding graph connections which result in a loop (cycle).
 * 
 * Because this code is array based, the total number of edges (graph connections) must be known before-hand.
 * 
 * Based on article and code found at: https://www.geeksforgeeks.org/?p=18516/
 * 
 * @version 1.0 2019-02-06
 */
public class CyclicalCheck
{
	private int mNumEdges = 0;
	private List<List<Integer>> mAdjacentLists;

	public CyclicalCheck(int numEdges)
	{ 
		this.mNumEdges = numEdges; 
		this.mAdjacentLists = new ArrayList<>(numEdges);
		
		for(int i = 0; i < numEdges; i++)
		{
			this.mAdjacentLists.add(new LinkedList<>());
		}
	}
	
	/**
	 * To detect a back edge, we can keep track of vertices currently in recursion stack of function for depth first search traversal. 
	 * If we reach a vertex that is already in the recursion stack, then there is a cycle in the tree.  The edge that connects the current 
	 * vertex to the vertex in the recursion stack is a back edge.  We use the recStack[] array to keep track of vertices in the 
	 * recursion stack.
	 * @return true of there's a cycle present, false otherwise.
	 */
	public boolean isCyclic()
	{
		boolean[] visited = new boolean[this.mNumEdges];
		boolean[] recStack = new boolean[this.mNumEdges];
		
		for(int i = 0; i < this.mNumEdges; i++)
		{
			if(isCyclic(i, visited, recStack)){
				return true;
			}
		}
		return false;
	}
	
	private boolean isCyclic(int i, boolean[] visited, boolean[] recStack)
	{
		if(recStack[i]){
			return true;
		}
		if(visited[i]){
			return false;
		}
		visited[i] = true;
		recStack[i] = true;
		List<Integer> children = this.mAdjacentLists.get(i);
		for(Integer c : children)
		{
			if(isCyclic(c, visited, recStack)){
				return true;
			}
		}
		
		recStack[i] = false;
		return false;
	}
	
	public void addEdge(int source, int dest)
	{
		this.mAdjacentLists.get(source).add(dest);
		return;
	}
}
