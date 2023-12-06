package group10.com.guesstheera.mainview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import group10.com.guesstheera.DifficultyUtil
import group10.com.guesstheera.GameActivity
import group10.com.guesstheera.MultiplayerGameActivity
import group10.com.guesstheera.R
import group10.com.guesstheera.personId


class OnlineGameStartActivity : AppCompatActivity() {
    private lateinit var heading: TextView
    private lateinit var loading: TextView
    //private lateinit var gameCode: EditText
    private lateinit var cancelBtn: Button
    private lateinit var createBtn: Button
    private lateinit var joinBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var cancelActivity: Button
    private var gameId = ""
    private var codeFound = false

    private var gameIntent = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_game_start)
        heading = findViewById(R.id.tvHead)
        //gameCode = findViewById(R.id.edtCode)
        createBtn = findViewById(R.id.btnCreate)
        joinBtn = findViewById(R.id.btnJoin)
        progressBar = findViewById(R.id.idPBLoading)
        loading = findViewById(R.id.loadingText)
        cancelBtn = findViewById(R.id.cancelHost)
        cancelActivity = findViewById(R.id.cancelActivity)

        //grab game type from intent
        gameIntent = intent.getStringExtra(GameActivity.DIFFICULTY_KEY).toString()
        heading.text = "$gameIntent Mode"

        //get the user name of player trying to join/create a game
        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email // This is the user's unique ID in Firebase

        if (userEmail != null) {
            personId = userEmail
            Log.d("USER ID", "Current logged-in user's ID: $userEmail")
        } else {
            personId = "Guest"
            Log.d("USER ID", "No user is currently logged in, setting user as guest")
        }
        cancelActivity.visibility = View.VISIBLE
        //if person hasn't logged in make sure to not allow them to play the online mode.

        if (personId == "Guest"){
            createBtn.isEnabled = false
            joinBtn.isEnabled = false
            createBtn.visibility = View.GONE
            joinBtn.visibility = View.GONE
            Toast.makeText(this, "Login to Your Google Account to Play Online", Toast.LENGTH_LONG).show()
        }

        cancelActivity.setOnClickListener{
            finish()
        }

        //create button hosts a game that players who select the join button can play with
        createBtn.setOnClickListener {
            //update UI to show user that they are waiting for player
            loading.text = "Waiting For Player to Join Match"
            createBtn.visibility = View.GONE
            joinBtn.visibility = View.GONE
            //gameCode.visibility = View.GONE
            heading.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            loading.visibility = View.VISIBLE
            cancelBtn.visibility = View.VISIBLE
            cancelActivity.visibility = View.GONE

            //add new game to real time database and input player
            val newGameRef = FirebaseDatabase.getInstance().reference.child("games").push()
            gameId = newGameRef.key.toString()
            newGameRef.child("gameType").setValue(gameIntent)
            newGameRef.child("player1").setValue(personId)

            //listen for player2 to join
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //when player 2 has joined start activity for both parties
                    if (snapshot.child("player2").exists()) {
                        accepted()
                        newGameRef.removeEventListener(this)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    resetUiVisibility()
                }
            }
            newGameRef.addValueEventListener(valueEventListener)

            //make sure to remove database entry if a user stops hosting a game
            cancelBtn.setOnClickListener {
                if (!codeFound) {
                    //remove the game from Firebase
                    newGameRef.removeValue().addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d("CANCEL BUTTON", "Game canceled and removed from Firebase")
                            resetUiVisibility()
                        } else {
                            // Handle error
                            Log.e("CANCEL BUTTON", "Failed to remove game from Firebase", it.exception)
                        }
                    }
                }
            }

            //if the user leaves the screen before player2 joins, delete the game
            lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    Log.d("IN ONDESTRYOY", "killing new game")
                    if (!codeFound) {
                        newGameRef.removeValue()
                    }
                    lifecycle.removeObserver(this)
                }
            })
        }

        //joins a game if someone is currently hosting, if not after 5 seconds of searching it will cancel and tell user there are no games to join
        joinBtn.setOnClickListener {
            //update UI to show user they are searching for a match
            loading.text = "Searching for an open Match..."
            createBtn.visibility = View.GONE
            joinBtn.visibility = View.GONE
            //gameCode.visibility = View.VISIBLE
            heading.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            loading.visibility = View.VISIBLE
            cancelActivity.visibility = View.GONE

            //make sure the game that is being hosted is the right game mode
            FirebaseDatabase.getInstance().reference.child("games")
                .orderByChild("gameType").equalTo(gameIntent)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val openGame = snapshot.children.firstOrNull {
                            it.child("player2").value == null
                        }
                        //if game found then get gameID and set player 2 to the gameID for real time updates and start multiplayer game
                        if (openGame != null) {
                            gameId = openGame.key.toString()
                            openGame.ref.child("player2").setValue(personId)
                            accepted()
                        } else {
                            //show that no open games found after 5 seconds
                            Handler().postDelayed({
                                Toast.makeText(this@OnlineGameStartActivity, "No open games found", Toast.LENGTH_SHORT).show()
                                resetUiVisibility()
                            }, 5000)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        resetUiVisibility()
                    }
                })
        }

    }

    fun resetUiVisibility() {
        createBtn.visibility = View.VISIBLE
        joinBtn.visibility = View.VISIBLE
        //gameCode.visibility = View.VISIBLE
        heading.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        loading.visibility = View.GONE
        cancelBtn.visibility = View.GONE
        cancelActivity.visibility = View.VISIBLE
    }
    //when the game is found run accepted for both player 1 and player 2
    fun accepted() {
        codeFound = true
        val intent = Intent(this, MultiplayerGameActivity::class.java)
        //provide game Id and game mode for the multiplayer game
        intent.putExtra(GameActivity.DIFFICULTY_KEY, gameIntent)
        intent.putExtra("UNIQUE_GAME_KEY", gameId)
        startActivity(intent)
    }
    override fun onResume() {
        super.onResume()
        cancelBtn.setOnClickListener {
            finish()
        }
    }
}