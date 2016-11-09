#!env/bin/python3
import random
import timeit
from functools import partial


# Basic idea : compare pairs of values and 
# switch them to obtain min, max pair
def bubbleSort(list) :
    for i in range(len(list)-1, 0, -1) :
        for j in range(i) :
            sorted = True
            if list[j] > list[j + 1] :
                tmp = list[j]
                list[j] = list[j + 1]
                list[j + 1] = tmp


# Basic idea : improve on BubbleSort by finding max value,
# placing it in correct position and reducing array at each loop
def selectionSort(list) :
    for filledSlot in range(len(list)-1, 0, -1) :
        indexMax = 0
        for i in range(1, filledSlot + 1) :
            if list[i] > list[indexMax] : 
                indexMax = i

        tmp = list[indexMax]
        list[indexMax] = list[filledSlot]
        list[filledSlot] = tmp


# Basic idea : select a pivot and place elements to the left or 
# to the right of it depending on their magnitude relative to the pivot
def insertSort(list, start = 0, gap = 1) :
    for i in range(start + gap, len(list), gap) :
        currentVal = list[i]
        position = i

        while position >= gap and list[position - gap] > currentVal :
            list[position] = list[position - gap]
            position = position - gap

        list[position] = currentVal


# Basic idea : improve insertSort by splitting the list into
# elements with a certain gap and then sorting them
def shellSort(list) :
    sublistCount = len(list)  // 2
    while sublistCount > 0 :
        for start in range(sublistCount) :
            insertSort(list, start, sublistCount)

        sublistCount //= 2


# Basic idea : split the list into unitary lists
# then reassemble while sorting until original list is obtained
def mergeSort(list):
    if len(list) > 1 :
        mid = len(list) // 2 
        right = list[mid:]
        left = list[:mid]

        mergeSort(right)
        mergeSort(left)

        i, j, k = (0, 0, 0)
        while i < len(left) and j < len(right):
            if left[i] < right[j]:
                list[k]=left[i]
                i += 1
            else:
                list[k]=right[j]
                j += 1
            k += 1

        while i < len(left):
            list[k]=left[i]
            i += 1
            k += 1

        while j < len(right):
            list[k]=right[j]
            j += 1
            k += 1    


# Basic idea : by choosing a pivot element, sort elements on the left
# or right of the wall wich marks were the element shall be placed
def quickSort(list) :
    quickSortHelper(list, 0, len(list) - 1)

def quickSortHelper(list, first, last) : 
    if first < last : 
        splitPoint = partition(list, first, last)
        quickSortHelper(list, first, splitPoint - 1)
        quickSortHelper(list, splitPoint + 1, last)

def partition(list, first, last) :
    pivot = list[first]
    leftPos = first + 1
    rightPos = last

    done = False
    while not done :
        while leftPos <= rightPos and list[leftPos] <= list[rightPos] :
            leftPos += 1

        while list[rightPos] >= pivot and rightPos >= leftPos : 
            rightPos -= 1
        
        if rightPos < leftPos :
            done = True
        else :
            tmp = list[leftPos]
            list[leftPos] = list[rightPos]
            list[rightPos] = tmp

    tmp = list[first]
    list[first] = list[rightPos]
    list[rightPos] = pivot

    return rightPos


# for each sorting algo. we will use the same random list
mainList = [random.random() for i in range(1000)]

algorithms = [bubbleSort, selectionSort, insertSort, 
                shellSort, mergeSort, quickSort]

for algo in algorithms:
    tmpList = mainList[:]
    print("\n" + algo.__name__ + " took : ")
    print(timeit.Timer(partial(algo, tmpList)).repeat(1, 1))
