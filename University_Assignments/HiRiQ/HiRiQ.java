import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class tester {
	// The following array will contain every possible combination for each index, regardless of the colors.
	// COMBINATIONS[n] -> combinations for position n
	private static final int[][][] COMBINATIONS = {
												{{0,1,2},{0,3,8}},
												{{0,1,2},{1,4,9}},
												{{0,1,2},{2,5,10}},
												{{3,4,5},{0,3,8},{3,8,15}},
												{{3,4,5},{1,4,9},{4,9,16}},
												{{3,4,5},{2,5,10},{5,10,17}},
												{{6,7,8},{6 ,13,20}},
												{{6,7,8},{7,8,9},{7 ,14,21}},
												{{6,7,8},{7,8,9},{8,9,10},{0,3,8},{3,8,15},{8,15,22}},
												{{7,8,9},{8,9,10},{9,10,11},{1,4,9},{4,9,16},{9,16,23}},
												{{8,9,10},{9,10,11},{10,11,12},{2,5,10},{5,10,17},{10,17,24}},
												{{9,10,11},{10,11,12},{11,18,25}},
												{{10,11,12},{12,19,26}},
												{{13,14,15},{6,13,20}},
												{{13,14,15},{14,15,16},{7,14,21}},
												{{13,14,15},{14,15,16},{15,16,17},{3,8,15},{8,15,22},{15,22,27}},
												{{14,15,16},{15,16,17},{16,17,18},{4,9,16},{9,16,23},{16,23,28}},
												{{15,16,17},{16,17,18},{17,18,19},{5,10,17},{10,17,24},{17,24,29}},
												{{16,17,18},{17,18,19},{11,18,25}},
												{{17,18,19},{12,19,26}},
												{{20,21,22},{6,13,20}},
												{{20,21,22},{21,22,23},{7,14,21}},
												{{20,21,22},{21,22,23},{22,23,24},{8,15,22},{15,22,27},{22,27,30}},
												{{21,22,23},{22,23,24},{23,24,25},{9,16,23},{16,23,28},{23,28,31}},
												{{22,23,24},{23,24,25},{24,25,26},{10,17,24},{17,24,29},{24,29,32}},
												{{23,24,25},{24,25,26},{11,18,25}},
												{{24,25,26},{12,19,26}},
												{{27,28,29},{15,22,27},{22,27,30}},
												{{27,28,29},{16,23,28},{23,28,31}},
												{{27,28,29},{17,24,29},{24,29,32}},
												{{30,31,32},{22,27,30}},
												{{30,31,32},{23,28,31}},
												{{30,31,32},{24,29,32}}};
	//--------------------------------------------------------------------------------------------------------------------------------//
	
	static class HiRiQ implements Cloneable	{
		
		// int is used to reduce storage to a minimum
		  public int config;
		  public byte weight;
		  public String move;	 // list of substitutions applied to arrive at that configuration

		// Initialize the configuration to one of 4 START setups n=0,1,2,3
		  HiRiQ(byte n) {
			  if (n==0)
			    {config=65536/2;weight=1;}
			  else
				  if (n==1)
				    {config=4403916;weight=11;}
				  else
					  if (n==2)
					    {config=-1026781599; weight=21;}
					  else
					    {config=-2147450879; weight=32;}
		  }
		  
		// Setter
		   public void setMove(String move){
			  this.move = move;
		  }
      
		  boolean IsSolved()
		  {
			  return( (config==65536/2) && (weight==1) );
		  }

		// Transforms the array of 33 booleans to an (int) cinfig and a (byte) weight
		  public void store(boolean[] B)
		  {
		  int a=1;
		  config=0;
		  weight=(byte) 0;
		  if (B[0]) {weight++;}
		  for (int i=1; i<32; i++)
		   {
		   if (B[i]) {config=config+a;weight++;}
		   a=2*a;
		   }
		  if (B[32]) {config=-config;weight++;}
		  }

		// Transform the int representation to an array of booleans.
		// The weight (byte) is necessary because only 32 bits are memorized
		// and so the 33rd is decided based on the fact that the config has the
		// correct weight or not 
		  public boolean[] load(boolean[] B)
		  {
		  byte count=0;
		  int fig=config;
		  B[32]=fig<0;
		  if (B[32]) {fig=-fig;count++;}
		  int a=2;
		  for (int i=1; i<32; i++)
		   {
		   B[i]= fig%a>0;
		   if (B[i]) {fig=fig-a/2;count++;}
		   a=2*a;
		   }
		  B[0]= count<weight;
		  return(B);
		  }
		  
		// Prints the int representation to an array of booleans.
		// The weight (byte) is necessary because only 32 bits are memorized
		// and so the 33rd is decided based on the fact that the config has the
		// correct weight or not 
		  public void printB(boolean Z)
		  {if (Z) {System.out.print("[ ]");} else {System.out.print("[@]");}}
		  
		  public void print()
		  {
		  byte count=0;
		  int fig=config;
		  boolean next,last=fig<0;
		  if (last) {fig=-fig;count++;}
		  int a=2;
		  for (int i=1; i<32; i++)
		   {
		   next= fig%a>0;
		   if (next) {fig=fig-a/2;count++;}
		   a=2*a;
		   }
		  next= count<weight;
		  
		  count=0;
		  fig=config;
		  if (last) {fig=-fig;count++;}
		  a=2;

		  System.out.print("      ") ; printB(next);
		  for (int i=1; i<32; i++)
		   {
		   next= fig%a>0;
		   if (next) {fig=fig-a/2;count++;}
		   a=2*a;
		   printB(next);
		   if (i==2 || i==5 || i==12 || i==19 || i==26 || i==29) {System.out.println() ;}
		   if (i==2 || i==26 || i==29) {System.out.print("      ") ;};
		   }
		   printB(last); System.out.println() ;
		   System.out.println();
		  }
		
  // Override default clone()
		public HiRiQ clone(){
		    try {
		      return (HiRiQ) super.clone();
		    } catch (CloneNotSupportedException e) {
		      return this;
		    }
		}
		
  // Returns W-subs and B-subs for a given node
	// in other words, its children
		public ArrayList<int[]> getCombinations(){
			ArrayList<int[]> comb = new ArrayList<int[]>();
			boolean[] B = new boolean[33]; 
			this.load(B);
			for(int i=0;i<33;i++){
				for(int j=0;j<COMBINATIONS[i].length;j++){
					// Each triplet is [A,B,C]. 
					// Minimized logic function: if !AC+A!C then there is combination
					if(B[COMBINATIONS[i][j][0]] != B[COMBINATIONS[i][j][2]]){
						int[] tmp = COMBINATIONS[i][j];
						// verify if entry exists already
						if(!contains(comb, tmp)) comb.add(COMBINATIONS[i][j]);	
					}
				}
			}

			B = null; 
			return comb;
		}

  // Get only W-subs
		public ArrayList<int[]> getWCombinations() {
			ArrayList<int[]> comb = new ArrayList<int[]>();
			boolean[] B = new boolean[33]; 
			this.load(B);
			for(int i=0;i<33;i++){
				for(int j=0;j<COMBINATIONS[i].length;j++){
					if(B[COMBINATIONS[i][j][0]] && B[COMBINATIONS[i][j][1]] && !B[COMBINATIONS[i][j][2]]
							|| !B[COMBINATIONS[i][j][0]] && B[COMBINATIONS[i][j][1]] && B[COMBINATIONS[i][j][2]]){
						int[] tmp = COMBINATIONS[i][j];
						
						// Verify if entry exists already
						if(!contains(comb, tmp)) comb.add(COMBINATIONS[i][j]);	
					}
				}
			}

			B = null; 
      return comb;
		}
		
	// To avoid adding repeated steps onto the queue
		public boolean contains(ArrayList<int[]> comb, int[] tmp) {
			for(int[] i: comb) {if(i[0]==tmp[0]&&i[1]==tmp[1]&&i[2]==tmp[2]) return true;}
			
			return false;
		}
		
  // Transforms triplets into substitutions		
		public String substitute(int[] n){
			
			boolean[] B = new boolean[33];
			this.load(B);
			String output="";
			
			// There are 4 cases in triplet ABC: Bsub and Wsub from left to right and from right to left
			if(B[n[0]]){
				output = (B[n[1]]) ? (n[0]+"W"+n[2]) : (n[2]+"B"+n[0]);
			}else{
				output = (B[n[1]]) ? (n[2]+"W"+n[0]) : (n[0]+"B"+n[2]);
			}
			
			B = null;	
			return output;
		}
		
	// If input HiRiQ is at 1 step of being solved,
	// this emthod will find the last step and apply it
		public void almostSolved() {
			if(this.config==264&&this.weight==2){
				System.out.println(this.move+"; 4W16");
				System.exit(0);
			}
			if(this.config==24576&&this.weight==2){
				System.out.println(this.move+"; 14W16");
				System.exit(0);
			}
			if(this.config==196608&&this.weight==2){
				System.out.println(this.move+"; 18W16");
				System.exit(0);
			}
			if(this.config==138412032&&this.weight==2){
				System.out.println(this.move+"; 28W16");
				System.exit(0);
			}
		}
	
	}
	
// Apply necessary substitution to new HiRiQ object
	public static HiRiQ apply(HiRiQ H, int[] move) {
		
		// Create HiRiQ object
		HiRiQ newH = (HiRiQ) H.clone();
		boolean[] B = new boolean[33];
		newH.load(B);
		
		// Apply substitution
		if(B[move[0]]){
			if(B[move[1]]){ B[move[0]]=false; B[move[1]]=false; B[move[2]]=true; }
			else{ B[move[0]]=false; B[move[1]]=true; B[move[2]]=true; }
		}else{
			if(B[move[1]]){ B[move[0]]=true; B[move[1]]=false; B[move[2]]=false; }
			else{ B[move[0]]=true; B[move[1]]=true; B[move[2]]=false; }	
		}
		
		newH.store(B);
		
		B = null;	
		return newH;
	}
	
// Polls first element in Queue and adds its possible next moves to the Queue
	public static boolean addToQueue(Queue<HiRiQ> Q, boolean mode){
		
		// First element in queue
		HiRiQ H = (HiRiQ) Q.poll();
		
		System.out.println("NEW LOOP\n Move: "+H.move);
		H.print();
		
		// Get possible next moves
		// mode == true : both W-sub and B-sub moves
		// mode == false : only W-sub moves
		ArrayList<int[]> moves = (mode)? H.getCombinations() : H.getWCombinations();
		
		// Create nodes and add to queue
		while(!moves.isEmpty()){
			HiRiQ newH = (apply(H, moves.get(0)));
			newH.setMove((H.move+"; "+H.substitute(moves.get(0))));
			
			// check if is within range of solving
			newH.almostSolved();
			
			System.out.println("Move: "+newH.move);
			newH.print();
			if(newH.IsSolved()){
				System.out.println("SOLUTION FOUND: " +newH.move);
				System.exit(0);
			}
			Q.add(newH);
			moves.remove(0);
		}
		
		if(Q.isEmpty()){
			System.out.println("NO SOLUTION FOUND");
			return false;
		}
		
		H = null; moves = null;
		return true;
	}

// Create initial configuration
	private static HiRiQ setup(int node) {
		HiRiQ H = new HiRiQ((byte) node);
		H.setMove("conf " + node);
		
		return H;
	}
	
	public static void main(String[] args) {
		
	// I will implement a Queue. The queue will start with the initial configuration.
	// it will then poll() and add the first element possible next steps, verifying as it adds if the solution is found
	// the process will continue until no more moves are possible
	// in other words Im implementing a logical tree BSF 
		
	// Halfway through development I realized the way the game plays has the following characteristic:
	// the piece moved will most of the times give place to the next required move to solve the puzzle.
	// thus DSF would be more convenient from a general POV. Due to time constraints I will keep my code 
	// BSF because it works, even thought its might be slower for configuration such as n=3
		
	// Setup initial configuration
		HiRiQ H = setup(0);		

  /* Exemple on how to set up  personalized initial game configuration
		boolean[] B = new boolean[33];
		H.load(B);
		B[16]=false;B[17]=true;B[4]=true;
		B[11]=false;B[24]=true;
		H.store(B);
  */

		System.out.println("INITIAL:\n");
		H.print();
		
  // No need to start queue if game is already solved
		if(H.IsSolved()){
			System.out.println("INPUT CONFIGURATION WAS ALREADY SOLVED");
			System.exit(0);
		}	
		
		// Add base configuration to queue
		Queue<HiRiQ> Q = new LinkedList<HiRiQ>();
		Q.add(H);
				
		// It will try W-subs first, if no solution is found it will then try with B-subs and W-subs
		boolean availableMoves;
		do{
			availableMoves = addToQueue(Q, false);
		}while(availableMoves);
		
		// Reset initial configuration
		H = setup(0);
		H.load(B);
		B[16]=false;B[17]=true;B[4]=true;
		B[11]=false;B[24]=true;
		H.store(B);
		
		Q.add(H);
		
		// Try with B-subs and W-subs
		do{
			availableMoves = addToQueue(Q, true);
		}while(availableMoves);		
	}
}
