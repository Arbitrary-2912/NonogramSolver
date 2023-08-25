import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NonogramPermutationUtil {
    static Map<Integer, Character> cim = new HashMap<>();
    private int r_min = 0, r_max = 0; // minimum and maximum length of a contiguous break, maximum should typically be the same as the sum.
    private int m_sum = 0;
    private ArrayList<Integer[]> permutations = new ArrayList<>();

    /**
     * Base instantiation of utility object
     *
     * @param r_min      minimum value of any entry in permutation
     * @param r_max      maximum value of any entry in permutation
     * @param m_sum      maximum sum of all values in permutation
     * @param arr_length number of entries in permutation
     */
    public NonogramPermutationUtil(int r_min, int r_max, int m_sum, int arr_length) {
        this.r_min = r_min;
        if (arr_length == 0) arr_length = 1; // defaulting
        char[] perm = new char[arr_length]; // framework for permutation data

        // populating cim map
        for (int i = 0; i < 100; i++) { // populate chars to a ridiculously high extent
            cim.put(i, (char) (i + 48));
        }
        String str = "";
        for (int i = r_min; i <= r_max; i++) { // 0 is the minimum, 15 is the maximum sequence
            str += cim.get(i);
        }
        permutation(perm, 0, str, m_sum);
    }

//    public static void main(String[] args) { // Test code
//        NonogramPermutationUtil l_util = new NonogramPermutationUtil(0, 15, 15, 4);
//        var x = l_util.getPermutations();
//        for (var j: x) {
//            for (var k: j) {
//                System.out.print(k + " ");
//            }
//            System.out.println();
//        }
//    }

    /**
     * Calculates all permutations
     *
     * @param perm  permutation frame array
     * @param pos   position on array for iteration
     * @param str   string of all possible permutation entry characters
     * @param M_sum maximum sum (verification measure)
     */
    private void permutation(char[] perm, int pos, String str, int M_sum) {
        if (pos == perm.length) {
            if (getSum(perm) == M_sum) {
                boolean pass = true;
                for (int i = 1; i < perm.length - 1; i++) { // ensures no zeroes in the middle
                    if (perm[i] == 48 + r_min) {
                        pass = false;
                        break;
                    }
                }
                if (pass) {
                    Integer[] numericalPermutation = new Integer[perm.length];
                    for (int i = 0; i < perm.length; i++) {
                        numericalPermutation[i] = (int) (perm[i] - 48);
                    }
                    permutations.add(numericalPermutation);
                }
            }
        } else {
            for (int i = 0; i < str.length(); i++) {
                perm[pos] = str.charAt(i);
                permutation(perm, pos + 1, str, M_sum);
            }
        }
    }

    /**
     * Returns list of permutations per parameters
     *
     * @return permutations
     */
    ArrayList<Integer[]> getPermutations() {
        return permutations;
    }

    /**
     * Calculates the sum of an array of chars
     *
     * @param perm array
     * @return sum of array
     */
    private int getSum(char[] perm) {
        var s = 0;
        for (char p : perm) {
            s += p - 48;
        }
        return s;
    }
}
