package io.github.dasampharos.game

import godot.Animation
import godot.AnimationPlayer
import godot.Area
import godot.Input
import godot.KinematicBody
import godot.Node
import godot.Spatial
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.annotation.RegisterSignal
import godot.core.Vector3
import godot.signals.Signal0
import godot.signals.signal
import io.github.dasampharos.game.util.onready
import io.github.dasampharos.game.util.slideCollisions
import kotlin.math.PI

@RegisterClass
class Player : KinematicBody() {
	@Export
	@RegisterProperty
	var speed = 14f

	@Export
	@RegisterProperty
	var fallAcceleration = 75f

	@Export
	@RegisterProperty
	var jumpImpulse = 20f

	@Export
	@RegisterProperty
	var bounceImpulse = 16f

	@RegisterSignal
	val signalHit: Signal0 by signal()

	private var velocity = Vector3.ZERO
	private val pivot: Spatial by onready("Pivot")
	private val mobDetector: Area by onready("MobDetector")
	private val animationPlayer: AnimationPlayer by onready("AnimationPlayer")

	@RegisterFunction
	override fun _ready() {
		mobDetector.bodyEntered.connect(this, this::onMobDetectorBodyEntered)
	}

	@RegisterFunction
	override fun _physicsProcess(delta: Double) {
		var direction = Vector3.ZERO
		if (Input.isActionPressed("move_right"))
			direction.x += 1
		if (Input.isActionPressed("move_left"))
			direction.x -= 1
		if (Input.isActionPressed("move_back"))
			direction.z += 1
		if (Input.isActionPressed("move_forward"))
			direction.z -= 1

		if (direction != Vector3.ZERO) {
			direction = direction.normalized()
			pivot.lookAt(translation + direction, Vector3.UP)
			animationPlayer.playbackSpeed = 4.0
		} else {
			animationPlayer.playbackSpeed = 1.0
		}

		if (isOnFloor() && Input.isActionJustPressed("jump")) {
			velocity.y += jumpImpulse
		}

		velocity.x = direction.x * speed
		velocity.z = direction.z * speed
		velocity.y -= fallAcceleration * delta

		slideCollisions.forEach { collision ->
			val collider = collision.collider as? Mob
			if (collider != null && collider.isInGroup("mob")) {
				if (Vector3.UP.dot(collision.normal) > 0.1) {
					velocity.y += bounceImpulse
					collider.squash()
				}
			}
		}

		velocity = moveAndSlide(velocity, Vector3.UP)
		pivot.rotation.x = PI / 6 * velocity.y / jumpImpulse
	}

	@RegisterFunction
	fun onMobDetectorBodyEntered(body: Node) {
		die()
	}

	private fun die() {
		signalHit.emit()
		queueFree()
	}
}
