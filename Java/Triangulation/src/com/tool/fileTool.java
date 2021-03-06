package com.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import com.model.Point;

public class fileTool {
	private BufferedReader reader;
	//private InputStream stream;
    private String filename;

    public fileTool(String name){
        filename = name;
        try{
            //stream = new FileInputStream(new File(filename));
            reader = new BufferedReader(new FileReader(filename));
        }catch(Exception e){

        }
    }
    
    public Point[] readFile() throws IOException{
    	String str = reader.readLine();
    	str.split("n=");
    	int num = Integer.parseInt(str.replace("n=", "").trim());
    	Point[] verctex = new Point[num];
    	for(int i = 0; i < num; i++){
    		str = reader.readLine();
    		String[] location = str.split(",");
    		double x = Double.parseDouble(location[0].replace("(","").trim());
    		double y = Double.parseDouble(location[1].replace(")","").trim());
    		verctex[i] = new Point(x,y);
    		//System.out.println(x+","+y);
    	}
		return verctex;
    	
    }
}
