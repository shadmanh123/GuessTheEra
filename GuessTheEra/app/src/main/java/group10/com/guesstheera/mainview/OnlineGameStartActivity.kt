package group10.com.guesstheera.mainview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import group10.com.guesstheera.DifficultyUtil
import group10.com.guesstheera.GameActivity
import group10.com.guesstheera.MultiplayerGameActivity
import group10.com.guesstheera.R
import group10.com.guesstheera.personId


var code = "null"
class OnlineGameStartActivity : AppCompatActivity() {
    private lateinit var heading: TextView
    private lateinit var loading: TextView
    private lateinit var gameCode: EditText
    private lateinit var createBtn: Button
    private lateinit var joinBtn: Button
    private lateinit var progressBar: ProgressBar
    private var gameId = ""

    private var gameIntent = ""
    //need to make sure gamemdoes are the same as well in the database
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_game_start)
        heading = findViewById(R.id.tvHead)
        gameCode = findViewById(R.id.edtCode)
        createBtn = findViewById(R.id.btnCreate)
        joinBtn = findViewById(R.id.btnJoin)
        progressBar = findViewById(R.id.idPBLoading)
        loading = findViewById(R.id.loadingText)

        gameIntent = intent.getStringExtra(GameActivity.DIFFICULTY_KEY).toString()
        heading.text = "$gameIntent Mode"


        createBtn.setOnClickListener {
            code = gameCode.text.toString()
            //to be deleted
            personId = "fran"
            if (code != "null" && code != "") {
                createBtn.visibility = View.GONE
                joinBtn.visibility = View.GONE
                gameCode.visibility = View.GONE
                heading.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
                loading.visibility = View.VISIBLE
                val newGameRef = FirebaseDatabase.getInstance().reference.child("games").push()
                gameId = newGameRef.key.toString()
                newGameRef.child("gameCode").setValue(code)
                newGameRef.child("gameType").setValue(gameIntent)
                newGameRef.child("player1").setValue(personId)


                // Listen for player2 to join
                FirebaseDatabase.getInstance().reference.child("games").child(gameId)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.child("player2").exists()) {
                                // Player2 has joined, start the game
                                accepted()
                                FirebaseDatabase.getInstance().reference.child("games")
                                    .child(gameId)
                                    .removeEventListener(this) // Remove listener after player2 joins
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle error
                        }
                    })
            } else {
                Toast.makeText(this, "Please Enter a Valid Game Code", Toast.LENGTH_SHORT).show()
            }
        }

        joinBtn.setOnClickListener {
            //to be deleted
            personId = "NOT fran"
            code = gameCode.text.toString()
            if (code != "null" && code != "") {
                progressBar.visibility = View.VISIBLE
                loading.visibility = View.VISIBLE

                FirebaseDatabase.getInstance().reference.child("games")
                    .orderByChild("gameCode").equalTo(code)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val gameSession = snapshot.children.firstOrNull()
                                if (gameSession != null && !gameSession.child("player2").exists()) {
                                    gameId = gameSession.key.toString() // Set the gameId for joining user
                                    gameSession.ref.child("player2").setValue(personId)
                                    accepted()
                                } else {
                                    Toast.makeText(this@OnlineGameStartActivity, "Game is full or doesn't exist", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this@OnlineGameStartActivity, "Invalid Game Code", Toast.LENGTH_SHORT).show()
                            }
                            resetUiVisibility()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            resetUiVisibility()
                        }
                    })
            } else {
                Toast.makeText(this, "Please Enter a Valid Game Code", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun resetUiVisibility() {
        createBtn.visibility = View.VISIBLE
        joinBtn.visibility = View.VISIBLE
        gameCode.visibility = View.VISIBLE
        heading.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        loading.visibility = View.GONE
    }
    fun accepted() {
        val intent = Intent(this, MultiplayerGameActivity::class.java)
        intent.putExtra(GameActivity.DIFFICULTY_KEY, gameIntent)
        intent.putExtra("UNIQUE_GAME_KEY", gameId)
        startActivity(intent)

//        createBtn.visibility = View.VISIBLE
//        joinBtn.visibility = View.VISIBLE
//        gameCode.visibility = View.VISIBLE
//        heading.visibility = View.VISIBLE
//        progressBar.visibility = View.GONE
//        loading.visibility = View.GONE
    }
}