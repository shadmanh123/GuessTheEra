package group10.com.guesstheera.mainview

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import group10.com.guesstheera.R

class TimerAdjustmentDialogFragment: DialogFragment(), OnSeekBarChangeListener{
    private lateinit var view: View
    private lateinit var timeAdjustSeekBar: SeekBar
    private lateinit var timeTextView: TextView
    private lateinit var setButton: Button
    private lateinit var cancelButton: Button
    private var minimumTime: Int = 10
    private var maximumTime: Int = 60
    private var desiredTime: Int? = null
    private var data: String = minimumTime.toString()

    companion object{
        const val TAG = "TimerAdjustDialogFragment"
        fun newInstance(): TimerAdjustmentDialogFragment {
            return TimerAdjustmentDialogFragment()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        view = requireParentFragment().layoutInflater.inflate(R.layout.dialog_time_adjust, null)
        builder.setView(view)
        timeAdjustSeekBar = view.findViewById(R.id.timeAdjustSeekBar)
        timeTextView = view.findViewById(R.id.timeTextView)
        setButton = view.findViewById(R.id.btnSet)
        cancelButton = view.findViewById(R.id.btnCancel)
        timerAdjustment()
        setButton.setOnClickListener {

            val result = Bundle().apply {
                desiredTime?.let { it1 -> putInt("selectedTime", it1) }
            }
            parentFragmentManager.setFragmentResult("timeKey", result)

            dismiss()
        }
        cancelButton.setOnClickListener {
            dismiss()
        }
        return builder.create()
    }

    private fun timerAdjustment() {
        timeAdjustSeekBar.min = minimumTime
        timeAdjustSeekBar.max = maximumTime
        desiredTime = timeAdjustSeekBar.progress
        timeTextView.setText("$desiredTime seconds")
        timeAdjustSeekBar.setOnSeekBarChangeListener(this)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        desiredTime = progress
        timeTextView.text = "$desiredTime seconds"
        data = desiredTime.toString()
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
    }
}