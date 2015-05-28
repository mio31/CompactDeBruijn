package ie.gmit.DeBruijnGraph;

import java.io.IOException;
import java.security.KeyStore.Entry;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.text.html.HTMLDocument.Iterator;

public class Runner {

	public static void main(String[] args) throws IOException {

		DeBruijnGraph graph2 = new DeBruijnGraph();
		graph2.buildGraph("test2.txt", 4);
		
		LinkedHashMap<String, HashSet<Node>> map = graph2.getMap();

		System.out.println("num of nodes  " + map.size());
	
		
	
		

	}

}
