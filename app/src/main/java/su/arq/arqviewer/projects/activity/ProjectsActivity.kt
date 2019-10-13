package su.arq.arqviewer.projects.activity

import android.accounts.AccountManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import su.arq.arqviewer.R
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.unity3d.player.UnityPlayerActivity
import su.arq.arqviewer.account.ARQAccount
import su.arq.arqviewer.entities.ARQBuild
import su.arq.arqviewer.projects.projectcard.ProjectsCardGrid
import su.arq.arqviewer.projects.projectcard.decor.GridSpacingItemDecoration
import su.arq.arqviewer.projects.projectcard.adapter.ProjectCardAdapter
import su.arq.arqviewer.projects.projectcard.model.ProjectCardModel
import su.arq.arqviewer.sign.activity.SignActivity
import su.arq.arqviewer.unity.ViewerActivity
import su.arq.arqviewer.utils.EXTRA_ARQ_ACCOUNT_NAME
import su.arq.arqviewer.webcomunication.loaders.ARQVBuildListLoader
import su.arq.arqviewer.utils.EXTRA_TOKEN
import su.arq.arqviewer.webcomunication.loaders.ARQVBuildContentLoader
import java.io.File
import java.lang.Exception
import kotlin.math.roundToInt

class ProjectsActivity :
    AppCompatActivity(),
    LoaderManager.LoaderCallbacks<Array<ARQBuild>>,
    ActivityCompat.OnRequestPermissionsResultCallback
{
    private var mLoaderManager: LoaderManager? = null
    private var token: String? = null
    var account: String? = null
    lateinit var projectsGrid: ProjectsCardGrid
    private var refreshLay: SwipeRefreshLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_projects)

        val recyclerView = this.findViewById<RecyclerView>(R.id.projects_grid)
        mLoaderManager = LoaderManager.getInstance(this)
        token = intent?.getStringExtra(EXTRA_TOKEN)
        account = intent?.getStringExtra(EXTRA_ARQ_ACCOUNT_NAME)

        refreshLay = findViewById(R.id.projects_refresh_lay)

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)

        projectsGrid = ProjectsCardGrid(this, recyclerView, metrics)

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

    fun requestPerms(){
        requestPermissions(
            arrayOf(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            228
        )
    }

    fun openProject(build: ARQBuild){
        intent = Intent(applicationContext, UnityPlayerActivity::class.java)
        val buildPath = "${filesDir.absolutePath}/$account/${model?.build?.guid}"
        intent.putExtra("EXTRA_VIEWER_BUILD_PATH", buildPath)

        startActivity(intent)

    }

    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<Array<ARQBuild>> {
        return ARQVBuildListLoader(applicationContext, token)
    }

    override fun onLoadFinished(p0: Loader<Array<ARQBuild>>, builds: Array<ARQBuild>?) {
        projectsGrid.refillModels(builds)
        refreshLay?.isRefreshing = false
    }

    override fun onLoaderReset(p0: Loader<Array<ARQBuild>>) {  }

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
                    Log.i(this.javaClass.simpleName, "File Created")
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
            requestPerms()
        }
    }
}
