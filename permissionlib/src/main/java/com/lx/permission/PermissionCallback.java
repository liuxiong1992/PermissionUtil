package com.lx.permission;

/** 类说明：申请权限回调
 *  create by liuxiong at 2019/4/28 0028 20:18 .
 */
public interface PermissionCallback {
    /** 方法说明：已有权限或者申请权限成功
     *  create by liuxiong at 2019/4/28 0028 20:32
     */
    void onPermissionGranted();

    /** 方法说明：申请权限失败并且用户没有勾选“禁止后不在询问”
     *  @param  rationalPermissons 用户不同意的权限集合
     *  @param  before true 权限申请前回调 false 权限申请后回调
     *  create by liuxiong at 2019/4/28 0028 21:56
     */
    void shouldShowRational(String[] rationalPermissons,boolean before);

    /** 方法说明：权限已被禁止 或 申请权限失败并且用户勾选了“禁止后不在询问”
     *  @param  rejectPermissons 被禁止的权限集合
     *  create by liuxiong at 2019/4/28 0028 21:55
     */
    void onPermissonReject(String[] rejectPermissons);
}
