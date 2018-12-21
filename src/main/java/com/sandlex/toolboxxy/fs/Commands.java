package com.sandlex.toolboxxy.fs;

enum Commands {
	
	mfts("[move files to subfolders] Moves all files from specified directory to subfolders named yyyy_MM_dd in accordance with file modification date"),
	rfbd("[rename files by date] Renames all files from specified directory to yyyyMMdd[i].[ext], where [i] is an autoincremented index, [ext] - original extension"),
	sfdf("[search for duplicated files] Finds duplicated files having different names but the same size, extension and optionally modification date"),
	ctdc("[compare two directories content] Compares content of two directories printing out all subfolders that present in one directory and absent in another and vice versa"),
	ctds("[compare two directories size] Runs through all subfolders of two directories with the same content and finds all subfolders with the same name but different size/content");
	
	private final String description;
	
	Commands(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return new StringBuilder(super.toString()).append(" - ").append(description).toString();
	}
	
}
