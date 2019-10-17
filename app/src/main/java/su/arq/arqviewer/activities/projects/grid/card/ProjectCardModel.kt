package su.arq.arqviewer.activities.projects.grid.card

import android.content.Context
import android.graphics.drawable.Drawable
import su.arq.arqviewer.entities.ARQBuild

class ProjectCardModel (context: Context, build: ARQBuild){
    val build = build
    var icon  = context.getDrawable(build.icon.id)
    var holder: ProjectCardViewHolder? = null
}
