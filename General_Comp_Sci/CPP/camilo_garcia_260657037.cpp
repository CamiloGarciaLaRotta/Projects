// COMP 322   -   A1
// CAMILO GARCIA LA ROTTA
// #260657037

//////////////////////////////////////////////////////////////////////////////
// libraries and globals 
//////////////////////////////////////////////////////////////////////////////
using namespace std;
#include <iostream>
#include <stdio.h>
#include <cstdlib>

const int ROWS = 5;
const int COLS = 5;

//////////////////////////////////////////////////////////////////////////////
//  helper functions
//////////////////////////////////////////////////////////////////////////////

// allocate memory for an array of pointers
int **alloc_matrix(void)
{
  int **m = new int*[ROWS];
  for (int i=0; i<ROWS; i++) { m[i] = new int[COLS]; }

  for (int i=0; i<ROWS; i++)
  {
    for (int j=0; j<COLS; j++) { m[i][j] = 0; }
  }

  return m;
}

// Deallocate memory of the input matrix
void delete_matrix(int **matrix)
{
  for (int i=0; i<ROWS; i++) { delete [] matrix[i]; }
  delete [] matrix;
}

// Q1 return a pointer to the array passed by reference
int *return_matrix(void) 
{ 
  int *matrix = new int[5];
  matrix[3] = 100;

  return matrix; 
}

// Q2 fill the input matrix with random integers
void fill_matrix(int matrix[ROWS][COLS])
{
  for (int i=0; i<ROWS; i++)
  {
    for (int j=0; j<COLS; j++) { matrix[i][j] = rand() % 2; }
  }
}

// Q7 fill the matrix passed by pointer with random integers
void fill_matrix(int** matrix)
{
  for (int i=0; i<ROWS; i++)
  {
    for (int j=0; j<COLS; j++) { matrix[i][j] = rand() % 2; }
  }
}

// Q8 fill the 2D matrix represented as 1D matrix with random integers
void fill_matrix(int matrix[], int size)
{
  for (int i=0; i<size; i++) { matrix[i] = rand() % 2;  }
}

// Q3 print the input matrix passed by reference
void print_matrix(int matrix[ROWS][COLS])
{
  for (int i=0; i<ROWS; i++)
  {
    for (int j=0; j<COLS; j++) { printf("%d\t", matrix[i][j]); }
    printf("\n");
  }
  printf("\n");
}

// Q7 print the matrix passed by pointer 
void print_matrix(int **matrix)
{
  for (int i=0; i<ROWS; i++)
  {
    for (int j=0; j<COLS; j++) { printf("%d\t", matrix[i][j]); }
    printf("\n");
  }
  printf("\n");
}

// Q8 print the 2D matrix represented as 1D matrix 
void print_matrix(int matrix[], int row_size, int col_size)
{
  for (int i=0; i<(row_size * col_size); i++)
  {
    if (i != 0 && (i % row_size == 0)) { printf("\n"); }
    printf("%d\t", matrix[i]);
  }
  printf("\n");
}

// Q4 return the transpose of the input matrix passed by reference
void transpose_matrix(int matrix[ROWS][COLS])
{
  int tmp[ROWS][COLS];

  for (int i=0; i<ROWS; i++)
  {
    for (int j=0; j<COLS; j++) { tmp[j][i] = matrix[i][j]; }
  }
  
  for (int i=0; i<ROWS; i++)
  {
    for (int j=0; j<COLS; j++) { matrix[i][j] = tmp[i][j]; }
  }
}

// Q7 return the transpose of the input matrix passed by pointer
void transpose_matrix(int** matrix)
{
  int tmp[ROWS][COLS];

  for (int i=0; i<ROWS; i++)
  {
    for (int j=0; j<COLS; j++) { tmp[j][i] = matrix[i][j]; }
  }
  
  for (int i=0; i<ROWS; i++)
  {
    for (int j=0; j<COLS; j++) { matrix[i][j] = tmp[i][j]; }
  }
}

