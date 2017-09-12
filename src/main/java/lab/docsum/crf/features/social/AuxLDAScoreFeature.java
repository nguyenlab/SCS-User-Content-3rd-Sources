package lab.docsum.crf.features.social;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.util.CoreMap;
import lab.docsum.crf.features.Feature;
import lab.docsum.misc.Doc;
import lab.docsum.misc.NLPUtils;

public class AuxLDAScoreFeature implements Feature<double[], Doc> {
	private String ldaFolder;
	
	public AuxLDAScoreFeature(String ldaFolder) {
		// TODO Auto-generated constructor stub
		this.ldaFolder = ldaFolder;
	}

	@Override
	public double[] extract(Doc doc, Object... args) {

		double[] ft = new double[doc.getSentences().size()];

		Function<CoreMap, Set<String>> getTokens = (cm) -> cm
				.get(TokensAnnotation.class).stream()
				.map(e -> e.get(TextAnnotation.class).toLowerCase())
				.collect(Collectors.toSet());

//		String ldaFolder = "";
		Map<String, Double> ldaWordDist = new HashMap<>();
//		switch (doc.getTag()) {
//		case "D":
//			// load lda file doc
//			ldaFolder = ldaDir + "/comment-sum/";
//			break;
//		case "C":
//			// load lda comment
//			ldaFolder = ldaDir + "/doc-sum/";
//			break;
//		}
		String name = getName(ldaFolder, doc.getSource());
		IOUtils.readLines(ldaFolder +"/"+ name).forEach(line -> {
			String[] cols = line.split("\\s+");
			ldaWordDist.put(cols[0], Double.valueOf(cols[1]));
		});

		for (int i = 0; i < doc.getSentences().size(); i++) {
			Set<String> tokens = getTokens.apply(doc.getSentences().get(i));
			List<String> words = tokens.stream()
					.filter(tk -> !NLPUtils.stopwords.contains(tk))
					.collect(Collectors.toList());
			double localLDAScore = localLDAScore(words, ldaWordDist);
			ft[i] = localLDAScore;
		}
		return ft;
	}

	@Override
	public String name() {
		return "Aux_LDA_Score";
	}

	private double localLDAScore(List<String> words,
			Map<String, Double> wordDist) {
		if (words.isEmpty())
			return 0.0;
		return words.stream().mapToDouble(w -> wordDist.getOrDefault(w, 0.0))
				.average().getAsDouble();
	}

	private String getName(String ldaFolder, String docSource) {
		String id = docSource.split("\\D+")[0];
		File folder = new File(ldaFolder);
		File[] files = folder.listFiles();
		for (File f : files) {
			String name = f.getName();
			if (name.startsWith(id))
				return name;
		}
		throw new IllegalStateException("Can not match with LDA file...");
	}
}
