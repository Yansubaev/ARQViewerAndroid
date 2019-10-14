package su.arq.arqviewer

import android.accounts.Account
import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.RecyclerView
import su.arq.arqviewer.entities.ARQBuild

interface ProjectsGridInteractor{
    var projectsRecyclerView: RecyclerView
    var displayMetrics: DisplayMetrics
    val context: Context
    val buildDirectory: String

    var onRequestLoadProject: ((account: Account?, token: String) -> Unit)?
    fun setOnRequestLoadProjectListener(m: (account: Account?, token: String) -> Unit) {
        onRequestLoadProject = m
    }

    var onRefillProjectsGrid: ((builds: Array<ARQBuild>?) -> Unit)?
    fun setOnRefillProjectsGridListener(m: ((builds: Array<ARQBuild>?) -> Unit)){
        onRefillProjectsGrid = m
    }

    fun openBuild(build: ARQBuild)

    fun requestPerms()
}