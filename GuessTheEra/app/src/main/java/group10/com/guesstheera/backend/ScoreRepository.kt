package group10.com.guesstheera.backend

import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class ScoreRepository {
    private val database = FirebaseDatabase.getInstance().reference
    private val multiPlayerScoresCollection = database.child("games")
    private val scoreMaps = mapOf(
        "Regular" to mutableMapOf<String, Score>(),
        "Hard" to mutableMapOf<String, Score>())

    fun getLeaderBoardScores(difficulty: String, limit: Int,callback: (List<Score>) -> Unit ){
        val query = multiPlayerScoresCollection.orderByChild("gameType").equalTo(difficulty)
        Log.d("Score Repository", "started listening to difficulty $difficulty")

        query.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("Score Repository", "received new multiplayer game data")
                this@ScoreRepository.onChildAdded(snapshot, difficulty)
                val scoreList = scoreMaps[difficulty]!!.values.sortedBy { -it.wins }
                callback.invoke(scoreList.take(limit))
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("Score Repository", "received multiplayer game data update")
                val scoreMap = scoreMaps[difficulty]!!
                val winner = snapshot.child("winner").value
                // assume winner record already exists, and that only now do we know that they won
                // if it doesn't exist, assume no winner
                var score = scoreMap[winner]
                if(score != null) {
                    score.wins += 1
                    score.losses -= 1
                }

                val scoreList = scoreMaps[difficulty]!!.values.sortedBy { -it.wins }
                callback.invoke(scoreList.take(limit))
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // should never happen
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // don't need to do anything, I guess
            }

            override fun onCancelled(error: DatabaseError) {
                val scoreList = scoreMaps[difficulty]!!.values.sortedBy { -it.wins }
                callback.invoke(scoreList.take(limit))
            }
        })
    }

    fun onChildAdded(child: DataSnapshot, difficulty: String) {
        val scoreMap = scoreMaps[difficulty]!!
        val winner = child.child("winner").value as? String

        val player1 = child.child("player1").child("UID").value as String
        var playerWon = player1 == winner
        val player1Record = scoreMap[player1]
        if (player1Record == null) {
            scoreMap[player1] = Score(
                user_id = player1,
                difficulty = difficulty,
                wins = if (playerWon) 1 else 0,
                losses = if (playerWon) 0 else 1
            )
        } else {
            if (playerWon) {
                player1Record.wins += 1
            } else {
                player1Record.losses += 1
            }
        }

        val player2 = child.child("player2").child("UID").value as String
        playerWon = player2 == winner
        val player2Record = scoreMap[player2]
        if (player2Record == null) {
            scoreMap[player2] = Score(
                user_id = player2,
                difficulty = difficulty,
                wins = if (playerWon) 1 else 0,
                losses = if (playerWon) 0 else 1
            )
        } else {
            if (playerWon) {
                player2Record.wins += 1
            } else {
                player2Record.losses += 1
            }
        }
    }
}