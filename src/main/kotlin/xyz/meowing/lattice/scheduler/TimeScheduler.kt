package xyz.meowing.lattice.scheduler

import org.apache.logging.log4j.LogManager
import org.jetbrains.annotations.ApiStatus
import xyz.meowing.lattice.Lattice
import xyz.meowing.lattice.event.ClientEvent
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

/** Runs actions on background threads using wall-clock delays. */
object TimeScheduler {
    private val executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors())
    private val tasks = ConcurrentHashMap<Long, ScheduledFuture<*>>()
    private val logger = LogManager.getLogger(TimeScheduler::class.java)
    private val nextId = AtomicLong()

    interface Handle {
        val isCancelled: Boolean
        fun cancel()
    }

    init {
        Lattice.eventBus.register<ClientEvent.Stop> { shutdown() }
    }

    fun schedule(delayMillis: Long, action: () -> Unit): Handle {
        val id = nextId.getAndIncrement()
        tasks[id] = executor.schedule({
            runSafely(action)
            tasks.remove(id)
        }, delayMillis, TimeUnit.MILLISECONDS)
        return createHandle(id)
    }

    fun repeat(
        intervalMillis: Long,
        initialDelayMillis: Long = intervalMillis,
        stopCondition: () -> Boolean = { false },
        action: () -> Unit
    ): Handle {
        return repeatDynamic({ intervalMillis }, initialDelayMillis, stopCondition, action)
    }

    fun repeatDynamic(
        intervalProvider: () -> Long,
        initialDelayMillis: Long = intervalProvider(),
        stopCondition: () -> Boolean = { false },
        action: () -> Unit
    ): Handle {
        val id = nextId.getAndIncrement()

        fun scheduleNext(delay: Long) {
            if (stopCondition()) {
                tasks.remove(id)
                return
            }
            tasks[id] = executor.schedule({
                runSafely(action)
                if (tasks.containsKey(id)) scheduleNext(intervalProvider())
            }, delay, TimeUnit.MILLISECONDS)
        }

        scheduleNext(initialDelayMillis)
        return createHandle(id)
    }

    @ApiStatus.Internal
    fun shutdown() {
        tasks.clear()
        executor.shutdown()
    }

    private fun createHandle(id: Long): Handle {
        return object : Handle {
            override val isCancelled: Boolean get() = !tasks.containsKey(id)

            override fun cancel() {
                tasks.remove(id)?.cancel(false)
            }
        }
    }

    private fun runSafely(action: () -> Unit) {
        try {
            action()
        } catch (e: Exception) {
            logger.error("Caught error while trying to run action", e)
        }
    }
}
