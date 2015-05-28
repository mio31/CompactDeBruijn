package ie.gmit.SequenceGraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class Graph {

	protected int dimension;

	Map<String, Connector> map = new HashMap<String, Connector>();

	public final TreeSet<Node> multinodes;

	protected Graph(int dimension, Map<String, Connector> map, TreeSet<Node> multinodes) {

		this.dimension = dimension;
		this.map = map;
		this.multinodes = multinodes;
	}

	public Graph(int dimension) {

		this.dimension = dimension;
		this.map = new HashMap<String, Connector>();
		this.multinodes = new TreeSet<Node>();
	}

	public void addSequence(Sequence sequence) {

		// Return if sequence is shorter then the tuple

		if (sequence.getLength() <= dimension)
			return;


		// The sequence spectrum Array
		String[] spectrum = new String[sequence.getLength() - dimension + 1];

		// supporting node.
		Node n = null;

		// An empty connector used in getConnectors method path

		Connector auxiliarConnector = new Connector(Integer.MAX_VALUE, null);

		// The connectors of to the l-tuples in the sequence in the sequence.
		// an empty array of connectors of size spectrum.length
		Connector[] cPath = new Connector[sequence.getLength() - dimension + 1];

		// The nodes containing the sequence

		ArrayList<Node> nodePath = new ArrayList<Node>();

		int index = 0;
		// getting the sequence spectrum from the sequence
		for (String label : sequence.spectrum(dimension))
			spectrum[index++] = label;

		getConnectors(auxiliarConnector, spectrum, cPath, 0, spectrum.length);

		// Creating new Nodes
		
		index = 0;
		
		int startingPoint;
		// The position of the spectrum where a contiguous region of nodes of
		// the same type (new / old).
		while (index < spectrum.length) {

			// Entered a region of the spectrum that is not found in the // graph.
			if (cPath[index].isNew()) {
				
				startingPoint = index;
				// Follows this region, marking the new connectors as "visited"
				// (index < -1)
				while (index < spectrum.length && cPath[index].isNew())

				{
					// System.out.println("index"+
					// index+"  "+connPath[index].index +"  "+spectrum[index]);
					cPath[index].index -= 1;
					index++;

				}
				// System.out.println("index"+
				// index);//+"  "+connPath[index].index);//
				// +"  "+spectrum[index]);
				int f = 0;
				/*
				 * we reached end of spectrum or there is 
				 * 
				 * 
				 * 
				 * 
				 */

				// At this point, either the actual connector points to an old
				// node, or the connector points to another part of the node to
				// be created (and the sequence has a repeat inside itself), or
				// the end of the spectrum was reached. In the three cases it
				// means that we identified the point where the new region ends.
				// And we may create the correspondent node:
				n = new Node(dimension);
				n.size = index - startingPoint + dimension - 1;

				// System.out.println("n lael before  "+n.label);
				n.label = spectrum[startingPoint];
				cPath[startingPoint].index = 0;

				cPath[startingPoint].node = n;

				int nodeIndex = 1;
				for (int i = startingPoint + 1; i < index; i++) {

					n.label += spectrum[i].substring(spectrum[i].length() - 1);
					cPath[i].index = nodeIndex++;
					cPath[i].node = n;
				}
				// System.out.println("N label "+n.label);
				multinodes.add(n);
			}
			if (index < spectrum.length) {

				// Visiting a portion of the spectrum that is alread in the
				// map
				startingPoint = index;
				// System.out.println("Starting point  " + startingPoint);
				n = cPath[index].node();
				int position = cPath[index].index();
				// System.out.println("position  " + position);
				index = startingPoint;
				/*
				 * contiguous(path, i, j) Let path be an array of connectors.
				 * This boolean func- tion returns true if the connectors in
				 * path[i . . . j] correspond to a substring of a node.*
				 * 
				 * index �? min(x : x ≥ start ∧ ¬contiguous(cPath, start, x))
				 */

				while (index < spectrum.length && !cPath[index].isNew()
						&& (cPath[index] == auxiliarConnector || (cPath[index].node() == n && index - startingPoint == cPath[index].index() - position))) {

					index++;

				}

				// A
				if (startingPoint > 0 && !cPath[startingPoint].isFirst()) {
					// System.out.println("CASE A");
					multinodes.remove(n);
					n.cut(cPath[startingPoint].offset(), true);
					multinodes.add(n);
					multinodes.add(n.followThis);

				}
				// B
				else if (index != spectrum.length && !cPath[index - 1].isLast(dimension)) {
					// System.out.println("CASE B");
					multinodes.remove(n);
					n.cut(cPath[index - 1].offset(), false);
					multinodes.add(n);
					multinodes.add(n.followThis);
					n = n.followThis;

				}
				// C
				else if (startingPoint == 0 && index != spectrum.length) {
					// System.out.println("CASE C");
					multinodes.remove(n);
					n.cut(cPath[startingPoint].offset(), true);
					multinodes.add(n);
					multinodes.add(n.followThis);
				}
				// D
				else if (startingPoint != 0 && index == spectrum.length && !cPath[index - 1].isLast(dimension)) {
					// System.out.println("CASE D");
					multinodes.remove(n);
					n.cut(cPath[index - 1].offset(), false);
					multinodes.add(n);
					multinodes.add(n.followThis);
					n = n.followThis;
				} //
				n.mark();
			} // if (index < spectrum.length)
		}// while (index < spectrum.length)

		// NODE PATH
	
		getConnectors(auxiliarConnector, spectrum, cPath, 0, sequence.getLength() - dimension + 1);
		n = cPath[0].node();
		startingPoint = 0;
		int position = cPath[0].index();
		nodePath.add(n);
		for (int i = 1; i < spectrum.length; i++)
			if (cPath[i] != auxiliarConnector && (cPath[i].node() != n || cPath[i].index() - position != i - startingPoint)) {
				n = cPath[i].node();
				startingPoint = i;
				position = cPath[i].index();
				nodePath.add(n);
			}

		// CONNECTING THE PATH
		n = nodePath.get(0);
		Node next = null;
		for (int i = 1; i < nodePath.size(); i++) {
			next = nodePath.get(i);
			n.bindTo(next);
			n = next;
		}

	}

	private void getConnectors(Connector dummy, String[] spectrum, Connector[] connPath, int i, int j) {

		Connector connector;

		String[] spectrumArray = spectrum;
		int index = i;
		while (index < j) {
			connector = map.get(spectrumArray[index]);
			if (connector == null) {
				do {
					// Add new pair to the map-tuple from the spectrum and new
					// empty connector
					// System.out.println("addint +"+spectrum[index]);
					map.put(spectrum[index], (connector = connPath[index] = new Connector(-1, null)));
					connPath[index] = connector;

				} while (index < j && connector == null);

			}
			if (index < j) {

				// "connector offset "+ connector.getOffset());
				connPath[index] = connector;
				// System.out.println(connector.offset+"connectoroffest");

				if (connector.node != null) {
					String label = connector.node().label;
					// System.out.println(label+ " label");
					String auxIndex = label.substring(0, (connector.offset() + dimension));
					// System.out.println(auxIndex+"  auxindex  "+
					// connector.offset()+ "  ");
					int i2 = 0;
					while ((++index < j) && auxIndex == spectrum[index]) {
						connPath[index] = dummy;
						auxIndex = label.substring(i2++, (connector.offset() + dimension));

					}
					connPath[index - 1] = map.get(spectrum[index - 1]);
				} else
					index++;

			}
		}

	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();
		for (Node n : multinodes)
			sb.append(n.toString());
		return sb.toString();
	}

	public long size() {

		return multinodes.size();
	}

	
}
