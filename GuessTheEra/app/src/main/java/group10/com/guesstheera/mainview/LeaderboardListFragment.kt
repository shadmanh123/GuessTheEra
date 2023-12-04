package group10.com.guesstheera.mainview

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import group10.com.guesstheera.R


class LeaderboardListFragment(private val difficulty: String): Fragment() {
    // apparently kotlin complains if a class and its subclass share names for private fields
    private var leaderboardView: View? = null
    private var listView: ListView? = null
    private var highScore: Int? = null
    private lateinit var highScoreStored: SharedPreferences
    private lateinit var highScoreTextView: TextView

    constructor() : this("")


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        leaderboardView = inflater.inflate(R.layout.fragment_leaderboard_list, container, false)
        listView = leaderboardView?.findViewById(R.id.game_leaderboard)
        highScoreTextView = leaderboardView!!.findViewById(R.id.highScoreTV)
        if (difficulty == "Regular"){
            highScoreStored = requireContext().getSharedPreferences("easyModeHighScore", AppCompatActivity.MODE_PRIVATE)
        }
        else{
            highScoreStored = requireContext().getSharedPreferences("hardModeHighScore", AppCompatActivity.MODE_PRIVATE)
        }
        highScore = highScoreStored.getInt("highScore", 0)
        highScoreTextView.setText("High Score: $highScore")
        return leaderboardView
    }
}