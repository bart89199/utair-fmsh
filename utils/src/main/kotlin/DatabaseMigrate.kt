package ru.fmsh

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format.alternativeParsing
import kotlinx.datetime.format.char
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.transactions.transaction

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


/*
TaskTable.insert { nw ->
                    try {
                        nw[TaskTable.id] = id
                        nw[TaskTable.journalNumber] = journalNumber
                        nw[TaskTable.type] = old[TaskTableOld.type]
                        nw[TaskTable.priority] =
                            old[TaskTableOld.priority].replace("\uD83D\uDFE2 ", "").replace("\uD83D\uDD34 ", "")
                        nw[TaskTable.message] = old[TaskTableOld.nameType]
                        nw[TaskTable.locationRepair] = old[TaskTableOld.locationRepair]
                        nw[TaskTable.count] = old[TaskTableOld.count]
                        nw[TaskTable.drawingNumber] = old[TaskTableOld.drawingNumber]
                        nw[TaskTable.pageCount] = old[TaskTableOld.pageCount]
                        nw[TaskTable.fullName] = old[TaskTableOld.fullName]
                        nw[TaskTable.division] = old[TaskTableOld.division]
                        nw[TaskTable.tgId] = old[TaskTableOld.tgId]
                        nw[TaskTable.userCode] = old[TaskTableOld.userCode]
                        nw[TaskTable.email] = old[TaskTableOld.email]
                        nw[TaskTable.phone] = old[TaskTableOld.phone]
                        nw[TaskTable.status] = status
                        nw[TaskTable.comment] = old[TaskTableOld.comment]
                        nw[TaskTable.applicationDate] =
                            LocalDateTime.parse(old[TaskTableOld.applicationDate].replace(Regex(" (?<h>\\d):"), " 0$1:"), dateTimeFormat)
                        nw[TaskTable.commentReady] = old[TaskTableOld.commentReady]
                        nw[TaskTable.dateReady] =
                            old[TaskTableOld.dateReady]?.let { LocalDateTime.parse(it.replace(Regex(" (?<h>\\d):"), " 0$1:"), dateTimeFormat) }
                        nw[TaskTable.commentsClosing] = old[TaskTableOld.commentsClosing]
                        nw[TaskTable.closingDate] =
                            old[TaskTableOld.closingDate]?.let { LocalDateTime.parse(it.replace(Regex(" (?<h>\\d):"), " 0$1:"), dateTimeFormat) }
                        nw[TaskTable.photo1] = old[TaskTableOld.photo1]
                        nw[TaskTable.photo2] = old[TaskTableOld.photo2]
                        nw[TaskTable.photo3] = old[TaskTableOld.photo3]
                        nw[TaskTable.commentShift] = old[TaskTableOld.commentShift]
                        nw[TaskTable.planCompleteDate] =
                            old[TaskTableOld.planCompleteDate]?.let { LocalDate.parse(it, dateFormat) }
                        nw[TaskTable.categoryChange] = old[TaskTableOld.categoryChange]
                        nw[TaskTable.amosOrderNumber] = old[TaskTableOld.amosOrderNumber]
                        nw[TaskTable.departmentOgm] = old[TaskTableOld.departmentOgm]
                    } catch (e: Throwable) {
                        println("Error on ext_number = $id")
                        e.printStackTrace()
                        ok = false
                        return@insert
                    }
 */
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

