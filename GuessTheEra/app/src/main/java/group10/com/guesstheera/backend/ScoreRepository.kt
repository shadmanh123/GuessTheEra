package group10.com.guesstheera.backend

import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ScoreRepository {
    private val database = FirebaseDatabase.getInstance().reference
    private val multiPlayerScoresCollection = database.child("multiplayer_data")

    fun getLeaderBoardScores(difficulty: String, limit: Long,callback: (List<Score>) -> Unit ){
        multiPlayerScoresCollection.whereEqualTo(difficultyKey, difficulty)
            .orderBy(winsKey, Query.Direction.DESCENDING)
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