package su.arq.arqviewer.activities.projects.grid.card

import android.animation.ValueAnimator
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import su.arq.arqviewer.R
import kotlin.math.roundToInt

class PCViewHolder(
    var view: View,
    private var mClickListener: PCAdapter.ItemClickListener?
) : RecyclerView.ViewHolder(view), View.OnClickListener {
    var projectName: TextView = view.findViewById(R.id.project_name_txt)
    var projectIcon: ImageView = view.findViewById(R.id.project_icon)
    var progressBar: ProgressBar = view.findViewById(R.id.download_progress_bar)
    var cloudIcon: ImageView = view.findViewById(R.id.cloud_icon)
    var density: Float? = null

    init { view.setOnClickListener(this) }

    companion object{
        const val animationDuration = 250L
        const val animationDelay = 100L
        const val leftNamePosition = 12
        const val rightNamePosition = 38
    }

    override fun onClick(v: View?) {
        if(mClickListener != null){
            mClickListener?.onItemClick(v, adapterPosition )
        }
    }

    fun downloadedAnimate(){
        val animator: ValueAnimator = ValueAnimator.ofInt(
            (rightNamePosition * (density ?: 1f)).roundToInt(),
            (leftNamePosition * (density ?: 1f)).roundToInt()
        )
        animator.duration = animationDuration
        animator.startDelay = animationDelay

        animator.addUpdateListener {
            val params = projectName.layoutParams as ConstraintLayout.LayoutParams
            params.marginStart = it.animatedValue as Int
            projectName.layoutParams = params
        }

        progressBar.postDelayed({progressBar.visibility = View.GONE}, animationDelay)
        cloudIcon.postDelayed({cloudIcon.visibility = View.GONE}, animationDelay)
        animator.setupStartValues()
        animator.start()
    }

    fun downloaded(){
        progressBar.visibility = View.GONE
        cloudIcon.visibility = View.GONE
        val params = projectName.layoutParams as ConstraintLayout.LayoutParams
        params.marginStart = (leftNamePosition * (density ?: 1f)).roundToInt()
        projectName.layoutParams = params
    }

    fun startDownloading(){
        progressBar.visibility = View.VISIBLE
        cloudIcon.visibility = View.GONE
        val params = projectName.layoutParams as ConstraintLayout.LayoutParams
        params.marginStart = (rightNamePosition * (density ?: 1f)).roundToInt()
        projectName.layoutParams = params
    }
    fun notDownloaded(){
        progressBar.visibility = View.GONE
        cloudIcon.visibility = View.VISIBLE
        val params = projectName.layoutParams as ConstraintLayout.LayoutParams
        params.marginStart = (rightNamePosition * (density ?: 1f)).roundToInt()
        projectName.layoutParams = params
    }
}