package lab.docsum.crf.main.features;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lab.docsum.crf.features.Feature;
import lab.docsum.crf.features.thirdparty.CosineVoting;
import lab.docsum.crf.features.thirdparty.Distance2SupportingDoc;
import lab.docsum.crf.features.thirdparty.A_P_KeyWord;
import lab.docsum.crf.features.thirdparty.P_KeyWord;
import lab.docsum.crf.features.thirdparty.R_P_KeyWord;
import lab.docsum.crf.features.thirdparty.STF_IDF;

public class GoogleSearchFeatures extends FeatureSets {
	public GoogleSearchFeatures() {
		featureSets = new ArrayList<>();
		featureSets.add(createDocFeatureSet());
		featureSets.add(createCommentFeatureSet());
	}

	@SuppressWarnings("rawtypes")
	private List<Feature> createDocFeatureSet() {
		return Arrays.asList(new Feature[] {//
				new CosineVoting(0.65), // new Cosine voting score
				//new Distance2SupportingDoc(), // Lexical distance of a sentence with supporting documents
				new A_P_KeyWord(5), // Average Probability of Supporting Document Keywords
				new P_KeyWord(5), // Probability of Supporting Document Keywords
				new R_P_KeyWord(5), // Relative Probability of Supporting Document Keywords
				new STF_IDF(3), // Social TF-IDF
				new Distance2SupportingDoc(10), //average euclidean distance to supporting documents
				});
	}

	@SuppressWarnings("rawtypes")
	private List<Feature> createCommentFeatureSet() {
		return Arrays.asList(new Feature[] {//
				new CosineVoting(0.35), // Cosine voting score
				//new Distance2SupportingDoc(),// Lexical distance of a sentence with supporting documents
				new A_P_KeyWord(5), // Average Probability of Supporting Document Keywords
				new P_KeyWord(5), // Probability of Supporting Document Keywords
				new R_P_KeyWord(5), // Relative Probability of Supporting Document Keywords
				new STF_IDF(6), // Social TF-IDF
				new Distance2SupportingDoc(10), //average euclidean distance to supporting documents
				});
	}
}