// Q5 return a dyn. allocated matrix resulting from A X B
int **iter_product_matrix(int a[ROWS][COLS], int b[ROWS][COLS])
{
  int **c = alloc_matrix();
  
  for (int i=0; i<ROWS; i++)
  {
    for (int j=0; j<COLS; j++)
    {
      for (int k=0; k<COLS; k++) { c[i][j] += a[i][k] * b[k][j]; }
    }
  }

  return c;
}

// Q6 fill a 2D matrix c with the product of a and b
void recur_prod_helper(int a[ROWS][COLS], int b[ROWS][COLS], int **c)
{
  static int i = 0, j = 0, k = 0;

  if (i >= ROWS) { return; }

  if (j < COLS)
  {
    if (k < COLS)
    {
      c[i][j] += a[i][k] * b[k][j];
      k++;
      recur_prod_helper(a,b,c);
    }
    k = 0;
    j++;
    recur_prod_helper(a,b,c);
  }
  j = 0;
  i++;
  recur_prod_helper(a,b,c);
}

// Q6 return a dyn. allocated matrix resulting from A X B
int **recur_product_matrix(int a[ROWS][COLS], int b[ROWS][COLS])
{
  int **c = alloc_matrix();
  recur_prod_helper(a,b,c);

  return c;
}

void print_separator() { printf("------------------------------------\n\n"); }

//////////////////////////////////////////////////////////////////////////////
//  ENTRY POINT OF THE PROGRAM
//////////////////////////////////////////////////////////////////////////////

int main()
{
  print_separator();

  /* Q1
   * There is no direct way of returning an array directly from a function,
   * because its declared inside a function and once the function returns
   * its address would become inaccessible.
   *
   * Even functions that return strings just return a pointer to the first
   * character, as a string is an array of characters in C++.
   *
   * However, a function can return a pointer to a dynamically created array.
   * The function below demonstrates that capability.
   */
  int *m = return_matrix();
  printf("Q1) Item of a ptr to array returned from a function: %d\n\n", m[3]);
  delete [] m;
  
  print_separator();

  // Q2
  int a[ROWS][COLS];
  fill_matrix(a);

  // Q3  
  printf("Q2-Q3) Randomly filled matrix: \n\n");
  print_matrix(a);
  
  print_separator();
  
  // Q4
  printf("Q4) Transpose of the matrix: \n\n");
  transpose_matrix(a);
  print_matrix(a);
  
  print_separator();
  
  // Q5
  printf("Q5) Iterative matrix product: \n\n");
  printf("Matrix A \n");
  fill_matrix(a);
  print_matrix(a);

  printf("Matrix B \n");
  int b[ROWS][COLS];
  fill_matrix(b);
  print_matrix(b);

  printf("A X B\n");
  int **c = iter_product_matrix(a, b);
  print_matrix(c);
  delete_matrix(c);

  print_separator();

  // Q6
  printf("Q6) Recursive matrix product: \n\n");
  printf("A X B\n");
  int **d = recur_product_matrix(a, b); 
  print_matrix(d);
  
  delete_matrix(d);

  print_separator();

  // Q7
  printf("Q7) Passing matrix by pointer: \n\n");
  int **e = alloc_matrix();
  
  printf("fill_matrix()\n");
  fill_matrix(e);
  
  printf("print_matrix()\n");
  print_matrix(e);
  
  printf("transpose_matrix()\n");
  transpose_matrix(e);
  print_matrix(e);
  
  delete_matrix(e);

  print_separator();

  // Q8
  printf("Q8) Implementing matrix as 1D array: \n\n");
  
  int f[ROWS*COLS] = {};
  
  printf("fill_matrix()\n");
  fill_matrix(f, ROWS*COLS);
  
  printf("print_matrix()\n");
  print_matrix(f, ROWS, COLS);

  print_separator();

  return 0;
}
