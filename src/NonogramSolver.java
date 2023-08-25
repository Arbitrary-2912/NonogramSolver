import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Class for solving nongrams.
 */
public class NonogramSolver {
    private STATE state; // State of solver
    private int[][] matrix; // Matrix: 1 denotes filled, 0 denotes unfilled, -1 denotes undecided
    private ArrayList<ArrayList<Integer>> row_params, column_params;
    protected Nonogram nonogram;

    /**
     * Base constructor, initializes solver
     *
     * @param dims          nonogram puzzle dimensions
     * @param row_params    row segments
     * @param column_params column segments
     */
    public NonogramSolver(List<Integer> dims, ArrayList<ArrayList<Integer>> row_params, ArrayList<ArrayList<Integer>> column_params) {
        state = STATE.UNSOLVED;
        matrix = new int[dims.get(1)][dims.get(0)];
        for (int i = 0; i < matrix.length; i++) {
            Arrays.fill(matrix[i], -1);
        }
        this.row_params = row_params;
        this.column_params = column_params;

        nonogram = new Nonogram(matrix, state, row_params, column_params);
        while (nonogram.getState() != STATE.SOLVED && nonogram.getState() != STATE.IMPOSSIBLE) {
            nonogram.update();
        }
        matrix = nonogram.getMatrix();
    }

