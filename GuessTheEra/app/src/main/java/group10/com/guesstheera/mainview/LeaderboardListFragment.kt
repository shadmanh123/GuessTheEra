package group10.com.guesstheera.mainview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import group10.com.guesstheera.R


class LeaderboardListFragment(private val difficulty: String): Fragment() {
    // apparently kotlin complains if a class and its subclass share names for private fields
    private var leaderboardView: View? = null
    private var listView: ListView? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        leaderboardView = inflater.inflate(R.layout.fragment_leaderboard_list, container, false)
        listView = leaderboardView?.findViewById(R.id.game_leaderboard)
        return leaderboardView
    }
}