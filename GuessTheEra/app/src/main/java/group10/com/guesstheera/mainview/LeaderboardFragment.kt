package group10.com.guesstheera.mainview

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import group10.com.guesstheera.DifficultyUtil
import group10.com.guesstheera.R
import group10.com.guesstheera.databinding.FragmentLeaderboardBinding

class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null

    private val binding get() = _binding!!

    private lateinit var fragmentList: List<Fragment>
    private lateinit var tabAdapter: LeaderboardTabAdapter

    private lateinit var leaderboardViewPager: ViewPager2
    private lateinit var leaderboardTabLayout: TabLayout
    private lateinit var tabLayoutMediator:TabLayoutMediator
    private var highScore: Int? = null
    private lateinit var highScoreStored: SharedPreferences
    private lateinit var highScoreTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        fragmentList = DifficultyUtil.difficultyOptions.map { LeaderboardListFragment(it) }
        tabAdapter = LeaderboardTabAdapter(requireActivity(), fragmentList)
        highScoreTextView = root.findViewById(R.id.highScoreTV)

        leaderboardViewPager = root.findViewById(R.id.leaderboard_page)
        leaderboardViewPager.adapter = tabAdapter

        leaderboardTabLayout = root.findViewById(R.id.leaderboard_tabs)
        val tabConfigurationStrategy =
            TabLayoutMediator.TabConfigurationStrategy { tab: TabLayout.Tab, pos: Int ->
                tab.text = DifficultyUtil.difficultyOptions[pos]
            }
        tabLayoutMediator = TabLayoutMediator(leaderboardTabLayout, leaderboardViewPager, tabConfigurationStrategy)
        tabLayoutMediator.attach()
        highScoreStored = requireContext().getSharedPreferences("HighScore", AppCompatActivity.MODE_PRIVATE)
        highScore = highScoreStored.getInt("highScore", 0)
        highScoreTextView.setText("High Score: $highScore")
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        tabLayoutMediator.detach()
    }

}