package lab.docsum.crf.main.features;

import java.util.List;

import lab.docsum.crf.features.Feature;

public abstract class FeatureSets {

	@SuppressWarnings("rawtypes")
	List<List<Feature>> featureSets;
	
	@SuppressWarnings("rawtypes")
	public List<List<Feature>> getFeatureSets() {
		return featureSets;
	}
}
