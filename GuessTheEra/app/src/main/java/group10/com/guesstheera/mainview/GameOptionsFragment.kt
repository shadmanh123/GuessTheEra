package group10.com.guesstheera.mainview

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import group10.com.guesstheera.DifficultyUtil
import group10.com.guesstheera.R
import group10.com.guesstheera.GameActivity
import group10.com.guesstheera.databinding.SingleplayerFragmentGameOptionsBinding

class GameOptionsFragment: Fragment() {
    private var _binding: SingleplayerFragmentGameOptionsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var regularModeButton: Button
    private lateinit var hardModeButton: Button
    private lateinit var customModeButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = SingleplayerFragmentGameOptionsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        regularModeButton = root.findViewById(R.id.option_regular_mode)
        regularModeButton.setOnClickListener {
            val intent = Intent(context, GameActivity::class.java)
            intent.putExtra(DIFFICULTY_KEY, DifficultyUtil.difficultyOptions[0])
            startActivity(intent)
        }

        hardModeButton = root.findViewById(R.id.option_hard_mode)
        hardModeButton.setOnClickListener {
            // can reuse game-activity here with minor tweaks based on difficulty as key
            val intent = Intent(context, GameActivity::class.java)
            intent.putExtra(DIFFICULTY_KEY, DifficultyUtil.difficultyOptions[1])
            startActivity(intent)

        }

        customModeButton = root.findViewById(R.id.option_custom_mode)
        customModeButton.setOnClickListener {
            val dialogFragment = GameSettingsDialogFragment.newInstance()
            dialogFragment.show(parentFragmentManager, "GameSettingsDialogFragment")
        }

        return root
    }

    companion object{
        val DIFFICULTY_KEY = "option_difficulty"
    }
}