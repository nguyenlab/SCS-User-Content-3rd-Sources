package lab.document.crf.features.rte;

import java.util.List;

public class DamerauLevenshtein {

    private List<String> compOne;
    private List<String> compTwo;
    private int[][] matrix;
    private Boolean calculated = false;

    public DamerauLevenshtein(List<String> a, List<String> b) {
        if (a != null && b != null) {
            compOne = a;
            compTwo = b;
        }
    }

    public int[][] getMatrix() {
        setupMatrix();
        return matrix;
    }

    public int getDistance() {
        if (!calculated) {
            setupMatrix();
        }

        return matrix[compOne.size()][compTwo.size()];
    }

    private void setupMatrix() {
        int cost = -1;
        int del, sub, ins;

        matrix = new int[compOne.size() + 1][compTwo.size() + 1];

        for (int i = 0; i <= compOne.size(); i++) {
            matrix[i][0] = i;
        }

        for (int i = 0; i <= compTwo.size(); i++) {
            matrix[0][i] = i;
        }

        for (int i = 1; i <= compOne.size(); i++) {
            for (int j = 1; j <= compTwo.size(); j++) {
                if (compOne.get(i - 1).equals(compTwo.get(j - 1))) {
                    cost = 0;
                } else {
                    cost = 1;
                }

                del = matrix[i - 1][j] + 1;
                ins = matrix[i][j - 1] + 1;
                sub = matrix[i - 1][j - 1] + cost;

                matrix[i][j] = minimum(del, ins, sub);

                if ((i > 1) && (j > 1) && (compOne.get(i - 1).equals(compTwo.get(j - 2)))
                        && (compOne.get(i - 2).equals(compTwo.get(j - 1)))) {
                    matrix[i][j] = minimum(matrix[i][j], matrix[i - 2][j - 2]
                            + cost);
                }
            }
        }

        calculated = true;
        // displayMatrix();
    }

    private void displayMatrix() {
        System.out.println("" + compOne);
        for (int y = 0; y <= compTwo.size(); y++) {
            if (y - 1 < 0) {
                System.out.print("");
            } else {
                System.out.print(compTwo.get(y - 1));
            }
            for (int x = 0; x <= compOne.size(); x++) {
                System.out.print(matrix[x][y]);
            }
            System.out.println();

        }
    }

    private int minimum(int d, int i, int s) {
        int m = Integer.MAX_VALUE;

        if (d < m) {
            m = d;
        }
        if (i < m) {
            m = i;
        }
        if (s < m) {
            m = s;
        }

        return m;
    }

    private int minimum(int d, int t) {
        int m = Integer.MAX_VALUE;

        if (d < m) {
            m = d;
        }
        if (t < m) {
            m = t;
        }

        return m;
    }
}
