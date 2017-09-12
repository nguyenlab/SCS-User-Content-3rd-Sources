package lab.docsum.crf.main;

import lab.docsum.crf.main.features.GoogleSearchFeatures;
import lab.docsum.crf.main.features.SingleFeatures;
import lab.docsum.crf.main.features.SocialFeatures;

public class FeatureProvider {
	public static final FeatureProvider inst = new FeatureProvider();

	SingleFeatures singleFeatures;
	SocialFeatures socialFeatures;
	GoogleSearchFeatures googleSearchFeatures;

	FeatureProvider() {
		singleFeatures=new SingleFeatures();
		socialFeatures=new SocialFeatures();
		googleSearchFeatures=new GoogleSearchFeatures();
	}

}
