package com.dailystudio.devbricksx.compiler

class GeneratedNames {
    companion object {
        private const val VIEW_MODEL_SUFFIX = "ViewModel"

        fun getViewModelName(className: String) : String {
            return buildString {
                this.append(className)
                this.append(VIEW_MODEL_SUFFIX)
            }
        }
    }
}