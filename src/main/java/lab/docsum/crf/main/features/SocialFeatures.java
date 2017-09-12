package lab.docsum.crf.main.features;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lab.docsum.crf.features.Feature;
import lab.docsum.crf.features.social.MaxCosine;
import lab.docsum.crf.features.social.MaxDistanceRTE;
import lab.docsum.crf.features.social.MaxLexicalRTE;
import lab.docsum.crf.features.social.MaxW2V;

public class SocialFeatures extends FeatureSets {
	public SocialFeatures() {
		featureSets = new ArrayList<>();
		featureSets.add(createSocialFeatureSet());
	}

	@SuppressWarnings("rawtypes")
	private List<Feature> createSocialFeatureSet() {
		return Arrays
				.asList(new Feature[] {//
				new MaxCosine(), //
						new MaxDistanceRTE(), //
						new MaxLexicalRTE(), //
						new MaxW2V(
								System.getProperty("lab.docsum.crf.main.features.SocialFeatures.MaxW2V.w2vPath")), //
				});
	}

}
