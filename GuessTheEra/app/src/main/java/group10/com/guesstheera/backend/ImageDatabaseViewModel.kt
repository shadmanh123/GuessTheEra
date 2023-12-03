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

    val imageArray: LiveData<ArrayList<ByteArray>> get() = _imageArray
    val imageFilePathsList: LiveData<List<String>> get() = _imageFilePathsList
    private val firestore: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    /**
     * gets document holding filepaths to images from firestore as an argument, and seed
     * then shuffle to randomize and take the first 5 as a subset
     * update the live filepaths list for the subset -> to be used to determine the year the photo was taken
     */
    private fun downloadImagesForGame(imageList: List<String>, seed: Long) {
        val shuffledImageFilePathList = imageList.shuffled(Random(seed))
        val gameImageFilePathList = shuffledImageFilePathList.take(5)
        updateImageFilePathsList(gameImageFilePathList)

        val storageRef = storage.reference
        val ONE_MB: Long = 1024*1024
        val images: ArrayList<ByteArray> = ArrayList()

        for (filepath in gameImageFilePathList) {
            val imageRef = storageRef.child(filepath)
            imageRef.getBytes(ONE_MB).addOnSuccessListener {
                //Log.d("debug: getting image from storage bucket", it.toString())
                images.add(it)
                //Log.d("debug: images list size inside", images.size.toString())
                if (images.size == gameImageFilePathList.size) {
                    updateImageArrayList(images)
                }
            }.addOnFailureListener {
                Log.d("debug: getting image failed", it.toString())
            }
        }
    }

    private fun updateImageFilePathsList(filepaths: List<String>) {
        _imageFilePathsList.value = filepaths
    }
    private fun updateImageArrayList(images: ArrayList<ByteArray>) {
        _imageArray.value = images
    }

    fun startDownloadProcess() {
        getImagesDocument()
    }
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
}
