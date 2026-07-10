package xyz.meowing.lattice.command.utils

/**
 * Used to indicate and error in parsing.
 *
 * @author Stivais
 */
class SyntaxException(override val message: String) : Exception()
