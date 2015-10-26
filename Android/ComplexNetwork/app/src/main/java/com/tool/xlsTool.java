package com.tool;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * Created by lhy on 10/22/15.
 */
public class xlsTool {
    private static xlsTool tool;
    private Workbook readtool = null;
    /*private xlsTool() {
        tool = new xlsTool();
    }

    public static xlsTool getTool() {
        return tool;
    }*/
    public xlsTool(InputStream strem) throws IOException, BiffException {
        readtool = Workbook.getWorkbook(strem);
    }

    private void init(String filename) throws IOException, BiffException {
        InputStream stream = new FileInputStream(filename);
        readtool = Workbook.getWorkbook(stream);
    }
    public xlsTool(String filename) throws IOException, BiffException {
        init(filename);
    }
    public String[][] readSheet(int which){
        Sheet sheet = readtool.getSheet(which);
        int col = sheet.getColumns();
        int row = sheet.getColumns();
        String data[][] = new String[col][row];
        for(int i = 0; i < col; i++){
            for(int j = 0; j < row; j++){
                Cell cell = sheet.getCell(i,j);
                data[i][j] = cell.getContents();
            }
        }
        return data;
    }

}
