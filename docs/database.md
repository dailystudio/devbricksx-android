# Database
Database facilities in **DevBricks X** provide a more efficient way to convert between **In-Memory objects** and **SQLite database records**. 

Thanks to [Android Room Library](https://developer.android.com/topic/libraries/architecture/room), in recent years, Android developers can access the SQLite database and store application data in much easier approaches than a decade ago. Even though it provides a set of powerful utilities to map objects into database structures and reduce complexity, it still requires developers to write a lot of codes to make it works. **Entity**, **Dao**, and **Database** are three main components that every developer should know and need to create whenever you want to save an object persistently. Most of these codes are repeated in logical and some of them are even duplicated. 

Can we avoid writing these codes? Not completely, but to some extent, the answer is **YES**. 

**IMPORTANT: Before you move on, we assume that you have the basic knowledge of how to use the Android Room Library. Otherwise, please read  the [offical document](https://developer.android.com/training/data-storage/room) and then continue to the next section.**

## Simple Usage

How to setup your builde.gradle, see the installation section of [devbricksx-java-annotations](../devbricksx-java-annotations/README.md). Now, let's start with a concrete example. In the case, we have a User class, which is defined as below:

```kotlin
class User (val id: UUID, val name: String) {
    var firstName: String? = null
    var lastName: String? = null
    var age: Int = 0
    var phoneNumber: String? = null
    var paid: Boolean = false
}
```
Now, to save it into an SQLite Database with Android Room Library, we need to add **@Entity** annotation on this class and then create two extra classes **UserDao** and **UserDatabase**. These three classes will be bound together to achieve the whole process. 

With our facilities, we can get all these things done with only one annotation **@RoomCompanion**. Here is the code:

```kotlin
@RoomCompanion(primaryKeys = ["id"], converters = [UUIDConverter::class])
class User (
@JvmField val id: UUID,
@JvmField val name: String) {
    @JvmField var firstName: String? = null
    @JvmField var lastName: String? = null
    @JvmField var age: Int = 0
    @JvmField var phoneNumber: String? = null
    @JvmField var paid: Boolean = false
}
```
Then you will get two auto-generated classes **UserDao** and **UserDatabase** under the same package of **User**. Only two things you need to do are:

1. Add **@RoomCompanion** annotation on your class and set few parameters
2. Add **@JvmField** annotation on each field of the class to make it accessible from Java code

Now, to save a user object, you can use the following snappit:

```kotlin
  ...
  public Long saveUser(context: Context, user: User) {
        val userDatabase = UserDatabase.getDatabase(context)
        return userDatabase.userDao().insert(user)
  }
  ...
```
Congratulation! You just have saved about **100 lines codes** which you may copy & paste again for another data class.

## Supported Features
Currently, this library only support basic features of Android Room Library and we are improving it support more in the future:

- **Entity**: PrimaryKey, ColumnInfo, ForeignKey, Index
- **Dao**: Query, Delete, Update
- **Database**: Migration

## Generated interfaces
For an annotated class, like **User**, we generate two classes **UserDao** and **UserDatabase** automatically. At the same time, these two classes also include plenty of built-in interfaces for ease of use.

### 1. UserDao

Interfaces | Descriptions
:--        | :--
getAll()   | Retrieve all the users in a List   
getAllLive() | Retrieve a LiveData of all the users
getAllLivePaged() | Retrieve a LiveData of PagedList of users
insert(user) | Insert a user
insert(users) | Insert a list of users
update(user) | Update a user
update(users) | Update a list of users
insertOrUpdate(user) | Insert a user or update if it exists
insertOrUpdate(users) | Insert a list of users or update them if they are alreay existed
delete(user) | Delete a user

### 2. UserDatabase
Interfaces | Descriptions
:--        | :--
getDatabase(context) | Get a singlinton instance of user database
getDatabase(context, migrations) | Get a singlinton instance of user database with database mirgrations support

We do not prevent developer to extend the interfaces of these two classes. For more information, please refer to the document of [devbricksx-java-annotations](../devbricksx-java-annotations/README.md). 

## Limitations
1. We are basing on the Android Room Library rather than rewriting it, generated codes also need to be compiled with the Room compiler. Do not forget the following line in your build.gradle, since **kapt** cannot be inherited from Gradle scripts in the library project.
    
    ```groovy
        kapt "androidx.room:room-compiler:2.4.3"
    ```

2. *[The latest version of Kapt (Kotlin Annotation Processor) does not support multiple rounds for the generated Kotlin files](https://kotlinlang.org/docs/reference/kapt.html#generating-kotlin-sources)*. Since we need Room compiler to compile our codes for second round, the codes generated by this library are all in **Java** format. For more information, please refer to the document of [devbricksx-java-annotations](../devbricksx-java-annotations/README.md). 

