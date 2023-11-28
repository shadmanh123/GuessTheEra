package group10.com.guesstheera.mainview

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import group10.com.guesstheera.R

class GuessAccuracyRangeDialogFragment: DialogFragment() {
    private lateinit var view: View
    private lateinit var guessAccuracyRange: RadioGroup
    private lateinit var setButton: Button
    private lateinit var cancelButton: Button
    private lateinit var gameSettingsStored: SharedPreferences
    companion object{
        const val TAG = "GuessAccuracyRangeDialogFragment"
        fun newInstance(): GuessAccuracyRangeDialogFragment{
            val fragment = GuessAccuracyRangeDialogFragment()
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        view = requireParentFragment().layoutInflater.inflate(R.layout.guess_accuracy_menu, null)
        builder.setView(view)
        guessAccuracyRange = view.findViewById(R.id.guessAcuracyRangeRadioGroup)
        setButton = view.findViewById(R.id.setButton)
        cancelButton = view.findViewById(R.id.cancelButton)
        setButton.setOnClickListener {
            if (guessAccuracyRange.checkedRadioButtonId != -1){
                val selectedOption = when(guessAccuracyRange.checkedRadioButtonId){
                    R.id.yearButton -> "year"
                    R.id.decadeButton -> "decade"
                    else ->""
                }
                if (selectedOption.isNotEmpty()) {
                    gameSettingsStored = requireContext().getSharedPreferences("gameSettingsStored", Context.MODE_PRIVATE)
                    val editor = gameSettingsStored.edit()
                    editor.putString("guessAccuracyRange", selectedOption)
                    editor.apply()
                    dismiss()
                }
            }
            else{
                Toast.makeText(requireContext(), "Select an option", Toast.LENGTH_SHORT).show()
            }
        }
        cancelButton.setOnClickListener {
            dismiss()
        }
        return builder.create()
    }
}