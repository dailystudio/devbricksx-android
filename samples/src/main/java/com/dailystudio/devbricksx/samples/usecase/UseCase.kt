package com.dailystudio.devbricksx.samples.usecase

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import com.dailystudio.devbricksx.GlobalContextWrapper
import com.dailystudio.devbricksx.annotations.*
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


@InMemoryManager(key = String::class, ordering = Ordering.Ascending)
@InMemoryRepository(key = String::class)
@DiffUtil
@Adapter(viewHolder = UseCaseViewHolder::class)
@ViewModel
@ListFragment(layout = R.layout.fragment_use_case)
data class UseCase(val name: String,
                   val `package`: String,
                   val title: String,
                   val icon: Int,
                   val desc: String) : InMemoryObject<String> {

    override fun getKey(): String {
        return name
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
