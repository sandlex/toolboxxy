package com.sandlex.toolboxxy.fs;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class FatherOfTools {

	protected final SimpleDateFormat sdfDir = new SimpleDateFormat("yyyy_MM_dd");
	protected final SimpleDateFormat sdfFile = new SimpleDateFormat("yyyyMMdd");
	protected final String sep = System.getProperty("file.separator");
	protected final String sourceDir;
	
	public FatherOfTools(String sourceDir) {
		this.sourceDir = sourceDir;
	}
	
	public abstract void execute();
	
	protected void checkSrcDirValidity(File srcDir) {
		if (!srcDir.isDirectory()) {
			System.out.println(String.format("%s is not a directory", sourceDir));
			System.exit(1);
		}
	}
	
	protected File getDatedDirectory(File srcDir, long modifDate) {
		String destName = sdfDir.format(new Date(modifDate));
		File destDir = new File(srcDir + sep + destName);
		if (!destDir.exists()) {
			destDir.mkdir();
		}
		
		return destDir;
	}
	
	protected String getExtension(File file) {
		String extension = "";

		if (file.getName().indexOf(".") != -1) {
			extension = file.getName().substring(file.getName().lastIndexOf("."), file.getName().length());
		}
		
		return extension;
	}
	

}
