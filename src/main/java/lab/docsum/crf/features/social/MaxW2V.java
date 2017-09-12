package lab.docsum.crf.features.social;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import lab.docsum.crf.features.Feature;
import lab.docsum.misc.Doc;
import lab.w2v.SimpleWord2VecDouble;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class MaxW2V implements Feature<List<double[]>, List<Doc>> {
	SimpleWord2VecDouble w2v = null;

	
	public MaxW2V(String w2vPath){
		System.out.println("Word2Vec path:=" + w2vPath);
		try {
			w2v = SimpleWord2VecDouble.loadGoogleModel(w2vPath, true);
			System.out.println("successfully loading w2v model");
		} catch (IOException ex) {
			Logger.getLogger(MaxW2V.class.getName()).log(Level.SEVERE,null,ex);
		}
	}

	public MaxW2V() {
		// TODO Auto-generated constructor stub
	}

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
			for (String tmp : tokens)
				s.add(tmp);

			for (int j = 0; j < comment.getSentences().size(); j++) {
				Set<String> Ctokens = getTokens.apply(comment.getSentences()
						.get(j));
				List<String> t = new ArrayList<String>();
				for (String tmp : Ctokens)
					t.add(tmp);

				//double s2vSim = w2vSim(s, t);
				double s2vSim = w2vSimV(s, t);
				matrixSim[i][j] = s2vSim;
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
		return "MaxW2V";
	}

	private double w2vSim(List<String> s, List<String> t) {
		double w2vSim = 0;
		for (String w1 : s) {
			for (String w2 : t) {
				double score = w2v.similarity(w1, w2);
				w2vSim += score;
			}
		}
		return w2vSim/(s.size() + t.size());
	}
	
	private double w2vSimV(List<String> s, List<String> t) {
		double w2vSim = 0;
		
		List<String> s_nospace = new ArrayList<String>();
		for (String w : s)
			s_nospace.add(w.replace("_", " "));
		List<String> t_nospace = new ArrayList<String>();
		for (String w : t)
			s_nospace.add(w.replace("_", " "));
		
		for (String w1 : s_nospace) {
			for (String w2 : t_nospace) {
				double score = w2v.similarity(w1, w2);
				w2vSim += score;
			}
		}
		return w2vSim/(s.size() + t.size());
	}
}
