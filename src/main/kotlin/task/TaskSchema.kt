package ru.fmsh.task

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

//enum class Type(val value: String) {
//    PRODUCTION("Изготовление"), REPAIR("Ремонт")
//}
//
//enum class Priority(val value: String) {
//    LOW("Низкий"), MEDIUM("Нормальный"), HIGH("Высокий")
//}
//
//enum class Status(val value: String) {
//    CLOSED("Закрыта"), IN_PROGRESS("В работе"), IN_PRECESSING("В обработке"), CANCELED("Отмена")
//}

object TaskTable : IntIdTable("tasks") {
    val journalNumber = varchar("number_journal", 1000)
    val type = varchar("type", 100)
    val priority = varchar("priority", 300)
    val message = varchar("message", 10000)
    val locationRepair = varchar("location_repair", 5000).nullable()
    val count = varchar("count", 1000).nullable()
    val drawingNumber = varchar("drawing_number", 1000).nullable()
    val pageCount = varchar("page_count", 1000).nullable()
    val fullName = varchar("full_name", 1000)
    val division = varchar("division", 1000).nullable()
    val tgId = varchar("tg_id", 500).nullable()
    val userCode = varchar("code_users", 500).nullable()
    val email = varchar("email", 500).nullable()
    val phone = varchar("phone", 500).nullable()
    val status = varchar("status", 100)
    val comment = varchar("comment", 10000).nullable()
    val applicationDate = datetime("application_date")
    val commentReady = varchar("comment_ready", 10000).nullable()
    val dateReady = datetime("date_ready").nullable()
    val commentsClosing = varchar("comments_closing", 10000).nullable()
    val closingDate = datetime("closing_date").nullable()
    val photo1 = varchar("photo_1", 2000).nullable()
    val photo2 = varchar("photo_2", 2000).nullable()
    val photo3 = varchar("photo_3", 2000).nullable()
    val commentShift = varchar("comment_shift", 2000).nullable()
    val planCompleteDate = date("plan_complete_date").nullable()
    val categoryChange = varchar("category_change", 2000).nullable()
    val amosOrderNumber = varchar("amos_order_number", 2000).nullable()
    val departmentOgm = varchar("department_ogm", 5000).nullable()
}

@Serializable
data class Task(
    @SerialName("id") val id: Int,
    @SerialName("number_journal") val journalNumber: String,
    @SerialName("type") val type: String,
    @SerialName("priority") val priority: String,
    @SerialName("message") val message: String,
    @SerialName("location_repair") val locationRepair: String?,
    @SerialName("count") val count: String?,
    @SerialName("drawing_number") val drawingNumber: String?,
    @SerialName("page_count") val pageCount: String?,
    @SerialName("full_name") val fullName: String,
    @SerialName("division") val division: String?,
    @SerialName("tg_id") val tgId: String?,
    @SerialName("code_users") val userCode: String?,
    @SerialName("email") val email: String?,
    @SerialName("phone") val phone: String?,
    @SerialName("status") val status: String,
    @SerialName("comment") val comment: String?,
    @SerialName("application_date") val applicationDate: LocalDateTime,
    @SerialName("comment_ready") val commentReady: String?,
    @SerialName("date_ready") val dateReady: LocalDateTime?,
    @SerialName("comments_closing") val commentsClosing: String?,
    @SerialName("closing_date") val closingDate: LocalDateTime?,
    @SerialName("photo_1") val photo1: String?,
    @SerialName("photo_2") val photo2: String?,
    @SerialName("photo_3") val photo3: String?,
    @SerialName("comment_shift") val commentShift: String?,
    @SerialName("plan_complete_date") val planCompleteDate: LocalDate?,
    @SerialName("category_change") val categoryChange: String?,
    @SerialName("amos_order_number") val amosOrderNumber: String?,
    @SerialName("department_ogm") val departmentOgm: String?,
)

@Serializable
data class DisplayTask(
    @SerialName("id") val id: Int,
    @SerialName("number_journal") val journalNumber: String?,
    @SerialName("type") val type: String,
    @SerialName("priority") val priority: String,
    @SerialName("message") val message: String?,
    @SerialName("location_repair") val locationRepair: String?,
    @SerialName("full_name") val fullName: String?,
    @SerialName("status") val status: String,
    @SerialName("comment") val comment: String?,
    @SerialName("plan_complete_date") val planCompleteDate: LocalDate?,
)

fun Task.toDisplayTask() = DisplayTask(
    id,
    journalNumber,
    type,
    priority,
    message,
    locationRepair,
    fullName,
    status,
    comment,
    planCompleteDate
)

fun Iterable<Task>.toDisplayTasks() = map { it.toDisplayTask() }

fun Query.toModel(): List<Task> = map {
    Task(
        it[TaskTable.id].value,
        it[TaskTable.journalNumber],
        it[TaskTable.type],
        it[TaskTable.priority],
        it[TaskTable.message],
        it[TaskTable.locationRepair],
        it[TaskTable.count],
        it[TaskTable.drawingNumber],
        it[TaskTable.pageCount],
        it[TaskTable.fullName],
        it[TaskTable.division],
        it[TaskTable.tgId],
        it[TaskTable.userCode],
        it[TaskTable.email],
        it[TaskTable.phone],
        it[TaskTable.status],
        it[TaskTable.comment],
        it[TaskTable.applicationDate],
        it[TaskTable.commentReady],
        it[TaskTable.dateReady],
        it[TaskTable.commentsClosing],
        it[TaskTable.closingDate],
        it[TaskTable.photo1],
        it[TaskTable.photo2],
        it[TaskTable.photo3],
        it[TaskTable.commentShift],
        it[TaskTable.planCompleteDate],
        it[TaskTable.categoryChange],
        it[TaskTable.amosOrderNumber],
        it[TaskTable.departmentOgm],
    )
}

//val taskTableColumns = listOf(
//    TaskTable.id,
//    TaskTable.journalNumber,
//    TaskTable.type,
//    TaskTable.priority,
//    TaskTable.nameType,
//    TaskTable.locationRepair,
//    TaskTable.count,
//    TaskTable.drawingNumber,
//    TaskTable.pageCount,
//    TaskTable.fullName,
//    TaskTable.division,
//    TaskTable.tgId,
//    TaskTable.userCode,
//    TaskTable.email,
//    TaskTable.phone,
//    TaskTable.status,
//    TaskTable.comment,
//    TaskTable.applicationDate,
//    TaskTable.commentReady,
//    TaskTable.dateReady,
//    TaskTable.commentsClosing,
//    TaskTable.closingDate,
//    TaskTable.photo1,
//    TaskTable.photo2,
//    TaskTable.photo3,
//    TaskTable.commentShift,
//    TaskTable.planCompleteDate,
//    TaskTable.categoryChange,
//    TaskTable.amosOrderNumber,
//    TaskTable.departmentOgm,
//    TaskTable.innerId
//)
