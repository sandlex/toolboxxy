package com.sandlex.toolboxxy.java;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.TreeMap;

public class Cp1251UtfConverter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Map<String, String> map = new TreeMap<String, String>();
		
		BufferedReader inputStream = null;
		BufferedWriter writer = null;
		
        try {
                inputStream = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "cp1251"));
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1], true), "utf8"));

                String str;
                while ((str = inputStream.readLine()) != null) {
                	String[] line = str.split("\\t");
                	String key = line[0];
                	String value = line[1];
                	
                	if (map.containsKey(key)) {
                		String currentValue = map.get(key) + "," + value;
                		map.put(key, currentValue);
                	} else {
                		map.put(key, value);
                	}
                }
                
                for (String key : map.keySet()) {
                    writer.append(key + "\t" + map.get(key));
                    writer.newLine();
                }
                
        } catch (FileNotFoundException ex) {
                ex.printStackTrace();
        } catch (IOException ex) {
                ex.printStackTrace();
        } finally {
                if (inputStream != null) {
                        try {
                                inputStream.close();
                                writer.close();
                        } catch (IOException ex) {
                                ex.printStackTrace();
                        }
                }
        }	
	}

}
