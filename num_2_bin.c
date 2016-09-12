#include <stdio.h>
#include <stdlib.h>

// Measure length of char[]
int lengthChar(char *c);

// Verify if char[] contains only valid characters
int isNumber(char *c);

// Verify if input is outside float range
int outOfRange(double input);

// Find the first occurence of a point in the string 
int findPoint(char *c);

// Print hex array
void printHex(int i);

main(int argc, char *argv[]){
	// I will use unsigned long long type variables for the integer section
	// because they offer the largest range possible of the integer types.
	// for the fractional part I will use double as its also the one which 
	// offers the largest range of floating points.

	// Ranges:	unsigned long long	[0 - 4291967295]
	//	   			double 			1.7E+/-308	
	
	/*---------- verify input ----------*/
	
	// Verify if only one argument of numerical value was entered
	if(argc != 2 || isNumber(argv[1]) == 1){
		 printf("Please enter a valid double number\n"); return 1;
	}
	
	// Transform char[] into double
	// Take into account that this generates an approx result,
	// Thus already creating an error w/r to theroretical value
	double input=strtod(argv[1],0);

	// Special case 
	if(input==0){
		printf("0\t(bin)\n0\t(hex)\n"); return 0;
	}	
	
	// Check if input is inside range 	
	if(outOfRange(input)==1){
		printf("Number outside of range of long\n"); return 1;
	}
	
	// Divide into integer and fractional part
	unsigned long long integer = strtoull(argv[1],NULL,10);
	double fractional =(double) input - integer;
	
	int point = findPoint(argv[1]);
	
	int sizeInput = lengthChar(argv[1]) / 3;         
	
	//number of integer and fractional digits
	int intDig, fracDig;
	
	if (point==0){
		intDig = sizeInput;	
		fracDig = 0;
	}else{
		intDig = point;
		fracDig = sizeInput - point-1; 
	}
	
	/*---------- integer convertion ----------*/

	int intInput = input;
 	
	// Max possible amount of digits dn = d10 * log 10 / log n
	
	// Integer output (bin))
	// approx log10/log2. Thus one more cause of lost of precision
	// the value has been hardcoded to counter the absence of math.h
	const double ratioLog2 = 3.3219280948873623478703194;  

	// The +1 is to bypass the absence of floor method (no math.h)
	unsigned long long intBinInputDigits = intDig * ratioLog2 + 1; 
			
	// Array that will contain final output digits
	int intBinOutput[intBinInputDigits];

	// Integer output (hex). Approx log10/log16. 
	const double ratioLog16 = 0.8304820237218405869675798;  
	
	unsigned long long intHexInputDigits = intDig*ratioLog16+1;
	
	int intHexOutput[intHexInputDigits];				

	
	// Temporary vars for mult and div
	unsigned long long dividend = intInput;
	unsigned long long tmpDiv=0;
		
	// Position counterss
	int intBinPos=0;	
	int intHexPos=0;	
	
	// Global array position counter
	int j;

	// We will obtrain the digits by long division method

	do{ // binary
		dividend /= 2;
		tmpDiv =  intInput - (dividend * 2);	
		intBinOutput[intBinPos++] = tmpDiv; 	
		intInput=dividend;	
	}while(dividend>0);
	
	// Reinitialize variables for another conversion
	intInput = input;
	dividend = intInput;

	do{ // Hexadecimal
		dividend/=16;
		tmpDiv =  intInput - (dividend * 16);	
		intHexOutput[intHexPos++] = tmpDiv; 	
		intInput=dividend;	
	}while(dividend>0);
	
	/*---------- fractional convertion  ----------*/
		
	unsigned long long fracBinInputDigits = fracDig*ratioLog2+1;
	int fracBinOutput[fracBinInputDigits];		
	
	unsigned long long fracHexInputDigits = fracDig*ratioLog16+1;
	int fracHexOutput[fracHexInputDigits];				
		
	//tmp variables for convertion	
	int tmpMult = 0;
	double tmpResult = fractional;
	
	//we will obtain the digits by multiplication method
	
	// Binary
	for(j=0;j<fracBinInputDigits;j++){
		tmpResult  *= 2;
		tmpMult = (int) tmpResult;
		tmpResult -= tmpMult;
		fracBinOutput[j] = tmpMult; 
	}
	
	//reinitialize variables for another converion	
	tmpResult = fractional;
	tmpMult = 0;

	// Hexadecimal	
	for(j=0;j<fracHexInputDigits;j++){
		tmpResult *= 16;
		tmpMult = (int) tmpResult;
		tmpResult -= tmpMult;
		fracHexOutput[j] = tmpMult;		
	}
		
	/*---------- output ----------*/
	
	// Binary
	for(j=intBinPos-1;j>=0;j--){
		printf("%d",intBinOutput[j]);
	}
	printf(".");
	for(j=0;j<fracBinInputDigits;j++){
		printf("%d",fracBinOutput[j]);
	}
	printf("\t(bin)\n");	
	
	// Hexadecimal
	for(j=intHexPos-1;j>=0;j--){
		printHex(intHexOutput[j]);
	}
	printf(".");
	for(j=0;j<fracHexInputDigits;j++){
		printHex(fracHexOutput[j]);
	}
	printf("\t\t(hex)\n");	
	
	return 0;
}

void printHex(int i){
	switch(i){
		case 10 : printf("A"); break; 
		case 11 : printf("B"); break; 
		case 12 : printf("C"); break; 
		case 13 : printf("D"); break; 
		case 14 : printf("E"); break; 
		case 15 : printf("F"); break; 
		default : printf("%d",i);
	}	
}

int lengthChar(char *c){
	// Model for this methos was found online to counter the restriction over the library string.h
	// Its a recursive method that loops until the char is the end of array \0
	
	static int length=0;
	if(*c!='\0'){		
		lengthChar(++c);
		length++;		
	}else{				
		return length;	
	}
}

int findPoint(char *c){
	int i; 	int l=lengthChar(c);
	
	for(i=0;i<l;i++){
		if(c[i]==46) return i;	// ASCII value of .
	}
	return 0;
}
 
int isNumber(char *c){
	int i; int l=lengthChar(c);
	int point = 0;	// To make sure that at most 1 point appears in input
	int e = 0;	// To make sure that at most 1 e appears in input representing exponent

	for(i=0;i<l;i++){
		if(c[i] == 46) point++;		
		if(c[i] == 101 || c[i] == 69) e++;	// ASCII value of E and e
		if((c[i] != 46 && c[i] != 101 && !isdigit(c[i]) || point>1 || e>1)) return 1;
	} 
	return 0;
}

int outOfRange(double input){
	// Note that for the assignment negative /numbers are considered outside range
	// The precence of a negative sign is /checked in isNumber() method.
	
	// Ranges of unsigned long long (integer part)
	double max=4294967295; 

	//min range of double 	(fractional part)(
	double min=1.7E-308;

	if(input > max || input < min) return 1;
	return 0;
}
		
