package com.alogrithm;

import java.text.DecimalFormat;

import com.model.Point;

public class DP {
    private static final int INFINITE = 10000;
    private int num;
    private Point[] vertex;
    private double[][] cost;
    private int[][] path;

    public DP(Point[] vertex){
        num = vertex.length;
        this.vertex = vertex;
        cost = new double[num][num];
        path = new int[num][num];
        
    }
    /* Solution one! According to teacher
     * cost[i][j] is the cost of {Vi-1,Vi,....,Vj}; 	
     * cost(i,j) = 0 if i=j;
     * cost(i,j) = min{cost(i,k)+cost(k+1,j)+w(Vi,Vk,Vj)}  if i < j;
     * */
    
    public void compute2(){
    	for(int i = 0; i < num; i++)
    		cost[i][i] = 0;
    	for(int r = 2; r < num; r++){
    		for(int i = 1; i < num -r+1; i++){
    			int j = i+r -1; 
    			double min = INFINITE;
    			path[i][j] = i;
    			for(int k = i; k < j; k++){
    				double t = cost[i][k] + cost[k+1][j]+weight(i-1, k,j);
    				if(t < min){
    					min = t;
    					path[i][j] = k;
    				}
    			}
    			cost[i][j] = min;
    			for(int k = 0; k < j; k++)
    				System.out.print("  ");
    			System.out.println(i+"  "+ j+ " "+cost[i][j]);
    		}
    	}
    }
    
    public double weight(int p1, int p2, int p3){
    	if(p2 == p3)
    		return length(p1,p2);
    	double p1p2 = length(p1,p2);
    	double p1p3 = length(p1,p3);
    	double p2p3 = length(p2,p3);
    	return p1p2+p2p3+p1p3;
    }

    /* Solution2! my own
     * cost(i,j) = 0  if i=j;
     * cost(i,j) = |ViVj| if j = i+1;
     * cost(i,j) = min{cost(i,k) + cost(k,j)}+|ViVj|  if j > i+1, i < k < j;
     */
    public void compute(){
    	for(int i = 0; i < num; i++){
            for(int j = i; j < num; j++){
                if(i == j){
                    cost[i][j] = 0;
                }else{
                	cost[i][j] = INFINITE;
                }
            }
        }
        for(int r = 2; r <= num; r++){
            for(int i = 0; i < num - r+1; i++){
                int j = i+r-1;
                double len = length(i, j);
                path[i][j] = i;
                if(j == i+1){
                    cost[i][j] = len;
                   
                }else{
                    for(int k = i+1; k < j; k++){
                        double t = cost[i][k] + cost[k][j]+len;
                        if(t < cost[i][j]){
                            cost[i][j] = t;
                            path[i][j]= k;
                        }
                    }
                }
             
            }
        }
    }
    public void Path(int v, int w){
    	if(v == w) return ;
    	Path(v, path[v][w]);
    	Path(path[v][w]+1, w);
    	System.out.println(v-1+" "+ path[v][w]+" "+w);
    }
    
    public void getPath(int v, int w){   	
     	if(v == w || w ==v+1) return;
    	getPath(v,path[v][w]);
    	getPath(path[v][w],w);
    	System.out.println(v+" "+ path[v][w]+" "+w);
    }
    public double getPerimeter(){
    	double perimeter = 0;
    	int start = 0;
    	for(int i = 1; i < num; i++){
    		int end = i;
    		perimeter += length(start, end);
    		start = end;
    	}
    	return perimeter;
    }
    
    public double getLength(int v, int w){
    	if(v >= 0 && w >= 0 && v < num && w < num)
    		return cost[v][w];	
    	else
    		return INFINITE;
    }

    public double length(int p1, int p2){
    	double len = Math.sqrt(Math.pow(vertex[p1].getX()-vertex[p2].getX(), 2) + Math.pow(vertex[p1].getY()-vertex[p2].getY(), 2));
        return Double.parseDouble(new DecimalFormat("#.000").format(len));
    }
}
