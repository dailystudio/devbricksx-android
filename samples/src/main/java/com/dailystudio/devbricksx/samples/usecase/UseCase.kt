package com.dailystudio.devbricksx.samples.usecase

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import com.dailystudio.devbricksx.GlobalContextWrapper
import com.dailystudio.devbricksx.annotations.*
import com.dailystudio.devbricksx.annotations.data.InMemoryCompanion
import com.dailystudio.devbricksx.annotations.data.Ordering
import com.dailystudio.devbricksx.annotations.fragment.DataSource
import com.dailystudio.devbricksx.annotations.fragment.ListFragment
import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.inmemory.InMemoryObject
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.ui.AbsSingleLineViewHolder
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type


@InMemoryCompanion(ordering = Ordering.Ascending)
@Adapter(viewHolder = UseCaseViewHolder::class)
@ViewModel
@ListFragment(
    layout = R.layout.fragment_use_case,
    dataSource = DataSource.Flow
)
data class UseCase(val name: String,
                   val `package`: String,
                   val title: String,
                   val icon: Int,
                   val desc: String) : InMemoryObject<String>, Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString()?: "",
        parcel.readString()?: "",
        parcel.readInt(),
        parcel.readString()?: "",
    ) {
    }

    override fun getKey(): String {
        return name
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(`package`)
        parcel.writeString(title)
        parcel.writeInt(icon)
        parcel.writeString(desc)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UseCase> {
        override fun createFromParcel(parcel: Parcel): UseCase {
            return UseCase(parcel)
        }

        override fun newArray(size: Int): Array<UseCase?> {
            return arrayOfNulls(size)
        }
    }

}

class UseCaseJsonDeserializer : JsonDeserializer<UseCase> {

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type?,
                             context: JsonDeserializationContext?): UseCase {
        val jsonObject = json.asJsonObject
        Logger.debug("jsonObject: $jsonObject")

        var icon: Int = 0

        val context: Context? = GlobalContextWrapper.context
        context?.let {
            icon = it.resources.getIdentifier(
                    jsonObject["icon"].asString,
                    "mipmap",
                    it.packageName)
        }

        return UseCase(
                jsonObject["name"].asString,
                jsonObject["package"].asString,
                jsonObject["title"].asString,
                icon,
                jsonObject["desc"].asString)
    }

}

class UseCaseViewHolder(itemView: View): AbsSingleLineViewHolder<UseCase>(itemView) {

    override fun getIcon(item: UseCase): Drawable? {
        return ResourcesCompatUtils.getDrawable(
                itemView.context, item.icon)
    }

    override fun getText(item: UseCase): CharSequence? {
        return item.title
    }

}
