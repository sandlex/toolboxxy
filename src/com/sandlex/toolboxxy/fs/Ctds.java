package com.sandlex.toolboxxy.fs;

import java.io.File;

public class Ctds extends FatherOfTools {

	private String secondDir;

	public Ctds(String sourceDir, String secondDir) {
		super(sourceDir);
		this.secondDir = secondDir;
	}

	@Override
	public void execute() {
		File srcDir = new File(sourceDir);
		File scndDir = new File(secondDir);

		checkSrcDirValidity(srcDir);
		checkSrcDirValidity(scndDir);

		File[] srcDirFiles = srcDir.listFiles();
		File[] scndDirFiles = scndDir.listFiles();
		
		if (srcDirFiles.length != scndDirFiles.length) {
			System.out.println("Compared folders have different structure");
			System.exit(1);
		}
		
		for (int i = 0; i < srcDirFiles.length; i++) {
			File sub1 = srcDirFiles[i];
			File sub2 = scndDirFiles[i];
			
			if (!sub1.getName().equals(sub2.getName())) {
				System.out.println("Compared folders have different structure");
				System.exit(1);
			}
			
			if (getDirSize(sub1) != getDirSize(sub2)) {
				System.out.println(String.format("[!] Subfolder %s has different size", sub1.getName()));
			}
		}
	}
	
	private long getDirSize(File dir) {
		long size = 0;
		
		if (dir.isFile()) {
			return dir.length();
		} else {
			File[] files = dir.listFiles();
			
			for (File file : files) {
				size += getDirSize(file);
			}
			return size;
		}
	}

}
