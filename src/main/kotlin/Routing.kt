package ru.fmsh

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.getAs
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.LocalDate
import ru.fmsh.task.TaskService
import ru.fmsh.task.toDisplayTasks

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
            cause.printStackTrace()
        }
    }
    routing {
        route("api/task") {
            get("all") {
                val priority = call.queryParameters["priority"]?.split(",")
                val status = call.queryParameters["status"]?.split(",")
                val dateStart = call.queryParameters["date_start"]?.let { LocalDate.parse(it, LocalDate.Formats.ISO) }
                val dateEnd = call.queryParameters["date_end"]?.let { LocalDate.parse(it, LocalDate.Formats.ISO) }
                val find = call.queryParameters["find"]
                val planDateType = when(call.queryParameters["plan_date_type"]?.lowercase()) {
                    "future" -> TaskService.PlanDateType.FUTURE
                    "today" -> TaskService.PlanDateType.TODAY
                    "overdue" -> TaskService.PlanDateType.OVERDUE
                    else -> TaskService.PlanDateType.ALL
                }
                val limit = call.queryParameters["limit"]?.toIntOrNull() ?: environment.config.property("task.default.limit").getAs<Int>()
                val orderType = when(call.queryParameters["order_type"]) {
                    "id" -> TaskService.OrderType.ID
                    "journal" -> TaskService.OrderType.JOURNAL_NUMBER
                    else -> TaskService.OrderType.PLAN_DATE
                }
                val sortAsc = call.queryParameters["sort_asc"]?.lowercase() == "true"
                val res = TaskService.get(limit, priority, status, dateStart, dateEnd, find, planDateType, orderType, sortAsc).toDisplayTasks()
                call.respond(res)
            }
            get {
                val extNumber = call.queryParameters["ext_number"]?.toIntOrNull()
                if (extNumber == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val res = TaskService.getByExtNumber(extNumber)
                if (res == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond(res)
                }
            }
        }
        staticResources("/", "static")
    }
}
