package su.arq.arqviewer.projects

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import su.arq.arqviewer.R
import android.util.DisplayMetrics
import android.util.Log
import su.arq.arqviewer.entities.ARQBuild
import su.arq.arqviewer.loaders.ARQVBuildsLoader
import kotlin.math.roundToInt

class ProjectsActivity : AppCompatActivity(), ProjectCardAdapter.ItemClickListener, LoaderManager.LoaderCallbacks<Array<ARQBuild>> {

    private var projectModels: MutableList<ProjectCardModel>? = null
    private var projectAdapter: ProjectCardAdapter? = null
    private var projectsGrid: RecyclerView? = null
    private var mLoaderManager: LoaderManager? = null
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_projects)
        val spanCount = 2

        projectsGrid = this.findViewById(R.id.projects_grid)
        projectsGrid?.layoutManager = GridLayoutManager(applicationContext, spanCount)

        token = intent?.getStringExtra("EXTRA_TOKEN")
        Log.d(this.javaClass.simpleName, token)

        mLoaderManager = LoaderManager.getInstance(this)

        projectModels = ArrayList()

        projectAdapter = ProjectCardAdapter(projectModels, projectsGrid?.context)
        projectAdapter?.setOnClickListener(this)

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val logicalDensity = metrics.density

        val itemDecoration = GridSpacingItemDecoration(spanCount, (22 * logicalDensity).roundToInt(), true)
        projectsGrid?.addItemDecoration(itemDecoration)

        projectsGrid?.adapter = projectAdapter

        mLoaderManager?.restartLoader(R.id.builds_loader, null, this)
    }

    fun quitProjects(view: View){
        super.onBackPressed()
    }

    override fun onItemClick(view: View?, position: Int) {

    }

    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<Array<ARQBuild>> {
        return ARQVBuildsLoader(applicationContext, token)
    }

    override fun onLoadFinished(p0: Loader<Array<ARQBuild>>, builds: Array<ARQBuild>?) {
        Log.d(this.javaClass.simpleName, "onLoadFinished: ${builds?.size}")
        builds?.forEach {
            projectModels?.add(ProjectCardModel(it.name, it.icon ?: 0))
            Log.d(this.javaClass.simpleName, "project: " + it.name)
        }
        updateProjectsGrid()
    }

    override fun onLoaderReset(p0: Loader<Array<ARQBuild>>) {

    }

    private fun updateProjectsGrid(){
        projectAdapter = ProjectCardAdapter(projectModels, projectsGrid?.context)
        projectAdapter?.setOnClickListener(this)

        projectsGrid?.adapter = projectAdapter

    }
}
