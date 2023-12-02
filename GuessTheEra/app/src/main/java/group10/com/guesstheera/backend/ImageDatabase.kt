package group10.com.guesstheera.backend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage

class ImageViewModel: ViewModel() {
    private var storage: FirebaseStorage = Firebase.storage
    private val _imageArray = MutableLiveData<ArrayList<ByteArray>>()

    val imageArray: LiveData<ArrayList<ByteArray>> get() = _imageArray

    fun downloadImagesForGame(seed: Int) {

    }
}