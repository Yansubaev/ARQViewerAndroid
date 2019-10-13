package su.arq.arqviewer.projects.projectcard

import android.content.Intent
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.unity3d.player.UnityPlayerActivity
import su.arq.arqviewer.entities.ARQBuild
import su.arq.arqviewer.projects.activity.ProjectsActivity
import su.arq.arqviewer.projects.projectcard.adapter.ProjectCardAdapter
import su.arq.arqviewer.projects.projectcard.decor.GridSpacingItemDecoration
import su.arq.arqviewer.projects.projectcard.model.ProjectCardModel
import java.io.File
import java.lang.Exception
import kotlin.math.roundToInt

class ProjectsCardGrid(
    var activity: ProjectsActivity,
    var projectsGrid: RecyclerView,
    var displayMetrics: DisplayMetrics
) : ProjectCardAdapter.ItemClickListener {

    private var projectModels: MutableList<ProjectCardModel>? = null
    private var projectAdapter: ProjectCardAdapter? = null
    private var itemHeight: Int

    private var tempProjectModel: ProjectCardModel? = null

    init {
        val spanCount = 2
        projectModels = ArrayList()

        projectsGrid.layoutManager = GridLayoutManager(activity.applicationContext, spanCount)

        val itemDecoration = GridSpacingItemDecoration(
            spanCount,
            (22 * displayMetrics.density).roundToInt(),
            true
        )

        itemHeight = (displayMetrics.widthPixels/2 - 33*displayMetrics.density).roundToInt()
        projectAdapter?.viewWidth = itemHeight

        projectAdapter = ProjectCardAdapter(projectModels, projectsGrid.context, displayMetrics.density)
        projectsGrid.addItemDecoration(itemDecoration)
        projectsGrid.adapter = projectAdapter

        projectAdapter?.setOnClickListener(this)

    }

    fun refillModels(builds: Array<ARQBuild>?){
        projectModels?.clear()
        builds?.forEach {
            val pm = ProjectCardModel(activity.applicationContext, it)
            projectModels?.add(pm)
        }
        updateGrid()

    }

    private fun updateGrid(){
        projectAdapter = ProjectCardAdapter(projectModels, projectsGrid?.context, displayMetrics.density)
        projectAdapter?.setOnClickListener(this)
        projectAdapter?.viewWidth = itemHeight

        projectAdapter?.addOnBindViewHolderListener { holder, position ->
            val build = projectAdapter?.cardModels?.get(position)?.build
            if(existInDevice(build) ){
                build?.downloaded = true
                holder.downloaded()
            }
        }

        projectsGrid.adapter = projectAdapter
    }

    private fun existInDevice(build: ARQBuild?): Boolean{
        return try {
            val apkStorage = File("${activity.filesDir.absolutePath}/${activity.account}")
            if(apkStorage.exists()){
                build?.file?.exists()!!
            }else{
                false
            }
        }catch (ex: Exception){
            Log.e(this.javaClass.simpleName, ex.message, ex)
            false
        }
    }


    override fun onItemClick(view: View?, position: Int) {
        val model = (projectsGrid.adapter as ProjectCardAdapter).getItem(position)

        if(model?.build?.downloaded == false) {
            model.build.downloaded = true
            model.build.file = File("${activity.filesDir.absolutePath}/${activity.account}")
            tempProjectModel = model

            activity.requestPerms()

        } else {
            Log.d(this.javaClass.simpleName, model?.holder?.projectName?.text.toString())

            if (model != null) {
                activity.openProject(model.build)
            }
        }
    }
}