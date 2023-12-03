package group10.com.guesstheera.backend

import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ScoreRepository {
    private val database = FirebaseFirestore.getInstance()
    private val multiPlayerScoresCollection = database.collection("multiplayer_data")

    private var ownScoreDocumentReference: DocumentReference? = null
    private val ownScoreListeners: MutableList<OwnScoreListener> = mutableListOf()
    init {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid // This is the user's unique ID in Firebase
        if(userId != null){
            getPlayerScore(userId){
                ownScoreDocumentReference = it
                onUpdateOwnDocumentReference()
            }
        }
    }

    companion object{
        const val userIdKey = "user_id"
        const val difficultyKey = "difficulty"
        const val winsKey = "wins"
        const val lossesKey = "losses"
    }

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

    fun getPlayerScore(userId: String, callback:(DocumentReference?) -> Unit) {
        multiPlayerScoresCollection.whereEqualTo(userIdKey, userId).get()
            .addOnSuccessListener {
                snapshot ->
                if(snapshot.size() == 1) {
                    val documentId = snapshot.documents[0].id
                    callback.invoke(multiPlayerScoresCollection.document(documentId))
                }
            }
            .addOnFailureListener{e ->
                Log.w("Score Repository", "Error getting player score", e)
                callback.invoke(null)
            }
    }


    // own record read-only helpers

    private fun onUpdateOwnDocumentReference(){
        for(listener in ownScoreListeners){
            ownScoreDocumentReference?.addSnapshotListener{
                    snapshot, _ ->
                listener.onUpdate(snapshot?.toObject(Score::class.java))
            }
        }
    }

    fun addOwnScoreListener(listener: OwnScoreListener){
        ownScoreListeners.plus(listener)
        ownScoreDocumentReference?.addSnapshotListener{
            snapshot, _ ->
            listener.onUpdate(snapshot?.toObject(Score::class.java))
        }
    }


    // own record write-only helpers
    fun updateOwnScore(difficulty: String, isWin: Boolean, onFailureListener: OnFailureListener){
        val isWinInt = if(isWin) 1 else 0
        val isLossInt = 1-isWinInt

        if(ownScoreDocumentReference == null){
            val user = FirebaseAuth.getInstance().currentUser
            val userId = user?.uid ?: return
            val scoreRecord = hashMapOf(
                userIdKey to userId,
                difficultyKey to
                winsKey to isWinInt,
                lossesKey to isLossInt
            )
            multiPlayerScoresCollection.add(scoreRecord).addOnSuccessListener {
                ownScoreDocumentReference = it
                onUpdateOwnDocumentReference()
            }.addOnFailureListener(onFailureListener)
        }
        else{
            ownScoreDocumentReference!!.get().addOnSuccessListener {
                snapshot ->
                val scoreRecord = hashMapOf(
                    userIdKey to snapshot[userIdKey],
                    difficultyKey to difficulty,
                    winsKey to (snapshot[winsKey] as Int) + isWinInt,
                    lossesKey to (snapshot[lossesKey] as Int) + isWinInt
                )
                ownScoreDocumentReference!!.update(scoreRecord)
                    .addOnFailureListener(onFailureListener)
            }.addOnFailureListener(onFailureListener)
        }
    }
}

interface OwnScoreListener{
    fun onUpdate(score: Score?)
}