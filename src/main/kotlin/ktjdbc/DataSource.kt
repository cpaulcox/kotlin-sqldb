package ktjdbc

import java.sql.Connection
import kotlin.reflect.KProperty

/**
 * - Add support for multiple connection pools e.g. DataSource("pool name")
 */
class DataSource {

    lateinit var connection: Connection

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Connection {

        if (! ::connection.isInitialized) {
            println("### Getting connection")
            connection = dataSource.connection  // what happens if this throws an exception?
        }

        return connection
    }

    companion object {

        lateinit var dataSource: javax.sql.DataSource

        operator fun getValue(thisRef: Any?, property: KProperty<*>): Connection {

            println("### Getting connection")

            return dataSource.connection  // what happens if this throws an exception?
        }
    }
}
