package ru.fmsh.task

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.case
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
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

    suspend fun get(
        limit: Int = -1,
        priority: List<String>? = null,
        status: List<String>? = null,
        type: List<String>? = null,
        planCompleteDateStart: LocalDate? = null,
        planCompleteDateEnd: LocalDate? = null,
        planDateType: PlanDateType = PlanDateType.ALL,
        sortAsc: Boolean = false
    ) = suspendTransaction {
//        addLogger(StdOutSqlLogger)
        TaskTable.select(TaskTable.columns)
            .let { if (priority.isNullOrEmpty()) it else it.andWhere { TaskTable.priority inList priority } }
            .let { if (status.isNullOrEmpty()) it else it.andWhere { TaskTable.status inList status } }
            .let { if (type.isNullOrEmpty()) it else it.andWhere { TaskTable.type inList type } }
            .let { if (planCompleteDateStart == null) it else it.andWhere { TaskTable.planCompleteDate greaterEq planCompleteDateStart } }
            .let { if (planCompleteDateEnd == null) it else it.andWhere { TaskTable.planCompleteDate lessEq planCompleteDateEnd } }
            .andWhere {
                when (planDateType) {
                    PlanDateType.FUTURE -> TaskTable.planCompleteDate greater now()
                    PlanDateType.OVERDUE -> TaskTable.planCompleteDate less now()
                    PlanDateType.TODAY -> TaskTable.planCompleteDate eq now()
                    PlanDateType.ALL -> Op.TRUE
                }
            }.apply { if (limit > 0) limit(limit) }.orderBy(
                case()
                    .When(TaskTable.priority eq "Высокий", intParam(3))
                    .When(TaskTable.priority eq "Нормальный", intParam(2))
                    .When(TaskTable.priority eq "Низкий", intParam(1))
                    .Else(intLiteral(0)) to SortOrder.DESC_NULLS_LAST,
                TaskTable.planCompleteDate to if (sortAsc) SortOrder.ASC_NULLS_LAST else SortOrder.DESC_NULLS_LAST
            ).toModel()
    }

}