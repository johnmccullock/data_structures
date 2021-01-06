package skiplist3;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A key/value implementation of a skip list which works more like a map.
 * 
 * Based on the skip list concept described by William Pugh in "Skip Lists: A Probabilistic Alternative to Balanced Trees".
 * 
 * SkipLists are good fast searching, but slow insertion/deletion.  They are often used instead of trees because of comparable
 * search speed and no need for tree-balancing.
 * 
 * Does not allow duplicate or null entries.
 * 
 * Based on article and code by Bill Whitney for www.drdobbs.com back in 1998: http://www.drdobbs.com/cpp/skip-lists-in-c/184403579
 * Since then, this code has been passed around the internet under many other author's names.
 * 
 * @author John McCullock
 * @version 1.0 2019-01-25
 * @param <K, V>
 */
public class SkipMap <K extends Comparable<K>, V> implements Iterable<K>
{
	/*
	 * The maximum number of levels the algorithm can generate.
	 */
	private int mMaxLevels = 0;
	/*
	 * The probability used for partitioning calculations.
	 */
	private double mProbability = 0.0;
	/*
	 * The highest level currently in use.
	 */
	private int mLevel = 0;
	/*
	 * The header is unaffected by any comparison calculations on the other nodes.  No matter the inserts or deletions, the header
	 * remains at the lead, even if it's the only node.
	 */
	private Node<K, V> mHeader = null;
	private int mSize = 0;
	
	public SkipMap(int maxLevels, double probability)
	{
		this.mMaxLevels = maxLevels;
		this.mProbability = probability;
		this.mLevel = 0;
		this.mHeader = new Node<K, V>(null, null, this.mMaxLevels);
		return;
	}
	
	private int randomLevel()
	{
		double r = Math.random();
		int lvl = 0;
		while(r < this.mProbability && lvl < this.mMaxLevels)
		{
			lvl++;
			r = Math.random();
		}
		return lvl;
	}
	
	public void put(K key, V value)
	{
		Node<K, V> current = this.mHeader;
		@SuppressWarnings("unchecked")
		Node<K, V>[] update = (Node<K, V>[])new Node[this.mMaxLevels + 1];
		
		for(int i = this.mLevel; i >= 0; i--)
		{
			while(current.forward[i] != null && current.forward[i].key.compareTo(key) < 0)
			{
				current = current.forward[i];
			}
			update[i] = current;
		}
		
		current = current.forward[0];
		
		/*
		 * If current != null or it's value matches the one to added, it's a duplicate.
		 */
		if(current == null || current.key.compareTo(key) != 0){
			int rLevel = this.randomLevel();
			
			if(rLevel > this.mLevel){
				for(int i = this.mLevel + 1; i < rLevel + 1; i++)
				{
					update[i] = this.mHeader;
				}
				this.mLevel = rLevel;
			}
			
			Node<K, V> n = new Node<K, V>(key, value, rLevel);
			
			for(int i = 0; i <= rLevel; i++)
			{
				n.forward[i] = update[i].forward[i];
				update[i].forward[i] = n;
			}
			this.mSize++;
		}
		return;
	}
	
	public void remove(K key)
	{
		Node<K, V> current = this.mHeader;
		
		@SuppressWarnings("unchecked")
		Node<K, V>[] update = (Node<K, V>[])new Node[this.mMaxLevels + 1];
		
		for(int i = this.mLevel; i >= 0; i--)
		{
			while(current.forward[i] != null && current.forward[i].key.compareTo(key) < 0)
			{
				current = current.forward[i];
			}
			update[i] = current;
		}
		
		current = current.forward[0];
		
		/*
		 * If current == null or its value doesn't match, the value wasn't found in the list.
		 */
		if(current != null && current.key.compareTo(key) == 0){
			for(int i = 0; i <= this.mLevel; i++)
			{
				if(update[i].forward[i] != current){
					break;
				}
				update[i].forward[i] = current.forward[i];
			}
			
			while(this.mLevel > 0 && this.mHeader.forward.length <= 0) 
			{
				this.mLevel--;
			}
			this.mSize--;
		}
		return;
	}
	
	public V get(K key)
	{
		Node<K, V> current = this.mHeader;
		for(int i = this.mLevel; i >= 0; i--)
		{
			while(current.forward[i] != null && current.forward[i].key.compareTo(key) < 0)
			{
				current = current.forward[i];
			}
		}
		
		current = current.forward[0];
		
		if(current != null && current.key.compareTo(key) == 0){
			return current.value;
		}
		return null;
	}
	
	public boolean hasKey(K key)
	{
		Node<K, V> current = this.mHeader;
		for(int i = this.mLevel; i >= 0; i--)
		{
			while(current.forward[i] != null && current.forward[i].key.compareTo(key) < 0)
			{
				current = current.forward[i];
			}
		}
		
		current = current.forward[0];
		
		if(current != null && current.key.compareTo(key) == 0){
			return true;
		}
		return false;
	}
	
	/**
	 * Returns a List, of type K, of all keys in order.  List is not backed by any map, so any changes to the List will not be reflected
	 * in the map.
	 * @return LinkedList
	 */
	public List<K> keyList()
	{
		List<K> list = new LinkedList<K>();
		Node<K, V> node = this.mHeader.forward[0];
		while(node != null)
		{
			list.add(node.key);
			node = node.forward[0];
		}
		return list;
	}
	
	@Override
	public Iterator<K> iterator()
	{
		return new Iterator<K>()
		{
			private Node<K, V> mCurrent = mHeader;
			
			@Override
			public boolean hasNext()
			{
				if(this.mCurrent == null){
					return false;
				}
				return this.mCurrent.forward[0] == null ? false : true;
			}
			
			@Override
			public K next()
			{
				if(!this.hasNext()){
					return null;
				}
				this.mCurrent = this.mCurrent.forward[0];
				return this.mCurrent.key;
			}
		};
	}
	
	public void clear()
	{
		for(int i = 0; i < this.mHeader.forward.length; i++)
		{
			this.mHeader.forward[i] = null;
		}
		this.mSize = 0;
		return;
	}
	
	public int size()
	{
		return this.mSize;
	}
	
	/**
	 * For diagnostic purposes.  Do not use for practical size references.  This is a convenience method for testing 
	 * the size field of this class for accuracy, by looping through the full set of nodes.
	 * @return the number of nodes in the list.
	 */
	public int deepCount()
	{
		int count = 0;
		Node<K, V> node = this.mHeader.forward[0];
		while(node != null)
		{
			count++;
			node = node.forward[0];
		}
		return count;
	}
	
	/**
	 * Returns the keys listed in order, and the level structure used. 
	 */
	@Override
	public String toString()
	{
		StringBuilder b = new StringBuilder();
		for(int i = 0; i <= this.mLevel; i++)
		{
			Node<K, V> node = this.mHeader.forward[i];
			b.append("Level " + i + ": ");
			while(node != null)
			{
				b.append(node.key.toString() + " ");
				node = node.forward[i];
			}
			if(i != this.mLevel){
				b.append("\n");
			}
		}
		return b.toString();
	}
	
	@SuppressWarnings("hiding")
	private class Node <K extends Comparable<K>, V>
	{
		public K key = null;
		public V value = null;
		public Node<K, V>[] forward = null;
		
		@SuppressWarnings("unchecked")
		public Node(K key, V value, int level)
		{
			this.key = key;
			this.value = value;
			forward = (Node<K, V>[])new Node[level + 1];
			return;
		}
	}
}
