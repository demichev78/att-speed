package com.company;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class Main {

	public static final String STD_FILENAME_PREFIX = "report-";
	public static final String STD_FILENAME_EXT = ".txt";
	public static final String DOWNLOAD_PREFIX = "Download:";
	public static final String SPEED_POSTFIX = "Mbit/s";
	public static final String UPLOAD_PREFIX = "Upload:";

	public static void main(String[] args) throws IOException {
		writeFile("result.txt", extractStats(listFiles("./speed-reports")));

	}

	private static File[] listFiles(String folderName) {
		if (folderName == null || folderName.length() == 0)
			throw new InvalidParameterException("Folder name cannot be empty");
		File folder = new File(folderName);
		return folder.listFiles();
	}

	private static List<String> extractStats(File[] fileList) {
		List<String> result = new ArrayList<>();
		if (fileList == null || fileList.length == 0) return result;
		for (File file : fileList) {
			if (file.isFile()) {
				String next = extractStats(file);
				if (next != null) {
					result.add(next);
				}
			}
		}
		return result;
	}

	private static String extractStats(File file) {
		String fileName = file.getName();
		if (fileName.length() == 0 || !fileName.contains(STD_FILENAME_PREFIX) || !fileName.endsWith(STD_FILENAME_EXT))
			return null;
		try {
			String downloadSpeed = "", uploadSpeed = "";
			String dateTime = fileName.substring(STD_FILENAME_PREFIX.length(), fileName.indexOf(STD_FILENAME_EXT));
			List<String> lines = Files.readAllLines(file.toPath(), Charset.defaultCharset());
			for (String nextLine : lines) {
				if (nextLine.contains(DOWNLOAD_PREFIX))
					downloadSpeed = nextLine.substring(DOWNLOAD_PREFIX.length(), nextLine.indexOf(SPEED_POSTFIX)).trim();
				if (nextLine.contains(UPLOAD_PREFIX))
					uploadSpeed = nextLine.substring(UPLOAD_PREFIX.length(), nextLine.indexOf(SPEED_POSTFIX)).trim();
			}
			return dateTime + "," + downloadSpeed + "," + uploadSpeed;
		} catch (IOException e) {
			System.out.println("Error reading " + fileName);
			e.printStackTrace();
			return null;
		}
	}

	private static void writeFile(String fileName, List<String> statsList) throws IOException {
		if (fileName == null || fileName.length() == 0)
			throw new InvalidParameterException("File name cannot be empty");
		if (statsList == null || statsList.size() == 0) {
			System.out.println("Statistics is empty, cannot write anything to " + fileName);
		}
		Files.write(Paths.get(fileName), (Iterable<? extends CharSequence>) statsList);
	}


}
