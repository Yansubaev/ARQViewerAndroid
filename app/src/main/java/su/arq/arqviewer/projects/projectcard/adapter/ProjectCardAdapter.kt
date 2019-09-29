package su.arq.arqviewer.projects.projectcard.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.view.LayoutInflater
import android.widget.ImageView
import su.arq.arqviewer.R
import su.arq.arqviewer.entities.ARQBuild
import su.arq.arqviewer.projects.projectcard.model.ProjectCardModel

class ProjectCardAdapter (
    var cardModels: List<ProjectCardModel>?,
    var context: Context?
)
    : RecyclerView.Adapter<ProjectCardAdapter.ViewHolder>()
{
    private var mClickListener: ItemClickListener? = null
    var viewWidth: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.project_card_item,
                parent,
                false
            ),
            mClickListener
        )
    }

    override fun getItemCount(): Int = cardModels?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
/*
        holder.view.layoutParams = ViewGroup.LayoutParams(
            holder.view.layoutParams.width,
            holder.view.layoutParams.width
            )
*/
/*
        holder.view.layoutParams = ViewGroup.LayoutParams(
            holder.view.layoutParams.width,
            viewWidth ?: 150
        )
*/
        Log.d(this.javaClass.simpleName, "height = ${holder.view.layoutParams.height}")
        holder.view.layoutParams = ViewGroup.LayoutParams(-1, viewWidth ?: 100)
        holder.projectName?.text = cardModels?.get(position)?.build?.name ?: "Error: NPE"
        holder.projectIcon?.setImageDrawable(cardModels?.get(position)?.icon)
    }

    fun getItem(position: Int) : String = cardModels?.get(position)?.build?.name ?: "Error: NPE"

    fun setOnClickListener(itemClickListener: ItemClickListener) {
        this.mClickListener = itemClickListener
    }

    class ViewHolder(
        var view: View,
        var mClickListener: ItemClickListener?
    )
        : RecyclerView.ViewHolder(view), View.OnClickListener
    {
        var projectName: TextView? = null
        var projectIcon: ImageView? = null

        init {
            this.projectName = view.findViewById(R.id.project_name_txt)
            this.projectIcon = view.findViewById(R.id.project_icon)
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if(mClickListener != null){
                mClickListener?.onItemClick(v, adapterPosition )
            }
        }

    }

    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }
}