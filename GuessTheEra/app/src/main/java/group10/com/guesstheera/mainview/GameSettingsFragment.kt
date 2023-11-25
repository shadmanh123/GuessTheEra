package group10.com.guesstheera.mainview

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build.VERSION_CODES.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import group10.com.guesstheera.databinding.FragmentGameSettingsBinding

class GameSettingsFragment : Fragment() {
    private var _binding: FragmentGameSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGameSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        root

        binding.imageAdjust.setOnClickListener {
            showYearRangeDialog()
        }

        val guessAdjustButton = binding.guessAdjust
        guessAdjustButton.setOnClickListener {
            showGuessAccuracyOptions()
        }

        return root

    }

    private fun showGuessAccuracyOptions() {
        val popupMenu = PopupMenu(requireContext(), binding.guessAdjust)
        popupMenu.menuInflater.inflate(R.menu.guess_accuracy_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.choose_year -> {
                    println("the accuracy range will be one year")
                    true
                }
                R.id.choose_decade -> {
                    println("the accuracy range will be one decade")
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.timeAdjust.setOnClickListener {
            showTimeAdjustDialog()
        }
    }

    private fun showTimeAdjustDialog() {
        val minimumValue = 15
        val maximumValue = 30

        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_time_adjust, null)

        val timeTextView = dialogView.findViewById<TextView>(R.id.timeTextView)
        val seekBar = dialogView.findViewById<SeekBar>(R.id.seekBar)
        seekBar.min = minimumValue
        seekBar.max = maximumValue

        val currentValue = // Get the current value, use some default initially
            seekBar.progress = currentValue - minValue
        timeTextView.text = "$currentValue seconds"

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = minimumValue + progress
                timeTextView.text = "$value sec"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
            .setPositiveButton("Set") { dialog, _ ->
                binding.timePerRound.text = "${seekBar.progress + minimumValue} seconds"
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setTitle("Adjust Time")

        val dialog = builder.create()
        dialog.show()
    }

    private fun showYearRangeDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_year_range)

        val startYearPicker: NumberPicker = dialog.findViewById(R.id.startYearPicker)
        val endYearPicker: NumberPicker = dialog.findViewById(R.id.endYearPicker)

        startYearPicker.minValue = 1907
        startYearPicker.maxValue = 2014

        endYearPicker.minValue = 1907
        endYearPicker.maxValue = 2014


        val okButton: Button = dialog.findViewById(R.id.okButton)
        okButton.setOnClickListener {
            val selectedStartYear = startYearPicker.value
            val selectedEndYear = endYearPicker.value

            if (selectedStartYear <= selectedEndYear) {
                handleYearRangeSelected(selectedStartYear, selectedEndYear)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Invalid range selected", Toast.LENGTH_SHORT).show()
            }
        }

        val cancelButton: Button = dialog.findViewById(R.id.cancelButton)
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
    private fun handleYearRangeSelected(startYear: Int, endYear: Int) {
        println("Selected range: $startYear - $endYear")
    }

}