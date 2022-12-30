package io.github.dasampharos.game.util

import godot.KinematicBody
import godot.KinematicCollision
import godot.Node
import godot.core.NodePath
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.safeCast

val KinematicBody.slideCollisions: Sequence<KinematicCollision>
    get() =
        sequence {
            for (index in 0 until getSlideCount()) {
                val collision = getSlideCollision(index)
                if (collision != null) {
                    yield(collision)
                }
            }
        }

inline fun <reified T : Any> Node.onready(path: String): OnReady<T> = OnReady(this, T::class, path)

class OnReady<T : Any>(
    private val owner: Node,
    private val type: KClass<T>,
    private val path: String
) {
    private val ref: T by lazy {
        val path = NodePath(this.path)
        val node = owner.getNode(path)
        type.safeCast(node) ?: error(path)
    }

    operator fun getValue(thisRef: Any, property: KProperty<*>): T = ref
}