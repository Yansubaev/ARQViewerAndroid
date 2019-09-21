package su.arq.arqviewer.launcher

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import su.arq.arqviewer.R

class LauncherCardAdapter(var cardModels: List<LauncherCardModel>?, var context: Context) : PagerAdapter(){

    var layoutInflater: LayoutInflater? = null

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1
    }

    override fun getCount(): Int {
        return cardModels?.size ?: 0
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater!!.inflate(R.layout.launcher_card_item, container, false)

        val topText: TextView
        val bottomText: TextView
        topText = view.findViewById(R.id.card_top_text)
        bottomText = view.findViewById(R.id.card_bottom_text)

        topText.text = cardModels?.get(position)?.topText ?: "Error: NPE"
        bottomText.text = cardModels?.get(position)?.bottomText ?: "Error: NPE"

        container.addView(view, 0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

}