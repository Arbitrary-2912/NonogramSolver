import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class for solving nongrams.
 */
public class NonogramSolver {
    private int[][] matrix; // Matrix: 1 denotes filled, 0 denotes unfilled, -1 denotes undecided
    private ArrayList<ArrayList<Integer>> row_params, column_params;
    protected static Nonogram nonogram;

    /**
     * Base constructor, initializes solver
     *
     * @param dims          nonogram puzzle dimensions
     * @param row_params    row segments
     * @param column_params column segments
     */
    public NonogramSolver(List<Integer> dims, ArrayList<ArrayList<Integer>> row_params, ArrayList<ArrayList<Integer>> column_params) {
        STATE state = STATE.UNSOLVED;
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
        private ArrayList<ArrayList<Integer>> row_params, column_params;

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
            row_params = r_params;
            column_params = c_params;

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
            // Forward row pass
            for (int i = 0; i < rows.size(); i++) {
                var r = rows.get(i);
                if (rows.get(i).state != STATE.SOLVED) {
                    r.update(matrix[i]);
                    rows.set(i, r);
                    matrix[i] = rows.get(i).values;
                }
                if (rows.get(i).state == STATE.UNSOLVED) {
                    state = STATE.UNSOLVED;
                    continue;
                }
                if (rows.get(i).state == STATE.IMPOSSIBLE) {
                    state = STATE.IMPOSSIBLE;
                    return;
                }
            }
            // Forward column pass
            for (int i = 0; i < columns.size(); i++) {
                var c = columns.get(i);
                if (columns.get(i).state != STATE.SOLVED) {
                    int[] m = new int[height];
                    for (int j = 0; j < rows.size(); j++) {
                        m[j] = matrix[j][i];
                    }
                    c.update(m);
                    columns.set(i, c);
                    for (int j = 0; j < height; j++) {
                        matrix[j][i] = columns.get(i).values[j];
                    }
                }
                if (columns.get(i).state == STATE.UNSOLVED) {
                    state = STATE.UNSOLVED;
                    continue;
                }
                if (columns.get(i).state == STATE.IMPOSSIBLE) {
                    state = STATE.IMPOSSIBLE;
                    return;
                }
            }
            boolean ff2 = false;
            for (var a : matrix) {
                for (var b : a) {
                    if (b == -1) ff2 = true;
                }
            }
            if (ff2) {
                state = STATE.UNSOLVED;
                for (int i = 0; i < rows.size(); i++) {
                    var tmp = rows.get(i);
                    tmp.state = STATE.UNSOLVED;
                    rows.set(i, tmp);
                }
                for (int i = 0; i < columns.size(); i++) {
                    var tmp = columns.get(i);
                    tmp.state = STATE.UNSOLVED;
                    columns.set(i, tmp);
                }
//                update(); // recurse
            }
        }

        public int[][] getMatrix() {
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
            possibilities.clear();
            // Check if already solved or is still unsolved
            if (Arrays.asList(v).contains(-1)) {
                state = STATE.UNSOLVED;
            } else {
                ArrayList<Integer> v_segmented = new ArrayList<>();
                for (int i = 0; i < v.length; i++) {
                    if (v[i] == 1) {
                        if (i > 0 && v[i] == v[i - 1] && v[i] == 1)
                            v_segmented.set(v_segmented.size() - 1, v_segmented.get(v_segmented.size() - 1) + 1);
                        else if (v[i] == 1)
                            v_segmented.add(1);
                    }
                }
                if (v_segmented == params) {
                    this.state = STATE.SOLVED;
                    return;
                }
            }

            // Update states
            findPossibilities();
            if (possibilities.size() < 1) {
                this.state = STATE.IMPOSSIBLE;
                return;
            }
            if (possibilities.size() > 1)
                values = findCommonalities(possibilities.size() - 1, possibilities.get(possibilities.size() - 1).values);
            else if (possibilities.size() == 1) {
                values = possibilities.get(0).values;
                possibilities.add(new NonogramLine(values, params, (Arrays.asList(values).contains(-1)) ? STATE.SOLVED : STATE.UNSOLVED));
            }

            if (Arrays.asList(values).contains(-1)) {
                this.state = STATE.UNSOLVED;
            } else {
                this.state = STATE.SOLVED;
            }
        }

        /**
         * Recursively possibilities from current state
         */
        private void findPossibilities() {
            var v = values;
            var m_sum = values.length - p_sum;
            var r_min = 0;
            var r_max = m_sum;
            var length = (int) Math.ceil(params.size() / 2d);

            // Calculate all Possibilities with Permutation Util
            NonogramPermutationUtil util = new NonogramPermutationUtil(r_min, r_max, m_sum, length);
            var permutations = util.getPermutations();
            // Verify all Possibilities
            main_loop:
            for (var p : permutations) {
                ArrayList<Integer> formattedPermutation = new ArrayList<>();
                for (int i = 0; i < params.size(); i++) {
                    if (i % 2 == 0) {
                        formattedPermutation.add(p[i / 2]);
                    } else {
                        formattedPermutation.add(params.get(i));
                    }
                }
                // Now there exists a formatted permutations of alternating 0's and 1's that contain a possible nonogram sequence
                // The actual values can
                int[] permutationValues = new int[values.length];
                int tp_sum = 0;
                for (int i = 0; i < formattedPermutation.size(); i++) { // Populates actual value array
                    Arrays.fill(permutationValues, tp_sum, tp_sum + formattedPermutation.get(i), (i % 2 == 0) ? 0 : 1);
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
        private int[] findCommonalities(int i, int[] cur) {
            if (i == 1) {
                for (int x = 0; x < cur.length; x++) {
                    cur[x] = (cur[x] == possibilities.get(0).values[x]) ? cur[x] : -1;
                }
                return cur;
            } else {
                var dur = possibilities.get(i - 1).values;
                boolean end_condition = true;
                for (int x = 0; x < cur.length; x++) {
                    cur[x] = (cur[x] == dur[x]) ? cur[x] : -1;
                    if (cur[x] != -1) {
                        end_condition = false;
                    }
                }
                if (end_condition) {
                    return cur;
                }

                return findCommonalities(i - 1, cur);
            }
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
