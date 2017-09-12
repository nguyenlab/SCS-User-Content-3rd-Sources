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
import lab.document.crf.features.rte.IDF;
import lab.document.crf.features.rte.LCS;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class MaxLexicalRTE implements Feature<List<double[]>, List<Doc>> {
	int feature_number = 5;
	
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
			Set<String> Stokens = getTokens.apply(doc.getSentences().get(i));
			List<String> s = new ArrayList<String>();
			for (String tmp : Stokens) s.add(tmp);
			
			for (int j = 0; j < comment.getSentences().size(); j++) {
				Set<String> Ctokens = getTokens.apply(comment.getSentences().get(j));
				List<String> t = new ArrayList<String>();
				for (String tmp : Ctokens) t.add(tmp);
				
				double maxLexical = lexicalRTESim(s, t);
				matrixSim[i][j] = maxLexical;
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
		return "MaxLexicalRTE";
	}
	
	private double lexicalRTESim (List<String> s, List<String> t){
		//Coefficient features
		double inclu_exclu_coefficient = Coefficient.inclu_exclu_coefficient(s, t);
		double word_overlap_coefficient = Coefficient.Overlap_coefficient(s, t);
		
    	double lsc_value=(double)LCS.lcs(s, t)/(double)Math.min(s.size(), t.size());
    	//Percentage of words in s appearing in t
    	double idf_percentage_st = IDF.occurT(s,t);
    	//Percentage of words in t apprearing in s
    	double idf_percentage_ts = IDF.occurT(t,s);
    	
   		double lexicalScore = lsc_value + inclu_exclu_coefficient + idf_percentage_st
						+ idf_percentage_ts + word_overlap_coefficient;
		
		return (double)lexicalScore/feature_number;
	}

}
