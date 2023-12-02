package group10.com.guesstheera.backend

import android.app.Application
import com.google.firebase.FirebaseApp

class FirebaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
    }
}