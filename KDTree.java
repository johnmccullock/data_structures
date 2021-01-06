package kdTree4;


public class KDTree <K extends Comparable<K>, V>
{
	private Node<K, V> mRoot = null;
	private int mDimensions = 0;
	
	public KDTree(int dimensions)
	{
		this.mDimensions = dimensions;
		return;
	}
	
	public boolean add(K[] keys, V value)
	{
		Node<K, V> nodeToAdd = new Node<K, V>(keys, value);
		
		if(this.mRoot == null){
			this.mRoot = new Node<K, V>(keys, value);
		}else{
			int dimension = -1;
			Node<K, V> parent = this.mRoot;
			do
			{
				dimension = (dimension + 1) % this.mDimensions;
				if(
				
			}while(true);
		}
	}
	
	private boolean areEqual(K[] keys1, K[] keys2)
	{
		for(int i = 0; i < this.mDimensions; i++)
		{
			if(keys1[i].compareTo(keys2[i]) != 0){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * https://github.com/codeandcats/KdTree/blob/master/KdTreeLib/KdTreeNode.cs
	 * @param <K>
	 * @param <V>
	 */
	@SuppressWarnings("hiding")
	private class Node <K extends Comparable<K>, V>
	{
		public K[] keys = null;
		public V value = null;
		public Node<K, V> left = null;
		public Node<K, V> right = null;
		
		public Node() { return; }
		
		public Node(K[] keys, V value)
		{
			this.keys = keys;
			this.value = value;
			return;
		}
	}
}
