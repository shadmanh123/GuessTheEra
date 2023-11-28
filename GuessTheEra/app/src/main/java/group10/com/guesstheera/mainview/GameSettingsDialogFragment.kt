package group10.com.guesstheera.mainview

import android.app.AlertDialog.Builder
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.DialogFragment
import group10.com.guesstheera.R
//import group10.com.guesstheera.databinding.FragmentGameSettingsBinding

class GameSettingsDialogFragment : DialogFragment() {
//    private var _binding: FragmentGameSettingsBinding? = null
//    private val binding get() = _binding!!
    private lateinit var view: View
    private lateinit var timerButton: Button
    private lateinit var imageYearRangeButton: Button
    private lateinit var guessAccuracyRangeButton: Button
    private lateinit var gameSettingsStored: SharedPreferences
    companion object{
        fun newInstance(): GameSettingsDialogFragment{
            val fragment = GameSettingsDialogFragment()
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = Builder(requireActivity())
        view = requireActivity().layoutInflater.inflate(R.layout.game_settings_dialog, null)
        builder.setView(view)
        timerButton = view.findViewById(R.id.timeAdjustBtn)
        imageYearRangeButton = view.findViewById(R.id.imageAdjustBtn)
        guessAccuracyRangeButton = view.findViewById(R.id.guessAdjustBtn)
        timerButton.setOnClickListener {
            adjustTimer()
        }
        imageYearRangeButton.setOnClickListener {
            adjustImageRange()
        }
        guessAccuracyRangeButton.setOnClickListener {
            adjustGuessRange()
        }
        return builder.create()
    }

    private fun adjustTimer() {
        val timerAdjustmentDialogFragment = TimerAdjustmentDialogFragment.newInstance()
        timerAdjustmentDialogFragment.show(parentFragmentManager, TimerAdjustmentDialogFragment.TAG)
    }
    private fun adjustImageRange() {
        val imageRangeAdjustmentDialogFragment = ImageRangeAdjustmentDialogFragment.newInstance()
        imageRangeAdjustmentDialogFragment.show(parentFragmentManager, ImageRangeAdjustmentDialogFragment.TAG)
    }
    private fun adjustGuessRange() {
        val guessAccuracyRangeDialogFragment = GuessAccuracyRangeDialogFragment.newInstance()
        guessAccuracyRangeDialogFragment.show(parentFragmentManager, GuessAccuracyRangeDialogFragment.TAG)
    }
}