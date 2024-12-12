package com.example.model

object TaskRepository {
    private val tasks = mutableListOf(
        Task("cleaning", "Clean the house", Priority.Low),
        Task("gardening", "Mow the lawn", Priority.Medium),
        Task("shopping", "Buy the groceries", Priority.High),
        Task("painting", "Paint the fence", Priority.Medium)
    )

    fun allTasks() = tasks

    fun tasksByPriority(priority: Priority) =
        tasks.filter { it.priority == priority }

    fun taskByName(name: String) =
        tasks.find { it.name.equals(name, true) }

    fun addTask(task: Task){
        if(taskByName(task.name) != null){
            throw IllegalStateException("Cannot duplicate tasks name!")
        }
        tasks.add(task)
    }

    fun removeTasks(taskName: String): Boolean{
        return tasks.removeIf { it.name == taskName }
    }
}
