package com.dailystudio.devbricksx.compiler

import com.squareup.kotlinpoet.ClassName

class TypeNamesUtils {

    companion object {

        fun getAndroidViewModelTypeName(): ClassName {
            return ClassName.bestGuess("androidx.lifecycle.AndroidViewModel")
        }

        fun getApplicationTypeName(): ClassName {
            return ClassName.bestGuess("android.app.Application")
        }

    }
}