# Global Context
As you know **Context** is an important thing in Android application. Your code can do few things without **Context**. The latest interfaces in Android SDK provide sufficient ways to help developers avoid holding an instance of Context. **DevBricksX** also provides you an interface to bind a global application context - **GlobalContextWrapper**. You can retrieve it anywhere in your application. 

## Bind context

To bind the context, you can call **`bindContext()`** in application creation:

```kotlin
open class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        GlobalContextWrapper.bindContext(applicationContext)
        
        ...
    }

}
```
**GlobalContextWrapper** will bind an application context rather than an activity context. Even you pass an Activity object as second parameter to **`bindContext()`**, it will call **`getApplicationContext()`** of Activity to retrieve correct application context for further operation. That means you needn't worry about the memory leak of this global context holder. Each application process only has one application context instance and will not hold any information about the view root.

## Usage context
Once, you have bound the application context. You can call **`getContext()`** whenever you need a **`Context`** instance. Here is an example:

```kotlin
GlobalContextWrapper.context?.startActivity(launchIntent)
```
If you use DevbricksApplication, it automatically binds the context of your application. Otherwise, you need to bind one before using this interface.


## Unbind context

Of course, you can call **`unbindContext()`** before your application is terminated.

```kotlin
open class MyApplication : Application() {

    override fun onTerminate() {
        ...
    
        GlobalContextWrapper.unbindContext(applicationContext)

        super.onTerminate()
    }


}
```
