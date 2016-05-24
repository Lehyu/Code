package com.ices.uaegis.com.ices.utility;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Lehyu on 2016/5/18.
 */
public class DialogUtil
{
    public static ProgressDialog createProgressDialog(Context paramContext, String paramString)
    {
        ProgressDialog localProgressDialog = new ProgressDialog(paramContext);
        localProgressDialog.setMessage(paramString);
        localProgressDialog.setProgressStyle(0);
        localProgressDialog.setMax(100);
        localProgressDialog.setCancelable(false);
        return localProgressDialog;
    }

    public static AlertDialog showAlertDialog(Context paramContext, String paramString1, String paramString2, String paramString3)
    {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramContext);
        localBuilder.setTitle(paramString1);
        localBuilder.setMessage(paramString3);
        localBuilder.setPositiveButton(paramString2, null);
        localBuilder.setCancelable(false);
        AlertDialog localAlertDialog = localBuilder.create();
        localAlertDialog.show();
        return localAlertDialog;
    }

    public static AlertDialog showNoButtonDialog(Context paramContext, String paramString1, String paramString2)
    {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramContext);
        localBuilder.setTitle(paramString1);
        localBuilder.setMessage(paramString2);
        localBuilder.setCancelable(false);
        AlertDialog localAlertDialog = localBuilder.create();
        localAlertDialog.show();
        return localAlertDialog;
    }

    public static ProgressDialog showProgressDialog(Context paramContext, String paramString)
    {
        ProgressDialog localProgressDialog = createProgressDialog(paramContext, paramString);
        localProgressDialog.show();
        return localProgressDialog;
    }
}