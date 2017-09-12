package lab.docsum.crf.features.thirdparty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import edu.stanford.nlp.util.CoreMap;
import lab.docsum.crf.features.Feature;
import lab.docsum.misc.Doc;
import lab.docsum.misc.NLPUtils;

public class P_KeyWord implements Feature<double[], List<Doc>> {
	int threshold = 5;
	
	public P_KeyWord(int threshold) {
		// TODO Auto-generated constructor stub
		this.threshold = threshold;
	}
	
	public P_KeyWord() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public double[] extract(List<Doc> input, Object... args) {
		// TODO Auto-generated method stub
		Doc doc = input.get(0);
		List<CoreMap> sents = doc.getSentences();
		double[] ft = new double[sents.size()];
		
		//collecting a set of sentences
		List<List<String>> lstSents = new ArrayList<>();
		for (CoreMap s : sents){
			List<String> tokens = NLPUtils.getTokenList.apply(s);
			List<String> words = tokens.stream()
					.filter(tk -> !NLPUtils.stopwords.contains(tk))
					.collect(Collectors.toList());
			lstSents.add(words);
		}
		Map<String, Integer> docMapTerms = NLPUtils.getFrequencyTerm(lstSents, 0);
		Set<String> docKeys = docMapTerms.keySet();
		
		//collecting a set of supporting sentences
		List<List<String>> lstSupportingSents = new ArrayList<>();
		for (int i = 1; i<input.size(); i++){
			for(CoreMap s : input.get(i).getSentences()){
				List<String> tokens = NLPUtils.getTokenList.apply(s);
				List<String> words = tokens.stream()
						.filter(tk -> !NLPUtils.stopwords.contains(tk))
						.collect(Collectors.toList());
				lstSupportingSents.add(words);
			}
		}
		//getting frequency terms
		Map<String, Integer> supportingDocs_frqTerms = NLPUtils.getFrequencyTerm(lstSupportingSents, threshold);
		Set<String> supportingDocKeys = supportingDocs_frqTerms.keySet();
		
		//calculating ST for each sentence
		for(int i = 0; i<sents.size(); i++){
			List<String> tokens = NLPUtils.getTokenList.apply(sents.get(i));
			List<String> words = tokens.stream()
					.filter(tk -> !NLPUtils.stopwords.contains(tk))
					.collect(Collectors.toList());
			
			double p_q = 0;
			for (String key : supportingDocKeys){
				if (words.contains(key)){
					int count_q = docMapTerms.get(key);
					int nKeys = getKeyDoc(docKeys, supportingDocKeys);
					p_q += (double) count_q/nKeys;
				}
			}
			ft[i] = (double)p_q/(words.size());
		}
			
		return ft;
	}
	
	private int getKeyDoc(Set<String> docKeys, Set<String> supportingDocKeys){
		int count = 0;
		for (String key : supportingDocKeys)
			if (docKeys.contains(key)) count++;
		return count;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "P_Keyword";
	}

}
