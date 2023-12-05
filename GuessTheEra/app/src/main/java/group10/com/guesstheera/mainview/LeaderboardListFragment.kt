package group10.com.guesstheera.mainview

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import group10.com.guesstheera.R
import group10.com.guesstheera.ScoreViewModel
import group10.com.guesstheera.ScoreViewModelFactory
import group10.com.guesstheera.backend.ScoreRepository


class LeaderboardListFragment(private val difficulty: String): Fragment() {
    // apparently kotlin complains if a class and its subclass share names for private fields
    private var leaderboardView: View? = null
    private var highScore: Int? = null
    private lateinit var highScoreStored: SharedPreferences
    private lateinit var highScoreTextView: TextView

    private lateinit var scoreViewModel: ScoreViewModel
    private var listView: ListView? = null
    private lateinit var scoreListAdapter: ScoreListAdapter
    private val LIMIT = 100

    constructor() : this("")


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        leaderboardView = inflater.inflate(R.layout.fragment_leaderboard_list, container, false)
        highScoreTextView = leaderboardView!!.findViewById(R.id.highScoreTV)
        if (difficulty == "Regular"){
            highScoreStored = requireContext().getSharedPreferences("easyModeHighScore", AppCompatActivity.MODE_PRIVATE)
        }
        else{
            highScoreStored = requireContext().getSharedPreferences("hardModeHighScore", AppCompatActivity.MODE_PRIVATE)
        }
        highScore = highScoreStored.getInt("highScore", 0)
        highScoreTextView.setText("High Score: $highScore")

        val repository = ScoreRepository()
        val scoreViewModelFactory = ScoreViewModelFactory(difficulty, repository)
        scoreViewModel = ViewModelProvider(this as ViewModelStoreOwner,
            scoreViewModelFactory)[ScoreViewModel::class.java]

        listView = leaderboardView?.findViewById(R.id.game_leaderboard)
        scoreListAdapter = ScoreListAdapter(requireContext(),
            scoreViewModel.topScores.value ?: emptyList())
        listView?.adapter = scoreListAdapter

        scoreViewModel.topScores.observe(requireActivity()){
                list ->
            Log.d("Leaderboard", "Showing ${list.size} leaderboard listings")
            scoreListAdapter.replace(list)
            scoreListAdapter.notifyDataSetChanged()
        }
        scoreViewModel.loadScore(LIMIT)

        return leaderboardView
    }
}