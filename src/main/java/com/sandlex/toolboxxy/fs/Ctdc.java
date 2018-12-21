package com.sandlex.toolboxxy.fs;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

public class Ctdc extends FatherOfTools {

	private String secondDir;
	Map<String, String> occurrences = new TreeMap<String, String>();

	public Ctdc(String sourceDir, String secondDir) {
		super(sourceDir);
		this.secondDir = secondDir;
	}

	@Override
	public void execute() {
		File srcDir = new File(sourceDir);
		File scndDir = new File(secondDir);

		checkSrcDirValidity(srcDir);
		checkSrcDirValidity(scndDir);

		runThroughDir(srcDir);
		runThroughDir(scndDir);
		
		printResults(srcDir);
		printResults(scndDir);

	}

	private void runThroughDir(File dir) {
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				if (occurrences.containsKey(file.getName())) {
					occurrences.remove(file.getName());
				} else {
					occurrences.put(file.getName(), dir.getAbsolutePath());
				}
			}
		}
	}

	private void printResults(File dir) {
		System.out.println("New in " + dir.getAbsolutePath() + ":");
		for (Map.Entry<String, String> entry : occurrences.entrySet()) {
			if (entry.getValue().equals(dir.getAbsolutePath())) {
				System.out.println(entry.getKey());
			}
		}
	}

}
