package su.arq.arqviewer.sign.page.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import su.arq.arqviewer.sign.page.model.SignPageModel

class SignPagerAdapter(fm: FragmentManager, private val models: List<SignPageModel>) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = models[position].fragment

    override fun getCount(): Int = models.size

}
