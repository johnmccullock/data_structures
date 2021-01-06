package spatialHash1;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * SpatialHashMap is a special kind of HashMap that use 2D grid coordinates to store items based on their x-y coordinates.
 * 
 * This is different than a multidimensional key/value map.  Both x and y values are combined into a single key using a pairing 
 * algorithm, the Cantor pairing method in this case. 
 * 
 * As items are added, their x-y coordinates are compared in relation to grid coordinates, yielding x-y grid coordinates which 
 * are paired using the Cantor Pairing algorithm.  The result of the pairing is a long integer, for use as a key in a HashMap. 
 * Each key represents an entire grid square, which can contain multiple items.
 *  
 * @author John McCullock
 * @version 1.0 2019-01-16
 * @param <T>
 */
public class SpatialHashMap<T extends Point2D.Double>
{
	private HashMap<Long, ArrayList<T>> mDataMap = new HashMap<Long, ArrayList<T>>();
	private int mCellSize = 0;
	
	/**
	 * 
	 * @param cellSize representing the width of each grid square.
	 */
	public SpatialHashMap(int cellSize)
	{
		if(cellSize < 1){
			throw new IllegalArgumentException("cellSize parameter cannot be less than one.");
		}
		this.mCellSize = cellSize;
		return;
	}
	
	private long getKey(long x, long y)
	{
		if(x < 0 || y < 0){
			throw new IllegalArgumentException("X and Y parameters cannot be negative.");
		}
		long xp = (long)Math.round((x / this.mCellSize) * this.mCellSize);
		long yp = (long)Math.round((y / this.mCellSize) * this.mCellSize);
		return Cantor.encode(xp, yp);
	}
	
	public void add(long x, long y, T item)
	{
		long key = this.getKey(x, y);
		if(!this.mDataMap.containsKey(key)){
			this.mDataMap.put(key, new ArrayList<T>());
		}
		this.mDataMap.get(key).add(item);
		return;
	}
	
	public ArrayList<T> search(long x, long y)
	{
		ArrayList<T> results = new ArrayList<T>();
		long key = this.getKey(x, y);
		ArrayList<T> recordSet = this.mDataMap.get(key);
		if(recordSet != null){
			for(T item : recordSet)
			{
				results.add(item);
			}
		}
		return results;
	}
}
