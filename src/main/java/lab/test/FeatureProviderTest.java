package lab.test;

import lab.docsum.crf.main.features.GoogleSearchFeatures;
import lab.docsum.crf.main.features.SingleFeatures;
import lab.docsum.crf.main.features.SocialFeatures;

public class FeatureProviderTest {
	public static final FeatureProviderTest inst = new FeatureProviderTest();

	SingleFeatures singleFeatures;
	SocialFeatures socialFeatures;
	GoogleSearchFeatures googleSearchFeatures;

	FeatureProviderTest() {
		singleFeatures=new SingleFeatures();
		socialFeatures=new SocialFeatures();
		googleSearchFeatures=new GoogleSearchFeatures();
	}

}
