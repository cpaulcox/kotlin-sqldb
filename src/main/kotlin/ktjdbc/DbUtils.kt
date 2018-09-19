package ktjdbc

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import util.logging.DEFAULT_TIMER_MARKER
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import java.sql.*
import javax.sql.DataSource

import util.config.config.db
/**
 * Created by paulcox on 24/04/2017.
 */

private val logger : Logger = LogManager.getLogger("DbUtils")

// Marker for all SQL related timing logging - intended to be inherited by specific markers per SQL
// statement or transaction
val SQL_TIMER_MARKER : Marker = MarkerManager.getMarker("SQL_TIMER").setParents(DEFAULT_TIMER_MARKER)

// Marker for any JDBC API related logging
val JDBC_MARKER : Marker = MarkerManager.getMarker("JDBC")


fun <T> Statement.mapResults(resultProc: (ResultSet) -> T ) : List<T> {
    val rs = this.resultSet

    val list : MutableList<T> = mutableListOf()

    while (rs.next()) {
        list.add(resultProc.invoke(rs))  // TODO exceptions inside lambda?
    }
    return list
}


// TODO modify this to use a Datasource and connection pool...handy level of direction that allows
// injection into the latter transaction "Function Literal With Receiver"
object ConnectionProvider {
    var url = ""
    fun registerDataProvider(url: String) {
        this.url = url
    }

    val conn: Connection
        get() = DriverManager.getConnection(url)
}

object HikariConnectionProvider {
    //var url = ""
    var datasource : DataSource? = null

    fun registerDataSource(url: String) {
        //this.url = url
        val config = HikariConfig()
        config.dataSourceClassName = "org.postgresql.ds.PGSimpleDataSource"  //TODO hack

        config.password = db.password
        config.username = db.user
        //config.
        //config.jdbcUrl = url

//        config.username = "root"
//        config.password = "password"
        config.maximumPoolSize = 10
        config.addDataSourceProperty("databaseName", db.database)
        config.addDataSourceProperty("serverName", db.host)
        config.addDataSourceProperty("portNumber", db.port)

        //config.autoCommit = false
        //config.addDataSourceProperty("cachePrepStmts", "true")

        //config.addDataSourceProperty("prepStmtCacheSize", "250")

        //config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        datasource = HikariDataSource(config)
    }

    val conn: Connection
        get() = datasource!!.connection
}



fun <T> PreparedStatement.mapResults(resultProc: (ResultSet) -> T ) : List<T> {
    val rs = this.resultSet

    val list : MutableList<T> = mutableListOf()

    while (rs.next()) {
        list.add(resultProc.invoke(rs))  // TODO exceptions inside lambda?
    }
    return list
}

// must call next() to position the cursor
fun <T> PreparedStatement.mapResult(resultProc: (ResultSet) -> T ) : T {

    if (resultSet == null) {
        logger.error(JDBC_MARKER, "null result set")
        throw NullPointerException("Null result set")
    }

    if (resultSet.next()) {
      return  resultProc.invoke(resultSet)
    }
    else throw RuntimeException("Missing PK")  // TODO wrong error???
}

fun transaction(init: Connection.() -> Any): Any {

    val conn = HikariConnectionProvider.conn  // create the receiver object
    conn.autoCommit = false

    try {
        return conn.init()        // pass the receiver object to the lambda
    } catch (e : Exception) {  // TODO should this just be SQL exceptions?  What about comms failures?
        conn.rollback()
        throw e  // probably a rollback exception + cause would be better TODO
    }
    finally {
        conn.commit()
        conn.close()
    }
}

