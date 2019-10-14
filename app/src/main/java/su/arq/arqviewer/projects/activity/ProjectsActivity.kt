package su.arq.arqviewer.projects.activity

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import su.arq.arqviewer.R
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.unity3d.player.UnityPlayerActivity
import su.arq.arqviewer.BuildMetaDataProvider
import su.arq.arqviewer.ProjectsGridInteractor
import su.arq.arqviewer.entities.ARQBuild
import su.arq.arqviewer.projects.projectcard.ProjectsCardGrid
import su.arq.arqviewer.sign.activity.SignActivity
import su.arq.arqviewer.utils.*
import su.arq.arqviewer.webcomunication.loaders.ARQVBuildListLoader

class ProjectsActivity :
    AppCompatActivity(),
    BuildMetaDataProvider,
    ProjectsGridInteractor,
    LoaderManager.LoaderCallbacks<Array<ARQBuild>>,
    ActivityCompat.OnRequestPermissionsResultCallback
{
    private var loaderManager: LoaderManager? = null
    private var account: Account? = null
    private lateinit var refreshLay: SwipeRefreshLayout
    private lateinit var accountManager: AccountManager

    override lateinit var displayMetrics: DisplayMetrics
    override var onRequestLoadProject: ((account: Account?, token: String) -> Unit)? = null
    override lateinit var projectsRecyclerView: RecyclerView
    override var onRefillProjectsGrid: ((builds: Array<ARQBuild>?) -> Unit)? = null

    override val context: Context
        get() = applicationContext
    override val buildDirectory: String
        get() = "${filesDir.path}/${account?.name}"
    override val token: String
        get() = AccountManager.get(applicationContext).peekAuthToken(account, account?.type)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_projects)

        projectsRecyclerView = this.findViewById(R.id.projects_grid)
        loaderManager = LoaderManager.getInstance(this)
        account = intent?.getParcelableExtra(EXTRA_ARQ_ACCOUNT)
        accountManager = AccountManager.get(applicationContext)

        refreshLay = findViewById(R.id.projects_refresh_lay)

        displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        ProjectsCardGrid(this)

        loaderManager?.restartLoader(R.id.builds_loader, null, this)

        refreshLay.setOnRefreshListener {
            loaderManager?.restartLoader(R.id.builds_loader, null, this)
        }
    }

    fun quitProjects(view: View){
        val am = AccountManager.get(applicationContext)
        am.removeAccountExplicitly(account)

        val intent = Intent(applicationContext, SignActivity::class.java)
        startActivity(intent)

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        intent.putExtra(EXTRA_FROM_ACTIVITY, "Projects")

        finish()
    }

    override fun requestPerms(){
        requestPermissions(
            arrayOf(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            228
        )
    }

    override fun openBuild(build: ARQBuild){
        intent = Intent(applicationContext, UnityPlayerActivity::class.java)
        val buildPath = build.file.absolutePath
        intent.putExtra(EXTRA_VIEWER_BUILD_PATH, buildPath)

        startActivity(intent)
    }

    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<Array<ARQBuild>> {
        return ARQVBuildListLoader(
            applicationContext,
            this
        )
    }

    override fun onLoadFinished(p0: Loader<Array<ARQBuild>>, builds: Array<ARQBuild>?) {
        onRefillProjectsGrid?.invoke(builds)
        refreshLay.isRefreshing = false
    }

    override fun onLoaderReset(p0: Loader<Array<ARQBuild>>) {  }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 228 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            onRequestLoadProject?.invoke(account, token)
        }else{
            requestPerms()
        }
    }
}
