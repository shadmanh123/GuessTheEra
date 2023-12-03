package group10.com.guesstheera.backend

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.GlobalScope

class FirebaseApplication : Application() {
    companion object {
        lateinit var imageDatabaseViewModel: ImageDatabaseViewModel
            private set
    }
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
        imageDatabaseViewModel = ViewModelProvider(
            ViewModelStore(),
            ViewModelProvider.AndroidViewModelFactory.getInstance(this)
        ).get(ImageDatabaseViewModel::class.java)
    }
}