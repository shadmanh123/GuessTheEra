package group10.com.guesstheera.mainview

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import group10.com.guesstheera.R

class ImageRangeAdjustmentDialogFragment:  DialogFragment() {
    private lateinit var view: View
    private lateinit var startYearPicker: NumberPicker
    private lateinit var endYearPicker: NumberPicker
    private lateinit var setButton: Button
    private lateinit var cancelButton: Button
    private var startYearPickerEarliestYear: Int = 1900
    private var startYearPickerLatestYear: Int = 2020
    private var endYearPickerEarliestYear: Int = 1900
    private var endYearPickerLatestYear: Int = 2020
    private var selectedStartYear: Int = 1900
    private var selectedEndYear: Int = 2020
    private lateinit var gameSettingsStored: SharedPreferences

    companion object{
        const val TAG = "ImageRangeAdjustmentDialogFragment"
        fun newInstance(): ImageRangeAdjustmentDialogFragment{
            val fragment = ImageRangeAdjustmentDialogFragment()
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        view = requireParentFragment().layoutInflater.inflate(R.layout.dialog_year_range, null)
        builder.setView(view)
        startYearPicker = view.findViewById(R.id.startYearPicker)
        endYearPicker = view.findViewById(R.id.endYearPicker)
        setButton = view.findViewById(R.id.setButton)
        cancelButton = view.findViewById(R.id.cancelButton)
        startYearPicker.minValue = startYearPickerEarliestYear
        startYearPicker.maxValue = startYearPickerLatestYear
        endYearPicker.minValue = endYearPickerEarliestYear
        endYearPicker.maxValue = endYearPickerLatestYear
        setButton.setOnClickListener {
            selectedStartYear = startYearPicker.value
            selectedEndYear = endYearPicker.value
            if (selectedStartYear > selectedEndYear){
                Toast.makeText(requireContext(), "Please choose a start year that is before the selected end year", Toast.LENGTH_SHORT).show()
            }
            else{
                gameSettingsStored = requireContext().getSharedPreferences("gameSettingsStored", Context.MODE_PRIVATE)
                val editor = gameSettingsStored.edit()
                editor.apply{
                    putString("startYear", selectedStartYear.toString())
                    putString("endYear", selectedEndYear.toString())
                    apply()
                }
                dismiss()
            }
        }
        cancelButton.setOnClickListener {
            dismiss()
        }
        return builder.create()
    }

}