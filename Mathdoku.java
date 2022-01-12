/** Mathdoku is a puzzle which ranges from size 3x3 to 9x9 */

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class Mathdoku {

    /** Class variables which helps in solving the puzzle */

    int Puzzlesize = 0; //Storing the puzzle size
    ArrayList<ArrayList<Character> > PuzzleList = new ArrayList<ArrayList<Character>>(); //Stores the puzzle square
    Map<String, Grouping> group = new HashMap<>(); //Stores the groups
    Map<String, List<Cell>> cell = new HashMap<>(); //Stores the Cells
    ArrayList<ArrayList<Integer> > Puzzle = new ArrayList<ArrayList<Integer>>(); //Stores the final puzzle output

    String grouper = ""; //String to save the grouping name
    //Temporary List of Cells
    List<Cell> temp = null;
    List<Cell> tempp = null;
    //Integer to store the choices we make while solving the puzzle
    int choices=0;


    /** Method to load the puzzle */

    public boolean loadPuzzle(BufferedReader stream) throws IOException {

        boolean puzzleloaded = true;

        //Checking if the stream is not null
        if(stream!=null) {
            String a = null;
            int n = 0;
            int index = 0;

            while ((a = stream.readLine()) != null) {

                a = a.trim();
                if (!a.isEmpty()) {

                    if (!a.matches("-?[0-9]+")) {
                        if (a.contains(" ")) {

                            // Removing the extra space between the groupings
                            StringTokenizer grouping = new StringTokenizer(a, " ");
                            StringBuilder build = new StringBuilder();

                            while(grouping.hasMoreElements())
                            {
                                build.append(grouping.nextElement()).append(" ");
                            }

                            a = build.toString();

                            // Splitting the input grouping into 3
                            String[] grouparray = a.split(" ");

                            if (grouparray.length == 3) {

                                String al;
                                int result;
                                String op;

                                al = grouparray[0];
                                result = Integer.parseInt(grouparray[1]);
                                op = grouparray[2];

                                group.put(al, new Grouping(result, op, al));

                            } else {
                                puzzleloaded = false;
                                return puzzleloaded;
                            }

                        } else {
                            ArrayList<Character> a1 = new ArrayList<>();
                            char[] ch = a.toCharArray();
                            if(ch.length < Puzzlesize){

                                puzzleloaded = false;
                                return puzzleloaded;

                            }
                            for (char value : ch) a1.add(value);
                            PuzzleList.add(a1);
                            for (int i = 0; i < a1.size(); i++) {
                                String gname = String.valueOf(a1.get(i));
                                if (cell.containsKey(gname)) {
                                    Cell cells = new Cell(index, i);
                                    //set the group name as key and list of cells as value
                                    List<Cell> grpCells = cell.get(gname);
                                    grpCells.add(cells);
                                    cell.put(gname, grpCells);
                                } else {
                                    Cell cells = new Cell(index, i);
                                    List<Cell> grpCells = new ArrayList<>();
                                    grpCells.add(cells);
                                    //set the group name as key and list of cells as value
                                    cell.put(gname, grpCells);
                                }

                            }
                            index++;
                        }
                    } else {
                        n = Integer.parseInt(a);
                        if( n < 3 || n > 9){
                            puzzleloaded = false;
                            return puzzleloaded;

                        }
                        Puzzlesize = n;
                    }


                } else
                {
                    return false;
                }


            }
            return !PuzzleList.isEmpty();
        } else{
            return false;
        }
    }



    /** Method to validate the input */

    public boolean validate(){


        boolean validpuzzle = true;

        //Validating n*n matrix
        if(PuzzleList != null && !PuzzleList.isEmpty()){

            for (List<Character> list : PuzzleList) {
                //checks the puzzle row size with column size in each row
                if (PuzzleList.size() != list.size()) {
                    validpuzzle = false;
                    return validpuzzle;
                }
            }
        } else{
            return false;
        }

        //Validating if every group has an operator and result
        for (Map.Entry<String, Grouping> entry : group.entrySet()) {
            Grouping temp = entry.getValue();
            String group = temp.alpha;
            String operator = temp.operator;
            String result = String.valueOf(temp.result);
            if(operator.contains("+") || operator.contains("-") || operator.contains("/") || operator.contains("*") || operator.contains("=")){

                validpuzzle = true;

            }else {
                validpuzzle = false;
                return validpuzzle;
            }
            if( !result.matches("[0-9]+") || result.matches("-[0-9]+")){

                return false;
            }

        }

        //Check if the grouping of cells are correct

        for (Map.Entry<String, List<Cell>> entry : cell.entrySet()) {

            String grouper = entry.getKey();
            List<Cell> temp = entry.getValue();
            Grouping temp1 = group.get(grouper);
            String op = temp1.operator;
            if(op.contains("+") || op.contains("*")){

                if(temp.size() <2){
                    validpuzzle = false;
                    return validpuzzle;
                }


            } else if(op.contains("-") || op.contains("/")){

                if(temp.size() != 2){
                    validpuzzle=false;
                    return validpuzzle;

                }

            } else if(op.equals("=")){

                if(temp.size() !=1 ){
                    validpuzzle = false;
                    return validpuzzle;
                }

            }

            //Checking if grouping is connected set of cell returns True/False
            boolean validcells = isconnected(grouper,temp);

            //If grouping is not connected returns false
            if(!validcells){
                return false;
            }
        }

        return validpuzzle;
    }


    /** Method to solve the puzzle and return true or false if the puzzle is solved/unsolved */

    public boolean solve(){
        //We solve the puzzle only when the puzzle is loaded
        if(PuzzleList!=null && !PuzzleList.isEmpty()) {
            choices = 0; //Choices are set to 0 in-case the puzzle is already solved and has some value
            ExpectedValues();
            return SolvePuzzle();
        } return false;
    }

    /** Method to print the result, if the solve method cannot solve the puzzle print returns the characters with */
    /** values cells of "=" operator filled and letters for others */
    public String print(){

        String output="";

        if (Puzzle != null && !Puzzle.isEmpty()) {
            for (int i = 0; i < Puzzlesize; i++) {
                List<Integer> row = Puzzle.get(i);
                List<Character> row1 = PuzzleList.get(i);
                for (int j = 0; j < Puzzlesize; j++) {
                    Integer value = row.get(j);
                    Character alpha = row1.get(j);
                    if(value == 0){
                        output = output.concat(String.valueOf(alpha));
                    } else{
                        output = output.concat(String.valueOf(value));
                    }
                }
                output = output.concat("\n");
            }
        }

        return output;
    }

    /** Method to print the choices we made while solving the puzzle */
    public int choices(){

        return choices;

    }

    /** Method to check if the grouping of cells are connected */
    private boolean isconnected(String alphabet, List<Cell> temper) {

        int n = PuzzleList.size();

        char[][] puzzlearray = new char[n][n];

        char[][] puzzlecheck = new char[n][n];

        int it=0;

        for (List<Character> l : PuzzleList) {

            for (int j = 0; j < n; j++) {


                char a = l.get(j);
                puzzlearray[it][j] = a;
            }
            it++;
        }

        char letter = alphabet.charAt(0);
        Cell currentcell = temper.get(0);


        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (puzzlearray[i][j] == letter)
                {
                    puzzlecheck[i][j] = 1;
                }
                else
                {
                    puzzlecheck[i][j] = 0;
                }
            }
        }

        //Initiate recursive function
        checkconnection(puzzlearray, puzzlecheck, currentcell.row, currentcell.column, puzzlearray[currentcell.row][currentcell.column],n);

        //Checks for unconnected letter of the grouping
        for (int i1 = 0 ; i1 < n ; i1++ ){
            for(int j1 = 0 ; j1 < n ; j1++){

                if(puzzlecheck[i1][j1]==1){
                    return false;
                }
            }
        }
        return true;
    }


    /** Helper method uses recursion to iterate through the array to check if the cells are connected */
    private void checkconnection(char[][] puzzlearray, char[][] puzzlecheck, int row, int column, char letter, int size)
    {

        char alpha = 0;
        alpha = puzzlearray[row][column];

        if (alpha != letter || puzzlecheck[row][column] == 0) {
            return ;
        }else puzzlecheck[row][column] = 0; //Setting zero if the cell has the letter

        // Checks if the current cell has the letter above,below,left or right. Thus we can make sure
        // if the cell is connected. Diagonal cells are considered not to be connected.
        if (row + 1 < size && puzzlearray[row + 1][column] == letter) {

            checkconnection(puzzlearray, puzzlecheck, row + 1, column, letter, size);
        }
        if (row - 1 >= 0 && puzzlearray[row - 1][column] == letter) {

            checkconnection(puzzlearray, puzzlecheck, row - 1, column, letter, size);
        }
        if (column + 1 < size && puzzlearray[row][column + 1] == letter) {
            checkconnection(puzzlearray, puzzlecheck, row, column + 1, letter, size);
        }
        if (column - 1 >= 0 && puzzlearray[row][column - 1] == letter) {

            checkconnection(puzzlearray, puzzlecheck, row, column - 1, letter, size);
        }

    }

    /** Method to create a puzzle and set the value of cells with "=" operator and calculate expected values */
    /** for the cells of all the groupings based on the filled puzzle  */
    private boolean ExpectedValues(){

        int k;
        int n = Puzzlesize;
        int result =0;
        int groupsize = 0;
        String operator = "";
        //Getting the empty row and column
        int row= -1;
        int col;
        int column = 0;
        int roww= 0;
        String groupalpha;

        //Creating a puzzle with 0 in the cells
        Puzzle = createpuzzle(n);

        //We are filling the puzzles with the result of the "=" operators
        fillequal();

        //Iterating through the puzzle to set the values
        for (ArrayList<Integer> list : Puzzle) {
            row = row+1;
            col = 0;
            Cell ceel;

            for (Integer item : list) {

                if (col < n) {

                    k = item;
                    //We check if the cell is 0
                    if (k == 0) {

                        //We get the grouping info
                        Cell ce = getgroup( row, col);
                        int rr = ce.row;
                        int cl = ce.column;
                        if (rr == row && cl == col) {
                            groupalpha = grouper;
                            operator = group.get(groupalpha).operator;
                            result = group.get(groupalpha).result;
                            groupsize = temp.size();
                            ceel = ce;
                            column = ceel.column;
                            roww = ceel.row;

                        }

                        //Looping to find the operation of the cell and perform the expected operation to get the grouping result
                       
                        if (operator.contains("+")) {

                            if (groupsize == 1) {
                                for (int v = 1; v <= n; v++) {
                                    if (result == v && incolumn(v,column,Puzzle) && inrow(v,roww,Puzzle)) {

                                        item = v;
                                        list.add(item);
                                    }
                                }
                            } else if (groupsize == 2) {
                                for (int v = 1; v <= n; v++) {

                                    for (int v1 = 1; v1 <= n; v1++) {

                                        if (v + v1 == result && incolumn(v,column,Puzzle) && inrow(v,roww,Puzzle)) {

                                                ceel = tempp.get(0);
                                                ceel.valueset().add(v);

                                                ceel = tempp.get(1);
                                                ceel.valueset().add(v1);


                                        }
                                    }
                                }

                            } else if (groupsize == 3) {
                                for (int v = 1; v <= n; v++) {

                                    for (int v1 =  1; v1 <= n; v1++) {
                                        for (int v2 =  1; v2 <= n; v2++) {

                                            if (v + v1 + v2 == result && incolumn(v,column,Puzzle) && inrow(v,roww,Puzzle)) {

                                                    ceel = tempp.get(0);
                                                    ceel.valueset().add(v);
                                                    ceel = tempp.get(1);
                                                    ceel.valueset().add(v1);
                                                    ceel = tempp.get(2);
                                                    ceel.valueset().add(v2);

                                            }
                                        }
                                    }
                                }
                            } else if (groupsize == 4) {

                                for (int v = 1; v <= n; v++) {
                                    for (int v1 =  1; v1 <= n; v1++) {
                                        for (int v2 =  1; v2 <= n; v2++) {
                                            for (int v3 = 1; v3 <= n; v3++) {

                                                if (v + v1 + v2 + v3 == result && incolumn(v, column, Puzzle) && inrow(v, roww, Puzzle)) {

                                                    ceel = tempp.get(0);
                                                    ceel.valueset().add(v);
                                                    ceel = tempp.get(1);
                                                    ceel.valueset().add(v1);
                                                    ceel = tempp.get(2);
                                                    ceel.valueset().add(v2);
                                                    ceel = tempp.get(3);
                                                    ceel.valueset().add(v3);

                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (groupsize == 5) {
                                for (int v = 1; v <= n; v++) {
                                    for (int v1 =  1; v1 <= n; v1++) {
                                        for (int v2 =  1; v2 <= n; v2++) {
                                            for (int v3 = 1; v3 <= n; v3++) {
                                                for (int v4 = 1; v4 <= n; v4++) {

                                                    if (v + v1 + v2 + v3 + v4 == result && incolumn(v, column, Puzzle) && inrow(v, roww, Puzzle)) {

                                                        ceel = tempp.get(0);
                                                        ceel.valueset().add(v);
                                                        ceel = tempp.get(1);
                                                        ceel.valueset().add(v1);
                                                        ceel = tempp.get(2);
                                                        ceel.valueset().add(v2);
                                                        ceel = tempp.get(3);
                                                        ceel.valueset().add(v3);
                                                        ceel = tempp.get(4);
                                                        ceel.valueset().add(v4);

                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }else if (groupsize == 6) {
                                for (int v = 1; v <= n; v++) {
                                    for (int v1 =  1; v1 <= n; v1++) {
                                        for (int v2 =  1; v2 <= n; v2++) {
                                            for (int v3 = 1; v3 <= n; v3++) {
                                                for (int v4 = 1; v4 <= n; v4++) {
                                                    for (int v5 = 1; v5 <= n; v5++) {

                                                        if (v + v1 + v2 + v3 + v4 == result && incolumn(v, column, Puzzle) && inrow(v, roww, Puzzle)) {

                                                            ceel = tempp.get(0);
                                                            ceel.valueset().add(v);
                                                            ceel = tempp.get(1);
                                                            ceel.valueset().add(v1);
                                                            ceel = tempp.get(2);
                                                            ceel.valueset().add(v2);
                                                            ceel = tempp.get(3);
                                                            ceel.valueset().add(v3);
                                                            ceel = tempp.get(4);
                                                            ceel.valueset().add(v4);
                                                            ceel = tempp.get(5);
                                                            ceel.valueset().add(v5);

                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }


                        } else if (operator.contains("/") && groupsize == 2) {

                                for (int v = 1; v <= n; v++) {
                                    for (int v1 = 1; v1 <= n; v1++) {
                                        double rrr = (double) v / v1;

                                        if (rrr == result) {

                                            if (v > v1 && incolumn(v, column,Puzzle) && inrow(v, roww,Puzzle)) {

                                                ceel = tempp.get(0);
                                                ceel.valueset().add(v);
                                                ceel = tempp.get(1);
                                                ceel.valueset().add(v1);

                                            }else if (v < v1 && incolumn(v1, column,Puzzle) && inrow(v1, roww,Puzzle) ) {

                                                ceel = tempp.get(0);
                                                ceel.valueset().add(v1);
                                                ceel = tempp.get(1);
                                                ceel.valueset().add(v);

                                            }

                                        }
                                    }
                                }

                        } else if (operator.contains("*")) {


                             if (groupsize == 2) {
                                for (int v = 1; v <= n; v++) {

                                    for (int v1 = v + 1; v1 <= n; v1++) {

                                        if (v * v1 == result && incolumn(v,column,Puzzle) && inrow(v,roww,Puzzle) ) {


                                                ceel = tempp.get(0);
                                                ceel.valueset().add(v);
                                            ceel.valueset().add(v1);
                                                ceel = tempp.get(1);
                                                ceel.valueset().add(v1);
                                            ceel.valueset().add(v);

                                        }
                                        else if(v * v1 == result && incolumn(v1,column,Puzzle) && inrow(v1,roww,Puzzle)) {

                                            ceel = tempp.get(0);
                                            ceel.valueset().add(v);
                                            ceel = tempp.get(1);
                                            ceel.valueset().add(v1);

                                        }
                                    }
                                }

                            } else if (groupsize == 3) {
                               {
                                    for (int v = 1; v <= n; v++) {

                                        for (int v1 = 1; v1 <= n; v1++) {
                                            for (int v2 = 1; v2 <= n; v2++) {

                                                if (v * v1 * v2 == result && incolumn(v, column,Puzzle) && inrow(v, roww,Puzzle)) {

                                                        ceel = tempp.get(0);
                                                        ceel.valueset().add(v);
                                                        ceel = tempp.get(1);
                                                        ceel.valueset().add(v1);
                                                        ceel = tempp.get(2);
                                                        ceel.valueset().add(v2);
                                                }
                                            }
                                        }
                                    }
                                }

                            } else if (groupsize == 4) {
                                {
                                    for (int v = 1; v <= n; v++) {
                                        for (int v1 = 1; v1 <= n; v1++) {
                                            for (int v2 = 1; v2 <= n; v2++) {
                                                for (int v3 = 1; v3 <= n; v3++) {

                                                    if (v * v1 * v2 * v3 == result && incolumn(v, column, Puzzle) && inrow(v, roww, Puzzle)) {

                                                            ceel = tempp.get(0);
                                                            ceel.valueset().add(v);
                                                            ceel = tempp.get(1);
                                                            ceel.valueset().add(v1);
                                                            ceel = tempp.get(2);
                                                            ceel.valueset().add(v2);
                                                            ceel = tempp.get(3);
                                                            ceel.valueset().add(v3);
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (groupsize == 5) {
                                {
                                    for (int v = 1; v <= n; v++) {
                                        for (int v1 = 1; v1 <= n; v1++) {
                                            for (int v2 = 1; v2 <= n; v2++) {
                                                for (int v3 = 1; v3 <= n; v3++) {
                                                    for (int v4 = 1; v4 <= n; v4++) {

                                                        if (v * v1 * v2 * v3 == result && incolumn(v, column, Puzzle) && inrow(v, roww, Puzzle)) {

                                                            ceel = tempp.get(0);
                                                            ceel.valueset().add(v);
                                                            ceel = tempp.get(1);
                                                            ceel.valueset().add(v1);
                                                            ceel = tempp.get(2);
                                                            ceel.valueset().add(v2);
                                                            ceel = tempp.get(3);
                                                            ceel.valueset().add(v3);
                                                            ceel.valueset().add(v4);
                                                        }
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (groupsize == 6) {
                                {
                                    for (int v = 1; v <= n; v++) {
                                        for (int v1 = 1; v1 <= n; v1++) {
                                            for (int v2 = 1; v2 <= n; v2++) {
                                                for (int v3 = 1; v3 <= n; v3++) {
                                                    for (int v4 = 1; v4 <= n; v4++) {
                                                        for (int v5 = 1; v5 <= n; v5++) {


                                                            if (v * v1 * v2 * v3 == result && incolumn(v, column, Puzzle) && inrow(v, roww, Puzzle)) {

                                                                ceel = tempp.get(0);
                                                                ceel.valueset().add(v);
                                                                ceel = tempp.get(1);
                                                                ceel.valueset().add(v1);
                                                                ceel = tempp.get(2);
                                                                ceel.valueset().add(v2);
                                                                ceel = tempp.get(3);
                                                                ceel.valueset().add(v3);
                                                                ceel.valueset().add(v4);
                                                                ceel.valueset().add(v5);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        } else if (operator.contains("-") && groupsize == 2) {

                                    for (int v = 1; v <= n; v++) {
                                        for (int v1 = 1; v1 <= n; v1++) {

                                            if (v - v1 == result) {
                                                if (incolumn(v, column,Puzzle) && inrow(v, roww,Puzzle) ) {

                                                     ceel = tempp.get(0);
                                                     ceel.valueset().add(v);
                                                     ceel.valueset().add(v1);

                                                     ceel = tempp.get(1);
                                                     ceel.valueset().add(v1);
                                                     ceel.valueset().add(v);

                                                }
                                            }
                                        }
                                    }
                        }
                    }
                    col++;
                }
            }
        }
        return true;
    }

    /** Method to create a puzzle with "0" in the cells */
    private ArrayList<ArrayList<Integer>> createpuzzle(int size){

        //Creating an empty arraylist to represent a puzzle
        ArrayList<ArrayList<Integer>> puzz = new ArrayList<>();

        //Creating a puzzle with 0 in all the cell
        for (int i = 0; i < size; i++) {
            ArrayList<Integer> list = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                list.add(0);
            }
            puzz.add(list);
        }
        return puzz;
    }


    /** Method to fill puzzle with the "=" operator */

    private void fillequal(){

        int k;
        int n = PuzzleList.size();
        int result =0;
        int groupsize = 0;
        String operator = "";
        //Getting the empty row and column
        int row= -1;
        int col;
        int column = 0;
        int roww= 0;
        String groupalpha;

        for (ArrayList<Integer> list : Puzzle) {
            row = row+1;
            col = 0;

            for (Integer item : list) {

                if (col < n) {

                    k = item;
                    Cell ceel;

                    if (k == 0) {

                        Cell ce = getgroup( row, col);
                        int rr = ce.row;
                        int cl = ce.column;
                        if (rr == row && cl == col) {
                            groupalpha = grouper;
                            operator = group.get(groupalpha).operator;
                            result = group.get(groupalpha).result;
                            groupsize = temp.size();
                            ceel = ce;
                            column = ceel.column;
                            roww = ceel.row;

                        }

                        /** Passing each values from the array to find the correct cell value to be placed */

                        if (operator.equals("=")) if (groupsize == 1) {
                            for (int v = 1; v <= n; v++) {
                                if (v == result && incolumn(v, column, Puzzle) && inrow(v, roww, Puzzle)) {
                                    Puzzle.get(row).set(col, v);
                                }
                            }
                        }
                    }
                    col++;
                }
            }
        }
    }

    /** Helper Method for ExpectedValue() and fillequal() methods to get the cell */
    private Cell getgroup(int row, int col){

        Cell ce = null;

        for (Map.Entry<String, List<Cell>> entry : cell.entrySet()) {

            grouper = entry.getKey();
            temp = entry.getValue();
            int o =0;
            while (o < entry.getValue().size()) {

                ce = temp.get(o);

                int rr = ce.row;
                int cl = ce.column;
                tempp = entry.getValue();

                if (rr == row && cl == col) {

                    return ce;
                }
                o++;
            }

        }
        return ce;
    }


    /** Method to solve the puzzle using recursion and backtracking */
    private boolean SolvePuzzle() {

        boolean solved = false;
        //We get the empty cell from the puzzle to place the value
        Cell EmptyCell = GetCell();
        if (EmptyCell != null) {

            for (int value = 1; value <= PuzzleList.size(); value++) {

                if(Valid(EmptyCell,value)) {
                    //Getting the row and column of the puzzle and setting the value
                    List<Integer> list = Puzzle.get(EmptyCell.row);
                    list.set(EmptyCell.column, value);

                    //Incrementing the choice
                    choices++;

                    //If the values is placed successfully fill the next cell
                    //else backtrack to previous state and try different value
                    if (SolvePuzzle()) {
                        solved = true;
                        return solved;
                    } else {
                        List<Integer> list1 = Puzzle.get(EmptyCell.row);
                        list1.set(EmptyCell.column, 0);
                    }
                }
            }
        } else {
            solved = true;
        }

        return solved;
    }

    /** Method to get the cell with value "0" in the puzzle */
    private Cell GetCell() {
        if (Puzzle == null || Puzzle.isEmpty()) {
            Puzzle = createpuzzle(PuzzleList.size());
        }
        if (!Puzzle.isEmpty()){
            for (int i = 0; i < Puzzle.size(); i++) {
                List<Integer> list = Puzzle.get(i);
                for (int j = 0; j < list.size(); j++) {
                    int cellValue = list.get(j);
                    if (cellValue == 0) {
                        return new Cell(i,j);
                    }
                }
            }
        }
        return null;
    }

    /** Checks if the value is valid to be placed in the cell */
    private boolean Valid(Cell EmptyCell, int value){

        boolean safe= false;

        if (inrow(value, EmptyCell.row,Puzzle)) {

            if (incolumn(value, EmptyCell.column, Puzzle)) {

                safe = CanPlace(EmptyCell.row, EmptyCell.column, value);
            }
        }

        return safe;

    }


    /** Method to check if the value to be placed is present in the expected set of values we have */
    private boolean CanPlace(int row, int column,int value) {

        Cell ce;
        boolean first;
        boolean second;
        boolean third;
        boolean fourth;
        boolean fifth;
        boolean sixth;

        // Getting the operator alphabet
        for (Map.Entry<String, List<Cell>> entry : cell.entrySet()) {


            grouper = entry.getKey();
            temp = entry.getValue();

            for (int o = 0; o < entry.getValue().size(); o++) {

                ce = temp.get(o);

                int rr = ce.row;
                int cl = ce.column;
                tempp = entry.getValue();

                if (rr == row && cl == column) {

                    if(temp.size() == 2){
                        first = temp.get(0).val.contains(value);
                        if(first) return CorrectGrouping(rr, cl, value);

                        second = temp.get(1).val.contains(value);
                        if(second) return CorrectGrouping(rr, cl, value);

                    }  if(temp.size() == 3){
                        first = temp.get(0).val.contains(value);
                        if(first) return CorrectGrouping(rr, cl, value);

                        second = temp.get(1).val.contains(value);
                        if(second) return CorrectGrouping(rr, cl, value);

                        third = temp.get(2).val.contains(value);
                        if(third) return CorrectGrouping(rr, cl, value);

                    } else if(temp.size() == 4){

                        first = temp.get(0).val.contains(value);
                        if(first) return CorrectGrouping(rr, cl, value);

                        second = temp.get(1).val.contains(value);
                        if(second) return CorrectGrouping(rr, cl, value);

                        third = temp.get(2).val.contains(value);
                        if(third) return CorrectGrouping(rr, cl, value);

                        fourth = temp.get(3).val.contains(value);
                        if(fourth) return CorrectGrouping(rr, cl, value);

                    } else if(temp.size() == 5){

                        first = temp.get(0).val.contains(value);
                        if(first) return CorrectGrouping(rr, cl, value);

                        second = temp.get(1).val.contains(value);
                        if(second) return CorrectGrouping(rr, cl, value);

                        third = temp.get(2).val.contains(value);
                        if(third) return CorrectGrouping(rr, cl, value);

                        fourth = temp.get(3).val.contains(value);
                        if(fourth) return CorrectGrouping(rr, cl, value);

                        fifth = temp.get(4).val.contains(value);
                        if(fifth) return CorrectGrouping(rr, cl, value);

                    } else if(temp.size() == 6){

                        first = temp.get(0).val.contains(value);
                        if(first) return CorrectGrouping(rr, cl, value);

                        second = temp.get(1).val.contains(value);
                        if(second) return CorrectGrouping(rr, cl, value);

                        third = temp.get(2).val.contains(value);
                        if(third) return CorrectGrouping(rr, cl, value);

                        fourth = temp.get(3).val.contains(value);
                        if(fourth) return CorrectGrouping(rr, cl, value);

                        fifth = temp.get(4).val.contains(value);
                        if(fifth) return CorrectGrouping(rr, cl, value);

                        sixth = temp.get(5).val.contains(value);
                        if(sixth) return CorrectGrouping(rr, cl, value);
                    }
                }
            }

        }
        return false;
    }

    /** Method to check if the value to be placed satisfies the grouping condition */
    private boolean CorrectGrouping(int row, int col, int value){

        String grpName;
        Grouping group = null;

        List<Character> list = PuzzleList.get(row);

        //gets the group name of the cell from input grid
        grpName = String.valueOf(list.get(col));

        for(Grouping grp : this.group.values()){

            //gets the group from the group description list
            if(grp.alpha.equals(grpName)){
                group = grp;
                break;
            }
        }

        if(group != null){

            String operator = group.operator;
            int result = group.result;
            int size = temp.size();

            if(size == 2){

                Cell c1 = temp.get(0);
                Cell c2 = temp.get(1);
                int row1 = c1.row;
                int col1 = c1.column;
                if(row1 == row && col1 == col){
                    return true;
                } else{
                    List<Integer> l1 = Puzzle.get(row1);
                    int resultant = l1.get(col1);
                    switch (operator) {
                        case "-":

                            return resultant - value == result || value - resultant == result;

                        case "+":

                            return resultant + value == result;
                        case "*":

                            return resultant * value == result;
                    }
                }

            } else if(size == 3){

                Cell c1 = temp.get(0);
                Cell c2 = temp.get(1);
                Cell c3 = temp.get(2);
                int row1 = c1.row;
                int row2 = c2.row;
                int row3 = c3.row;
                int col1 = c1.column;
                int col2 = c2.column;
                int col3 = c3.column;
                if(row1 == row && col1 == col || row2 == row && col2 == col){
                    return true;
                } else if (row3 == row && col3 == col){

                    List<Integer> l1 = Puzzle.get(row1);
                    int resultant = l1.get(col1);

                    List<Integer> l2 = Puzzle.get(row2);
                    int resultant1 = l2.get(col2);

                      if(operator.equals("+")){

                          return resultant + resultant1 + value == result;
                    } else if(operator.equals("*")){

                          return resultant * resultant1 * value == result;
                    }
                }

            } else if (size == 4){

                Cell c1 = temp.get(0);
                Cell c2 = temp.get(1);
                Cell c3 = temp.get(2);
                Cell c4 = temp.get(3);
                int row1 = c1.row;
                int row2 = c2.row;
                int row3 = c3.row;
                int row4 = c4.row;
                int col1 = c1.column;
                int col2 = c2.column;
                int col3 = c3.column;
                int col4 = c4.column;
                if(row1 == row && col1 == col || row2 == row && col2 == col || row3 == row && col3 == col){
                    return true;
                } else if (row4 == row && col4 == col){

                    List<Integer> l1 = Puzzle.get(row1);
                    int resultant = l1.get(col1);

                    List<Integer> l2 = Puzzle.get(row2);
                    int resultant1 = l2.get(col2);

                    List<Integer> l3 = Puzzle.get(row3);
                    int resultant2 = l3.get(col3);

                    if(operator.equals("+")){

                        return resultant + resultant1 + resultant2 + value == result;
                    } else if(operator.equals("*")){

                        return resultant * resultant1 * resultant2 * value == result;
                    }
                    
                }
                
            }  else if (size == 5){

                Cell c1 = temp.get(0);
                Cell c2 = temp.get(1);
                Cell c3 = temp.get(2);
                Cell c4 = temp.get(3);
                Cell c5 = temp.get(4);
                int row1 = c1.row;
                int row2 = c2.row;
                int row3 = c3.row;
                int row4 = c4.row;
                int row5 = c5.row;
                int col1 = c1.column;
                int col2 = c2.column;
                int col3 = c3.column;
                int col4 = c4.column;
                int col5 = c5.column;
                if(row1 == row && col1 == col || row2 == row && col2 == col || row3 == row && col3 == col || row4 == row && col4 == col){
                    return true;
                } else if (row5 == row && col5 == col){

                    List<Integer> l1 = Puzzle.get(row1);
                    int resultant = l1.get(col1);

                    List<Integer> l2 = Puzzle.get(row2);
                    int resultant1 = l2.get(col2);

                    List<Integer> l3 = Puzzle.get(row3);
                    int resultant2 = l3.get(col3);

                    List<Integer> l4 = Puzzle.get(row4);
                    int resultant3 = l4.get(col4);

                    if(operator.equals("+")){

                        return resultant + resultant1 + resultant2 + resultant3 + value == result;
                    } else if(operator.equals("*")){

                        return resultant * resultant1 * resultant2 * resultant3 * value == result;
                    }
                }
            }
        }
        return true;
    }

    /** Check if the value is already present in the row **/
    private boolean inrow(int value, int row , ArrayList<ArrayList<Integer>> Puzzle ){

        ArrayList<Integer> a = Puzzle.get(row);

        for(Integer list : a){

            if(list == value){
                return false;
            }
        }

        return true;

    }

    /** Check if value present is already present in column **/
    private boolean incolumn(int value, int column, ArrayList<ArrayList<Integer>> Puzzle){

        for(List<Integer> list : Puzzle) {
            for (int i = 0; i < list.size(); i++) {
                if (i == column) {
                    if (list.get(i) == value) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


/** Class to store the grouping information */
    public class Grouping{

        int result;
        String operator;
        String alpha;
        Grouping(int result,String operator,String alpha){
            this.result = result;
            this.operator = operator;
            this.alpha = alpha;
        }

    }
/** Class to store the Cell information and set of values */
    public class Cell{
        int row;
        int column;
        Set<Integer> val = new HashSet<>();
        Cell(int row,int column){
            this.row = row;
            this.column = column;
        }
        public Set<Integer> valueset(){

            return this.val;
        }
    }
}