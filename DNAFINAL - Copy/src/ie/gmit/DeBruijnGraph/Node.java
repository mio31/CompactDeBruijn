package ie.gmit.DeBruijnGraph;

import java.util.ArrayList;
import java.util.List;

public class Node {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sequence == null) ? 0 : sequence.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (sequence == null) {
			if (other.sequence != null)
				return false;
		} else if (!sequence.equalsIgnoreCase(other.sequence))
			return false;
		return true;
	}

	private String sequence;
	private int occurences = 0;
	private Node next;
	public Node getNext() {
		return next;
	}

	public void setNext(Node linkedListNode) {
		this.next = linkedListNode;
	}

	private List<Node> nextNodes =new ArrayList<Node>();

	

	public List<Node> out() {
		return nextNodes;
	}

	public void setNextNodes(List<Node> nextNodes) {
		this.nextNodes = nextNodes;
	}

	public Node(String sequence) {
		super();
		this.sequence = sequence;
		this.occurences = 1;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public int getOccurences() {
		return occurences;
	}

	public void setOccurences() {
		this.occurences += 1;
	}

	
	

}
