package com.ices.uaegis.com.ices.utility;

/**
 * Created by Lehyu on 2016/5/18.
 */
public abstract interface MessageListener<T>
{
    public abstract void handleMessage(int paramInt, T paramObject);
}
