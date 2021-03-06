package com.tool;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.Algorithm.Bayesian;

public class FileIOTool {
	private BufferedReader stream;
    private String filename;

    public FileIOTool(String name){
        filename = name;
        try{
            stream = new BufferedReader(new FileReader(filename));
        }catch(Exception e){

        }
    }
    
    public ArrayList<String[]> readA() throws IOException{
    	ArrayList<String[]> data = new ArrayList<String[]>();
    	String line = stream.readLine();
    	
    	while(line != null){
    		System.out.println(line);
    		data.add(str2array(line));    	
    		line = stream.readLine();
    	}
    	return data;
    }
    
    private String[] str2array(String str){
    	String[] record = str.split("\\s+");
		for(int i = 0; i < record.length; i++)
			record[i] = record[i].trim();
		return record;
    }
    public String[][] read() throws IOException{
    	ArrayList<String[]> data = readA();
    	String[][] records = new String[data.size()][data.get(0).length];
    	for(int i = 0; i < records.length; i++){
    		records[i] = data.get(i);
    	}
    	return records;
    }
}
