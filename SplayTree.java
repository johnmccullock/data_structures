package splayTree2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Implements a top-down splay tree. "Splaying" a tree is an operation which rotates a node to the root position. The idea is to 
 * move the most recently inserted or searched node to the root, where it can be more quickly found in later searches.
 * 
 * Splay trees have found a lot of use in environments where only 20% of data is being searched for 80% of the time, 
 * which is pretty common.
 * 
 * Based on article and code by Danny Sleator found at http://www.link.cs.cmu.edu/splay/
 * @author John McCullock
 * 
 * @param <K>
 * @param <V>
 * @version 1.0 2019-02-06
 */
public class SplayTree<K extends Comparable<K>, V>
{
	private Node<K, V> mRoot = null;
	private int mSize = 0;
	
	public void put(K key, V value)
	{
		if(this.mRoot == null){
			this.mRoot = new Node<K, V>(key, value);
			this.mSize = 1;
			return;
		}
		
		this.splay(key);
		
		if(key.compareTo(this.mRoot.key) == 0){
			return;
		}
		
		Node<K, V> n = new Node<K, V>(key, value);
		if(key.compareTo(this.mRoot.key) < 0){
			n.left = this.mRoot.left;
			n.right = this.mRoot;
			this.mRoot.left = null;
		}else{
			n.right = this.mRoot.right;
			n.left = this.mRoot;
			this.mRoot.right = null;
		}
		this.mRoot = n;
		this.mSize++;
		return;
	}
	
	public V get(K key)
	{
		if(this.mRoot == null){
			return null;
		}
		this.splay(key);
		if(this.mRoot.key.compareTo(key) != 0){
			return null;
		}
		return this.mRoot.value;
	}
	
	public boolean containsKey(K key)
	{
		if(this.mRoot == null){
			return false;
		}
		this.splay(key);
		if(this.mRoot.key.compareTo(key) != 0){
			return false;
		}
		return true;
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
	
	public void clear()
	{
		this.mRoot = null;
		this.mSize = 0;
		return;
	}
	
	public void remove(K key)
	{
		this.splay(key);
		if(key.compareTo(this.mRoot.key) != 0){
			// not found.
			return;
		}
		if(this.mRoot.left == null){
			this.mRoot = this.mRoot.right;
		}else{
			Node<K, V> x = this.mRoot.right;
			this.mRoot = this.mRoot.left;
			this.splay(key);
			this.mRoot.right = x;
		}
		this.mSize = this.mSize - 1 < 0 ? 0 : this.mSize - 1;
		return;
	}
	
	private void splay(K key)
	{
		Node<K, V> n = new Node<K, V>();
		Node<K, V> leftChild = n;
		Node<K, V> rightChild = n;
		Node<K, V> leftParent = this.mRoot;
		Node<K, V> rightParent = null;
		while(true)
		{
			if(key.compareTo(leftParent.key) < 0){
				if(leftParent.left == null){
					break;
				}
				if(key.compareTo(leftParent.left.key) < 0){
					/* Rotate right. */
					rightParent = leftParent.left;
					leftParent.left = rightParent.right;
					rightParent.right = leftParent;
					leftParent = rightParent;
					if(leftParent.left == null){
						break;
					}
				}
				/* Link right. */
				rightChild.left = leftParent;
				rightChild = leftParent;
				leftParent = leftParent.left;
			}else if(key.compareTo(leftParent.key) > 0){
				if(leftParent.right == null){
					break;
				}
				if(key.compareTo(leftParent.right.key) > 0){
					/* Rotate left. */
					rightParent = leftParent.right;
					leftParent.right = rightParent.left;
					rightParent.left = leftParent;
					leftParent = rightParent;
					if(leftParent.right == null){
						break;
					}
				}
				/* Link left. */
				leftChild.right = leftParent;
				leftChild = leftParent;
				leftParent = leftParent.right;
			}else{
				break;
			}
		}
		/* Reassemble */
		leftChild.right = leftParent.left;
		rightChild.left = leftParent.right;
		leftParent.left = n.right;
		leftParent.right = n.left;
		this.mRoot = leftParent;
		return;
	}
	
	@SuppressWarnings("hiding")
	private class Node<K extends Comparable<K>, V>
	{
		public K key = null;
		public V value = null;
		public Node<K, V> left = null;
		public Node<K, V> right = null;
		
		public Node()
		{
			return;
		}
		
		public Node(K key, V value)
		{
			this.key = key;
			this.value = value;
			return;
		}
	}
}
