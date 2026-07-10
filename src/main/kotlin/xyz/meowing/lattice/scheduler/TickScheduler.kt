package xyz.meowing.lattice.scheduler

import org.apache.logging.log4j.LogManager
import xyz.meowing.lattice.Lattice
import xyz.meowing.lattice.event.TickEvent
import java.util.PriorityQueue

/** Runs actions on the client tick. Wired to the event bus on first use. */
object TickScheduler {
    private val logger = LogManager.getLogger(TickScheduler::class.java)
    private val queue = PriorityQueue<Task>(compareBy { it.executeTick })
    private var nextId = 0L

    var currentTick = 0L
        private set

    interface Handle {
        val isCancelled: Boolean
        fun cancel()
    }

    private class Task(
        var executeTick: Long,
        val action: () -> Unit,
        val interval: () -> Long = { 0 }
    ) : Handle {
        override var isCancelled = false
            private set

        override fun cancel() {
            isCancelled = true
        }
    }

    init {
        Lattice.eventBus.register<TickEvent.Start> { onTick() }
    }

    private fun onTick() {
        currentTick++
        while (true) {
            val task = synchronized(queue) {
                queue.peek()?.takeIf { currentTick >= it.executeTick }?.let { queue.poll() }
            } ?: break

            if (task.isCancelled) continue

            try {
                task.action()
            } catch (e: Exception) {
                logger.error("Caught error while trying to run action", e)
            }

            val interval = task.interval()
            if (interval > 0 && !task.isCancelled) {
                task.executeTick = currentTick + interval
                synchronized(queue) { queue.offer(task) }
            }
        }
    }

    fun post(action: () -> Unit) {
        schedule(1, action)
    }

    fun schedule(delay: Long, action: () -> Unit): Handle {
        val task = Task(currentTick + delay, action)
        synchronized(queue) { queue.offer(task) }
        return task
    }

    fun repeat(interval: Long, initialDelay: Long = interval, action: () -> Unit): Handle {
        return repeatDynamic({ interval }, initialDelay, action)
    }

    fun repeatDynamic(
        intervalProvider: () -> Long,
        initialDelay: Long = intervalProvider(),
        action: () -> Unit
    ): Handle {
        val task = Task(currentTick + initialDelay, action, intervalProvider)
        synchronized(queue) { queue.offer(task) }
        return task
    }
}
