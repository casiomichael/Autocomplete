import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * General trie/priority queue algorithm for implementing Autocompletor
 * 
 * @author Austin Lu
 * @author Jeff Forbes
 */
public class TrieAutocomplete implements Autocompletor {

	/**
	 * Root of entire trie
	 */
	protected Node myRoot;

	/**
	 * Constructor method for TrieAutocomplete. Should initialize the trie rooted at
	 * myRoot, as well as add all nodes necessary to represent the words in terms.
	 * 
	 * @param terms
	 *            - The words we will autocomplete from
	 * @param weights
	 *            - Their weights, such that terms[i] has weight weights[i].
	 * @throws NullPointerException
	 *             if either argument is null
	 * @throws IllegalArgumentException
	 *             if terms and weights are different length
	 */
	public TrieAutocomplete(String[] terms, double[] weights) {
		if (terms == null || weights == null) {
			throw new NullPointerException("One or more arguments null");
		}
		if (terms.length != weights.length) 
			throw new IllegalArgumentException("terms and weights have different lengths!");
		Set<String> strSet = new HashSet<String>(Arrays.asList(terms));
		if (strSet.size() != terms.length)
			throw new IllegalArgumentException("some terms are duplicates!");
		for (double dub:weights)
			if (dub < 0) 
				throw new IllegalArgumentException("some weights are negative!");

		// Represent the root as a dummy/placeholder node
		myRoot = new Node('-', null, 0);

		for (int i = 0; i < terms.length; i++) {
			add(terms[i], weights[i]);
		}
	}

	/**
	 * Add the word with given weight to the trie. If word already exists in the
	 * trie, no new nodes should be created, but the weight of word should be
	 * updated.
	 * 
	 * In adding a word, this method should do the following: Create any necessary
	 * intermediate nodes if they do not exist. Update the subtreeMaxWeight of all
	 * nodes in the path from root to the node representing word. Set the value of
	 * myWord, myWeight, isWord, and mySubtreeMaxWeight of the node corresponding to
	 * the added word to the correct values
	 * 
	 * @throws a
	 *             NullPointerException if word is null
	 * @throws an
	 *             IllegalArgumentException if weight is negative.
	 */
	private void add(String word, double weight) {
		if (word.equals(null)) throw new NullPointerException("word is null!");
		if (weight < 0) throw new IllegalArgumentException("illegal weight: " + weight);
		// TODO: Implement add
		Node current = myRoot;
		for (int k = 0; k < word.length(); k++) {
			char ch = word.charAt(k);
			if (current.mySubtreeMaxWeight < weight) 
				current.mySubtreeMaxWeight = weight;
			if (current.children.get(ch) == null)
				current.children.put(ch, new Node(ch, current, weight));
			current = current.children.get(ch);
		}
		current.setWord(word);
		current.setWeight(weight);
		current.isWord = true;
	}

	/**
	 * Required by the Autocompletor interface. Returns an array containing the k
	 * words in the trie with the largest weight which match the given prefix, in
	 * descending weight order. If less than k words exist matching the given prefix
	 * (including if no words exist), then the array instead contains all those
	 * words. e.g. If terms is {air:3, bat:2, bell:4, boy:1}, then topKMatches("b",
	 * 2) should return {"bell", "bat"}, but topKMatches("a", 2) should return
	 * {"air"}
	 * 
	 * @param prefix
	 *            - A prefix which all returned words must start with
	 * @param k
	 *            - The (maximum) number of words to be returned
	 * @return An Iterable of the k words with the largest weights among all words
	 *         starting with prefix, in descending weight order. If less than k such
	 *         words exist, return all those words. If no such words exist, return
	 *         an empty Iterable
	 * @throws a
	 *             NullPointerException if prefix is null
	 */
	public Iterable<String> topMatches(String prefix, int k) {
		// TODO: Implement topKMatches
		if (k < 0) throw new IllegalArgumentException("Illegal value of k:" + k);
		if (prefix.equals(null)) throw new NullPointerException("prefix is null");
		Node current = myRoot;
		if (k == 0) return new LinkedList<String>();
		PriorityQueue<Node> nodePQ = new PriorityQueue<Node>(k, new Node.ReverseSubtreeMaxWeightComparator());
		PriorityQueue<Term> termPQ = new PriorityQueue<Term>(k, new Term.WeightOrder());
		LinkedList<String> list = new LinkedList<String>();
		for(int i=0; i < prefix.length(); i++){
			if (current.children.get(prefix.charAt(i)) == null) // if prefix not in trie
				return new LinkedList<String>();
			else 
				current = current.children.get(prefix.charAt(i));
		}

		nodePQ.add(current);
		while (nodePQ.size() > 0) {
			current = nodePQ.poll();
			if (current.children.values() != null) { // adds the children to nodePQ
				for (Node n: current.children.values())
					nodePQ.add(n);
			}
			if (current.isWord == true) { // checks to see if head is a word, if so, adds then removes
				termPQ.add(new Term(current.getWord(), current.getWeight()));
				if (termPQ.size() > k)
					termPQ.poll(); // since size is gonna be greater than k, get rid of the lowest term, which is the first one
			}
			if (termPQ.size() >= k && current.mySubtreeMaxWeight < termPQ.peek().getWeight()) {
				break;
			}
		}
		// traverse through all nodes and add it to the queue
		// peeking allows you to check the current head, and you can check to see if its a word
		// basically add it to thoge nodePQ, go through each thing in nodePQ while removing 
		// when it's not a word, removing when it is a word but adding it to termPQ, then move it to an iterable 
		while(termPQ.size() > 0) {
			list.addFirst(termPQ.poll().getWord());
		}
		return list;
	}

	/**
	 * Given a prefix, returns the largest-weight word in the trie starting with
	 * that prefix.
	 * 
	 * @param prefix
	 *            - the prefix the returned word should start with
	 * @return The word from with the largest weight starting with prefix, or an
	 *         empty string if none exists
	 * @throws a
	 *             NullPointerException if the prefix is null
	 */
	public String topMatch(String prefix) {
		// TODO: Implement topMatch
		if (prefix.equals(null)) throw new NullPointerException("prefix is null!");
		Node current = myRoot;
		for(int k=0; k < prefix.length(); k++){
			if (current.children.get(prefix.charAt(k)) == null) // if prefix not in trie
				return "";
			else 
				current = current.children.get(prefix.charAt(k));
		}
		double rootSubtreeMax = current.mySubtreeMaxWeight; // root's subtree max
		while (current.isWord != true && current.mySubtreeMaxWeight == rootSubtreeMax) {
			for (Node n: current.children.values()) {
				if (n.mySubtreeMaxWeight == rootSubtreeMax)
					current = n; // keep going down until you hit a word that matches				
			}
		}
		return current.getWord();
	}

	/**
	 * Return the weight of a given term. If term is not in the dictionary, return
	 * 0.0
	 */
	public double weightOf(String term) {
		// TODO complete weightOf
		if (term.equals(null)) throw new NullPointerException("prefix is null!");
		Node current = myRoot;
		for(int k=0; k < term.length(); k++){
			if (current.children.get(term.charAt(k)) == null) // if prefix not in trie
				return 0.0;
			else 
				current = current.children.get(term.charAt(k));
		}
		double rootSubtreeMax = current.mySubtreeMaxWeight; // root's subtree max
		while (current.isWord != true && current.mySubtreeMaxWeight == rootSubtreeMax) {
			for (Node n: current.children.values()) {
				if (n.mySubtreeMaxWeight == rootSubtreeMax)
					current = n; // keep going down until you hit a word that matches				
			}
		}
		return current.getWeight();
	}
}
