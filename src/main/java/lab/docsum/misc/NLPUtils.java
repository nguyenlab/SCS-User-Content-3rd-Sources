package lab.docsum.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class NLPUtils {
	public static final Set<String> stopwords;
	public static final StanfordCoreNLP instance;
	public static final Set<String> indicatorWords;

	static {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, ner");
		//props.setProperty("annotators", "tokenize, ssplit");
		instance = new StanfordCoreNLP(props);

		BufferedReader reader = new BufferedReader(
				new InputStreamReader(NLPUtils.class.getResourceAsStream("stopwords.txt")));
		Set<String> tmp = new HashSet<String>();
		try {
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				tmp.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		stopwords = Collections.unmodifiableSet(tmp);

		reader = new BufferedReader(new InputStreamReader(NLPUtils.class.getResourceAsStream("indicators.txt")));
		tmp = new HashSet<String>();
		try {
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				tmp.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		indicatorWords = Collections.unmodifiableSet(tmp);
	}
	
	public static Map<String, Integer> getFrequencyTerm(List<List<String>> lstSupportingSents, int threshold){
		Map<String, Integer> mapWords = new HashMap<>();
		List<String> lstWords = new ArrayList<>();
		
		//collecting words
		for (List<String> sent : lstSupportingSents){
			for (String w : sent){
				lstWords.add(w);
			}
		}
		boolean[] flag = new boolean[lstWords.size()];
		for (int i = 0; i< flag.length; i++)
			flag[i] = true;
		for (int i = 0; i<lstWords.size(); i++){
			String w1 = lstWords.get(i);
			int frq = 0;
			for (int j = 0; j<lstWords.size(); j++){
				String w2 = lstWords.get(j);
				if (w1.equals(w2) && (flag[j])) {
					frq ++;
					flag[j] = false;
				}
			}
			if (frq>threshold)
				mapWords.put(w1, frq);
		}
		return mapWords;
	}
	
	public static Function<CoreMap, Set<String>> getTokenSet = (cm) -> cm
			.get(TokensAnnotation.class).stream()
			.map(e -> e.get(LemmaAnnotation.class).toLowerCase())
			.collect(Collectors.toSet());
	
	public static Function<CoreMap, List<String>> getTokenList = (cm) -> cm
			.get(TokensAnnotation.class).stream()
			.map(e -> e.get(LemmaAnnotation.class).toLowerCase())
			.collect(Collectors.toList());
}
