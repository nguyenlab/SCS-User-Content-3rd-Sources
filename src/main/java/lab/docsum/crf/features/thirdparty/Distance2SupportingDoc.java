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
import lab.document.crf.features.rte.Coefficient;
import lab.document.crf.features.rte.IDF;
import lab.document.crf.features.rte.LCS;
import lab.document.crf.features.rte.OtherFeature;

public class Distance2SupportingDoc implements Feature<double[], List<Doc>> {
	int threshold = 5;
	int feature_number = 5;
	
	public Distance2SupportingDoc(int threshold) {
		// TODO Auto-generated constructor stub
		this.threshold = threshold;
	}
	
	public Distance2SupportingDoc() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public double[] extract(List<Doc> input, Object... args) {
		// TODO Auto-generated method stub
		Doc doc = input.get(0);
		List<CoreMap> sents = doc.getSentences();
		double[] ft = new double[sents.size()];
		
		List<Doc> lstSuppportingDocs = new ArrayList<>();
		for (int i = 1; i<input.size(); i++)
			lstSuppportingDocs.add(input.get(i));
		
		List<List<String>> lstFrqWords = new ArrayList<>();
		for (Doc supportdoc : lstSuppportingDocs){
			List<String> frqWords = getFrqWord2List(doc, threshold);
			lstFrqWords.add(frqWords);
		}
		
		for(int i = 0; i<sents.size(); i++){
			double distance = 0;
			List<String> tokens = NLPUtils.getTokenList.apply(sents.get(i));
			List<String> words = tokens.stream()
					.filter(tk -> !NLPUtils.stopwords.contains(tk))
					.collect(Collectors.toList());
			
			for (List<String> frqword : lstFrqWords)
				//distance += manhattanDistance(words, frqword);
				distance += euclideanDistance(words, frqword);

			ft[i] = distance/lstSuppportingDocs.size();
		}		
		return ft;
	}
	
	private double euclideanDistance(List<String> words, Map<String, Integer> frqWords){
		double distance = 0;
		Set<String> keys = frqWords.keySet();
		List<String> lstKeys = new ArrayList<>();
		for(String key : keys) lstKeys.add(key);
		distance = OtherFeature.euclidean(words, lstKeys);	
		return distance;
	}
	
	private double euclideanDistance(List<String> words, List<String> frqWords){
		double distance = 0;
		distance = OtherFeature.euclidean(words, frqWords);	
		return distance;
	}
	
	private double manhattanDistance(List<String> words, List<String> frqWords){
		double distance = 0;
		distance = OtherFeature.manhatanDistance(words, frqWords);	
		return distance;
	}
	
	private Map<String, Integer> getFrqWord(Doc doc, int threshold){
		List<List<String>> lstSents = new ArrayList<>();
		for(CoreMap s : doc.getSentences()){
			List<String> tokens = NLPUtils.getTokenList.apply(s);
			List<String> words = tokens.stream()
					.filter(tk -> !NLPUtils.stopwords.contains(tk))
					.collect(Collectors.toList());
			lstSents.add(words);
		}
		Map<String, Integer> docMapTerms = NLPUtils.getFrequencyTerm(lstSents, threshold);
		return docMapTerms;
	}
		
	private List<String> getFrqWord2List(Doc doc, int threshold){
			List<List<String>> lstSents = new ArrayList<>();
			for(CoreMap s : doc.getSentences()){
				List<String> tokens = NLPUtils.getTokenList.apply(s);
				List<String> words = tokens.stream()
						.filter(tk -> !NLPUtils.stopwords.contains(tk))
						.collect(Collectors.toList());
				lstSents.add(words);
			}
		
		Map<String, Integer> docMapTerms = NLPUtils.getFrequencyTerm(lstSents, threshold);
		Set<String> keys = docMapTerms.keySet();
		List<String> lstfrqWords = new ArrayList<>();
		for (String key : keys) lstfrqWords.add(key);
		
		return lstfrqWords;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "Euclidean_Dist2SupportingDoc";
	}

	private double LexDistance2DocSim(List<String> s, List<List<List<String>>> docs){
		double lexdistance = 0;
		if (docs.isEmpty()) return 0;
		for (List<List<String>> doc : docs){
			for (List<String> h : doc){
				lexdistance += lexicalRTESim(s, h);
			}
		}
		return lexdistance/(docs.size());
	}
	
	private double lexicalRTESim(List<String> s, List<String> t){
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
