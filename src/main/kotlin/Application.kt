package ru.fmsh

import io.ktor.server.application.*
import ru.fmsh.daatabase.configureDatabase

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
