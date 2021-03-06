package entry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import io.xlsTool;
import jxl.read.biff.BiffException;
import model.Graph;;

public class Entry {
	public static void main(String[] args){
		String filename = "/home/lhy/Book/ComplexNetwork/data.xls";
		try {
			xlsTool tool = new xlsTool(filename);
			String[][] data = tool.readSheet(1);
			/*for(int i = 0; i < data.length; i++){
				for(int j = 0; j < data[i].length; j++){
					System.out.print(data[i][j]+"  ");
				}
				System.out.println();
			}*/
			Graph graph = new Graph(data);
			System.out.println(graph.getAvgCoef());
			System.out.println(graph.avgShortestPathLength());
			System.out.println(graph.getCoreness());
		} catch (BiffException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
