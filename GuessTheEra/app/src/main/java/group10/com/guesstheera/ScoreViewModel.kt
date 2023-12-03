package group10.com.guesstheera

import group10.com.guesstheera.backend.Score

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import group10.com.guesstheera.backend.ScoreRepository
import java.lang.IllegalArgumentException

class ScoreViewModel(private val difficulty:String,
                     private val repository: ScoreRepository): ViewModel() {
    private val _topScores = MutableLiveData<List<Score>>()
    val topScores: LiveData<List<Score>> get() = _topScores

    fun loadScore(limit: Int){
        repository.getLeaderBoardScores(difficulty, limit){
            scores -> _topScores.postValue(scores)
        }
    }
}

class ScoreViewModelFactory(private val difficulty:String,
                            private val repository: ScoreRepository)
: ViewModelProvider.Factory{
    // shamelessly copied from in-class example
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{ //create() creates a new instance of the modelClass, which is CommentViewModel in this case.
        if(modelClass.isAssignableFrom(ScoreViewModel::class.java))
            return ScoreViewModel(difficulty, repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}