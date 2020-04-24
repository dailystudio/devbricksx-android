# Logging
**DevBricksX** bases and enhance the default Android logging system. Same as default logging system, it separates the log in different levels:

DevBricks Logger        | Android Log
:---                    | :-------
**`.debug()`**          | Log.d()
**`.debugSecure()`**    | Log.d()
**`.info()`**           | Log.i()
**`.warn()`**           | Log.w()
**`.error()`**          | Log.e()

Different with default logging utilities, **`Logger`** do not need you to provide a TAG as parameter when you print the log. It will automatically provides a TAG according the class and method which is currently calling **`Logger`** to print the logs. For example,

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

If you derive you application from  **DevBricksApplication** , it enables or disables debug outputs according to your build type. But you can use **`setDebugEnabled()`** and **`setSecureDebugEnabled()`** to force enable debug logging anywhere in your code.

#

