package ru.fmsh.task

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.StringColumnType
import org.jetbrains.exposed.sql.VarCharColumnType
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.castTo
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import ru.fmsh.daatabase.suspendTransaction


object TaskService {
    suspend fun getAll(): List<Task> = suspendTransaction {
        TaskTable.select(TaskTable.columns).toModel()
    }

    suspend fun getByExtNumber(extNumber: Int) = suspendTransaction {
        TaskTable.select(TaskTable.columns).where { TaskTable.id eq extNumber }.toModel().firstOrNull()
    }

    private fun columEquals(column: Column<String>, value: String?): Op<Boolean> =
        if (value.isNullOrBlank()) Op.TRUE else column eq value

    private fun columnsContains(columns: List<Column<Any>>, value: String?): Op<Boolean> =
        if (value.isNullOrBlank()) Op.TRUE else columns.fold<Column<Any>, Op<Boolean>>(Op.TRUE) { base, column ->
            base or (column.castTo(VarCharColumnType()) like value)
        }

    suspend fun get(
        priority: String?,
        status: String?,
        planCompleteDataStart: Long?,
        planCompleteDataEnd: Long?,
        stringFind: String?
    ) = suspendTransaction {
        TaskTable.select(TaskTable.columns)
            .where { columEquals(TaskTable.priority, priority) and columEquals(TaskTable.status, status) and columnsContains(listOf(), stringFind) }
            .toModel()
    }
}