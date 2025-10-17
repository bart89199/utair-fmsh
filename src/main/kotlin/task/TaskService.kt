package ru.fmsh.task

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.case
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.VarCharColumnType
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.castTo
import org.jetbrains.exposed.sql.intParam
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.stringParam
import ru.fmsh.database.suspendTransaction


object TaskService {
    suspend fun getAll(): List<Task> = suspendTransaction {
        TaskTable.select(TaskTable.columns).toModel()
    }

    suspend fun getByExtNumber(extNumber: Int) = suspendTransaction {
        TaskTable.select(TaskTable.columns).where { TaskTable.id eq extNumber }.toModel().firstOrNull()
    }

    private fun columnsContains(columns: List<Column<*>>, value: String?): Op<Boolean> =
        if (value.isNullOrBlank() || columns.isEmpty()) Op.TRUE else columns.fold<Column<*>, Op<Boolean>>(Op.FALSE) { base, column ->
            base or (column.castTo(VarCharColumnType()) like "%$value%")
        }

    private fun now() = Clock.System.todayIn(TimeZone.currentSystemDefault())

    enum class PlanDateType {
        TODAY, OVERDUE, FUTURE, ALL
    }

    fun <T, R> columnParser(column: Column<T>, default: Expression<R>, vararg vals: Pair<T, Expression<R>>) =
        case().also {
            for ((v, r) in vals) it.When(
                column eq v,
                r
            )
        }.value ?: default

    suspend fun get(
        limit: Int = -1,
        priority: List<String>? = null,
        status: List<String>? = null,
        type: List<String>? = null,
        planCompleteDateStart: LocalDate? = null,
        planCompleteDateEnd: LocalDate? = null,
        stringFind: String? = null,
        planDateType: PlanDateType = PlanDateType.ALL,
        sortAsc: Boolean = false
    ) = suspendTransaction {
        addLogger(StdOutSqlLogger)
        TaskTable.select(TaskTable.columns)
            .where {
                return@where (if (priority.isNullOrEmpty()) Op.TRUE else TaskTable.priority inList priority) and
                        (if (status.isNullOrEmpty()) Op.TRUE else TaskTable.status inList status) and
                        (if (type.isNullOrEmpty()) Op.TRUE else TaskTable.type inList type) and


                (if (planCompleteDateStart == null) Op.TRUE else TaskTable.planCompleteDate greaterEq planCompleteDateStart) and
                        (if (planCompleteDateEnd == null) Op.TRUE else TaskTable.planCompleteDate lessEq planCompleteDateEnd) and

                        columnsContains(
                            listOf(
                                TaskTable.id,
                                TaskTable.journalNumber,
                                TaskTable.type,
                                TaskTable.priority,
                                TaskTable.nameType,
                                TaskTable.count,
                                TaskTable.status,
                                TaskTable.comment,
                                TaskTable.applicationDate,
                                TaskTable.planCompleteDate
                            ), stringFind
                        ) and
                        (columnParser(
                            TaskTable.priority,
                            intParam(0),
                            "Высокий" to intParam(3),
                            "Нормальный" to intParam(2),
                            "Низкий" to intParam(1)
                        ) eq intParam(3)) and

                        when (planDateType) {
                            PlanDateType.FUTURE -> TaskTable.planCompleteDate greater now()
                            PlanDateType.OVERDUE -> TaskTable.planCompleteDate less now()
                            PlanDateType.TODAY -> TaskTable.planCompleteDate eq now()
                            PlanDateType.ALL -> Op.TRUE
                        }

            }.apply { if (limit > 0) limit(limit) }.orderBy(
//                TaskTable.planCompleteDate to if (sortAsc) SortOrder.ASC_NULLS_LAST else SortOrder.DESC_NULLS_LAST,
//                columnParser(
//                    TaskTable.priority,
//                    intParam(0),
//                    "Высокий" to intParam(3),
//                    "Нормальный" to intParam(2),
//                    "Низкий" to intParam(1)
//                ) to SortOrder.DESC_NULLS_LAST,
                TaskTable.planCompleteDate to if (sortAsc) SortOrder.ASC_NULLS_LAST else SortOrder.DESC_NULLS_LAST
            )
            .toModel()
    }
}