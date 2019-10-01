package su.arq.arqviewer.projects.activity

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import su.arq.arqviewer.R
import android.util.DisplayMetrics
import su.arq.arqviewer.account.ARQAccount
import su.arq.arqviewer.entities.ARQBuild
import su.arq.arqviewer.projects.projectcard.decor.GridSpacingItemDecoration
import su.arq.arqviewer.projects.projectcard.adapter.ProjectCardAdapter
import su.arq.arqviewer.projects.projectcard.model.ProjectCardModel
import su.arq.arqviewer.sign.activity.SignActivity
import su.arq.arqviewer.utils.EXTRA_ARQ_ACCOUNT_NAME
import su.arq.arqviewer.webcomunication.loaders.ARQVBuildListLoader
import su.arq.arqviewer.utils.EXTRA_TOKEN
import kotlin.math.roundToInt

class ProjectsActivity :
    AppCompatActivity(),
    ProjectCardAdapter.ItemClickListener,
    LoaderManager.LoaderCallbacks<Array<ARQBuild>>
{
    private var projectModels: MutableList<ProjectCardModel>? = null
    private var projectAdapter: ProjectCardAdapter? = null
    private var projectsGrid: RecyclerView? = null
    private var mLoaderManager: LoaderManager? = null
    private var token: String? = null
    private var itemHeight: Int? = null
    private var account: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_projects)
        val spanCount = 2

        projectsGrid = this.findViewById(R.id.projects_grid)
        projectsGrid?.layoutManager = GridLayoutManager(applicationContext, spanCount)
        mLoaderManager = LoaderManager.getInstance(this)
        projectModels = ArrayList()
        token = intent?.getStringExtra(EXTRA_TOKEN)
        account = intent?.getStringExtra(EXTRA_ARQ_ACCOUNT_NAME)
        projectAdapter?.setOnClickListener(this)

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val logicalDensity = metrics.density

        val itemDecoration = GridSpacingItemDecoration(
            spanCount,
            (22 * logicalDensity).roundToInt(),
            true
        )
        itemHeight = (metrics.widthPixels/2 - 33*logicalDensity).roundToInt()
        projectAdapter?.viewWidth = itemHeight

        projectAdapter = ProjectCardAdapter(projectModels, projectsGrid?.context)
        projectsGrid?.addItemDecoration(itemDecoration)
        projectsGrid?.adapter = projectAdapter
        mLoaderManager?.restartLoader(R.id.builds_loader, null, this)
    }

    fun quitProjects(view: View){
        val am = AccountManager.get(applicationContext)
        am.removeAccountExplicitly(ARQAccount(account ?: ""))

        val intent = Intent(applicationContext, SignActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onItemClick(view: View?, position: Int) {
        
    }

    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<Array<ARQBuild>> {
        return ARQVBuildListLoader(applicationContext, token)
    }

    override fun onLoadFinished(p0: Loader<Array<ARQBuild>>, builds: Array<ARQBuild>?) {
        builds?.forEach {
            projectModels?.add(
                ProjectCardModel(
                    applicationContext,
                    it
                )
            )
        }
        updateProjectsGrid()
    }

    override fun onLoaderReset(p0: Loader<Array<ARQBuild>>) {

    }

    private fun updateProjectsGrid(){
        projectAdapter = ProjectCardAdapter(
            projectModels,
            projectsGrid?.context
        )
        projectAdapter?.setOnClickListener(this)

        projectAdapter?.viewWidth = itemHeight
        projectsGrid?.adapter = projectAdapter
    }
}
