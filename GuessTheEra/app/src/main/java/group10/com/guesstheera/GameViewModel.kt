package group10.com.guesstheera

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    private val _counter = MutableLiveData<Int>()
    val counter: LiveData<Int> get() = _counter
    var gameList: List<Int> = listOf(0,0,0,0,0)
    private var imageGameMap: Map<Int, String> = mapOf()
    var yearList: List<String> = listOf()
    private val imageList = listOf(
        R.drawable.p_1907,
        R.drawable.p_1909,
        R.drawable.p_1910,
        R.drawable.p_1912,
        R.drawable.p_1930,
        R.drawable.p_1943,
        R.drawable.p_1944,
        R.drawable.p_1945,
        R.drawable.p_1948,
        R.drawable.p_1956,
        R.drawable.p_1957,
        R.drawable.p_1960,
        R.drawable.p_1961,
        R.drawable.p_1963,
        R.drawable.p_1967,
        R.drawable.p_1975,
        R.drawable.p_1978,
        R.drawable.p_1979,
        R.drawable.p_1984,
        R.drawable.p_1986,
        R.drawable.p_1989,
        R.drawable.p_1995,
        R.drawable.p_1996,
        R.drawable.p_2003,
        R.drawable.p_2005,
        R.drawable.p_2007,
        R.drawable.p_2011,
        R.drawable.p_2012,
        R.drawable.p_2013,
        R.drawable.p_2014
    )
    private val imageYearMap = mapOf(
        R.drawable.p_1907 to "1907",
        R.drawable.p_1909 to "1909",
        R.drawable.p_1910 to "1910",
        R.drawable.p_1912 to "1912",
        R.drawable.p_1930 to "1930",
        R.drawable.p_1943 to "1943",
        R.drawable.p_1944 to "1944",
        R.drawable.p_1945 to "1945",
        R.drawable.p_1948 to "1948",
        R.drawable.p_1956 to "1956",
        R.drawable.p_1957 to "1957",
        R.drawable.p_1960 to "1960",
        R.drawable.p_1961 to "1961",
        R.drawable.p_1963 to "1963",
        R.drawable.p_1967 to "1967",
        R.drawable.p_1975 to "1975",
        R.drawable.p_1978 to "1978",
        R.drawable.p_1979 to "1979",
        R.drawable.p_1984 to "1984",
        R.drawable.p_1986 to "1986",
        R.drawable.p_1989 to "1989",
        R.drawable.p_1995 to "1995",
        R.drawable.p_1996 to "1996",
        R.drawable.p_2003 to "2003",
        R.drawable.p_2005 to "2005",
        R.drawable.p_2007 to "2007",
        R.drawable.p_2011 to "2011",
        R.drawable.p_2012 to "2012",
        R.drawable.p_2013 to "2013",
        R.drawable.p_2014 to "2014"
    )

    init {
        gameList = getRandomSubList(imageList, 5)
        imageGameMap = createImageNamesMap(gameList)
        yearList = imageGameMap.values.toList()
    }

    // Function to get a random sublist of 5 for now
    private fun getRandomSubList(list: List<Int>, size: Int): List<Int> {
        return list.shuffled().take(size)
    }
    //chatGPT assisted with creating random list of 5 and a mapper to get the strings https://chat.openai.com/share/47bb1f9b-e3b0-455c-b52d-9c6c2224154d
    private fun createImageNamesMap(selectedList: List<Int>): Map<Int, String> {
        val map = mutableMapOf<Int, String>()
        for (imageId in selectedList) {
            imageYearMap[imageId]?.let { name ->
                map[imageId] = name
            }
        }
        return map
    }

    //created handler and timer functions with the help of ThreadExampleKotlin
    private var timerHandler: Handler? = null
    private val timerRunnable = object : Runnable {
        override fun run() {
            // Decrement the counter
            val currentCount = _counter.value ?: 30
            _counter.postValue(currentCount - 1)
            // Post the next decrement to be run after 1 second
            timerHandler?.postDelayed(this, 1000)

        }
    }

    fun startTimer() {
        // Initialize the handler if not already done
        if (timerHandler == null) {
            timerHandler = Handler(Looper.getMainLooper())
        }

        // Reset the counter value
        _counter.value = 30

        // Remove any existing callbacks to prevent multiple timers running
        timerHandler?.removeCallbacks(timerRunnable)

        // Start the timer using the handler
        timerHandler?.postDelayed(timerRunnable, 1000)
    }

    fun timerStop() {
        if (timerHandler == null) {
            timerHandler = Handler(Looper.getMainLooper())
        }
        timerHandler?.removeCallbacks(timerRunnable)
    }

    override fun onCleared() {
        super.onCleared()
        timerHandler?.removeCallbacks(timerRunnable)
    }
}
