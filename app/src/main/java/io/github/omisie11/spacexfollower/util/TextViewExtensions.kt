package io.github.omisie11.spacexfollower.util

import android.animation.ValueAnimator
import android.widget.TextView

fun TextView.animateNumber(
    finalValue: Int,
    startValue: Int = 0,
    animationDuration: Long = 1000L
) {
    val animator = ValueAnimator.ofInt(startValue, finalValue)
    animator.duration = animationDuration

    animator.addUpdateListener { valueAnimator ->
        this.text = valueAnimator.animatedValue.toString()
    }
    animator.start()
}