# Devbricks X Java Annotations

This library provides a set of annotations that simplify your development work with [Android Room Library](https://developer.android.com/topic/libraries/architecture/room). It helps you to generate core elements that are required by that library, such **Entity**, **Dao**, **Database**, etc. Generally, you can save around 100 lines of code per entity class.

## Installation
To use annotation and its compiler, add the following dependencies in build.gradle of your application.

#### Jcenter

```groovy
repositories { 
    jcenter()
}
```

#### Dependencies

```groovy
dependencies {
     implementation "com.dailystudio:devbricksx-java-annotations:$devbricksx_version"

     kapt "com.dailystudio:devbricksx-java-compiler:$devbricksx_version"
     kapt "androidx.room:room-compiler:2.2.5"
}
```

#### Latest version

```groovy
devbricksx_version = "0.7.5"
```

The codes generated by this library require Android Room Library to compile. DO NOT forget to include **room-compiler** in dependencies.

## Quick Sample

To quick start with the library, let's take **User** class as an example. It is the same one described in official document of **Android Room Library**. You can visit the [website](https://developer.android.com/training/data-storage/room) to the see the original version. Here is our version with **@RoomCampanion** and **@DaoExtention**:

```kotlin
@RoomCompanion(primaryKeys = ["uid"],
        autoGenerate = true,
        extension = UserDaoExtension::class
)
data class User(@JvmField val uid: Int,
                @JvmField val firstName: String?,
                @JvmField val lastName: String?)


@DaoExtension(entity = User::class)
interface UserDaoExtension {
    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Insert
    fun insertAll(vararg users: User)
}
```
Since the code generated in this library are Jave codes, **@JvmField** should be annotated on each file of the class. Otherwise, you will get compile errors with field accessibility issues in generated codes. **User** is the class that you want to be persistently stored in the database. **UserDaoExtension** is an interface that defines extended interfaces in class **UserDao**.

Compile your code and then you can use class **UserDao** and **UserDatabase** in your application. If you don't know how to use these two classes, please refer to the [Android Room Library](https://developer.android.com/topic/libraries/architecture/room) document for more information. 

## Annotations

### 1. RoomCompanion
It is the core annotation of this library and will help you generate companion classes that will be used by Android Room Library. Here is a complete example:

#### User.kt

```kotlin
@RoomCompanion(primaryKeys = ["id"], 
        autoGenerate = true,
        database = "user",
        extension = UserDaoExtension::class,
        converters = [UUIDConverter::class, DateConverter::class],
        foreignKeys = [ForeignKey(entity = Group::class,
                parentColumns = ["id"],
                childColumns = ["group_id"],
                onDelete = ForeignKey.CASCADE
        )],
        pageSize = 50
)
data class User (@JvmField val id: UUID, 
                 @JvmField val name: String) {
    @JvmField var firstName: String? = null
    @JvmField var lastName: String? = null
    @JvmField var age: Int = 0
    @JvmField var phoneNumber: String? = null
    @JvmField var paid: Boolean = false
    @JvmField var groupId: UUID? = null
}
```

#### Group.kt

```kotlin
@RoomCompanion(primaryKeys = ["id"],
        database = "user",
        indices = [ Index(value = ["name"])]
)
data class Group(@JvmField val id: UUID,
                 @JvmField val name: String) {

    @JvmField var createdTime: Date? = null
}
```
Here we defined two classes **User** and **Group** and their relationship. Both of them are using UUID as a primary key and a user might belong to a group through a foreign key constraint. 

#### Parameters

- **primaryKeys**
    
    Specify which fields in the class will be used as the primary keys of the database. 

- **autoGenerate**

    Indicate whether the primary key is generated automatically or not. Visit [here](https://developer.android.com/training/data-storage/room/defining-data#primary-key) for more details.
    
- **database**
    
    Define the name of the database in which the class will be stored. The classes with the same value of database parameter will be stored in the same database and share the same **Database** class.

- **extension**
    
    Specify a class which defines extended interfaces in the **Dao** class. It defines the interfaces in the same way as the one which is used to define interfaces in **Dao** class in the [official docuement](https://developer.android.com/training/data-storage/room) of Android Room Library.

- **converters**

    Specify a set of converter classes that will be used to convert the fields. Visit [here](https://developer.android.com/training/data-storage/room/referencing-data#type-converters) for more details. 
    
- **foreignKeys**

    Specify a set of foreign keys that will be used to build the relationship of classes in that same database. It uses the **@ForeignKey** annotation which is defined in Android Room Library. Visit [here](https://developer.android.com/reference/android/arch/persistence/room/ForeignKey) for more details. 

- **indices**

    Specify a set of fields that will be used to create indices on in the database. It uses the **@Index** annotation which is defined in Android Room Library.Visit [here](https://developer.android.com/training/data-storage/room/defining-data#column-indexing) for more details. 
    
- **repository**

    Indicate whether an auto-generated **Repository** class is required. By default, a Repository class will also be auto-generated with **Dao** and **Database**. If you do not need a **Repository**, you can set it to false. But it will be a bit complex when you are using our high-level annotations like **@ViewModel**.

- **pageSize**

    Define the page size of generated default interfaces that return a PagedList. Visit [here](https://developer.android.com/topic/libraries/architecture/paging) for more details. 


### 2. DaoExtension

By default, the generated **Dao** class includes the following interfaces:

Interfaces | Descriptions
:--        | :--
getAll()   | Retrieve all the objects in a List   
getAllLive() | Retrieve a LiveData of all the objects
getAllLivePaged() | Retrieve a LiveData of PagedList of objects
insert(object) | Insert a object
insert(objects) | Insert a list of objects
update(object) | Update a object
update(objects) | Update a list of objects
insertOrUpdate(object) | Insert a object or update if it exists
insertOrUpdate(objects) | Insert a list of objects or update them if they are alreay existed
delete(object) | Delete a object

This annotation is used to define an interface or abstract class that provides definitions of extended interfaces of **Dao** class.

#### Parameters

- **entity**
    
    Specify the class which this extension is used for.

### 3. Page

If an interface, which defined in the interface or class with a DaoExtension annotated, returns PageList. You should annotate it with this annotation and indicate the page size of the PageList. By default, the page size is 10.

#### Parameters

- **pageSize**

    Define the page size of the interface which returns a PagedList. Visit [here](https://developer.android.com/topic/libraries/architecture/paging) for more details. 