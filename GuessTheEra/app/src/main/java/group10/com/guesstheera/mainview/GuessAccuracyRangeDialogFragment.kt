package group10.com.guesstheera.mainview

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import group10.com.guesstheera.R

class GuessAccuracyRangeDialogFragment: DialogFragment() {
    private lateinit var view: View
    private lateinit var guessAccuracyRange: RadioGroup
    private lateinit var setButton: Button
    private lateinit var cancelButton: Button
    private var selectedOption = 1

    companion object{
        const val TAG = "GuessAccuracyRangeDialogFragment"
        fun newInstance(): GuessAccuracyRangeDialogFragment{
            val fragment = GuessAccuracyRangeDialogFragment()
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        view = requireParentFragment().layoutInflater.inflate(R.layout.dialog_game_mode, null)
        builder.setView(view)
        guessAccuracyRange = view.findViewById(R.id.guessAcuracyRangeRadioGroup)
        setButton = view.findViewById(R.id.setButton)
        cancelButton = view.findViewById(R.id.cancelButton)

        guessAccuracyRange.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = view.findViewById<RadioButton>(checkedId)
            selectedOption = group.indexOfChild(radioButton)
        }

        setButton.setOnClickListener {
            if (guessAccuracyRange.checkedRadioButtonId != -1){
                val result = Bundle().apply {
                    selectedOption?.let { it1 -> putInt("selectedMode", it1) }
                }
                parentFragmentManager.setFragmentResult("modeKey", result)
                dismiss()
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