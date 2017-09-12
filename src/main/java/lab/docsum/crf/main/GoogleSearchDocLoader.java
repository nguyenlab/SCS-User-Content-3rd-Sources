package lab.docsum.crf.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.pipeline.Annotation;
import lab.docsum.misc.Doc;
import lab.docsum.misc.NLPUtils;

public class GoogleSearchDocLoader {
	static final Logger logger = Logger.getLogger(GoogleSearchDocLoader.class
			.getName());

	public static List<Doc> getGoogleSearchDocs(String dataDir,
			String mainDocName) {
		
		System.out.println("Preprocessing main doc..." + dataDir + "\t" + mainDocName);
		List<Doc> docList = null;
		File[] fileList = new File(dataDir + "/google-search-docs/"
				+ mainDocName).listFiles(f -> f.getName().endsWith(".txt"));
		System.out.println("The number files of main doc.." +  mainDocName + "\t" + fileList.length);
		
		File serFile = new File(dataDir + "/google-search-docs.preprocessed",
				mainDocName + ".ser.bin.gz");
		if (serFile.exists()) {
			//System.out.println("Existing file..." + serFile);
			try {
				docList = IOUtils.readObjectFromFile(serFile);
				System.out.println("Reading from Existing file..." + serFile);
				System.out.println("DOC LIST SIZE..." + docList.size());
			} catch (IOException | ClassNotFoundException e1) {
				System.out.println("Error in file " + serFile.getName());
				logger.log(Level.SEVERE, e1.getMessage(), e1);
				//logger.log(Level.SEVERE, "Error in file ", serFile.getName());
			}
		}
		if (docList == null) {
			System.out.println("Tokenizing data...");
			docList = loadDoc(fileList);

			docList.sort((d1, d2) -> {
				String s1 = d1.getSource();
				String s2 = d2.getSource();
				int i1 = Integer.valueOf(s1.replace(".txt", ""));
				int i2 = Integer.valueOf(s2.replace(".txt", ""));
				return Integer.compare(i1, i2);
			});
			System.out.println("passing docList sort...");
			try {
				serFile.getParentFile().mkdirs();
				System.out.println("getting parent folder...and writing data into google-search-docs.preprocessed");
				IOUtils.writeObjectToFile(docList, serFile);
				logger.log(Level.INFO, "Processing Google data,...Saved pre-processed data");
			} catch (IOException e1) {
				logger.log(Level.SEVERE, e1.getMessage(), e1);
			}
		}
		return docList;
	}

	public static Map<String, List<Doc>> getGoogleSearchDocs(String dataDir)
			throws IOException {

		Map<String, List<Doc>> docMap = null;
		logger.log(Level.INFO, "Pre-processing data");
		File serFile = new File(dataDir + "/google-search-docs.ser.bin.gz");
		if (serFile.exists()) {
			logger.log(Level.INFO, "Loading existing data from supporting documents...");
			try {
				docMap = IOUtils.readObjectFromFile(serFile);
			} catch (IOException | ClassNotFoundException e1) {
				logger.log(Level.SEVERE, e1.getMessage(), e1);
			}
		}

		if (docMap == null) {
			File[] fileList = Helper.listFiles(new File(dataDir,
					"google-search-docs"));
			logger.log(Level.INFO, "Found " + fileList.length + " files.");
			List<Doc> docList = loadDoc(fileList);
			docMap = new HashMap<>();// mapping
			for (int i = 0; i < fileList.length; i++) {
				File file = fileList[i];
				Doc doc = docList.get(i);
				assert file.getName() == doc.getSource();
				String parentName = file.getParentFile().getName();
				List<Doc> docs = docMap.get(parentName);
				if (docs == null) {
					docMap.put(parentName, docs = new ArrayList<>());
				}
				docs.add(doc);
			}

			for (List<Doc> docs : docMap.values()) {
				docs.sort((d1, d2) -> {
					String s1 = d1.getSource();
					String s2 = d2.getSource();
					int i1 = Integer.valueOf(s1.replace(".txt", ""));
					int i2 = Integer.valueOf(s2.replace(".txt", ""));
					return Integer.compare(i1, i2);

				});
			}
			try {
				IOUtils.writeObjectToFile(docMap, serFile);
				logger.log(Level.INFO, "Saved pre-processed data");
			} catch (IOException e1) {
				logger.log(Level.SEVERE, e1.getMessage(), e1);
			}
		}

		return docMap;
	}

	static List<Doc> loadDocFast(File[] files) {
		List<Doc> docs = new ArrayList<>();
		Arrays.asList(files).parallelStream().forEach((file) -> {
			Doc doc = loadDocFile(file, true);
			synchronized (docs) {
				docs.add(doc);
				if (docs.size() % 10 == 0) {
					logger.log(Level.INFO, "doc: {0}", docs.size());
				}
			}
		});

		logger.log(Level.INFO, "Pre-processing data done. Jobs done: {0}", docs.size());
		return docs;
	}

	//loading file fast or slow
	static List<Doc> loadDoc(File[] files) {
		return loadDocListFile(files, true);
	}

	static List<Doc> loadDocListFile(File[] files, boolean preprocessed) {
		if (preprocessed) {
			return loadDocFast(files);
		}
		final List<Doc> docs = new ArrayList<>();
		for (File file : files) {
			System.out.println("Processing file..." + file.getAbsolutePath());
			if (file.getName().contains("DS.Store")) continue;
			//a flag that indicates using annotation or not
			Doc doc = loadDocFile(file, false);
			docs.add(doc);
		}
		return docs;
	}

	static Doc loadDocFile(File file, boolean preprocessed) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(file), "utf-8"))) {
			//System.out.println("Processing file..." + file.getName());
			Doc doc = new Doc();
			doc.setSource(file.getName());
			doc.setTag("G");
			String title = reader.readLine();
			if(title==null){
				title="";
			}
			title = title.trim();
			doc.setTitle(title);

			for (String line = reader.readLine(); line != null; line = reader
					.readLine()) {
				line = line.trim();
				if (line.isEmpty())
					continue;
				if (line.split("\\s+").length < 4) {
					continue;
				}

				Annotation sent = new Annotation(line);
				if (preprocessed) {
					//System.out.println("File..." + file.getAbsolutePath() + ":: processing sentence..." + sent);
					NLPUtils.instance.annotate(sent);
				}

				doc.getSentences().add(sent);
			}
			return doc;

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: Processing file..." + file.getAbsolutePath());
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: Processing file..." + file.getAbsolutePath());
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: Processing file..." + file.getAbsolutePath());
			e.printStackTrace();
		}
		//System.out.println(file.getAbsolutePath());
		return null;
	}
}
