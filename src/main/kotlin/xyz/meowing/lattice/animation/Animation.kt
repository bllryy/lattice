package xyz.meowing.lattice.animation

enum class EasingType {
    LINEAR,
    EASE_IN,
    EASE_OUT,
    EASE_IN_OUT,
}

enum class AnimationType {
    POSITION,
    SIZE,
    COLOR,
    ALPHA,
    CUSTOM,
}

data class AnimationTarget<T>(
    val startValue: T,
    val endValue: T,
    val setter: (T) -> Unit
)

abstract class Animation<T>(
    val target: AnimationTarget<T>,
    val duration: Long,
    val type: EasingType,
    val animationType: AnimationType,
    val elementId: String,
    val onComplete: (() -> Unit)? = null
) {
    private var startTime: Long = 0
    private var isStarted = false
    var isCompleted = false
        private set

    fun start() {
        if (!isStarted) {
            startTime = System.currentTimeMillis()
            isStarted = true
            AnimationManager.register(this)
        }
    }

    fun update(): Boolean {
        if (!isStarted || isCompleted) return false

        val elapsed = System.currentTimeMillis() - startTime
        val progress = (elapsed.toFloat() / duration).coerceIn(0f, 1f)
        val easedProgress = applyEasing(progress)

        target.setter(interpolate(target.startValue, target.endValue, easedProgress))

        if (progress >= 1f) {
            isCompleted = true
            onComplete?.invoke()
            return true
        }

        return false
    }

    private fun applyEasing(t: Float): Float = when (type) {
        EasingType.LINEAR -> t
        EasingType.EASE_IN -> t * t
        EasingType.EASE_OUT -> 1f - (1f - t) * (1f - t)
        EasingType.EASE_IN_OUT -> if (t < 0.5f) 2f * t * t else 1f - 2f * (1f - t) * (1f - t)
    }

    abstract fun interpolate(start: T, end: T, progress: Float): T
}

class FloatAnimation(
    target: AnimationTarget<Float>,
    duration: Long,
    type: EasingType,
    animationType: AnimationType,
    elementId: String,
    onComplete: (() -> Unit)? = null
) : Animation<Float>(target, duration, type, animationType, elementId, onComplete) {
    override fun interpolate(start: Float, end: Float, progress: Float): Float = start + (end - start) * progress
}

class ColorAnimation(
    target: AnimationTarget<Int>,
    duration: Long,
    type: EasingType,
    elementId: String,
    onComplete: (() -> Unit)? = null
) : Animation<Int>(target, duration, type, AnimationType.COLOR, elementId, onComplete) {
    override fun interpolate(start: Int, end: Int, progress: Float): Int {
        val a = channel(start shr 24, end shr 24, progress)
        val r = channel(start shr 16, end shr 16, progress)
        val g = channel(start shr 8, end shr 8, progress)
        val b = channel(start, end, progress)
        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }

    private fun channel(start: Int, end: Int, progress: Float): Int {
        val s = start and 0xFF
        val e = end and 0xFF
        return (s + (e - s) * progress).toInt()
    }
}

class VectorAnimation(
    target: AnimationTarget<Pair<Float, Float>>,
    duration: Long,
    type: EasingType,
    animationType: AnimationType,
    elementId: String,
    onComplete: (() -> Unit)? = null
) : Animation<Pair<Float, Float>>(target, duration, type, animationType, elementId, onComplete) {
    override fun interpolate(start: Pair<Float, Float>, end: Pair<Float, Float>, progress: Float): Pair<Float, Float> {
        return (start.first + (end.first - start.first) * progress) to
            (start.second + (end.second - start.second) * progress)
    }
}
