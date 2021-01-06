package partitionedList2;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * A specialized linked list which is partitioned into different sections.  Items are added by specifying a partition, and are 
 * inserted at the beginning of that partition.  This is similar to a priority queue, except that items are grouped by priority 
 * instead of individually.
 * 
 * The ordering of this list is not automatic, but is manually imposed.
 * 
 * List items are easily accessed by iterator.  The iterator is the most efficient method of removing items when you're already 
 * visiting the whole list.  The list's remove(), size(), contains(), and get() methods all involve linear searches, iterating 
 * through the list from the beginning.
 * 
 * At this time, the iterator only moves from head to tail.
 * 
 * @author John McCullock
 * @version 1.0 2019-05-24
 * @param <T> The type of elements in this list.
 */
public class PartitionedList<T> implements Iterable<T>
{
	private int mNumPartitions = 0;
	private PartitionNode<T>[] mPartitions = null;
	
	public PartitionedList(int numPartitions)
	{
		this.mNumPartitions = numPartitions;
		this.initialize();
		return;
	}
	
	/**
	 * The list starts off as the specified number of partitions (as nodes) linked together by next and previous references.
	 */
	@SuppressWarnings("unchecked")
	private void initialize()
	{
		this.mPartitions = (PartitionNode[])new PartitionNode[this.mNumPartitions];
		for(int i = 0; i < this.mNumPartitions; i++)
		{
			this.mPartitions[i] = new PartitionNode<T>();
		}
		for(int i = 0; i < this.mNumPartitions; i++)
		{
			if(i == 0){
				this.mPartitions[i].next = this.mPartitions[i + 1];
			}else if(i == this.mNumPartitions - 1){
				this.mPartitions[i].prev = this.mPartitions[i - 1];
			}else{
				this.mPartitions[i].prev = this.mPartitions[i - 1];
				this.mPartitions[i].next = this.mPartitions[i + 1];
			}
		}
		return;
	}
	
	/**
	 * Adds a value into a new list element, inserted at the specified partition.
	 * @param partition The partition ("group") where the value is to be inserted.
	 * @param value the item to be added.
	 */
	public void add(int partition, T value)
	{
		PartitionNode<T> parent = this.mPartitions[partition];
		PartitionNode<T> newNode = new DataNode<T>(value);
		PartitionNode<T> child = null;
		if(parent.next != null){
			child = parent.next;
		}
		parent.next = newNode;
		newNode.prev = parent;
		if(child != null){
			newNode.next = child;
			child.prev = newNode;
		}
		return;
	}
	
	public T get(int index)
	{
		T value = null;
		Iterator<T> it = this.iterator();
		int count = 0;
		while(it.hasNext())
		{
			value = it.next();
			if(count == index){
				break;
			}
			count++;
		}
		return value;
	}
	
	public boolean contains(T value)
	{
		boolean results = false;
		Iterator<T> it = this.iterator();
		while(it.hasNext())
		{
			T item = it.next();
			if(item.equals(value)){
				results = true;
				break;
			}
		}
		return results;
	}
	
	private void remove(DataNode<T> node)
	{
		if(node == null){
			return;
		}
		PartitionNode<T> parent = null;
		PartitionNode<T> child = null;
		if(node.prev != null && node.next != null){
			parent = node.prev;
			child = node.next;
			parent.next = child;
			child.prev = parent;
		}else if(node.prev != null){
			parent = node.prev;
			parent.next = null;
		}else if(node.next != null){
			child = node.next;
			child.prev = null;
		}
		return;
	}
	
	public T removeAt(int index)
	{
		T value = null;
		Iterator<T> it = this.iterator();
		int count = 0;
		while(it.hasNext())
		{
			value = it.next();
			if(count == index){
				it.remove();
				break;
			}
			count++;
		}
		return value;
	}
	
	public T remove(T value)
	{
		T results = null;
		Iterator<T> it = this.iterator();
		while(it.hasNext())
		{
			results = it.next();
			if(results.equals(value)){
				it.remove();
				break;
			}
		}
		return results;
	}
	
	public void clear()
	{
		this.initialize();
		return;
	}
	
	public int size()
	{
		int count = 0;
		Iterator<T> it = this.iterator();
		while(it.hasNext())
		{
			it.next();
			count++;
		}
		return count;
	}
	
	public Iterator<T> iterator()
	{
		return new PartitionListIterator();
	}
	
	@SuppressWarnings("hiding")
	public class PartitionNode<T>
	{
		public PartitionNode<T> prev = null;
		public PartitionNode<T> next = null;
	}
	
	@SuppressWarnings("hiding")
	public class DataNode<T> extends PartitionNode<T>
	{
		public T value = null;
		
		public DataNode(T value)
		{
			super();
			this.value = value;
			return;
		}
	}
	
	private class PartitionListIterator implements Iterator<T>
	{
		private PartitionNode<T> mCurrent = null;
		
		public PartitionListIterator()
		{
			/*
			 * In order for this to work, PartitionNodes need to be skipped in favor of Data Nodes. So the constructor
			 * needs to start off at the first DataNode if possible.
			 */
			this.mCurrent = mPartitions[0];
			do
			{
				if(this.mCurrent.next == null){
					break;
				}
				if(this.mCurrent.next instanceof DataNode){
					break;
				}
				if(this.mCurrent.next instanceof PartitionNode){
					this.mCurrent = this.mCurrent.next;
				}
			}while(this.mCurrent.next != null);
			return;
		}
		
		@Override
		public boolean hasNext()
		{
			if(this.mCurrent == null){
				return false;
			}
			do
			{
				if(this.mCurrent.next == null){
					return false;
				}
				if(this.mCurrent.next instanceof DataNode){
					return true;
				}
				if(this.mCurrent.next instanceof PartitionNode){
					this.mCurrent = this.mCurrent.next;
				}
			}while(this.mCurrent.next != null);
			
			return this.mCurrent.next != null && this.mCurrent.next instanceof DataNode;
		}
		
		@Override
		public T next()
		{
			if(!this.hasNext()){
				return null;
			}
			this.mCurrent = this.mCurrent.next;
			return ((DataNode<T>)this.mCurrent).value;
		}
		
		@Override
		public void remove()
		{
			if(!(this.mCurrent instanceof DataNode)){
				return;
			}
			PartitionedList.this.remove((DataNode<T>)this.mCurrent);
			return;
		}
	}
	
	public LinkedList<T> list()
	{
		LinkedList<T> results = new LinkedList<T>();
		Iterator<T> it = this.iterator();
		while(it.hasNext())
		{
			results.add(it.next());
		}
		return results;
	}
	
	@Override
	public String toString()
	{
		StringBuilder b = new StringBuilder();
		Iterator<T> it = this.iterator();
		int i = 0;
		while(it.hasNext())
		{
			b.append("[");
			b.append(i);
			b.append("]");
			b.append(it.next());
			b.append(" ");
			i++;
		}
		return b.toString();
	}
}
