package su.arq.arqviewer.walkthrough

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import su.arq.arqviewer.R

class WalkthroughCardAdapter(
    var cardModels: List<WalkthroughCardModel>?,
    var context: Context,
    var itemWidth: Int
) : PagerAdapter(){

    var layoutInflater: LayoutInflater? = null

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1
    }

    override fun getCount(): Int {
        return cardModels?.size ?: 0
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater!!.inflate(R.layout.walkthrough_card_item, container, false)

        val topText: TextView
        val bottomText: TextView
        val card: CardView
        card = view.findViewById(R.id.card_view_cards)
        topText = view.findViewById(R.id.card_top_text)
        bottomText = view.findViewById(R.id.card_bottom_text)

        val cl = ConstraintLayout.LayoutParams(card.layoutParams)
        cl.width = itemWidth
        cl.height = itemWidth
        cl.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        cl.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        cl.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        cl.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        cl.topMargin = 240
        cl.bottomMargin = 196
        card.layoutParams = cl
        topText.text = cardModels?.get(position)?.topText ?: "Error: NPE"
        bottomText.text = cardModels?.get(position)?.bottomText ?: "Error: NPE"

        container.addView(view, 0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

}