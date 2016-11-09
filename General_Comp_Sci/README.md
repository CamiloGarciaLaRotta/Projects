# Sorting

####Disclosure
	
This script isn't meant as a rigorous runtime benchmark, but rather an exercice to implement different sorting  
algorithms and understand their advantages and disadvantages with respect to different data distributions.

The test list to be sorted is generated via the `random.random()` function. I found 1000 elements to be a safe limit for the list. Tests with 1200 exceeded the default stack depth : `RecursionError: maximum recursion depth exceeded in comparison`.

####Choice of timer

I chose `timeit.timeit()` for convenience and facility of usage. Other approaches, such as `time.perf_counter()` have higher resolution for short intervals, but it's implementation wasn't as straightforward if I wanted to avoid taking into account sleep and system-wide time.

Because `tmpList` is already sorted after the first call of the sorting method, I forced the `.repeat()` function to  only execute 1 time as opposed to the default 3. The other two runs would significantly reduce the real execution time of the algorithm.

# Hashing

####Disclosure

The functions used in the script were chosen to represent the general idea behind some types of hashing. 
It is by no means an extensive list of mathematical algorithms but rather an overview of the most common cases. 

####Closed Addressing

Using the widely implemented approach of a Linked List at each bucket.

####Open Addressing

| 	Type  		    |	 Function		            |
| ------------- 	| ------------- 		        |
| Linear Probing  	| H(k) = (k + i) % M  		    |
| Quadratic Probing	| H(k) = (k + i + i^2) % M 	    |
| Double Hashing	| H(k) = h_1(k) + i * h_2(k)	|

####Cuckoo Hashing

Mixing Separate Chaining and Open Addressing. We use 2 hashing tables with their own hashing functions.

1.  we insert new element in first table using H_1(k)
2.  if collision we move old element into second table using H_2(k)
3.  repeat until no collision or until K collisions are found
4.  define an arbitrary number K which will represent an infinite loop between the 2 tables for a given element
