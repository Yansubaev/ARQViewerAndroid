package su.arq.arqviewer.projects.activity

import android.accounts.AccountManager
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import su.arq.arqviewer.R
import android.util.DisplayMetrics
import android.util.Log
import su.arq.arqviewer.account.ARQAccount
import su.arq.arqviewer.entities.ARQBuild
import su.arq.arqviewer.projects.projectcard.decor.GridSpacingItemDecoration
import su.arq.arqviewer.projects.projectcard.adapter.ProjectCardAdapter
import su.arq.arqviewer.projects.projectcard.model.ProjectCardModel
import su.arq.arqviewer.sign.activity.SignActivity
import su.arq.arqviewer.utils.EXTRA_ARQ_ACCOUNT_NAME
import su.arq.arqviewer.webcomunication.loaders.ARQVBuildListLoader
import su.arq.arqviewer.utils.EXTRA_TOKEN
import su.arq.arqviewer.webcomunication.loaders.ARQVBuildContentLoader
import java.io.File
import java.lang.Exception
import kotlin.math.roundToInt

class ProjectsActivity :
    AppCompatActivity(),
    ProjectCardAdapter.ItemClickListener,
    LoaderManager.LoaderCallbacks<Array<ARQBuild>>,
    ActivityCompat.OnRequestPermissionsResultCallback
{
    private var projectModels: MutableList<ProjectCardModel>? = null
    private var projectAdapter: ProjectCardAdapter? = null
    private var projectsGrid: RecyclerView? = null
    private var mLoaderManager: LoaderManager? = null
    private var token: String? = null
    private var itemHeight: Int? = null
    private var account: String? = null
    private var density: Float = 1f

    private var refreshLay: SwipeRefreshLayout? = null

    private var tempProjectModel: ProjectCardModel? = null

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

        refreshLay = findViewById(R.id.projects_refresh_lay)

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        density = metrics.density

        val itemDecoration = GridSpacingItemDecoration(
            spanCount,
            (22 * density).roundToInt(),
            true
        )
        itemHeight = (metrics.widthPixels/2 - 33*density).roundToInt()
        projectAdapter?.viewWidth = itemHeight

        projectAdapter = ProjectCardAdapter(projectModels, projectsGrid?.context, density)
        projectsGrid?.addItemDecoration(itemDecoration)
        projectsGrid?.adapter = projectAdapter
        mLoaderManager?.restartLoader(R.id.builds_loader, null, this)

        refreshLay?.setOnRefreshListener {
            mLoaderManager?.restartLoader(R.id.builds_loader, null, this)
        }
    }

    fun quitProjects(view: View){
        val am = AccountManager.get(applicationContext)
        am.removeAccountExplicitly(ARQAccount(account ?: ""))

        val intent = Intent(applicationContext, SignActivity::class.java)
        startActivity(intent)

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        intent.putExtra("FROM_ACTIVITY", "Projects")

        finish()
    }

    override fun onItemClick(view: View?, position: Int) {
        val model = (projectsGrid?.adapter as ProjectCardAdapter).getItem(position)

        if(model?.build?.downloaded == false) {
            model.build.downloaded = true

            tempProjectModel = model

            requestPermissions(
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                228
            )
        } else {
            Log.d(this.javaClass.simpleName, model?.holder?.projectName?.text.toString())
        }

    }

    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<Array<ARQBuild>> {
        return ARQVBuildListLoader(applicationContext, token)
    }

    override fun onLoadFinished(p0: Loader<Array<ARQBuild>>, builds: Array<ARQBuild>?) {
        projectModels?.clear()
        builds?.forEach {
            val pm = ProjectCardModel(applicationContext, it)
            projectModels?.add(pm)
        }
        updateProjectsGrid()
        refreshLay?.isRefreshing = false
    }

    override fun onLoaderReset(p0: Loader<Array<ARQBuild>>) {  }

    private fun updateProjectsGrid(){
        projectAdapter = ProjectCardAdapter(projectModels, projectsGrid?.context, density)
        projectAdapter?.setOnClickListener(this)
        projectAdapter?.viewWidth = itemHeight

        projectAdapter?.addOnBindViewHolderListener { holder, position ->
            val build = projectAdapter?.cardModels?.get(position)?.build
            if(existInDevice(build) ){
                build?.downloaded = true
                holder.downloaded()
            }
        }

        projectsGrid?.adapter = projectAdapter
    }

    private fun existInDevice(build: ARQBuild?): Boolean{
        return try {
            val apkStorage = File("${filesDir.absolutePath}/$account")
            if(apkStorage.exists()){
                val outputFile = File(apkStorage, "${build?.guid}.arq")
                outputFile.exists()
            }else{
                false
            }
        }catch (ex: Exception){
            Log.e(this.javaClass.simpleName, ex.message, ex)
            false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 228 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            try {
                val apkStorage = File("${filesDir.absolutePath}/$account")

                if (!apkStorage.exists()) { apkStorage.mkdir() }

                val outputFile = File(apkStorage, "${tempProjectModel?.build?.guid}.arq")

                if (!outputFile.exists()) {
                    outputFile.createNewFile()
                    Log.e(this.javaClass.simpleName, "File Created")
                }

                ARQVBuildContentLoader(
                    applicationContext,
                    token,
                    outputFile,
                    tempProjectModel
                ).execute()
            }catch (ex: Exception){
                Log.e(this.javaClass.simpleName, ex.message, ex)
            }
            tempProjectModel = null
        }else{
            requestPermissions(arrayOf(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE), 228
            )
        }
    }
}
