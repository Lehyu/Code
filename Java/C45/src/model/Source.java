package model;

public class Source{
    private int attri_num;
    private int record_num;
    private String[][] data;
    public Source(int attri_num, int record_num){
    	this.attri_num = attri_num;
    	this.record_num = record_num;
    	data = new String[attri_num][record_num];
    }
    
    
}
