import java.io.*;
import java.util.*;




public class FordFulkerson {

	
	public static ArrayList<Integer> pathDFS(Integer source, Integer destination, WGraph graph){
		ArrayList<Integer> Stack = new ArrayList<Integer>();
		
		// if no path was found return an empty stack
		if(! DFS(source, destination, graph, Stack)) Stack.clear();
		
		return Stack;
	}
	
	// recursive DFS which will fill the stack with a found path
	private static boolean DFS(Integer current, Integer destination, WGraph graph, ArrayList<Integer> Stack) {
		
		// push node value into stack
		Stack.add(current.intValue());
		System.out.println(current.intValue() + "\n");
		
		// verify if we found destination
		if(current.equals(destination)) return true;
		
		// recursive call
		for(Edge e:graph.getEdges()) {
			// visit nodes that are adjacent to current node and haven't been visited before
			if(e.nodes[0] == current && e.weight > 0 && Stack.indexOf(e.nodes[1]) < 0)
				if (DFS(e.nodes[0], destination, graph, Stack)) return true;
		}
		
		// did not find path
		Stack.remove(Stack.size()-1);
		return false;
	}


	public static void fordfulkerson(Integer source, Integer destination, WGraph graph, String filePath){
		String answer="";
		String myMcGillID = "260657037"; 
		int maxFlow = 0;
		
		// flow graph with all flows initially at zero
		WGraph flowG = new WGraph(graph);
		for(Edge e:flowG.getEdges()) {
			// in the flow graph, the weight represents the net flow
			e.weight = 0;
		}
		
		// residual graph based on current flowG
		WGraph resG = new WGraph(graph);
		ArrayList<Edge> edges = resG.getEdges();
		for(int i=0;i<edges.size();i++){
			Edge e = edges.get(i);
			edges.add(new Edge(e.nodes[1], e.nodes[0], 0));
		}
        
		// REPLACED FOR EACH BY THE ABOVE FOR BECAUSE OF CONCURRENCY PROB
//		for(Edge e:resG.getEdges()) {
//			// create backwards edges with initial 0 flow
//			resG.addEdge(new Edge(e.nodes[1], e.nodes[0], 0));
//		}
		
		// loop until no more augmenting paths are found in resG
		ArrayList<Integer> augmentingPath = pathDFS(source, destination, resG);
		while(augmentingPath.size() > 0) {
			
			// find bottleneck flow in augmenting path
			int b = findBottleneckFlow(augmentingPath, resG, graph);
			
			// augment given path by b in resG
			augmentPath(augmentingPath, b, flowG, resG);
			maxFlow += b; 
			
			// find next augmenting path
			augmentingPath = pathDFS(source, destination, resG);
		}
		
		answer += maxFlow + "\n" + graph.toString();	
		writeAnswer(filePath+myMcGillID+".txt",answer);
		System.out.println(answer);
	}
	
	// find maximal possible flow increase in given path
	private static int findBottleneckFlow(ArrayList<Integer> path, WGraph resG, WGraph graph) {
		
		// final bottleneck flow
		int b = 0;
		
		// tmp nodes flow and capacity
		int x,y,f,c;
		
		// iterate through all edges in path
		for(int i=0;i<path.size()-1;i++) {
			x = path.get(i);
			y = path.get(i+1);
			f = resG.getEdge(x,  y).weight;
			c = graph.getEdge(x, y).weight;
			
			// on first iteration bottleneck hasnt been found
			// on any other iteration bottleneck is min(b|c-f)
			b = (b==0 || c-f < b) ? c-f : b;
		}
		
		return b;
	}

	// update flowG and resG given the new augmenting path
	private static void augmentPath(ArrayList<Integer> path, int b, WGraph flowG, WGraph resG) {
		
		int x,y;
		Edge e;
		
		for(int i=0;i<path.size()-1;i++) {
			x = path.get(i);
			y = path.get(i+1);
			
			// flowG
			e = flowG.getEdge(x, y);
			flowG.setEdge(x, y, e.weight+b);
			
			// resG
			e = resG.getEdge(x, y);
			resG.setEdge(x, y, e.weight-b);
			e = resG.getEdge(y, x);
			resG.setEdge(y, x, e.weight+b);
		}
	}



	public static void writeAnswer(String path, String line){
		BufferedReader br = null;
		File file = new File(path);
		// if file doesnt exists, then create it
		
		try {
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(line+"\n");	
		bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	 public static void main(String[] args){
		 String file = args[0];
		 File f = new File(file);
		 WGraph g = new WGraph(file);
		 fordfulkerson(g.getSource(),g.getDestination(),g,f.getAbsolutePath().replace(".txt",""));
	 }
}