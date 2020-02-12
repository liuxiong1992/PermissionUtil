package com.lx.permissionutil;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.lx.permission.FailedCallBack;
import com.lx.permission.PermissionCallback;
import com.lx.permission.PermissionUtil;
import com.lx.permission.SuccessCallback;

public class MainActivity extends AppCompatActivity {
    String tag="----"+ MainActivity.class.getSimpleName();

    //要申请的权限
    final String[] permission= new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE};

    //要申请权限的回调
    PermissionCallback callback=new PermissionCallback() {

        @Override
        public void onPermissionGranted() {
            toast("权限申请成功--方式1");
        }

        @Override
        public void shouldShowRational(String[] rationalPermissons, boolean before) {

            StringBuilder sb=new StringBuilder();
            sb.append("我们将获取以下权限:\n\n");

            for(int i=0;i<rationalPermissons.length;i++){
                sb.append((i+1)+"、");
                if(Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(rationalPermissons[i])){
                    sb.append("读写设备外部存储空间的权限，将被用于获取用户头像、保存一些文件到项目文件夹中");
                }else if(Manifest.permission.CAMERA.equals(rationalPermissons[i])){
                    sb.append("使用摄像头的权限将，被用于拍照获取用户头像，直播视频采集");
                }else if(Manifest.permission.READ_PHONE_STATE.equals(rationalPermissons[i])){
                    sb.append("读取手机状态信息的权限，将被用于登录时账号验证");
                }

                sb.append("\n");
            }

            PermissionUtil.showDialog(MainActivity.this, sb.toString(),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            //重新申请权限
                            PermissionUtil.requestAgain(MainActivity.this,callback);
                        }
                    });
        }

        @Override
        public void onPermissonReject(String[] rejectPermissons) {
            StringBuilder sb=new StringBuilder();
            sb.append("我们需要的权限:\n\n");

            for(int i=0;i<rejectPermissons.length;i++){
                sb.append((i+1)+"、");
                if(Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(rejectPermissons[i])){
                    sb.append("读写外部存储空间的权限，将被用于获取用户头像、保存一些文件到项目文件夹中");
                }else if(Manifest.permission.CAMERA.equals(rejectPermissons[i])){
                    sb.append("使用摄像头的权限，被用于拍照获取用户头像，直播视频采集");
                }else if(Manifest.permission.READ_PHONE_STATE.equals(rejectPermissons[i])){
                    sb.append("读取手机状态信息的权限，将被用于登录时账号验证");
                }
                sb.append("\n");
            }
            sb.append("\n被设为禁止,请到设置里开启权限");

            PermissionUtil.showDialog(MainActivity.this, sb.toString(),
                    getString(R.string.dialog_cancel),
                    getString(R.string.dialog_go_setting),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            //重新申请权限
                            PermissionUtil.startSettingsActivity(MainActivity.this);
                        }
                    }
            );
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /** 申请结果处理方式一*/
        findViewById(R.id.text_view01).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //申请权限
                PermissionUtil.request(MainActivity.this,permission,callback);
            }
        });


        /** 申请结果处理方式二 */
        findViewById(R.id.text_view02).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //申请权限
                final SuccessCallback successCallback=new SuccessCallback(){

                    @Override
                    public void onPermissionGranted() {
                        toast("权限申请成功--方式2");
                    }
                };

                PermissionUtil.request(MainActivity.this, permission,successCallback );

                /**  申请失败的回调，整个app只需设置一次，统一在一个地方处理 */
                PermissionUtil.setFailedCallBack(new FailedCallBack() {
                    @Override
                    public void shouldShowRational(String[] rationalPermissons, boolean before) {
                        StringBuilder sb=new StringBuilder();
                        sb.append("我们将获取以下权限:\n\n");

                        for(int i=0;i<rationalPermissons.length;i++){
                            sb.append((i+1)+"、");
                            if(Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(rationalPermissons[i])){
                                sb.append("读写设备外部存储空间的权限，将被用于获取用户头像、保存一些文件到项目文件夹中");
                            }else if(Manifest.permission.CAMERA.equals(rationalPermissons[i])){
                                sb.append("使用摄像头的权限将，被用于拍照获取用户头像，直播视频采集");
                            }else if(Manifest.permission.READ_PHONE_STATE.equals(rationalPermissons[i])){
                                sb.append("读取手机状态信息的权限，将被用于登录时账号验证");
                            }

                            sb.append("\n");
                        }

                        PermissionUtil.showDialog(MainActivity.this, sb.toString(),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                        //重新申请权限
                                        PermissionUtil.requestAgain(MainActivity.this);
                                    }
                                });
                    }

                    @Override
                    public void onPermissonReject(String[] rejectPermissons) {
                        StringBuilder sb=new StringBuilder();
                        sb.append("我们需要的权限:\n\n");

                        for(int i=0;i<rejectPermissons.length;i++){
                            sb.append((i+1)+"、");
                            if(Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(rejectPermissons[i])){
                                sb.append("读写外部存储空间的权限，将被用于获取用户头像、保存一些文件到项目文件夹中");
                            }else if(Manifest.permission.CAMERA.equals(rejectPermissons[i])){
                                sb.append("使用摄像头的权限，被用于拍照获取用户头像，直播视频采集");
                            }else if(Manifest.permission.READ_PHONE_STATE.equals(rejectPermissons[i])){
                                sb.append("读取手机状态信息的权限，将被用于登录时账号验证");
                            }
                            sb.append("\n");
                        }
                        sb.append("\n被设为禁止,请到设置里开启权限");

                        PermissionUtil.showDialog(MainActivity.this, sb.toString(),
                                getString(R.string.dialog_cancel),
                                getString(R.string.dialog_go_setting),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        //重新申请权限
                                        PermissionUtil.startSettingsActivity(MainActivity.this);
                                    }
                                }
                        );
                    }
                });
            }
        });
    }

    void toast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    void log(String message){
        Log.d(tag,message);
    }
}
