package com.lx.permission;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * 类说明：权限工具类
 * create by liuxiong at 2019/4/28 0028 20:35.
 */
public class  PermissionUtil {
    private static String TAG="----"+PermissionUtil.class.getSimpleName();
    private static FailedCallBack failedCallBack;

    private static  String[] permissions;

    /** 方法说明：申请权限
     *  @param context 应用上下文
     *  create by liuxiong at 2019/4/28 0028 20:40
     */
    public static void request(Context context, String[] permissions, final PermissionCallback callback){
        PermissionUtil.permissions=permissions;

        PermissionCallback callback02 = checkArguments(context, permissions, callback);
        
        PermissionActivity.request(context.getApplicationContext(), permissions, callback02==null
                        ?callback:callback02,false);
    }

    /** 方法说明：用户说明后重新申请
     *  @param context 应用上下文
     *  create by liuxiong at 2019/4/28 0028 20:40
     */
    public static void requestAgain(Context context,PermissionCallback callback){
        //检验参数
        PermissionCallback callback02 = checkArguments(context, permissions, callback);
        //启动一个activity申请权限
        PermissionActivity.request(context.getApplicationContext(), permissions, callback02==null
                ?callback:callback02,true);
    }
    /** 方法说明：用户说明后重新申请
     *  @param context 应用上下文
     *  create by liuxiong at 2019/4/28 0028 20:40
     */
    @Deprecated
    public static void requestAgain(Context context, String[] permissions,PermissionCallback callback){
        //检验参数
        PermissionCallback callback02 = checkArguments(context, permissions, callback);
        //启动一个activity申请权限
        PermissionActivity.request(context.getApplicationContext(), permissions, callback02==null
                ?callback:callback02,true);
    }

    /**
    * 方法说明:  检查参数
    * created by liuxiong on 2019/4/29 14:41
    */
    private static PermissionCallback checkArguments(Context context, String[] permissions,
                                                     final PermissionCallback callback) {
        if(context==null){
            throw new IllegalArgumentException("context 不能为空");
        }
        if(permissions==null){
            throw new IllegalArgumentException("permissions 不能为空");
        }
        if(callback==null){
            Log.d(TAG,"PermissionCallback 为空");
        }

        /** 如果传入的callback 是 SuccessCallback，那么需要一个PermissionCallback代理一下回调*/
        PermissionCallback callback02=null;
        if(callback instanceof SuccessCallback){
            Log.d(TAG,"callback is SuccessCallback");
            callback02=new PermissionCallback() {
                @Override
                public void onPermissionGranted() {
                    if(callback!=null){
                        callback.onPermissionGranted();
                    }
                }

                @Override
                public void shouldShowRational(String[] rationalPermissons, boolean before) {
                    if(failedCallBack!=null){
                        failedCallBack.shouldShowRational(rationalPermissons,before);
                    }else{
                        Log.d(TAG,"没有设置权限申请失败的回调");
                    }
                }

                @Override
                public void onPermissonReject(String[] rejectPermissons) {
                    if(failedCallBack!=null){
                        failedCallBack.onPermissonReject(rejectPermissons);
                    }else{
                        Log.d(TAG,"没有设置权限申请失败的回调");
                    }
                }
            };
        }
        return callback02;
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
