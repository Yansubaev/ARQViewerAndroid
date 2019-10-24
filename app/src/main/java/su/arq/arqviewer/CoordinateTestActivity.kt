package su.arq.arqviewer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import su.arq.arqviewer.activities.projects.grid.card.GridSpacingItemDecoration
import su.arq.arqviewer.activities.projects.grid.card.ProjectCardAdapter
import su.arq.arqviewer.activities.projects.grid.card.ProjectCardModel
import su.arq.arqviewer.entities.ARQBuild
import kotlin.math.roundToInt

class CoordinateTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coordinate_test)
    }
}
