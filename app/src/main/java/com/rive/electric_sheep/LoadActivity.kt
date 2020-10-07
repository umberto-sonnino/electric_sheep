package com.rive.electric_sheep

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
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

interface LoadFinishedDelegate {
    fun onLoadingFinished()
}

class LoadActivity : AppCompatActivity(), LoadFinishedDelegate {
    private lateinit var switch: Switch
    private lateinit var animationView: ElectricSheep
    private lateinit var listView: ListView
    private lateinit var layout: LinearLayout

    val bgColor = Color.parseColor("#3497DB")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layout = LinearLayout(this);
        layout.orientation = LinearLayout.VERTICAL

        val pad = 50
        layout.setPadding(pad, pad * 2, pad, 0)

        // Add the Sheep Animation
        val androidSheep = resources.openRawResource(R.raw.android_sheep)
        val bytes = androidSheep.readBytes()
        animationView = ElectricSheep(bytes, this)
        animationView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            1200
        )

        // Setup Switch that toggles animation visibility.
        switch = Switch(this)
        val switchParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        switchParams.gravity = Gravity.CENTER_HORIZONTAL
        switchParams.setMargins(30, 100, 30, 30)
        switch.layoutParams = switchParams

        // Setup ListView that'll become visible at the end of the animation.
        listView = ListView(this)
        val cards = Array<String>(10) { "$it" }
        listView.adapter = CardAdapter(this, cards)
        val divider = ColorDrawable(Color.TRANSPARENT)
        listView.divider = divider
        listView.dividerHeight = 50
        listView.visibility = View.GONE

        // Add all the elements to the screen.
        layout.addView(animationView);
        layout.addView(listView)
        layout.addView(switch)
        layout.setBackgroundColor(bgColor)
        setContentView(layout)

        switch.setOnCheckedChangeListener { _, isChecked ->
            toggleFinished(isChecked)
        }
    }

    fun toggleFinished(isChecked: Boolean) {
        if (isChecked) {
            animationView.onComplete()
        }
    }

    override fun onLoadingFinished() {
        hideAnimation()
        showList()
    }

    private fun hideAnimation() {
        switch.visibility = View.GONE
        animationView.visibility = View.GONE

        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), bgColor, R.color.purple)
        colorAnimation.duration = 250
        colorAnimation.addUpdateListener {
            layout.setBackgroundColor(it.getAnimatedValue() as Int)
        }
        colorAnimation.start()
    }

    private fun showList() {
        listView.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(1500)
                .setListener(null)
        }
    }

}

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
