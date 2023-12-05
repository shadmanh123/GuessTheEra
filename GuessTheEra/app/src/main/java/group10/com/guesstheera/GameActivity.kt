package group10.com.guesstheera

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.FirebaseDatabase
import group10.com.guesstheera.backend.FirebaseApplication
import group10.com.guesstheera.mainview.GameOptionsFragment.Companion.DIFFICULTY_KEY
import group10.com.guesstheera.mainview.LeaderboardFragment
import group10.com.guesstheera.mainview.MainActivity
import kotlin.math.absoluteValue

class GameActivity : AppCompatActivity() {

    private lateinit var yearSelected: TextView
    private lateinit var slider: SeekBar
    private lateinit var timer: TextView
    private lateinit var gameViewModel: GameViewModel
    private lateinit var image: ImageView
    private lateinit var guess: Button
    private lateinit var score: TextView
    private lateinit var easyModeHighScoreStored: SharedPreferences
    private lateinit var hardModeHighScoreStored: SharedPreferences

    //will need to be set by intent from
    private var gameIntent = ""
    private var currentGuess = 0
    private var currentIndex = 1
    private var currentImage = 0
    private var totalScore = 0
    private var multiplier = 1.0
    private var consecutiveCorrectGuesses = 0

    private var customMode = 0
    private var customTime = 30 //set default time
    private var customGrayscale = false

    private var currentHighScore = 0



    companion object{
        val DIFFICULTY_KEY = "option_difficulty"
        private val SCORE_KEY = "SCORE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.gameplay)
        slider = findViewById(R.id.sliderDecades);
        yearSelected = findViewById(R.id.selectedYear)
        timer = findViewById(R.id.timer)
        guess = findViewById(R.id.btnLockInGuess)
        image = findViewById(R.id.image)
        score = findViewById(R.id.score)


        gameViewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        //get the game type that will be played
        gameIntent = intent.getStringExtra(DIFFICULTY_KEY).toString()

        if (gameIntent == "Regular"){
            startRegularModeGame(30)
        }
        //hard mode set
        else if (gameIntent == "Hard"){
            startHardModeGame(30)
        }
        //custom game mode set. customize base on input parameters
        else{
            customMode = intent.getIntExtra("custom_mode", 1)
            customTime = intent.getIntExtra("custom_time", 30)
            customGrayscale = intent.getBooleanExtra("custom_grayscale", false)

            //player set easy mode
            if (customMode == 0){
                gameIntent = "Hard"
                startHardModeGame(customTime)
            }
            //player set hard mode
            else {
                gameIntent = "Regular"
                startRegularModeGame(customTime)
            }
        }
    }

