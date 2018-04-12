package com.example.christianalderite.barkr;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public final class Utilities {

    static ProgressDialog dialogProgress;

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void showLoadingDialog(Context context){
        dialogProgress = new ProgressDialog(context);
        String append;
        if(!isNetworkAvailable(context)){
            append = "Waiting for internet connection. ";
        }else{
            append = " Loading data. Please wait... ";
        }
        dialogProgress.setMessage(append);
        dialogProgress.setCancelable(false);
        dialogProgress.setIndeterminate(true);
        dialogProgress.show();
    }

    public static void dismissDialog(){
        dialogProgress.dismiss();
    }

    public static void taskFailedAlert(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Task Failed");
        builder.setMessage("Cannot complete task. Please connect to the internet and try again.");
        builder.setCancelable(true);
        builder.show();
    }

    public static void basicAlert(Context context, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.show();
    }

}
