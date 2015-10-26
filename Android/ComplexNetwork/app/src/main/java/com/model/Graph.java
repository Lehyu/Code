package com.model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by lhy on 10/22/15.
 */
public class Graph implements Serializable {
    private static final int INFINITE = 200;
    private final int V;
    private int E;
    private int [][] DLen;
    private double avgLength;
    private double[] delta;
    private double[] Coef;
    private double avgCoef;
    private int Coreness;
    private String title;
    private ArrayList<Bag<Integer>> adj;

    /*init Graph with data from the xls
    * */
    public Graph(String[][] data){
        this.V = data.length;
        init(data);
    }

    /* If the data[i][j] = "y", it means there is a edge between i and j.
     *
    * */
    private void init(String [][] data){
        E = 0;
        DLen = new int[V][V];
        avgCoef = 0;
        avgLength = 0;
        Coef = new double[V];
        Coreness = 0;
        adj = new ArrayList<Bag<Integer>>();
        for(int i = 0; i < V; i++)
            adj.add(new Bag<Integer>(i));

        for(int i = 0; i < V; i++){
            for(int j = 0; j < V; j++) {
                if ("y".equals(data[i][j])) {
                    addEdg(i,j);
                    DLen[i][j] = 1;
                }else if("m".equals(data[i][j])){
                    DLen[i][j] = 0;
                }else{
                    DLen[i][j] = INFINITE;
                }
            }
        }

        floyd();
        computeDist();
        coefOfEachNode();
        ComputeAvgLength();
        //ComputeCoreness();
        //Log.v("Graph", String.valueOf(adj.size()));
    }

    /*---------------------------------------public tool----------------------------------------*/

    public double[] getDelta(){return delta;}
    public int getCoreness(){
        return Coreness;
    }

    public double getCoef(){
        return avgCoef;
    }
    public double getAvgLength(){
        return avgLength;
    }

    public void setCoreness(int coreness) {
        Coreness = coreness;
    }

    /*return the num of node
        * */
    public int V(){ return  V;}
    /* return the num of edge
    * */
    public int E(){return E;}

    /* delete node v, we should also delete the edges that connecting v.
     * and we delete node v by node's name
     */
    public void delVec(Integer v){
        Iterator<Bag<Integer>> it = adj.iterator();
        while(it.hasNext()){
            Bag<Integer> bag = it.next();
            if(v.equals(bag.getLabel())){
                Iterator<Integer> bag_it = bag.itertor();
                while(bag_it.hasNext()){
                    delEdgeOfNode(bag_it.next(), v);
                }
                it.remove();
                break;
            }
        }
    }


    /*----------------------------private------------------------------*/
    //node-degree distribution
    private void computeDist(){

        int[] deg = new int[V];
        for(int i = 0; i < V; i++){
            deg[i] = 0;
        }
        /* Obviously, for V node, the max degree of one node is V-1, the min is 0.
         * we can compute each node's degree
         * and then compute the num of same degree.
        * */
        for(Bag<Integer> bag: adj){
            deg[bag.size()]++;
        }
        /* the num of node that has i degree is deg[i]
        * */
       delta = new double[V];
        for(int i = 0; i < V; i++){
            delta[i] = deg[i] ;/// V;
        }

    }

    /* compute the average shortest path-length
    * */
    private void ComputeAvgLength(){
        int total = 0;
        for(int i = 0; i < V; i++){
            for(int j = i+1; j < V; j++){
                total += DLen[i][j];
            }
        }

        avgLength = 2*total/(V*(V-1));
    }


    /* We
    * */
    public void computeCoreness(){
        Coreness = 0;
        ArrayList<Bag<Integer>> copy = adj; //copy of adj
        sort();
        Coreness = adj.get(0).size();
        while(!copy.isEmpty()){
            if(adj.get(0).size() >= Coreness){
                Coreness++;
                continue;
            }
            Iterator it = adj.iterator();
            while(it.hasNext()){
                Bag<Integer> bag = (Bag<Integer>) it.next();
                if(bag.size() < Coreness){
                    delVec(bag.getLabel());
                    break;
                }

            }
            sort();
        }
        adj = copy;
        //copy.clear();
    }
    /* compute the shortest path length between each node
         */
    private void floyd(){
        for(int i = 0; i < V; i++){
            for(int j = 0; j < V; j++){
                for(int k = 0; k < V; k++) {
                    if (DLen[i][j] > DLen[i][k] + DLen[k][j]) {
                        DLen[i][j] = DLen[i][k] + DLen[k][j];
                    }
                }
            }
        }

    }
    /* compute each node's clustering coefficient
    * */

    private void coefOfEachNode(){
        for(Bag<Integer> bag: adj){
            ArrayList<Integer> set = bag.getBag();
            int ET = 0;
            int T = set.size();
            if(T - 1 <= 0){
                Coef[bag.getLabel()] = 0;
            }else {
                for (int w : set) {
                    for (int v : set) {
                        if(isEdgeExist(w,v)){
                            ET++;
                        }
                    }
                }
                Coef[bag.getLabel()] = ET / (T * (T - 1));
            }
        }

        for(int i = 0; i < Coef.length; i++){
            avgCoef += Coef[i];
        }
        avgCoef /= V;
    }

    private boolean isEdgeExist(Integer v, Integer w){
        for(int k: adj.get(v).getBag()){
            if(k == w){
                return true;
            }
        }
        return false;
    }

    /* the xls data is a matrix of undirected graph
    * */
    private void addEdg(int v, int w){
        adj.get(v).add(w);
    }

    /* return the Iterator of a bag,
     * it's the set of node that connected to v
    * */
    private Iterator<Integer> iteratorOfNode(Integer v){
        Iterator<Bag<Integer>> it = adj.iterator();
        while(it.hasNext()){
            Bag<Integer> bag = it.next();
            if(v.equals(bag.getLabel())){
                return bag.itertor();
            }
        }
        return null;
    }

    /* return the degree of one node,
     * it's the size of the set that connected to v
    * */
    private int degree(Integer v){
        Bag<Integer> bag = (Bag<Integer>) iteratorOfNode(v);
        if(bag == null)
            return 0;
        return bag.size();
    }

    private void delEdgeOfNode(Integer i, Integer j){
        for(int index = 0; index < adj.size(); index++){
            if(i.equals(adj.get(index).getLabel())){
                adj.get(index).remove(j);
            }
        }
    }

    /* delete edge
     * as we ko
    * */
    private void delEdge(Integer v, Integer w){
        delEdgeOfNode(v,w);
        delEdgeOfNode(w,v);
        E--;
    }




    private void sort(){
        BagComparatorBySize<Integer> comparetor = new BagComparatorBySize();
        Collections.sort(adj, comparetor);
    }

    private void sortByLabel(){
        BagComparetorByLabel comparetor = new BagComparetorByLabel();
        Collections.sort(adj, comparetor);
    }

    public String getTitle() {
        return title;
    }
}
