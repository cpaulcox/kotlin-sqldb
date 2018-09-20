package ktjdbc

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import java.sql.*

/**
 * Created by paulcox on 24/04/2017.
 */

private val logger : Logger = LogManager.getLogger("DbUtils")

// Marker for all SQL related timing logging - intended to be inherited by specific markers per SQL
// statement or transaction
val SQL_TIMER_MARKER : Marker = MarkerManager.getMarker("SQL_TIMER")//.setParents(DEFAULT_TIMER_MARKER)

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

// TODO optional parameter for a named pool
fun transaction(init: Connection.() -> Any): Any {

    val conn : Connection by DataSource()  // the receiver
    conn.autoCommit = false  // TODO exception handling - set this elsewhere?

    try {  // TODO try with resources .use{ }
        return conn.init()        // pass the receiver object to the lambda - invoke the lambda extension method
    } catch (e : Exception) {  // TODO should this just be SQL exceptions?  What about comms failures?
        conn.rollback()
        throw e  // probably a rollback exception + cause would be better TODO
    }
    finally {
        conn.commit()  // TODO tests on close and exception handling
        conn.close()   // not needed with try-with-resources
    }
}

