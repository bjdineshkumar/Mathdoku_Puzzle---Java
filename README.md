# Mathdoku_Puzzle-Java

## Solving Mathdoku puzzle using java ( Part of Software Development Concepts )

Description of Mathdoku:
MathDoku is a mathematical and logical puzzle based on "KenKen" and loosely similar to sudoku. Puzzles can be solved by combining the four main mathematical functions of addition, subtraction, multiplication, and division. Difficulty levels of the puzzles can be chosen from random, easy, medium, or hard and hints can be accessed to help solve puzzles. Size of puzzles can also be chosen from 6X6, 8X8, or a printable version.

Solution:

1. We first load and validate the puzzle.
 
2. The following are the steps taken to solve the puzzle: 
 
 a. First a puzzle is created with “0” in all the cells.
 
 b. The puzzle is then filled with result value of the “=” operator.
 
 c. Using the above filled puzzle, we then iterate through every row & column and 
 we get the specific cell of every row and column.
 
 d. If the cell has “0” we then get the grouping alphabet of the particular cell with the 
 grouping operator and the result.
 
 e. We use the operator to find the correct operator to do the operation.
 
 
 f. We then try all the possible values with the operation to find the desired result. When 
 we get the desired result, we compare it in row and column to confirm it does not 
 violate the row/column rule since we have filled the puzzle with “=” operator already.
 
 g. If the value we got is correct, We then add it to a hashset of the cell.
 
 h. We do this for all the cells in the puzzle.
 
 i. At the end of the iteration, we will have a set of values for every cell which satisfies the 
 grouping operation and result.
 
 j. We then use the SolvePuzzle() method, to try solving the puzzle using recursion and 
 backtracking.
  
      a. We get the cell which is empty from the puzzle we created. The row and column 
         of the unfilled cell is got from the emptycell.
  
      b. We try all the values from 1 to n to be placed in the particular cell.
  
      c. We use the method Valid() to check if the values is valid to be placed in the cell
 
 **Valid():**
 
      a. This method checks if the value is in the particular row
 
      b. If not in the row, It checks if it is in the column already
 
      c. If not in the column, it checks if the value satisfies the grouping constraint
         and is present in the set of value we got from ExpectedValue() method
         using the CanPlace() method
 
 **CanPlace() :**
 
      a. We get the particular cell using the row and column.
 
      b. We check if the value is in the set of values of the cell which we have 
         already got using the ExpectedValue() function.
 
      c. If the value is present we then check if the value form the correct 
         grouping using CorrectGrouping() method:
               i. The correct grouping method gets the particular operator and the 
                  result of the cell using the row and column.
               ii. It checks if it satisfies the grouping operation and result.
               iii. If it satisfies the condition, true is returned.
 
 h. If all the above conditions are satisfied then the value is placed in the 
 particular cell.
 
 i. Then the SolvePuzzle() method is called recursively.
 
 j. If a value is not set in a cell then we return false, and we set 0 in the previous
 cell and go back to try different value until a correct value is set for this cell
 thus backtracking until we get the correct value.
 
 k. If all the values are set True is returned and when we can’t set the correct
 value to solve the puzzle false is returned.
