# Logging
DevBricks bases and enhance the default Android logging mechanism. Same as default logging mechanism, it separates the log in four different priorities:

DevBricks Logger        | Android Log
:---                    | :-------
**`.debug()`**          | Log.d()
**`.debugSecure()`**    | Log.d()
**`.info()`**           | Log.i()
**`.warn()`**           | Log.w()
**`.error()`**          | Log.e()

Different with default logging utils, **`Logger`** do not need you to provide a TAG when you print the log. It will automatically provides a TAG according the class and method which is currently calling **`Logger`** to print the logs. For example,
```java
public class MyApplication extends DevBricksApplication {

	@Override
	public void onCreate() {
		super.onCreate();

		Logger.debug("Hello app: %s", getString(R.string.app_name))
	}

}
```
The first parameter is the output format of log, while rest parameters provide the content of the arguments describe in the first parameter. It is exactly same as **`String.format()`**. The TAG will be generated as `MyApplication: onCreate()` and the log output will be like this:
```java
...
02-22 17:09:06.888 8476-8476/? D/MyApplication: onCreate(): Hello app: MyApplication
...
```
There is another important interfaces in **`Logger`** class is **`setDebugEnabled()`** and **`setSecureDebugEnabled()`**. As you seen in the last chapter, **`DevBricksApplication`** will automatically enable or disable debug outputs according to some case. But you can use **`setDebugEnabled()`** and **`setSecureDebugEnabled()`**to force enable or disable debug logging.
