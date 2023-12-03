package group10.com.guesstheera.backend

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ScoreRepository {
    private val database = FirebaseDatabase.getInstance().reference
    private val multiPlayerScoresCollection = database.child("multiplayer_data")

    fun getLeaderBoardScores(difficulty: String, limit: Int,callback: (List<Score>) -> Unit ){
        multiPlayerScoresCollection.orderByChild("gameType").equalTo(difficulty)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val scoreMap = mutableMapOf<String, Score>()
                    snapshot.children.forEach {
                        child ->
                        val winner = child.child("winner").value

                        val player1 = child.child("player1").child("UID").value as String
                        var playerWon = player1 == winner
                        val player1Record = scoreMap[player1]
                        if(player1Record == null){
                            scoreMap[player1] = Score(user_id = player1,
                                difficulty = difficulty,
                                wins = if(playerWon) 1 else 0,
                                losses = if(playerWon) 0 else 1)
                        }
                        else{
                            if(playerWon) {
                                player1Record.wins += 1
                            }
                            else{
                                player1Record.losses += 1
                            }
                        }

                        val player2 = child.child("player2").child("UID").value as String
                        playerWon = player2 == winner
                        val player2Record = scoreMap[player2]
                        if(player2Record == null){
                            scoreMap[player2] = Score(user_id = player1,
                                difficulty = difficulty,
                                wins = if(playerWon) 1 else 0,
                                losses = if(playerWon) 0 else 1)
                        }
                        else{
                            if(playerWon) {
                                player2Record.wins += 1
                            }
                            else{
                                player2Record.losses += 1
                            }
                        }
                    }
                    val scoreList = scoreMap.values.sortedBy { it.wins }.subList(0, limit)
                    callback.invoke(scoreList)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback.invoke(emptyList())
                }
            })
    }
}