# Logging

Logging system in **DevBricksX** bases on Android logging system, but provides more enhanced features. 


## Basic usage

Same as default logging system, it separates the logs into different levels:

DevBricks Logger        | Android Log
:---                    | :-------
**`.debug()`**          | Log.d()
**`.debugSecure()`**    | Log.d()
**`.info()`**           | Log.i()
**`.warn()`**           | Log.w()
**`.error()`**          | Log.e()

Compare to default logging utilities, **`Logger`** do not need you to provide a TAG as parameter when you print the log. It will automatically provides a TAG according the class and method which is currently calling **`Logger`** to print the logs. For example,

```kotlin
class MyApplication : DevBricksApplication() {

    override fun onCreate() {
        super.onCreate();

        Logger.debug("Hello app: ${getString(R.string.app_name)}")
    }

}
```

It is quite similar as calling **Log** interfaces, but TAG is automatically generated as `MyApplication: onCreate()` by the library and the log output will be like this:

```console
...
02-22 17:09:06.888 8476-8476/? D/MyApplication: onCreate(): Hello app: MyApplication
...
```

If you derive you application from **DevBricksApplication** , it enables or disables debug outputs according to your build type. But you can use **`setDebugEnabled()`** and **`setSecureDebugEnabled()`** to force enable debug logging anywhere in your code.

## Force debug outputs
If you use **DevBricksApplication**, by default, the debug logs will be suppressed in release build. But there is still a way to force printing debug logs. It is useful when you are debugging an issue which cannot be reproduced in debug build. Simply create an empty file in a specific directory on external storage and restart the application, the debug outputs will appear in Logcat console. 

The file should be put in directory `<sdcard>/Android/data/<your_packge_name>/files/Documents` and it names `dslog_force`. 

Here is a concrete sample. We assume that the root of your external storage is `/storage/emulated/0` and your package name is `com.mysample.app`. Then you need to create the file with the following command:

```shell
# adb shell touch /storage/emulated/0/Android/data/com.mysample.app/files/Documents/dslog_force
```

After that, restart your application, you will see the debug outputs. You can remove that file with the command:

```shell
# adb shell rm /storage/emulated/0/Android/data/com.mysample.app/files/Documents/dslog_force
```
And then everything will back to normal.

## Secure debug
There would be a concern about security. If the debug outputs can be turned on in a release build, can I put some sensitive information in debug outputs? The answer is yes. You can use debugSecure() to print sensitive logs in debug mode:

``` kotlin
fun login() {
    ...
    
    Logger.debugSecure("credential: uid = $uid, secret = $secret")
    
    ...
}
```
This line of debug output will not be seen even when the file `dslog_force` exists in the right place.
