package su.arq.arqviewer

import android.graphics.Typeface
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_test_scrolling.*
import java.lang.reflect.Type

class TestScrollingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_scrolling)
        setSupportActionBar(toolbar)
        toolbar_layout.title = "Проекты"
        toolbar_layout.apply {
            val ttf = Typeface.createFromAsset(applicationContext.assets, "fonts/ttnorms_bold.ttf")
            setCollapsedTitleTypeface(ttf)
            setExpandedTitleTypeface(ttf)
        }

        /*fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/
    }
}
