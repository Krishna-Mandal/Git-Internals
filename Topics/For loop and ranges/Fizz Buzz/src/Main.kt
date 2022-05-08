const val DIV_BY_5 = 5
const val DIV_BY_3 = 3
const val FIZZ = "Fizz"
const val BUZZ = "Buzz"
const val FIZZ_BUZZ = "$FIZZ$BUZZ"
const val ZERO = 0

fun main() {
    for (i in readln().toInt()..readln().toInt()) {
        val divBy5 = i % DIV_BY_5 == ZERO
        val divBy3 = i % DIV_BY_3 == ZERO
        if (divBy5 && divBy3) {
            println(FIZZ_BUZZ)
        } else if (divBy5) {
            println(BUZZ)
        } else if (divBy3) {
            println(FIZZ)
        } else {
            println(i)
        }
    }
}