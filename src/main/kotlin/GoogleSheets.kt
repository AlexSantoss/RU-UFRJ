import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*



class GoogleSheets {

    companion object {
        const val LUNCH = "A3:H10"
        const val DINNER = "A11:H18"
        const val SHEET_ID = "1JkHSsxDBMSKuGeXD72OmuS89DysLE80WkhazChPDBqE"
//        const val SHEET_ID = "1YvCqBrNw5l4EFNplmpRBFrFJpjl4EALlVNDk3pwp_dQ"

        const val name = "RU Bot"
        const val credentials_path = "/google-sheets.json"

        private val json = JacksonFactory.getDefaultInstance()
        private val scopes = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY)
        private val http_transport = GoogleNetHttpTransport.newTrustedTransport()
        private val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
    }

    private val credential: Credential

    lateinit var lastLocalUpdate: Date
    val lastSheetUpdate: Date
        get() = df.parse(getRange("B19")[0][0] as String)


    lateinit var dinner: MutableList<MutableList<Any>>
    lateinit var lunch: MutableList<MutableList<Any>>

    init {
        credential = getCredentials()
        updateMenus()
    }

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

        return GoogleCredential.fromStream(input).createScoped(scopes)
    }

}