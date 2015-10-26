package model;

import java.util.Comparator;

/**
 * Created by lhy on 10/23/15.
 */
public class BagComparetorByLabel<Item> implements Comparator<Bag<Item>>{
    @Override
    public int compare(Bag<Item> lhs, Bag<Item> rhs) {
    	int label = ((Bag<Integer>)lhs).getLabel();
    	int rlabel = ((Bag<Integer>)rhs).getLabel();
    	if(label > rlabel) return 1;
    	else if(label == rlabel) return 0;
    	else return -1;
       
    }
}
