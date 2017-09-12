package lab.docsum.crf.features.social;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import lab.docsum.crf.features.Feature;
import lab.docsum.misc.Doc;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class MaxCosine implements Feature<List<double[]>, List<Doc>> {
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
			for (int j = 0; j < comment.getSentences().size(); j++) {
				Set<String> neighborTokens = getTokens.apply(comment
						.getSentences().get(j));
				long inter = tokens.stream()
						.filter(e -> neighborTokens.contains(e)).count();
				double cosineSim = inter
						/ Math.sqrt(tokens.size() * neighborTokens.size()
								+ 1e-6);
				matrixSim[i][j] = cosineSim;
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
		return "MaxCosine";
	}

}
