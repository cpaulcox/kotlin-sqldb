package ktjdbc

import java.sql.Connection
import kotlin.reflect.KProperty

class DataSource {

   companion object {

       lateinit var dataSource: javax.sql.DataSource

       operator fun getValue(thisRef: Any?, property: KProperty<*>): Connection {

           return dataSource.connection  // what happens if this throws an exception?
       }
   }
}
