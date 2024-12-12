package com.example

import com.example.model.Priority
import com.example.model.Task
import com.example.model.TaskRepository
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.lang.IllegalArgumentException

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        staticResources("/static", "static")

        route("/tasks"){
            get {
                val tasks = TaskRepository.allTasks()
                call.respond(tasks)
            }

            get("/byName/{name}"){
                val name = call.parameters["name"]
                if(name == null){
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val task = TaskRepository.taskByName(name)

                if(task == null){
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                call.respond(task)
            }

            get("/byPriority/{priority}"){
                val priorityParam = call.parameters["priority"]

                if(priorityParam == null){
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                try {
                    val priority = Priority.valueOf(priorityParam)
                    val tasks = TaskRepository.tasksByPriority(priority)

                    if(tasks.isEmpty()){
                        call.respond(HttpStatusCode.NotFound)
                        return@get
                    }
                    call.respond(tasks)
                }catch (e: IllegalArgumentException){
                    call.respond(HttpStatusCode.BadRequest)
                }catch (e : IllegalStateException){
                    call.respond(HttpStatusCode.BadRequest)
                }





            }

            post {
                try {

                    val task = call.receive<Task>()
                    TaskRepository.addTask(task)
                    call.respond(HttpStatusCode.Created)


                }catch (e: IllegalStateException){
                    call.respond(HttpStatusCode.BadRequest)
                }catch (e: JsonConvertException){
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            delete("/{taskName}"){
                val taskName = call.parameters["taskName"]

                if(taskName == null){
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }

                if(TaskRepository.removeTasks(taskName)){
                    call.respond(HttpStatusCode.NoContent)
                }else{
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }
}
