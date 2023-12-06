package group10.com.guesstheera.backend

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import java.util.Random

class ImageDatabaseViewModel(application: Application): AndroidViewModel(application){
    private var storage: FirebaseStorage = Firebase.storage
    private val _imageArray = MutableLiveData<ArrayList<ByteArray>>()
    private val _imageFilePathsList = MutableLiveData<List<String>>()
    private val downloadState = MutableLiveData<Boolean>()
    private var prevGameImageFilePathsList : List<String>? = null

    val imageArray: LiveData<ArrayList<ByteArray>> get() = _imageArray
    val imageFilePathsList: LiveData<List<String>> get() = _imageFilePathsList
    private val firestore: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    /**
     * gets list holding filepaths to images from firestore as an argument, and seed
     * then shuffle to randomize and take the first 5 as a subset
     * download the images from cloud storage bucket and set them into appropriate index when download task is complete
     * update the live filepaths list for the subset -> to be used to determine the year the photo was taken
     * update the live image array for the subset -> used by GameViewModel
     */
    private fun downloadImagesForGame(imageList: List<String>, seed: Long) {
        //val shuffledImageFilePathList = imageList.shuffled(Random(seed))
        var shuffledImageFilePathList = imageList.shuffled()
        var gameImageFilePathList = shuffledImageFilePathList.take(5)

        if (prevGameImageFilePathsList != null) {
            var commonElements = gameImageFilePathList.intersect(prevGameImageFilePathsList!!.toSet())
            while (commonElements.isNotEmpty()) {
                shuffledImageFilePathList = shuffledImageFilePathList.shuffled()
                gameImageFilePathList = shuffledImageFilePathList.take(5)
                commonElements = gameImageFilePathList.intersect(prevGameImageFilePathsList!!.toSet())
            }
        }
        prevGameImageFilePathsList = gameImageFilePathList

        val storageRef = storage.reference
        val TWO_MB: Long = 2*1024*1024
        val images: ArrayList<ByteArray> = ArrayList()
        val emptyByteArray = ByteArray(0)

        for (index in gameImageFilePathList.indices) {
            val filepath = gameImageFilePathList[index]
            val imageRef = storageRef.child(filepath)
            images.add(index, emptyByteArray)
            imageRef.getBytes(TWO_MB).addOnSuccessListener {

                //place successful download in correct index
                images.set(index, it)
                val taskFinished: Boolean = !images.contains(emptyByteArray)
                if (images.size == gameImageFilePathList.size && taskFinished) {
                    updateImageArrayList(images)
                    updateImageFilePathsList(gameImageFilePathList)
                }
            }.addOnFailureListener {
                Log.d("debug: getting image failed", it.toString())
            }
        }
    }

    fun startDownloadProcess() {
        if (downloadState.value == true) {
            Log.d("debug: download state", "")
            return
        }
        downloadState.value = true
        getImagesDocument()
        downloadState.value = false
    }

    /*fun isDownloading(): LiveData<Boolean> {
        return downloadState
    }*/

    /**
     * gets the document from firestore containing the array of image filepaths,
     * passes filepaths to downloadImagesForGame()
     */
    private fun getImagesDocument() {
        val imageCollection = firestore.collection("images")
        Log.d("debug: getting collection", imageCollection.toString())
        val gameImageDocument = imageCollection.document("game_images")
        Log.d("debug: getting document", gameImageDocument.toString())

        gameImageDocument.get()
            .addOnSuccessListener { documentSnapshot ->
                Log.d("debug: getting document success", documentSnapshot.toString())
                if (documentSnapshot.exists()) {
                    val filepaths = documentSnapshot.get("filepaths") as? List<String>
                    if (filepaths != null) {
                        downloadImagesForGame(filepaths, 0L)
                    } else {
                        Log.d("debug: unable to get filepaths", "")
                    }
                }
            }
            .addOnFailureListener{ e ->
                Log.e("debug: getting document failed", e.toString())
            }
    }

    private fun updateImageFilePathsList(filepaths: List<String>) {
        _imageFilePathsList.value = filepaths
    }
    private fun updateImageArrayList(images: ArrayList<ByteArray>) {
        _imageArray.value = images
    }
}
