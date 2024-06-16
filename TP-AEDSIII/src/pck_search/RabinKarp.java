package pck_search;

public class RabinKarp {
    // prime number used to calculate the hash
    private static final int PRIME = 233;

    //function to search for a pattern in the file
    public long[] searchPattern(String text, String pattern) {
        // Variables to calculate the time
        long start = System.currentTimeMillis();
        long end;
        long timeElapsed;

        int patternLength = pattern.length();
        int textLength = text.length();
        long patternHash = calculateHash(pattern);
        long textHash = calculateHash(text.substring(0, patternLength));
        long pos = 0;

        // loop to check if the hash of the pattern is equal to the hash of the first part of the text
        for (int i = 0; i <= textLength - patternLength; i++) {
            // check if the hash of the pattern is equal to the hash of the part of the text
            if (patternHash == textHash && pattern.equals(text.substring(i, i + patternLength))) {
                end = System.currentTimeMillis();
                timeElapsed = end - start;
                return new long[]{i,timeElapsed};
            }
            // otherwise, recalculate the hash of the text
            if (i < textLength - patternLength) {
                textHash = recalculateHash(textHash, text.charAt(i), text.charAt(i + patternLength), patternLength);
            }
        }
        end = System.currentTimeMillis();
        timeElapsed = end - start;
        return new long[]{-1,timeElapsed};
    }

    //function to calculate the hash 
    private static long calculateHash(String str) {
        long hash = 0;

        for (int i = 0; i < str.length(); i++) {
            hash += str.charAt(i) * Math.pow(PRIME, i);
        }
        return hash;
    }

    // function to recalculate the hash 
    private static long recalculateHash(long oldHash, char oldChar, char newChar, int patternLength) {
        long newHash = oldHash - oldChar;
        newHash /= PRIME;
        newHash += newChar * Math.pow(PRIME, patternLength - 1);
        return newHash;
    }
}
