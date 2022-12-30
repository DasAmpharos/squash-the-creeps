package io.github.dasampharos.game

import godot.Label
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty

@RegisterClass
class ScoreLabel : Label() {
	@RegisterProperty
	var score = 0

	@RegisterFunction
	fun onMobSquashed() {
		score += 1
		text = "Score: $score"
	}
}
