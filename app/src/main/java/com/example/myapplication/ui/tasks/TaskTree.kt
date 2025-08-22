package com.example.myapplication.ui.tasks

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


class TaskTree {
    private var root: Node? = null

    // date format must match exactly what you write into task.dueDate
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    /** Remove all nodes from the tree */
    fun clear() {
        root = null
    }

    fun insert(task: Task) {
        val key = try {
            LocalDateTime.parse(task.dueDate, formatter)
        } catch (e: DateTimeParseException) {
            // invalid or placeholder date—ignore
            return
        }
        root = insertRec(root, key, task)
    }

    private fun insertRec(node: Node?, key: LocalDateTime, task: Task): Node {
        // Standard BST insert
        if (node == null) {
            return Node(key, task)
        }

        if (key.isBefore(node.key)) {
            node.left = insertRec(node.left, key, task)
        } else {
            node.right = insertRec(node.right, key, task)
        }

        // Update height of current node
        node.height = 1 + maxOf(getHeight(node.left), getHeight(node.right))

        // Get balance factor
        val balance = getBalance(node)

        // Left Left Case
        if (balance > 1 && key.isBefore(node.left!!.key)) {
            return rightRotate(node)
        }

        // Right Right Case
        if (balance < -1 && key.isAfter(node.right!!.key)) {
            return leftRotate(node)
        }

        // Left Right Case
        if (balance > 1 && key.isAfter(node.left!!.key)) {
            node.left = leftRotate(node.left!!)
            return rightRotate(node)
        }

        // Right Left Case
        if (balance < -1 && key.isBefore(node.right!!.key)) {
            node.right = rightRotate(node.right!!)
            return leftRotate(node)
        }

        return node
    }

    private fun getHeight(node: Node?): Int {
        return node?.height ?: 0
    }

    private fun getBalance(node: Node?): Int {
        if (node == null) return 0
        return getHeight(node.left) - getHeight(node.right)
    }

    private fun rightRotate(y: Node): Node {
        val x = y.left!!
        val T2 = x.right

        // Perform rotation
        x.right = y
        y.left = T2

        // Update heights
        y.height = maxOf(getHeight(y.left), getHeight(y.right)) + 1
        x.height = maxOf(getHeight(x.left), getHeight(x.right)) + 1

        return x
    }

    private fun leftRotate(x: Node): Node {
        val y = x.right!!
        val T2 = y.left

        // Perform rotation
        y.left = x
        x.right = T2

        // Update heights
        x.height = maxOf(getHeight(x.left), getHeight(x.right)) + 1
        y.height = maxOf(getHeight(y.left), getHeight(y.right)) + 1

        return y
    }

    /** Get all tasks in order of due date (earliest first) */
    fun getTasksInOrder(): List<Task> {
        val tasks = mutableListOf<Task>()
        inOrderTraversal(root, tasks)
        return tasks
    }

    /** Get tasks due within the next X hours */
    fun getTasksDueWithinHours(hours: Int): List<Task> {
        val tasks = mutableListOf<Task>()
        val now = LocalDateTime.now()
        val deadline = now.plusHours(hours.toLong())
        getTasksDueWithinHoursRec(root, now, deadline, tasks)
        return tasks
    }

    private fun getTasksDueWithinHoursRec(node: Node?, now: LocalDateTime, deadline: LocalDateTime, tasks: MutableList<Task>) {
        if (node == null) return
        
        // Check left subtree first (earlier dates)
        getTasksDueWithinHoursRec(node.left, now, deadline, tasks)
        
        // Check current node
        if (node.key.isAfter(now) && node.key.isBefore(deadline)) {
            tasks.add(node.task)
        }
        
        // Check right subtree (later dates)
        getTasksDueWithinHoursRec(node.right, now, deadline, tasks)
    }

    private fun inOrderTraversal(node: Node?, tasks: MutableList<Task>) {
        if (node == null) return
        inOrderTraversal(node.left, tasks)
        tasks.add(node.task)
        inOrderTraversal(node.right, tasks)
    }

    private class Node(
        val key: LocalDateTime,
        val task: Task,
        var left: Node? = null,
        var right: Node? = null,
        var height: Int = 1  // Height of node for AVL balancing
    )
}