object TaskTableOld : Table("query") {
    val extNumber = text("ext_number")
    val journalNumber = text("number_journal").nullable()
    val type = text("type")
    val priority = text("priority")
    val nameType = text("name_type")
    val locationRepair = text("lokation_repair").nullable()
    val count = text("count").nullable()
    val drawingNumber = text("drawing_number").nullable()
    val pageCount = text("page_count").nullable()
    val fullName = text("full_name")
    val division = text("division").nullable()
    val tgId = text("tg_id").nullable()
    val userCode = text("code_users").nullable()
    val email = text("email").nullable()
    val phone = text("phone").nullable()
    val status = text("status")
    val comment = text("comment").nullable()
    val applicationDate = text("application_date")
    val commentReady = text("comment_ready").nullable()
    val dateReady = text("date_ready").nullable()
    val commentsClosing = text("comments_closing").nullable()
    val closingDate = text("closing_date").nullable()
    val photo1 = text("photo_1").nullable()
    val photo2 = text("photo_2").nullable()
    val photo3 = text("photo_3").nullable()
    val commentShift = text("comment_shift").nullable()
    val planCompleteDate = text("plan_complet_date").nullable()
    val categoryChange = text("category_change").nullable()
    val amosOrderNumber = text("amos_order_number").nullable()
    val departmentOgm = text("department_ogm").nullable()
    val innerId = text("id")
}

//val TasksInWorkIds = listOf(
//    1688,
//    1586,
//    1774,
//    1587
//)
//
//val NewTasksIds = listOf(1797, 1798, 1799, 1800, 1801, 1802, 1803, 1804, 1805, 1806, 1807, 1808, 1809)

val dateFormat = LocalDate.Format { dayOfMonth(); char('.'); monthNumber(); char('.'); year() }
val timeFormat = LocalTime.Format {
    hour(); alternativeParsing({ char('.') }) { char(':') }; minute(); alternativeParsing({ char('.') }) {
    char(':')
}; second()
}
val dateTimeFormat = LocalDateTime.Format { date(dateFormat); char(' '); time(timeFormat) }

fun String.toDateTime() = LocalDateTime.parse(
    this.replace(Regex(" (?<h>\\d):"), " 0$1:").replace(" :", ":").replace(": ", ":"),
    dateTimeFormat
)

