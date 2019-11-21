package su.arq.arqviewer.activities.projects.grid.card

import android.content.Context
import su.arq.arqviewer.build.entities.ARQBuild

data class PCModel (private val context: Context, var build: ARQBuild){
    var icon  = context.getDrawable(build.icon.id)
    var holder: PCViewHolder? = null
}
