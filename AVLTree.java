package avlTree3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Implements an AVL tree, one of the first self-balancing trees, invented by Adelson-Velsky and Landis.  
 * Developers tend to choose red-black trees and other data structures over the AVL tree for efficiency.  The AVL tree's put 
 * and remove methods entail two passes through the tree; one to put/remove, the second to rebalance the tree.  
 * Red-black trees and others take fewer operations to achieve balance.
 * 
 * This version uses the method checkCompliance for put and remove operations.  It's a convoluted diagnostic that should be commented
 * out for production use.
 * 
 * Based on article and code by Marcelo Silva found at: https://algs4.cs.princeton.edu/99misc/AVLTreeST.java.html
 * @author John McCullock
 *
 * @param <K>
 * @param <V>
 * @version 1.0 2019-03-12
 */
public class AVLTree <K extends Comparable<K>, V>
{
	private Node mRoot = null;
	
	public AVLTree() { return; }
	
	public void put(K key, V value)
	{
		if (key == null) throw new IllegalArgumentException("key parameter cannot be null");
		if (value == null) {
            this.remove(key);
            return;
        }
        this.mRoot = put(this.mRoot, key, value);
        assert this.checkCompliance();
		return;
	}
	
	private Node put(Node root, K key, V value)
	{
		if(root == null){
			return new Node(key, value, 0, 1);
		}
		int cmp = key.compareTo(root.key);
		if(cmp < 0){
			root.left = put(root.left, key, value);
		}else if (cmp > 0){
			root.right = put(root.right, key, value);
		}else{
			root.value = value;
			return root;
		}
		root.size = 1 + this.size(root.left) + this.size(root.right);
		root.height = 1 + Math.max(height(root.left), height(root.right));
		return balance(root);
	}
	
