fun main() {
    var len = readln().toInt()
    var lenList = mutableListOf<Int>()
    var count = 0
    var lastNum = Int.MIN_VALUE

    for (index in 1..len) {
        var current = readln().toInt()
        if (current >= lastNum) {
            ++count
            lastNum = current
        } else {
            lenList.add(count)
            count = 1
            lastNum = current
        }
    }
    lenList.add(count)

    println(lenList.maxOf { it })
}