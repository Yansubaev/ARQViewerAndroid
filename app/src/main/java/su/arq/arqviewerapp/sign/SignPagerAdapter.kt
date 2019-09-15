package su.arq.arqviewerapp.sign

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class SignPagerAdapter(fm: FragmentManager, private val models: List<SignPageModel>) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = models[position].fragment

    override fun getCount(): Int = models.size

}
