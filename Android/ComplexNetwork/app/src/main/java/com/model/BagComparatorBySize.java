package com.model;

import android.content.ClipData;

import java.util.Comparator;

/**
 * Created by lhy on 10/23/15.
 */
public class BagComparatorBySize<Item> implements Comparator<Bag<Item>>{
    @Override
    public int compare(Bag<Item> lhs, Bag<Item> rhs) {
        int lhsize = ((Bag<Item>)lhs).size();
        int rhsize = ((Bag<Item>)rhs).size();
        //System.out.println("In compare "+lhsize+"  "+rhsize);
        if(lhsize > rhsize){
            return 1;
        }else if(lhsize == rhsize){
            return 0;
        }else{
            return -1;
        }
    }
}
