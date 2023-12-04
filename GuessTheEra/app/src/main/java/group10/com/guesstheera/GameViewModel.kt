package group10.com.guesstheera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import group10.com.guesstheera.backend.FirebaseApplication
import group10.com.guesstheera.backend.ImageDatabaseViewModel

class GameViewModel : ViewModel() {

    private val _counter = MutableLiveData<Int>()
    private var roundCounter: Int = 0
    private var isGameRunning: Boolean = false
    var imageArrayList: ArrayList<ByteArray>? = ArrayList()
    var imageFilePaths: List<String>? = listOf()
    private val imageDatabaseViewModel = FirebaseApplication.imageDatabaseViewModel

    val counter: LiveData<Int> get() = _counter
    var gameList: MutableList<Bitmap> = mutableListOf()
    var yearList: MutableList<String> = mutableListOf()
    private val imageDataObserver: Observer<ArrayList<ByteArray>> = Observer {
        if (!isGameRunning()) {
            Log.d("debug: updating image list", it.toString())
            imageArrayList = it
            convertImageByteArrayToBitMap()
        }
    }
    private val filePathObserver: Observer<List<String>> = Observer {
        if (!isGameRunning()) {
            Log.d("debug: updating filepath list", it.toString())
            imageFilePaths = it
            convertFilePathsToYear()
        }
    }

    //start observing the image and file path lists so that they are updated when user wants a new game
    init {
        imageDatabaseViewModel.imageArray.observeForever(imageDataObserver)
        imageDatabaseViewModel.imageFilePathsList.observeForever(filePathObserver)
    }

    /**
     * takes the list of file paths and converts them into the years each image was taken
     */
    private fun convertFilePathsToYear() {
        if (imageFilePaths!=null) {
            if (yearList.size>=5) {
                yearList.clear()
            }
            for (i in imageFilePaths!!.indices) {
                val tokens: List<String> = imageFilePaths!![i].split("_")
                val yearExtension: List<String> = tokens[tokens.size-1].split(".")
                yearList.add(i, yearExtension[0])
                Log.d("debug: list size", yearList.size.toString())
            }
        }
    }

    /**
     * takes the list of image byteArrays and converts them to Bitmaps to be displayed by the activity
     */
    private fun convertImageByteArrayToBitMap() {
        if (imageArrayList!=null) {
            if (gameList.size>=5) {
                gameList.clear()
            }
            for ((index, image) in imageArrayList!!.withIndex()) {
                val bitmap: Bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
                gameList.add(index, bitmap)
                Log.d("debug: list size", gameList.size.toString())
            }
        }
    }

    private fun isGameRunning(): Boolean {
        Log.d("debug: game state", "$isGameRunning $roundCounter")
        return isGameRunning && roundCounter in 1..5
    }

    //created handler and timer functions with the help of ThreadExampleKotlin
    private var timerHandler: Handler? = null
    private val timerRunnable = object : Runnable {
        override fun run() {
            // Decrement the counter
            val currentCount = _counter.value
            if (currentCount != null) {
                _counter.postValue(currentCount - 1)
            }
            // Post the next decrement to be run after 1 second
            timerHandler?.postDelayed(this, 1000)

        }
    }

    fun startTimer(time: Int) {
        // Initialize the handler if not already done
        if (timerHandler == null) {
            timerHandler = Handler(Looper.getMainLooper())
            isGameRunning = true
        }
        roundCounter++
        Log.d("debug: round counter", roundCounter.toString())
        Log.d("debug: round counter", isGameRunning.toString())
        // Reset the counter value
        _counter.value = time

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
        roundCounter = 0
        isGameRunning = false
    }

    override fun onCleared() {
        super.onCleared()
        timerHandler?.removeCallbacks(timerRunnable)
        imageDatabaseViewModel.imageArray.removeObserver(imageDataObserver)
        imageDatabaseViewModel.imageFilePathsList.removeObserver(filePathObserver)
    }
}
