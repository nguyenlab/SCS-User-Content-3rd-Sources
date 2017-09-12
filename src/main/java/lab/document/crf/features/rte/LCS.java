package lab.document.crf.features.rte;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class LCS {

    public static ArrayList<Double> readFile(File file) throws IOException {
        ArrayList<Double> length = new ArrayList<Double>();
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
            int j = lcs(Arrays.asList(word), Arrays.asList(word2));
            double tmp = (double) j / word.length;
            length.add(tmp);
        }
        return length;

    }

    public static int lcs(List<String> a, List<String> b) {
        int[][] length = new int[a.size() + 1][b.size() + 1];
        for (int i = a.size() - 1; i >= 0; i--) {
            for (int j = b.size() - 1; j >= 0; j--) {
                if (a.get(i).equals(b.get(j))) {
                    length[i][j] = length[i + 1][j + 1] + 1;
                } else {
                    length[i][j] = Math.max(length[i + 1][j], length[i][j + 1]);
                }
            }
        }
        return length[0][0];
    }

    static float[] frequecy(List<String> a, List<String> b) {
        float fre[] = new float[a.size()];
        for (int i = 0; i < b.size(); i++) {
            float tmp = 0;
            for (int j = 0; j < a.size(); j++) {
                if (b.get(i).equals(a.get(j))) {
                    tmp++;
                }
            }
            fre[i] = tmp / (float) a.size();
        }
        return fre;
    }

}
