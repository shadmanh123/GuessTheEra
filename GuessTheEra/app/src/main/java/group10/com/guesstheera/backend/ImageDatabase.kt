package group10.com.guesstheera.backend

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage

class ImageViewModel: ViewModel() {
    private var storage: FirebaseStorage = Firebase.storage
    private val _imageArray = MutableLiveData<ArrayList<ByteArray>>()

    val imageArray: LiveData<ArrayList<ByteArray>> get() = _imageArray
    val firestore: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    /*fun downloadImagesForGame(imageList: List<String>, seed: Int) {
        val storage = Firebase.storage
        val storageRef = storage.reference
    }

    fun startDownloadProcess() {
        getImagesDocument()
    }
    fun getImagesDocument() : List<String>? {
        val imageCollection = firestore.collection("images")
        val gameImageDocument = imageCollection.document("game_images")

        gameImageDocument.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val filepaths = documentSnapshot.get("filepaths") as? List<String>
                    if (filepaths != null) {
                        downloadImagesForGame(filepaths, 0)
                    } else {
                        Log.d("debug: unable to get filepaths", "")
                    }
                }
            }
    }*/
}
