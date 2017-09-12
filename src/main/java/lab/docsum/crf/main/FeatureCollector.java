package lab.docsum.crf.main;

import static lab.docsum.crf.main.DocComLoader.getDocComList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import lab.docsum.crf.features.Feature;
import lab.docsum.misc.Doc;

public class FeatureCollector {
	// static int nThreads = 40;
	static final Logger logger = Logger.getLogger(FeatureCollector.class
			.getName());

	public static void main(String[] args) throws Exception {
		exec(args[0]);
		// exec("./data/yahoonews-10fold-tagged");
	}

	/**
	 * feature output format: $ROOT/features/$FEATURE_NAME/$DOC_NAME
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void exec(String dataDir) throws Exception {

		
		List<List<Doc>> docComList = getDocComList(dataDir);
		
		//Map<String, List<Doc>> googleSearchDocs = GoogleSearchDocLoader
				//.getGoogleSearchDocs(dataDir);

		logger.log(Level.INFO, "Extracting features");
		final File allFeatureDir = new File(dataDir, "features");
		allFeatureDir.mkdirs();

		final int[] counter = { 0 };
		docComList
				.parallelStream()
				.forEach(docCom -> {
					// single feature
						for (int i = 0; i < docCom.size(); i++) {
							// i=0 : doc, i=1 : comment
							Doc doc = docCom.get(i);
							String docId = doc.getSource() + "." + doc.getTag();
							List<Feature> singleFeatures = FeatureProvider.inst.singleFeatures
									.getFeatureSets().get(i);

							for (Feature feature : singleFeatures) {
								File ftDir = new File(allFeatureDir, feature
										.name());
								ftDir.mkdirs();
								File ftFile = new File(ftDir, docId);
								if (ftFile.exists()) {
									continue;
								}

								Object ft = null;

								try {
									ft = feature.extract(doc);
								} catch (Exception ex) {
									logger.log(Level.SEVERE, ex.getMessage(),
											ex);
								}

								FeatureSaver.saveFeature(ftFile, ft);
							}
						}

						// cross features

						for (Feature feature : FeatureProvider.inst.socialFeatures
								.getFeatureSets().get(0)) {
							File ftDir = new File(allFeatureDir, feature.name());
							ftDir.mkdirs();

							if (docCom
									.stream()
									.mapToInt(
											d -> new File(ftDir, d.getSource()
													+ "." + d.getTag())
													.exists() ? 1 : 0).sum() == docCom
									.size())
								continue;

							List<?> fts = (List<?>) feature.extract(docCom);
							for (int i = 0; i < docCom.size(); i++) {
								Doc doc = docCom.get(i);
								String docId = doc.getSource() + "."
										+ doc.getTag();
								Object ft = fts.get(i);

								File ftFile = new File(ftDir, docId);

								FeatureSaver.saveFeature(ftFile, ft);
							}
						} 

						// google search features
						System.out.println("Extracting features for supporting documents");
						for (int i = 0; i < docCom.size(); i++) {
							System.out.println("The number of supporting docs..." + docCom.size());
							Doc doc = docCom.get(i);
							String docId = doc.getSource() + "." + doc.getTag();
							List<Feature> singleFeatures = FeatureProvider.inst.googleSearchFeatures
									.getFeatureSets().get(i);
							for (Feature feature : singleFeatures) {
								File ftDir = new File(allFeatureDir, feature.name());
								System.out.println("Feature name:=" + ftDir.getAbsolutePath());
								ftDir.mkdirs();
								File ftFile = new File(ftDir, docId);
								if (ftFile.exists()) {
									continue;
								}

								Object ft = null;
								
								//logger.log(Level.SEVERE, "BEFOR RUNNING GOOGLE FEATURES.." + dataDir + "\t" + doc.getSource());
								List<Doc> supportingDoc = GoogleSearchDocLoader.getGoogleSearchDocs(dataDir, doc.getSource());
								List<Doc> input = new ArrayList<>();
								input.add(doc);
								input.addAll(supportingDoc);

								try {
									ft = feature.extract(input);
								} catch (Exception ex) {
									logger.log(Level.SEVERE, ex.getMessage(),
											ex);
								}

								FeatureSaver.saveFeature(ftFile, ft);
							}
						}

						synchronized (counter) {
							counter[0]++;
							if (counter[0] % 10 == 0) {
								logger.log(Level.INFO, "Doc+Com: {0}",
										counter[0]);
							}
						}
					});

		logger.log(Level.INFO, "Feature Extraction Completed. Jobs done: {0}",
				counter[0]);
		logger.log(Level.INFO, "All Done.");
	}

}
