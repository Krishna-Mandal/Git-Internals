package gitinternals

import java.io.File
import java.io.FileInputStream
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.zip.InflaterInputStream

fun main() {
    println("Enter .git directory location:")
    val gitDirectoryPath = readln()
    println("Enter git object hash:")
    val gitObjectsHash = readln()
    val gitObjectFile = GitObjectInflater(gitDirectoryPath, gitObjectsHash)
    gitObjectFile.inflateBlob()
}

class GitObjectInflater(gitDirectory: String, objectHash: String) {
    private var fileName: String

    init {
        this.fileName = "$gitDirectory/$objectFolderName/${objectHash.substring(0, 2)}/${objectHash.substring(2, )}"
    }

    companion object Property {
        const val objectFolderName = "objects"
        val propertyList = listOf("commit", "tree", "parent", "author", "committer" )
    }

    private fun getDateTime(epochTime: String, offset: String): String {
        val hrs = offset.substring(0, 3)
        val min = offset.substring(3,)
        val instant = Instant.ofEpochSecond(epochTime.toLong())
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.ofHoursMinutes(hrs.toInt(), min.toInt()))

        return "${localDateTime.toString().replace("T", " ")} $hrs:$min"
    }

    fun inflateBlob() {
        val file = File(this.fileName)
        val fis = FileInputStream(file)
        val iis = InflaterInputStream(fis)

        val gitString = iis.readAllBytes().toString(Charsets.UTF_8).replace('\u0000', '\n').split("\n").filter { it.isNotBlank() }
        val gitHeader = gitString.first().trim().split(" ")


        when (gitHeader.first()) {
            "blob" -> {
                println("*BLOB*")
                for (str in gitString - gitString. first()) {
                    println(str)
                }
            }
            else -> {
                val gitKeyValue = gitString.map { it.trim() }.groupBy { it.split(" ").first()}
                gitKeyValue.filter { it.key in propertyList }.forEach{
                    printInfo(it.key, it.value)
                }

                println("commit message:")
                gitKeyValue.filter { it.key !in propertyList }.forEach{
                    println("${it.value.joinToString (separator = " ")}")
                }
            }
        }

        iis.close()
        fis.close()
    }

    private fun printInfo(key: String, value: List<String>) {
        when(key) {
            "commit" -> println("*COMMIT*")
            "tree" -> println("$key: ${value.last().split(" ").last()}")
            "parent" -> {
                var parents = mutableListOf<String>()
                value.forEach{
                    parents.add(it.split(" ", limit = 2).last())
                }
                println("parents: ${parents.joinToString(separator = " | ")}")
            }
            "author" -> {
                val(_, authName, authEmail, authTimeStamp, authOffset) = value.first().split(" ")
                println("$key: $authName ${authEmail.replace("<", "").replace(">", "")} original timestamp: ${getDateTime(authTimeStamp, authOffset)}")
            }
            "committer" -> {
                val(_, commName, commEmail, commTimeStamp, commOffset) = value.first().split(" ")
                println("$key: $commName ${commEmail.replace("<", "").replace(">", "")} commit timestamp: ${getDateTime(commTimeStamp, commOffset)}")
            }
        }
    }

}