package group10.com.guesstheera.mainview

import android.app.AlertDialog.Builder
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import group10.com.guesstheera.DifficultyUtil
import group10.com.guesstheera.GameActivity
import group10.com.guesstheera.GameSettingsViewModel
import group10.com.guesstheera.GameViewModel
import group10.com.guesstheera.R
//import group10.com.guesstheera.databinding.FragmentGameSettingsBinding

class GameSettingsDialogFragment : DialogFragment() {
    private lateinit var view: View
    private lateinit var timerButton: Button
    private lateinit var guessAccuracyRangeButton: Button
    private lateinit var gameSettingsViewModel: GameSettingsViewModel
    private lateinit var startBtn: Button
    private lateinit var cancelBtn: Button
    private lateinit var grayscale: CheckBox

    private var setTime = 30 //set default values for when users don't select options
    private var setMode = 1
    private var setGrayscale = false
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
        guessAccuracyRangeButton = view.findViewById(R.id.guessAdjustBtn)
        startBtn = view.findViewById(R.id.saveBtn)
        cancelBtn = view.findViewById(R.id.cancelBtn)
        grayscale = view.findViewById(R.id.checkBox)

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

        guessAccuracyRangeButton.setOnClickListener {
            adjustGuessRange()
        }

        grayscale.setOnCheckedChangeListener { _, isChecked ->
            setGrayscale = isChecked
        }

        parentFragmentManager.setFragmentResultListener("timeKey", this) { _, result ->
            result.getInt("selectedTime").let { selectedTime ->
                gameSettingsViewModel.setTime(selectedTime)
                setTime = selectedTime
            }
        }
        parentFragmentManager.setFragmentResultListener("modeKey", this) { _, result ->
            result.getInt("selectedMode").let { selectedMode ->
                gameSettingsViewModel.setMode(selectedMode)
                setMode = selectedMode
            }
        }

        startBtn.setOnClickListener{
            val intent = Intent(context, GameActivity::class.java)

            intent.putExtra(GameOptionsFragment.DIFFICULTY_KEY, "custom")
            intent.putExtra("custom_time", setTime)
            intent.putExtra("custom_grayscale", setGrayscale)
            intent.putExtra("custom_mode", setMode)
            Log.d("SETTINGS FOR CUSTOM GAME", "Timer: $setTime, Grayscale: $setGrayscale, Mode: $setMode")
            dismiss()
            startActivity(intent)
        }

        cancelBtn.setOnClickListener{
            dismiss()
        }

        return builder.create()
    }

    private fun adjustTimer() {
        val timerAdjustmentDialogFragment = TimerAdjustmentDialogFragment.newInstance().apply {
            setTargetFragment(this@GameSettingsDialogFragment, 0)
        }
        timerAdjustmentDialogFragment.show(parentFragmentManager, TimerAdjustmentDialogFragment.TAG)
    }

    private fun adjustGuessRange() {
        val guessAccuracyRangeDialogFragment = GuessAccuracyRangeDialogFragment.newInstance().apply {
            setTargetFragment(this@GameSettingsDialogFragment, 0)
        }
        guessAccuracyRangeDialogFragment.show(parentFragmentManager, GuessAccuracyRangeDialogFragment.TAG)
    }
}