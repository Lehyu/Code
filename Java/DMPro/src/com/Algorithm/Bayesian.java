package com.Algorithm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Bayesian {
	private String RegExp1 = "(>=|<=|!=|==|>|<|=).*";
	private String RegExp2 = ">=|<=|!=|==|>|<|=";
	private HashMap<String, Integer> attriMap;
	private String[][] data;
	private HashMap<String, Integer> label;
	private int CIndex;
	
	public Bayesian(String[][] data, int label_index, boolean flag){
		init(data, flag);
		this.CIndex = label_index;
		setC();
	}
	
	public Bayesian(String[][] data, String label, boolean flag){
		init(data, flag);
		this.CIndex = attriMap.get(label);
		setC();
	}
	
	private void init(String[][] data, boolean flag){
		if(flag){
			attriMap = new HashMap<String, Integer>();
			this.data = new String[data.length - 1][data[0].length];
			for(int i = 0; i < data[0].length; i++)
				attriMap.put(data[0][i], i);
			for(int i = 1; i < data.length;i++){
				this.data[i-1] = data[i];
			}
		}else{
			attriMap = new HashMap<String, Integer>();
			this.data = new String[data.length][data[0].length];
			for(int i = 0; i < data.length;i++){
				this.data[i] = data[i];
			}
		}
	} 

	private void setC(){
		label = new HashMap<String, Integer>();
		for(int i = 0; i < data.length; i++){
			String cur = data[i][CIndex];
			Integer value = label.get(cur);
			if(value == null){
				value = 1;
			}else{
				value++;
			}
			label.put(cur, value);
		}
	}
	//set C [P(X|C)]
	public void setC(String attribute){
		label.clear();
		int index = attriMap.get(attribute);
		this.CIndex = index;
		setC();
	}
	public String predict(String condition, String which){
		double max = 0;
		String answer = null;
		Set<String> category = label.keySet();
		Iterator it = category.iterator();
		while(it.hasNext()){
			String curCate = (String) it.next();
			double p = PX_Ci(condition+"|"+which+"="+curCate);
			if(p > max){
				max = p;
				answer = curCate;
			}
		}
		return answer;
	}
	//return P(Ci)
	public double PCi(String selected){
		return (double)label.get(selected)/data.length;
	}
	//get the num of Ci
	private int getL(String selected){
		return label.get(selected);
	}
	/*  P(Xi|Ci) = |(Xi,Ci)|/|Ci|
	 *  |Ci| = label.get(Ci)
	 * */
	public double PXi_Ci(HashMap<Integer, String> condition){
		int numerator = selected(condition);
		int denominator = getL(condition.get(CIndex));
		return (double)numerator/denominator;
	}
	public double PX_Ci(String condition){
		String[] cons = condition.split("\\|");
		double ratio = 1;
		if(cons.length > 2){
			return 0;
		}else{
			String[] X = cons[0].split(",");
			String[] tmp = cons[1].split("=");
			String CKey = tmp[0].trim();
			String CValue = tmp[1].trim();
			for(int i = 0; i < X.length; i++){
				tmp = X[i].split(RegExp2);
				String key = tmp[0].trim();
				tmp = X[i].split(key);
				String value = tmp[tmp.length-1].trim();
				HashMap<Integer, String> xCon = new HashMap<Integer,String>();
				xCon.put(attriMap.get(key), value);
				xCon.put(attriMap.get(CKey), CValue);
//				System.out.println("In PX_Ci "+key+" "+value+" "+CKey+" "+CValue);
//				System.out.println("In PX_Ci "+attriMap.get(key)+" "+value+" "+attriMap.get(CKey)+" "+CValue);
				ratio *= PXi_Ci(xCon);
			}
			ratio *= PCi(CValue);
			return ratio;
		}
	}
	public int selected(HashMap<Integer, String> conditions) {
		int num = 0;
		Set<Integer> key = conditions.keySet();
		for (int i = 0; i < data.length; i++) {
			Iterator<Integer> it = key.iterator();
			boolean flag = true;
			while (it.hasNext()) {
				int j = it.next();
				String exp = conditions.get((Integer)j);
				String value;
				String oper;
				if(!exp.matches(RegExp1)){
					value = exp;
					oper = "==";
				}else{
					 String[] tmp = exp.split(RegExp2);
					 value = tmp[1].trim();
					 tmp = exp.split(value);
					 oper = tmp[0].trim();
				}
				if (!compare(data[i][j],oper,value)) {
					flag = false;
					break;
				}
			}
			if (flag)
				num++;
		}
		return num;
	}
	
	private boolean compare(String data, String oper, String value){
		int flag = data.compareTo(value);
		if(("==".equals(oper)|| "=".equals(oper) )&& flag == 0){
			return true;
		}else if(">=".equals(oper) && flag >= 0){
			return true;
		}else if("<=".equals(oper) && flag <= 0){
			return true;
		}else if("!=".equals(oper) && flag!=0){
			return true;
		}else if(">".equals(oper) && flag > 0){
			return true;
		}else if("<".equals(oper) && flag < 0){
			return true;
		}else{
			return false;
		}
		
	}
}
