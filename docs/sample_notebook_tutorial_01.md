# Write a Notebook #1 - Create the database

This series of tutorials show you how to use **DevBricksX** to create a notebook application on Android. In this chapter, you will see how to create core data objects and store them in local database persistently.

## Preparation
Before we starting to write any codes, you need to make sure you have configured the libraries properly.

Add the following dependencies in build.gradle of your application.

#### Dependencies

```groovy
repositories { 
    mavenCentral()
}

dependencies {
    implementation "cn.dailystudio:devbricksx:$devbricksx_version"
    implementation "cn.dailystudio:devbricksx-java-annotations:$devbricksx_version"
    implementation "cn.dailystudio:devbricksx-kotlin-annotations:$devbricksx_version"

    kapt "cn.dailystudio:devbricksx-java-compiler:$devbricksx_version"
    kapt "cn.dailystudio:devbricksx-kotlin-compiler:$devbricksx_version"
    kapt "androidx.room:room-compiler:2.5.2"
}
```

#### Latest version

```groovy
devbricksx_version = "1.8.5"
```

Synchronizing the project configurations, and then let's move to the codes.

## Your first line of the code
In the notebook application, we have two different kinds of objects, **Note** and **Notebook**.

- **Notebook** is a collection of notes which are in the same category. Generally, it has a unique identifier and a display name. 

- **Note** is a piece of document you want to save. It contains text, pictures, attachments, etc. But to simplify our implementation, we only support pure text note in this sample.
	
Now, let us define our object with some codes. Since we tend to save notes and notebooks permanently in the database, we can use **RoomCompanion** to help us generate boilerplate codes.

### 1.Notebook
Here is the definition of **Notebook** object:

```kotlin
@RoomCompanion(primaryKeys = ["id"],
    autoGenerate = true,
    converters = [DateConverter::class],
    extension = NotebookDaoExtension::class,
    database = "notes",
)
class Notebook(id: Int = 0) : Record(id) {
    @JvmField var name: String? = null
}

```
Now, you can create **Notebook** objects in your application and save them into the database. 

> You might notice that the **Notebook** derives from a base class **Record**. **Record** is a pre-defined class in **DevBricksX** which is usually used as a superclass of an object with timestamp properties, e.g *created* or *lastModified*.

A **RoomCompanion** annotation is attached to the class as we need the **DevBricksX** annotation processor to generate some companion codes. Here, let us go through each parameter of the annotation:

- **primaryKeys**

	This parameter indicates **Room** library to use property "id" of Notebook as the primary key of each notebook record.

- **autoGenerate**

	This parameter tells **Room** library to generate "id" property automatically with an increasing integer.

- **converters**

	The parent class of **Notebook** is **Record**. Its member field, created and lastModified, is defined with **Date** type. **Room** library needs converters to convert them into supported data types before storing. **DateConverter** is a pre-defined utility to convert **Date** objects to **Long**.

- **extension**

	We intend to display the notebooks in reverse chronological order. Default interfaces of generated **Dao** class do not have such a function. We have to do it by ourselves.

- **database**

	By default, **Notebook** objects will be saved into a dedicated database named **notebook.db**. But, in our application, we need to store them in the same database as **Note**. So, we need to specify the database parameter.

### 2.Note
Here is the definition of **Note** object:

```kotlin
@RoomCompanion(primaryKeys = ["id"],
    autoGenerate = true,
    extension = NoteDaoExtension::class,
    database = "notes",
    foreignKeys = [ ForeignKey(entity = Notebook::class,
            parentColumns = ["id"],
            childColumns = ["notebook_id"],
            onDelete = ForeignKey.CASCADE
    )]
)
class Note(id: Int = 0) : Record(id) {
    @JvmField var notebook_id: Int = -1
    @JvmField var title: String? = null
    @JvmField var desc: String? = null
}

```
Then, you can create **Note** objects in your application and save them into the database. 

Same as **Notebook** class, **Note** also derives from **Record** and is annotated by **RoomCompanion** with the same parameters. The only difference is that **Note** class has one more parameter in **RoomCompanion** than **Notebookt**:


- **foreignKeys**

	It uses [ForeignKey](https://developer.android.com/reference/android/arch/persistence/room/ForeignKey), an annotation defined in Room annotation library, to describe the relationship with **Notebook**. In our application, each **Note** has a property "notebook_id" that refers to the identifier of the **Notebook** it belongs to.
	

## Magic happens
After we have defined this two classes with **RoomCompanion** annotation, without any extra codes, you can use Room library to save **Notebook** and **Note** in your main activity:

```kotlin
val notebooks = arrayOf(
        "Games", "Tech",
        "Books", "Office",
        "Home"
)
val maxNotes = 5

val database = NotesDatabase.getDatabase(
        this@MainActivity)

val notes = mutableListOf<Note>()
for ((i, displayName) in notebooks.withIndex()) {
    val notebookId = i + 1
    val nb = Notebook(notebookId).apply {
        name = displayName
        created = Date()
        lastModified = created
    }

    notes.clear()
    for (j in 0 until maxNotes) {
        val noteId = notebookId * maxNotes + j + 1

        notes.add(Note(noteId).apply {
            notebook_id = notebookId
            title = "$displayName $j"
            desc = "Write something for $displayName $j"

            created = Date()
            lastModified = created
        })
    }

    database.notebookDao().insertOrUpdate(nb)
    database.noteDao().insertOrUpdate(notes)
}

```
In the codes above, we create five notebooks and also add five notes with sample text in each notebook.

If you don't look into the codes of annotation processors, you will never know what happened. **NotesDatabase**, **NotebookDao**, and **NoteDao** are generated by annotation processors.

## Summary
As you have seen in this article, the annotation processor in **DevBricksX** can help you generate Room relative stuff. Next time, I will show how to generate code for user interfaces. 
