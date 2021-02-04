# PermissionUtil

## 1、android 权限申请工具类库 ##

 android 权限申请的回调在activity里面，在本类库中，为了方便在fragment等其他需要权限的地方申请和处理回调，在每次申请时，都会启动一个透明的activity，并在透明activity里面申请权限和处理回调，然后在透明activity任务完成后finish掉。

### 特点：

1. 可以在任意地方发起权限申请；
2. 可以统一处理权限回调；
3. 新增维护一个请求队列，依次处理请求，在连续多次发起请求时，不会造成回调冲突；
4. 已经拥有申请的全部权限或api版本小于23，都会返回申请成功。

博客地址：[https://blog.csdn.net/liu_xiong/article/details/89711470](https://blog.csdn.net/liu_xiong/article/details/89711470 "csdn地址")

## 2、效果 ##
申请时拒绝，拒绝后会回调被拒绝的权限，可以对被拒绝的权限进行说明，并重新申请。

![](https://img-blog.csdnimg.cn/20190501115628756.gif)

申请时权限被拒绝，并勾选了不再询问，可以请求用户去设置里面打开。

![](https://img-blog.csdnimg.cn/20190501115925903.gif)

## 2、gradle 配置  ##
	
project的build.gradle

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

app的build.gradle

	dependencies {
			implementation 'com.github.liuxiong1992:PermissionUtil:1.5'
	}


## 3、使用方式一：单独处理失败回调 ##
   

要申请的权限

	final String[] permission= new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE};

要申请权限的回调

    PermissionCallback callback=new PermissionCallback() {

        @Override
        public void onPermissionGranted() {
            toast("权限申请成功--方式1");
        }

        @Override
        public void shouldShowRational(final RequestBean requestBean,String[] rationalPermissons, final boolean before) {

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
                }else if(Manifest.permission.RECORD_AUDIO.equals(rationalPermissons[i])){
                    sb.append("录制引起的权限，将被用于直播音频采集");
                }

                if(i==rationalPermissons.length-1){
                    sb.append("。");
                }else{
                    sb.append("；");
                }

                sb.append("\n");
            }

            PermissionUtil.showDialog(MainActivity.this, sb.toString(),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            //重新申请权限
                            PermissionUtil.requestAgain(MainActivity.this,requestBean);
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
                }else if(Manifest.permission.RECORD_AUDIO.equals(rejectPermissons[i])){
                    sb.append("录制引起的权限，将被用于直播音频采集");
                }
                if(i==rejectPermissons.length-1){
                    sb.append("。");
                }else{
                    sb.append("；");
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

发起权限申请

    PermissionUtil.request(MainActivity.this,permission,callback);

## 4、使用方式二：统一处理失败回调 ##

在权限申请前设置统一回调，比如application里面

	/**  申请失败的回调，整个app只需设置一次，统一在一个地方处理 */
    PermissionUtil.setFailedCallBack(new FailedCallBack() {
        @Override
        public void shouldShowRational(final RequestBean requestBean,String[] rationalPermissons, boolean before) {
            StringBuilder sb=new StringBuilder();
            sb.append("我们需要获取以下权限:\n\n");

            for(int i=0;i<rationalPermissons.length;i++){
                sb.append((i+1)+"、");
                if(Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(rationalPermissons[i])){
                    sb.append("读写设备外部存储空间的权限，将被用于获取用户头像、保存一些文件到项目文件夹中");
                }else if(Manifest.permission.CAMERA.equals(rationalPermissons[i])){
                    sb.append("使用摄像头的权限，将被用于拍照获取用户头像，直播视频采集");
                }else if(Manifest.permission.READ_PHONE_STATE.equals(rationalPermissons[i])){
                    sb.append("读取手机状态信息的权限，将被用于登录时账号验证");
                }else if(Manifest.permission.RECORD_AUDIO.equals(rationalPermissons[i])){
                    sb.append("录制引起的权限，将被用于直播音频采集");
                }

                if(i==rationalPermissons.length-1){
                    sb.append("。");
                }else{
                    sb.append("；");
                }
                sb.append("\n");
            }

            //弹出提示dialog
            PermissionUtil.showDialog(MainActivity.this, sb.toString(),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            //重新申请权限
                            PermissionUtil.requestAgain(MainActivity.this,requestBean);
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
                }else if(Manifest.permission.RECORD_AUDIO.equals(rejectPermissons[i])){
                    sb.append("录制引起的权限，将被用于直播音频采集");
                }

                if(i==rejectPermissons.length-1){
                    sb.append("。");
                }else{
                    sb.append("；");
                }

                sb.append("\n");
            }
            sb.append("\n被设为禁止,请到设置里开启权限");

            //弹出提示dialog
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

权限申请时设置成功的回调

    /** 申请结果处理方式二 */
    findViewById(R.id.text_view02).setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            //申请权限
            PermissionUtil.request(MainActivity.this, permission, new SuccessCallback(){

                @Override
                public void onPermissionGranted() {
                    toast("权限申请成功--方式2");
                }
            });
	}

## 5、版本说明 ##


### version 1.1 ###

弃用PermissionUtil类的requestAgain(Context context,String[] permissions,PermissionCallback callback)方法，用requestAgain(Context context,PermissionCallback callback)替代，以方便统一处理回调结果时方便调用。

### version 1.3 ###
解决统一回调方式重新申请没有成功回调的bug，添加PermissionUtil.requestAgain的重载方法requestAgain（Context context）
    
### version 1.4 ###
出现问题，已删除

### version 1.5 ###
1. 增加请求队列，同时发起多个权限请求会按顺序一个一个处理，解决多个请求回调发生冲突的问题；
2. 修改了PermissionUtil类的request、requestAgain方法，使用时需要修改；
3. 回调方法增加一个参数RequestBean，里面保存了上一次请求的参数，可以在requestAgain方法传入再次发起请求；
4. targetSdkVersion 提升到30；
5. 支持库转为使用androidx。
