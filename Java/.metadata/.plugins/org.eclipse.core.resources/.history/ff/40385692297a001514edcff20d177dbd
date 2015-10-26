package com.entry;

import java.io.IOException;

import com.alogrithm.DP;
import com.tool.fileTool;

public class Triangulation {
	private static String filename = "/home/lhy/Code/Java/Triangulation/data/test_10.txt";
	public static void main(String[] args){
		fileTool tool = new fileTool(filename);
		try {
			DP dp = new DP(tool.readFile());
			dp.compute();
			System.out.println(dp.getLength(0, 9)*2 - dp.getPerimeter());
			dp.getPath(0, 9);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
