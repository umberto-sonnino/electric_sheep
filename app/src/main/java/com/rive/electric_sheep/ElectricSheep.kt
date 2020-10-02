package com.rive.electric_sheep

import android.content.Context
import android.graphics.Canvas
import android.view.View
import app.rive.runtime.kotlin.*
import kotlin.math.min


class ElectricSheep : View {
    private var lastTime: Long = 0

    private val renderer: Renderer
    private val artboard: Artboard
    private val animationInstances = ArrayList<LinearAnimationInstance>()
    private val completedAnimations = ArrayList<LinearAnimationInstance>()

    private val start: LinearAnimationInstance
    private val end: LinearAnimationInstance
    private val vibrate: LinearAnimationInstance
    private val move: LinearAnimationInstance

    private val mixSeconds = 0.1f
    private var mix = 1.0f

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

        canvas.save()
        if (isPlaying) {
            advanceAnimations(elapsed)
            artboard.advance(elapsed)
            // Paint again.
            invalidate()
        }
        artboard.draw(renderer, canvas)
        canvas.restore()
    }

    fun advanceAnimations(elapsed: Float) {
        if (!isPlaying) return

        val mixValue = min(1.0f, mix / mixSeconds)

        animationInstances.forEach {
            val result = it.advance(elapsed)
            onResult(result, it)
            it.apply(artboard, 1f)
        }

        completedAnimations.forEach {
            animationInstances.remove(it)
            onCompleted(it)
        }
        completedAnimations.clear()
    }

    fun onCompleted(animation: LinearAnimationInstance) {
        when (animation) {
            start -> {
                animationInstances.add(move)
                animationInstances.add(vibrate)
            }
            end -> {
                isPlaying = false
                val endAnimation = end.animation
                // Subtract small epsilon to avoid animation advancing to the first frame
                val finalTime: Float =
                    (endAnimation.duration.toFloat() - 0.00001f) / endAnimation.fps
                end.time(finalTime)
                end.advance(0f)
                end.apply(artboard, 1f)
            }
            move -> {
                animationInstances.add(end)
            }
        }
    }

    fun onResult(loop: Loop, animation: LinearAnimationInstance) {
        when (loop) {
            Loop.ONESHOT, Loop.LOOP -> {
                if (animation != vibrate)
                    completedAnimations.add(animation)
            }
            else -> {
                return
            }
        }
    }

    fun restart() {
        isPlaying = true
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