package com.paimon.fooddiary

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.random.Random

class DiaryFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var search: EditText
    private lateinit var emptyView: TextView
    private lateinit var countView: TextView
    private lateinit var filterChip: TextView
    private var adapter: EntryAdapter? = null
    private var all: List<FoodEntry> = emptyList()
    private var categoryFilter: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?): View =
        inflater.inflate(R.layout.fragment_diary, container, false)

    override fun onViewCreated(view: View, saved: Bundle?) {
        recycler = view.findViewById(R.id.recycler)
        search = view.findViewById(R.id.search_box)
        emptyView = view.findViewById(R.id.empty_view)
        countView = view.findViewById(R.id.count_view)
        filterChip = view.findViewById(R.id.filter_chip)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        adapter = EntryAdapter(emptyList(), ::openEntry, ::toggleFavorite, ::askDelete)
        recycler.adapter = adapter

        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) { applyFilter() }
            override fun afterTextChanged(s: Editable?) {}
        })

        filterChip.setOnClickListener {
            categoryFilter = null
            filterChip.text = getString(R.string.all_categories)
            applyFilter()
        }

        view.findViewById<FloatingActionButton>(R.id.fab_add).setOnClickListener {
            (activity as? MainActivity)?.openEditor(-1L)
        }

        view.findViewById<ExtendedFloatingActionButton>(R.id.fab_random).setOnClickListener {
            askPaimon()
        }

        view.findViewById<TextView>(R.id.chip_category_picker).setOnClickListener { showCategoryPicker() }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    fun refresh() {
        if (!::recycler.isInitialized) return
        all = DiaryStore.load(requireContext())
        applyFilter()
    }

    private fun applyFilter() {
        val keyword = if (::search.isInitialized) search.text.toString().trim().lowercase() else ""
        val filtered = all.filter { entry ->
            val okCat = categoryFilter == null || entry.category == categoryFilter
            val okKey = keyword.isEmpty() ||
                entry.name.lowercase().contains(keyword) ||
                entry.note.lowercase().contains(keyword) ||
                entry.category.lowercase().contains(keyword)
            okCat && okKey
        }
        adapter?.submit(filtered)
        countView.text = getString(R.string.count_format, filtered.size, all.size)
        emptyView.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        emptyView.setText(if (all.isEmpty()) R.string.empty_no_data else R.string.empty_no_match)
    }

    private fun showCategoryPicker() {
        val names = (listOf(getString(R.string.all_categories)) + Categories.ALL).toTypedArray()
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.pick_category)
            .setItems(names) { dialog, which ->
                if (which == 0) {
                    categoryFilter = null
                    filterChip.text = getString(R.string.all_categories)
                } else {
                    categoryFilter = Categories.ALL[which - 1]
                    filterChip.text = getString(R.string.filter_format, Categories.ALL[which - 1])
                }
                applyFilter()
                dialog.dismiss()
            }
            .show()
    }

    private fun openEntry(entry: FoodEntry) {
        (activity as? MainActivity)?.openEditor(entry.id)
    }

    private fun toggleFavorite(entry: FoodEntry) {
        entry.favorite = !entry.favorite
        DiaryStore.upsert(requireContext(), entry)
        refresh()
    }

    private fun askDelete(entry: FoodEntry) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_title)
            .setMessage(getString(R.string.delete_message, entry.name))
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ ->
                DiaryStore.delete(requireContext(), entry.id)
                refresh()
            }
            .show()
    }

    /** 派蒙推荐：今天吃什么，交给命运！ */
    private fun askPaimon() {
        val pool = all.ifEmpty {
            DiaryStore.presets().map {
                FoodEntry(0, it.first, it.second, 5, it.third, System.currentTimeMillis())
            }
        }
        val pick = pool[Random.nextInt(pool.size)]
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.paimon_pick_title)
            .setMessage(
                Categories.emoji(pick.category) + "  " + pick.name + "\n\n" +
                    pick.note.ifBlank { getString(R.string.paimon_pick_hint) }
            )
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.paimon_pick_save) { _, _ ->
                DiaryStore.upsert(
                    requireContext(),
                    FoodEntry(
                        id = System.currentTimeMillis(),
                        name = pick.name,
                        category = pick.category,
                        rating = pick.rating,
                        note = pick.note,
                        time = System.currentTimeMillis()
                    )
                )
                refresh()
            }
            .show()
    }
}
