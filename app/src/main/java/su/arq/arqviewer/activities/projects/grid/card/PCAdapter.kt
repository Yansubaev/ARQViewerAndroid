package su.arq.arqviewer.activities.projects.grid.card

import android.content.Context
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import su.arq.arqviewer.R
import su.arq.arqviewer.entities.ARQBuild

class PCAdapter (
    cardModels: List<PCModel>?,
    context: Context?,
    density: Float
) : RecyclerView.Adapter<PCViewHolder>() {
    var cardModels = cardModels
    private var context = context
    private var density = density
    private var modelByBuildMap: HashMap<String, PCModel> = HashMap()
    private var itemClickListener: ItemClickListener? = null
    private var onBindViewHolder:
            MutableList<((holder: PCViewHolder, position: Int) -> Unit)> = mutableListOf()

    var viewWidth: Int? = null

    fun getItem(position: Int) : PCModel? = cardModels?.get(position)

    fun getItem(build: ARQBuild?) : PCModel? = modelByBuildMap[build?.guid]

    fun setOnClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    fun addOnBindViewHolderListener(m: (holder: PCViewHolder, position: Int) -> Unit){
        onBindViewHolder.add(m)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PCViewHolder {
        val holder = PCViewHolder(
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

    override fun onBindViewHolder(holder: PCViewHolder, position: Int) {
        holder.view.layoutParams = ViewGroup.LayoutParams(-1, viewWidth ?: 100)
        cardModels?.get(position)?.let {
            holder.projectName.text = it.build.name
            holder.projectIcon.setImageDrawable(it.icon)
            it.holder = holder
            modelByBuildMap[it.build.guid] = it
        }
        onBindViewHolder.forEach { it(holder, position) }
    }

    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }
}
