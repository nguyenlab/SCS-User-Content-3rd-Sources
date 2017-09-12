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

public class STF_IDF implements Feature<double[], List<Doc>> {
	int threshold = 5;
	
	public STF_IDF(int threshold) {
		// TODO Auto-generated constructor stub
		this.threshold = threshold;
	}
	
	public STF_IDF() {
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
		Map<String, Integer> docMapTerms = NLPUtils.getFrequencyTerm(lstSents, threshold);
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
		
		//calculating ST for each sentence
		for(int i = 0; i<sents.size(); i++){
			List<String> tokens = NLPUtils.getTokenList.apply(sents.get(i));
			List<String> words = tokens.stream()
					.filter(tk -> !NLPUtils.stopwords.contains(tk))
					.collect(Collectors.toList());
			double s_TFIDF = 0;
			for (String w : words){
				if (docKeys.isEmpty()){
					s_TFIDF = 0;
					break;
				}
				if (docKeys.contains(w)){
					int tf = docMapTerms.get(w);
					//System.out.println("TF:=" + tf);
					double idf = iDFSent(w, lstSupportingSents);
					//System.out.println("TF-IDF:=" + tf*idf);
					s_TFIDF += tf*idf;
				}
			}
			//System.out.println("STF-IDF:=" + s_TFIDF);
			s_TFIDF = s_TFIDF/words.size();
			if (Double.isNaN(s_TFIDF)) s_TFIDF = 0;
			ft[i] = s_TFIDF;
		}
			
		return ft;
	}
	
	private double iDFDoc(String w, List<Doc> lstSupportingDocs){
		double idf = 0;
		int N = lstSupportingDocs.size();
		int df = 0;
		for (Doc doc : lstSupportingDocs){
			List<List<String>> lstSents = new ArrayList<>();
			for(CoreMap s : doc.getSentences()){
				List<String> tokens = NLPUtils.getTokenList.apply(s);
				List<String> words = tokens.stream()
						.filter(tk -> !NLPUtils.stopwords.contains(tk))
						.collect(Collectors.toList());
				lstSents.add(words);
			}
			for (List<String> s : lstSents){
				if (s.contains(w)) df++;
				break;
			}
		}
		if (df==0) idf = 0;
		else idf = Math.log(N/df);
		//System.out.println("N:=" + N + "\t DF:=" + df + "\t IDF:=" + idf);
		return idf;
	}
	
	private double iDFSent(String w, List<List<String>> lstSupportingSents){
		double idf = 0;
		int N = lstSupportingSents.size();
		int df = 0;
		for (List<String> s : lstSupportingSents)
			if (s.contains(w)) df++;
		if (df==0) idf = 0;
		else idf = Math.log(N/df);
		//System.out.println("N:=" + N + "\t DF:=" + df + "\t IDF:=" + idf);
		return idf;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "STF_IDF";
	}

}
