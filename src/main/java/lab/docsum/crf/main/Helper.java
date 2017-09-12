package lab.docsum.crf.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Helper {
	public static File[] listDocComFiles(File dir) throws IOException {

		Map<String, Boolean> seen = new ConcurrentHashMap<String, Boolean>();
		// return Files.walk(dir.toPath()).map(p -> p.toFile()).filter(f -> {
		// return seen.putIfAbsent(f.getName(), Boolean.TRUE) == null &&
		// f.isFile() && f.getName().endsWith(".txt");
		// }).toArray(File[]::new);

		List<File> fileList = new ArrayList<>();
		listDocComFiles(dir, fileList);
		return fileList.stream().filter(f->seen.putIfAbsent(f.getName(), Boolean.TRUE)==null).toArray(File[]::new);
	}

	public static void listDocComFiles(File dir, List<File> fileList) {
		for (File file : dir.listFiles()) {
			if (file.getName().contains(".DS_Store")) continue;
			if (file.isFile() && file.getName().endsWith(".txt")) {
				fileList.add(file);
			} else if(file.isDirectory()){
				listDocComFiles(file, fileList);
			}else{
				System.out.println("WARNING: not file nor folder: "+file.getAbsolutePath());
			}
		}
	}
	
	public static File[] listFiles(File dir) throws IOException {

		List<File> fileList = new ArrayList<>();
		listFiles(dir, fileList);
		return fileList.toArray(new File[0]);
	}

	public static void listFiles(File dir, List<File> fileList) {
		for (File file : dir.listFiles()) {
			if (file.isFile() && file.getName().endsWith(".txt")) {
				fileList.add(file);
			} else if(file.isDirectory()){
				listDocComFiles(file, fileList);
			}else{
				System.out.println("WARNING: not file nor folder: "+file.getAbsolutePath());
			}
		}
	}
}
