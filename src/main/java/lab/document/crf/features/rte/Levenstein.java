package lab.document.crf.features.rte;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Levenstein {

    private static int minOfThree(int a, int b, int c) {
        return Math.min(a, Math.min(b, c));
    }

    private static int cost(String t, String h) {
        return t.equals(h) ? 0 : 1;
    }

    private static int[][] initializeMatrix(int s, int t) {
        int[][] matrix = new int[s + 1][t + 1];
        for (int i = 0; i <= s; i++) {
            matrix[i][0] = i;
        }

        for (int j = 0; j <= t; j++) {
            matrix[0][j] = j;
        }
        return matrix;
    }

    public static int distance(List<String> t, List<String> h) {
        if (t == h) {
            return 0;
        }
        if (t.isEmpty()) {
            return h.size();
        }
        if (h.isEmpty()) {
            return t.size();
        }

        int matrix[][] = initializeMatrix(t.size(), h.size());

        for (int i = 0; i < t.size(); i++) {
            for (int j = 1; j <= h.size(); j++) {
                matrix[i + 1][j] = minOfThree(matrix[i][j] + 1,
                        matrix[i + 1][j - 1] + 1, matrix[i][j - 1]
                        + cost(t.get(i), h.get(j - 1)));
            }
        }

        return matrix[t.size()][h.size()];
    }

    static ArrayList<Integer> readFile(File file) throws IOException {
        ArrayList<Integer> length = new ArrayList<Integer>();
        FileReader read = new FileReader(file);
        BufferedReader in = new BufferedReader(read);
        String s = null;
        ArrayList<String> hSens = new ArrayList<String>();
        ArrayList<String> tSens = new ArrayList<String>();

        while ((s = in.readLine()) != null) {
            tSens.add(s);
            String temp = in.readLine();
            hSens.add(temp);

        }
        in.close();
        for (int i = 0; i < hSens.size(); i++) {
            String word[] = hSens.get(i).split(":");
            String word2[] = tSens.get(i).split(":");
            int j = distance(Arrays.asList(word2), Arrays.asList(word));

            length.add(j);
        }

        return length;

    }
}
