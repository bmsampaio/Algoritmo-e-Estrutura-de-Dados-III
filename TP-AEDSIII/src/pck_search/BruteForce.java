package pck_search;

public class BruteForce {

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

    public Result BruteForceSearch(String txt, String pat) {
        int M = pat.length();
        int N = txt.length();
        int comparisons = 0;

        long startTime = System.currentTimeMillis();

        for (int i = 0; i <= N - M; i++) {
            int j;
            for (j = 0; j < M; j++) {
                comparisons++;
                if (txt.charAt(i + j) != pat.charAt(j)) {
                    break;
                }
            }
            if (j == M) {
                System.out.println("Found pattern at index " + i);
            }
        }

        long endTime = System.currentTimeMillis();
        long totalTime = (endTime - startTime);

        return new Result(comparisons, totalTime);
    }
}
