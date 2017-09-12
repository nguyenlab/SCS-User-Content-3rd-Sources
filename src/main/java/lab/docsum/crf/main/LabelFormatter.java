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

import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import lab.docsum.misc.Doc;

public class LabelFormatter {

	// static Random random = new Random(123);
	// static int top = 4;
	// static String selectOption;

	public static void main(String[] args) throws Exception {
		// try {
		// selectOption = args[1];
		// top = Integer.valueOf(args[2]);
		// } catch (Exception e) {
		// // TODO: handle exception
		// }
		exec(args[0]);
		// exec("./data/yahoonews-10fold-tagged");
	}

	public static void exec(String dataDir) throws Exception {
		String outputDir = dataDir + "/outputs";
		String submissionDir = dataDir + "/predicts";
		String goldDir = dataDir + "/gold";
		String fold2IdsMapFile = dataDir + "/ids-in-folds.txt";

		File[] fileList = Helper.listDocComFiles(new File(dataDir, "folds"));
		System.out.println(Arrays.toString(fileList));

		Map<String, String> id2NameMap = Arrays
				.asList(fileList)
				.stream()
				.collect(
						Collectors
								.toMap(e -> e.getName().replaceAll(
										"^(\\d+).*$", "$1"), e -> e.getName()));

		System.out.println(id2NameMap);
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
		List<List<Doc>> docComList = DocComLoader.loadDocCom(fileList, false);
		Map<String, List<Doc>> id2DocComMap = docComList.stream().collect(
				Collectors.toMap(
						e -> e.get(0).getSource()
								.replaceAll("^(\\d+).*$", "$1"), e -> e));
		System.out.println(id2DocComMap.keySet());
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

					for (String id : ids) {
						String name = id2NameMap.get(id);
						Object value = id2DocComMap.get(id);
						if (value == null) {
							System.out.println("id2DocComMap err: " + fold
									+ "/" + id + "-" + name);
							continue;
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
							for (String line = testPredReader.readLine(); line != null
									&& !(line = line.trim()).isEmpty(); line = testPredReader
									.readLine()) {

								switch (line) {
								case "1": {
									if (!texts.hasNext()) {
										throw new IllegalStateException(
												"id list vs. test files mismatched: "
														+ fold + "/" + id + "-"
														+ name);
									}
									texts.next();
									toWrite.add(line);
								}
									break;
								case "0": {
									if (!texts.hasNext()) {
										throw new IllegalStateException(
												"id list vs. test files mismatched: "
														+ fold + "/" + id + "-"
														+ name);
									}
									texts.next();
									toWrite.add(line);
								}
									break;
								default:
									throw new IllegalStateException("label "
											+ line + " undefined. ");
								}

							}

							for (String line : filterOutput(toWrite)) {
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
		formatByTagFunc.apply("D");
		formatByTagFunc.apply("C");

	}

	static List<String> filterOutput(List<String> output) {
		return output;
		// if (selectOption == null || selectOption == "")
		// return output;
		// List<String> newOutput = new ArrayList<>(output);
		// switch (selectOption) {
		// case "random":
		// Collections.shuffle(newOutput, random);
		// break;
		// case "shortest":
		// Collections.sort(newOutput,
		// (s1, s2) -> Integer.compare(s1.length(), s2.length()));
		// break;
		// case "longest":
		// Collections.sort(newOutput,
		// (s1, s2) -> -Integer.compare(s1.length(), s2.length()));
		// break;
		// default:
		// throw new IllegalStateException("Unkown Select Option: "
		// + selectOption);
		// }
		//
		// return newOutput.subList(0, newOutput.size() < top ? newOutput.size()
		// : top);

	}

}
