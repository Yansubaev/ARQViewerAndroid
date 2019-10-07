package su.arq.arqviewer.projects.projectcard.adapter

import android.animation.ValueAnimator
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.ProgressBar
import su.arq.arqviewer.R
import su.arq.arqviewer.entities.ARQBuild
import su.arq.arqviewer.projects.projectcard.model.ProjectCardModel
import kotlin.math.roundToInt

class ProjectCardAdapter (
    var cardModels: List<ProjectCardModel>?,
    var context: Context?,
    var density: Float
) : RecyclerView.Adapter<ProjectCardAdapter.ViewHolder>() {

    private var mClickListener: ItemClickListener? = null
    lateinit var holder: ViewHolder
    var viewWidth: Int? = null

    private var onBindViewHolder:
            MutableList<((holder: ViewHolder, position: Int) -> Unit)> = mutableListOf()

    fun addOnBindViewHolderListener(m: (holder: ViewHolder, position: Int) -> Unit){
        onBindViewHolder.add(m)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        holder =  ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.project_card_item,
                parent,
                false
            ),
            mClickListener
        )
        holder.density = density
        return holder
    }

    override fun getItemCount(): Int = cardModels?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.layoutParams = ViewGroup.LayoutParams(-1, viewWidth ?: 100)
        holder.projectName.text = cardModels?.get(position)?.build?.name ?: "Error: NPE"
        holder.projectIcon.setImageDrawable(cardModels?.get(position)?.icon)
        cardModels?.get(position)?.holder = holder
        onBindViewHolder.forEach { it(holder, position) }
    }

    fun getItem(position: Int) : ProjectCardModel? = cardModels?.get(position)

    fun setOnClickListener(itemClickListener: ItemClickListener) {
        this.mClickListener = itemClickListener
    }

    class ViewHolder(
        var view: View,
        var mClickListener: ItemClickListener?
    ) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var projectName: TextView = view.findViewById(R.id.project_name_txt)
        var projectIcon: ImageView = view.findViewById(R.id.project_icon)
        var progressBar: ProgressBar = view.findViewById(R.id.download_progress_bar)
        var cloudIcon: ImageView = view.findViewById(R.id.cloud_icon)
        var density: Float? = null

        init {
            view.setOnClickListener(this)
        }

        companion object{
            var animationDuration = 250L
            var animationDelay = 100L
            var leftNamePosition = 12
            var rightNamePosition = 38
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
    }

    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }
}
