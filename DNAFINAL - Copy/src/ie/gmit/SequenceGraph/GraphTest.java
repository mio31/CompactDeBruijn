package ie.gmit.SequenceGraph;

import ie.gmit.n50.CalculateN50;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GraphTest {

	public static void main(String[] args) throws IOException {

		int kmerLenght = Integer.parseInt(args[0]);
		String file=args[1];
		
		Graph g = new Graph(kmerLenght);
		ie.gmit.DeBruijnGraph.DeBruijnGraph graph2 = new ie.gmit.DeBruijnGraph.DeBruijnGraph();
		graph2.buildGraph(file, kmerLenght);
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line;
		while ((line = in.readLine()) != null) {
			if (line.startsWith(">")) {

			} else {
				g.addSequence(new Sequence(line));
			}
		}
		in.close();
		int nodeNumber=0;
		BufferedWriter writer = new BufferedWriter( new FileWriter(file+"-seqGraphNodes.fasta"));
		for(Node n:g.multinodes){
			writer.write(">"+nodeNumber);
			writer.newLine();
			writer.write(n.label);
			writer.newLine();
			nodeNumber++;
			
		}
		writer.close();
		System.out.println("Number of Nodes in Sequence Graph  "+g.multinodes.size() );
		System.out.println("Number of Nodes in Debruijn Graph "+graph2.getMap().size());
		CalculateN50 n50= new CalculateN50();
		n50.calculate("min", 10, new File(file+"-seqGraphNodes.fasta"));
		
		
		
		

		

		
		

	}
}
