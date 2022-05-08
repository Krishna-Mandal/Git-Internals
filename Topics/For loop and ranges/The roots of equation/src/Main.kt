const val MIN = 0
const val MAX = 1000
fun main() {
    val a = readln().toInt()
    val b = readln().toInt()
    val c = readln().toInt()
    val d = readln().toInt()

    for (index in MIN..MAX) {
        val first = a * index * index * index
        val second = b * index * index
        val third = c * index

        if (first + second + third + d == 0) {
            println(index)
        }
    }
}