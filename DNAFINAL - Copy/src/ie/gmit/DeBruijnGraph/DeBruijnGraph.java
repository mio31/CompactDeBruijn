package ie.gmit.DeBruijnGraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.text.html.HTMLDocument.Iterator;

public class DeBruijnGraph {

	

	private LinkedHashMap<String, HashSet<Node>> map = new LinkedHashMap<String, HashSet<Node>>();

	public LinkedHashMap<String, HashSet<Node>> getMap() {
		return map;
	}

	public void setMap(LinkedHashMap<String, HashSet<Node>> map) {
		this.map = map;
	}
	int kmerLength;

	public LinkedHashMap<String, HashSet<Node>> buildGraph(String genome, int k)
			throws IOException {
		kmerLength=k;
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(genome))));
		String line = null;
		while ((line = reader.readLine()) != null) {
			process(line, k);

		}
		reader.close();
		return map;

	}

	public String graphToString(List<Node> graph) {
		String result = "";
		java.util.Iterator<Node> it = graph.iterator();
		while (it.hasNext()) {
			result += it.next().getSequence() + " ->";
		}
		return result;

	}
	public void printPath(Node node){
		if(!node.out().isEmpty())
		System.out.println(node.getSequence());
		printPath(node.out().get(0));
		
			
	}
	int duplicateCount=0;

	private void process(String sequence, int k) {
		String previous = null;
		String next = null;
		for (int i = 0; i < sequence.length() - k + 1; i++) {
			String kmer = sequence.substring(i, i + k);
			if (map.containsKey(kmer)) {
				if(previous!=null){
					map.get(previous).add(new Node(kmer));
					previous=kmer;
				    duplicateCount++;
				}
				

			} else {
				Node node = new Node(kmer);
				HashSet<Node> out=new HashSet<Node>();
				
				if (map.get(previous) != null) {
					map.get(previous).add(node);
				}

				map.put(kmer, out);
				previous = kmer;

			}
			
		}

	}
	

}
