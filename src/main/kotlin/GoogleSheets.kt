import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

class GoogleSheets {

    companion object {
        const val LUNCH = "A3:H10"
        const val DINNER = "A11:H18"
        const val SHEET_ID = "1JkHSsxDBMSKuGeXD72OmuS89DysLE80WkhazChPDBqE"
//        const val SHEET_ID = "1YvCqBrNw5l4EFNplmpRBFrFJpjl4EALlVNDk3pwp_dQ"

        const val name = "RU Bot"
        const val tokens_path = "tokens"
        const val credentials_path = "/credentials.json"

        private val json = JacksonFactory.getDefaultInstance()
        private val scopes = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY)
        private val http_transport = GoogleNetHttpTransport.newTrustedTransport()
        private val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
    }

    private val credential = getCredentials()

    lateinit var lastLocalUpdate: Date
    val lastSheetUpdate: Date
        get() = df.parse(getRange("B19")[0][0] as String)


    lateinit var dinner: MutableList<MutableList<Any>>
    lateinit var lunch: MutableList<MutableList<Any>>

    init { updateMenus() }

    fun updateMenus(){
        lunch = getRange(LUNCH)
        dinner = getRange(DINNER)
        lastLocalUpdate = df.parse(getRange("B19")[0][0] as String)
    }

    private fun getRange(range: String) = Sheets.Builder(http_transport, json, credential)
        .setApplicationName(name).build()
        .spreadsheets().values().get(SHEET_ID, range)
        .execute().getValues()

    private fun getCredentials(): Credential {

        val input = GoogleSheets::class.java.getResourceAsStream(credentials_path)
            ?: throw FileNotFoundException("Resource not found: $credentials_path")

        val clientSecrets = GoogleClientSecrets.load(json, InputStreamReader(input))

        val flow = GoogleAuthorizationCodeFlow.Builder(http_transport, json, clientSecrets, scopes).run {
            setDataStoreFactory(FileDataStoreFactory(File(tokens_path)))
            accessType = "offline"
            build()
        }

        val receiver = LocalServerReceiver.Builder().run {
            port = 8888
            build()
        }

        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }

}