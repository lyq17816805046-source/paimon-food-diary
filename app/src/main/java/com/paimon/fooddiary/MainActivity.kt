package com.paimon.fooddiary

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private var currentTag: String = TAG_DIARY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DiaryStore.seedIfFirstRun(this)

        val bottom = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_diary -> { switchTo(TAG_DIARY, DiaryFragment()); true }
                R.id.nav_stats -> { switchTo(TAG_STATS, StatsFragment()); true }
                else -> false
            }
        }

        // 派蒙才不是应急食品！长按标题看看反应
        findViewById<View>(R.id.toolbar).setOnLongClickListener {
            Snackbar.make(it, getString(R.string.not_emergency_food), Snackbar.LENGTH_LONG)
                .setAction("哼！", null).show()
            true
        }
    }

    private fun switchTo(tag: String, fragment: Fragment) {
        if (tag == currentTag) return
        val fm = supportFragmentManager
        val tx = fm.beginTransaction()
        fm.findFragmentByTag(currentTag)?.let { tx.hide(it) }
        val existing = fm.findFragmentByTag(tag)
        if (existing == null) tx.add(R.id.fragment_holder, fragment, tag) else tx.show(existing)
        tx.commit()
        currentTag = tag
    }

    fun openEditor(id: Long) {
        startActivity(Intent(this, EditEntryActivity::class.java).putExtra(EditEntryActivity.EXTRA_ID, id))
    }

    override fun onResume() {
        super.onResume()
        supportFragmentManager.findFragmentByTag(currentTag)?.let {
            if (it is DiaryFragment) it.refresh()
        }
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    companion object {
        private const val TAG_DIARY = "diary"
        private const val TAG_STATS = "stats"
    }
}
