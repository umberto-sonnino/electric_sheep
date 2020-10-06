package com.rive.electric_sheep

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import app.rive.runtime.kotlin.File
import app.rive.runtime.kotlin.LinearAnimationInstance
import app.rive.runtime.kotlin.Renderer

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main);
    }

    fun loadPage(view: View) {
        val intent = Intent(this, LoadActivity::class.java)
        startActivity(intent)
    }
}