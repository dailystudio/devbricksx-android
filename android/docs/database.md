# Database
Database facilities in DevBricks X provides a more efficient way to convert between **In-Memory objects** and **SQLite database records**. 

Thanks to [Android Room](https://developer.android.com/topic/libraries/architecture/room), in recent years, Android developers can access SQLite database and store application data in much easier approaches than a decade ago. Even though it provides a set of powerful utilites to map objects into database structures and recude the complexity, it still requires developers to write a lot of codes to make it works. **Entity**, **Dao** and **Database** are three main components that every developer should know and need to create everythime when you want to save an object persistently. Most of these codes are repeated in logical and some of them are even duplicated. 

Can we avoid writing these codes? Not completely, but to some extend, the answer is **YES**. 

## Simple Usage

Let's start with a concrete example. In the case, we have a user object, which is defined as below:

```kotlin
class User (val id: UUID, val name: String) {
    var firstName: String? = null
    var lastName: String? = null
    var age: Int = 0
    var phoneNumber: String? = null
    var paid: Boolean = false
}
```
Now, to save it into a SQLite Database with Android Room Library, we need to add **@Entity** annotation on this class and then create two extra classes **UserDao** and **UserDatabase**. These three class will be bound together to archieve the whole process. 

With our facilities, we can get all these things done with one annotation @RoomCompanion. Here is the code:

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

1. Add @RoomCompanion annotation on your class and set few parameters
2. Add @JvmField annotation on each field of the class to make it accessible from Java code

To save a user object, you can use the following snappit:

```kotlin
  ...
  public Long saveUser(context: Context, user: User) {
        val userDatabase = UserDatabase.getDatabase(context)
        return userDatabase.userDao().insert(user)
  }
  ...
```
Everyting is done. You just have saved about **100 lines codes** which you may copy & paste again for another data class.

## Generated interfaces
For an object, e.g. **User**, we generate two classes UserDao and UserDatabase automatically. At the same time, these two classes also includes built-in interfaces for ease of use.

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

We do not prevent developer to extend the interfaces of these two classes. For more information, please refer to the document of [devbricks-java-compiler](). 

## Limitations
1. Since we are basing on the Android Room Library rather than rewriting it, generated codes also need to be compiled with Room compiler. Do not forget the following line in your build.gradle:
	
	```groovy
	    kapt "androidx.room:room-compiler:2.2.5"
	```

2. *[The latest version of Kapt (Kotlin Annotation Processor) does not support multiple rounds for the generated Kotlin files](https://kotlinlang.org/docs/reference/kapt.html)*. The means the codes generated which is related with are in **Java** format. For more information, please refer to the document of [devbricks-java-compiler](). 

