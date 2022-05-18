const val STEP = 1
fun main() {
    val min = readln().toInt()
    val max = readln().toInt()

    val minSum = (min - STEP) * min / 2
    val maxSum = max * (max + 1) / 2

    println(maxSum - minSum)
}