package group10.com.guesstheera

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextClock
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import group10.com.guesstheera.R
import group10.com.guesstheera.mainview.MainActivity

import kotlin.math.absoluteValue

//can only refine the scoring until shad gets the google login to work
class MultiplayerGameActivity : AppCompatActivity() {
//https://chat.openai.com/share/638070d6-c768-43f5-8c90-8cf248bc9f0a
    private lateinit var yearSelected: TextView
    private lateinit var slider: SeekBar
    private lateinit var timer: TextView
    private lateinit var gameViewModel: GameViewModel
    private lateinit var image: ImageView
    private lateinit var guess: Button
    private lateinit var score: TextView

    //will need to be set by intent from
    private var gameIntent = ""
    private var currentGuess = 0
    private var currentIndex = 1
    private var currentImage = 0
    private var totalScore = 0
    private var multiplier = 1.0
    private var consecutiveCorrectGuesses = 0

    private lateinit var gameRef: DatabaseReference
    private var player1Id = ""
    private var player2Id = ""
    private var player1Stage = 0
    private var player2Stage = 0
    private var opponentScore = 0
    private lateinit var opponentTotalScore: TextView
    private var gameId = ""
    private var winner = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiplayer_game)
        slider = findViewById(R.id.sliderDecades);
        yearSelected = findViewById(R.id.selectedYear)
        timer = findViewById(R.id.timer)
        guess = findViewById(R.id.btnLockInGuess)
        image = findViewById(R.id.image)
        score = findViewById(R.id.score)
        opponentTotalScore = findViewById(R.id.opponentScore)


        gameViewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        //get the game type that will be played
        gameIntent = intent.getStringExtra(GameActivity.DIFFICULTY_KEY).toString()
        gameId = intent.getStringExtra("UNIQUE_GAME_KEY").toString()

        gameRef = FirebaseDatabase.getInstance().reference.child("games").child(gameId)
        Log.d("CHECKING GAME KEY", "KEY: $gameId, Mode Selected: $gameIntent")
        if (gameIntent == "Regular"){
            startRegularModeGame(30)
        }
        //hard mode set
        else {
            startHardModeGame(30)
        }

        gameRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    if (player1Id == "" && player2Id =="") {
                        player1Id = snapshot.child("player1").getValue(String::class.java) ?: ""
                        player2Id = snapshot.child("player2").getValue(String::class.java) ?: ""
                    }
                    Log.d("Player names:", "Player 1 pix3: $player1Id, Player 2 pix6: $player2Id")
                    //ensure player IDs are not empty, CANNOT CHECK UNTIL USERNAME IS FIXED
                    if (player1Id.isNotEmpty() && player2Id.isNotEmpty()) {
                        //if player1 id is associated with this instance
                        opponentScore = if (player1Id == personId) {
                            snapshot.child("player2").child("score").getValue(Int::class.java) ?: 0
                        } else {
                            snapshot.child("player1").child("score").getValue(Int::class.java) ?: 0
                        }
                        opponentTotalScore.text = "Opponent Score: $opponentScore"

                        player1Stage = snapshot.child("player1").child("stage").getValue(Int::class.java) ?: 0
                        player2Stage = snapshot.child("player2").child("stage").getValue(Int::class.java) ?: 0
                        Log.d("STAGE OF PLAYER", "P1 Stage: $player1Stage, P2 Stage: $player2Stage")
                        if (player1Stage == 5 && player2Stage == 5) {
                            val p1Score = snapshot.child("player1").child("score").getValue(Int::class.java) ?: 0
                            val p2Score = snapshot.child("player2").child("score").getValue(Int::class.java) ?: 0
                            Log.d("STAGE OF PLAYER", "P1 Score: $p1Score, P2 Score: $p2Score")
                            //if score the same, show as tie
                            if (p1Score > p2Score) {
                                winner = snapshot.child("player1").child("UID").getValue(String::class.java) ?: ""
                                gameRef.child("winner").setValue(winner)
                            }
                            else if (p1Score < p2Score) {
                                winner = snapshot.child("player2").child("UID").getValue(String::class.java) ?: ""
                                gameRef.child("winner").setValue(winner)
                            }
                            else{
                                winner = "tie"
                                gameRef.child("winner").setValue(winner)
                            }
                            showGameFinishedDialog(this@MultiplayerGameActivity, winner)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }


    private fun startRegularModeGame(time:Int){
        //initiate regular game mode
        gameViewModel.startTimer(time)
        //set grayscale for custom mode

        //set the current image
        image.setImageResource(gameViewModel.gameList.first())

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

        image.setImageResource(gameViewModel.gameList.first())

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

            //if player1 id is associated with this instance
            val playerRef = if (player1Id == personId) {
                gameRef.child("player1")
            } else {
                gameRef.child("player2")
            }

            playerRef.child("score").setValue(totalScore)
            playerRef.child("stage").setValue(currentIndex)



            //iterate list and set slider for next image
            image.setImageResource(gameViewModel.gameList[currentIndex])

            gameViewModel.startTimer(time)
            currentIndex++
            slider.progress = 6

        } else {
            totalScore += checkGuess(currentImage, currentGuess)
            score.text = "Score: $totalScore"
            //if player1 id is associated with this instance
            if (player1Id == personId){
                gameRef.child("player1").child("score").setValue(totalScore)
                gameRef.child("player1").child("stage").setValue(currentIndex)
                gameRef.child("player1").child("UID").setValue(player1Id)
            }
            //if player2 id is associated with this instance
            else{
                gameRef.child("player2").child("score").setValue(totalScore)
                gameRef.child("player2").child("stage").setValue(currentIndex)
                gameRef.child("player2").child("UID").setValue(player2Id)
            }


            guess.isEnabled = false
            gameViewModel.timerStop()
            //show dialog that game is ended with final score, ability to go to leaderboard or play another game
            showWaitingDialog(this@MultiplayerGameActivity)
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
            //if player1 id is associated with this instance
            val playerRef = if (player1Id == personId) {
                gameRef.child("player1")
            } else {
                gameRef.child("player2")
            }

            playerRef.child("score").setValue(totalScore)
            playerRef.child("stage").setValue(currentIndex)


            image.setImageResource(gameViewModel.gameList[currentIndex])

            gameViewModel.startTimer(time)
            currentIndex++
            slider.progress = 60
        } else {
            //lock in current guess
            totalScore += checkGuessHard(currentImage, currentGuess)
            score.text = "Score: $totalScore"
            //if player1 id is associated with this instance
            if (player1Id == personId){
                gameRef.child("player1").child("score").setValue(totalScore)
                gameRef.child("player1").child("stage").setValue(currentIndex)
                gameRef.child("player1").child("UID").setValue(player1Id)
            }
            //if player2 id is associated with this instance
            else{
                gameRef.child("player2").child("score").setValue(totalScore)
                gameRef.child("player2").child("stage").setValue(currentIndex)
                gameRef.child("player2").child("UID").setValue(player2Id)
            }


            guess.isEnabled = false
            gameViewModel.timerStop()
            showWaitingDialog(this@MultiplayerGameActivity)
        }
    }

    //dialog created upon finishing the game or running out of time
    private fun showGameFinishedDialog(activity: Activity?, winner: String) {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.multiplayer_game_finish_dialog, null)
        val dialog = AlertDialog.Builder(activity)
            .setView(dialogView)
            .create()
        val finalScore: TextView = dialogView.findViewById(R.id.finalScore)
        val opponentFinalScore: TextView = dialogView.findViewById(R.id.opponentFinalScore)
        val showLeaderboard: Button = dialogView.findViewById(R.id.buttonLeaderboard)
        val showWinner: TextView = dialogView.findViewById(R.id.winnerUID)

        finalScore.text = "Score: $totalScore"
        opponentFinalScore.text = "Opponent Final Score: $opponentScore"
        showWinner.text = "$winner"

        showLeaderboard.setOnClickListener {
            dialog.dismiss()
            //route the player to the LeaderboardFragment through MainActivity
            routeToLeaderboardFragment()
        }

        // Display the custom dialog
        dialog.show()
    }

    private fun showWaitingDialog(activity: Activity?) {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.multiplayer_game_waiting_dialog, null)
        val dialog = AlertDialog.Builder(activity)
            .setView(dialogView)
            .create()
        val finalScore: TextView = dialogView.findViewById(R.id.finalScore)

        finalScore.text = "Score: $totalScore"

        //display the custom dialog
        dialog.show()
    }

    private fun routeToLeaderboardFragment() {
        val intent = Intent(this, MainActivity::class.java).apply {
            //to navigate to leaderboard fragment directly
            //putExtra("SCORE_KEY", totalScore)
        }
        startActivity(intent)
    }
}