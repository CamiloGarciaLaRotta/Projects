import string
import random

####### TODO #######
'''
finish cuckoo Hashing
'''

# HashArray size
M = 100

# key generation
key_gen = lambda s : sum(map(ord, s))

# a = 0 for linear, a = 1 for quadratic
h_1 = lambda k, i = 0, a = 0 : ((k % M) + i + a * i ** 2) % M

# second hashing function for double hashing and cuckoo
h_2 = lambda k, i = 0, a = 0 : (2*(k % M) + i + a * i ** 2) % M

# Node and Linked List for Separate Chainning
class Node :
    def __init__(self, cargo = None, next = None) :
        self.cargo = cargo
        self.next = next
    
    def __str__(self) :
        return str(self.value)

class LinkedList :
    def __init__(self, head = None,  size = 0) :
        self.head = head
        self.size = size

    def add(self, newNode) :
        if(self.size == 0) :
            self.head = newNode
        else :
             newNode.next = self.head
             self.head = newNode 

        self.size += 1


# use an array of linked lists to add collisions
def separateChainning(list) :
    hashArray = [LinkedList() for _ in range(M)]

    # fill hashArray
    for s in list :
        key = key_gen(s)
        index = h_1(key)
        value = Node(s)

        hashArray[index].add(value)
    
    # count collisions
    collisions = 0    
    for linkedList in hashArray :
        if(linkedList.size > 1) : collisions += linkedList.size - 1

    return collisions
    

# in case of collision try a cell at constant distance
def linearProbing(list, a = 0) :
    hashArray = [None for _ in range(M)]
    
    # fill hashArray
    for s in list :
            key = key_gen(s)
            
            # start probing
            i = 0
            collisions = 0
            while(hashArray[h_1(key, i, a)] != None) :
                i += 1
                collisions += 1
            
            hashArray[h_1(key, i, a)] = s
        
    return collisions

# in case of collision try a cell at a quadratic distance
def quadraticProbing(list) :
    return linearProbing(list, 1)

    
# in case of collision apply a second hashing function
def doubleHashing(list) :
    hashArray = [None for _ in range(M)]
    
    # fill hashArray
    for s in list :
            key = key_gen(s)
            
            # start hashing
            i_1 = h_1(key)
            i_2 = h_2(key)
            collisions = 0

            while(hashArray[i_1] != None) :
                i_1 = (i_1 +i_2) % M
                collisions += 1
            
            hashArray[i_1] = s

    return collisions

# alternate collisions between 2 hash arrays with 2 hash functions
def cuckooHashing(list) :
    #maximum amount of switches between tables before I consider it an infinite loop 
    MAX_HASHES = 2*M

    hashArr_1 = [None for _ in range(M)]
    hashArr_2 = [None for _ in range(M)]

'''    for s in list : 
        key = key_gen(s)
        done = False
        
        while(!done) :
            if(hashArr_1[h_1(key)] == None) :
                hashArr_1[h_1(key)] = s
                done = True
            else : 
                tmpVal = hashArr_1[h_1(key)];
                hashArr_1[h_1(key)] = s'''



# make array of random strings
mainList = [(''.join(random.SystemRandom().choice(string.ascii_letters + string.digits) for _ in range(5))) for _ in range(75)]

algorithms =  [separateChainning, linearProbing, quadraticProbing,  doubleHashing]
loadFactor = [1/4, 2/4, 3/4]

for load in loadFactor :
    print("LOAD FACTOR : " + str(load))

    for algo in algorithms :
        N = load * M
        tmpList = mainList[0:int(N)]
        print(algo.__name__ + " had " + str(algo(tmpList)) + " collision(s)\n")

