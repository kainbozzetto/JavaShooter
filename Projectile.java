import org.lwjgl.util.vector.Vector2f;

public class Projectile {
	
	static int FIREBALL = 0;
	static int FIREARROW = 1;
	static int LIGHTNINGSTRIKE = 2;
	static int SHOCKLASER = 3;
	static int FLAMEBURST = 4;
	
	int ID;
	
	float x = 0;
	float y = 0;
	float a = 0;
	
	float initialX;
	float initialY;
	
	float speed;
	float max_distance;
	
	boolean used;
	int collision;
	
	Entity entity;
	Vector2f velocity;
	
	public Projectile() {
		used = true;
		collision = 0;
	}

	public boolean checkDistance() {
		double distance = Math.sqrt((x - initialX)*(x - initialX)+(y - initialY)*(y - initialY));
		if(distance > max_distance)
			return true;
		
		return false;
	}
	
	public void update_projectile(int delta) {
		if(!used) {
			velocity.x = (float) (-speed * delta * Math.sin(a));
			velocity.y = (float) (-speed * delta * Math.cos(a));
			x += velocity.x;
			y += velocity.y;
		
			used = checkDistance();
		}		
	}	
		
	// Player-Player collision detection
	public void check_player_collision(Player player) {
		CollisionInfo info = new CollisionInfo();
			
		Entity thisEntity = entity;
		thisEntity.transform(x, y, a);
		
		Entity otherEntity = player.entity;
		otherEntity.transform(player.x, player.y, player.a);
		
		Vector2f delta = new Vector2f();
		Vector2f.sub(velocity, player.velocity, delta);
		
		info = thisEntity.collide(otherEntity, delta);
		
		boolean m_collisionReported = (info.m_overlapped || info.m_collided);
		
		if(!m_collisionReported)
			return;
		
		if(info.m_overlapped)
		{
			if(info.m_mtdLengthSquared <= 0.00001f)
			{
				info.m_collided = false;
				return;
			}

			// Overlapped
			used = true;
			collision = 3;
			player_collision(player);
		}
	}
	
	// Player-StaticObject collision detection
	public void check_static_object_collision(StaticObject object) {
		CollisionInfo info = new CollisionInfo();
			
		Entity thisEntity = entity;
		thisEntity.transform(x, y, a);
		
		Entity otherEntity = object.entity;
		otherEntity.transform(object.x, object.y, object.a);
		
		Vector2f delta = new Vector2f();
		Vector2f.sub(velocity, object.velocity, delta);
		
		info = thisEntity.collide(otherEntity, delta);
		
		boolean m_collisionReported = (info.m_overlapped || info.m_collided);
		
		if(!m_collisionReported)
			return;
				
		if(info.m_overlapped)
		{
			if(info.m_mtdLengthSquared <= 0.00001f)
			{
				info.m_collided = false;
				return;
			}

			// Overlapped
			used = true;
			collision = 3;
			static_object_collision(object);
		}
	}
	
	public void player_collision(Player player) {}
	
	public void static_object_collision(StaticObject object) {}
	
	public void paint() {}
}