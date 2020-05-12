# Devbricks X Kotlin Annoations
This library provides annotations that simplify your development work with **ViewModel**, **Fragement**, etc.

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
    implementation "com.dailystudio:devbricksx:$devbricksx_version"
    implementation "com.dailystudio:devbricksx-kotlin-annotations:$devbricksx_version"

    kapt "com.dailystudio:devbricksx-kotlin-compiler:$devbricksx_version"

    // (Optional) Annotations and processors to generate low-level facilities, such as Dao, Database, etc. 
    implementation "com.dailystudio:devbricksx-java-annotations:$devbricksx_version"
    kapt "com.dailystudio:devbricksx-java-compiler:$devbricksx_version"
    kapt "androidx.room:room-compiler:2.2.5"
}
```

#### Latest version

```groovy
devbricksx_version = "0.6.2"
```

If you want to auto-generate low-level facilities of an object class, such as **Dao**, **Database**, etc., DO NOT forget to include devbricksx-java-anntations and related compiler. See [devbricksx-java-annotations](../devbricksx-java-annotations/README.md) for more information.

## Simple Usage

Let's base on the **User** class we define in [devbricks-java-annotations](../devbricksx-java-annotations/README.md). Now we want to create an user interface for **User** class. It represents as a list of all the users that are stored in the database:

```kotlin
@RoomCompanion(primaryKeys = ["uid"],
        autoGenerate = true,
        extension = UserDaoExtension::class
)
@ViewModel
@Adapter(viewHolder = UserViewHolder::class)
@ListFragment
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

class UserViewHolder(itemView: View): AbsSingleLineViewHolder<User>(itemView) {

    override fun getIcon(item: User): Drawable? {
        return ResourcesCompatUtils.getDrawable(itemView.context,
                R.mipmap.ic_launcher)
    }

    override fun getText(item: User): CharSequence? {
        return buildString {
            append(item.firstName)
            append('.')
            append(item.lastName)
        }
    }

}
```
With a few lines of codes, you get a fragment named **UsersFragment**. You only need to add three annotations @ViewModel, @Adapter and @ListFragment on **User** class and create a UserViewHolder class to map the data fields into the UI elements.