fun String.toDate() = LocalDate.parse(this, dateFormat)
fun main() {
    print("Enter old db name(default: utair-real): ")
    val oldDBName = readlnOrNull().let { if (it.isNullOrEmpty()) "utair-real" else it }
    print("Enter new db name(it can be same db as the old, default: utair1): ")
    val newDBName = readlnOrNull().let { if (it.isNullOrEmpty()) "utair1" else it }
    print("Enter old db user(default: postgres): ")
    val oldDBUser = readlnOrNull().let { if (it.isNullOrEmpty()) "postgres" else it }
    print("Enter new db user(default: old db user): ")
    val newDBUser = readlnOrNull().let { if (it.isNullOrEmpty()) oldDBUser else it }
    print("Enter old db password(default: system environment variable DB_PASSWORD): ")
    val oldDBPassword = readlnOrNull().let { if (it.isNullOrEmpty()) System.getenv("DB_PASSWORD") else it }
    print("Enter new db password(default: old db password): ")
    val newDBPassword = readlnOrNull().let { if (it.isNullOrEmpty()) oldDBPassword else it }
    val oldDB = Database.connect("jdbc:postgresql://localhost:5432/$oldDBName", user = oldDBUser, password = oldDBPassword)
    val newDB = Database.connect("jdbc:postgresql://localhost:5432/$newDBName", user = newDBUser, password = newDBPassword)

    val broken = mutableListOf<Pair<Int, Throwable>>()
    val tasks = mutableListOf<Task>()

    transaction(db = newDB) {
        SchemaUtils.drop(TaskTable)
        SchemaUtils.create(TaskTable)
    }
    transaction(db = oldDB) {
        TaskTableOld.selectAll().forEach { old ->
            val id = old[TaskTableOld.extNumber].toInt()
            val journalNumber = old[TaskTableOld.journalNumber] ?: "-"
//            val status = when (id) {
//                in TasksInWorkIds -> "В работе"
//                in NewTasksIds -> "В обработке"
//                else -> "Закрыта"
//            }

            try {
                val task = Task(
                    id = id,
                    journalNumber = journalNumber,
                    type = old[TaskTableOld.type],
                    priority = old[TaskTableOld.priority].replace("\uD83D\uDFE2 ", "").replace("\uD83D\uDD34 ", ""),
                    message = old[TaskTableOld.nameType],
                    locationRepair = old[TaskTableOld.locationRepair],
                    count = old[TaskTableOld.count],
                    drawingNumber = old[TaskTableOld.drawingNumber],
                    pageCount = old[TaskTableOld.pageCount],
                    fullName = old[TaskTableOld.fullName],
                    division = old[TaskTableOld.division],
                    tgId = old[TaskTableOld.tgId],
                    userCode = old[TaskTableOld.userCode],
                    email = old[TaskTableOld.email],
                    phone = old[TaskTableOld.phone],
                    status = old[TaskTableOld.status],
                    comment = old[TaskTableOld.comment]?.let { if (it == "Отсутствует" || it == "-") null else it },
                    applicationDate = old[TaskTableOld.applicationDate].toDateTime(),
                    commentReady = old[TaskTableOld.commentReady],
                    dateReady = old[TaskTableOld.dateReady]?.toDateTime(),
                    commentsClosing = old[TaskTableOld.commentsClosing],
                    closingDate = old[TaskTableOld.closingDate]?.toDateTime(),
                    photo1 = old[TaskTableOld.photo1],
                    photo2 = old[TaskTableOld.photo2],
                    photo3 = old[TaskTableOld.photo3],
                    commentShift = old[TaskTableOld.commentShift],
                    planCompleteDate = old[TaskTableOld.planCompleteDate]?.toDate(),
                    categoryChange = old[TaskTableOld.categoryChange],
                    amosOrderNumber = old[TaskTableOld.amosOrderNumber],
                    departmentOgm = old[TaskTableOld.departmentOgm]
                )
                tasks.add(task)
            } catch (e: Throwable) {
                println("Problems with ext_number: $id")
                broken.add(id to e)
            }


        }
    }
    if (broken.isNotEmpty()) {
        println()
        println("------------------------------------------")
        println("Problem ext_number`s:")
        for ((id, e) in broken) {
            println("$id - ${e.message}")
        }
        println("------------------------------------------")
        println("Do you want to continue?(y or n): ")
        var ans = readlnOrNull()
        while (ans != "y" && ans != "n" && ans != null) {
            print("Try again: ")
            ans = readlnOrNull()
        }
        if (ans == "n") {
            println("Stoping...")
            return
        }
    }
    transaction(db = newDB) {
        tasks.forEach { task ->
            TaskTable.insert {
                it[TaskTable.id] = task.id
                it[TaskTable.journalNumber] = task.journalNumber
                it[TaskTable.type] = task.type
                it[TaskTable.priority] = task.priority
                it[TaskTable.message] = task.message
                it[TaskTable.locationRepair] = task.locationRepair
                it[TaskTable.count] = task.count
                it[TaskTable.drawingNumber] = task.drawingNumber
                it[TaskTable.pageCount] = task.pageCount
                it[TaskTable.fullName] = task.fullName
                it[TaskTable.division] = task.division
                it[TaskTable.tgId] = task.tgId
                it[TaskTable.userCode] = task.userCode
                it[TaskTable.email] = task.email
                it[TaskTable.phone] = task.phone
                it[TaskTable.status] = task.status
                it[TaskTable.comment] = task.comment
                it[TaskTable.applicationDate] = task.applicationDate
                it[TaskTable.commentReady] = task.commentReady
                it[TaskTable.dateReady] = task.dateReady
                it[TaskTable.commentsClosing] = task.commentsClosing
                it[TaskTable.closingDate] = task.closingDate
                it[TaskTable.photo1] = task.photo1
                it[TaskTable.photo2] = task.photo2
                it[TaskTable.photo3] = task.photo3
                it[TaskTable.commentShift] = task.commentShift
                it[TaskTable.planCompleteDate] = task.planCompleteDate
                it[TaskTable.categoryChange] = task.categoryChange
                it[TaskTable.amosOrderNumber] = task.amosOrderNumber
                it[TaskTable.departmentOgm] = task.departmentOgm
            }

        }
    }

    print("Migrate ${tasks.size} tasks successfully!")

}