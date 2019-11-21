package su.arq.arqviewer.activities.projects

import android.accounts.Account
import android.accounts.AccountManager
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import su.arq.arqviewer.R
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.loader.app.LoaderManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.activity_test_scrolling.*
import su.arq.arqviewer.build.BuildListProvider
import su.arq.arqviewer.account.ARQAccount
import su.arq.arqviewer.build.entities.BuildMetaData
import su.arq.arqviewer.activities.projects.grid.ProjectsGridInteractor
import su.arq.arqviewer.build.entities.ARQBuild
import su.arq.arqviewer.activities.projects.grid.PCGridController
import su.arq.arqviewer.activities.sign.SignActivity
import su.arq.arqviewer.utils.*
import su.arq.arqviewer.build.tasks.RomBuildListLoader
import su.arq.arqviewer.build.tasks.UrlBuildListLoader
import su.arq.unitylib.MainUnityActivity

class ProjectsActivity :
    AppCompatActivity(),
    BuildMetaData,
    ProjectsGridInteractor,
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

    private var blProvider: BuildListProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        accountManager = AccountManager.get(applicationContext)
        val al = accountManager.getAccountsByType(ARQAccount.TYPE)
        account = al.last()

        setContentView(R.layout.activity_test_scrolling)

        projectsRecyclerView = this.findViewById(R.id.projects_grid)
        setSupportActionBar(toolbar)
        toolbar_layout.apply {
            val ttf = Typeface.createFromAsset(applicationContext.assets, "fonts/ttnorms_bold.ttf")
            setCollapsedTitleTypeface(ttf)
            setExpandedTitleTypeface(ttf)
            title = resources.getText(R.string.projects)
        }

        refreshLay = findViewById(R.id.projects_refresh_lay)

        displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        PCGridController(this)

        blProvider = UrlBuildListLoader(context, this)
        subscribeListeners()
        blProvider?.startLoadingList()
    }

    private fun subscribeListeners(){
        blProvider?.setOnBuildListLoadedListener {
            onRefillProjectsGrid?.invoke(it.builds)
            refreshLay.isRefreshing = false
            blProvider = null
        }
        blProvider?.setOnBuildListLoadingErrorListener {
            toastInternetUnavailable(toolbar_layout, resources)
            blProvider = null
            blProvider = RomBuildListLoader(this)
            subscribeListeners()
            blProvider?.startLoadingList()
            refreshLay.isRefreshing = false
        }
        refreshLay.setOnRefreshListener {
            blProvider = null
            blProvider = UrlBuildListLoader(context, this)
            blProvider?.startLoadingList()
            subscribeListeners()
        }
    }

    fun quitProjects(view: View){
        accountManager.removeAccountExplicitly(account)
        val intent = Intent(applicationContext, SignActivity::class.java)
        intent.putExtra(EXTRA_FROM_ACTIVITY, ProjectsActivity::class.java.simpleName)
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

    override fun openBuild(build: ARQBuild, v: View){
        intent = Intent(applicationContext, MainUnityActivity::class.java)
        val buildPath = build.buildFile.absolutePath
        intent.putExtra(EXTRA_VIEWER_BUILD_PATH, buildPath)
        val b: Bundle
        val bitmap = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.parseColor("#308cf8"))
        b = ActivityOptions.makeThumbnailScaleUpAnimation(v, bitmap, 0, 0).toBundle()
        //overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
        startActivity(intent, b)
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
}