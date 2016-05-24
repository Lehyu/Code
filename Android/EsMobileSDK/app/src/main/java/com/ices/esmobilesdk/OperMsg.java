package com.ices.esmobilesdk;

/**
 * Created by Lehyu on 2016/5/16.
 */
public class OperMsg {
    public final static int LOGIN = 1;

    private int option;
    private String msg;

    public OperMsg(){
    }

    public OperMsg(int option){
        this.option = option;
    }

    public OperMsg(int option, String msg){
        this.option = option;
        this.msg = msg;
    }

    public int getOption() {
        return option;
    }

    public void setOption(int option) {
        this.option = option;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
