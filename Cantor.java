package spatialHash1;

/**
 * Encodes two integers or long integers into a single number.  Can also decode the generated number back into the original pair.
 * http://dmauro.com/post/77011214305/a-hashing-function-for-x-y-z-coordinates
 * http://szudzik.com/ElegantPairing.pdf
 * http://sachiniscool.blogspot.com/2011/06/cantor-pairing-function-and-reversal.html
 * @author John McCullock
 * @version 1.0  2019-01-16
 */
public class Cantor
{
	public static long encode(long x, long y)
	{
		return (long)Math.floor((x + y) * (x + y + 1) / 2 + x);
	}
	
	public static Pair decode(long encoded)
	{
		long t = (long)Math.floor((-1.0 + Math.sqrt(1.0 + 8 * encoded)) / 2.0);
		long x = (long)Math.floor(encoded - t * (t + 1) / 2);
		long y = (long)Math.floor(t * (t + 3) / 2 - encoded);
		return new Pair(x, y);
	}
	
	public static class Pair
	{
		public long x = 0L;
		public long y = 0L;
		
		public Pair() { return; }
		
		public Pair(long x, long y)
		{
			this.x = x;
			this.y = y;
			return;
		}
	}
}
