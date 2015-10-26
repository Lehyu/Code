package model;

public class Attribute<V> {
	private String label;
	private int num;
	private V[] record;
	public Attribute(int num){
		this.num = num;
	}
	
	public String getLabel(){
		return label;
	}
	
	public V[] getRecord(){
		return record;
	}
	
	public int getNum(){
		return num;
	}

}
