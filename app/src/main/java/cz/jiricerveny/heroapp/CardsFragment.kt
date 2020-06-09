package cz.jiricerveny.heroapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import cz.jiricerveny.heroapp.databinding.FragmentCardsBinding
import cz.jiricerveny.heroapp.teams.*

/**
 * This fragment contains cards which are displayed as ViewPager
 */
class CardsFragment : Fragment() {

    /**
     * Creates View with ViewPager and TabLayout
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCardsBinding.inflate(inflater, container, false)
        val viewPager = binding.pagerFr

        val pagerAdapter = ViewPagerAdapter(activity)
        viewPager.adapter = pagerAdapter

        val tabLayout = binding.tabsFr
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = "${position + 1}"
        }.attach()

        return binding.root
    }

    /**
     * Inner class of Adapter used to display fragments in ViewPager
     */
    private inner class ViewPagerAdapter(fa: FragmentActivity?) : FragmentStateAdapter(fa!!) {
        override fun getItemCount(): Int {
            return 7
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> SlaviaFragment()
                1 -> SpartaFragment()
                2 -> PlzenFragment()
                3 -> BanikFragment()
                4 -> BoleslavFragment()
                5 -> JablonecFragment()
                else -> SlovackoFragment()
            }
        }


        //override fun createFragment(position: Int): Fragment = SlaviaFragment()
    }
}