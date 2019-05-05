package cat.gencat.access.os


import cat.gencat.access.functions.PATH_TO_DESPESES
import cat.gencat.access.functions.PATH_TO_FORMS
import cat.gencat.access.functions.Utils.Companion.currentCourseYear
import javafx.scene.control.Alert
import java.io.*
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


class GesticusOs {

    companion object {

//        /* source: 0009990600-2018-2019-... */
//        private fun findDestinationReport(source: String): String {
////            val codiEstada = source.substring(3, 6)
////            val destination = "${PATH_TO_COPY}${codiEstada}"
//            return "${PATH_TO_COPY}"
//        }

        /* source: 099999999A-999-A.pdf */
        /* source: A9999999A-999-A.pdf */
//        private fun findDestinationForm(source: String): String {
////            val start = source.indexOf("-") + 1
////            val codiEstada = source.substring(start, start + 3)
////            val destination = "${PATH_TO_COPY}${source}"
//            return "${PATH_TO_COPY}"
//        }

        /* This method copies source to destination */
        @Throws(IOException::class)
        private fun copy(source: String, destination: String): Boolean {
            if (!File(source).exists()) {
                Alert(Alert.AlertType.ERROR, "El fitxer $source no existeix").showAndWait()
                return false
            }
            Files.copy(Paths.get(source), Paths.get(destination), StandardCopyOption.REPLACE_EXISTING)
            return true
        }

        /* This method moves source to destination */
        @Throws(IOException::class)
        private fun move(source: String, destination: String): Boolean {
            if (!File(source).exists()) {
                Alert(Alert.AlertType.ERROR, "El fitxer $source no existeix").showAndWait()
                return false
            }
            Files.move(Paths.get(source), Paths.get(destination), StandardCopyOption.ATOMIC_MOVE)
            return true
        }

        /* This method copies source to destination */
//        @Throws(IOException::class)
//        fun copyReport(source: String): Boolean {
//            val destination = findDestinationReport(source)
//            return copy("${PATH_TO_REPORTS}${source}", destination)
//        }

        /* This method moves source to destination */
//        @Throws(IOException::class)
//        fun moveReport(source: String): Boolean {
//            val destination = findDestinationReport(source)
//            return move("${PATH_TO_REPORTS}${source}", destination)
//        }

        /* This method copies source to destination. source is a full path */
//        @Throws(IOException::class)
//        private fun copyForm(source: String): Boolean {
//            val destination = findDestinationForm(source)
//            return copy(source, destination)
//        }

        /* This method moves source to destination. source is a full path */
//        @Throws(IOException::class)
//        private fun moveForm(source: String): Boolean {
//            val destination = findDestinationForm(source)
//            return move(source, destination)
//        }

        /* This method zips de files of a directory into a file. TODO("Review") */
        fun zipDirectory(directory: String, out: String) {

            if (Files.isDirectory(Paths.get(directory))) {
                val files: Array<out File> = File(directory).listFiles()
//                val files: Stream<Path> = Files.list(Paths.get(directory))
                ZipOutputStream(BufferedOutputStream(FileOutputStream(PATH_TO_DESPESES + out))).use { zos ->
                    files.forEach { path ->
                        FileInputStream(path).use { fis ->
                            BufferedInputStream(fis).use { bis ->
                                val entry = ZipEntry(path.name)
                                zos.putNextEntry(entry)
                                bis.copyTo(zos, 1024)
                            }
                        }
                    }
                }
            }
        }


        /* nif is 099999999 or A9999999A renames 099999999.pdf or A9999999A.pdf to 099999999-999-A.pdf or A9999999A-999-A.pdf */
        @Throws(IOException::class)
        fun renameForm(nif: String, numEstada: String, tipusEstada: String): Boolean {
            val sourceFullname = "${PATH_TO_FORMS}\\${currentCourseYear()}\\${nif}.pdf"
            return if (Files.exists(Paths.get(sourceFullname))) {
                val num = numEstada.substring(3, 6)
                val destFullname = "${PATH_TO_FORMS}\\${currentCourseYear()}\\${nif}-${num}-${tipusEstada}.pdf"
                File(sourceFullname).renameTo(File(destFullname))
                true
            } else {
                false
            }
            //copyForm(destFullname)
        }
    }
}