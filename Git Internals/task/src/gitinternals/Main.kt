package gitinternals

import java.io.File
import java.io.FileInputStream
import java.util.zip.InflaterInputStream

fun main() {
    println("Enter git object location:")
    val gitObjectsPath = readln()
    var gitObjectFile = GitObjectInflater(gitObjectsPath)
    gitObjectFile.inflateBlob()
}

class GitObjectInflater(var fileName: String) {

    fun inflateBlob() {
        val file = File(this.fileName)
        val fis = FileInputStream(file)
        val iis = InflaterInputStream(fis)

        while (iis.available() == 1) {
            for(char in String(iis.readAllBytes())) {
                if(char.code == 0) {
                    println()
                } else {
                    print(char)
                }
            }
        }
    }

}