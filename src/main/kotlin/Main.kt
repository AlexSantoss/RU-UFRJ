import kotlin.concurrent.fixedRateTimer
import kotlin.system.measureTimeMillis



fun main() {
    val menu = GoogleSheets()

    fixedRateTimer(period = 1000L) { verifyAndUpdate(menu) }
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

