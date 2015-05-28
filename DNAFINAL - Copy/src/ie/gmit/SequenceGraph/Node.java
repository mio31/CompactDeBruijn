package ie.gmit.SequenceGraph;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class Node implements Comparable<Node> {

    protected int dimension;
    public String label;

    @Override
    public int compareTo(Node o) {
	
	return this.getLabel().compareTo(o.getLabel());
    }

    int offset = 0;

    protected boolean inRepeat = false;

    // The size of the  label
    public int size = 0;

    protected TreeSet<Node> in = new TreeSet<Node>();

    protected TreeSet<Node> out = new TreeSet<Node>();

    Node followThis = null;

    protected boolean mark = false;

    public Node(int dimension) {

	this.dimension = dimension;
    }

    public void addNext(Node n) {

	out.add(n);
    }

    public void addPrevious(Node n) {

	in.add(n);
    }


    /**
     * Binds the <code>this</code> node to a given one without testing for an
     * overlap of size dimension-1.
     * 
     * @param nextNode
     *            The node to be binded tho this node.
     */
    void bindTo(Node nextNode) {

	this.out.add(nextNode);
	nextNode.in.add(this);

    }

    void unbindTo(Node nextNode) {

	this.out.remove(nextNode);
	nextNode.in.remove(this);
    }

    public boolean compare(Node other) {
	return this.label == other.label;
    }

    void cut(int point, boolean first) {

	Node followThis = new Node(dimension);
	String newLabel = null;

	// CUT LEFT
	if (first) {
	   
	    followThis.size = point + dimension - 1;
	   
	    followThis.label = label.substring(0, followThis.size);
	    newLabel = label.substring(point, size);
	    this.size = size - point;
	}
	// CUT RIGHT
	else {
	    followThis.size = point + dimension;	   
	    followThis.label = label.substring(0, followThis.size);
	    newLabel = label.substring((point + 1), size);
	    this.size = size - point - 1;
	}

	for (Node n : in) {
	    n.out.remove(this);
	    n.out.add(followThis);
	}

	this.label = newLabel;
	followThis.followThis = this.followThis;
	followThis.offset = this.offset;
	this.offset -= first ? point : (point + 1);

	TreeSet<Node> tmp = followThis.in;
	followThis.in = this.in;
	this.in = tmp;

	followThis.bindTo(this);

	this.followThis = followThis;

	followThis.mark = this.mark;
	followThis.inRepeat = this.inRepeat;
	// followThis.sequences = this.sequences.duplicate();

    }

    public String getLabel() {

	return label;// DNABitSequence.decode(label, size, 1);
    }

    public Set<Node> getNexts() {

	return out;
    }

    public Set<Node> getPrevious() {

	return in;
    }

    public int inDegree() {

	return in.size();
    }

    public boolean inRepeat() {

	return inRepeat;
    }

    public boolean isMarked() {

	return mark;
    }

    public boolean isSink() {

	return outDegree() == 0;
    }

    public boolean isSource() {

	return inDegree() == 0;
    }

    public void mark() {

	mark = true;
    }

    public void markRepeat() {

	inRepeat = true;
    }

    /**
     * Merges a multidimensional node to a next node. No consistency test is
     * made. If the suffix of this node doesn't match the prefix of
     * <code>next</code>, the biobruijn.core.graph is no more consistent with
     * the collection of strings it should represented.
     * 
     * @param next
     *            The node to be merged. This node should be removed from the
     *            biobruijn.core.graph.
     * 
     * @return The position of the first extended l-tuple.
     */
    /*
     * public int mergeTo(Node next) {
     * 
     * int toReturn = size - dimension + 1 - offset;
     * 
     * // Actualize the label. BitSet newLabel = new BitSet(4 * (size +
     * next.size - dimension + 1)); newLabel.or(label); //sequences =
     * sequences.union(next.sequences); label = newLabel; int index =
     * next.label.nextSetBit(4 * (dimension - 1)); int auxOffset = 4 * size;
     * while (index >= 0) { label.set(auxOffset + index - 4 * (dimension - 1));
     * index = next.label.nextSetBit(index + 1); } size += next.size - dimension
     * + 1;
     * 
     * // Actualize the edges. out = next.out; for (Node n : out) {
     * n.in.remove(next); n.in.add(this); if (n.followThis == next) n.followThis
     * = next.followThis; }
     * 
     * next.out = next.in = null;
     * 
     * return toReturn;
     * 
     * }
     */

    // public void mergeWith(MultiDimensionalNode dbnode) {
    //
    // MultiDimensionalNode n = dbnode;
    // int index = label.nextSetBit(4 * (dimension - 1)) % 4;
    // for (MultiDimensionalNode node : in) {
    // if (node == null) continue;
    // node.out[index] = this;
    // in[node.label.nextSetBit(4 * (node.size - dimension)) % 4] = node;
    // }
    // index = label.nextSetBit(4 * (size - dimension)) % 4;
    // for (MultiDimensionalNode node : out) {
    // if (node == null) continue;
    // node.in[index] = this;
    // out[node.label.nextSetBit(4 * (node.size - dimension)) % 4] = node;
    // }
    // this.color = Palete.mix(color, n.getColor());
    // n.sayGoodBye();
    // }

    public String nextLabel() {

	if (out.size() > 0)
	    return ((TreeSet<Node>) out).first().getLabel();
	return null;
    }

    public int offset() {

	return offset;
    }

    /*
     * public Set<MultiDimensionalNode> out() {
     * 
     * RedBlackSet<MultiDimensionalNode> s = new
     * RedBlackSet<MultiDimensionalNode>(); for (MultiDimensionalNode n : out)
     * s.add(n); return s; }
     */

    public int outDegree() {

	return out.size();
    }

    public String previousLabel() {

	if (in.size() > 0)
	    return in.floor(this).getLabel();
	return null;
    }

    public void removeNext(Node n) {

	out.remove(n);
    }

    public void removePrevious(Node n) {

	in.remove(n);
    }

    public void sayGoodBye() {

	if (in != null) {
	    for (Node node : in)
		node.out.remove(this);
	    in.clear();
	}

	if (out != null) {
	    for (Node node : out)
		node.in.remove(this);
	    out.clear();
	}
    }

    /*
     * public void setColor(int color) {
     * 
     * this.color = color; }
     */

    void setLabel(String label) {

	this.label = label;
	this.size = label.length();
    }

    @Override
    public String toString() {

	String in = "[ ";
	for (Node n : this.in)
	    in += n == null ? "null " : n.label + " ";
	in += "]";

	String out = "[ ";
	for (Node n : this.out)
	    out += n == null ? "null " : n.label + " ";
	out += "]";

	return "\n" + in + " " + label + " "+label.length()+ " " + out;
    }

    public void unmark() {

	mark = false;
    }

    /*
     * public boolean sameSequenceSet(MultiDimensionalNode node) {
     * 
     * if (node == null) return false; return sequences.equals(node.sequences);
     * }
     * 
     * public RedBlackLongMultiset sequenceSet() {
     * 
     * return sequences; }
     */

    public Node next() {

	for (Node node : out)
	    if (node != null)
		return node;
	return null;
    }

    public boolean preceeds(Node n) {

	for (Node node : out)
	    if (node == n)
		return true;
	return false;
    }

    /*
     * boolean lowComplexity() {
     * 
     * int[] counters = { 0, 0, 0, 0 }; for (int i = 0; i < size; i++)
     * counters[label.nextSetBit(4 * i) % 4]++; for (int i = 0; i < 4; i++) if
     * (counters[i] > 2 * size / 5) { System.out.print(this.toString()); return
     * true; } return false; }
     */

    /*
     * public int[] integerSequence() {
     * 
     * int[] toReturn = new int[size];
     * 
     * for (int i = 0; i < size; i++) toReturn[i] = 8 * (label.get(4 * i) ? 1 :
     * 0) + 4 (label.get(4 * i + 1) ? 1 : 0) + 2 (label.get(4 * i + 2) ? 1 : 0)
     * + (label.get(4 * i + 3) ? 1 : 0);
     * 
     * return toReturn;
     * 
     * }
     */

}
