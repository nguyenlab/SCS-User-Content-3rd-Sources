package lab.docsum.crf.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
import lab.docsum.crf.features.social.MaxCosine;
import lab.docsum.crf.features.social.MaxDistanceRTE;
import lab.docsum.crf.features.social.MaxLexicalRTE;
import lab.docsum.crf.features.social.MaxW2V;
import lab.docsum.crf.features.thirdparty.CosineVoting;
import lab.docsum.crf.features.thirdparty.A_P_KeyWord;
import lab.docsum.crf.features.thirdparty.Distance2SupportingDoc;
import lab.docsum.crf.features.thirdparty.P_KeyWord;
import lab.docsum.crf.features.thirdparty.R_P_KeyWord;
import lab.docsum.crf.features.thirdparty.STF_IDF;

public class FeatureFormatterL2R {
	static final Logger logger = Logger.getLogger(FeatureFormatterL2R.class
			.getName());

	public static void main(String[] args) throws Exception {

		formatL2R(args[0]);
		// formatCRFMallet("./data/yahoonews-10fold-tagged");
	}

	public static void formatL2R(String dataDir) throws Exception {

		@SuppressWarnings("rawtypes")
		final List<String> featureNames = Arrays.asList(new Feature[] { //
					new LabelFeature(),//
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
						new InTitleFeature(), // new
						new LSAScoresFeature(), //
						new HITSScoresFeature(), //
						new NumStopWordFeature(),// new
						new LocalLDAScoreFeature(""),// new
						new AuxLDAScoreFeature(""),// new
						new MaxCosine(), // new
						new MaxDistanceRTE(), // new
						new MaxLexicalRTE(), // new
						new MaxW2V(), // new*/
						new CosineVoting(0), // new CosineVoting score
						new Distance2SupportingDoc(10), // new Lexical distance
						new A_P_KeyWord(), // new Average Probability of
											// Supporting Document Keywords
						new P_KeyWord(), // new Probability of Supporting
											// Document Keywords
						new R_P_KeyWord(), // new Relative Probability of
											// Supporting Document Keywords
						new STF_IDF(), // new Social TF-IDF*/
				// for class
				}).stream().map(f -> f.name()).collect(Collectors.toList());

		logger.log(Level.INFO, "Features: \n{0}\n",
				String.join("\n", featureNames));

		String allFeatureDir = dataDir + "/features";
		String allFoldDir = dataDir + "/folds";
		String inputDir = dataDir + "/inputs";

		File[] fileList = Helper.listDocComFiles(new File(dataDir, "folds"));
		Map<String, String> id2NameMap = Arrays
				.asList(fileList)
				.stream()
				.collect(
						Collectors
								.toMap(e -> e.getName().replaceAll(
										"^(\\d+).*$", "$1"), e -> e.getName()));

		// System.out.println(id2NameMap);
		// System.out.println(id2NameMap.size());

		Map<String, List<String>> fold2IdsMap = new HashMap<>();
		try (BufferedWriter fold2IdsWriter = new BufferedWriter(new FileWriter(
				new File(dataDir, "ids-in-folds.txt")));) {
			for (File foldFile : new File(allFoldDir).listFiles()) {
				if (foldFile.getName().contains("DS_Store")) continue;
				List<String> ids = Arrays
						.asList(foldFile.listFiles(f -> f.getName().endsWith(
								".txt"))).stream()
						.map(f -> f.getName().replaceAll("^(\\d+).*$", "$1"))
						.collect(Collectors.toList());
				fold2IdsMap.put(foldFile.getName(), ids);
				fold2IdsWriter.append(foldFile.getName() + ids).append("\n");
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		Function<String, Object> ftWriteFunc = (tag) -> {
			File tagDir = new File(inputDir, tag);
			tagDir.mkdirs();
			
			for (String testfold : fold2IdsMap.keySet()) {
				int qid=0;
				try (BufferedWriter testWriter = new BufferedWriter(
						new FileWriter(new File(tagDir, testfold + ".test")));
						BufferedWriter trainWriter = new BufferedWriter(
								new FileWriter(new File(tagDir, testfold
										+ ".train")));) {
					BufferedWriter writer;
					for (String fold : fold2IdsMap.keySet()) {
						if (fold.equals(testfold)) {
							writer = testWriter;
						} else {
							writer = trainWriter;
						}

						List<String> ids = fold2IdsMap.get(fold);
						
						for (String id : ids) {
							qid++;
							String name = id2NameMap.get(id);
							List<BufferedReader> ftReaders = new ArrayList<>();
							for (String ftName : featureNames) {
								File ftFile = new File(new File(allFeatureDir,
										ftName), name + "." + tag);
								if (ftFile.exists()) {
									ftReaders.add(new BufferedReader(
											new FileReader(ftFile)));
								} else {
									System.out
											.println("Feature File Not Found "
													+ ftName + "/"
													+ ftFile.getName());
								}
							}

							List<String> ftRow;
							while (null != (ftRow = ftReaders
									.stream()
									.map(r -> {
										try {
											return r.readLine();
										} catch (Exception e) {
											logger.log(Level.SEVERE,
													e.getMessage(), e);
											return null;
										}
									}).collect(Collectors.toList()))) {
								if (ftRow.get(0) == null)
									break;
								writer.append(ftRow.get(0)) //write label
										.append(" qid:").append(String.valueOf(qid));//write qid
								
								//write features
								for(int i=1;i<ftRow.size();i++){
									writer.append(" ").append(String.valueOf(i)).append(":").append(ftRow.get(i));
								}
								writer.append("# doc_id="+id);
								writer.append("\n");
								//writer.append(String.join(" ", ftRow)).append(
								//		"\n");
							}

							ftReaders.forEach(r -> {
								try {
									r.close();
								} catch (Exception e) {
									e.printStackTrace();
								}
							});
							// empty line for next sequence 
							// no sequence but qid
							//writer.append("\n");
						}
					}
				} catch (Exception ex) {
					logger.log(Level.SEVERE, ex.getMessage(), ex);
				}
			}
			return null;
		};

		ftWriteFunc.apply("D");
		ftWriteFunc.apply("C");

	}

}
