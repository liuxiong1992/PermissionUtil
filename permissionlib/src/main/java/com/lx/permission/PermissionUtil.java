package com.lx.permission;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;

/**
 * 类说明：权限工具类
 * create by liuxiong at 2019/4/28 0028 20:35.
 */
public class  PermissionUtil {
    private static String TAG="----"+PermissionUtil.class.getSimpleName();
    private static FailedCallBack failedCallBack;

    static RequestQueue queue=new RequestQueue();

    static void setComplete(Context context){
        queue.firstNode= queue.firstNode.nextNode;
        if(queue.firstNode!=null&&context!=null){
            //Toast.makeText(context,"请求下一个",Toast.LENGTH_SHORT).show();
            Log.d(TAG,"请求下一个");
            //请求权限
            startPermissionActivity(context);
        }
    }
    /**
     * 方法说明:  启动申请权限的activity（全透明的activity）
     * created by liuxiong on 2019/4/29 11:50
     */
    private static void startPermissionActivity(Context context) {
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        Log.d(TAG,"startActivity");
    }


    /** 方法说明：申请权限，用于统一处理失败结果，需要设置一次 Failcallback
     *  @param context 应用上下文
     *  @param permissions 请求的权限列表
     *  @param successCallback 请求成功的回调
     *  create by liuxiong at 2019/4/28 0028 20:40
     */
    public static void request(Context context, String[] permissions, SuccessCallback successCallback){
        RequestBean requestBean=new RequestBean(permissions,failedCallBack,successCallback);
        request(context, requestBean);
    }

    /** 方法说明：申请权限，用于单独处理失败结果
     *  @param context 应用上下文
     *  @param permissions 请求的权限列表
     *  @param callback 请求结果的回调
     *  create by liuxiong at 2019/4/28 0028 20:40
     */
    public static void request(Context context, String[] permissions,PermissionCallback callback){
        RequestBean requestBean=new RequestBean(permissions,callback);
        request(context, requestBean);
    }

    /**
     * 方法说明：再次申请权限
     * create by liuxiong at 2021/2/3 16:07.
     */
    public static void requestAgain(Context context, RequestBean requestBean){
        if(requestBean==null){
            throw new IllegalArgumentException("requestBean 不能为null");
        }
        requestBean.setAgain(true);
        request(context, requestBean);
    }

    /**
     * 方法说明：申请权限
     * @param requestBean 请求的requestBean，里面封装了一次请求需要的参数
     * create by liuxiong at 2021/2/3 15:12.
     */
    private static void request(Context context, RequestBean requestBean) {
        if(Build.VERSION.SDK_INT >=23){
            if(queue.firstNode==null){
                //加入到队列并请求权限
                queue.add(requestBean);
                startPermissionActivity(context);
            }else{
                //加入到队列
                queue.add(requestBean);
            }
        }else{
            //不需要申请权限，直接回调请求成功,不需要加入队列
            requestBean.getCallBack().onPermissionGranted();
        }
    }


    /**
    * 方法说明:  设置权限申请失败后的回调（一般是统一处理权限申请失败的时候设置）
    * created by liuxiong on 2019/4/29 14:26
    */
    public static void setFailedCallBack(FailedCallBack failedCallBack){
        PermissionUtil.failedCallBack = failedCallBack;
    }

    /** 方法说明：弹出一个dialog，展示一条 message
     *  create by liuxiong at 2019/4/28 0028 20:40
     */
    public static void showDialog(final Activity activity, String message,DialogInterface.OnClickListener positiveListener){

        showDialog(activity,message,null,null,positiveListener);
    }

    /** 方法说明：弹出一个dialog，展示一条 message
     *  create by liuxiong at 2019/4/28 0028 20:40
     */
    public static void showDialog(final Activity activity, String message,String negativeButtonText ,String positiveButtonText,
                                  DialogInterface.OnClickListener positiveListener){
        if(message==null){
            Log.d(TAG,"message==null");
        }
        if(negativeButtonText==null){
            negativeButtonText=activity.getResources().getString(R.string.dialog_cancel);
        }
        if(positiveButtonText==null){
            positiveButtonText=activity.getResources().getString(R.string.dialog_comfirm);
        }
        if(positiveListener==null){
            positiveListener=new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };
        }
        new AlertDialog.Builder(activity).setMessage(message).setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setPositiveButton(positiveButtonText,positiveListener).show();
    }

    /**
     * 跳转到当前设置里当前app页面
     * @param context
     */
    public static void startSettingsActivity(Context context) {
        Uri packageURI = Uri.parse("package:" + context.getPackageName());
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
