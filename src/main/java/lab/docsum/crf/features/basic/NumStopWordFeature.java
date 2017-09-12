package lab.docsum.crf.features.basic;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.util.CoreMap;
import lab.docsum.crf.features.Feature;
import lab.docsum.misc.Doc;
import lab.docsum.misc.NLPUtils;

public class NumStopWordFeature implements Feature<int[],Doc>  {

	@Override
	public int[] extract(Doc doc, Object... args) {
		
		int[] ft = new int[doc.getSentences().size()];
		
		Function<CoreMap, Set<String>> getTokens = (cm) -> cm
				.get(TokensAnnotation.class).stream()
				.map(e -> e.get(TextAnnotation.class).toLowerCase())
				.collect(Collectors.toSet());

		for (int i = 0; i < doc.getSentences().size(); i++) {
			Set<String> tokens = getTokens.apply(doc.getSentences().get(i));
			long nStopwords = tokens.stream().filter(tk->NLPUtils.stopwords.contains(tk)).count();
			ft[i] = (int) nStopwords;
		}
		return ft;
	}

	@Override
	public String name() {
		return "Num_Stop_Words";
	}
}
