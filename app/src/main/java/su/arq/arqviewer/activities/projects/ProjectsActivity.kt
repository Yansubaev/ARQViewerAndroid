package su.arq.arqviewer.activities.projects

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import su.arq.arqviewer.R
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.unity3d.player.UnityPlayerActivity
import kotlinx.android.synthetic.main.activity_test_scrolling.*
import su.arq.arqviewer.account.ARQAccount
import su.arq.arqviewer.entities.BuildMetaData
import su.arq.arqviewer.activities.projects.grid.ProjectsGridInteractor
import su.arq.arqviewer.entities.ARQBuild
import su.arq.arqviewer.activities.projects.grid.ProjectsCardGrid
import su.arq.arqviewer.activities.sign.SignActivity
import su.arq.arqviewer.utils.*
import su.arq.arqviewer.webcomunication.loaders.ARQVBuildListLoader
import su.arq.arqviewer.webcomunication.response.AuthenticationData
import su.arq.arqviewer.webcomunication.response.BuildListData
import android.net.NetworkInfo
import android.net.ConnectivityManager
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class ProjectsActivity :
    AppCompatActivity(),
    BuildMetaData,
    ProjectsGridInteractor,
    LoaderManager.LoaderCallbacks<BuildListData>,
    Loader.OnLoadCanceledListener<BuildListData>,
    ActivityCompat.OnRequestPermissionsResultCallback
{
    private var loaderManager: LoaderManager? = null
    private var account: Account? = null
    private lateinit var refreshLay: SwipeRefreshLayout
    private lateinit var accountManager: AccountManager

    override lateinit var displayMetrics: DisplayMetrics
    override var onRequestLoadProject: ((token: String) -> Unit)? = null
    override lateinit var projectsRecyclerView: RecyclerView
    override var onRefillProjectsGrid: ((builds: Array<ARQBuild>?) -> Unit)? = null

    override val context: Context
        get() = applicationContext
    override val buildDirectory: String
        get() = "${filesDir.path}/${account?.name}"
    override val token: String
        get() = accountManager.peekAuthToken(account, account?.type)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        accountManager = AccountManager.get(applicationContext)
        val al = accountManager.getAccountsByType(ARQAccount.TYPE)
        account = al.last()

        setContentView(R.layout.activity_test_scrolling)

        projectsRecyclerView = this.findViewById(R.id.projects_grid)
        setSupportActionBar(toolbar)
        toolbar_layout.title = "Проекты"
        toolbar_layout.apply {
            val ttf = Typeface.createFromAsset(applicationContext.assets, "fonts/ttnorms_bold.ttf")
            setCollapsedTitleTypeface(ttf)
            setExpandedTitleTypeface(ttf)
        }

        loaderManager = LoaderManager.getInstance(this)

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
        accountManager.removeAccountExplicitly(account)
        val intent = Intent(applicationContext, SignActivity::class.java)
        startActivity(intent)
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

    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<BuildListData> {
        return ARQVBuildListLoader(
            applicationContext,
            this
        )
    }

    override fun onLoadFinished(p0: Loader<BuildListData>, builds: BuildListData?) {
        onRefillProjectsGrid?.invoke(builds?.builds)
        refreshLay.isRefreshing = false
    }

    override fun onLoaderReset(p0: Loader<BuildListData>) {  }

    override fun onLoadCanceled(loader: Loader<BuildListData>) {
        dlog(this, "onLoadCanceled")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 228 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            onRequestLoadProject?.invoke(token)
        }else{
            requestPerms()
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}
