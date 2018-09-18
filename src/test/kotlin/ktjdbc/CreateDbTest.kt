package ktjdbc

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.junit.jupiter.api.Test
import org.sqlite.SQLiteDataSource
import java.sql.Connection


class CreateDbTest {

    @Test
    fun createInMemoryDatasourceDB() {


        // Code to inject and configure a datasource - db specific and able to be mocked
        val sqliteDs = SQLiteDataSource()
        sqliteDs.url =  "jdbc:sqlite::memory:"
        DataSource.dataSource = sqliteDs


        // Application code - db driver independent
        val conn : Connection by DataSource

        conn.use {// try - with resources
            println("Connected")

            val sql = "SELECT COUNT(*) AS n FROM sqlite_master"


            val s = conn.createStatement()
            val rs = s.executeQuery(sql)
            rs.next()
            println("The sqlite_master table contains ${rs.getInt(1)} row(s)")
        }
    }

    @Test
    fun pooledConnectionTest() {
        // Code to inject and configure a datasource - db specific and able to be mocked
        val config = HikariConfig().apply {
            poolName = "SQLitePool"
            driverClassName = "org.sqlite.JDBC"
            jdbcUrl = "jdbc:sqlite::memory:"
            connectionTestQuery = "SELECT 1"
            maxLifetime = 60000 // 60 Sec
            idleTimeout = 45000 // 45 Sec
            maximumPoolSize = 50 // 50 Connections (including idle connections)
        }
        DataSource.dataSource = HikariDataSource(config)

        // Application code - db driver independent
        val conn1 : Connection by DataSource
        val conn2 : Connection by DataSource

        conn1.use {// try - with resources
            println("Connected")

            val sql = "SELECT COUNT(*) AS n FROM sqlite_master"


            val s = conn1.createStatement()
            val rs = s.executeQuery(sql)
            rs.next()
            println("The sqlite_master table contains ${rs.getInt(1)} row(s)")
        }
    }
}