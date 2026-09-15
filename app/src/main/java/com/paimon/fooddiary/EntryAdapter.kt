package com.paimon.fooddiary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EntryAdapter(
    private var items: List<FoodEntry>,
    private val onClick: (FoodEntry) -> Unit,
    private val onFavorite: (FoodEntry) -> Unit,
    private val onDelete: (FoodEntry) -> Unit
) : RecyclerView.Adapter<EntryAdapter.VH>() {

    private val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val icon: TextView = view.findViewById(R.id.entry_icon)
        val name: TextView = view.findViewById(R.id.entry_name)
        val category: TextView = view.findViewById(R.id.entry_category)
        val stars: TextView = view.findViewById(R.id.entry_stars)
        val note: TextView = view.findViewById(R.id.entry_note)
        val date: TextView = view.findViewById(R.id.entry_date)
        val fav: ImageButton = view.findViewById(R.id.entry_fav)
        val del: ImageButton = view.findViewById(R.id.entry_del)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_entry, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.icon.text = Categories.emoji(item.category)
        holder.name.text = item.name
        holder.category.text = item.category
        holder.stars.text = stars(item.rating)
        holder.note.text = item.note.ifBlank { holder.itemView.context.getString(R.string.no_note) }
        holder.date.text = fmt.format(Date(item.time))
        holder.fav.setImageResource(if (item.favorite) R.drawable.ic_star_filled else R.drawable.ic_star_outline)
        holder.fav.contentDescription = holder.itemView.context.getString(R.string.favorite)
        holder.itemView.setOnClickListener { onClick(item) }
        holder.fav.setOnClickListener { onFavorite(item) }
        holder.del.setOnClickListener { onDelete(item) }
    }

    override fun getItemCount(): Int = items.size

    fun submit(newItems: List<FoodEntry>) {
        items = newItems
        notifyDataSetChanged()
    }

    private fun stars(rating: Int): String = "★".repeat(rating.coerceIn(0, 5)) + "☆".repeat((5 - rating).coerceIn(0, 5))
}
