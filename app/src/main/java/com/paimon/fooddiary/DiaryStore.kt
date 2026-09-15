package com.paimon.fooddiary

import android.content.Context
import org.json.JSONArray

/** 本地存储：全部数据都存在 SharedPreferences 里，不联网、不上传 */
object DiaryStore {
    private const val PREF = "paimon_diary"
    private const val KEY_ENTRIES = "entries"
    private const val KEY_SEEDED = "seeded"

    private fun prefs(ctx: Context) =
        ctx.applicationContext.getSharedPreferences(PREF, Context.MODE_PRIVATE)

    fun load(ctx: Context): MutableList<FoodEntry> {
        val raw = prefs(ctx).getString(KEY_ENTRIES, "[]") ?: "[]"
        val list = mutableListOf<FoodEntry>()
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                list.add(FoodEntry.fromJson(arr.getJSONObject(i)))
            }
        } catch (e: Exception) {
            // 数据坏了就当没有，别崩溃
        }
        list.sortByDescending { it.time }
        return list
    }

    fun save(ctx: Context, entries: List<FoodEntry>) {
        val arr = JSONArray()
        entries.forEach { arr.put(it.toJson()) }
        prefs(ctx).edit().putString(KEY_ENTRIES, arr.toString()).apply()
    }

    fun upsert(ctx: Context, entry: FoodEntry) {
        val list = load(ctx)
        val idx = list.indexOfFirst { it.id == entry.id }
        if (idx >= 0) list[idx] = entry else list.add(entry)
        list.sortByDescending { it.time }
        save(ctx, list)
    }

    fun delete(ctx: Context, id: Long) {
        save(ctx, load(ctx).filterNot { it.id == id })
    }

    fun find(ctx: Context, id: Long): FoodEntry? = load(ctx).firstOrNull { it.id == id }

    /** 第一次打开时，给旅行者塞几道提瓦特名菜当例子 */
    fun seedIfFirstRun(ctx: Context) {
        val p = prefs(ctx)
        if (p.getBoolean(KEY_SEEDED, false)) return
        val now = System.currentTimeMillis()
        val seeds = presets().mapIndexed { i, it ->
            FoodEntry(
                id = now - i,
                name = it.first,
                category = it.second,
                rating = 5 - (i % 3),
                note = it.third,
                time = now - i * 1000L,
                favorite = i == 0
            )
        }
        save(ctx, seeds)
        p.edit().putBoolean(KEY_SEEDED, true).apply()
    }

    fun presets(): List<Triple<String, String, String>> = listOf(
        Triple("甜甜花酿鸡", Categories.MAIN, "派蒙心中的第一名！蜜汁裹着脆皮，一口就飞起来～"),
        Triple("摩拉肉", Categories.SNACK, "形状像摩拉，吃起来也像很有钱的味道。"),
        Triple("仙跳墙", Categories.SOUP, "大补的汤，冒险前喝一碗，树脂都不心疼了。"),
        Triple("文心豆腐", Categories.MAIN, "清清淡淡，璃月港的老味道。"),
        Triple("三彩团子", Categories.SWEET, "雷之国的甜点心，三种颜色三种心情。"),
        Triple("冰镇甜甜花", Categories.DRINK, "夏天就该来一杯，凉到心里那种。"),
        Triple("鸣神岛天妇罗", Categories.SNACK, "酥脆！不过派蒙才不是贪吃，是在做品鉴工作。"),
        Triple("黄金蟹", Categories.RARE, "蒙德海鲜馆的招牌，蟹黄能拌三碗饭。")
    )
}
