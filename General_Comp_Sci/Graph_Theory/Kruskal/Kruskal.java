import java.util.*;

public class Kruskal{

    public static WGraph kruskal(WGraph g){
    	
    	WGraph mst = new WGraph();
    	
        // 1 start with each bertex in its own component -> trivial
    	DisjointSets ds = new DisjointSets(g.getNbNodes());
    	
    	// 2 repeatidly merge 2 components into 1 by choosing a light edge
    	for(int i=0;i<g.listOfEdgesSorted().size();i++) {
    		// retrieve minimal weighted edge and its nodes
    		Edge e = g.listOfEdgesSorted().remove(i);
    		
    		// only add smallest safe cut
    		if(IsSafe(ds, e)) mst.addEdge(e);
    	}

        return mst;
    }

    public static Boolean IsSafe(DisjointSets p, Edge e){
    	// edge is safe	if it respects the vertex
    	//				if it is a light cut
    	
    	// retrieve nodes
		int n1 = e.nodes[0];
		int n2 = e.nodes[1];

        if(p.find(n1) != p.find(n2)){
        	//unite for future inspections
        	p.union(n1, n2);
        	
        	return true;
        }
        
        // not safe edge
        return false;
    }

    public static void main(String[] args){

        String file = args[0];
        WGraph g = new WGraph(file);
        WGraph t = kruskal(g);
        System.out.println(t);

   } 
}