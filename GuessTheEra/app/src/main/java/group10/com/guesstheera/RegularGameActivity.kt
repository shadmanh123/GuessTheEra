package group10.com.guesstheera

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class RegularGameActivity : AppCompatActivity() {

    private lateinit var yearSelected: TextView
    private lateinit var slider: SeekBar
    private lateinit var timer: TextView
    private lateinit var gameViewModel: GameViewModel
    private lateinit var image: ImageView
    private lateinit var guess: Button

    private var currentIndex = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.gameplay)
        
        slider = findViewById(R.id.sliderDecades);
        yearSelected = findViewById(R.id.selectedYear)
        timer = findViewById(R.id.timer)
        guess = findViewById(R.id.btnLockInGuess)
        image = findViewById(R.id.image)

        gameViewModel = ViewModelProvider(this).get(GameViewModel::class.java)
        gameViewModel.startTimer()

        image.setImageResource(gameViewModel.gameList.first())

        //convert map to list of strings


        guess.setOnClickListener {
            //Check if the currentIndex is within the bounds of the list
            if (currentIndex < gameViewModel.gameList.size) {
                //Set the image resource to the current index
                image.setImageResource(gameViewModel.gameList[currentIndex])

                //start timer
                gameViewModel.startTimer()
                Log.d("DEBUG PRINT:", gameViewModel.yearList.toString())
                //val decade = 1900 + (slider.progress * 10)
                Toast.makeText(this, "The Photo was taken in ${gameViewModel.yearList[currentIndex-1]}", Toast.LENGTH_SHORT).show()
                //Increment the index for the next click
                currentIndex++
                slider.progress = 6
            } else {
                //at end of list
                //Option 1: Reset to the start of the list
                //currentIndex = 0

                //Option 2: Do nothing or disable the button, etc.
                Toast.makeText(this, "The Photo was taken in ${gameViewModel.yearList[currentIndex-1]}", Toast.LENGTH_SHORT).show()
                guess.isEnabled = false
            }
        }


        gameViewModel.counter.observe(this, Observer { timeLeft ->
            // Update the timer TextView to show the remaining time
            timer.text = if (timeLeft > 0) {
                "Time: $timeLeft"
            } else {
                // When countdown reaches zero, you can handle any additional actions here
                "Time's up!"
            }
        })

        slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(slider: SeekBar, progress: Int, fromUser: Boolean) {
                val decade = 1900 + (progress * 10)
                yearSelected.text = "$decade 's"
            }

            override fun onStartTrackingTouch(slider: SeekBar) {
                //not necessary
            }

            override fun onStopTrackingTouch(slider: SeekBar) {
                //not necessary
            }
        })
    }

}


