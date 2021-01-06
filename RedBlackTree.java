package redBlackTree1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Implements a red-black tree data structure, a self-balancing tree for storing key-value pairs.
 * 
 * Based on article and c++ code by Manish Bhojasia, found at https://www.sanfoundry.com/cpp-program-implement-red-black-tree/
 * 
 * @author John McCullock
 *
 * @param <K>
 * @param <V>
 * @version 1.0 2019-01-30
 */
public class RedBlackTree<K extends Comparable<K>, V>
{
	private enum NodeColor{BLACK, RED};
	
	private Node<K, V> mRoot = null;
	private int mSize = 0;
	private boolean mInDebugMode = false;
	
	public RedBlackTree()
	{
		this.mRoot = null;
		return;
	}
	
	/**
	 * Intended for use in testing this class.
	 * @param useDebugMode
	 */
	public RedBlackTree(boolean useDebugMode)
	{
		this.mRoot = null;
		this.mInDebugMode = useDebugMode;
		if(this.mInDebugMode){
			this.verifyProperties();
		}
		return;
	}
	
	private void verifyProperties()
	{
		this.verifyProperty1(this.mRoot);
		this.verifyProperty2(this.mRoot);
		// Skip property 3: There's not really a way to verify that NIL leaves are black if they're null.
		this.verifyProperty4(this.mRoot);
		this.verifyProperty5(this.mRoot);
		return;
	}
	
	/**
	 * Property #1: Each node is either red or black.
	 * @param n
	 */
	private void verifyProperty1(Node<K, V> n)
	{
		assert (this.getNodeColor(n).equals(NodeColor.RED) || this.getNodeColor(n).equals(NodeColor.BLACK));
		if(n == null){
			return;
		}
		this.verifyProperty1(n.left);
		this.verifyProperty1(n.right);
		return;
	}
	
	private void verifyProperty2(Node<K, V> root)
	{
		assert (this.getNodeColor(root).equals(NodeColor.BLACK));
		return;
	}
	
	private void verifyProperty4(Node<K, V> n)
	{
		if(this.getNodeColor(n).equals(NodeColor.RED)){
			assert (this.getNodeColor(n.left).equals(NodeColor.BLACK));
			assert (this.getNodeColor(n.right).equals(NodeColor.BLACK));
			assert (this.getNodeColor(n.parent).equals(NodeColor.BLACK));
		}
		if(n == null){
			return;
		}
		this.verifyProperty4(n.left);
		this.verifyProperty4(n.right);
		return;
	}
	
	private void verifyProperty5(Node<K, V> root)
	{
		int pathBlackCount = -1;
		pathBlackCount = this.verifyProperty5(root, 0, pathBlackCount);
		return;
	}
	
	private int verifyProperty5(Node<K, V> n, int blackCount, int pathBlackCount)
	{
		if(this.getNodeColor(n).equals(NodeColor.BLACK)){
			blackCount++;
		}
		if(n == null){
			if(pathBlackCount == -1){
				pathBlackCount = blackCount;
			}else{
				assert (blackCount == pathBlackCount);
			}
			return pathBlackCount;
		}
		pathBlackCount =  this.verifyProperty5(n.left, blackCount, pathBlackCount);
		pathBlackCount = this.verifyProperty5(n.right, blackCount, pathBlackCount);
		return pathBlackCount;
	}
	
	private Node<K, V> getNode(K key)
	{
		Node<K, V> n = this.mRoot;
		while(n != null)
		{
			int comparison = key.compareTo(n.key);
			if(comparison == 0){
				return n;
			}else if(comparison < 0){
				n = n.left;
			}else{
				n = n.right;
			}
		}
		return n;
	}
	
	public V get(K key)
	{
		Node<K, V> n = this.getNode(key);
		return n == null ? null : n.value;
	}
	
	public boolean containsKey(K key)
	{
		Node<K, V> n = this.getNode(key);
		return n != null;
	}
	
	public boolean isEmpty()
	{
		return this.mRoot == null;
	}
	
	public int size()
	{
		return this.mSize;
	}
	
	public List<K> keyList()
	{
		List<K> list = new LinkedList<K>();
		this.keyList(this.mRoot, list);
		return list;
	}
	
	private void keyList(Node<K, V> root, List<K> list)
	{
		if(root == null){
			return;
		}
		
		this.keyList(root.left, list);
		list.add(root.key);
		this.keyList(root.right, list);
		return;
	}
	
	public Collection<V> values()
	{
		ArrayList<V> col = new ArrayList<V>();
		this.valueCollection(this.mRoot, col);
		return col;
	}
	
