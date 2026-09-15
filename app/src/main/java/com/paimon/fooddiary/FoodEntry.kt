package com.paimon.fooddiary

import org.json.JSONObject

/** 一条美食记录 */
data class FoodEntry(
    val id: Long,
    var name: String,
    var category: String,
    var rating: Int,
    var note: String,
    var time: Long,
    var favorite: Boolean = false
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("name", name)
        put("category", category)
        put("rating", rating)
        put("note", note)
        put("time", time)
        put("favorite", favorite)
    }

    companion object {
        fun fromJson(o: JSONObject): FoodEntry = FoodEntry(
            id = o.optLong("id"),
            name = o.optString("name"),
            category = o.optString("category", Categories.OTHER),
            rating = o.optInt("rating", 3),
            note = o.optString("note"),
            time = o.optLong("time"),
            favorite = o.optBoolean("favorite")
        )
    }
}

object Categories {
    const val MAIN = "主食"
    const val SNACK = "小吃"
    const val SOUP = "汤羹"
    const val SWEET = "甜品"
    const val DRINK = "饮品"
    const val RARE = "奇珍"
    const val OTHER = "其他"

    val ALL = listOf(MAIN, SNACK, SOUP, SWEET, DRINK, RARE, OTHER)

    fun emoji(category: String): String = when (category) {
        MAIN -> "🍗"
        SNACK -> "🍢"
        SOUP -> "🍲"
        SWEET -> "🍰"
        DRINK -> "🥤"
        RARE -> "✨"
        else -> "🍽️"
    }
}
