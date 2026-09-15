package com.paimon.fooddiary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment

class StatsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?): View =
        inflater.inflate(R.layout.fragment_stats, container, false)

    override fun onViewCreated(view: View, saved: Bundle?) {
        val ctx = requireContext()
        val list = DiaryStore.load(ctx)
        val total = view.findViewById<TextView>(R.id.stat_total)
        val avg = view.findViewById<TextView>(R.id.stat_avg)
        val fav = view.findViewById<TextView>(R.id.stat_fav)
        val latest = view.findViewById<TextView>(R.id.stat_latest)
        val best = view.findViewById<TextView>(R.id.stat_best)
        val bars = view.findViewById<LinearLayout>(R.id.stat_bars)
        val note = view.findViewById<TextView>(R.id.stat_note)

        total.text = list.size.toString()
        avg.text = if (list.isEmpty()) "0.0"
            else String.format("%.1f", list.map { it.rating }.average())
        fav.text = list.count { it.favorite }.toString()
        latest.text = list.firstOrNull()?.name ?: getString(R.string.none_yet)
        best.text = list.maxByOrNull { it.rating }?.name ?: getString(R.string.none_yet)

        bars.removeAllViews()
        if (list.isEmpty()) {
            note.visibility = View.VISIBLE
            return
        }
        note.visibility = View.GONE
        val max = list.groupingBy { it.category }.eachCount().values.maxOrNull() ?: 1
        Categories.ALL.forEach { cat ->
            val count = list.count { it.category == cat }
            if (count == 0) return@forEach
            val row = LayoutInflater.from(ctx).inflate(R.layout.item_stat_row, bars, false)
            row.findViewById<TextView>(R.id.row_label).text = Categories.emoji(cat) + " " + cat
            row.findViewById<TextView>(R.id.row_value).text = count.toString()
            row.findViewById<ProgressBar>(R.id.row_bar).max = max
            row.findViewById<ProgressBar>(R.id.row_bar).progress = count
            bars.addView(row)
        }
    }
}
