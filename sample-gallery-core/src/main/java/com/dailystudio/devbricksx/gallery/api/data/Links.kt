package com.dailystudio.devbricksx.gallery.api.data

data class Links(
    var first: String? = null,
    var prev: String? = null,
    var next: String? = null,
    var last: String? = null,
) {

    companion object {
        fun fromString(str: String?): Links {
            val linksStr = str?: return Links()

            val parts = linksStr.split(",")

            return Links().apply {
                for (p in parts) {
                    when {
                        p.endsWith("; rel=\"first\"") -> {
                            first = p.removeSuffix("; rel=\"first\"")
                                .replace("<", "")
                                .replace(">", "")
                        }
                        p.endsWith("; rel=\"last\"") -> {
                            last = p.removeSuffix("; rel=\"last\"")
                                .replace("<", "")
                                .replace(">", "")
                        }
                        p.endsWith("; rel=\"next\"") -> {
                            next = p.removeSuffix("; rel=\"next\"")
                                .replace("<", "")
                                .replace(">", "")
                        }
                        p.endsWith("; rel=\"prev\"") -> {
                            prev = p.removeSuffix("; rel=\"prev\"")
                                .replace("<", "")
                                .replace(">", "")
                        }
                    }
                }
            }
        }
    }
}