package lab.docsum.crf.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import lab.docsum.misc.Doc;

public class SubmissionFormatterL2R {

	static Random random = new Random(123);
	static int top = 6;
	static String selectOption;

	public static void main(String[] args) throws Exception {
		try {
			selectOption = args[1];
			top = Integer.valueOf(args[2]);
			System.out.println("SELECTION TYPE=" + selectOption
					+ " \tTHE NUMBER OF SELECTED INSTANCES=" + top);
		} catch (Exception e) {
			// TODO: handle exception
		}
		exec(args[0]);
		// exec("./data/yahoonews-10fold-tagged");
	}

	public static void exec(String dataDir) throws Exception {
		String outputDir = dataDir + "/outputs";
		String submissionDir = dataDir + "/submission";
		String goldDir = dataDir + "/gold";
		String fold2IdsMapFile = dataDir + "/ids-in-folds.txt";

		File[] fileList = Helper.listDocComFiles(new File(dataDir, "folds"));
		// System.out.println("THE TOTAL FILES IN ALL FOLDS....");
		System.out.println(Arrays.toString(fileList));

		Map<String, String> id2NameMap = Arrays
				.asList(fileList)
				.stream()
				.collect(
						Collectors
								.toMap(e -> e.getName().replaceAll(
										"^(\\d+).*$", "$1"), e -> e.getName()));

		// System.out.println("THE ID FILES IN ALL FOLDS");
		// System.out.println(id2NameMap);
		// System.out.println(id2NameMap.size());

		Map<String, List<String>> fold2IdsMap = new HashMap<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(
				fold2IdsMapFile));) {
			String line;
			while (null != (line = reader.readLine())) {
				String cols[] = line.split("[\\Q[], \\E]+");
				fold2IdsMap.put(cols[0],
						Arrays.asList(cols).subList(1, cols.length));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println("FILES IN FOLDS after MAPPING...");
		// System.out.println(fold2IdsMap.keySet());

		List<List<Doc>> docComList = DocComLoader.loadDocCom(fileList, false);
		Map<String, List<Doc>> id2DocComMap = docComList.stream().collect(
				Collectors.toMap(
						e -> e.get(0).getSource()
								.replaceAll("^(\\d+).*$", "$1"), e -> e));
		// System.out.println("THE ORIGINAL FILES...");
		// System.out.println(id2DocComMap.keySet());

		Map<String, String> tag2task = new HashMap<>();
		tag2task.put("D", "docSum");
		tag2task.put("C", "tweetSum");
		Map<String, String> tag2dir = new HashMap<>();
		tag2dir.put("D", "doc-sum");
		tag2dir.put("C", "comment-sum");

		Function<String, ?> formatByTagFunc = (tag) -> {
			File tagDir = new File(outputDir, tag);
			for (String fold : fold2IdsMap.keySet()) {
				List<String> ids = fold2IdsMap.get(fold);
				try (BufferedReader testPredReader = new BufferedReader(
						new FileReader(new File(tagDir, fold + ".test")));) {

					File outDir = new File(submissionDir, fold + "/"
							+ tag2dir.get(tag));
					outDir.mkdirs();

					// Map<String, Integer> id2taskNo = new HashMap<>();
					// ids.stream().sorted().forEach(e -> id2taskNo.put(e,
					// id2taskNo.size() + 1));

					// slow code
					Map<String, Integer> id2taskNo = Arrays
							.asList(new File(goldDir, fold + "/summary")
									.listFiles(f -> f.getName()
											.endsWith(".txt")))
							.stream()
							.map(e -> e.getName().split("\\D+"))
							.collect(
									Collectors.toMap(e -> String.valueOf(e[2]),
											e -> Integer.valueOf(e[1])));

					// System.out.println("THE IDS in SUMMARY...");
					// System.out.println(ids);

					for (String id : ids) {
						String name = id2NameMap.get(id);
						// System.out.println("NAME OF FILE..." + name);
						Object value = id2DocComMap.get(id);
						// System.out.println("ID2NAMEMAP..." + name
						// + " ID2DOCCOMMAP..." + value);
						if (value == null) {
							System.out.println("id2DocComMap err: " + fold
									+ "/" + id + "-" + name);
							throw new IllegalStateException("Null value...");
						}
						Iterator<String> texts = id2DocComMap.get(id).stream()
								.filter(e -> e.getTag().equals(tag))
								.findFirst().get().getSentences().stream()
								.map(e -> e.get(TextAnnotation.class))
								.iterator();

						try (Writer writer = new BufferedWriter(
								new OutputStreamWriter(new FileOutputStream(
										new File(outDir, "task"
												+ id2taskNo.get(id) + "_"
												+ tag2task.get(tag) + " "
												+ name))))) {
							List<String> toWrite = new ArrayList<>();

							List<Object[]> scoredLines = new ArrayList<>();

							for (String line = testPredReader.readLine(); line != null
									&& !(line = line.trim()).isEmpty(); line = testPredReader
									.readLine()) {

								if (texts.hasNext()) {
									//if ((Double.isNaN(Double.valueOf(line))) || (line.trim().contains("nan")))
										//line = "-2.0";
									//else{
									double v = 0;
									try{
										v = Double.valueOf(line);
									} catch(NumberFormatException ex){
										v = -10.0;
									}
									scoredLines.add(new Object[] {v,texts.next() });// modification
									//scoredLines.add(new Object[] {Double.valueOf(line),texts.next() });
								}
								if(!texts.hasNext()){
									break;
								}
							}

							scoredLines.sort((v1, v2) -> {
								return -Double.compare((Double) v1[0],
										(Double) v2[0]);
							});

							for (Object[] scoredLine : scoredLines.subList(0,
									Math.min(scoredLines.size(), top))) {
								toWrite.add((String) scoredLine[1]);
							}
							if (texts.hasNext()) {
								int rem = 0;
								while (texts.hasNext()) {
									rem++;
									texts.next();
								}

								throw new IllegalStateException(
										"id list vs. test files mismatched: "
												+ tag + "/" + fold + "/" + id
												+ "-" + name + " iter: "
												+ scoredLines.size() + " rem: "
												+ rem);
							}
							for (String line : filterOutput(toWrite)) {
								if (line.split(" ").length<5) continue; //add
								writer.append(line).append("\n");
							}

						} catch (Exception e2) {
							// TODO: handle exception
							e2.printStackTrace();
						}

					}

					testPredReader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			return null;
		};

		Function<String, Boolean> validateByTagFunc = (tag) -> {
			boolean valid=true;
			File tagDir = new File(outputDir, tag);
			for (String fold : fold2IdsMap.keySet()) {
				List<String> ids = fold2IdsMap.get(fold);

				List<String> predLines = IOUtils.linesFromFile(new File(tagDir,
						fold + ".test").getAbsolutePath()).stream().filter(line->!line.trim().isEmpty()).collect(Collectors.toList());
				
				int textCount = 0;
				for (String id : ids) {
					String name = id2NameMap.get(id);
					Object value = id2DocComMap.get(id);
					if (value == null) {
						System.out.println("id2DocComMap err: " + fold + "/"
								+ id + "-" + name);
						throw new IllegalStateException("Null value...");
					}
					List<String> texts = id2DocComMap.get(id).stream()
							.filter(e -> e.getTag().equals(tag)).findFirst()
							.get().getSentences().stream()
							.map(e -> e.get(TextAnnotation.class))
							.collect(Collectors.toList());
					textCount += texts.size();

				}
				if (textCount != predLines.size()) {
					System.out.println("Size Mismatched for " + fold
							+ " #predLines=" + predLines.size() + " #texts="
							+ textCount);
					valid=false;
				}

			}

			return valid;
		};
		
		boolean statusD = validateByTagFunc.apply("D");
		boolean statusC = validateByTagFunc.apply("C");
		
		if(!statusD || !statusC){
			System.out.println("SOME ERROR");
			
			return;
		}

		formatByTagFunc.apply("D");
		formatByTagFunc.apply("C");

	}

	static List<String> filterOutput(List<String> output) {
		if (selectOption == null || selectOption == "")
			return output;
		List<String> newOutput = new ArrayList<>(output);
		switch (selectOption) {
		case "random":
			Collections.shuffle(newOutput, random);
			break;
		case "shortest":
			Collections.sort(newOutput,
					(s1, s2) -> Integer.compare(s1.length(), s2.length()));
			break;
		case "longest":
			Collections.sort(newOutput,
					(s1, s2) -> -Integer.compare(s1.length(), s2.length()));
			break;
		case "top":
			// System.out.println("Selecting top sentences...");
			break;
		default:
			throw new IllegalStateException("Unkown Select Option: "
					+ selectOption);
		}

		return newOutput.subList(0, Math.min(newOutput.size(), top));

	}

}
