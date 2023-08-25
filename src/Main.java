import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * Main entry class providing a terminal-based interface for solving nonogram puzzles.
 *
 * @see NonogramSolver
 */
public class Main {

    /**
     * Static scanner for data collection
     *
     * @see Scanner
     */
    static Scanner s = new Scanner(System.in);

    /**
     * Main method. Initializes and executes solver from collected inputs
     *
     * @param args
     * @see Main#collectIntData(String)
     * @see Main#collectArrayData(String)
     * @see NonogramSolver
     */
    public static void main(String[] args) {
        List<Integer> dims = collectArrayData("Enter dimensions (Format: {width} {height}): ");

        ArrayList<ArrayList<Integer>> row_params = new ArrayList<>(), column_params = new ArrayList<>();
        for (int i = 0; i < dims.get(1); i++) {
            ArrayList<Integer> arr = collectArrayData("Enter row " + (i + 1) + " parameters");
            row_params.add(arr);
        }
        for (int i = 0; i < dims.get(0); i++) {
            ArrayList<Integer> arr = collectArrayData("Enter column " + (i + 1) + " parameters");
            column_params.add(arr);
        }

        NonogramSolver n = new NonogramSolver(dims, row_params, column_params); // Returns a matrix where 1 denotes a filled entry and 0 denotes a blank entry
        n.printNonogramSolution();
        s.close();
    }

    /**
     * Input collection wrapper
     *
     * @param msg - query message
     * @return int data
     */
    public static int collectIntData(String msg) {
        System.out.println(msg);
        return Integer.parseInt(s.nextLine());
    }

    /**
     * Input collection wrapper
     *
     * @param msg - query message
     * @return int list data
     */
    public static ArrayList<Integer> collectArrayData(String msg) {
        System.out.println(msg);
        try {
            String p = s.nextLine();
            String[] srr = p.split(" ");
            ArrayList<Integer> arr = new ArrayList<Integer>();
            for (String i : srr) {
                arr.add(Integer.parseInt(i));
            }
            return arr;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}