    public void printNonogramSolution() {
        System.out.println(nonogram.getState());
        if (nonogram.getState() == STATE.SOLVED) {
            System.out.println("SOLUTION");
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    System.out.print(matrix[i][j] + " ");
                }
                System.out.println();
            }
        }
    }


    /**
     * Base Nonogram Class
     */
    private static class Nonogram {
        private int[][] matrix;
        private STATE state;
        private List<NonogramLine> rows;
        private List<NonogramLine> columns;

        private int width; // number of columns
        private int height; // number of rows

        /**
         * Instantiates a Nonogram Structure
         *
         * @param m        matrix
         * @param s        state
         * @param r_params row parameters
         * @param c_params column parameters
         */
        public Nonogram(int[][] m, STATE s, ArrayList<ArrayList<Integer>> r_params, ArrayList<ArrayList<Integer>> c_params) {
            matrix = m;
            state = s;
            rows = new ArrayList<NonogramLine>();
            columns = new ArrayList<NonogramLine>();

            width = m[0].length;
            height = m.length;

            for (int i = 0; i < height; i++) {
                NonogramLine n;
                int[] lv = matrix[i]; // line values
                ArrayList<Integer> params = r_params.get(i);

                // Formatting Line Parameters i.e. the lengths of the "filled" and "unfilled" segments
                var ps = params.size();
                for (int j = 0; j < 2 * ps - 1; j++) {
                    if (j % 2 != 0) {
                        params.add(j, 0);
                        j++;
                    }
                }
                params.add(0, 0);
                params.add(0);

                n = new NonogramLine(lv, params);
                rows.add(n);
            }

            for (int i = 0; i < width; i++) {
                NonogramLine n;
                int[] lv = new int[height]; // line values
                for (int j = 0; j < height; j++) {
                    lv[i] = matrix[j][i];
                }
                ArrayList<Integer> params = c_params.get(i);

                // Formatting Parameters
                var ps = params.size();
                for (int j = 0; j < 2 * ps - 1; j++) {
                    if (j % 2 != 0) {
                        params.add(j, 0);
                        j++;
                    }
                }
                params.add(0, 0);
                params.add(0);

                n = new NonogramLine(lv, params);
                columns.add(n);
            }
        }

        /**
         * Update nonogram states
         */
        private void update() {
            state = STATE.SOLVED;
            for (int i = 0; i < rows.size(); i++) {
                var r = rows.get(i);
                if (rows.get(i).state != STATE.SOLVED) {
                    r.update(matrix[i]);
                    rows.set(i, r);
                    matrix[i] = rows.get(i).values;
                }
                if (rows.get(i).state == STATE.UNSOLVED) {
                    state = STATE.UNSOLVED;
                }
                if (rows.get(i).state == STATE.IMPOSSIBLE) {
                    state = STATE.IMPOSSIBLE;
                    return;
                }
            }
            if (state == STATE.SOLVED) return;
            for (int i = 0; i < columns.size(); i++) {
                var c = columns.get(i);
                if (columns.get(i).state != STATE.SOLVED) {
                    int[] m = new int[height];
                    for (int j = 0; j < rows.size(); j++) {
                        m[i] = matrix[j][i];
                    }
                    c.update(m);
                    columns.set(i, c);
                    for (int j = 0; j < height; j++) {
                        matrix[j][i] = columns.get(i).values[j];
                    }
                }
                if (columns.get(i).state == STATE.UNSOLVED) {
                    state = STATE.UNSOLVED;
                }
                if (columns.get(i).state == STATE.IMPOSSIBLE) {
                    state = STATE.IMPOSSIBLE;
                    return;
                }
            }
        }

        public int[][] getMatrix () {
            return matrix;
        }

        private STATE updateState() {
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    if (matrix[i][j] == -1) return STATE.UNSOLVED;
                }
            }
            for (int i = 0; i < rows.size(); i++) {
                if (rows.get(i).state == STATE.IMPOSSIBLE) {
                    return STATE.IMPOSSIBLE;
                } else if (rows.get(i).state == STATE.UNSOLVED) {
                    return STATE.UNSOLVED;
                }
            }

            for (int i = 0; i < columns.size(); i++) {
                if (columns.get(i).state == STATE.IMPOSSIBLE) {
                    return STATE.IMPOSSIBLE;
                } else if (columns.get(i).state == STATE.UNSOLVED) {
                    return STATE.UNSOLVED;
                }
            }

            return STATE.SOLVED;
        }

        private STATE getState() {
            return state;
        }
    }

    /**
     * Base Class For a Line
     */
    private static class NonogramLine {
        /**
         * Base Properties
         */
        private int[] values;
        private ArrayList<Integer> params;
        private ArrayList<NonogramLine> possibilities;
        private STATE state;
        private int p_sum = 0;

        /**
         * Instantiates a Nonogram Line Structure
         *
         * @param v determined integer values of each square
         * @param p parameters
         */
        public NonogramLine(int[] v, ArrayList<Integer> p) {
            this(v, p, STATE.UNSOLVED);
        }

        /**
         * Instantiates a Nonogram Line Structure
         *
         * @param v determined integer values of each square
         * @param p parameters
         * @param s state
         */
        public NonogramLine(int[] v, ArrayList<Integer> p, STATE s) {
            values = v;
            params = p;
            possibilities = new ArrayList<NonogramLine>();
            state = s;

            for (Integer param : params) { // Calculate sum of parameters, useful calculating permutations
                p_sum += param;
            }
        }

        /**
         * Updates matrix values by solving the next iteration of the NonogramLine
         *
         * @param v
         */
        private void update(int[] v) {
            // Update values
            values = v;

            // Update states
            findPossibilities();
            if (possibilities.size() < 1) {
                this.state = STATE.IMPOSSIBLE;
                return;
            }
            values = findCommonalities();
            if (!Arrays.asList(values).contains(-1)) {
                this.state = STATE.SOLVED;
            } else {
                this.state = STATE.UNSOLVED;
            }
        }

        /**
         * Recursively possibilities from current state

         */
        private void findPossibilities() {
            var v = values;
            var m_sum = values.length-p_sum;
            var r_min = 0;
            var r_max = m_sum;
            var length = (int) Math.ceil(params.size()/2d);

            // Calculate all Possibilities with Permutation Util
            NonogramPermutationUtil util = new NonogramPermutationUtil(r_min, r_max, m_sum, length);
            var permutations = util.getPermutations();
            // Verify all Possibilities
            main_loop: for (var p: permutations) {
                ArrayList<Integer> formattedPermutation = new ArrayList<>();
                for (int i = 0; i < params.size(); i++) {
                    if (i%2==0) {
                        formattedPermutation.add(p[i/2]);
                    } else {
                        formattedPermutation.add(params.get(i));
                    }
                }
                // Now there exists a formatted permutations of alternating 0's and 1's that contain a possible nonogram sequence
                // The actual values can
                int[] permutationValues = new int[values.length];
                int tp_sum = 0;
                for (int i = 0; i < formattedPermutation.size(); i++) { // Populates actual value array
                    Arrays.fill(permutationValues, tp_sum, tp_sum+formattedPermutation.get(i), (i%2==0)?0:1);
                    tp_sum += formattedPermutation.get(i);
                }
                // Now it is intended to corroborate whether the permutation matches with the actual values, ie if there is a 0 in the permutation value at an index, there isn't a 1 there in the original array or vice versa
                for (int i = 0; i < values.length; i++) {
                    if ((values[i] == 1 && permutationValues[i] == 0) || (values[i] == 0 && permutationValues[i] == 1)) {
                        continue main_loop;
                    }
                }
                // If everything passes, the permutation is appended to the possibilities of the nonogram line with the same line parameters as the original line
                NonogramLine n = new NonogramLine(permutationValues, params, STATE.SOLVED);
                possibilities.add(n);
            }
        }

        /**
         * Returns the possible versions of the nonogram line
         */
        private ArrayList<NonogramLine> getPossibilities() {
            return possibilities;
        }

        /**
         * Back-propagates to find commonalities in all possibilities
         */
        private int[] findCommonalities() {
            int[] v = values;
            a:
            for (int i = 0; i < possibilities.size(); i++) {
                var x = possibilities.get(i);
                b:
                for (int j = 0; j < v.length; j++) {
                    int cur = x.values[j];

                    if (v[j] == -1 && cur != x.values[j]) {
                        continue;
                    }

                    v[j] = cur;
                }
            }
            return v;
        }
    }

    /**
     * Enum to store solver states
     */
    private enum STATE {
        SOLVED,
        UNSOLVED,
        IMPOSSIBLE
    }

}
