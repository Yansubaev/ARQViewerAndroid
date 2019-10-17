package su.arq.arqviewer.activities.projects.grid

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.RecyclerView
import su.arq.arqviewer.entities.ARQBuild

interface ProjectsGridInteractor{
    var projectsRecyclerView: RecyclerView
    var displayMetrics: DisplayMetrics
    val context: Context
    val buildDirectory: String
    var onRequestLoadProject: ((token: String) -> Unit)?
    var onRefillProjectsGrid: ((builds: Array<ARQBuild>?) -> Unit)?

    fun setOnRequestLoadProjectListener(m: (token: String) -> Unit) {
        onRequestLoadProject = m
    }
    fun setOnRefillProjectsGridListener(m: ((builds: Array<ARQBuild>?) -> Unit)){
        onRefillProjectsGrid = m
    }
    fun openBuild(build: ARQBuild)
    fun requestPerms()
}