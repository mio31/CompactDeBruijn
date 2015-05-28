package ie.gmit;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class Graph {

	protected final int dimension;

	Map<String, Connector> map = new HashMap<String, Connector>();

	public final TreeSet<Node> multinodes;

	protected int lastNumberOfNodes = 0;

	protected Graph(int dimension, Map map,	TreeSet<Node> multinodes) {

		this.dimension = dimension;
		this.map = map;
		this.multinodes = multinodes;
	}

	public Graph(int dimension) {

		this.dimension = dimension;
		this.map = new HashMap();
		this.multinodes = new TreeSet<Node>();
	}

	public void addSequence(Sequence sequence) {

		// If the sequence is smaller than l-tuple, then it's spectrum is surely
		// empty!
		if (sequence.getLength() <= dimension)
			return;

		// VARIABLES:

		// The sequence spectrum.
		String[] spectrum = new String[sequence.getLength()-dimension+1] ;

		// Auxiliar node.
		Node n = null;

		// An auxiliar connector.
		// Connector connector = null;

		// A general connector to be put between the first and the last
		// consecutive connector binded to an existing node in the path of
		// connectors.
		final Connector dummy = new Connector(Integer.MAX_VALUE, null);

		// The connectors corresponding to the l-tuples in the order they appear
		// in the sequence.
		Connector[] connPath = new Connector[sequence.getLength()-dimension+1];

		// The nodes containing the sequence in the order they appear in the
		// sequence.
		ArrayList<Node> nodePath = new ArrayList<Node>();

		// STEP 1: GETTING THE SEQUENCE SPECTRUM!
		int index = 0;
		for (String label : sequence.spectrum(dimension))
			spectrum[index++] = label;
		System.out.println(spectrum.length+" length");
		for (String label : spectrum)
			System.out.println(label);
		getConnectors(dummy, spectrum, connPath, 0, spectrum.length);
		for (Connector c : connPath) { System.out.println(c); }
		/*
		 * 
		 */
		// STEP 2: GETTING/CREATING THE NODES
		// Here a cursor that guides the walk through the sequence spectrum.
		index = 0;
		// The position of the spectrum where a contiguous region of nodes of
		// the same type (new / old).
		int startingPoint;
		while (index < spectrum.length) {

			if (connPath[index].isNew()) {
				// Entered a region of the spectrum that is not found in the
				// biobruijn.core.graph.
				startingPoint = index;
				// Follows this region, marking the new connectors as "visited"
				// (index < -1)
				while (index < spectrum.length && connPath[index].isNew())
					connPath[index++].index--;
				// At this point, either the actual connector points to an old
				// node, or the connector points to another part of the node to
				// be created (and the sequence has a repeat inside itself), or
				// the end of the spectrum was reached. In the three cases it
				// means that we identified the point where the new region ends.
				// And we may create the correspondent node:
				n = new Node(dimension);
				n.size = index - startingPoint + dimension - 1;
				//n.label = new BitSet(4 * (n.size));
				// System.out.println(n.label);
				n.label=spectrum[startingPoint];
				connPath[startingPoint].index = 0;
				// System.out.println(n.label);
				connPath[startingPoint].node = n;
				//int base = 4 * dimension;
				int nodeIndex = 1;
				for (int i = startingPoint + 1; i < index; i++) {
					//n.label.set(base+ (spectrum[i].nextSetBit(4 * (dimension - 1)) % 4));
					//System.out.println(startingPoint+"starting point");
					//System.out.println(spectrum[i]+ "spectrum");
					n.label += spectrum[i].substring(spectrum[i].length() - 1);
					//base += 4;
					connPath[i].index = nodeIndex++;
					connPath[i].node = n;
				}
				multinodes.add(n);
			}
			if (index < spectrum.length) {

				// Visiting a portion of the spectrum that is alread in the
				// biobruijn.core.graph.
				startingPoint = index;
				n = connPath[index].node();
				int position = connPath[index].index();
				index = startingPoint;
				while (index < spectrum.length
						&& !connPath[index].isNew()
						&& (connPath[index] == dummy || (connPath[index].node() == n && index- startingPoint == connPath[index].index()	- position)))
					index++;
				if (index != startingPoint	&& (connPath[index - 1] == dummy || connPath[startingPoint].node() != connPath[index - 1].node())) {
					getConnectors(dummy, spectrum, connPath, startingPoint,	index);
					index = startingPoint;
					while (index < spectrum.length	&& !connPath[index].isNew()	&& (connPath[index] == dummy || (connPath[index].node() == n && index - startingPoint == connPath[index]
									.index() - position)))
						index++;
				}

				// A
				if (startingPoint > 0 && !connPath[startingPoint].isFirst()) {
					multinodes.remove(n);
					n.cut(connPath[startingPoint].offset(), true);
					multinodes.add(n);
					multinodes.add(n.followThis);
					if (!connPath[index - 1].isLast(dimension)
							&& (index != spectrum.length || !n.inRepeat())) {
						multinodes.remove(n);
						n.cut(connPath[index - 1].offset(), false);
						multinodes.add(n);
						multinodes.add(n.followThis);
						n = n.followThis;
					}
				}
				// B
				else if (index != spectrum.length
						&& !connPath[index - 1].isLast(dimension)) {
					multinodes.remove(n);
					n.cut(connPath[index - 1].offset(), false);
					multinodes.add(n);
					multinodes.add(n.followThis);
					n = n.followThis;
					if (startingPoint == 0 && !connPath[0].isFirst()
							&& !n.inRepeat()) {
						multinodes.remove(n);
						n.cut(connPath[startingPoint].offset(), true);
						multinodes.add(n);
						multinodes.add(n.followThis);
					}
				}
				// C
				else if (startingPoint == 0 && index != spectrum.length
						&& !connPath[0].isFirst() && !n.isSink()
						&& !n.inRepeat()) {
					multinodes.remove(n);
					n.cut(connPath[startingPoint].offset(), true);
					multinodes.add(n);
					multinodes.add(n.followThis);
				}
				// D
				else if (startingPoint != 0 && index == spectrum.length
						&& !connPath[index - 1].isLast(dimension)
						&& !n.isSource() && !n.inRepeat()) {
					multinodes.remove(n);
					n.cut(connPath[index - 1].offset(), false);
					multinodes.add(n);
					multinodes.add(n.followThis);
					n = n.followThis;
				} // else { NO NEED TO CUT!!! }\
				n.mark();
			} // if (index < spectrum.length)
		}// while (index < spectrum.length)

		// GETTING THE NODE PATH
		//System.out.println(connPath.length+ "connpath  "+ spectrum.length);
		/*getConnectors(dummy, spectrum, connPath, 0, spectrum.length);
		n = connPath[0].node();
		startingPoint = 0;
		int position = connPath[0].index();
		nodePath.add(n);
		for (int i = 1; i < spectrum.length; i++)
			if (connPath[i] != dummy
					&& (connPath[i].node() != n || connPath[i].index()
							- position != i - startingPoint)) {
				n = connPath[i].node();
				startingPoint = i;
				position = connPath[i].index();
				nodePath.add(n);
			}

		// CONNECTING THE PATH
		n = nodePath.get(0);
		Node next = null;
		for (int i = 1; i < nodePath.size(); i++) {
			next = nodePath.get(i);
			n.bindTo(next);
			n = next;
		}*/
		
		
		// END: CONNECTING THE PATH
		

		
		// MARKING THE REPEATS
		
		/*
		 * 
		 * startingPoint = -1; for (int i = 0; i < nodePath.size(); i++) if
		 * 
		 * (nodePath.get(i).isMarked()) if (startingPoint < 0) startingPoint =
		 * 
		 * i; else ; else if (startingPoint >= 0) { if
		 * 
		 * (nodePath.get(startingPoint).inDegree() > 1|| nodePath.get(i -
		 * 
		 * 1).outDegree() > 1) for (int j = startingPoint; j < i; j++)
		 * 
		 * nodePath.get(j).markRepeat(); startingPoint = -1; } if (startingPoint
		 * 
		 * >= 0) if (nodePath.get(startingPoint).inDegree() > 1 ||
		 * 
		 * nodePath.get(nodePath.size() - 1).outDegree() > 1) for (int j =
		 * 
		 * startingPoint; j < nodePath.size(); j++)
		 * 
		 * nodePath.get(j).markRepeat(); // END: MARKING REPEATS
		 * 
		 * 
		 * 
		 * long id = sequence.getId(); for (MultiDimensionalNode node :
		 * 
		 * nodePath) node.insertSequenceId(id);
		 * 
		 * 
		 * 
		 * // MERGING NODES WHERE IT IS POSSIBLE MultiDimensionalNode previous =
		 * 
		 * null; MultiDimensionalNode actual = nodePath.get(0); for (int i = 1;
		 * 
		 * i < nodePath.size(); i++) { previous = actual; actual =
		 * 
		 * nodePath.get(i); if (previous != actual && previous.inRepeat() ==
		 * 
		 * actual.inRepeat() && previous.outDegree() == 1 && actual.inDegree()
		 * 
		 * == 1) { multinodes.remove(actual); BitSet label = actual.label; int
		 * 
		 * size = actual.size; index = previous.mergeTo(actual);
		 * 
		 * 
		 * 
		 * if (nodePath.get(0).equals(actual)) nodePath.set(0, previous); i--;
		 * 
		 * int toRemove = nodePath.indexOf(actual); while (toRemove > -1) {
		 * 
		 * nodePath.remove(toRemove); toRemove = nodePath.indexOf(actual); }
		 * 
		 * 
		 * 
		 * for (int j = 0; j < size - dimension + 1; j++) { connector =
		 * 
		 * map.get(label.get(4 * j, 4 * (j + dimension))); connector.index =
		 * 
		 * index + j; connector.node = previous; } actual = previous; } }
		 * 
		 */
		
		// END: MERGING NODES
		

		
		// nodePath.get(0).find().add(sequence.getId());
		

		
	}
	

	
	/*
	 * 
	 * public boolean checkConnectors() {
	 * 
	 * 
	 * 
	 * boolean toReturn = true; MultiDimensionalNode oldNode = null;
	 * 
	 * MultiDimensionalNode node = null; Connector connector = null; BitSet l =
	 * 
	 * null; for (BitSet label : map.keySet()) { connector = map.get(label);
	 * 
	 * oldNode = connector.node; try { node = connector.node(); connector.node =
	 * 
	 * oldNode; } catch (Throwable t) { System.err.println("Could not get node "
	 * 
	 * + ie.gmit.DNABitSequence.decode(label, dimension, 1)); toReturn = false;
	 * 
	 * } l = node.label.get(4 * connector.offset(), 4 * (connector.offset() +
	 * 
	 * dimension)); if (!l.equals(label)) {
	 * 
	 * System.err.println(ie.gmit.DNABitSequence.decode( label, dimension, 1) +
	 * 
	 * "!= " + ie.gmit.DNABitSequence.decode(l, dimension, 1) + " | [" +
	 * 
	 * connector.offset() + "] # " + node); toReturn = false; } }
	 * +	 * 
	 * return toReturn; }
	 * 
	 * public boolean checkOverlaps() {
	 * 
	 * String other = null; String actual = null; boolean toReturn = true;
	 * 
	 * for (MultiDimensionalNode node : multinodes) { actual = node.getLabel();
	 * for (MultiDimensionalNode in : node.in) if (in != null) { other =
	 * in.getLabel(); if (!other.substring(other.length() - dimension + 1)
	 * .equals(actual.substring(0, dimension - 1))) { System.err.println(other +
	 * " -X-> " + actual); toReturn = false; } } for (MultiDimensionalNode out :
	 * node.out) if (out != null) { other = out.getLabel(); if
	 * (!actual.substring(actual.length() - dimension + 1)
	 * .equals(other.substring(0, dimension - 1))) { System.err.println(actual +
	 * " -X-> " + other); toReturn = false; } } }
	 * 
	 * return toReturn; }
	 */
	/*
	 * public void findConectedComponents() {
	 * 
	 * if (lastNumberOfNodes == multinodes.size() && components != null) return;
	 * 
	 * lastNumberOfNodes = multinodes.size();
	 * 
	 * TreeMap<Set<Long>, Set<MultiDimensionalNode>> components = new
	 * TreeMap<Set<Long>, Set<MultiDimensionalNode>>();
	 * 
	 * Set<MultiDimensionalNode> component = null; for (MultiDimensionalNode
	 * node : multinodes) { Set<Long> symbol = node.find(); component =
	 * components.get(symbol); if (component == null) { component = new
	 * TreeSet<MultiDimensionalNode>(); components.put(symbol, component); }
	 * component.add(node); }
	 * 
	 * this.components = new ArrayList<Component>(); long componentCounter = 0;
	 * for (Set<MultiDimensionalNode> c : components.values())
	 * this.components.add(new Component(componentCounter++, c));
	 * 
	 * }
	 * 
	 * public Component[] getComponents() {
	 * 
	 * if (components == null) findConectedComponents(); return
	 * components.toArray(new Component[components.size()]); }
	 */
	
	
	private void getConnectors(Connector dummy, String[] spectrum,Connector[] connPath, int i, int j) {

		Connector connector;
		
		String[] spectrumArray = spectrum;
		int index = i;
		while (index < j) {
			connector = map.get(spectrumArray[index]);
			if (connector == null) {
				do {
					//System.out.println("adding" + spectrumArray[index]);
					map.put(spectrum[index],
							(connector = connPath[index] = new Connector(-1,
									null)));
					connPath[index] = connector;

				} while (index < j && connector == null);

			}
			if (index < j) {
				//System.out.println("spectrum   "+ spectrumArray[index]+ "connector offset "+ connector.getOffset());
				connPath[index] = connector;
				// System.out.println(connector.offset+"connectoroffest");

				if (connector.node != null) {
					connPath[index] = connector;
					String label = connector.node().label;
					System.out.println(connPath.length+" connector path");
					String dummyLabel=label.substring(index,index+dimension);
					while ((index < j) && spectrumArray[index].equalsIgnoreCase(dummyLabel)) {					
						index++;				    
						connPath[index] = dummy;					
						dummyLabel = label.substring(index,index+dimension);						
					}
					connPath[index - 1] = map.get(spectrumArray[index - 1]);
					
				} else
					index++;

			}
		}

	}

	/*protected void getConnectors(final Connector dummie, BitSet[] spectrum,
			Connector[] connPath, int start, int stop) {

		Connector connector;
		// GETTING THE CONNECTORS
		int index = start;
		while (index < stop) {
			// System.out.println("index "+index+" stop "+ stop);
			connector = map.get(spectrum[index]);
			if (connector == null) {
				do {
					map.put(spectrum[index],
							(connector = connPath[index] = new Connector(-1,
									null)));
					// if (++index < stop) connector = map.get(spectrum[index]);
				} while (index < stop && connector == null);
			}
			if (index < stop) {
				connPath[index] = connector;
				if (connector.node != null) {
					BitSet label = connector.node().label;
					// System.out.println(label+ " label");
					int auxIndex = label
							.nextSetBit(4 * (connector.offset() + dimension));
					// System.out.println(auxIndex+"  auxindex  "+
					// connector.offset()+ "  ");
					while ((++index < stop)
							&& (auxIndex % 4) == (spectrum[index]
									.nextSetBit((4 * (dimension - 1))) % 4)) {
						connPath[index] = dummie;
						// System.out.print((spectrum[index].nextSetBit((4 *
						// (dimension - 1))) % 4)+" in while auxindex    ");
						auxIndex = label.nextSetBit(auxIndex + 1);
						// System.out.println(auxIndex+"  auxindex  "+
						// connector.offset()+ " in while  after ");
					}
					connPath[index - 1] = map.get(spectrum[index - 1]);
				} else
					index++;
			}
		}
	}*/

	/*
	 * public int getNumberOfComponents() {
	 * 
	 * return components == null ? 0 : components.size(); }
	 */

	/**
	 * Gets the path corresponding to a spectrum in the biobruijn.core.graph.
	 * (Assumes that the biobruijn.core.graph contains the path!)
	 * 
	 * @param spectrum
	 *            The spectrum that determines the path
	 * @return the set of nodes corresponding to the path
	 */
	/*
	 * private ArrayList<MultiDimensionalNode> getPath(Iterator<BitSet>
	 * spectrum) {
	 * 
	 * ArrayList<MultiDimensionalNode> path = new
	 * ArrayList<MultiDimensionalNode>(); MultiDimensionalNode node = null; int
	 * offset = 0; BitSet tuple = null;
	 * 
	 * while (spectrum.hasNext()) { if (path.isEmpty()) { tuple =
	 * spectrum.next(); Connector connector = map.get(tuple); node =
	 * connector.node(); offset = connector.offset(); } else { node =
	 * node.getNext(tuple); offset = 0; } path.add(node); do tuple =
	 * spectrum.next(); while (spectrum.hasNext() && ++offset < node.size -
	 * dimension + 1); } return path; }
	 */

	public int getTupleSize() {

		return dimension;
	}

	/*
	 * public void printDOT(String FileName, Set<MultiDimensionalNode>
	 * componentNodes) {
	 * 
	 * System.out.println("do pici uz"); try { BufferedWriter fw = new
	 * BufferedWriter(new FileWriter(FileName));
	 * 
	 * System.out .println(
	 * "digraph G {\n    node [style=filled,fillcolor=white,shape=box,height=.35,width=.35];\n"
	 * );
	 * 
	 * for (MultiDimensionalNode node : componentNodes) {
	 * System.out.println("    " + node.getLabel().substring(0, dimension) +
	 * " [label=\"" + node.getLabel() + "\""
	 * 
	 * + "];\n"); }
	 * 
	 * System.out.println("\n");
	 * 
	 * for (MultiDimensionalNode node : componentNodes) if (!node.isSink())
	 * System.out.println("    " + node.getPrefix(dimension) + " -> " +
	 * printNeighborhood(node) + ";\n");
	 * 
	 * fw.write("}\n");
	 * 
	 * fw.flush(); fw.close();
	 * 
	 * } catch (IOException e) { e.printStackTrace(); }
	 * 
	 * }
	 */
	/*
	 * public void printFASTA(String FileName, ArrayList<DNABitSequence>
	 * sequences) {
	 * 
	 * try { BufferedWriter fw = new BufferedWriter(new FileWriter(FileName));
	 * 
	 * for (DNABitSequence sequence : sequences) { fw.write(">" +
	 * sequence.getName() + "\n" + sequence.getSequence() + "\n"); }
	 * 
	 * fw.flush(); fw.close();
	 * 
	 * } catch (IOException e) { e.printStackTrace(); }
	 * 
	 * }
	 */

	/*
	 * public void printDOTComponents() { System.out.println("printing dot");
	 * findConectedComponents();
	 * 
	 * Set<MultiDimensionalNode> singletons = new
	 * TreeSet<MultiDimensionalNode>();
	 * 
	 * int counter = 0; String filename = "testFile"; if
	 * (deBruijnFile.lastIndexOf('.') > 0) filename = deBruijnFile.substring(0,
	 * deBruijnFile.lastIndexOf('.')); else filename = "testFile"; for
	 * (Component component : components) { if (component.sequenceIds.size() ==
	 * 1) { // SINGLETON singletons.addAll(component.nodes); continue; }
	 * printDOT(filename + ".component." + (++counter) + ".dot",
	 * component.getNodes()); } if (singletons.size() > 0) printDOT(filename +
	 * ".singletons.dot", singletons); }
	 */

	/*
	 * public void printFASTAComponents(SequencePool pool) {
	 * 
	 * findConectedComponents();
	 * 
	 * ArrayList<DNABitSequence> singletons = new ArrayList<DNABitSequence>();
	 * ArrayList<DNABitSequence> sequences = null;
	 * 
	 * int counter = 0; String filename = null; if
	 * (deBruijnFile.lastIndexOf('.') > 0) filename = deBruijnFile.substring(0,
	 * deBruijnFile.lastIndexOf('.')); else filename = deBruijnFile; for
	 * (Component component : components) { if (component.sequenceIds.size() ==
	 * 1) { for (long id : component.sequenceIds)
	 * singletons.add(pool.retrieve(id)); continue; } sequences = new
	 * ArrayList<DNABitSequence>(); for (long id : component.sequenceIds)
	 * sequences.add(pool.retrieve(id)); printFASTA(filename + ".component." +
	 * (++counter) + ".fasta", sequences); } if (singletons.size() > 0)
	 * printFASTA(filename + ".singletons.fasta", singletons); }
	 */

	/*protected String printNeighborhood(Node node) {

		StringBuffer toReturn = new StringBuffer();

		int counter = 0;

		for (Node n : node.out)
			if (n != null) {
				toReturn.append(n.getPrefix(dimension) + " ");
				counter++;
			}
		return counter > 1 ? "{ " + toReturn.toString() + "}" : toReturn
				.toString();
	}*/

	/*
	 * public void printRepeatedNodes() {
	 * 
	 * int counter = 1;
	 * 
	 * for (MultiDimensionalNode node : multinodes) if (node.inRepeat())
	 * System.out.println(">Repeat " + (counter++) + "\n" + node.getLabel() +
	 * "\n");
	 * 
	 * }
	 */

	/*
	 * public void printUDG(String FileName, Set<MultiDimensionalNode>
	 * componentNodes) {
	 * 
	 * unmarkAll(); Stack<MultiDimensionalNode> s = new
	 * Stack<MultiDimensionalNode>();
	 * 
	 * try { BufferedWriter fw = new BufferedWriter(new FileWriter(FileName));
	 * 
	 * fw.write("[\n"); int indentLevel = 1;
	 * 
	 * MultiDimensionalNode v = null; for (MultiDimensionalNode node :
	 * componentNodes) if ((v = node).inDegree() == 0) s.push(node);
	 * 
	 * if (s.isEmpty()) s.push(v);
	 * 
	 * (s.pop()).printUDG("", indentLevel, s, fw); while (!s.isEmpty()) {
	 * MultiDimensionalNode n = s.pop(); if (!n.isMarked()) { fw.write(",");
	 * n.printUDG("", indentLevel, s, fw); } }
	 * 
	 * fw.write("]\n");
	 * 
	 * fw.flush(); fw.close();
	 * 
	 * } catch (IOException e) { // TODO: handle exception }
	 * 
	 * }
	 */

	/*
	 * public void setDeBruijnFile(String deBruijnFile) {
	 * 
	 * this.deBruijnFile = deBruijnFile; }
	 */

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();
		for (Node n : multinodes)
			sb.append(n.toString());
		return sb.toString();
	}

	public void unmarkAll() {

		for (Node node : multinodes)
			node.unmark();
	}

	public long size() {

		return multinodes.size();
	}

	public static void main(String[] args) {
		// System.out.println("fdsafdsaf");
		Graph g = new Graph(4);
		Sequence s1 = new Sequence("GGACAGATATTTT");
		Sequence s2 = new Sequence("CCTCAGATATCCC");
		Sequence s3 =  new Sequence("CGACACTCTCTCTGGTA");
		g.addSequence(s1);
		System.out.println();
		g.addSequence(s2);
		g.addSequence(s3);
		// g.findConectedComponents();
		// System.out.println(g.lastNumberOfNodes + "  num of nodes");
		System.out.println(g.multinodes.size() + "  Number of Nodes");
		// g.printUDGComponents();

		// System.out.println(g.map.toString());
		System.out.println(g.toString());

		// System.out.println(g.printRepeatedNodes(out););
		// MultiDimensionalNode[] result = g.multinodes.toArray(new
		// MultiDimensionalNode[g.multinodes.size()]);
		// System.out.println(result);
		// System.out.println(g.getNumberOfComponents());

	}

}