	private void valueCollection(Node<K, V> root, Collection<V> col)
	{
		if(root == null){
			return;
		}
		
		this.valueCollection(root.left, col);
		col.add(root.value);
		this.valueCollection(root.right, col);
		return;
	}
	
	private void rotateLeft(Node<K, V> n)
	{
		Node<K, V> r = n.right;
		this.replaceNode(n, r);
		n.right = r.left;
		if(r.left != null){
			r.left.parent = n;
		}
		r.left = n;
		n.parent = r;
		return;
	}
	
	private void rotateRight(Node<K, V> n)
	{
		Node<K, V> lef = n.left;
		this.replaceNode(n, lef);
		n.left = lef.right;
		if(lef.right != null){
			lef.right.parent = n;
		}
		lef.right = n;
		n.parent = lef;
		return;
	}
	
	private void replaceNode(Node<K, V> oldN, Node<K, V> newN)
	{
		if(oldN.parent == null){
			this.mRoot = newN;
		}else{
			if(oldN == oldN.parent.left){
				oldN.parent.left = newN;
			}else{
				oldN.parent.right = newN;
			}
		}
		if(newN != null){
			newN.parent = oldN.parent;
		}
		return;
	}
	
	public void put(K key, V value)
	{
		Node<K, V> newNode = new Node<K, V>(key, value, NodeColor.RED, null, null);
		if(this.mRoot == null){
			this.mRoot = newNode;
			this.mSize = 1;
		}else{
			Node<K, V> n = this.mRoot;
			while(true)
			{
				int comparison = key.compareTo(n.key);
				if(comparison == 0){
					n.value = value;
					return;
				}else if(comparison < 0){
					if(n.left == null){
						n.left = newNode;
						break;
					}else{
						n = n.left;
					}
				}else{
					if(n.right == null){
						n.right = newNode;
						break;
					}else{
						n = n.right;
					}
				}
			}
			newNode.parent = n;
			this.mSize++;
		}
		this.insertCase1(newNode);
		if(this.mInDebugMode){
			this.verifyProperties();
		}
		return;
	}
	
	private void insertCase1(Node<K, V> n)
	{
		if(n.parent == null){
			n.color = NodeColor.BLACK;
		}else{
			this.insertCase2(n);
		}
		return;
	}
	
	private void insertCase2(Node<K, V> n)
	{
		if(this.getNodeColor(n.parent).equals(NodeColor.BLACK)){
			return;
		}else{
			this.insertCase3(n);
		}
		return;
	}
	
	private void insertCase3(Node<K, V> n)
	{
		if(this.getNodeColor(uncle(n)).equals(NodeColor.RED)){
			n.parent.color = NodeColor.BLACK;
			uncle(n).color = NodeColor.BLACK;
			grandparent(n).color = NodeColor.RED;
			this.insertCase1(grandparent(n));
		}else{
			this.insertCase4(n);
		}
		return;
	}
	
	private void insertCase4(Node<K, V> n)
	{
		if(n == n.parent.right && n.parent == grandparent(n).left){
			this.rotateLeft(n.parent);
			n = n.left;
		}else if(n == n.parent.left && n.parent == grandparent(n).right){
			this.rotateRight(n.parent);
			n = n.right;
		}
		this.insertCase5(n);
		return;
	}
	
	private void insertCase5(Node<K, V> n)
	{
		n.parent.color = NodeColor.BLACK;
		grandparent(n).color = NodeColor.RED;
		if(n == n.parent.left && n.parent == grandparent(n).left){
			this.rotateRight(grandparent(n));
		}else{
			assert (n == n.parent.right && n.parent == grandparent(n).right);
			this.rotateLeft(grandparent(n));
		}
		return;
	}
	
	public void clear()
	{
		this.mRoot = null;
		this.mSize = 0;
		return;
	}
	
	public void remove(K key)
	{
		Node<K, V> child = null;
		Node<K, V> n = this.getNode(key);
		if(n == null){
			return;
		}
		if(n.left != null && n.right != null){
			Node<K, V> pred = this.getMaxNode(n.left);
			n.key = pred.key;
			n.value = pred.value;
			n = pred;
		}
		assert (n.left == null || n.right == null);
		child = n.right == null ? n.left : n.right;
		if(this.getNodeColor(n).equals(NodeColor.BLACK)){
			n.color = this.getNodeColor(child);
			this.deleteCase1(n);
		}
		this.replaceNode(n, child);
		n = null;
		this.mSize--;
		if(this.mInDebugMode){
			this.verifyProperties();
		}
		return;
	}
	
	private void deleteCase1(Node<K, V> n)
	{
		if(n.parent == null){
			return;
		}else{
			this.deleteCase2(n);
		}
		return;
	}
	
