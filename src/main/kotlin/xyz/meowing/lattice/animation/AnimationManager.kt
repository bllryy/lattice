package xyz.meowing.lattice.animation

object AnimationManager {
    private val activeAnimations = mutableListOf<Animation<*>>()

    val activeCount: Int
        get() = activeAnimations.size

    fun register(animation: Animation<*>) {
        stopAnimations(animation.elementId, animation.animationType)
        activeAnimations.add(animation)
    }

    fun update() {
        val activeList = activeAnimations.toList()
        val completed = activeList.filter { it.update() }
        activeAnimations.removeAll(completed)
    }

    fun clear() {
        activeAnimations.clear()
    }

    fun stopAnimations(elementId: String, animationType: AnimationType? = null) {
        activeAnimations.removeAll {
            it.elementId == elementId && (animationType == null || it.animationType == animationType)
        }
    }
}
