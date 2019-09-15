package su.arq.arqviewerapp.projects

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import su.arq.arqviewerapp.R
import android.util.DisplayMetrics
import android.util.Log
import android.widget.TextView
import kotlin.math.roundToInt

class ProjectsActivity : AppCompatActivity(), ProjectCardAdapter.ItemClickListener {

    private var projectModels: MutableList<ProjectCardModel>? = null
    private var projectAdapter: ProjectCardAdapter? = null
    private var projectsGrid: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_projects)
        val spanCount = 2

        projectsGrid = this.findViewById(R.id.projects_grid)
        projectsGrid?.layoutManager = GridLayoutManager(applicationContext, spanCount)

        projectModels = ArrayList()
        projectModels?.add(ProjectCardModel("1.Pizdec"))
        projectModels?.add(ProjectCardModel("2.Nahui"))
        projectModels?.add(ProjectCardModel("3.Blyat"))
        projectModels?.add(ProjectCardModel("4.Pizdec"))
        projectModels?.add(ProjectCardModel("5.Nahui"))
        projectModels?.add(ProjectCardModel("6.Blyat"))
        projectModels?.add(ProjectCardModel("7.Pizdec"))
        projectModels?.add(ProjectCardModel("8.Nahui"))
        projectModels?.add(ProjectCardModel("9.Blyat"))

        projectAdapter = ProjectCardAdapter(projectModels, projectsGrid?.context)
        projectAdapter?.setOnClickListener(this)

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val logicalDensity = metrics.density

        val itemDecoration = GridSpacingItemDecoration(spanCount, (22 * logicalDensity).roundToInt(), true)
        projectsGrid?.addItemDecoration(itemDecoration)

        projectsGrid?.adapter = projectAdapter
    }

    fun quitProjects(view: View){
        super.onBackPressed()
    }

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onItemClick(view: View?, position: Int) {
        Log.i("Yansub", "Clicked at " + projectAdapter?.getItem(position))
        view?.findViewById<TextView>(R.id.project_name_txt)?.text = "LOHPIDR"
    }

}
