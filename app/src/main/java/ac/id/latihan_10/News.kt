package ac.id.latihan_10

import java.io.Serializable

data class News(
    var id: String? = null,
    val title: String = "",
    val short_desc: String = "",
    val desc: String = "",
    val img: String = ""
): Serializable
