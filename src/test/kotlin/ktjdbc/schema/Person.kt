package ktjdbc.schema

// + Easier table definition than JPA annotations
// - Lose ability to do dirty flags on updates
// - Lose session cache (persistence context)
// + Simple insert function
// - Database/SQL portability - for unit tests
object Person {

// Use const for statically defined values - cannot use trimIndent() as it is dynamic assignment
const val PERSON_TABLE = """
    CREATE TABLE IF NOT EXISTS PERSON (
     id integer PRIMARY KEY,
     forename text NOT NULL,
     surname text NOT NULL,
     email text NOT NULL UNIQUE,
     phone text NOT NULL UNIQUE
    );
"""

    inline fun insertPerson(id : Int, forename : String, surname : String, email : String, phone : String) = """
    INSERT INTO PERSON (id, forename, surname, email, phone)
        values ($id, '$forename', '$surname', '$email', '$phone');
    """.trimIndent()

    inline fun findPersonById(id : Int) = """
        SELECT * FROM PERSON WHERE id = $id
    """.trimIndent()
}
