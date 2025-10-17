package ru.fmsh.task

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.VarCharColumnType
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.castTo
import org.jetbrains.exposed.sql.or
import ru.fmsh.daatabase.suspendTransaction


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

    enum class OrderType {
        PLAN_DATE,
        JOURNAL_NUMBER
    }

    suspend fun get(
        limit: Int = -1,
        priority: List<String>? = null,
        status: List<String>? = null,
        planCompleteDateStart: LocalDate? = null,
        planCompleteDateEnd: LocalDate? = null,
        stringFind: String? = null,
        planDateType: PlanDateType = PlanDateType.ALL,
        orderType: OrderType = OrderType.PLAN_DATE,
        sortAsc: Boolean = false
        ) = suspendTransaction {
        TaskTable.select(TaskTable.columns)
            .where {
                if (priority.isNullOrEmpty()) Op.TRUE else TaskTable.priority inList priority and
                        if (status.isNullOrEmpty()) Op.TRUE else TaskTable.status inList status and
                        (if (planCompleteDateStart == null) Op.TRUE else TaskTable.planCompleteDate greaterEq planCompleteDateStart) and
                        (if (planCompleteDateEnd == null) Op.TRUE else TaskTable.planCompleteDate lessEq planCompleteDateEnd) and
                        columnsContains(listOf(TaskTable.id, TaskTable.journalNumber, TaskTable.type, TaskTable.priority,
                            TaskTable.nameType, TaskTable.count, TaskTable.status, TaskTable.comment, TaskTable.applicationDate,
                            TaskTable.planCompleteDate), stringFind) and
                        when (planDateType) {
                            PlanDateType.FUTURE -> TaskTable.planCompleteDate greater now()
                            PlanDateType.OVERDUE -> TaskTable.planCompleteDate less now()
                            PlanDateType.TODAY -> TaskTable.planCompleteDate eq now()
                            PlanDateType.ALL -> Op.TRUE
                        }
            }.apply { if (limit > 0) limit(limit) }.orderBy(
                when (orderType) {
                    OrderType.PLAN_DATE -> TaskTable.planCompleteDate
                    OrderType.JOURNAL_NUMBER -> TaskTable.journalNumber
                }, if (sortAsc) SortOrder.ASC_NULLS_LAST else SortOrder.DESC_NULLS_LAST
            )
            .toModel()
    }
}