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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import group10.com.guesstheera.GameSettingsViewModel
import group10.com.guesstheera.GameViewModel
import group10.com.guesstheera.R
//import group10.com.guesstheera.databinding.FragmentGameSettingsBinding

class GameSettingsDialogFragment : DialogFragment() {
    private lateinit var view: View
    private lateinit var timerButton: Button
    private lateinit var imageYearRangeButton: Button
    private lateinit var guessAccuracyRangeButton: Button
    private lateinit var gameSettingsViewModel: GameSettingsViewModel
    private lateinit var startBtn: Button
    private lateinit var cancelBtn: Button
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
        startBtn = view.findViewById(R.id.saveBtn)
        cancelBtn = view.findViewById(R.id.cancelBtn)

        gameSettingsViewModel = ViewModelProvider(this).get(GameSettingsViewModel::class.java)

        gameSettingsViewModel.time.observe(this, Observer { selectedTime ->
            timerButton.text = "$selectedTime Seconds"
        })

        gameSettingsViewModel.mode.observe(this, Observer { selectedMode ->
            if (selectedMode == 0)
                guessAccuracyRangeButton.text = "Hard"
            else
                guessAccuracyRangeButton.text = "Regular"
        })

        timerButton.setOnClickListener {
            adjustTimer()
        }
        imageYearRangeButton.setOnClickListener {
            adjustImageRange()
        }
        guessAccuracyRangeButton.setOnClickListener {
            adjustGuessRange()
        }

        parentFragmentManager.setFragmentResultListener("timeKey", this) { _, result ->
            result.getInt("selectedTime").let { selectedTime ->
                gameSettingsViewModel.setTime(selectedTime)
            }
        }
        parentFragmentManager.setFragmentResultListener("modeKey", this) { _, result ->
            result.getInt("selectedMode").let { selectedMode ->
                gameSettingsViewModel.setMode(selectedMode)
            }
        }

        startBtn.setOnClickListener{
            TODO()
        }

        cancelBtn.setOnClickListener{
            TODO()
        }

        return builder.create()
    }

    private fun adjustTimer() {
        val timerAdjustmentDialogFragment = TimerAdjustmentDialogFragment.newInstance().apply {
            setTargetFragment(this@GameSettingsDialogFragment, 0)
        }
        timerAdjustmentDialogFragment.show(parentFragmentManager, TimerAdjustmentDialogFragment.TAG)
    }
    private fun adjustImageRange() {
        val imageRangeAdjustmentDialogFragment = ImageRangeAdjustmentDialogFragment.newInstance()
        imageRangeAdjustmentDialogFragment.show(parentFragmentManager, ImageRangeAdjustmentDialogFragment.TAG)
    }
    private fun adjustGuessRange() {
        val guessAccuracyRangeDialogFragment = GuessAccuracyRangeDialogFragment.newInstance().apply {
            setTargetFragment(this@GameSettingsDialogFragment, 0)
        }
        guessAccuracyRangeDialogFragment.show(parentFragmentManager, GuessAccuracyRangeDialogFragment.TAG)
    }
}