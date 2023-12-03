package group10.com.guesstheera

import android.util.Log
import group10.com.guesstheera.backend.OwnScoreListener
import group10.com.guesstheera.backend.Score

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import group10.com.guesstheera.backend.ScoreRepository

class ScoreViewModel(private val repository: ScoreRepository): ViewModel() {
    private val _topScores = MutableLiveData<List<Score>>()
    val topScores: LiveData<List<Score>> get() = _topScores

    private val _ownScore: MutableLiveData<Score?> = MutableLiveData<Score?>()
    val ownScore: LiveData<Score?> get() = _ownScore

    init {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid // This is the user's unique ID in Firebase
        if(userId != null){
            repository.addOwnScoreListener(object : OwnScoreListener {
                override fun onUpdate(score: Score?) {
                    _ownScore.value = score
                }
            })
        }
    }

    fun updateOwnScore(score: Int){
        repository.updateOwnScore(score){
            exception ->
            Log.w("Score Repository", "Failed to update own score: ${exception.message}")
        }
    }
    fun loadScore(limit: Long){
        repository.getLeaderBoardScores(limit){
            scores -> _topScores.postValue(scores)
        }
    }
}