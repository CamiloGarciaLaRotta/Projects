import java.util.ArrayList;
import java.util.Arrays;
public class tester {
	public static void main(String[] args) {
		class Number{

		//=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
			
		public Number convert(Number A, short Base) {
			
									/////// General variable initializations ///////
			
			
			
			//output INT part
			java.util.ArrayList<Short> intOutput = new java.util.ArrayList<Short>();
			//output nonREP part
			java.util.ArrayList<Short> nRepOutput = new java.util.ArrayList<Short>();
			//output REP part
			java.util.ArrayList<Short> repOutput = new java.util.ArrayList<Short>();
			//output BASE
			java.util.ArrayList<Short> B = new java.util.ArrayList<Short>();
			B.add(Base);
			//output BASE in base A
			java.util.ArrayList<Short> BinA = new java.util.ArrayList<Short>();
			short BtoConv = Base;
			while(BtoConv>0){
				BinA.add((short) (BtoConv%A.Base));
				BtoConv/=A.Base;
			}
			
			//the following variables will be used in the three different sections
			
			//tmp remainder, before division its zero
			java.util.ArrayList<Short> tmpR = new java.util.ArrayList<Short>();
			//tmp result of the multiplication between ratio and divisor		//IF TIME FIX SO ITS NOT NECESSARY
			java.util.ArrayList<Short> tmpMult = new java.util.ArrayList<Short>();
			//tmp result of the division
			java.util.ArrayList<Short> tmpDiv = new java.util.ArrayList<Short>();
			//tmp dividend, before division its the int part of A
			java.util.ArrayList<Short> dividend = new java.util.ArrayList<Short>();
			
											/////// handle INT part ///////
			
			//division starts with remainder zero
			tmpR.add((short) 0);
			//set dividend to be INT part
			for(int i=0;i<A.Int.length;i++){
				dividend.add(A.Int[i]);
			}
			
			//transformation of (X)-B into (U)-R
			do{	
				tmpR.clear(); tmpMult.clear(); tmpDiv.clear();	
				tmpDiv = ((divide(dividend,BinA,A.Base)));		//calculate ratio     //TRY WITH DIVISOR 2 DIGITS
				tmpMult = (multiply(tmpDiv,BinA,A.Base));		
				tmpR = (substract(dividend,tmpMult,A.Base));	//calculate reminder
				//because the remainder will be short
				//we pass it to the final base through 
				//standard base 10 intermidiate process
				intOutput.add(convert(tmpR,A.Base));			//add reminder to output
				dividend.clear();
				for(int i=0;i<tmpDiv.size();i++){
					dividend.add(tmpDiv.get(i));				//update dividend
				}
			}while(countNonNull(tmpDiv)>0);
			
											/////// handle FRACTIONAL part ///////
			
			//I will approximate the fractional values by
			//multiplying the number by the base and obtaining 
			//a remainder and and Integer part. The list of
			//int's will result in the fractional part.
			//I will keep track of the remainders, for as soon
			//as we obtain the same dividend we know the numbers
			//following that point are repeated
			
			//we don't take the countNonNull because a zero value after 
			//the comma doesn't mean the same as a zero before the comma
			short aDigits = (short) A.NonRep.length;
			
			//ArrayList array which will contain all 
			//fractions to help find the REP reminder
			java.util.ArrayList<Short>[] tmpRep = new java.util.ArrayList[(int) Math.pow(A.Base, aDigits)];
			
			//division starts with remainder zero
			tmpR.clear(); tmpMult.clear();
			tmpR.add((short) 0);
			
			//set tmpMult to be fractional part
			for(int i=0;i<A.NonRep.length;i++){
				tmpMult.add(A.NonRep[i]);
			}
			
			//position counter of Rep
			int pos = 0;
			//will store pos of first iteration of REP
			int repPos=0;
			//if repetition occurred
			boolean rep = false;
			//digit to subtract from fraction
			short tmpDigit = 0;
			
			
			System.out.println(" Base : "+ BinA);
			System.out.println(" Input [ "+ A.NonRep.length +" ]  : "+ tmpMult+"\n----------------------\n");
			
			do{
				tmpMult = (multiply(tmpMult, BinA, A.Base));
				System.out.println(" Mult : " + tmpMult);
				nRepOutput.add(0, cut(tmpMult, aDigits, A.Base));			//obtain int part
				System.out.println(" nRepOutput : " + nRepOutput);
				tmpDigit = cut(tmpMult, aDigits, A.Base);						//used to subtract digit from fraction
				System.out.println(" tmpDigit : " + tmpDigit);
				for(int i=aDigits;i<tmpMult.size();i++){
					tmpMult.set(i, (short)0);
				}
				System.out.println(" Mult : " + tmpMult);
				//add result to the remainder array
				tmpRep[pos] = tmpMult; pos++;						//store fraction
				//check if repetition occurs
				for(int i=0;i<pos-1;i++){
					if(equals(tmpRep[i],tmpRep[pos-1])) rep = true;
					repPos = i;
				}
			}while(!rep);
		
			
			
			System.out.println("nRepOutput: "+ nRepOutput +  "\n----------------\nEND nREP");
			
												/////// output ///////
			
			//create output object
			Number C=new Number(); C.Base=Base; C.Int= new short[intOutput.size()]; 
			C.NonRep=new short[pos-repPos]; C.Rep=new short[repPos]; 
			
			//fill its attributes
			for(int j=0;j<C.Int.length;j++){
				C.Int[j]=intOutput.get(j);
			}
			int n = 0;
			for(int j=nRepOutput.size()-1;j>=repPos;j--){
				C.NonRep[C.NonRep.length-1-n]=nRepOutput.get(j); n++;
			}
			n=0;
			for(int j=repPos-1;j>=0;j--){
				//C.Rep[j]=nRepOutput.get(j);
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

						//////////////////////////HELPER METHODS//////////////////////////
		
		//---- ( A / B ) in any base ----//
		public java.util.ArrayList<Short> divide(java.util.ArrayList<Short> A, java.util.ArrayList<Short> B, short base) {
			java.util.ArrayList<Short> output = new java.util.ArrayList<Short>();
			
			if(larger(B,A)) output.add((short) 0);
			else{
				java.util.ArrayList<Short> tmpMult = new java.util.ArrayList<Short>();
				java.util.ArrayList<Short> dummyJ = new java.util.ArrayList<Short>();
				java.util.ArrayList<Short> tmpDiv = new java.util.ArrayList<Short>();
				
				short bDigits = (short) countNonNull(B);
				short posA = 0;					//position of tmpDiv digits
				
				//build tmp dividend with same size as B from bigger digit to smaller
				for(int i=0;i<bDigits;i++){ 		
					tmpDiv.add(0, (A.get(A.size()-1-posA))); posA++;
				}
				boolean nextDigit = true;
				do{
					if(posA>=A.size()) nextDigit =false;
					else{					
						//only 2 possibilities: B is either smaller than adigits or adigits-1
						while(larger(B,tmpDiv)) {
							posA++; 
							tmpDiv.add(0, (A.get(A.size()-posA))); 
						}	
					}
					for(short j=(short)(base-1);j>=0;j--){
						//to be able to use only one multiplier method
						//we user a dummy wrapper for j
						dummyJ.clear();
						dummyJ.add(j);
						tmpMult = multiply(B, dummyJ, base);    //SHOULD BE 2 4 BUT TMPDIV is 4 2
						//verify if j is biggest multiplier
						if(larger(tmpDiv, tmpMult)||equal(tmpDiv, tmpMult)){
							tmpDiv = (substract(tmpDiv, tmpMult, base));
							output.add(j);
							break;
							
						}
					}
					//test if we can still add digits
					if (nextDigit && posA!=A.size()) {
						posA++;
						tmpDiv.add(0, (A.get(A.size()-posA))); 
					}else break;
				}while(nextDigit); 
			}
			
			//output was added in descending order, it needs to be reversed
			reverse(output);
			return output;
		}
		
		//---- ( A * B ) in any base ----//
		public java.util.ArrayList<Short> multiply(java.util.ArrayList<Short> A, java.util.ArrayList<Short> B, short base) {
			//max possible size of multiplication is 2*max digits
			java.util.ArrayList<Short> output = new java.util.ArrayList<Short>(2*A.size());
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
					//add necessary zeros at beginning of tmp
					if(i==0)for(int k=0;k<j;k++) tmpMult.add((short) 0);
					prod = (short) ((A.get(i) * B.get(j)) + carry);
					tmpMult.add((short) (prod%base));
					carry=(short) (prod/base);
				}
				tmpMult.add(carry);
				//to save space add immidiately to output
				output = add(tmpMult,output,base);
			}					
			
			return output;
		}		
		
		//---- ( A + B ) in any base ----//
		private java.util.ArrayList<Short> add(java.util.ArrayList<Short> A, java.util.ArrayList<Short> B, short base) {
			java.util.ArrayList<Short> output = new java.util.ArrayList<Short>(2*A.size());
			
			short carry = 0;
			//choose max amount of places in addition
			int ADigits = countNonNull(A); int BDigits = countNonNull(B);
			
			//redundant code, shall clean if time allows it
			//same operations, different digit sizes
			if(ADigits>=BDigits){
				for(int i=0;i<ADigits;i++){
					if(i<B.size()){ //if there are still digits in B
						output.add((short) ((A.get(i)+B.get(i)+carry)%base));
						carry = (short) ((A.get(i)+B.get(i)+carry)/base);
					}else{
						output.add((short) ((A.get(i)+carry)%base));
						carry = (short) ((A.get(i)+carry)/base);
					}
				}
				output.add(carry);
			}else{
				for(int i=0;i<BDigits;i++){
					if(i<A.size()){ //if there are still digits in A
						output.add((short) ((A.get(i)+B.get(i)+carry)%base));
						carry = (short) ((A.get(i)+B.get(i)+carry)/base);
					}else{
						output.add((short) ((B.get(i)+carry)%base));
						carry = (short) ((B.get(i)+carry)/base);
					}
				}
				output.add(carry);
			}			
			return output;
			
		}
		
		//---- ( A - B ) in any base  ----//
		public java.util.ArrayList<Short> substract(java.util.ArrayList<Short> A, java.util.ArrayList<Short> B, short base){
			java.util.ArrayList<Short> output= new java.util.ArrayList<Short>();
			//to make code uniform and not modify A
			java.util.ArrayList<Short> tmpA= new java.util.ArrayList<Short>();
			for(int i=0;i<A.size();i++){
				tmpA.add(A.get(i));
			}
			short aDigits = (short) countNonNull(tmpA);
			short bDigits = (short) countNonNull(B);
			//max digits for loop
			short maxDigits =  (aDigits > bDigits) ? aDigits : bDigits;
			short tmpResult = 0;
			//The following takes into account 3 diff scenarios
			//Not clean code, will review if time allows
			for(int i=0;i<maxDigits;i++){
				if(aDigits>bDigits){
					if(i<bDigits){
						//if needed to borrow
						while(tmpA.get(i)<B.get(i)){
							borrow(tmpA, base, i);		
						}
						tmpResult =(short)  ( ( tmpA.get(i)-B.get(i) ));
						//if  needed to borrow, substraction might exceed base
						//the 10-answer is the same diff from the base and the actual answer
						if( tmpResult>base-1) tmpResult=(short) (base-(10-tmpResult));	
						output.add(tmpResult);	
					}else{
						//because no more digits in B, result is simply tmpA
						output.add((short) ((tmpA.get(i))));
					}
				}else if(aDigits==bDigits){
					if(i<aDigits-1){
						//if needed to borrow
						while(tmpA.get(i)<B.get(i)){
							borrow(tmpA, base, i);		
						}					
					}
					tmpResult =(short)  ( ( tmpA.get(i)-B.get(i) ));
					//if  needed to borrow, substraction might exceed base
					//the 10-answer is the same diff from the base and the actual answer
					if( tmpResult>base-1)tmpResult=(short) (base-(10-tmpResult));
					output.add(tmpResult);	
				}else{
					if(i<aDigits-1){
						//if needed to borrow
						while(tmpA.get(i)<B.get(i)){
							borrow(tmpA, base, i);
						}
						tmpResult =(short)  ( ( tmpA.get(i)-B.get(i) ));
						//if  needed to borrow, substraction might exceed base
						//the 10-answer is the same diff from the base and the actual answer
						if( tmpResult>base-1)tmpResult=(short) (base-(10-tmpResult));
						output.add(tmpResult);	 				
					}else if(i<aDigits){
						output.add((short) ((tmpA.get(i)-B.get(i))));
					}else{
						//because no more digits in tmpA, result is simply -B
						output.add((short) (((-1)*B.get(i))));
					}
				}
			}
					
			return output;
			}
		
		//During substraction, borrow from higher value position
		private void borrow(java.util.ArrayList<Short> A, short base, int i) {
			
			int n = 0;
			//find first non zero digit to borrow from
			while(i+n+1<A.size()&&A.get(i+n+1)==0) n++;
			
			do{
				//reduce the bigger digit by 1
				if(base>9){
					A.set((i+n+1), (short) ( ( A.get( ( i+n+1) ) ) -1 ) );   
					A.set((i+n), (short) ((A.get((i+n)))+1*Math.pow(base, n+1))); //add the digit to the needed position	
				}else{
					if(A.get(i+n+1)==10) A.set((i+n+1), (short) (base-1)); //10 is always equals to the base
					else A.set((i+n+1), (short) ( ( A.get( ( i+n+1) ) ) -1 ) ); //if not 10 just reduce by 1   
					A.set((i+n), (short) ((A.get((i+n)))+10)); //add the digit to the needed position	
				}
				n--;
			}while(n>0); 
						
		}

		// verify if A > B
		public boolean larger(java.util.ArrayList<Short> A, java.util.ArrayList<Short> B) {
			//two fastest to check cases to save time
			if(countNonNull(A)>countNonNull(B))	return true;
			if(countNonNull(A)<countNonNull(B))	return false;
			//if not above, we go through the ArrayLists
			for(int i=countNonNull(A)-1;i>=0;i--){
				if(A.get(i)>B.get(i)) return true;
				if(A.get(i)<B.get(i)) return false;
			}
			return false;
		}
		
		// verify if A == B
		public boolean equal(java.util.ArrayList<Short> A, java.util.ArrayList<Short> B) {
			if(countNonNull(A)!=countNonNull(B))	return false;
			for(int i=countNonNull(A)-1;i>=0;i--){
				if(A.get(i)!=B.get(i)) return false;
			}
			return true;
		}
		
		public boolean equals(java.util.ArrayList<Short> A, java.util.ArrayList<Short> B) {
			if(countNonNull(A)!=countNonNull(B))	return false;
			for(int i=0;i<countNonNull(A);i++){
				if(A.get(i)!=B.get(i)) return false;
			}
			return true;
		}

		// count amount of non-null greater than zero entries in A
		public int countNonNull(java.util.ArrayList<Short> A){
			//we go through the the ArrayList backwards
			//counting nulls or zeroes, at the first 
			//occurrence of a non-zero the counter stops
			int n=0;
			for(int i=A.size()-1;i>=0;i--){
		        if (A.get(i)==null||A.get(i)==0) n++;
		        else break;
			}
			return A.size()-n;
		}
		
		// reverse order of A
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
		
		//decimal conversion of reminder
		public short convert(java.util.ArrayList<Short> A, short baseA) {
			short output = 0;
			//transform into decimal
			//because its a reminder of division 
			//by base B, the input will always be < B
			//thus simply transforming to decimal 
			//results in the same number as in base B
			for(int i=0;i<A.size();i++){
				output+=(A.get(i)*Math.pow(baseA, i));
			}
			return output;
		}
		
		//Verify if A is greater than zero
		public boolean biggerThanZero(java.util.ArrayList<Short> A) {
			for(int i=0;i<A.size();i++){
				if(A.get(i)<0) return false;
			}
			
			return true;
		}
		////////////////////////////////////////////////////////////	
			
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