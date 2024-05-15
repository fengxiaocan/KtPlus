package com.sqlite.lib

class SqliteCreateTable(val tableName: String) {
    var primaryKey: String = "id"
    private val columns = LinkedHashMap<String, String>()

    fun addTextColumn(
        column: String,
        defaultValue: String? = null,
        notNull: Boolean = false,
        unique: Boolean = false
    ) {
        columns[column] = addColumn("TEXT", defaultValue, notNull, unique).toString()
    }

    fun addIntColumn(
        column: String,
        defaultValue: Int? = null,
        notNull: Boolean = false,
        unique: Boolean = false
    ) {
        columns[column] = addColumn("INTEGER", defaultValue?.toString(), notNull, unique).toString()
    }

    fun addBoolColumn(
        column: String,
        defaultValue: Boolean = false,
        notNull: Boolean = false,
        unique: Boolean = false
    ) {
        columns[column] =
            addColumn("INTEGER", if (defaultValue) "1" else "0", notNull, unique).toString()
    }

    private fun addColumn(
        type: String,
        defaultValue: String? = null,
        notNull: Boolean = false,
        unique: Boolean = false
    ): StringBuffer {
        val buffer = StringBuffer(type)
        if (notNull) {
            buffer.append(" NOT NULL")
        }
        if (defaultValue != null) {
            buffer.append(" DEFAULT ")
            buffer.append(defaultValue)
        }
        if (unique) {
            buffer.append(" UNIQUE")
        }
        return buffer
    }
}