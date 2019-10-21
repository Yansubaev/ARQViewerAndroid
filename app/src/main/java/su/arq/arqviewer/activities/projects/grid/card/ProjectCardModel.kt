package su.arq.arqviewer.activities.projects.grid.card

import android.content.Context
import android.graphics.drawable.Drawable
import su.arq.arqviewer.entities.ARQBuild

data class ProjectCardModel (private val context: Context, var build: ARQBuild){
    var icon  = context.getDrawable(build.icon.id)
    var holder: ProjectCardViewHolder? = null
}
