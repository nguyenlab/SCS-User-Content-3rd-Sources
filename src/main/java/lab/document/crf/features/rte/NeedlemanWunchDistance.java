package lab.document.crf.features.rte;

import java.util.List;

public class NeedlemanWunchDistance {
    // Using simple linear gap score (-2 per indel)
    // and 4 for a match, -1 for a mismatch
    // Feel free to change this

    public static final int gapscore = -2;
    public static final int matchscore = 4;
    public static final int mismatchscore = -1;

    private List<String> x; // First string
    private List<String> y; // Second string
    private int xlen, ylen; // their lengths
    private int[][] scoreArray;

    public NeedlemanWunchDistance(List<String> a, List<String> b) {
        x = a;
        y = b;
        xlen = x.size();
        ylen = y.size();
        scoreArray = new int[ylen + 1][xlen + 1];
    }

    public void fillScoreArray() {
        int row, col; // for indexing through array
        int northwest, north, west; // (row, col) entry will be max of these
        int best; // will be the max
        // Fill the top row and left column:
        for (col = 0; col <= x.size(); col++) {
            scoreArray[0][col] = gapscore * col;
        }
        for (row = 0; row <= y.size(); row++) {
            scoreArray[row][0] = gapscore * row;
        }
        // Now fill in the rest of the array:
        for (row = 1; row <= y.size(); row++) {
            for (col = 1; col <= x.size(); col++) {
                if (x.get(col - 1).equals(y.get(row - 1))) {
                    northwest = scoreArray[row - 1][col - 1] + matchscore;
                } else {
                    northwest = scoreArray[row - 1][col - 1] + mismatchscore;
                }
                west = scoreArray[row][col - 1] + gapscore;
                north = scoreArray[row - 1][col] + gapscore;
                best = northwest;
                if (north > best) {
                    best = north;
                }
                if (west > best) {
                    best = west;
                }
                scoreArray[row][col] = best;
            }
        }
    }

    public void print3(int x) {
        // Print x in 3 spaces
        String s = "" + x;
        if (s.length() == 1) {
            System.out.print("  " + s);
        } else if (s.length() == 2) {
            System.out.print(" " + s);
        } else if (s.length() == 3) {
            System.out.print(s);
        } else {
            System.out.print("***");
        }
    }

    public void printArray() {
        for (int row = 0; row < scoreArray.length; row++) {
            for (int col = 0; col < scoreArray[row].length; col++) {
                print3(scoreArray[row][col]);
            }
            System.out.println();
        }
    }
}
