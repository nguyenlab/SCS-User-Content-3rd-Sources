package lab.docsum.crf.main.features;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lab.docsum.crf.features.Feature;
import lab.docsum.crf.features.LabelFeature;
import lab.docsum.crf.features.basic.InTitleFeature;
import lab.docsum.crf.features.basic.IndicatorWordsFeature;
import lab.docsum.crf.features.basic.LengthFeature;
import lab.docsum.crf.features.basic.LogLikelihoodFeature;
import lab.docsum.crf.features.basic.NumStopWordFeature;
import lab.docsum.crf.features.basic.PositionFeature;
import lab.docsum.crf.features.basic.Similarity2NeighboringSentencesFeature;
import lab.docsum.crf.features.basic.ThematicWordsFeature;
import lab.docsum.crf.features.basic.UpperCaseWordsFeature;
import lab.docsum.crf.features.complex.HITSScoresFeature;
import lab.docsum.crf.features.complex.LSAScoresFeature;
import lab.docsum.crf.features.complex.LocalLDAScoreFeature;
import lab.docsum.crf.features.social.AuxLDAScoreFeature;

public class SingleFeatures extends FeatureSets {
	public SingleFeatures() {
		featureSets = new ArrayList<>();
		featureSets.add(createDocFeatureSet());
		featureSets.add(createCommentFeatureSet());
	}

	@SuppressWarnings("rawtypes")
	private List<Feature> createDocFeatureSet() {
		return Arrays.asList(new Feature[] { //
				new PositionFeature(), //
						new LengthFeature(), //
						new LogLikelihoodFeature(), //
						new ThematicWordsFeature(), //
						new IndicatorWordsFeature(), //
						new UpperCaseWordsFeature(), //
						new Similarity2NeighboringSentencesFeature(-3), //
						new Similarity2NeighboringSentencesFeature(-2), //
						new Similarity2NeighboringSentencesFeature(-1), //
						new Similarity2NeighboringSentencesFeature(1), //
						new Similarity2NeighboringSentencesFeature(2), //
						new Similarity2NeighboringSentencesFeature(3), //
						new InTitleFeature(), //
						new LSAScoresFeature(), //
						new HITSScoresFeature(), //
						new NumStopWordFeature(),//
						new LocalLDAScoreFeature(System.getProperty("lab.docsum.crf.main.features.SingleFeatures.doc.LocalLDAScoreFeature.ldaFolder")),//
						new AuxLDAScoreFeature(System.getProperty("lab.docsum.crf.main.features.SingleFeatures.doc.AuxLDAScoreFeature.ldaFolder")),//
						new LabelFeature(), //
				});
	}

	@SuppressWarnings("rawtypes")
	private List<Feature> createCommentFeatureSet() {
		return Arrays.asList(new Feature[] { //
				new PositionFeature(), //
						new LengthFeature(), //
						new LogLikelihoodFeature(), //
						new ThematicWordsFeature(), //
						new IndicatorWordsFeature(), //
						new UpperCaseWordsFeature(), //
						new Similarity2NeighboringSentencesFeature(-3), //
						new Similarity2NeighboringSentencesFeature(-2), //
						new Similarity2NeighboringSentencesFeature(-1), //
						new Similarity2NeighboringSentencesFeature(1), //
						new Similarity2NeighboringSentencesFeature(2), //
						new Similarity2NeighboringSentencesFeature(3), //
						new InTitleFeature(), //
						new LSAScoresFeature(), //
						new HITSScoresFeature(), //
						new NumStopWordFeature(),//
						new LocalLDAScoreFeature(System.getProperty("lab.docsum.crf.main.features.SingleFeatures.comment.LocalLDAScoreFeature.ldaFolder")),//
						new AuxLDAScoreFeature(System.getProperty("lab.docsum.crf.main.features.SingleFeatures.comment.AuxLDAScoreFeature.ldaFolder")),//
						new LabelFeature(), //
				});
	}
}
