package su.arq.arqviewer.projects

import android.animation.ValueAnimator
import android.support.constraint.ConstraintLayout
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView

fun cloudVisible(
    cloud: ImageView?,
    progressBar: ProgressBar?,
    name: TextView?,
    start: Int,
    end: Int
){
    val animator: ValueAnimator = ValueAnimator.ofInt(start, end)
    animator.duration = 250L
    animator.startDelay = 100L
    animator.addUpdateListener {
        val params = name?.layoutParams as ConstraintLayout.LayoutParams
        params.marginStart = it.animatedValue as Int
        name.layoutParams = params
    }
    cloud?.visibility = View.VISIBLE
    progressBar?.visibility = View.INVISIBLE
    animator.start()
}

fun progressVisible(cloud: ImageView?, progressBar: ProgressBar?, name: TextView?){
    cloud?.visibility = View.INVISIBLE
    progressBar?.visibility = View.VISIBLE
}