package su.arq.arqviewer.projects.activity

import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
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
import android.widget.ProgressBar
import su.arq.arqviewer.account.ARQAccount
import su.arq.arqviewer.entities.ARQBuild
import su.arq.arqviewer.projects.nothingVisible
import su.arq.arqviewer.projects.progressVisible
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
    private var logicalDensity: Float = 1f

    private var refreshLay: SwipeRefreshLayout? = null

    private var tempGuid: String? = null
    private var tempProgressBar: ProgressBar? = null

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
        logicalDensity = metrics.density

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

        refreshLay?.setOnRefreshListener {
            mLoaderManager?.restartLoader(R.id.builds_loader, null, this)
        }
    }

    fun quitProjects(view: View){
        val am = AccountManager.get(applicationContext)
        am.removeAccountExplicitly(ARQAccount(account ?: ""))

        val intent = Intent(applicationContext, SignActivity::class.java)
        startActivity(intent)
        finish()
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun onItemClick(view: View?, position: Int) {
        val buildModel = (projectsGrid?.adapter as ProjectCardAdapter).getItem(position)

        if(buildModel?.build?.downloaded == false){
            buildModel.progressBar?.visibility = View.VISIBLE
            buildModel.progressBar?.isIndeterminate = true
            buildModel.build.downloaded = true

            tempGuid = buildModel.build.guid
            tempProgressBar = buildModel.progressBar
            buildModel.cloudIcon?.visibility = View.INVISIBLE
            progressVisible(buildModel.cloudIcon, buildModel.progressBar, buildModel.name)

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(
                    arrayOf(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    228
                )
            }
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
        projectAdapter = ProjectCardAdapter(projectModels, projectsGrid?.context)
        projectAdapter?.setOnClickListener(this)

        projectAdapter?.viewWidth = itemHeight
        projectsGrid?.adapter = projectAdapter
        (projectsGrid?.adapter as ProjectCardAdapter).cardModels?.forEach{
            if(existInDevice(it.build)){
                it.build.downloaded = true
                nothingVisible(
                    it.cloudIcon,
                    it.progressBar,
                    it.name,
                    (38*logicalDensity).roundToInt(),
                    (12*logicalDensity).roundToInt()
                )
            }
        }
    }

    private fun existInDevice(build: ARQBuild): Boolean{
        return try {
            val apkStorage = File("${filesDir.absolutePath}/$account")
            if(apkStorage.exists()){
                val outputFile = File(apkStorage, "${build.guid}.arq")
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

                val outputFile = File(apkStorage, "$tempGuid.arq")

                if (!outputFile.exists()) {
                    outputFile.createNewFile()
                    Log.e(this.javaClass.simpleName, "File Created")
                }

                ARQVBuildContentLoader(
                    applicationContext,
                    token,
                    tempGuid,
                    outputFile,
                    tempProgressBar
                ).execute()
            }catch (ex: Exception){
                Log.e(this.javaClass.simpleName, ex.message, ex)
            }

            tempGuid = null
            tempProgressBar = null
        }else{
            requestPermissions(arrayOf(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE), 228
            )
        }
    }
}
