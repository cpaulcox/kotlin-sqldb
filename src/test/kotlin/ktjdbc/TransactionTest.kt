package ktjdbc

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import ktjdbc.schema.Person.PERSON_TABLE
import ktjdbc.schema.Person.findPersonById
import ktjdbc.schema.Person.insertPerson
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test


class TransactionTest {

    companion object {

        @BeforeAll  // Junit complains if not a static method on a test class but won't run it unless marked as JvmStatic
        @JvmStatic
        fun initialiseConnectionPool() {

            // Code to inject and configure a datasource - db specific and able to be mocked
            val config = HikariConfig().apply {
                poolName = "SQLitePool"
                driverClassName = "org.sqlite.JDBC"
                jdbcUrl = "jdbc:sqlite::memory:"
                connectionTestQuery = "SELECT 1"
                maxLifetime = 60000 // 60 Sec
                idleTimeout = 45000 // 45 Sec
                maximumPoolSize = 50 // 50 Connections (including idle connections)
                initializationFailTimeout = -1 // get connection in background avoids failfast on startup but may fail later
            }
            DataSource.dataSource = HikariDataSource(config)

        }

        @AfterAll
        @JvmStatic
        fun shutdownConnectionPool() {

            val ds = DataSource.dataSource as HikariDataSource

            ds.close()
        }
    }

    @Test
    fun txTest() {
        transaction {
            //use {// try - with resources - the transaction code closes the connection but fails as try-with resources also does it
            // probably should nest transaction {} within use {}.  Does close() without commit() rollback?
                println("Connected")

                val s = createStatement()
                val rs = s.execute(PERSON_TABLE)

                val insert = createStatement()
                val rsInsert = s.executeUpdate(insertPerson(1, "Bob", "Smith", "bob@email.com", "+44 123456789"))

                println(rsInsert)


                val s1 = createStatement()
                val rs1 = s1.executeQuery(findPersonById(1))
                rs1.next()

                println("${rs1.getString("forename")} ")
            //}
        }
    }
}
