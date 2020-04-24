# Global Context
As you know **`Context`** is an important thing in Android application. Your code can do few things without **`Context`**. DevBricks provides an interfaces to bind a global application context - **`GlobalContextWrapper`**. You can retrieve it at anywhere in your application. To bind the context, you can call **`bindContext()`**:
```java
public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		GlobalContextWrapper.bindContext(getApplicationContext());
	}

}
```
Once, you have bound the application context. You can call **`getContext()`** when you need a **`Context`** instance. Here is an example:
```java
Context context = GlobalContextWrapper.getContext();

if (context != null) {
	context.startActivity(launchIntent);
}
```
**`GlobalContextWrapper`** will bind an application context rather than an activity context. Even you pass an **`Activity`** object as second parameter to **`bindContext()`**, it will call **`getApplicationContext()`** of **`Activity`** to retrieve correct application context for further operation. That means you needn't to worry about memory leak of this global context holder. Each application only has one application context instance and will not hold any information about view root. Anyway, to make the usage rigorous, you can call **`unbindContext()`** before your application is terminated.
```java
public class MyApplication extends Application {

	@Override
	public void onTerminate() {
		GlobalContextWrapper.unbindContext(getApplicationContext());
		super.onTerminate();
	}

}
```

