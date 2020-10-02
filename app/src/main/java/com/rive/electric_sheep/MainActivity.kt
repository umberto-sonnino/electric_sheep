package com.rive.electric_sheep

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import app.rive.runtime.kotlin.File
import app.rive.runtime.kotlin.LinearAnimationInstance
import app.rive.runtime.kotlin.Renderer

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this);
        layout.orientation = LinearLayout.VERTICAL
        layout.weightSum = 2.0f

        val android_sheep = resources.openRawResource(R.raw.android_sheep)
        val bytes = android_sheep.readBytes()
        val simpleView = ElectricSheep(bytes, this)

        val btnTag = Button(this)
        val layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        btnTag.setLayoutParams(layoutParams)
        btnTag.setText("Restart")
        btnTag.setOnClickListener {
            simpleView.restart()
        }

        layout.addView(btnTag)
        layout.addView(simpleView);

        setContentView(layout);
    }
}