package group10.com.guesstheera.mainview

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class LeaderboardTabAdapter(activity: FragmentActivity,
    var list: List<Fragment> = ArrayList<Fragment>()) :
FragmentStateAdapter(activity){

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }

    override fun getItemCount(): Int {
        return list.size
    }
}