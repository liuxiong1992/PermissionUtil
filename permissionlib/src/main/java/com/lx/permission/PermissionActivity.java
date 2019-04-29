package com.lx.permission;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;

/**
 * 类说明：申请权限的activity
 * create by liuxiong at 2019/4/28 0028 20:12
 */
public class PermissionActivity extends AppCompatActivity {
    private static String TAG="----"+PermissionActivity.class.getSimpleName();

    public static final String KEY_PERMISSIONS = "permissions";
    private static final int RC_REQUEST_PERMISSION = 100;
    private static PermissionCallback CALLBACK;
    private static boolean isAgain=false; //是否为向用户说明后重新申请


    /**
    * 方法说明:  申请权限
    * @param  callback 申请结果回调
    * @param  isAgain 是否为向用户说明后重新申请
    * created by liuxiong on 2019/4/29 11:50
    */
    protected static void request(Context context, String[] permissions, PermissionCallback callback,boolean isAgain) {
        CALLBACK = callback;
        PermissionActivity.isAgain = isAgain;
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.putExtra(KEY_PERMISSIONS, permissions);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        Log.d(TAG,"startActivity");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG,"---onCreate");
        Intent intent = getIntent();
        if (!intent.hasExtra(KEY_PERMISSIONS)) {
            finish();
            return;
        }
        String[] permissions = getIntent().getStringArrayExtra(KEY_PERMISSIONS);
        if (Build.VERSION.SDK_INT >= 23) {

            ArrayList<String> needRequestPermission = getNeedRequestPermission(permissions);
            if(needRequestPermission.size()==0){//已经拥有所有要申请的权限
                if(CALLBACK!=null){
                    CALLBACK.onPermissionGranted();
                }
                finish();
            }else{ //有权限需要申请
                //应该向用户说明为什么需要的权限
                ArrayList<String> rationalPermission = getRationalPermission(needRequestPermission);

                if(rationalPermission.size()==0||isAgain){//没有应该向用户说明为什么需要的权限或已经说明过了
                    Log.d(TAG,"---requestPermissions");
                    requestPermissions(listToArray(needRequestPermission), RC_REQUEST_PERMISSION);
                }else{
                    if(CALLBACK!=null){
                        CALLBACK.shouldShowRational(listToArray(rationalPermission),true);
                    }
                    finish();
                }
            }
        }else{
            Log.d(TAG,"api 版本小于23");
            if(CALLBACK!=null){
                CALLBACK.onPermissionGranted();
            }
            finish();
        }
    }

    /** 方法说明：找出被禁止的权限
     *  @param needRequestPermission 没有的权限集合
     *  @return 被禁止的权限集合
     *  create by liuxiong at 2019/4/28 0028 22:15
     */
    @TargetApi(23)
    @NonNull
    private ArrayList<String> getRejectPermission(ArrayList<String> needRequestPermission) {
        ArrayList<String> rejectPermission = new ArrayList<>();
        for(int i=0;i<needRequestPermission.size();i++){
            boolean rationale = shouldShowRequestPermissionRationale(needRequestPermission.get(i));
            if(!rationale){ //这个没有的权限已经被禁止了
                rejectPermission.add(needRequestPermission.get(i));
            }
        }
        return rejectPermission;
    }

    /** 方法说明：找出需要询问的权限
     *  @param needRequestPermission 没有同意的权限集合
     *  @return 被禁止的权限集合
     *  create by liuxiong at 2019/4/28 0028 22:15
     */
    @TargetApi(23)
    @NonNull
    private ArrayList<String> getRationalPermission(ArrayList<String> needRequestPermission) {
        ArrayList<String> rationalPermission = new ArrayList<>();
        for(int i=0;i<needRequestPermission.size();i++){
            boolean rationale = shouldShowRequestPermissionRationale(needRequestPermission.get(i));
            if(rationale){ //这个没有的权限已经被禁止了
                rationalPermission.add(needRequestPermission.get(i));
            }
        }
        return rationalPermission;
    }

    /** 方法说明：找出没有的权限
     *  create by liuxiong at 2019/4/28 0028 21:07
     */
    public ArrayList<String> getNeedRequestPermission(String[] permission){
        ArrayList<String> needRequestPermission=new ArrayList<>();
        for(int i=0;i<permission.length;i++){
            int perm = this.checkCallingOrSelfPermission(permission[i]);
            if(perm != PackageManager.PERMISSION_GRANTED){
                needRequestPermission.add(permission[i]);
            }
        }
        return needRequestPermission;
    }

    /** 方法说明：list转array
     *  create by liuxiong at 2019/4/28 0028 22:02
     */
    public String[] listToArray(ArrayList<String> needRequestPermission){
        String[] result = new String[needRequestPermission.size()];
        for(int i=0;i<needRequestPermission.size();i++){
            result[i]=needRequestPermission.get(i);
        }
        return result;
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != RC_REQUEST_PERMISSION) {
            finish();
            return;
        }
        //没有同意的权限
        ArrayList<String> noPermissions = getNeedRequestPermission(permissions);

        if(noPermissions.size()==0){
            //所有权限都通过
            if(CALLBACK!=null){
                CALLBACK.onPermissionGranted();
            }
        }else{
            //被禁止的权限集合
            ArrayList<String> rejectPermission = getRejectPermission(noPermissions);

            if(rejectPermission.size()==0){ //所有的权限都应该向用户说明
                if(CALLBACK!=null){
                    //应该向用户说明的权限集合
                    CALLBACK.shouldShowRational(listToArray(noPermissions),false);
                }

            }else{ //有权限已经被禁止了
                if(CALLBACK!=null){ //直接回调
                    CALLBACK.onPermissonReject(listToArray(rejectPermission));
                }
//                if(CALLBACK!=null){
//                    //应该向用户说明的权限集合
//                    ArrayList<String> rationalPermission = getRationalPermission(noPermissions);
//                    CALLBACK.shouldShowRational(listToArray(rationalPermission),false);
//                }
            }
        }

        finish();
    }

}
