package su.arq.arqviewer.sign.page.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import su.arq.arqviewer.sign.page.model.SignPageModel

class SignPagerAdapter(fm: FragmentManager, private val models: List<SignPageModel>) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = models[position].fragment

    override fun getCount(): Int = models.size

}
