package su.arq.arqviewer.projects.projectcard.model

import android.content.Context
import android.graphics.drawable.Drawable
import su.arq.arqviewer.entities.ARQBuild

class ProjectCardModel (context: Context, build: ARQBuild){
    val build: ARQBuild = build
    var icon: Drawable? = context.getDrawable(build.icon.id)
}