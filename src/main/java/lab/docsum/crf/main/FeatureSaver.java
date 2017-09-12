package lab.docsum.crf.main;

import java.io.File;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

import lab.docsum.misc.Object2Iterator;
import edu.stanford.nlp.io.IOUtils;

public class FeatureSaver {
	public static boolean saveFeature(File ftFile, Object ft) {

		try {
			File ftFileTmp = new File(ftFile.getAbsolutePath() + ".tmp");
			IOUtils.writeStringToFile(
					String.join("\n", Object2Iterator.stringIterable(ft))
							+ "\n", ftFileTmp.getAbsolutePath(), "utf-8");
			Files.move(ftFileTmp.toPath(), ftFile.toPath());
			return true;
		} catch (Exception e1) {
			Logger.getLogger(FeatureSaver.class.getName()).log(Level.SEVERE,
					e1.getMessage(), e1);
			return false;
		}
	}
}
