extends KinematicBody

export var speed := 14
export var fall_acceleration := 75

export var jump_impulse := 20
export var bounce_impulse := 16

var velocity := Vector3.ZERO

func _physics_process(delta):
	var direction := Vector3.ZERO
	if Input.is_action_pressed("move_right"):
		direction.x += 1
	if Input.is_action_pressed("move_left"):
		direction.x -= 1
	if Input.is_action_pressed("move_back"):
		direction.z += 1
	if Input.is_action_pressed("move_forward"):
		direction.z -= 1
	
	if direction != Vector3.ZERO:
		direction = direction.normalized()
		$Pivot.look_at(translation + direction, Vector3.UP)
	
	if is_on_floor() and Input.is_action_just_pressed("jump"):
		velocity.y += jump_impulse
	
	velocity.x = direction.x * speed
	velocity.z = direction.z * speed
	velocity.y -= fall_acceleration * delta
	
	for index in range(get_slide_count()):
		var collision = get_slide_collision(index)
		var collider = collision.collider
		if collider != null && collider.is_in_group("mob"):
			if Vector3.UP.dot(collision.normal) > 0.1:
				collider.squash()
				velocity.y += bounce_impulse
	
	velocity = move_and_slide(velocity, Vector3.UP)
