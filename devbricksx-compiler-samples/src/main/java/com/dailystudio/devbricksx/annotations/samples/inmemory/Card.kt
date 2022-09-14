package com.dailystudio.devbricksx.annotations.samples.inmemory

import android.graphics.Color
import com.dailystudio.devbricksx.annotations.data.InMemoryCompanion
import com.dailystudio.devbricksx.annotations.data.Ordering
import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.inmemory.InMemoryObject
import java.util.*

@Adapter(viewHolder = Unit::class)
@ViewModel
@InMemoryCompanion(ordering = Ordering.Descending)
data class Card(val id: Int,
                val title: String,
                val desc: String? = null) : InMemoryObject<Int> {

    companion object {
        private val RAND : Random = Random(System.currentTimeMillis())
    }

    val color: Int = Color.argb(255,
        RAND.nextInt(255),
        RAND.nextInt(255),
        RAND.nextInt(255))

    override fun getKey(): Int {
        return id
    }

}

abstract class IncreasedIntKeyInMemoryObject: IntKeyInMemoryObject()
abstract class IntKeyInMemoryObject: InMemoryObject<Int>
//abstract class IntKeyInMemoryObject
