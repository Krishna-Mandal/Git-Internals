package gitinternals

import java.io.File
import java.io.FileInputStream
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.zip.InflaterInputStream

fun main() {
    val gitInflate = GitObjectInflater()
    gitInflate.commandParser()
}

class GitObjectInflater {
    lateinit var gitDirectory: String
    lateinit var objectHash: String
    lateinit var fileName: String

    companion object Property {
        const val objectFolderName = "objects"
        val propertyList = listOf("commit", "tree", "parent", "author", "committer" )
    }

    fun commandParser() {
        println("Enter .git directory location:")
        this.gitDirectory = readln()
        println("Enter command:")
        when(readln()) {
            "cat-file" -> handleGitObjects()
            "list-branches" -> handleBranches()
        }
    }

    private fun handleBranches() {
        val branchPath = "/refs/heads"
        val headPath = "/HEAD"
        val head = getHead("${this.gitDirectory}$headPath")
        val branches = mutableListOf<String>()
        File("${this.gitDirectory}$branchPath").walk().filter { it.isFile }.forEach { branches.add(it.name) }
        branches.sort()

        for (branch in branches) {
            if(branch == head) {
                println("* $branch")
            } else {
                println("  $branch")
            }
        }
    }

    private fun getHead(path: String): String {
        return File(path).readLines().first().split("/").last()
    }

     private fun handleGitObjects() {
        println("Enter git object hash:")
        this.objectHash = readln()
        this.fileName = "$gitDirectory/$objectFolderName/${objectHash.substring(0, 2)}/${objectHash.substring(2, )}"
        this.handleCatFile()
    }

    private fun getDateTime(epochTime: String, offset: String): String {
        val hrs = offset.substring(0, 3)
        val min = offset.substring(3,)
        val instant = Instant.ofEpochSecond(epochTime.toLong())
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.ofHoursMinutes(hrs.toInt(), min.toInt()))

        return "${localDateTime.toString().replace("T", " ")} $hrs:$min"
    }

    private fun inflateBlob(fileName: String): ByteArray {
        val file = File(fileName)
        val fis = FileInputStream(file)
        val iis = InflaterInputStream(fis)
        return iis.readAllBytes()!!
    }
    private fun handleCatFile() {

        val gitByte = inflateBlob(this.fileName)
        val gitString = gitByte.toString(Charsets.UTF_8).replace('\u0000', '\n').split("\n").filter { it.isNotBlank() }
        val gitHeader = gitString.first().trim().split(" ")
        gitString - gitString.first()


        when (gitHeader.first()) {
            "blob" -> {
                println("*BLOB*")
                for (str in gitString - gitString. first()) {
                    println(str)
                }
            }
            "tree" -> {
                println("*TREE*")
                printTreeData(gitByte.map { Char(it.toUShort()) }.joinToString(""))
            }
            else -> {
                val gitKeyValue = gitString.map { it.trim() }.groupBy { it.split(" ").first()}
                gitKeyValue.filter { it.key in propertyList }.forEach{
                    printInfo(it.key, it.value)
                }

                println("commit message:")
                gitKeyValue.filter { it.key !in propertyList }.forEach{
                    println(it.value.joinToString (separator = " "))
                }
            }
        }

    }
    private fun printTreeData(gitString: String) {
        val linesOfTree = gitString.split(0.toChar())
        var (number, name) = linesOfTree[1].split(" ")
        var hash: String
        for (i in 2 until linesOfTree.size - 1) {
            hash = linesOfTree[i].substring(0, 20).map { it.code.toByte() }.joinToString("") { "%02x".format(it)}
            val tmp = linesOfTree[i].substring(20).split(" ")
            println("$number $hash $name")
            number = tmp[0]
            name = tmp[1]
        }
        hash = linesOfTree[linesOfTree.size - 1].map { it.code.toByte() }.joinToString("") { "%02x".format(it)}
        println("$number $hash $name")
    }

    private fun printInfo(key: String, value: List<String>) {
        when(key) {
            "commit" -> println("*COMMIT*")
            "tree" -> println("$key: ${value.last().split(" ").last()}")
            "parent" -> {
                val parents = mutableListOf<String>()
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