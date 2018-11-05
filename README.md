# OftenUtils 常用工具类

##### 这个项目主要是方便自己使用而传上来的一个项目，里面主要含有：
* DeviceUtils(获取手机唯一标示[MD5加密])
* DisplayUtils屏幕信息工具类(超级方便，用了后就停不下来)
* PermissionUtils权限跳转工具类
* PhoneUtils电话号码工具类
* ToastUtils提示框工具类(超级方便，用了后就停不下来)
# 使用

修改你的  `build.gradle`文件

```gradle

//root project
allprojects {
        repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

//module project
     dependencies {
            implementation 'com.github.MrWangChong:OftenUtils:1.0.0'
    }

```

使用方法

* DisplayUtils需要在使用前初始化一次（DisplayUtils.init(this)）
具体使用可参看列子中的BaseActivity
初始化一次之后，获取屏幕高度宽度，状态栏高度，虚拟按键高度以及各个单位之间的转换dp,sp,px之间的转换就再也不需要传Context进去了，简直不要再方便了

* ToastUtils就是出于新手(老司机)也经常使用Toast最后忘记.show()弄得每次都找不到问题原因，并且多次调用Toast.show()会存在半天Toast都不消失的问题而写得一个工具类，调用起来很方便，ToastUtils.showToast就可以了

```java
//判断是否已经开启了通知栏权限
public boolean isEnableNotification() {
    try {
        String packageName = getPackageName();
        PackageManager pm = getPackageManager();
        @SuppressLint("WrongConstant")
        ApplicationInfo ai = pm.getApplicationInfo(packageName, PackageManager.GET_ACTIVITIES);
        Log.d("FriendsAddListActivity", "uid : " + ai.uid);
        return PermissionUtils.isEnableNotification(this, packageName, ai.uid);
    } catch (Exception e) {
        e.printStackTrace();
    }
    return true;
}

//跳转到开启通知栏权限页面
PermissionUtils.jumpNoticePermissionSetting(this)

//跳转到授权页面(定位，自启等等)
PermissionUtils.jumpPermissionSetting(this);

//验证是不是手机号
PhoneUtils.isMobilePhone(number)

//弹出Toast提示
ToastUtils.showToast(this, "网络不可用");
```
