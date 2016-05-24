package com.ices.uaegis.com.ices.utility;

/**
 * Created by Lehyu on 2016/5/18.
 */
public abstract interface AsyncExecutor<T>
{
    public abstract T asyncExecute();

    public abstract void executeComplete(T paramT);

    public abstract void executePrepare();
}