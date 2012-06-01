package com.sandlex.toolboxxy.fs;

import java.io.File;

public class Mfts extends FatherOfTools {

	public Mfts(String sourceDir) {
		super(sourceDir);
	}

	@Override
	public void execute() {
		File srcDir = new File(sourceDir);
		
		checkSrcDirValidity(srcDir);

		File[] files = srcDir.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				File destDir = getDatedDirectory(srcDir, file.lastModified());
				
				File movedFile = new File(destDir + sep + file.getName());
				boolean status = file.renameTo(movedFile);
				System.out.println(String.format("[%s] %s -> %s", status ? "OK"
						: "FAILED", file.getName(), destDir.getAbsolutePath()));
			}
		}
	}

}
