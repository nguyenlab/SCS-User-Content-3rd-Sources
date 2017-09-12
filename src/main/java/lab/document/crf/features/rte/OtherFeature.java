package lab.document.crf.features.rte;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public  class OtherFeature {

    // Search different words in two sentences
    public static Set<String> distinctWord(List<String> t, List<String> h) {
        Set<String> setWords = new HashSet<String>(t);
        setWords.addAll(h);
        return setWords;
    }

    // Create an vector with elements are 
    public static ArrayList<Integer> vector(Set<String> word, List<String> s) {

        ArrayList<Integer> v = new ArrayList<Integer>();
        for (String w : word) {
            int countw = 0;
            for (int i = 0; i < s.size(); i++) {
                if (s.get(i).equals(w)) {
                    countw++;
                }
            }
            v.add(countw);
        }
        return v;
    }

    // Manhatan Distance
    public static  double manhatanDistance(List<String> t, List<String> h) {
        Set<String> setWords = distinctWord(t, h);
        ArrayList<Integer> x = vector(setWords, t);
        ArrayList<Integer> y = vector(setWords, h);
        double ManhatanDistance = 0;
        for (int i = 0; i < x.size(); i++) {

            ManhatanDistance = ManhatanDistance + Math.abs(x.get(i) - y.get(i));
        }

        return ManhatanDistance;
    }

    // Euclidean Distance
    public static double euclidean(List<String> t, List<String> h) {
        Set<String> setWords = distinctWord(t, h);
        ArrayList<Integer> x = vector(setWords, t);
        ArrayList<Integer> y = vector(setWords, h);
        double Euclidean = 0;
        for (int i = 0; i < x.size(); i++) {

            Euclidean = Euclidean + (x.get(i) - y.get(i))
                    * (x.get(i) - y.get(i));
        }

        return Math.sqrt(Euclidean);
    }

    // Cosine score
    public static double cosine(List<String> t, List<String> h) {
        Set<String> setWords = distinctWord(t, h);
        ArrayList<Integer> x = vector(setWords, t);
        ArrayList<Integer> y = vector(setWords, h);
        double Cosin = 0;
        double valueX = 0;
        double valueY = 0;
        double value = 0;

        for (int i = 0; i < x.size(); i++) {

            value = value + x.get(i) * y.get(i);
            valueX = valueX + x.get(i) * x.get(i);
            valueY = valueY + y.get(i) * y.get(i);
        }
        Cosin = value / Math.sqrt(valueY * valueX);
        return Cosin;
    }
}
