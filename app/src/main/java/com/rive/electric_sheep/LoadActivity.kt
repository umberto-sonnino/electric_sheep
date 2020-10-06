package com.rive.electric_sheep

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.text.Layout.JUSTIFICATION_MODE_INTER_WORD
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

interface LoadFinishedDelegate {
    fun onLoadingFinished()
}

class LoadActivity : AppCompatActivity(), LoadFinishedDelegate {
    lateinit var switch: Switch
    lateinit var animationView: ElectricSheep
    lateinit var loremIpsum: TextView
    lateinit var layout: LinearLayout

    val bgColor = Color.parseColor("#3497DB")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layout = LinearLayout(this);
        layout.orientation = LinearLayout.VERTICAL

        // Add the Sheep Animation
        val android_sheep = resources.openRawResource(R.raw.android_sheep)
        val bytes = android_sheep.readBytes()
        animationView = ElectricSheep(bytes, this)
        animationView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            1200
        )

        loremIpsum = TextView(this)
        loremIpsum.setText(R.string.lorem_ipsum)
        loremIpsum.visibility = View.GONE
        val textParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        textParams.setMargins(30, 30, 30, 0)
        loremIpsum.layoutParams = textParams
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            loremIpsum.justificationMode = JUSTIFICATION_MODE_INTER_WORD
        }

        switch = Switch(this)
        val switchParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        switchParams.gravity = Gravity.CENTER_HORIZONTAL
        switchParams.setMargins(30, 100, 30, 30)
        switch.layoutParams = switchParams


        layout.addView(animationView);
        layout.addView(loremIpsum)
        layout.addView(switch)
        layout.setBackgroundColor(bgColor)
        setContentView(layout)

        switch.setOnCheckedChangeListener { _, isChecked ->
            toggleFinished(isChecked)
        }
    }

    override fun onLoadingFinished() {
        switch.visibility = View.GONE
        animationView.visibility = View.GONE
        loremIpsum.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(1500)
                .setListener(null)
        }
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), bgColor, Color.WHITE)
        colorAnimation.duration = 250
        colorAnimation.addUpdateListener {
            layout.setBackgroundColor(it.getAnimatedValue() as Int)
        }
        colorAnimation.start()
    }

    fun toggleFinished(isChecked: Boolean) {
        if (isChecked) {
            animationView.onComplete()
        }
    }
}