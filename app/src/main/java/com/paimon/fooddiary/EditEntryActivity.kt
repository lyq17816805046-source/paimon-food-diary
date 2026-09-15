package com.paimon.fooddiary

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EditEntryActivity : AppCompatActivity() {

    private var entryId: Long = -1L
    private var createdAt: Long = 0L
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        title = getString(R.string.edit_title)

        val nameBox = findViewById<EditText>(R.id.edit_name)
        val noteBox = findViewById<EditText>(R.id.edit_note)
        val spinner = findViewById<Spinner>(R.id.edit_category)
        val ratingBar = findViewById<RatingBar>(R.id.edit_rating)
        val favToggle = findViewById<TextView>(R.id.edit_fav)
        val timeView = findViewById<TextView>(R.id.edit_time)
        val save = findViewById<Button>(R.id.btn_save)
        val del = findViewById<Button>(R.id.btn_delete)

        spinner.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, Categories.ALL
        )

        entryId = intent.getLongExtra(EXTRA_ID, -1L)
        val existing = if (entryId > 0) DiaryStore.find(this, entryId) else null
        if (existing != null) {
            nameBox.setText(existing.name)
            noteBox.setText(existing.note)
            ratingBar.rating = existing.rating.toFloat()
            spinner.setSelection(Categories.ALL.indexOf(existing.category).coerceAtLeast(0))
            createdAt = existing.time
            isFavorite = existing.favorite
            del.visibility = android.view.View.VISIBLE
            del.setOnClickListener { confirmDelete(existing) }
        } else {
            entryId = System.currentTimeMillis()
            isFavorite = false
            createdAt = System.currentTimeMillis()
            del.visibility = android.view.View.GONE
        }
        favToggle.text = favText(isFavorite)
        favToggle.setOnClickListener {
            isFavorite = !isFavorite
            favToggle.text = favText(isFavorite)
        }

        timeView.text = getString(R.string.record_time, android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", createdAt))

        save.setOnClickListener {
            val name = nameBox.text.toString().trim()
            if (name.isEmpty()) {
                nameBox.error = getString(R.string.need_name)
                return@setOnClickListener
            }
            val entry = FoodEntry(
                id = entryId,
                name = name,
                category = spinner.selectedItem?.toString() ?: Categories.OTHER,
                rating = ratingBar.rating.toInt().coerceIn(1, 5),
                note = noteBox.text.toString().trim(),
                time = createdAt,
                favorite = isFavorite
            )
            DiaryStore.upsert(this, entry)
            finish()
        }
    }

    private fun favText(on: Boolean): String =
        if (on) getString(R.string.fav_on) else getString(R.string.fav_off)

    private fun confirmDelete(entry: FoodEntry) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_title)
            .setMessage(getString(R.string.delete_message, entry.name))
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ ->
                DiaryStore.delete(this, entry.id)
                finish()
            }
            .show()
    }

    companion object {
        const val EXTRA_ID = "entry_id"
    }
}
