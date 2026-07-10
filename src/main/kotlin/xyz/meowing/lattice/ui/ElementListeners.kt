package xyz.meowing.lattice.ui

class ElementListeners {
    val mouseEnter = mutableListOf<(MouseEvent.Move.Enter) -> Unit>()
    val mouseExit = mutableListOf<(MouseEvent.Move.Exit) -> Unit>()
    val mouseMove = mutableListOf<(MouseEvent.Move) -> Unit>()
    val mouseScroll = mutableListOf<(MouseEvent.Scroll) -> Boolean>()
    val mouseClick = mutableListOf<(MouseEvent.Click) -> Boolean>()
    val mouseRelease = mutableListOf<(MouseEvent.Release) -> Boolean>()
    val charType = mutableListOf<(KeyEvent.Type) -> Boolean>()

    fun clear() {
        mouseEnter.clear()
        mouseExit.clear()
        mouseMove.clear()
        mouseScroll.clear()
        mouseClick.clear()
        mouseRelease.clear()
        charType.clear()
    }
}
