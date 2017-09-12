package lab.docsum.crf.features.social;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import lab.docsum.crf.features.Feature;
import lab.docsum.misc.Doc;
import lab.document.crf.features.rte.Coefficient;
import lab.document.crf.features.rte.DamerauLevenshtein;
import lab.document.crf.features.rte.JaroWinklerDistance;
import lab.document.crf.features.rte.Levenstein;
import lab.document.crf.features.rte.OtherFeature;
import lab.document.crf.features.rte.SmithWaterman;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class MaxDistanceRTE implements Feature<List<double[]>, List<Doc>> {
	
	JaroWinklerDistance jaro=new JaroWinklerDistance();
	SmithWaterman sw;
	DamerauLevenshtein dl;
	int feature_number = 9;
	
	@Override
	public List<double[]> extract(List<Doc> input, Object... args) {
		// TODO Auto-generated method stub
		Doc doc = input.get(0);
		Doc comment = input.get(1);
		double[][] matrixSim = new double[doc.getSentences().size()][comment
				.getSentences().size()];
		Function<CoreMap, Set<String>> getTokens = (cm) -> cm
				.get(TokensAnnotation.class).stream()
				.map(e -> e.get(LemmaAnnotation.class).toLowerCase())
				.collect(Collectors.toSet());

		for (int i = 0; i < doc.getSentences().size(); i++) {
			Set<String> tokens = getTokens.apply(doc.getSentences().get(i));
			List<String> s = new ArrayList<String>();
			for (String tmp : tokens) s.add(tmp);
			
			for (int j = 0; j < comment.getSentences().size(); j++) {
				Set<String> Ctokens = getTokens.apply(comment
						.getSentences().get(j));
				List<String> t = new ArrayList<String>();
				for (String tmp : Ctokens) t.add(tmp);
				
				double distanceSim = distanceRTESim(s, t);
				matrixSim[i][j] = distanceSim;
			}
		}
		double[] maxRow = maxRows(matrixSim);
		double[] maxColumn = maxColumn(matrixSim);

		return Arrays.asList(maxRow, maxColumn);
	}

	private double[] maxRows(double[][] matrix) {
		// double[] row = matrix[index];
		// double max = Double.NEGATIVE_INFINITY;
		// for (int i = 0; i<row.length; i++){
		// if (max<row[i]) max = row[i];
		// }
		// return max;

		// return Arrays.stream(matrix[index]).max().getAsDouble();

		return Arrays.stream(matrix)
				.mapToDouble(row -> Arrays.stream(row).max().getAsDouble())
				.toArray();
	}

	private double[] maxColumn(double[][] matrix) {
		// double max=Double.NEGATIVE_INFINITY;
		// for(int i=0;i<matrix.length;i++){
		// max=Math.max(max, matrix[i][col]);
		//
		// }
		// return max;

		// return Arrays.stream(matrix).map(m -> m[col]).max(Double::compare)
		// .get();

		double[] maxs = matrix[0].clone();
		for (int i = 1; i < matrix.length; i++) {
			for (int j = 0; j < maxs.length; j++) {
				maxs[j] = Math.max(maxs[j], matrix[i][j]);
			}
		}
		return maxs;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "MaxDistanceRTE";
	}
	
	private double distanceRTESim (List<String> s, List<String> t){
		sw = new SmithWaterman(s,t);
		dl = new DamerauLevenshtein(s, t);
		
		//Other features
		double manhatan = OtherFeature.manhatanDistance(s, t);
		double euclidean = OtherFeature.euclidean(s, t);
		double cosine = OtherFeature.cosine(s, t);
		
		//Coefficient features
		double dice_coefficient = Coefficient.Dice_coefficient(s, t);
		double jarccard_coefficient = Coefficient.Jaccard_coefficient(s, t);
		
    	double lenvenstein_distance=Levenstein.distance(s,t); 
    	
    	//Jaro distance
    	double jaro_distance = jaro.proximity(s,t);
		//SmithWaterman value		
		double smith_waterman = (double)sw.computeSmithWaterman();
		//DamerauLevenshtein value
    	double damerrau_Levenshtein = (double) dl.getDistance();
    	double total = manhatan + euclidean + cosine + dice_coefficient + jarccard_coefficient + jaro_distance + 
    			lenvenstein_distance + damerrau_Levenshtein + smith_waterman;
    	
		return (double)total/feature_number;
	}

}
