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

        gameIntent = intent.getStringExtra(GameActivity.DIFFICULTY_KEY).toString()
        heading.text = "$gameIntent Mode"

        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email // This is the user's unique ID in Firebase

        if (userEmail != null) {
            personId = userEmail
            Log.d("USER ID", "Current logged-in user's ID: $userEmail")
        } else {
            personId = "Guest"
            Log.d("USER ID", "No user is currently logged in, setting user as guest")
        }

        if (personId == "Guest"){
            createBtn.isEnabled = false
            joinBtn.isEnabled = false
            createBtn.visibility = View.GONE
            joinBtn.visibility = View.GONE
            cancelActivity.visibility = View.VISIBLE
            Toast.makeText(this, "Login to Your Google Account to Play Online", Toast.LENGTH_LONG).show()
        }

        cancelActivity.setOnClickListener{
            finish()
        }

        createBtn.setOnClickListener {
            loading.text = "Waiting For Player to Join Match"
            createBtn.visibility = View.GONE
            joinBtn.visibility = View.GONE
            //gameCode.visibility = View.GONE
            heading.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            loading.visibility = View.VISIBLE
            cancelBtn.visibility = View.VISIBLE

            val newGameRef = FirebaseDatabase.getInstance().reference.child("games").push()
            gameId = newGameRef.key.toString()
            newGameRef.child("gameType").setValue(gameIntent)
            newGameRef.child("player1").setValue(personId)

            // Listen for player2 to join
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
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

            // If the user leaves the screen before player2 joins, delete the game
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

        joinBtn.setOnClickListener {
            loading.text = "Searching for an open Match..."
            createBtn.visibility = View.GONE
            joinBtn.visibility = View.GONE
            //gameCode.visibility = View.VISIBLE
            heading.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            loading.visibility = View.VISIBLE
            //cancelBtn.visibility = View.VISIBLE

            FirebaseDatabase.getInstance().reference.child("games")
                .orderByChild("gameType").equalTo(gameIntent)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val openGame = snapshot.children.firstOrNull {
                            it.child("player2").value == null
                        }
                        if (openGame != null) {
                            gameId = openGame.key.toString()
                            openGame.ref.child("player2").setValue(personId)
                            accepted()
                        } else {
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
    }
    fun accepted() {
        codeFound = true
        val intent = Intent(this, MultiplayerGameActivity::class.java)
        intent.putExtra(GameActivity.DIFFICULTY_KEY, gameIntent)
        intent.putExtra("UNIQUE_GAME_KEY", gameId)
        startActivity(intent)
    }
}