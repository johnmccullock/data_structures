package trie2;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Character-based trie implementation.  Primarily useful for auto-complete text functions.
 * 
 * One of the disadvantages of this implementation is that it is case-sensitive.
 * 
 * Based on code and articles by Saurabh Odhyan at https://github.com/odhyan/trie and 
 * unknown author at https://www.programcreek.com/2014/05/leetcode-implement-trie-prefix-tree-java/
 * 
 * @author John McCullock
 * @version 1.0 2019-03-08
 */
public class Trie
{
	private Node mRoot = new Node();
	private ArrayList<String> mBuffer = null;
	
	public void add(String word)
	{
		if(word == null || word.length() <= 0){
			return;
		}
		this.add(this.mRoot, word, 0);
		return;
	}
	
	private void add(Node root, String word, int pos)
	{
		pos = pos < 0 ? 0 : pos;
		if(pos == word.length()){
			root.words++;
			return;
		}
		root.prefixes++;
		char k = word.charAt(pos);
		if(!root.children.containsKey(k)){
			root.children.put(k, new Node());
		}
		Node child = root.children.get(k);
		this.add(child, word, pos + 1);
		return;
	}
	
	public void remove(String word)
	{
		if(word == null || word.length() <= 0){
			return;
		}
		this.remove(this.mRoot, word, 0);
		return;
	}
	
	private void remove(Node root, String word, int pos)
	{
		pos = pos < 0 ? 0 : pos;
		if(pos == word.length()){
			root.words--;
			return;
		}
		root.prefixes--;
		char k = word.charAt(pos);
		Node child = root.children.get(k);
		this.remove(child, word, pos + 1);
		return;
	}
	
	public boolean contains(String word)
	{
		if(word == null || word.length() <= 0){
			return false;
		}
		return this.countWord(word) > 0 ? true : false;
	}
	
	public int countWord(String word)
	{
		if(word == null || word.length() <= 0){
			return 0;
		}
		return this.countWord(this.mRoot, word, 0);
	}
	
	private int countWord(Node root, String word, int pos)
	{
		int results = 0;
		pos = pos < 0 ? 0 : pos;
		if(pos == word.length()){
			return root.words;
		}
		char k = word.charAt(pos);
		Node child = root.children.get(k);
		if(child != null){
			results = this.countWord(child, word, pos + 1);
		}
		return results;
	}
	
	public int countPrefix(String word)
	{
		if(word == null || word.length() <= 0){
			return 0;
		}
		return this.countPrefix(this.mRoot, word, 0);
	}
	
	private int countPrefix(Node root, String word, int pos)
	{
		int results = 0;
		pos = pos < 0 ? 0 : pos;
		if(pos == word.length()){
			return root.prefixes;
		}
		char k = word.charAt(pos);
		Node child = root.children.get(k);
		if(child != null){
			results = this.countPrefix(child, word, pos + 1);
		}
		return results;
	}
	
	public ArrayList<String> getAllWords()
	{
		this.mBuffer = new ArrayList<String>();
		if(this.mRoot == null){
			return this.mBuffer;
		}
		this.getAllWords(this.mRoot, "");
		return this.mBuffer;
	}
	
	private void getAllWords(Node root, String word)
	{
		if(root.words > 0){
			this.mBuffer.add(word);
		}
		for(char k : root.children.keySet())
		{
			Node child = root.children.get(k);
			this.getAllWords(child, word + k);
		}
		return;
	}
	
	public ArrayList<String> startsWith(String word)
	{
		this.mBuffer = new ArrayList<String>();
		if(word == null || word.length() <= 0){
			return this.mBuffer;
		}
		this.startsWith(this.mRoot, word, 0);
		return this.mBuffer;
	}
	
	private void startsWith(Node root, String word, int pos)
	{
		pos = pos < 0 ? 0 : pos;
		char k = word.charAt(pos);
		Node child = root.children.get(k);
		if(child == null){
			return;
		}
		if(pos == word.length() - 1){
			this.mBuffer = new ArrayList<String>();
			this.getAllWords(child, word);
			return;
		}
		this.startsWith(child, word, pos + 1);
		return;
	}
	
	private class Node
	{
		public HashMap<Character, Node> children = new HashMap<Character, Node>();
		public int words = 0;
		public int prefixes = 0;
	}
}
