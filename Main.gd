extends Node

export (PackedScene) var mob_scene

func _ready():
	randomize()

func _on_MobTimer_timeout():
	var mob = mob_scene.instance()
	
	var spawn_location = $SpawnPath/SpawnLocation
	spawn_location.unit_offset = randf()
	var player_position = $Player.transform.origin
	mob.initialize(spawn_location.translation, player_position)
	add_child(mob)
