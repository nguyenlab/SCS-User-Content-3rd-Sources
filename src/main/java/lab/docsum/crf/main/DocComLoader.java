package lab.docsum.crf.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import lab.docsum.misc.Doc;
import lab.docsum.misc.NLPUtils;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.pipeline.Annotation;

public class DocComLoader {
	static final Logger logger = Logger.getLogger(DocComLoader.class.getName());

	static List<List<Doc>> loadDocComFast(File[] files) {
		final List<List<Doc>> docComs = new ArrayList<>();
		Arrays.asList(files).parallelStream().forEach((file) -> {
			List<Doc> docCom = loadDocCom(file, true);
			synchronized (docComs) {
				docComs.add(docCom);
				if (docComs.size() % 10 == 0) {
					logger.log(Level.INFO, "DocCom: {0}", docComs.size());
				}
			}
		});

		logger.log(Level.INFO, "Pre-processing data done. Jobs done: {0}",
				docComs.size());
		logger.log(Level.INFO, "====================================================");
		return docComs;
	}

	static List<List<Doc>> loadDocCom(File[] files) {
		return loadDocCom(files, true);

	}

	static List<List<Doc>> loadDocCom(File[] files, boolean preprocessed) {
		if (preprocessed) {
			return loadDocComFast(files);
		}
		final List<List<Doc>> docComs = new ArrayList<>();
		for (File file : files) {
			List<Doc> docCom = loadDocCom(file, false);
			docComs.add(docCom);
		}
		return docComs;
	}

	static List<Doc> loadDocCom(File file, boolean preprocessed) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(file), "utf-8"))) {
			//logger.log(Level.INFO, "Pre-processing file...",
				//	file.getName());
			
			Doc doc = new Doc();
			Doc com = new Doc();
			String title = null;
			doc.setTag("D");
			doc.setSource(file.getName());
			com.setTag("C");
			com.setSource(file.getName());

			for (String line = reader.readLine(); line != null; line = reader
					.readLine()) {
				line = line.trim();
				if (title == null) {
					if (!line.isEmpty()) {
						title = line;
						doc.setTitle(title);
						com.setTitle(title);
					}
				} else if (line.startsWith("<D>") && line.endsWith("</D>")) {
					line = line.substring(3, line.length() - 4);
					String[] cols = line.split("\t");
					int textspan;
					String label = null;
					if (cols.length > 1) {

						String lastCol = cols[cols.length - 1];
						if (lastCol.matches("[01]")) {
							label = lastCol;
						}
					}

					if (label == null) {
						doc.getLabels().add("0");
						textspan = cols.length;
					} else {
						doc.getLabels().add(label);
						textspan = cols.length - 1;
					}
					Annotation sent = new Annotation(String.join(" ", Arrays
							.asList(cols).subList(0, textspan)));
					if (preprocessed)
						NLPUtils.instance.annotate(sent);
					doc.getSentences().add(sent);
				} else if (line.startsWith("<C>") && line.endsWith("</C>")) {
					line = line.substring(3, line.length() - 4);
					String[] cols = line.split("\t");
					int textspan;
					String label = null;
					if (cols.length > 1) {

						String lastCol = cols[cols.length - 1];
						if (lastCol.matches("[01]")) {
							label = lastCol;
						}
					}

					if (label == null) {
						com.getLabels().add("0");
						textspan = cols.length;
					} else {
						com.getLabels().add(label);
						textspan = cols.length - 1;
					}

					Annotation sent = new Annotation(String.join(" ", Arrays
							.asList(cols).subList(0, textspan)));
					if (preprocessed)
						NLPUtils.instance.annotate(sent);
					com.getSentences().add(sent);
				}
			}
			return new ArrayList<>(Arrays.asList(doc, com));

		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	public static List<List<Doc>> getDocComList(String dataDir)
			throws IOException {
		List<List<Doc>> docComList = null;
		logger.log(Level.INFO, "Pre-processing data for main documents");
		File serFile = new File(dataDir + "/prepocessed-docs.ser.bin.gz");
		if (serFile.exists()) {
			logger.log(Level.INFO, "Loading existing data of original documents...");
			try {
				docComList = IOUtils.readObjectFromFile(serFile);
			} catch (IOException | ClassNotFoundException e1) {
				logger.log(Level.SEVERE, e1.getMessage(), e1);
			}
		}

		if (docComList == null) {
			File[] fileList = Helper.listDocComFiles(new File(dataDir, "folds"));
			System.out.println("The number of files in folds..." + fileList.length);
			System.out.println("=====================================");
			System.out.println("Loading comment files....");
			docComList = loadDocCom(fileList);

			try {
				IOUtils.writeObjectToFile(docComList, serFile);
				logger.log(Level.INFO, "Saved pre-processed data");
			} catch (IOException e1) {
				logger.log(Level.SEVERE, e1.getMessage(), e1);
			}
		}
		return docComList;
	}
}
