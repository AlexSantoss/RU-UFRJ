import kotlin.concurrent.fixedRateTimer
import kotlin.system.measureTimeMillis
import com.google.cloud.firestore.FirestoreOptions


fun main() {

    val menu = GoogleSheets()
    val db = Firestore()

    db.write(menu)
    fixedRateTimer(period = 1000L*60L*30L) { verifyAndUpdate(menu) }
    while(true){}
}

fun verifyAndUpdate(menu: GoogleSheets){
    val old = menu.lastLocalUpdate
    val new = menu.lastSheetUpdate

    if(new.compareTo(old) == 1){
        menu.updateMenus()
        println(menu.lastLocalUpdate)
    }
}

