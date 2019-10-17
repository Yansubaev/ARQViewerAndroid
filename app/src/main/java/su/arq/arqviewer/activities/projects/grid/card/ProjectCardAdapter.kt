package su.arq.arqviewer.activities.projects.grid.card

import android.content.Context
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import su.arq.arqviewer.R
import su.arq.arqviewer.entities.ARQBuild

class ProjectCardAdapter (
    cardModels: List<ProjectCardModel>?,
    context: Context?,
    density: Float
) : RecyclerView.Adapter<ProjectCardViewHolder>() {
    private var density = density
    private var context = context
    private var modelByBuildMap: HashMap<ARQBuild, ProjectCardModel> = HashMap()
    private var itemClickListener: ItemClickListener? = null
    private var onBindViewHolder:
            MutableList<((holder: ProjectCardViewHolder, position: Int) -> Unit)> = mutableListOf()

    var cardModels = cardModels
        set(value) {
            value?.forEach { modelByBuildMap[it.build] = it }
            field = value
        }
    var viewWidth: Int? = null

    fun getItem(position: Int) : ProjectCardModel? = cardModels?.get(position)

    fun getItem(build: ARQBuild?) : ProjectCardModel? = modelByBuildMap[build]

    fun setOnClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    fun addOnBindViewHolderListener(m: (holder: ProjectCardViewHolder, position: Int) -> Unit){
        onBindViewHolder.add(m)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectCardViewHolder {
        val holder = ProjectCardViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.project_card_item,
                parent,
                false
            ),
            itemClickListener
        )
        holder.density = density
        return holder
    }

    override fun getItemCount(): Int = cardModels?.size ?: 0

    override fun onBindViewHolder(holder: ProjectCardViewHolder, position: Int) {
        holder.view.layoutParams = ViewGroup.LayoutParams(-1, viewWidth ?: 100)
        holder.projectName.text = cardModels?.get(position)?.build?.name ?: "Error: NPE"
        holder.projectIcon.setImageDrawable(cardModels?.get(position)?.icon)
        cardModels?.get(position)?.holder = holder
        onBindViewHolder.forEach { it(holder, position) }
    }

    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }
}
