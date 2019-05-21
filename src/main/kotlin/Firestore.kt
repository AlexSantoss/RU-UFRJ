import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import java.io.FileNotFoundException

class Firestore {
    companion object {
        const val accountPath = "bd-firestore.json"
    }

    private val db: com.google.cloud.firestore.Firestore

    init {
        val serviceAccount = Firestore::class.java.getResourceAsStream(accountPath)
            ?: throw FileNotFoundException("Resource not found: $accountPath")

        val credentials = GoogleCredentials.fromStream(serviceAccount)

        val options = FirebaseOptions.Builder()
            .setCredentials(credentials)
            .build()

        FirebaseApp.initializeApp(options)

        db = FirestoreClient.getFirestore()
    }

    fun write(menu: GoogleSheets){
        sendToFirebase("lunch", menu.lunch)
        sendToFirebase("dinner", menu.dinner)
    }

    private fun sendToFirebase(meal: String, raw: MutableList<MutableList<Any>>) {
        val tempHash = hashMapOf<Any, Any>()
        for(day in 1..7){
            for(cat in 1..7){
                tempHash[raw[cat][0]] = raw[cat][day]
            }
            db.collection(raw[0][day].toString().trim()).document(meal).set(tempHash)
        }
    }
}
