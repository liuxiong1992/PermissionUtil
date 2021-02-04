package com.lx.permission;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.function.Consumer;

/**
 * 类说明：申请权限的activity
 * create by liuxiong at 2019/4/28 0028 20:12
 */
public class PermissionActivity extends AppCompatActivity {
    private static String TAG="----"+PermissionActivity.class.getSimpleName();

    public static final String KEY_PERMISSIONS = "permissions";
    private static final int RC_REQUEST_PERMISSION = 100;

    private RequestBean mRequestBean;
    private PermissionCallback mCallback;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRequestBean=PermissionUtil.queue.firstNode;
        Log.d(TAG,"---onCreate");
        if (mRequestBean==null) {
            finish();
            return;
        }
        mCallback=mRequestBean.getCallBack();

        if (mCallback==null) {
            finish();
            return;
        }

        if (Build.VERSION.SDK_INT >= 23) {

            ArrayList<String> needRequestPermission = getNeedRequestPermission(mRequestBean.permissions);
            //已经拥有所有要申请的权限
            if(needRequestPermission.size()==0){

                callPermissionGranted();

            //有权限需要申请
            }else{
                //被拒绝过的权限的权限列表
                ArrayList<String> rationalPermission = getRationalPermission(needRequestPermission);

                //没有被用户拒绝过的权限或者已经向用户说明过了
                if(rationalPermission.size()==0||mRequestBean.isAgain()){
                    Log.d(TAG,"---requestPermissions");
                    requestPermissions(listToArray(needRequestPermission), RC_REQUEST_PERMISSION);
                }else{

                    //在申请权限前回调，提示用户需要申请哪些权限
                    callShouldShowRational(listToArray(rationalPermission),true);
                }
            }
        }else{
            Log.d(TAG,"api 版本小于23");
            callPermissionGranted();
        }
    }

    private void callPermissionGranted(){
        if(mCallback!=null){
            mCallback.onPermissionGranted();
        }
        PermissionUtil.setComplete(getApplicationContext());
        finish();
    }

    private void callShouldShowRational(String[] rationalPermissons,boolean before){
        if(mCallback!=null){
            mCallback.shouldShowRational(mRequestBean,rationalPermissons,before);
        }
        PermissionUtil.setComplete(getApplicationContext());
        finish();
    }

    private void callPermissonReject(String[] rejectPermissons){
        if(mCallback!=null){
            mCallback.onPermissonReject(rejectPermissons);
        }
        PermissionUtil.setComplete(getApplicationContext());
        finish();
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
            callPermissionGranted();
        }else{
            //被禁止的权限集合
            ArrayList<String> rejectPermission = getRejectPermission(noPermissions);

            if(rejectPermission.size()==0){ //所有的权限都应该向用户说明

                //申请权限后，一些权限没有被通过，应该向用户说明
                callShouldShowRational(listToArray(noPermissions),false);

            //有权限已经被禁止了
            }else{
                callPermissonReject(listToArray(rejectPermission));
            }
        }
    }

}
