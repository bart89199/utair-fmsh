package ru.fmsh

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import ru.fmsh.database.configureDatabase

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDatabase()
    configureSerialization()
    configureSockets()
    configureRouting()
//    transaction {
//        SchemaUtils.create(TaskTable)
//    }
}
