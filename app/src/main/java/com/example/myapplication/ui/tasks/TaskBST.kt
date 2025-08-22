package com.example.myapplication.ui.tasks

/** super-thin BST for Tasks keyed by id */
class TaskBST {

    private class Node(var task: Task) {
        var left: Node?  = null
        var right: Node? = null
    }

    private var root: Node? = null

    /** wipe the whole tree */
    fun clear() { root = null }

    /** insert (or replace if the id already exists) */
    fun insert(t: Task) { root = insertRec(root, t) }

    private fun insertRec(cur: Node?, t: Task): Node {
        if (cur == null) return Node(t)
        when {
            t.id < cur.task.id -> cur.left  = insertRec(cur.left,  t)
            t.id > cur.task.id -> cur.right = insertRec(cur.right, t)
            else               -> cur.task  = t                     // replace
        }
        return cur
    }

    /** in-order traversal – handy if you ever need sorted output */
    fun toList(): List<Task> = buildList { inOrder(root) }

    private fun MutableList<Task>.inOrder(n: Node?) {
        if (n == null) return
        inOrder(n.left); add(n.task); inOrder(n.right)
    }
}
