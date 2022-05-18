fun isPrime(n: Int, i: Int = 2): Boolean {
    if (n == 1) {
        return false
    }
    if (n == 2 || n ==3) {
        return true
    }
    if (n % i == 0) {
        return false
    }
    if (i >= n / 2) {
        return true
    }

    return isPrime(n, i + 1)
}

fun main() {
    val n = readLine()!!.toInt()
    print(isPrime(n))
}