package su.arq.arqviewer.projects.projectcard

import android.accounts.Account
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import su.arq.arqviewer.ProjectsGridInteractor
import su.arq.arqviewer.entities.ARQBuild
import su.arq.arqviewer.projects.projectcard.adapter.ProjectCardAdapter
import su.arq.arqviewer.projects.projectcard.decor.GridSpacingItemDecoration
import su.arq.arqviewer.projects.projectcard.model.ProjectCardModel
import su.arq.arqviewer.webcomunication.loaders.ARQVBuildContentLoader
import java.io.File
import java.lang.Exception
import kotlin.math.roundToInt

class ProjectsCardGrid(
    var interactor: ProjectsGridInteractor
) : ProjectCardAdapter.ItemClickListener {
    private var projectsGrid = interactor.projectsRecyclerView
    private var displayMetrics = interactor.displayMetrics
    private var context = interactor.context
    private val buildsDir = interactor.buildDirectory
    private var projectModels: MutableList<ProjectCardModel>? = null
    private var projectAdapter: ProjectCardAdapter? = null
    private var itemHeight: Int

    private var tempProjectModel: ProjectCardModel? = null

    init {
        val spanCount = 2
        projectModels = ArrayList()

        projectsGrid.layoutManager = GridLayoutManager(context, spanCount)

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

        interactor.setOnRequestLoadProjectListener { account, token ->
            startLoadingProject(account, token)
        }
        interactor.setOnRefillProjectsGridListener {
            refillModels(it)
        }
    }

    private fun refillModels(builds: Array<ARQBuild>?){
        projectModels?.clear()
        builds?.forEach {
            val pm = ProjectCardModel(context, it)
            projectModels?.add(pm)
        }
        updateGrid()

    }

    private fun updateGrid(){
        projectAdapter = ProjectCardAdapter(projectModels, projectsGrid.context, displayMetrics.density)
        projectAdapter?.setOnClickListener(this)
        projectAdapter?.viewWidth = itemHeight

        projectAdapter?.addOnBindViewHolderListener { holder, position ->
            val build = projectAdapter?.cardModels?.get(position)?.build
            if(build?.file?.exists() == true){
                holder.downloaded()
            }
        }

        projectsGrid.adapter = projectAdapter
    }

    private fun startLoadingProject(account: Account?, token: String){
        try {
            ARQVBuildContentLoader(
                context,
                token,
                tempProjectModel
            ).execute()
        }catch (ex: Exception){
            Log.e(this.javaClass.simpleName, ex.message, ex)
        }finally {
            tempProjectModel = null
        }
    }

    override fun onItemClick(view: View?, position: Int) {
        val model = (projectsGrid.adapter as ProjectCardAdapter).getItem(position)

        if(model?.build?.downloaded == false) {
            tempProjectModel = model

            interactor.requestPerms()

        } else {
            Log.d(this.javaClass.simpleName, model?.holder?.projectName?.text.toString())

            if (model != null) {
                interactor.openBuild(model.build)
            }
        }
    }
}