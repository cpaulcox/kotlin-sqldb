package ktjdbc

import org.junit.jupiter.api.Test
import java.sql.SQLException
import java.sql.DriverManager
import java.sql.ResultSet
import org.sqlite.SQLiteDataSource




class CreateDbTest {

    @Test
    fun createDatabase() {

        createNewDatabase("testDB")
    }

    fun createNewDatabase(fileName: String) {

        val url = "jdbc:sqlite:./build/$fileName"  // assumes Gradle build folder for cleaning

        try {
            DriverManager.getConnection(url).use { conn ->
                if (conn != null) {
                    val meta = conn.metaData
                    println("The driver name is ${meta.driverName}")
                    println("A new database has been created. ${meta.url}")
                }

            }
        } catch (e: SQLException) {
            println(e.message)
        }
    }

    @Test
    fun createInMemoryDatasourceDB() {
        val ds = SQLiteDataSource()
        ds.url = "jdbc:sqlite::memory:"

        val conn = ds.connection  // what happens if this throws an exception?

        conn.use {// try - with resources
            println("Connected")

            val sql = "SELECT COUNT(*) AS n FROM sqlite_master"


            val s = conn.createStatement()
            val rs = s.executeQuery(sql)
            rs.next()
            println("The sqlite_master table contains ${rs.getInt(1)} row(s)")
        }
    }
}