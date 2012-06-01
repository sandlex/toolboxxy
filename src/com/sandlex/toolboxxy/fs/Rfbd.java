package com.sandlex.toolboxxy.fs;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Rfbd extends FatherOfTools {

	private Map<String, Integer> occurrences = new HashMap<String, Integer>();
	
	public Rfbd(String sourceDir) {
		super(sourceDir);
	}

	@Override
	public void execute() {
		File srcDir = new File(sourceDir);
		
		checkSrcDirValidity(srcDir);

		File[] files = srcDir.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				String datedName = sdfFile.format(new Date(file.lastModified()));
				int index = 1;
				if (occurrences.containsKey(datedName)) {
					index = occurrences.get(datedName);
				} 
				occurrences.put(datedName, index + 1);
				
				File renamedFile = new File(srcDir + sep + datedName + index + getExtension(file));
				boolean status = file.renameTo(renamedFile);
				System.out.println(String.format("[%s] %s -> %s", status ? "OK"
						: "FAILED", file.getName(), renamedFile.getName()));
			}
		}
	}

}
