package su.arq.arqviewer.activities.sign.page

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SignPagerAdapter(fm: FragmentManager, private val models: List<SignPageModel>) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = models[position].fragment

    override fun getCount(): Int = models.size

}
