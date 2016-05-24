package com.ices.uaegis.com.ices.utility;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by Lehyu on 2016/5/18.
 */
public class NoButtonMessageDialog extends AlertDialog
{
    public NoButtonMessageDialog(Context paramContext)
    {
        super(paramContext);
        init();
    }

    protected NoButtonMessageDialog(Context paramContext, int paramInt)
    {
        super(paramContext, paramInt);
        init();
    }

    protected void init()
    {
        setCanceledOnTouchOutside(false);
        setCancelable(true);
        setMessage("No Information Provided");
    }
}
