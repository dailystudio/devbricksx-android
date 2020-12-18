# Write a Notebook with DevBricksX

This tutorial shows you how to use **DevBricksX** to create a notebook application on Android.

## Preparation
Before we starting to write any codes, you need to make sure you have configure the libraries properly.

Add the following dependencies in build.gradle of your application.

#### Jcenter

```groovy
repositories { 
    jcenter()
}
```

#### Dependencies

```groovy
dependencies {
	 devbricksx_version = "1.2.9"
    
    implementation "com.dailystudio:devbricksx:$devbricksx_version"
    implementation "com.dailystudio:devbricksx-java-annotations:$devbricksx_version"
    implementation "com.dailystudio:devbricksx-kotlin-annotations:$devbricksx_version"

    kapt "com.dailystudio:devbricksx-java-compiler:$devbricksx_version"
    kapt "com.dailystudio:devbricksx-kotlin-compiler:$devbricksx_version"
    kapt "androidx.room:room-compiler:2.2.5"
}
```
Synchronizing the project configurations, and then let's move to the codes.

## Your first line of the code
In the notebook application, we have two different kind of objects, **Note** and **Notebook**.

- **Notebook** is a colletion of notes which are in the same category. Generally, it has an unique identifier and a display name. 

- **Note** is a piece of document you want to save. It contins text, pictures, attachments, etc. But for simplify our implementation, we only support pure text note in this sample.
	
Now, let us define our object with some codes. Since we tend to save notes and notebooks permenantly in the database, we can use **RoomCompanion** to help us generate boilerplate codes.

### 1.Notebook
Here is the definition of **Notebook** object:

```kotlin
@RoomCompanion(primaryKeys = ["id"],
        autoGenerate = true,
        converters = [DateConverter::class],
        extension = NotebookDaoExtension::class,
        database = "notes",
)
open class Notebook(id: Int = 0) : Record(id) {
    @JvmField var name: String? = null

    override fun toString(): String {
        return buildString {
            append("Notebook [$id]: $name")
        }
    }
}

```
Now, you can create Notebook objects in your application and save them into database. 

> You might notice that the **Notebook** derives from a base class **Record**. **Record** is a pre-defined class in **DevBricksX** which is usually used as super class of an object with timestamp properties, e.g *created* or *lastModified*.

A RoomCompanion annotation is attached to the class as we need annotation processor can assit us to generate some codes. Here, let us go through with each parameters of the annotation:

- **primaryKeys**

	This paramter indicates **Room** library to use property "id" of Notebook as the primary key of each notebook record.

- **autoGenerate**

	This paramter tells **Room** library to generate "id" proptery automatically with an increased integer.

- **converters**

	The parent class of **Notebook** is **Record**. Its member created and lastModified is defined with **Date** type. **Room** library needs converters to convert them into supported data types before storing. **DateConverter** is a pre-defined utility to convert **Date** objects to **Long**.

- **extension**

	We intend to display the notebooks in reverse chronological order. Default interfaces of generated **Dao** class does not have such a function. We have to do it by ourself.

- **database**

	By default, **Notebook** objects will be saved into a database named **notebook.db**