	public V get(K key)
	{
		if(key == null){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		Node n = this.get(this.mRoot, key);
		if(n == null){
			return null;
		}
		return n.value;
	}
	
	private Node get(Node root, K key)
	{
		if(root == null){
			return null;
		}
		int cmp = key.compareTo(root.key);
		if(cmp < 0){
			return this.get(root.left, key);
		}else if(cmp > 0){
			return this.get(root.right, key);
		}else{
			return root;
		}
	}
	
	public boolean contains(K key)
	{
		return this.get(key) != null;
	}
	
	public void remove(K key)
	{
		if(key == null){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if(!this.contains(key)){
			return;
		}
		this.mRoot = this.remove(this.mRoot, key);
		assert this.checkCompliance();
	}
	
	private Node remove(Node root, K key)
	{
		int cmp = key.compareTo(root.key);
		if(cmp < 0){
			root.left = this.remove(root.left, key);
		}else if(cmp > 0){
			root.right = this.remove(root.right, key);
		}else{
			if(root.left == null){
				return root.right;
			}else if(root.right == null){
				return root.left;
			}else{
				Node temp = root;
				root = this.min(temp.right);
				root.right = this.removeMin(temp.right);
				root.left = temp.left;
			}
		}
		root.size = 1 + this.size(root.left) + this.size(root.right);
		root.height = 1 + Math.max(this.height(root.left), this.height(root.right));
		return this.balance(root);
	}
	
	public void removeMin()
	{
		if(this.isEmpty()){
			throw new NoSuchElementException("AVLTree is empty.");
		}
		this.mRoot = this.removeMin(this.mRoot);
		assert this.checkCompliance();
		return;
	}
	
	private Node removeMin(Node root)
	{
		if(root.left == null){
			return root.right;
		}
		root.left = this.removeMin(root.left);
		root.size = 1 + this.size(root.left) + this.size(root.right);
		root.height = 1 + Math.max(this.height(root.left), this.height(root.right));
		return this.balance(root);
	}
	
	public void removeMax()
	{
		if(this.isEmpty()){
			throw new NoSuchElementException("AVLTree is empty.");
		}
		this.mRoot = this.removeMax(this.mRoot);
		assert this.checkCompliance();
	}
	
	private Node removeMax(Node root)
	{
		if(root.right == null){
			return root.left;
		}
		root.right = this.removeMax(root.right);
		root.size = 1 + this.size(root.left) + this.size(root.right);
		root.height = 1 + Math.max(this.height(root.left), this.height(root.right));
		return this.balance(root);
	}
	
	public int height()
	{
		return this.height(this.mRoot);
	}
	
	private int height(Node n)
	{
		return n == null ? -1 : n.height;
	}
	
	private Node balance(Node n)
	{
		if(balanceFactor(n) < -1){
			if(balanceFactor(n.right) > 0){
				n.right = rotateRight(n.right);
			}
			n = rotateLeft(n);
		}else if(balanceFactor(n) > 1){
			if(balanceFactor(n.left) < 0){
				n.left = rotateLeft(n.left);
			}
			n = rotateRight(n);
		}
		return n;
	}
	
	private int balanceFactor(Node n)
	{
		return this.height(n.left) - this.height(n.right);
	}
	
	private Node rotateLeft(Node x)
	{
		Node y = x.right;
		x.right = y.left;
		y.left = x;
		y.size = x.size;
		x.size = 1 + this.size(x.left) + this.size(x.right);
		x.height = 1 + Math.max(height(x.left), height(x.right));
		y.height = 1 + Math.max(height(y.left), height(y.right));
		return y;
	}
	
	private Node rotateRight(Node x)
	{
		Node y = x.left;
		x.left = y.right;
		y.right = x;
		y.size = x.size;
		x.size = 1 + this.size(x.left) + this.size(x.right);
		x.height = 1 + Math.max(height(x.left), height(x.right));
		y.height = 1 + Math.max(height(y.left), height(y.right));
		return y;
	}
	
	public int size()
	{
		return this.size(this.mRoot);
	}
	
	private int size(Node root)
	{
		return root == null ? 0 : root.size;
	}
	
	public boolean isEmpty()
	{
		return this.mRoot == null;
	}
	
	public int rank(K key)
	{
		if(key == null){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		return this.rank(key, this.mRoot);
	}
	
	private int rank(K key, Node root)
	{
		if(root == null){
			return 0;
		}
		int cmp = key.compareTo(root.key);
		if(cmp < 0){
			return this.rank(key, root.left);
		}else if(cmp > 0){
			return 1 + this.size(root.left) + this.rank(key, root.right);
		}else{
			return this.size(root.left);
		}
	}
	
	public K select(int k)
	{
		if(k < 0 || k >= this.size()){
			throw new IllegalArgumentException("Argument out of range: 0 to " + (this.size() - 1));
		}
		Node x = this.select(this.mRoot, k);
		return x.key;
	}
	
	private Node select(Node root, int k)
	{
		if(root == null){
			return null;
		}
		int t = this.size(root.left);
		if(t > k){
			return this.select(root.left,  k);
		}else if(t < k){
			return this.select(root.right, k - t - 1);
		}else{
			return root;
		}
	}
	
	public List<K> keys()
	{
		List<K> list = new LinkedList<K>();
		this.keys(this.mRoot, list);
		return list;
	}
	
	private void keys(Node root, List<K> list)
	{
		if(root == null){
			return;
		}
		this.keys(root.left, list);
		list.add(root.key);
		this.keys(root.right, list);
		return;
	}
	
	public List<K> keys(K low, K high)
	{
		if(low == null){
			throw new IllegalArgumentException("First argument cannot be null.");
		}
		if(high == null){
			throw new IllegalArgumentException("Second argument cannot be null.");
		}
		List<K> list = new LinkedList<K>();
		this.keys(this.mRoot, list, low, high);
		return list;
	}
	
	private void keys(Node root, List<K> list, K low, K high)
	{
		if(root == null){
			return;
		}
		int cmpLow = low.compareTo(root.key);
		int cmpHigh = high.compareTo(root.key);
		if(cmpLow < 0){
			this.keys(root.left, list, low, high);
		}
		if(cmpLow <= 0 && cmpHigh >= 0){
			list.add(root.key);
		}
		if(cmpHigh > 0){
			this.keys(root.right, list, low, high);
		}
		return;
	}
	
	public Collection<V> values()
	{
		ArrayList<V> col = new ArrayList<V>();
		this.values(this.mRoot, col);
		return col;
	}
	
	private void values(Node root, Collection<V> col)
	{
		if(root == null){
			return;
		}
		this.values(root.left, col);
		col.add(root.value);
		this.values(root.right, col);
		return;
	}
	
	public K floor(K key)
	{
		if(key == null){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if(this.isEmpty()){
			throw new NoSuchElementException("AVLTree is empty.");
		}
		Node n = this.floor(this.mRoot, key);
		return n == null ? null : n.key;
	}
	
	private Node floor(Node root, K key)
	{
		if(root == null){
			return null;
		}
		int cmp = key.compareTo(root.key);
		if(cmp == 0){
			return root;
		}
		if(cmp < 0){
			return this.floor(root.left, key);
		}
		Node n = this.floor(root.right, key);
		return n != null ? n : root;
	}
	
	public K ceil(K key)
	{
		if(key == null){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if(this.isEmpty()){
			throw new NoSuchElementException("AVLTree is empty.");
		}
		Node n = this.ceil(this.mRoot, key);
		return n == null ? null : n.key;
	}
	
	private Node ceil(Node root, K key)
	{
		if(root == null){
			return null;
		}
		int cmp = key.compareTo(root.key);
		if(cmp == 0){
			return root;
		}
		if(cmp > 0){
			return this.ceil(root.right, key);
		}
		Node n = this.ceil(root.left, key);
		return n != null ? n : root;
	}
	
	public K min()
	{
		if(this.isEmpty()){
			throw new NoSuchElementException("AVLTree is empty.");
		}
		return this.min(this.mRoot).key;
	}
	
	private Node min(Node root)
	{
		if(root.left == null){
			return root;
		}
		return this.min(root.left);
	}
	
	public K max()
	{
		if(this.isEmpty()){
			throw new NoSuchElementException("AVLTree is empty.");
		}
		return this.max(this.mRoot).key;
	}
	
	private Node max(Node root)
	{
		if(root.right == null){
			return root;
		}
		return this.max(root.right);
	}
	
	private boolean checkCompliance()
	{
		return this.isBSTCompliant() && this.isAVLCompliant() && this.isSizeCompliant() && this.isRankConsistent();
	}
	
	private boolean isAVLCompliant()
	{
		return this.isAVLCompliant(this.mRoot);
	}
	
	private boolean isAVLCompliant(Node root)
	{
		if(root == null){
			return true;
		}
		int bf = this.balanceFactor(root);
		if(bf > 1 || bf < -1){
			return false;
		}
		return this.isAVLCompliant(root.left) && this.isAVLCompliant(root.right);
	}
	
	private boolean isBSTCompliant()
	{
		return this.isBSTCompliant(this.mRoot, null, null);
	}
	
	private boolean isBSTCompliant(Node root, K min, K max)
	{
		if(root == null){
			return true;
		}
		if(min != null && root.key.compareTo(min) <= 0){
			return false;
		}
		if(max != null && root.key.compareTo(max) >= 0){
			return false;
		}
		return this.isBSTCompliant(root.left, min, root.key) && this.isBSTCompliant(root.right, root.key, max);
	}
	
	private boolean isSizeCompliant()
	{
		return this.isSizeConsistent(this.mRoot);
	}
	
	private boolean isSizeConsistent(Node root)
	{
		if(root == null){
			return true;
		}
		if(root.size != this.size(root.left) + this.size(root.right) + 1){
			return false;
		}
		return this.isSizeConsistent(root.left) && this.isSizeConsistent(root.right);
	}
	
	private boolean isRankConsistent()
	{
		for(int i = 0; i < this.size(); i++)
		{
			if(i != this.rank(this.select(i))){
				return false;
			}
		}
		for(K key : this.keys())
		{
			if(key.compareTo(this.select(this.rank(key))) != 0){
				return false;
			}
		}
		return true;
	}
	
	private class Node
	{
		public K key = null;
		public V value = null;
		public int height = -1;
		public int size = 0;
		public Node left = null;
		public Node right = null;
		
		public Node(K key, V value, int height, int size)
		{
			this.key = key;
			this.value = value;
			this.height = height;
			this.size = size;
			return;
		}
	}
}
