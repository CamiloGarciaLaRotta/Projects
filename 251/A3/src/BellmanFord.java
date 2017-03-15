import java.util.*;

public class BellmanFord{

    private int[] distances = null;
    private int[] predecessors = null;
    private int source;

    BellmanFord(WGraph g, int source) throws Exception{
        /* Constructor, input a graph and a source
         * Computes the Bellman Ford algorithm to populate the
         * attributes 
         *  distances - at position "n" the distance of node "n" to the source is kept
         *  predecessors - at position "n" the predecessor of node "n" on the path
         *                 to the source is kept
         *  source - the source node
         *
         *  If the node is not reachable from the source, the
         *  distance value must be Integer.MAX_VALUE
         */
        
    	// initialize properties
        this.source = source;
    	this.predecessors = new int[g.getNbNodes()];
    	this.distances = new int[g.getNbNodes()];
    	
    	Arrays.fill(this.distances, Integer.MAX_VALUE);
    	this.distances[0] = 0;

        // relax all edges at most V-1 times 
        for(int i=0;i<g.getNbNodes()-1;i++) {
            for(Edge e:g.getEdges()) {
            	relax(e);
            }
        }
        
        // identify negative edge
        for(Edge e:g.getEdges()) {
        	if (distances[e.nodes[1]] > distances[e.nodes[0]] + e.weight) throw new Exception("Graph contains negative cycle");
        }
    }
    
    // relax the weight of the edge, updating predecessor and distances
    private void relax(Edge e) {
    	int u = e.nodes[0];
    	int v = e.nodes[1];
    	
		if (this.distances[v] > this.distances[u] + e.weight) {
			this.distances[v] = this.distances[u] + e.weight;
			this.predecessors[v] = u;
		}
	}

	public int[] shortestPath(int destination) throws Exception{
        /*Returns the list of nodes along the shortest path from 
         * the object source to the input destination
         * If not path exists an Error is thrown
         */

        ArrayList<Integer> pathList = new ArrayList<Integer>();
        
        int node = destination;
        while(node != this.source) {
        	// check for unreachable node
        	if(this.distances[node] == Integer.MAX_VALUE) throw new Exception("Node cant be reached from source");
        	else {
        		pathList.add(0, node);
            	node = this.predecessors[node];
        	}
        }
        // add last node
        pathList.add(0, node);

        // map ArrayList onto int array
        return pathList.stream().mapToInt(i->i).toArray();
        
    }

    public void printPath(int destination){
        /*Print the path in the format s->n1->n2->destination
         *if the path exists, else catch the Error and 
         *prints it
         */
        try {
            int[] path = this.shortestPath(destination);
            for (int i = 0; i < path.length; i++){
                int next = path[i];
                if (next == destination){
                    System.out.println(destination);
                }
                else {
                    System.out.print(next + "-->");
                }
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    public static void main(String[] args){

        String file = args[0];
        WGraph g = new WGraph(file);
        try{
            BellmanFord bf = new BellmanFord(g, g.getSource());
            bf.printPath(g.getDestination());
        }
        catch (Exception e){
            System.out.println(e);
        }

   } 
}