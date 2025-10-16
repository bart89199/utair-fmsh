package ru.fmsh.task

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

enum class Type(val value: String) {
    PRODUCTION("Изготовление"), REPAIR("Ремонт")
}

enum class Priority(val value: String) {
    LOW("Низкий"), MEDIUM("Нормальный"), HIGH("Высокий")
}

enum class Status(val value: String) {
    CLOSED("Закрыта"), IN_PROGRESS("В работе"), IN_PRECESSING("В обработке"), CANCELED("Отмена")
}
object TaskTable : IntIdTable("tasks", "ext_number") {
    val journalNumber = varchar("number_journal", 200).nullable()
    val type = varchar("type", 20)
    val priority = varchar("priority", 20)
    val nameType = varchar("name_type", 50).nullable()
    val locationRepair = varchar("lokation_repair", 20).nullable()
    val count = varchar("count", 20)
    val drawingNumber = varchar("drawing_number", 50).nullable()
    val pageCount = varchar("page_count", 20).nullable()
    val fullName = varchar("full_name", 40).nullable()
    val division = varchar("division", 20).nullable()
    val tgId = varchar("tg_id", 30).nullable()
    val userCode = varchar("code_users", 30).nullable()
    val email = varchar("email", 30).nullable()
    val phone = varchar("phone", 30).nullable()
    val status = varchar("status", 30)
    val comment = varchar("comment", 200).nullable()
    val applicationDate = datetime("application_date")
    val commentReady = varchar("comment_ready", 100).nullable()
    val dateReady = varchar("date_ready", 20).nullable()
    val commentsClosing = varchar("comments_closing", 100).nullable()
    val closingDate = datetime("closing_date").nullable()
    val photo1 = varchar("photo_1", 20).nullable()
    val photo2 = varchar("photo_2", 20).nullable()
    val photo3 = varchar("photo_3", 20).nullable()
    val commentShift = varchar("comment_shift", 100).nullable()
    val planCompleteDate = date("plan_complet_date")
    val categoryChange = varchar("category_change", 20).nullable()
    val amosOrderNumber = varchar("amos_order_number", 50).nullable()
    val departmentOgm = varchar("department_ogm", 50).nullable()
    val innerId = varchar("id", 30)
}

@Serializable
data class Task(
    @SerialName("ext_number") val extNumber: Int,
    @SerialName("number_journal") val journalNumber: String?,
    @SerialName("type") val type: String,
    @SerialName("priority") val priority: String,
    @SerialName("name_type") val nameType: String?,
    @SerialName("location_repair") val locationRepair: String?,
    @SerialName("count") val count: String,
    @SerialName("drawing_number") val drawingNumber: String?,
    @SerialName("page_count") val pageCount: String?,
    @SerialName("full_name") val fullName: String?,
    @SerialName("division") val division: String?,
    @SerialName("tg_id") val tgId: String?,
    @SerialName("code_users") val userCode: String?,
    @SerialName("email") val email: String?,
    @SerialName("phone") val phone: String?,
    @SerialName("status") val status: String,
    @SerialName("comment") val comment: String?,
    @SerialName("application_date") val applicationDate: LocalDateTime,
    @SerialName("comment_ready") val commentReady: String?,
    @SerialName("date_ready") val dateReady: String?,
    @SerialName("comments_closing") val commentsClosing: String?,
    @SerialName("closing_date") val closingDate: LocalDateTime?,
    @SerialName("photo_1") val photo1: String?,
    @SerialName("photo_2") val photo2: String?,
    @SerialName("photo_3") val photo3: String?,
    @SerialName("comment_shift") val commentShift: String?,
    @SerialName("plan_complete_date") val planCompleteDate: LocalDate,
    @SerialName("category_change") val categoryChange: String?,
    @SerialName("amos_order_number") val amosOrderNumber: String?,
    @SerialName("department_ogm") val departmentOgm: String?,
    @SerialName("id") val id: String
)

fun Query.toModel(): List<Task> = map {
    Task(
        it[TaskTable.id].value,
        it[TaskTable.journalNumber],
        it[TaskTable.type],
        it[TaskTable.priority],
        it[TaskTable.nameType],
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
        it[TaskTable.innerId]
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
