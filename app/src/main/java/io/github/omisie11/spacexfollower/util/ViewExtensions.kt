package io.github.omisie11.spacexfollower.util

import android.view.View

fun View.toggleVisibility(): View {
    when (visibility) {
        View.GONE -> visibility = View.VISIBLE
        View.VISIBLE -> visibility = View.GONE
        View.INVISIBLE -> visibility = View.VISIBLE
    }
    return this
}