    private fun startRegularModeGame(time:Int){
        //initiate regular game mode
        gameViewModel.startTimer(time)
        //set grayscale for custom mode
        if (customGrayscale) {
            //create a grayscale ColorMatrix
            val colorMatrix = ColorMatrix()
            colorMatrix.setSaturation(0f) // 0 means grayscale

            //apply the ColorMatrix to a ColorMatrixColorFilter
            val filter = ColorMatrixColorFilter(colorMatrix)
            image.colorFilter = filter
        }
        //set the current image
        image.setImageBitmap(gameViewModel.gameList.first())

        //call helper function when player guesses or they run out of time
        guess.setOnClickListener {
            updateUIOnGuess(time)
        }

        gameViewModel.counter.observe(this, Observer { timeLeft ->
            if (timeLeft > 0) {
                timer.text = "Time: $timeLeft"
            } else {
                updateUIOnGuess(time)
            }
        })

        //use seekbar to get the guess from user
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
    private fun startHardModeGame(time:Int){
        //same as easy mode, calling update hard mode functions instead and setting slider to 120 instead of 6
        gameViewModel.startTimer(time)
        //hard game setting
        slider.max = 120
        slider.progress = 60

        if (customGrayscale) {
            //create a grayscale ColorMatrix
            val colorMatrix = ColorMatrix()
            colorMatrix.setSaturation(0f) // 0 means grayscale

            //apply the ColorMatrix to a ColorMatrixColorFilter
            val filter = ColorMatrixColorFilter(colorMatrix)
            image.colorFilter = filter
        }
        image.setImageBitmap(gameViewModel.gameList.first())

        guess.setOnClickListener {
            updateUIOnGuessHard(time)
        }

        gameViewModel.counter.observe(this, Observer { timeLeft ->
            if (timeLeft > 0) {
                timer.text = "Time: $timeLeft"
            } else {
                updateUIOnGuessHard(time)
            }
        })

        slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(slider: SeekBar, progress: Int, fromUser: Boolean) {
                val year = 1900 + progress
                yearSelected.text = "$year"
            }

            override fun onStartTrackingTouch(slider: SeekBar) {
                //not necessary
            }

            override fun onStopTrackingTouch(slider: SeekBar) {
                //not necessary
            }
        })
    }
    //provided by ChatGPT https://chat.openai.com/share/fc88a8dc-61c2-40c1-b37c-ec21b56ee7bf
    //rounds image year to decade it was taken for comparison in regular
    private fun roundToNearestTen(number: Int): Int {
        return (number / 10) * 10
    }

    //helper function for regular mode, sets slider to decade for comparison
    private fun sliderToDecade(selection: Int): Int {
        return (selection * 10) + 1900
    }

    //helper function for hard mode, sets slider to year for comparison
    private fun sliderToYear(selection: Int): Int {
        return selection + 1900
    }

    private fun checkGuess(imageYear: Int, playerGuess:Int): Int{
        var score: Double
        val yearDiff = (imageYear-playerGuess).absoluteValue

        //3 tiers of scoring for regular as its less sophisticated game mode
        //if correct decade give max points and add to multiplier for consecutive guesses
        if (imageYear == playerGuess){
            score = 1000.0
            if (multiplier > 1){
                consecutiveCorrectGuesses++
                multiplier += 0.1 * consecutiveCorrectGuesses
                score *= multiplier
            }
            else{
                multiplier = 1.1
            }
            Toast.makeText(this, "You are right! the decade is $imageYear", Toast.LENGTH_SHORT).show()
        }

        //award half points for being close and only 100 for 20 y off
        else{
            if (yearDiff == 10 )
                score = 500.0
            else if (yearDiff == 20)
                score = 100.0
            else
                score = 0.0
            consecutiveCorrectGuesses = 0
            multiplier = 1.0
            Toast.makeText(this, "The Photo was taken in  $imageYear", Toast.LENGTH_SHORT).show()
        }
        return score.toInt()
    }

    private fun updateUIOnGuess(time: Int){
        //get year and guess year
        currentGuess  = sliderToDecade(slider.progress)
        currentImage = roundToNearestTen(gameViewModel.yearList[currentIndex-1].toInt())
        Log.d("DEBUG PRINT:", "image rounded $currentImage, Guess from slider $currentGuess")

        //make sure we are still within range of image list
        if (currentIndex < gameViewModel.gameList.size) {
            //lock in current guess & add to total score
            totalScore += checkGuess(currentImage, currentGuess)
            score.text = "Score: $totalScore"

            //iterate list and set slider for next image
            if (customGrayscale) {
                //create a grayscale ColorMatrix
                val colorMatrix = ColorMatrix()
                colorMatrix.setSaturation(0f) // 0 means grayscale

                //apply the ColorMatrix to a ColorMatrixColorFilter
                val filter = ColorMatrixColorFilter(colorMatrix)
                image.colorFilter = filter
            }
            image.setImageBitmap(gameViewModel.gameList[currentIndex])

            gameViewModel.startTimer(time)
            currentIndex++
            slider.progress = 6
        } else {
            // start the download process as soon as the final guess is made
            FirebaseApplication.imageDatabaseViewModel.startDownloadProcess()
            totalScore += checkGuess(currentImage, currentGuess)
            score.text = "Score: $totalScore"
            //maybe create intent for new fragment or activity showing score
            guess.isEnabled = false
            gameViewModel.timerStop()
            //show dialog that game is ended with final score, ability to go to leaderboard or play another game
            showGameFinishedDialog(this)
        }
    }

    //different scoring system based on difficulty
    private fun checkGuessHard(imageYear: Int, playerGuess:Int): Int{
        var score: Double
        val yearDiff = (imageYear - playerGuess).absoluteValue
        var scoreCalculator = 1.0
        //check if guess matches image year, give max points and add to multiplier
        if (imageYear == playerGuess){
            score = 1000.0
            if (multiplier > 1){
                consecutiveCorrectGuesses++
                multiplier += 0.1 * consecutiveCorrectGuesses
                score *= multiplier
            }
            //only multiply for when correct twice in a row
            else{
                multiplier = 1.1
            }
            Toast.makeText(this, "You are right! The Photo was taken in $imageYear", Toast.LENGTH_SHORT).show()
        }
        //if close to correct year then give high score and don't remove multiplier if there is one
        else if (yearDiff < 6){
            //don't increase multiplier but apply multiplier to 925 if its available
            score = 950.0
            consecutiveCorrectGuesses++
            score *= multiplier

            Toast.makeText(this, "Close! The Photo was taken in $imageYear", Toast.LENGTH_SHORT).show()
        }
        //after this point reset multiplier & consecutive correct guesses
        else if (yearDiff in 6..10){
            score = 600.0
            scoreCalculator += 0.1 * (10-yearDiff) //better the guess the more it is multiplied
            score *= scoreCalculator
            //reset multiplier
            consecutiveCorrectGuesses = 0
            multiplier = 1.0
            Toast.makeText(this, "The Photo was taken in $imageYear", Toast.LENGTH_SHORT).show()
        }
        else if (yearDiff in 11..19){
            score = 300.0
            scoreCalculator += 0.1 * (20-yearDiff)
            score *= scoreCalculator
            consecutiveCorrectGuesses = 0
            multiplier = 1.0
            Toast.makeText(this, "The Photo was taken in $imageYear", Toast.LENGTH_SHORT).show()
        }
        else if (yearDiff in 20..25)
            score = 100.0
        else{
            score = 0.0
            consecutiveCorrectGuesses = 0
            multiplier = 1.0
            Toast.makeText(this, "The Photo was taken in  $imageYear", Toast.LENGTH_SHORT).show()
        }
        return score.toInt()
    }

    //mostly the same, except get year of image and year of slider, set slider progress to 60 instead of 6
    private fun updateUIOnGuessHard(time: Int){
        currentGuess  = sliderToYear(slider.progress)
        currentImage = gameViewModel.yearList[currentIndex-1].toInt()
        Log.d("DEBUG PRINT:", "image year $currentImage, Guess from slider $currentGuess")

        if (currentIndex < gameViewModel.gameList.size) {
            //lock in current guess
            totalScore += checkGuessHard(currentImage, currentGuess)
            score.text = "Score: $totalScore"

            if (customGrayscale) {
                //create a grayscale ColorMatrix
                val colorMatrix = ColorMatrix()
                colorMatrix.setSaturation(0f) // 0 means grayscale

                //apply the ColorMatrix to a ColorMatrixColorFilter
                val filter = ColorMatrixColorFilter(colorMatrix)
                image.colorFilter = filter
            }

            image.setImageBitmap(gameViewModel.gameList[currentIndex])

            gameViewModel.startTimer(time)
            currentIndex++
            slider.progress = 60
        } else {
            // start the download process as soon as the final guess is made
            FirebaseApplication.imageDatabaseViewModel.startDownloadProcess()
            //lock in current guess
            totalScore += checkGuessHard(currentImage, currentGuess)
            score.text = "Score: $totalScore"
            //maybe create intent for new fragment or activity showing score
            guess.isEnabled = false
            gameViewModel.timerStop()
            showGameFinishedDialog(this)
        }
    }

    //dialog created upon finishing the game or running out of time
    private fun showGameFinishedDialog(activity: Activity?) {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.game_finish_dialog, null)
        val dialog = AlertDialog.Builder(activity)
            .setView(dialogView)
            .create()
        val finalScore: TextView = dialogView.findViewById(R.id.finalScore)
        val playAgain: Button = dialogView.findViewById(R.id.buttonPlayAgain)
        val showLeaderboard: Button = dialogView.findViewById(R.id.buttonLeaderboard)
        updateHighScore(totalScore, gameIntent)
        finalScore.text = "Score: $totalScore"


        //FirebaseApplication.imageDatabaseViewModel.startDownloadProcess()
        // Set up the button click listeners
        playAgain.setOnClickListener {
            dialog.dismiss()
            //restart the GameActivity
            //reset view model images
            this@GameActivity.recreate()
        }

        showLeaderboard.setOnClickListener {
            dialog.dismiss()
            //route the player to the LeaderboardFragment through MainActivity
            routeToLeaderboardFragment()
        }

        // Display the custom dialog
        dialog.show()
    }

    private fun updateHighScore(totalScore: Int, gameIntent: String) {
        if (gameIntent == "Regular") {
            easyModeHighScoreStored = getSharedPreferences("easyModeHighScore", MODE_PRIVATE)
            currentHighScore = easyModeHighScoreStored.getInt("highScore", 0)
            if (totalScore > currentHighScore){
                val editor = easyModeHighScoreStored.edit()
                editor.putInt("highScore", totalScore)
                editor.apply()
            }
        }
        else{
            hardModeHighScoreStored = getSharedPreferences("hardModeHighScore", MODE_PRIVATE)
            currentHighScore = hardModeHighScoreStored.getInt("highScore", 0)
            if (totalScore > currentHighScore){
                val editor = hardModeHighScoreStored.edit()
                editor.putInt("highScore", totalScore)
                editor.apply()
            }
        }
    }

    private fun routeToLeaderboardFragment() {
        val intent = Intent(this, MainActivity::class.java).apply {
            //to navigate to leaderboard fragment directly
            //putExtra("SCORE_KEY", totalScore)
        }
        startActivity(intent)
    }
}



