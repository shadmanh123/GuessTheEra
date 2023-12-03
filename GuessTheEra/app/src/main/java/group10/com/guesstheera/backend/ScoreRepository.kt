package group10.com.guesstheera.backend

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.material.color.utilities.Score
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ScoreRepository {
    private val database = FirebaseFirestore.getInstance()
    private val singlePlayerScoresCollection = database.collection("singleplayer_scores")
    fun addScore(playerName: String, score: Int, callback: (Boolean) -> Unit){
        val scoreData = hashMapOf(
            "playerName" to playerName,
            "score" to score
        )

        singlePlayerScoresCollection.add(scoreData).addOnSuccessListener {
            callback.invoke(true)
        }
            .addOnFailureListener{e ->
                Log.w("Score Repository", "Error adding score", e)
                callback.invoke(false)
            }
    }

    fun getLeaderBoardScores(limit: Long,callback: (List<Score>) -> Unit ){
        singlePlayerScoresCollection.orderBy("score", Query.Direction.DESCENDING)
            .limit(limit).get().addOnSuccessListener { documents ->
                val scores = documents.toObjects(Score::class.java)
                callback.invoke(scores)
            }
            .addOnFailureListener{ exception ->
                Log.d("Score Repository", "$exception")
                callback.invoke(emptyList())
            }
    }

}