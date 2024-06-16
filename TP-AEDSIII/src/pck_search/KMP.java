package pck_search;

import java.util.Arrays;

public class KMP {

    public static class Result {
        private int comparisons;
        private long totalTime;

        public Result(int comparisons, long totalTime) {
            this.comparisons = comparisons;
            this.totalTime = totalTime;
        }

        public int getComparisons() {
            return comparisons;
        }

        public long getTotalTime() {
            return totalTime;
        }
    }

    public Result KMPSearch(String txt, String pat) {
        int M = pat.length() + 1;
        long N = txt.length();

        int[] lps = new int[M];
        int[] lps_ = new int[M];
        int j = 0;
        int comparisons = 0;

        computeLPSArray(pat, M, lps, lps_);

        int i = 0;
        long start = System.currentTimeMillis();
        while (i < N) {
            comparisons++;
            if (pat.charAt(j) == txt.charAt(i)) {
                j++;
                i++;
            }
            if (j == M - 1) {
                System.out.println("Found pattern at index " + (i - j));
                j = lps_[j];
            } else if (i < N && pat.charAt(j) != txt.charAt(i)) {
                if (lps_[j] == -1) {
                    i += 1;
                    j = 0;
                } else {
                    j = lps_[j];
                }
            }
        }

        long finish = System.currentTimeMillis();
        long total = (finish - start);
        return new Result(comparisons, total);
    }

    void computeLPSArray(String pat, int M, int[] lps, int[] lps_) {
        int comp = 0;
        int pos = 2;
        int i = 1;
        lps[0] = -1;
        lps[1] = 0;

        // Common KMP
        while (i < M - 1) {
            if (pat.charAt(i) == pat.charAt(comp)) {
                comp++;
                lps[pos] = comp;
                pos++;
                i++;
            } else {
                comp = 0;
                if (pat.charAt(i) == pat.charAt(comp)) {
                    comp++;
                    lps[pos] = comp;
                    pos++;
                    i++;
                } else {
                    lps[pos] = comp;
                    pos++;
                    i++;
                }
            }
        }
        // System.out.println("KMP");
        // System.out.println(Arrays.toString(lps));

        // Improved KMP
        pos = 1;
        comp = 0;
        lps_[0] = -1;
        lps_[M - 1] = lps[M - 1];

        while (pos < (M - 1)) {
            if (pat.charAt(pos) == pat.charAt(comp)) {
                lps_[pos] = lps_[comp];
                pos++;
                comp++;
            } else {
                lps_[pos] = lps[pos];
                pos++;
                comp = 0;
            }
        }

        // System.out.println("Improved KMP");
        // System.out.println(Arrays.toString(lps_));
    }
}
