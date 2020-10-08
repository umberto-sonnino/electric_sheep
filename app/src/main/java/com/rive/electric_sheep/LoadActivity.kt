package com.rive.electric_sheep

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

// This is a delegate class that can relay events in the ElectricSheep class.
interface LoadDelegate {
    fun onLoadingFinished()
    fun onLoadingStarted()
}

class LoadActivity : AppCompatActivity(), LoadDelegate {
    private lateinit var switch: Switch
    private lateinit var animationView: ElectricSheep
    private lateinit var listView: ListView
    private lateinit var layout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layout = LinearLayout(this);
        layout.orientation = LinearLayout.VERTICAL
        layout.setBackgroundColor(ContextCompat.getColor(this, R.color.background))
        val padding = 50
        layout.setPadding(padding, padding * 2, padding, 0)

        setupAnimation()
        setupSwitch()
        setupList()

        layout.addView(animationView);
        layout.addView(switch)
        layout.addView(listView)
        setContentView(layout)
    }

    // Gets triggered in the ElectricSheep class, when the 'start' animation has completed.
    override fun onLoadingStarted() {
        switch.isEnabled = true
        switch.setOnCheckedChangeListener { _, isChecked ->
            toggleFinished(isChecked)
        }
    }

    // Gets triggered in the ElectricSheep class, when the 'end' animation has completed.
    override fun onLoadingFinished() {
        // Hide animation & switch
        switch.visibility = View.GONE
        animationView.visibility = View.GONE

        showList()
    }

    private fun showList() {
        listView.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(1000)
                .setListener(null)
        }
    }

    private fun toggleFinished(isChecked: Boolean) {
        if (isChecked) {
            animationView.onComplete()
        }
    }

    private fun setupSwitch() {
        // Setup the Switch that toggles the completed state of the animation.
        // Initially this Switch is disabled, and becomes enabled when the animation has
        //  completed its brief starting animation.
        switch = Switch(this)
        switch.isEnabled = false
        val switchParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        switchParams.gravity = Gravity.CENTER_HORIZONTAL
        switchParams.setMargins(30, 100, 30, 30)
        switch.layoutParams = switchParams
    }

    private fun setupAnimation() {
        // Add the Sheep Animation
        // This animation has three states:
        // - Start
        // - Loading
        // - End
        val androidSheep = resources.openRawResource(R.raw.android_sheep)
        val bytes = androidSheep.readBytes()
        animationView = ElectricSheep(bytes, this)
        animationView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            1200
        )
    }

    private fun setupList() {
        // Setup a list of elements that'll become visible at the end of the animation.
        // It'll be invisible at first, but its visibility will change once
        //  onLoadingFinished() is triggered.
        listView = ListView(this)
        val cards = Array<String>(10) { "$it" }
        listView.adapter = CardAdapter(this, cards)
        val divider = ColorDrawable(Color.TRANSPARENT)
        listView.divider = divider
        listView.dividerHeight = 50
        listView.visibility = View.GONE
    }
}

// ListView adapter for showing cards.
class CardAdapter(
    private val context: Context,
    private val dataSource: Array<String>
) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(p0: Int): Any {
        return dataSource[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.list_row, parent, false)
        return rowView
    }
}
