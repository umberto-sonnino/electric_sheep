package com.rive.electric_sheep

import android.content.Context
import android.graphics.Canvas
import android.view.View
import app.rive.runtime.kotlin.*


class ElectricSheep : View {
    private var lastTime: Long = 0

    private val renderer: Renderer
    private val artboard: Artboard
    private val animationInstances = ArrayList<LinearAnimationInstance>()

    private val start: LinearAnimationInstance
    private val end: LinearAnimationInstance
    private val vibrate: LinearAnimationInstance
    private val move: LinearAnimationInstance

    private lateinit var targetBounds: AABB

    var isPlaying = true
        get() = field
        set(value) {
            if (value != field) {
                field = value
                if (value) {
                    lastTime = System.currentTimeMillis()
                    invalidate()
                }
            }
        }

    constructor(fileBytes: ByteArray, context: Context) : super(context) {
        val file = File(fileBytes)
        artboard = file.artboard()
        renderer = Renderer()
        start = LinearAnimationInstance(artboard.animation("start"))
        end = LinearAnimationInstance(artboard.animation("end"))
        vibrate = LinearAnimationInstance(artboard.animation("sheep_vibration"))
        move = LinearAnimationInstance(artboard.animation("sheep_movement"))
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        lastTime = System.currentTimeMillis()
        animationInstances.add(start)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        redraw(canvas)
    }

    fun redraw(canvas: Canvas) {
        val currentTime = System.currentTimeMillis()
        val elapsed = (currentTime - lastTime) / 1000f
        lastTime = currentTime
        renderer.canvas = canvas
        renderer.align(Fit.CONTAIN, Alignment.CENTER, targetBounds, artboard.bounds())

        advanceAnimations(elapsed)

        canvas.save()
        artboard.advance(elapsed)
        artboard.draw(renderer, canvas)
        canvas.restore()

        if (isPlaying) {
            // Paint again.
            invalidate()
        }
    }

    fun advanceAnimations(elapsed: Float) {
        if (!isPlaying) return

        animationInstances.forEach {
            val result = it.advance(elapsed)
            onResult(result, it)
            it.apply(artboard, 1f)
        }
    }

    fun onResult(loop: Loop, animation: LinearAnimationInstance) {
        when (loop) {
            Loop.NONE -> {
                return
            }
            Loop.ONESHOT -> {
                if (animation == start) {
                    animationInstances.clear()
                    animationInstances.add(vibrate)
                    animationInstances.add(move)
                }
            }

            Loop.LOOP -> {
                if (animation == move) {
                    animationInstances.clear()
                    animationInstances.add(end)
                } else if (animation == end) {
//                    animationInstances.clear()
                    isPlaying = false
                }
            }

            Loop.PINGPONG -> {
            }
        }
    }

    fun restart() {
        animationInstances.clear()
        lastTime = System.currentTimeMillis()
        animationInstances.add(start)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh);
        targetBounds = AABB(w.toFloat(), h.toFloat());
        invalidate()
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        renderer.cleanup()
    }
}