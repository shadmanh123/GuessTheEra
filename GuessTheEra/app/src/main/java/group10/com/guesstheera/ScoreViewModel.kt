package group10.com.guesstheera

import android.media.audiofx.DynamicsProcessing.Limiter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.color.utilities.Score
import group10.com.guesstheera.backend.ScoreRepository

class ScoreViewModel(private val repository: ScoreRepository): ViewModel() {
    private val _topScores = MutableLiveData<List<Score>>()
    val topScores: LiveData<List<Score>> get() = _topScores

    fun addScore(playerName: String, score: Int){
        repository.addScore(playerName, score){result ->
            //Handle result if needed
        }
    }
    fun loadScore(limit: Long){
        repository.getLeaderBoardScores(limit){
            scores -> _topScores.postValue(scores)
        }
    }
}