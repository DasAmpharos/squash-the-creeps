package io.github.dasampharos.game

import godot.AnimationPlayer
import godot.KinematicBody
import godot.VisibilityNotifier
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.annotation.RegisterSignal
import godot.core.Vector3
import godot.global.GD
import godot.signals.Signal0
import godot.signals.signal
import io.github.dasampharos.game.util.onready
import kotlin.math.PI

@RegisterClass
class Mob : KinematicBody() {
	@Export
	@RegisterProperty
	var minSpeed = 10f

	@Export
	@RegisterProperty
	var maxSpeed = 18f

	@RegisterSignal
	val signalSquashed: Signal0 by signal()

	private var velocity = Vector3.ZERO
	private val animationPlayer: AnimationPlayer by onready("AnimationPlayer")
	private val visibilityNotifier: VisibilityNotifier by onready("VisibilityNotifier")

	@RegisterFunction
	override fun _ready() {
		visibilityNotifier.screenExited.connect(this, this::onScreenExited)
	}

	@RegisterFunction
	fun initialize(startPosition: Vector3, playerPosition: Vector3) {
		lookAtFromPosition(startPosition, playerPosition, Vector3.UP)
		rotateY(GD.randRange(-PI / 4, PI / 4))

		val randomSpeed = GD.randRange(minSpeed, maxSpeed)
		velocity = Vector3.FORWARD * randomSpeed
		velocity = velocity.rotated(Vector3.UP, rotation.y)
		animationPlayer.playbackSpeed = (randomSpeed / minSpeed).toDouble()
	}

	@RegisterFunction
	override fun _physicsProcess(delta: Double) {
		moveAndSlide(velocity)
	}

	@RegisterFunction
	fun squash() {
		signalSquashed.emit()
		queueFree()
	}

	@RegisterFunction
	fun onScreenExited() {
		queueFree()
	}
}