	private void deleteCase2(Node<K, V> n)
	{
		if(this.getNodeColor(sibling(n)).equals(NodeColor.RED)){
			n.parent.color = NodeColor.RED;
			sibling(n).color = NodeColor.BLACK;
			if(n == n.parent.left){
				this.rotateLeft(n.parent);
			}else{
				this.rotateRight(n.parent);
			}
		}
		this.deleteCase3(n);
		return;
	}
	
	private void deleteCase3(Node<K, V> n)
	{
		if(this.getNodeColor(n.parent).equals(NodeColor.BLACK) && 
				this.getNodeColor(sibling(n)).equals(NodeColor.BLACK) && 
				this.getNodeColor(sibling(n).left).equals(NodeColor.BLACK) && 
				this.getNodeColor(sibling(n).right).equals(NodeColor.BLACK)){
			sibling(n).color = NodeColor.RED;
			this.deleteCase1(n.parent);
		}else{
			this.deleteCase4(n);
		}
		return;
	}
	
	private void deleteCase4(Node<K, V> n)
	{
		if(this.getNodeColor(n.parent).equals(NodeColor.RED) && 
				this.getNodeColor(sibling(n)).equals(NodeColor.BLACK) && 
				this.getNodeColor(sibling(n).left).equals(NodeColor.BLACK) && 
				this.getNodeColor(sibling(n).right).equals(NodeColor.BLACK)){
			sibling(n).color = NodeColor.RED;
			n.parent.color = NodeColor.BLACK;
		}else{
			this.deleteCase5(n);
		}
		return;
	}
	
	private void deleteCase5(Node<K, V> n)
	{
		if(n == n.parent.left && 
				this.getNodeColor(sibling(n)).equals(NodeColor.BLACK) && 
				this.getNodeColor(sibling(n).left).equals(NodeColor.RED) && 
				this.getNodeColor(sibling(n).right).equals(NodeColor.BLACK)){
			sibling(n).color = NodeColor.RED;
			sibling(n).left.color = NodeColor.BLACK;
			this.rotateRight(sibling(n));
		}else if(n == n.parent.right && 
				this.getNodeColor(sibling(n)).equals(NodeColor.BLACK) &&
				this.getNodeColor(sibling(n).right).equals(NodeColor.RED) &&
				this.getNodeColor(sibling(n).left).equals(NodeColor.BLACK)){
			sibling(n).color = NodeColor.RED;
			sibling(n).right.color = NodeColor.BLACK;
			this.rotateLeft(sibling(n));
		}
		this.deleteCase6(n);
		return;
	}
	
	private void deleteCase6(Node<K, V> n)
	{
		sibling(n).color = this.getNodeColor(n.parent);
		n.parent.color = NodeColor.BLACK;
		if(n == n.parent.left){
			assert(this.getNodeColor(sibling(n).right) == NodeColor.RED);
			sibling(n).right.color = NodeColor.BLACK;
			this.rotateLeft(n.parent);
		}else{
			assert(this.getNodeColor(sibling(n).left) == NodeColor.RED);
			sibling(n).left.color = NodeColor.BLACK;
			this.rotateRight(n.parent);
		}
		return;
	}
	
	private NodeColor getNodeColor(Node<K, V> n)
	{
		return n == null ? NodeColor.BLACK : n.color;
	}
	
	private Node<K, V> uncle(Node<K, V> n)
	{
		assert (n != null);
		assert (n.parent != null);
		assert (n.parent.parent != null);
		return this.sibling(n.parent);
	}
	
	private Node<K, V> sibling(Node<K, V> n)
	{
		assert (n != null);
		assert (n.parent != null);
		if(n == n.parent.left){
			return n.parent.right;
		}else{
			return n.parent.left;
		}
	}
	
	private Node<K, V> grandparent(Node<K, V> n)
	{
		assert (n != null);
		assert (n.parent != null);
		assert (n.parent.parent != null);
		return n.parent.parent;
	}
	
	private Node<K, V> getMaxNode(Node<K, V> n)
	{
		assert (n != null);
		while(n.right != null){
			n = n.right;
		}
		return n;
	}
	
	@SuppressWarnings("hiding")
	private class Node<K extends Comparable<K>, V>
	{
		public NodeColor color = null;
		public K key = null;
		public V value = null;
		public Node<K, V> parent = null;
		public Node<K, V> left = null;
		public Node<K, V> right = null;
		
		public Node(K key, V value, NodeColor color, Node<K, V> left, Node<K, V> right)
		{
			this.key = key;
			this.value = value;
			this.color = color;
			this.left = left;
			this.right = right;
			if(left != null){
				left.parent = this;
			}
			if(right != null){
				right.parent = this;
			}
			this.parent = null;
			return;
		}
	}
	
	
}
