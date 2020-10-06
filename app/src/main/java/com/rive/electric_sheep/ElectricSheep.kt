package com.rive.electric_sheep

import android.content.Context
import android.graphics.Canvas
import android.view.View
import app.rive.runtime.kotlin.*
import kotlin.math.min


class ElectricSheep : View {
    private var onFinishedDelegate: LoadFinishedDelegate?
    private var lastTime: Long = 0

    private val renderer: Renderer
    private val artboard: Artboard
    private val animationInstances = ArrayList<LinearAnimationInstance>()
    private val completedAnimations = ArrayList<LinearAnimationInstance>()

    private val start: LinearAnimationInstance
    private val end: LinearAnimationInstance
    private val vibrate: LinearAnimationInstance
    private val move: LinearAnimationInstance

    private var moveMix = 1.0f

    private lateinit var targetBounds: AABB

    private var isPlaying = true
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
        onFinishedDelegate = if (context is LoadFinishedDelegate) context else null
        val file = File(fileBytes)
        artboard = file.artboard()
        renderer = Renderer()

        start = LinearAnimationInstance(artboard.animation("start"))
        end = LinearAnimationInstance(artboard.animation("end"))
        end.animation.loop = Loop.ONESHOT
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

    private fun redraw(canvas: Canvas) {
        renderer.canvas = canvas
        renderer.align(Fit.CONTAIN, Alignment.CENTER, targetBounds, artboard.bounds())

        canvas.save()
        if (isPlaying) {
            val currentTime = System.currentTimeMillis()
            val elapsed = (currentTime - lastTime) / 1000f
            lastTime = currentTime
            advanceAnimations(elapsed)
            artboard.advance(elapsed)
            // Paint again.
            invalidate()
        }
        artboard.draw(renderer, canvas)
        canvas.restore()
    }

    private fun advanceAnimations(elapsed: Float) {

        moveMix = min(1f, moveMix + elapsed/2)

        animationInstances.forEach {
            val result = it.advance(elapsed)
            onResult(result, it)
            it.apply(artboard, moveMix)
        }

        completedAnimations.forEach {
            animationInstances.remove(it)
            onCompleted(it)
        }
        completedAnimations.clear()
    }

    private fun onCompleted(animation: LinearAnimationInstance) {
        when (animation) {
            start -> {
                animationInstances.add(move)
                animationInstances.add(vibrate)
                moveMix = 0f
            }
            end -> {
                isPlaying = false
                completedAnimations.add(end)
                completedAnimations.add(vibrate)
                onFinishedDelegate?.onLoadingFinished()
            }
        }
    }

    private fun onResult(loop: Loop?, animation: LinearAnimationInstance) {
        when (loop) {
            Loop.ONESHOT, Loop.LOOP -> {
                if (animation == start || animation == end) {
                    completedAnimations.add(animation)
                }
            }
            else -> {
                return
            }
        }
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

    fun onComplete() {
        completedAnimations.add(move)
        completedAnimations.add(vibrate)
        animationInstances.add(end)
    }
}