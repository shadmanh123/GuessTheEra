package group10.com.guesstheera.mainview

import android.app.AlertDialog.Builder
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
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
        return builder.create()
    }

    private fun adjustTimer() {
        val timerAdjustmentDialogFragment = TimerAdjustmentDialogFragment.newInstance()
        timerAdjustmentDialogFragment.show(parentFragmentManager, "TimerAdjustmentDialogFragment")
    }
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//
//        _binding = FragmentGameSettingsBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//
//        binding.imageAdjust.setOnClickListener {
//            showYearRangeDialog()
//        }
//
//        val guessAdjustButton = binding.guessAdjust
//        guessAdjustButton.setOnClickListener {
//            showGuessAccuracyOptions()
//        }
//
//        return root
//
//    }
//
//    private fun showGuessAccuracyOptions() {
//        val popupMenu = PopupMenu(requireContext(), binding.guessAdjust)
//        popupMenu.menuInflater.inflate(R.menu.guess_accuracy_menu, popupMenu.menu)
//
//        popupMenu.setOnMenuItemClickListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.optionA -> {
//                    println("the accuracy range will be one year")
//                    true
//                }
//                R.id.optionB -> {
//                    println("the accuracy range will be one decade")
//                    true
//                }
//                else -> false
//            }
//        }
//
//        popupMenu.show()
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.timeAdjust.setOnClickListener {
//            showTimeAdjustDialog()
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun showTimeAdjustDialog() {
//        val minimumValue = 15
//        val maximumValue = 30
//
//        val dialogView = LayoutInflater.from(requireContext())
//            .inflate(R.layout.dialog_time_adjust, null)
//
//        val timeTextView = dialogView.findViewById<TextView>(R.id.timeTextView)
//        val seekBar = dialogView.findViewById<SeekBar>(R.id.timeAdjustSeekBar)
//        seekBar.min = minimumValue
//        seekBar.max = maximumValue
//
//        val currentValue = seekBar.progress
//        timeTextView.text = "$currentValue seconds"
//        val cancelButton: Button = dialogView.findViewById(R.id.btnCancel)
//        val confirmButton: Button = dialogView.findViewById(R.id.btnSet)
//
//
//
//        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                val value = minimumValue + progress
//                timeTextView.text = "$value sec"
//            }
//
//            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
//
//            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
//        })
//
//        val builder = AlertDialog.Builder(requireContext())
//        builder.setView(dialogView).setTitle("Adjust Time")
//            /*.setPositiveButton("Set") { dialog, _ ->
//                binding.timePerRound.text = "${seekBar.progress + minimumValue} seconds"
//                dialog.dismiss()
//            }
//            .setNegativeButton("Cancel") { dialog, _ ->
//                dialog.dismiss()
//            }
//            .setTitle("Adjust Time")*/
//
//        val dialog = builder.create()
//        dialog.show()
//
//        cancelButton.setOnClickListener {
//            dialog.dismiss()
//        }
//        confirmButton.setOnClickListener {
//            dialog.dismiss()
//        }
//    }
//
//    private fun showYearRangeDialog() {
//        val dialog = Dialog(requireContext())
//        dialog.setContentView(R.layout.dialog_year_range)
//
//        val startYearPicker: NumberPicker = dialog.findViewById(R.id.startYearPicker)
//        val endYearPicker: NumberPicker = dialog.findViewById(R.id.endYearPicker)
//
//        startYearPicker.minValue = 1907
//        startYearPicker.maxValue = 2014
//
//        endYearPicker.minValue = 1907
//        endYearPicker.maxValue = 2014
//
//
//        val okButton: Button = dialog.findViewById(R.id.confirmButton)
//        okButton.setText("Confirm")
//        okButton.setOnClickListener {
//            val selectedStartYear = startYearPicker.value
//            val selectedEndYear = endYearPicker.value
//
//            if (selectedStartYear <= selectedEndYear) {
//                handleYearRangeSelected(selectedStartYear, selectedEndYear)
//                dialog.dismiss()
//            } else {
//                Toast.makeText(requireContext(), "Invalid range selected", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        val cancelButton: Button = dialog.findViewById(R.id.cancelButton)
//        cancelButton.setText("Cancel")
//        cancelButton.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        dialog.show()
//    }
//    private fun handleYearRangeSelected(startYear: Int, endYear: Int) {
//        println("Selected range: $startYear - $endYear")
//    }
//
}