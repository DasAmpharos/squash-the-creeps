package io.github.dasampharos.game

import godot.ColorRect
import godot.Input
import godot.InputEvent
import godot.Node
import godot.PackedScene
import godot.PathFollow
import godot.Timer
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.extensions.instanceAs
import godot.global.GD
import io.github.dasampharos.game.util.onready

@RegisterClass
class Main : Node() {
	@Export
	@RegisterProperty
	lateinit var mobScene: PackedScene

	private val player by onready<Player>("Player")
	private val mobTimer by onready<Timer>("MobTimer")
	private val spawnLocation by onready<PathFollow>("SpawnPath/SpawnLocation")
	private val scoreLabel by onready<ScoreLabel>("UserInterface/ScoreLabel")
	private val retry by onready<ColorRect>("UserInterface/Retry")

	@RegisterFunction
	override fun _ready() {
		GD.randomize()
		player.signalHit.connect(this, this::onPlayerHit)
		mobTimer.timeout.connect(this, this::onMobTimerTimeout)
		retry.hide()
	}

	@RegisterFunction
	override fun _unhandledInput(event: InputEvent) {
		if (Input.isActionJustPressed("ui_accept") && retry.visible) {
			val tree = getTree()!!
			tree.reloadCurrentScene()
		}
	}

	@RegisterFunction
	fun onMobTimerTimeout() {
		val mob = mobScene.instanceAs<Mob>()!!
		spawnLocation.unitOffset = GD.randf()
		mob.signalSquashed.connect(scoreLabel, scoreLabel::onMobSquashed)
		mob.initialize(spawnLocation.translation, player.transform.origin)
		addChild(mob)
	}

	@RegisterFunction
	fun onPlayerHit() {
		retry.show()
		mobTimer.stop()
	}
}
