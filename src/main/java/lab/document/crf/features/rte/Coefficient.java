package lab.document.crf.features.rte;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Coefficient {

    public static double Matching_coefficient(List<String> t, List<String> h) {
        Set<String> common = new HashSet<>(t);
        common.retainAll(h);
        return common.size();
    }

    public static double Dice_coefficient(List<String> t, List<String> h) {
        Set<String> X = new HashSet<>(t);
        Set<String> Y = new HashSet<>(h);

        double tmp = 2 * Matching_coefficient(t, h);
        double tmp2 = X.size() + Y.size();
        return tmp / tmp2;
    }

    public static double Overlap_coefficient(List<String> t, List<String> h) {
        double count = Matching_coefficient(t, h);
        Set<String> X = new HashSet<>(t);
        Set<String> Y = new HashSet<>(h);
      
        return count / Math.min(X.size(), Y.size());
    }

    public static double Jaccard_coefficient(List<String> t, List<String> h) {

        Set<String> setWords = new HashSet<>();
        setWords.addAll(t);
        setWords.addAll(h);
        double tmp = Matching_coefficient(t, h);
        return tmp / setWords.size();

    }

    public static double inclu_exclu_coefficient(List<String> t, List<String> h) {
        Set<String> X = new HashSet<>(t);
        Set<String> Y = new HashSet<>(h);

        Set<String> setWords = new HashSet<>();
        setWords.addAll(X);
        setWords.addAll(Y);
       
        double tmp = X.size() + Y.size();
        return (double) (setWords.size()) / tmp;
    }
}
