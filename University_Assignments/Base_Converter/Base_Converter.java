import java.util.ArrayList;
import java.util.Arrays;
public class tester {
	public static void main(String[] args) {
		class Number{

		//=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
			
		public Number convert(Number A, short Base) {
			
									/////// General variable initializations ///////
			
      java.util.ArrayList<Short> intOutput = new java.util.ArrayList<Short>();
			java.util.ArrayList<Short> nRepOutput = new java.util.ArrayList<Short>();
			java.util.ArrayList<Short> repOutput = new java.util.ArrayList<Short>();
			//output BASE
			java.util.ArrayList<Short> B = new java.util.ArrayList<Short>();
			
      B.add(Base);
			
      // Output BASE in base A
			java.util.ArrayList<Short> BinA = new java.util.ArrayList<Short>();
			short BtoConv = Base;
			
      while(BtoConv>0){
				BinA.add((short) (BtoConv%A.Base));
				BtoConv/=A.Base;
			}
			
			// The following variables will be used in the three different sections
			
			// Tmp remainder, before division its zero
			java.util.ArrayList<Short> tmpR = new java.util.ArrayList<Short>();
			// Tmp result of the multiplication between ratio and divisor
			java.util.ArrayList<Short> tmpMult = new java.util.ArrayList<Short>();
			// Tmp result of the division
			java.util.ArrayList<Short> tmpDiv = new java.util.ArrayList<Short>();
			// Tmp dividend, before division its the int part of A
			java.util.ArrayList<Short> dividend = new java.util.ArrayList<Short>();
			
											/////// handle INT part ///////
			
			// Division starts with remainder zero
			tmpR.add((short) 0);
			
      // Set dividend to be INT part
			for(int i=0;i<A.Int.length;i++){
				dividend.add(A.Int[i]);
			}
			
			// Transformation of (X)-B into (U)-R
			do{	
				tmpR.clear(); tmpMult.clear(); tmpDiv.clear();	
				tmpDiv = ((divide(dividend,BinA,A.Base)));		// Calculate ratio
				tmpMult = (multiply(tmpDiv,BinA,A.Base));		
				tmpR = (substract(dividend,tmpMult,A.Base));	// Calculate reminder

				// Because the remainder will be short
				// We pass it to the final base through 
				// Standard base 10 intermidiate process
				
        intOutput.add(convert(tmpR,A.Base));			// Add reminder to output
				dividend.clear();
				for(int i=0;i<tmpDiv.size();i++){
					dividend.add(tmpDiv.get(i));				// Update dividend
				}
			}while(countNonNull(tmpDiv)>0);
			
											/////// handle FRACTIONAL part ///////
			
			// I will approximate the fractional values by
			// multiplying the number by the base and obtaining 
			// a remainder and and Integer part. The list of
			// int's will result in the fractional part.
			// I will keep track of the remainders, for as soon
			// as we obtain the same dividend we know the numbers
			// following that point are repeated
			
			// We don't take the countNonNull because a zero value after 
			// the comma doesn't mean the same as a zero before the comma
			short aDigits = (short) A.NonRep.length;
			
			// ArrayList array which will contain all 
			// fractions to help find the REP reminder
			java.util.ArrayList<Short>[] tmpRep = new java.util.ArrayList[(int) Math.pow(A.Base, aDigits)];
			
			// Division starts with remainder zero
			tmpR.clear(); tmpMult.clear();
			tmpR.add((short) 0);
			
			// Set tmpMult to be fractional part
			for(int i=0;i<A.NonRep.length;i++){
				tmpMult.add(A.NonRep[i]);
			}
			
			// Initialize variables
      int pos = 0;
      int repPos=0;
			boolean rep = false;
			short tmpDigit = 0;
			
			System.out.println(" Base : "+ BinA);
			System.out.println(" Input [ "+ A.NonRep.length +" ]  : "+ tmpMult+"\n----------------------\n");
			
			do{
				tmpMult = (multiply(tmpMult, BinA, A.Base));
				System.out.println(" Mult : " + tmpMult);
				
        nRepOutput.add(0, cut(tmpMult, aDigits, A.Base));			// Obtain int part
				System.out.println(" nRepOutput : " + nRepOutput);
				
        tmpDigit = cut(tmpMult, aDigits, A.Base);						// Used to subtract digit from fraction
				System.out.println(" tmpDigit : " + tmpDigit);
				
        for(int i=aDigits;i<tmpMult.size();i++){
					tmpMult.set(i, (short)0);
				}
        System.out.println(" Mult : " + tmpMult);

				// Add result to the remainder array
				tmpRep[pos] = tmpMult; pos++;				
				
        //check if repetition occurs
				for(int i=0;i<pos-1;i++){
					if(equals(tmpRep[i],tmpRep[pos-1])) rep = true;
					repPos = i;
				}
			}while(!rep);
		
			
			System.out.println("nRepOutput: "+ nRepOutput +  "\n----------------\nEND nREP");
			
												/////// output ///////
			
			// Create output object
			Number C=new Number(); C.Base=Base; C.Int= new short[intOutput.size()]; 
			C.NonRep=new short[pos-repPos]; C.Rep=new short[repPos]; 
			
			// Fill its attributes
			for(int j=0;j<C.Int.length;j++){
				C.Int[j]=intOutput.get(j);
			}

			int n = 0;

			for(int j=nRepOutput.size()-1;j>=repPos;j--){
				C.NonRep[C.NonRep.length-1-n] = nRepOutput.get(j); n++;
			}

			n=0;

			for(int j=repPos-1;j>=0;j--){
					C.Rep[n]=nRepOutput.get(j); n++;
			}
			
			return C;
		}

		public short cut(ArrayList<Short> A, short aDigits, short B) {
			short output = 0;
			int n = 0;
			for(int i=aDigits;i<A.size();i++){
				output+=((A.get(i))*Math.pow(B, n)); n++;
			}
			
			return output;
		}

						//////// PERSONAL Helper Methods ////////
		
		//---- ( A / B ) in any base ----//
		public java.util.ArrayList<Short> divide(java.util.ArrayList<Short> A, java.util.ArrayList<Short> B, short base) {
			java.util.ArrayList<Short> output = new java.util.ArrayList<Short>();
			
			if(larger(B,A)) output.add((short) 0);
			else{
				java.util.ArrayList<Short> tmpMult = new java.util.ArrayList<Short>();
				java.util.ArrayList<Short> dummyJ = new java.util.ArrayList<Short>();
				java.util.ArrayList<Short> tmpDiv = new java.util.ArrayList<Short>();
				
				short bDigits = (short) countNonNull(B);
				short posA = 0;					// Position of tmpDiv digits
				
				// Build tmp dividend with same size as B from bigger digit to smaller
				for(int i=0;i<bDigits;i++){ 		
					tmpDiv.add(0, (A.get(A.size()-1-posA))); posA++;
				}

				boolean nextDigit = true;
				
        do{
					if(posA>=A.size()) nextDigit =false;
					else{					
						// Only 2 possibilities: B is either smaller than adigits or adigits-1
						while(larger(B,tmpDiv)) {
							posA++; 
							tmpDiv.add(0, (A.get(A.size()-posA))); 
						}	
					}

					for(short j=(short)(base-1);j>=0;j--){
						// To be able to use only one multiplier method
						// wWe user a dummy wrapper for j
						dummyJ.clear();
						dummyJ.add(j);
						tmpMult = multiply(B, dummyJ, base);  
						
            // Verify if j is biggest multiplier
						if(larger(tmpDiv, tmpMult) || equal(tmpDiv, tmpMult)){
							tmpDiv = (substract(tmpDiv, tmpMult, base));
							output.add(j);
							break;
							
						}
					}
					// Test if we can still add digits
					if (nextDigit && posA != A.size()) {
						posA++;
						tmpDiv.add(0, (A.get(A.size() - posA))); 
					}else break;
				}while(nextDigit); 
			}
			
			// Output was added in descending order, it needs to be reversed
			reverse(output);
			return output;
		}
		
		//---- ( A * B ) in any base ----//
		public java.util.ArrayList<Short> multiply(java.util.ArrayList<Short> A, java.util.ArrayList<Short> B, short base) {
			// Max possible size of multiplication is 2*max digits
			java.util.ArrayList<Short> output = new java.util.ArrayList<Short>(2 * A.size());
			for(int i=0;i<2*A.size();i++){
				output.add((short) 0);
			}
			
			short carry = 0;
			short prod = 0;
			java.util.ArrayList<Short> tmpMult = new java.util.ArrayList<Short>();
		
			for(int j=0;j<B.size();j++){
				carry=0;
				tmpMult.clear();
				for(int i=0;i<A.size();i++){
					// Add necessary zeros at beginning of tmp
					if(i==0)for(int k=0;k<j;k++) tmpMult.add((short) 0);
					
					prod = (short) ((A.get(i) * B.get(j)) + carry);
					tmpMult.add((short) (prod%base));
					carry=(short) (prod/base);
				}
				tmpMult.add(carry);
				// To save space add immidiately to output
				output = add(tmpMult,output,base);
			}					
			
			return output;
		}		
		
		//---- ( A + B ) in any base ----//
		private java.util.ArrayList<Short> add(java.util.ArrayList<Short> A, java.util.ArrayList<Short> B, short base) {
			java.util.ArrayList<Short> output = new java.util.ArrayList<Short>(2 * A.size());
			
			short carry = 0;
			// Choose max amount of places in addition
			int ADigits = countNonNull(A); int BDigits = countNonNull(B);
			
			// Redundant code, shall clean if time allows it
			// Same operations, different digit sizes
			if(ADigits>=BDigits){
				for(int i=0;i<ADigits;i++){

					// If there are still digits in B
          if(i<B.size()){  
						output.add((short) ((A.get(i) + B.get(i) + carry) % base));
						carry = (short) ((A.get(i) + B.get(i) + carry) / base);
					}else{
						output.add((short) ((A.get(i) + carry) % base));
						carry = (short) ((A.get(i) + carry) / base);
					}
				}
				output.add(carry);
			}else{
				for(int i=0;i<BDigits;i++){
					if(i<A.size()){ // If there are still digits in A
						output.add((short) ((A.get(i) + B.get(i) + carry) % base));
						carry = (short) ((A.get(i) + B.get(i) + carry) / base);
					}else{
						output.add((short) ((B.get(i) + carry) % base));
						carry = (short) ((B.get(i) + carry) / base);
					}
				}
				output.add(carry);
			}			
			return output;
			
		}
		
		//---- ( A - B ) in any base  ----//
		public java.util.ArrayList<Short> substract(java.util.ArrayList<Short> A, java.util.ArrayList<Short> B, short base){
			java.util.ArrayList<Short> output= new java.util.ArrayList<Short>();
			// To make code uniform and not modify A
			java.util.ArrayList<Short> tmpA= new java.util.ArrayList<Short>();
			for(int i=0;i<A.size();i++){
				tmpA.add(A.get(i));
			}

			short aDigits = (short) countNonNull(tmpA);
			short bDigits = (short) countNonNull(B);
			
      // Max digits for loop
			short maxDigits =  (aDigits > bDigits) ? aDigits : bDigits;
			short tmpResult = 0;
			
      // The following takes into account 3 diff scenarios
			// Not clean code, will review if time allows
			for(int i=0;i<maxDigits;i++){
				if(aDigits>bDigits){
					if(i<bDigits){
						
            // If needed to borrow
						while(tmpA.get(i)<B.get(i)){
							borrow(tmpA, base, i);		
						}
            tmpResult =(short)  ( ( tmpA.get(i) - B.get(i) ));
						
            // If  needed to borrow, substraction might exceed base
						// the 10-answer is the same diff from the base and the actual answer
						if( tmpResult>base-1) tmpResult=(short) (base-(10-tmpResult));	
						output.add(tmpResult);	
					}else{
						// Because no more digits in B, result is simply tmpA
						output.add((short) ((tmpA.get(i))));
					}
				}else if(aDigits==bDigits){
					if(i<aDigits-1){
						// If needed to borrow
						while(tmpA.get(i)<B.get(i)){
							borrow(tmpA, base, i);		
						}					
					}
					tmpResult =(short)  ( ( tmpA.get(i)-B.get(i) ));

					// If  needed to borrow, substraction might exceed base
					// The 10-answer is the same diff from the base and the actual answer
					if( tmpResult>base-1)tmpResult=(short) (base-(10-tmpResult));
					output.add(tmpResult);	
				}else{
					if(i<aDigits-1){
						// If needed to borrow
						while(tmpA.get(i)<B.get(i)){
							borrow(tmpA, base, i);
						}
						tmpResult =(short)  ( ( tmpA.get(i)-B.get(i) ));

						// If  needed to borrow, substraction might exceed base
						// The 10-answer is the same diff from the base and the actual answer
						if( tmpResult>base-1)tmpResult=(short) (base-(10-tmpResult));
						output.add(tmpResult);	 				
					}else if(i<aDigits){
						output.add((short) ((tmpA.get(i)-B.get(i))));
					}else{
						// Because no more digits in tmpA, result is simply -B
						output.add((short) (((-1)*B.get(i))));
					}
				}
			}
					
			return output;
			}
		
		// During substraction, borrow from higher value position
		private void borrow(java.util.ArrayList<Short> A, short base, int i) {
			
			int n = 0;
			// Find first non zero digit to borrow from
			while(i+n+1<A.size()&&A.get(i+n+1)==0) n++;
			
			do{
				// Reduce the bigger digit by 1
				if(base>9){
					A.set((i+n+1), (short) ( ( A.get( ( i+n+1) ) ) -1 ) );
					// Add the digit to the needed position	
					A.set((i+n), (short) ((A.get((i+n)))+1*Math.pow(base, n+1))); 
				}else{
					if(A.get(i+n+1)==10) A.set((i+n+1), (short) (base-1)); 
					else A.set((i+n+1), (short) ( ( A.get( ( i+n+1) ) ) -1 ) ); // iIf not 10 just reduce by 1
					// Add the digit to the needed position	
					A.set((i+n), (short) ((A.get((i + n))) + 10)); 
				}
				n--;
			}while(n>0); 
						
		}

		// Verify if A > B
		public boolean larger(java.util.ArrayList<Short> A, java.util.ArrayList<Short> B) {
			// Two fastest to check cases to save time
			if(countNonNull(A)>countNonNull(B))	return true;
			if(countNonNull(A)<countNonNull(B))	return false;
			
			// If not above, we go through the ArrayLists
			for(int i=countNonNull(A)-1;i>=0;i--){
				if(A.get(i)>B.get(i)) return true;
				if(A.get(i)<B.get(i)) return false;
			}
			return false;
		}
		
		// Verify if A == B
		public boolean equal(java.util.ArrayList<Short> A, java.util.ArrayList<Short> B) {
			if(countNonNull(A) != countNonNull(B))	return false;
			for(int i=countNonNull(A)-1;i>=0;i--){
				if(A.get(i) != B.get(i)) return false;
			}
			return true;
		}
		
		public boolean equals(java.util.ArrayList<Short> A, java.util.ArrayList<Short> B) {
			if(countNonNull(A) != countNonNull(B))	return false;
			for(int i=0;i<countNonNull(A);i++){
				if(A.get(i) != B.get(i)) return false;
			}
			return true;
		}

		// Count amount of non-null greater than zero entries in A
		public int countNonNull(java.util.ArrayList<Short> A){
			// We go through the the ArrayList backwards
			// counting nulls or zeroes, at the first 
			// occurrence of a non-zero the counter stops
			int n=0;
			for(int i=A.size()-1;i>=0;i--){
		        if (A.get(i)==null||A.get(i)==0) n++;
		        else break;
			}
			return A.size()-n;
		}
		
		// Reverse order of A
		public void reverse(java.util.ArrayList<Short> A){
			int i = 0;
			int j = A.size()-1;
			while (i < j) {
			    short temp = A.get(i);
			    A.set( i, A.get(j));
			    A.set( j, temp);
			    i++; j--;
			}
		}		
		
		// Decimal conversion of reminder
		public short convert(java.util.ArrayList<Short> A, short baseA) {
			short output = 0;
			// Transform into decimal because its a reminder of division 
			// by base B, the input will always be < B
			// thus simply transforming to decimal results in the same number as in base B
			for(int i=0;i<A.size();i++){
				output+=(A.get(i)*Math.pow(baseA, i));
			}
			return output;
		}
		
		// Verify if A is greater than zero
		public boolean biggerThanZero(java.util.ArrayList<Short> A) {
			for(int i=0;i<A.size();i++){
				if(A.get(i)<0) return false;
			}
			
			return true;
		}
			
		//=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
		
		public void printShortArray(short[] S) {
			for (int i = S.length-1; i>=0; i--) {
		        System.out.print(S[i]);
		    }
		}
		
		public void printNumber(Number N) {
			System.out.print("(");
			N.printShortArray(N.Int);
			System.out.print(".");
			N.printShortArray(N.NonRep);
			System.out.print("{");
			N.printShortArray(N.Rep);
			System.out.print("})_");
			System.out.println(N.Base);
		}
		
		short Base; short[] Int,NonRep,Rep;
		
		};
		
		Number N1=new Number(); N1.Base=2;
		N1.Int=new short[5]; N1.Int[0]=0; N1.Int[1]=1; N1.Int[2]=0; N1.Int[3]=0; N1.Int[4]=1;
		N1.NonRep=new short[3]; N1.NonRep[0]=1;N1.NonRep[1]=0;N1.NonRep[2]=1;
		N1.Rep=new short[0];
		N1.printNumber(N1);
		
		Number N2=new Number() ;
		short R=15;
		N2=N1.convert(N1,R);
		N2.printNumber(N2);
	}
}
