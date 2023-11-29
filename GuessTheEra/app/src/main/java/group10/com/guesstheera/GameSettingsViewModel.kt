package group10.com.guesstheera

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameSettingsViewModel : ViewModel() {

    private val _time = MutableLiveData<Int>()
    val time: LiveData<Int> get() = _time

    private val _mode = MutableLiveData<Int>()
    val mode: LiveData<Int> get() = _mode

    fun setTime(value: Int) {
        _time.value = value
    }

    fun setMode(value: Int) {
        _mode.value = value
    }

}
