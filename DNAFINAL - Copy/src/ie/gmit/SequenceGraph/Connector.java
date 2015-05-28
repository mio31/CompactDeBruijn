package ie.gmit.SequenceGraph;

public class Connector {

	int index;

	Node node;

	public Connector(int index, Node node) {
		this.index = index;
		this.node = node;
	}

	/**
	 * Tests if the connector is new.
	 * 
	 * A connector is new if an only if the last thing that happened with it was
	 * precisely its creation. During the creation of new nodes in the biobruijn.core.graph,
	 * every time the algorithm finds an existing new connector, the new
	 * connetor's index should be set to a negative number different from -1.
	 * Otherwise there is no way to identify repeats inside the new-inserted
	 * sequence.
	 * 
	 * @return <code>TRUE</code> if and only if the connector is new.
	 */
	public boolean isNew() {
		return (index == -1 && node == null);
	}

	/**
	 * Tests if the connector points to the last l-tuple of a multidimensional
	 * node.
	 * 
	 * @param dimension
	 *            The dimension of the de Bruijn biobruijn.core.graph.
	 * @return <code>TRUE</code> if the connector points to the last l-tuple
	 *         of the multidimensional node. <code>FALSE</code> otherwise.
	 */
	public boolean isLast(int dimension) {
		return (offset() == node.size - dimension);
	}

	/**
	 * Tests if the connector points to the first l-tuple of a multidimensional
	 * node.
	 * 
	 * @param dimension
	 *            The dimension of the de Bruijn biobruijn.core.graph.
	 * @return <code>TRUE</code> if the connector points to the first l-tuple
	 *         of the multidimensional node. <code>FALSE</code> otherwise.
	 */
	public boolean isFirst() {
		return (offset() == 0);
	}

	/**
	 * Gets the l-tuple index, i.e., the index of the l-tuple in the original
	 * (not cutted) node. This index may differ from the actual l-tuple offset
	 * if the node was cutted.
	 * 
	 * @return The l-tuple index.
	 */
	public int index() {
		return index;
	}

	/**
	 * Gets the l-tuple offset, i.e., the position of the l-tuple in the
	 * connected  node.
	 * 
	 * @return The l-tuple offset.
	 */
	public int offset() {
		while (index + node.offset < 0)
			node = node.followThis;
		return index + node.offset;
	}

	/**
	 * Updates the pointer (if necessary) and gets the multinode containing the
	 * referenced l-tuple.
	 * 
	 * @return The node containing the corresponding l-tuple.
	 */
	public Node node() {
		while (index + node.offset < 0)
			node = node.followThis;
		return node;
	}

	public int toS(){
		return this.hashCode();
	}
	@Override
	public String toString() {

		if (node == null)
			if (index == Integer.MAX_VALUE)
				return " [#]";
			else
				return " -[" + index + "]-> null";

		return " -[" + offset() + "]-> " + node.getLabel();

	}

}
