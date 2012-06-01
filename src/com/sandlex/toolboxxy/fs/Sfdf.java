package com.sandlex.toolboxxy.fs;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Sfdf extends FatherOfTools {

	public Sfdf(String sourceDir) {
		super(sourceDir);
	}

	@Override
	public void execute() {
		File srcDir = new File(sourceDir);
		
		checkSrcDirValidity(srcDir);

		List<File> files = Arrays.asList(srcDir.listFiles());
		for (File file : files) {
			if (file.isFile()) {
				
				List<File> nextFiles = files.subList(files.indexOf(file) + 1, files.size());
				for (File nextFile : nextFiles) {
					if (nextFile.isFile() && areTwoFilesEqual(file, nextFile)) {
						System.out.println(String.format("[!] %s = %s", file.getName(), nextFile.getName()));
					}
				}
			}
		}
	}
	
	private boolean areTwoFilesEqual(File file1, File file2) {
		return file2.length() == file1.length() && getExtension(file1).equals(getExtension(file2));
	}

}
