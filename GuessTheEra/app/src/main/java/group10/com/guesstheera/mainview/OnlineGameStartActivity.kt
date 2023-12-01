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
import group10.com.guesstheera.GameActivity
import group10.com.guesstheera.MultiplayerGameActivity
import group10.com.guesstheera.R
import group10.com.guesstheera.personId

var isCodeMaker = true
var code = "null"
var codeFound = false
var checkTemp = true
var keyValue: String = "null"

class OnlineGameStartActivity : AppCompatActivity() {
    private lateinit var heading: TextView
    private lateinit var loading: TextView
    private lateinit var gameCode: EditText
    private lateinit var createBtn: Button
    private lateinit var joinBtn: Button
    private lateinit var progressBar: ProgressBar

    private var gameIntent = ""
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
            code = "null"
            codeFound = false
            checkTemp = true
            keyValue = "null"
            code = gameCode.text.toString()
            createBtn.visibility = View.GONE
            joinBtn.visibility = View.GONE
            gameCode.visibility = View.GONE
            heading.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            loading.visibility = View.VISIBLE
            if(code!= "null" && code!="") {
                isCodeMaker = true
                FirebaseDatabase.getInstance().reference.child("games").addValueEventListener(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var check = isValueAvailable(snapshot, code)
                        Handler().postDelayed({
                            if(check){
                                createBtn.visibility = View.VISIBLE
                                joinBtn.visibility = View.VISIBLE
                                gameCode.visibility = View.VISIBLE
                                heading.visibility = View.VISIBLE
                                progressBar.visibility = View.GONE
                                loading.visibility = View.GONE
                            }else{
                                //need to create a check for player ID being null
                                val gameId = FirebaseDatabase.getInstance().reference.child("games").push().key
                                FirebaseDatabase.getInstance().reference.child("games").child(gameId.toString()).child("player1").setValue(personId)
                                FirebaseDatabase.getInstance().reference.child("games").child(gameId.toString()).child("gameCode").setValue(code)
                                FirebaseDatabase.getInstance().reference.child("games").push().setValue(code)

                                isValueAvailable(snapshot, code)
                                checkTemp = false
                                Handler().postDelayed({
                                    accepted()
                                    Toast.makeText(this@OnlineGameStartActivity, "Please do not exit", Toast.LENGTH_SHORT).show()
                                }, 300)
                            }
                        }, 2000)
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            }else{
                createBtn.visibility = View.VISIBLE
                joinBtn.visibility = View.VISIBLE
                gameCode.visibility = View.VISIBLE
                heading.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                loading.visibility = View.GONE
                Toast.makeText(this, "Please Enter a Valid Game Code", Toast.LENGTH_SHORT).show()
            }
        }

        joinBtn.setOnClickListener {
            code = "null"
            codeFound = false
            checkTemp = true
            keyValue = "null"
            code = gameCode.text.toString()
            if(code!= "null" && code!="") {
                createBtn.visibility = View.GONE
                joinBtn.visibility = View.GONE
                gameCode.visibility = View.GONE
                heading.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
                loading.visibility = View.VISIBLE
                isCodeMaker = false
                FirebaseDatabase.getInstance().reference.child("games").addValueEventListener(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var data : Boolean = isValueAvailable(snapshot, code)
                        Handler().postDelayed({
                            if(data){
                                FirebaseDatabase.getInstance().reference.child("games").child(code).child("player2").setValue(personId)
                                codeFound = true
                                accepted()
                                createBtn.visibility = View.VISIBLE
                                joinBtn.visibility = View.VISIBLE
                                gameCode.visibility = View.VISIBLE
                                heading.visibility = View.VISIBLE
                                progressBar.visibility = View.GONE
                                loading.visibility = View.GONE
                            }else{
                                createBtn.visibility = View.VISIBLE
                                joinBtn.visibility = View.VISIBLE
                                gameCode.visibility = View.VISIBLE
                                heading.visibility = View.VISIBLE
                                progressBar.visibility = View.GONE
                                loading.visibility = View.GONE
                                Toast.makeText(this@OnlineGameStartActivity, "Invalid Game Code", Toast.LENGTH_SHORT).show()
                            }
                        }, 2000)
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })

            }else{
                Toast.makeText(this, "Please Enter a Valid Game Code", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun accepted(){
        val intent = Intent(this, MultiplayerGameActivity::class.java)
        //intent.putExtra(MultiplayerGameOptionsFragment.DIFFICULTY_KEY, DifficultyUtil.difficultyOptions[0])
        startActivity(intent)

        createBtn.visibility = View.VISIBLE
        joinBtn.visibility = View.VISIBLE
        gameCode.visibility = View.VISIBLE
        heading.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        loading.visibility = View.GONE
    }

    fun isValueAvailable(snapshot: DataSnapshot, code: String): Boolean{
        val data = snapshot.children
        data.forEach{
            val value = it.value.toString()
            if(value == code){
                keyValue = it.key.toString()
                return true
            }
        }
        return false
    }
}