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

    final String[] permission= new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE};

    PermissionCallback callback=new PermissionCallback() {

        @Override
        public void onPermissionGranted() {
            toast("权限申请成功--方式1");
        }

        @Override
        public void shouldShowRational(String[] rationalPermissons, boolean before) {
            for(String str : rationalPermissons){
                log("shouldShowRational---方式1 "+str);
            }

            PermissionUtil.showDialog(MainActivity.this, "我们获取xxx权限仅用于xxx",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            //重新申请权限
                            PermissionUtil.requestAgain(MainActivity.this,permission,callback);
                        }
                    });
        }

        @Override
        public void onPermissonReject(String[] rejectPermissons) {
            for(String str : rejectPermissons){
                log("onPermissonReject---方式1 "+str);
            }

            PermissionUtil.showDialog(MainActivity.this, "xxx功能需要的xxx权限被禁止，请到设置里设置权限",
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
                //重新申请权限
                PermissionUtil.request(MainActivity.this,permission,callback);
            }
        });


        /** 申请结果处理方式二 */
        findViewById(R.id.text_view02).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //重新申请权限
                PermissionUtil.request(MainActivity.this, permission, new SuccessCallback(){

                    @Override
                    public void onPermissionGranted() {
                        toast("权限申请成功--方式2");
                    }
                });



                /**  申请失败的回调，整个app只需设置一次，统一在一个地方处理 */
                PermissionUtil.setFailedCallBack(new FailedCallBack() {
                    @Override
                    public void shouldShowRational(String[] rationalPermissons, boolean before) {
                        for(String str : rationalPermissons){
                            log("shouldShowRational---方式2 "+str);
                        }
                    }

                    @Override
                    public void onPermissonReject(String[] rejectPermissons) {
                        for(String str : rejectPermissons){
                            log("onPermissonReject---方式2 "+str);
                        }
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
