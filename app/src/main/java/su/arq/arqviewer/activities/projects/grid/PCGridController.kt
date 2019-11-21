package su.arq.arqviewer.activities.projects.grid

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import su.arq.arqviewer.LoadedBuildSaver
import su.arq.arqviewer.entities.ARQBuild
import su.arq.arqviewer.activities.projects.grid.card.PCAdapter
import su.arq.arqviewer.activities.projects.grid.card.GridSpacingItemDecoration
import su.arq.arqviewer.activities.projects.grid.card.PCModel
import su.arq.arqviewer.utils.toastInternetUnavailable
import su.arq.arqviewer.tasks.ARQVBuildContentLoader
import java.lang.Exception
import kotlin.math.roundToInt

class PCGridController(
    private var interactor: ProjectsGridInteractor
) : PCAdapter.ItemClickListener {
    private var projectsGrid = interactor.projectsRecyclerView
    private var displayMetrics = interactor.displayMetrics
    private var context = interactor.context
    private var projectModels: MutableList<PCModel>
    private var projectAdapter: PCAdapter
    private var itemHeight: Int

    private var tempBuild: ARQBuild? = null

    init {
        val spanCount = 2
        projectModels = ArrayList()
        projectsGrid.layoutManager = GridLayoutManager(context, spanCount)
        projectAdapter = PCAdapter(
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

        //region Listeners
        projectAdapter.setOnClickListener(this)
        interactor.setOnRequestLoadProjectListener { token ->
            startLoadingProject(token)
        }
        interactor.setOnRefillProjectsGridListener {
            refillModels(it)
        }
        //endregion
    }

    private fun refillModels(builds: Array<ARQBuild>?){
        projectModels.clear()
        builds?.forEach {
            val pm = PCModel(context, it)
            projectModels.add(pm)
        }
        updateGrid()
    }

    private fun updateGrid(){
        projectAdapter = PCAdapter(
            projectModels,
            projectsGrid.context,
            displayMetrics.density
        )
        projectAdapter.setOnClickListener(this)
        projectAdapter.viewWidth = itemHeight

        projectAdapter.addOnBindViewHolderListener { holder, position ->
            val build = projectAdapter.cardModels?.get(position)?.build
            if(build?.buildFile?.exists() == true){
                holder.downloaded()
            }
        }
        projectsGrid.adapter = projectAdapter
    }

    private fun startLoadingProject(token: String){
        try {
            ARQVBuildContentLoader(context, token, tempBuild).apply {
                setOnProgressUpdateListener { build, progress ->
                    projectAdapter.getItem(build)?.holder?.progressBar?.setProgress(progress, true)
                }
                setOnPreExecuteListener {build ->
                    projectAdapter.getItem(build)?.holder?.startDownloading()
                }
                setOnPostExecuteListener {build, success ->
                    if(build == null || !success){
                        toastInternetUnavailable(
                            interactor.projectsRecyclerView,
                            interactor.context.resources
                        )
                        projectAdapter.getItem(build)?.holder?.notDownloaded()
                    }else{
                        projectAdapter.getItem(build)?.holder?.downloadedAnimate()
                    }
                }
            }.execute()
        } catch (ex: Exception){
            Log.e(this.javaClass.simpleName, ex.message, ex)
        } finally {
            tempBuild = null
        }
    }

    override fun onItemClick(view: View?, position: Int) {
        val model = (projectsGrid.adapter as PCAdapter).getItem(position)

        if(model?.build?.downloaded == false) {
            tempBuild = model.build
            interactor.requestPerms()
        } else {
            if (model != null) {
                interactor.openBuild(model.build)
            }
        }
    }
}