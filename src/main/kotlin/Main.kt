import kotlin.concurrent.fixedRateTimer

fun main() {

    val menu = GoogleSheets()
    val db = Firestore()

    db.write(menu)
    fixedRateTimer(period = 1000L*30L) { verifyAndUpdate(menu, db) }
    while(true){}
}

fun verifyAndUpdate(menu: GoogleSheets, db: Firestore){
    val old = menu.lastLocalUpdate
    val new = menu.lastSheetUpdate

    if(new.compareTo(old) == 1){
        menu.updateMenus()
        db.write(menu)
        println(menu.lastLocalUpdate)
    }
}

