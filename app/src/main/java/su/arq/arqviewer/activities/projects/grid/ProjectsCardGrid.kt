package su.arq.arqviewer.activities.projects.grid

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import su.arq.arqviewer.entities.ARQBuild
import su.arq.arqviewer.activities.projects.grid.card.ProjectCardAdapter
import su.arq.arqviewer.activities.projects.grid.card.GridSpacingItemDecoration
import su.arq.arqviewer.activities.projects.grid.card.ProjectCardModel
import su.arq.arqviewer.webcomunication.tasks.ARQVBuildContentLoader
import java.lang.Exception
import kotlin.math.roundToInt

class ProjectsCardGrid(
    private var interactor: ProjectsGridInteractor
) : ProjectCardAdapter.ItemClickListener {
    private var projectsGrid = interactor.projectsRecyclerView
    private var displayMetrics = interactor.displayMetrics
    private var context = interactor.context
    private var projectModels: MutableList<ProjectCardModel>
    private var projectAdapter: ProjectCardAdapter
    private var itemHeight: Int

    private var tempBuild: ARQBuild? = null

    init {
        val spanCount = 2
        projectModels = ArrayList()

        projectsGrid.layoutManager = GridLayoutManager(context, spanCount)

        projectAdapter = ProjectCardAdapter(
            projectModels,
            projectsGrid.context,
            displayMetrics.density
        )

        val itemDecoration = GridSpacingItemDecoration(
            spanCount,
            (22 * displayMetrics.density).roundToInt(),
            true
        )

        itemHeight = (displayMetrics.widthPixels/2 - 33*displayMetrics.density).roundToInt()
        projectAdapter.viewWidth = itemHeight

        projectsGrid.addItemDecoration(itemDecoration)
        projectsGrid.adapter = projectAdapter

        projectAdapter.setOnClickListener(this)

        interactor.setOnRequestLoadProjectListener { token ->
            startLoadingProject(token)
        }
        interactor.setOnRefillProjectsGridListener {
            refillModels(it)
        }
    }

    private fun refillModels(builds: Array<ARQBuild>?){
        projectModels.clear()
        builds?.forEach {
            val pm = ProjectCardModel(context, it)
            projectModels.add(pm)
        }
        updateGrid()
    }

    private fun updateGrid(){
        projectAdapter = ProjectCardAdapter(
            projectModels,
            projectsGrid.context,
            displayMetrics.density
        )
        projectAdapter.setOnClickListener(this)
        projectAdapter.viewWidth = itemHeight

        projectAdapter.addOnBindViewHolderListener { holder, position ->
            val build = projectAdapter.cardModels?.get(position)?.build
            if(build?.file?.exists() == true){
                holder.downloaded()
            }
        }
        projectsGrid.adapter = projectAdapter
    }

    private fun startLoadingProject(token: String){
        try {
            val loader = ARQVBuildContentLoader(context, token, tempBuild)
            loader.setOnProgressUpdateListener { build, progress ->
                projectAdapter.getItem(build)?.holder?.progressBar?.setProgress(progress, true)
            }
            loader.setOnPreExecuteListener {
                projectAdapter.getItem(it)?.holder?.progressBar?.isIndeterminate = false
            }
            loader.setOnPostExecuteListener {
                projectAdapter.getItem(it)?.holder?.downloadedAnimate()
            }
        }catch (ex: Exception){
            Log.e(this.javaClass.simpleName, ex.message, ex)
        }finally {
            tempBuild = null
        }
    }

    override fun onItemClick(view: View?, position: Int) {
        val model = (projectsGrid.adapter as ProjectCardAdapter).getItem(position)

        if(model?.build?.downloaded == false) {
            tempBuild = model.build
            interactor.requestPerms()
        } else {
            Log.d(this.javaClass.simpleName, model?.holder?.projectName?.text.toString())

            if (model != null) {
                interactor.openBuild(model.build)
            }
        }
    }
}