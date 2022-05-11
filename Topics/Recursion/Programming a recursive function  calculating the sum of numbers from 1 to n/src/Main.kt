fun sumRecursive(n: Int): Int {
    return if (n <= 0) {
        0
    } else {
        n + sumRecursive(n - 1)
    }
}

fun main() {
    val n = readLine()!!.toInt()
    print(sumRecursive(n))
}