package com.lx.permission;

public class RequestBean {

    /** 申请的权限列表 */
    String[] permissions;

    /** 申请权限失败的回调 */
    FailedCallBack failedCallBack;
    /** 申请权限成功的回调 */
    SuccessCallback successCallback;

    /** 是否为向用户说明后重新申请 */
    private boolean isAgain=false;

    /** 后一个节点 */
    public RequestBean nextNode;

    private PermissionCallback mCallBack;

    public RequestBean(String[] permissions, FailedCallBack failedCallBack, SuccessCallback successCallback) {
        this.permissions = permissions;
        this.failedCallBack = failedCallBack;
        this.successCallback = successCallback;
    }

    public RequestBean(String[] permissions, PermissionCallback callBack) {
        this.permissions = permissions;
        this.mCallBack = callBack;
    }

    public PermissionCallback getCallBack() {
        if(mCallBack==null){
            mCallBack = new PermissionCallback() {
                @Override
                public void onPermissionGranted() {
                    if(successCallback!=null){
                        successCallback.onPermissionGranted();
                    }
                }

                @Override
                public void shouldShowRational(RequestBean requestBean,String[] rationalPermissons, boolean before) {
                    if(failedCallBack!=null){
                        failedCallBack.shouldShowRational(requestBean,rationalPermissons,before);
                    }
                }

                @Override
                public void onPermissonReject(String[] rejectPermissons) {
                    if(failedCallBack!=null){
                        failedCallBack.onPermissonReject(rejectPermissons);
                    }
                }
            };
        }

        return mCallBack;
    }

    public boolean isAgain() {
        return isAgain;
    }

    public void setAgain(boolean again) {
        isAgain = again;
    }
}
