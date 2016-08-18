import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

public class Player {
	
	static int max_projectiles = 50;
	
	// Projectiles
	Projectile[] projectile;
	int m_projectile;

	int ID;
	int team_ID;
	
	float x, y, a;
	
	float speed;
	
	int radius;
	
	float current_health;
	float max_health;
	
	float current_energy;
	float max_energy;
	
	Entity entity;
	
	Vector2f velocity;
	
	int current_projectile;
	long last_fire;
	int last_firing_interval;
	
	Game game;
	
	public Player(int id, int teamID, Game g) {
		ID = id;
		team_ID = teamID;
		
		x = y = a = 0;
		
		speed = 0.125f;
		
		radius = 20;
		
		current_health = max_health = 100;
		
		current_energy = max_energy = 400;
		
		entity = new Entity(Entity.CIRCLE, radius);
		
		velocity = new Vector2f();
		
		game = g;
		
		
		// Projectile init
		projectile = new Projectile[max_projectiles];
		m_projectile = 0;
		
		current_projectile = Projectile.FIREBALL;
	}
	
	// Movement functions
	
	public void move_forward(int delta) {
		velocity.x = (float) (-speed * delta * Math.sin(a));
		velocity.y = (float) (-speed * delta * Math.cos(a));
		
		x += velocity.x;
		y += velocity.y;
	}
	
	public void move_backward(final int delta) {
		velocity.x = (float) (speed * 0.6 * delta * Math.sin(a));
		velocity.y = (float) (speed * 0.6 * delta * Math.cos(a));

		x += velocity.x;
		y += velocity.y;
	}
	
	public void move_left(final int delta) {
		velocity.x = (float) (-speed * 0.8 * delta * Math.cos(a));
		velocity.y = (float) (speed * 0.8 * delta * Math.sin(a));

		x += velocity.x;
		y += velocity.y;
	}
	
	public void move_right(final int delta) {
		velocity.x = (float) (speed * 0.8 * delta * Math.cos(a));
		velocity.y = (float) (-speed * 0.8 * delta * Math.sin(a));

		x += velocity.x;
		y += velocity.y;
	}
	
	public void move_forwardleft(final int delta) {
		velocity.x = (float) (-speed * 0.8 * delta * Math.sin(a + Math.PI/4));
		velocity.y = (float) (-speed * 1.0 * delta * Math.cos(a + Math.PI/4));

		x += velocity.x;
		y += velocity.y;
	}
	
	public void move_forwardright(final int delta) {
		velocity.x = (float) (-speed * 0.8 * delta * Math.sin(a - Math.PI/4));
		velocity.y = (float) (-speed * 1.0 * delta * Math.cos(a - Math.PI/4));

		x += velocity.x;
		y += velocity.y;
	}
	
	public void move_backwardleft(final int delta) {
		velocity.x = (float) (speed * 0.8 * delta * Math.sin(a - Math.PI/4));
		velocity.y = (float) (speed * 0.6 * delta * Math.cos(a - Math.PI/4));

		x += velocity.x;
		y += velocity.y;
	}
	
	public void move_backwardright(final int delta) {
		velocity.x = (float) (speed * 0.8 * delta * Math.sin(a + Math.PI/4));
		velocity.y = (float) (speed * 0.6 * delta * Math.cos(a + Math.PI/4));

		x += velocity.x;
		y += velocity.y;
	}
	
	public void reset_velocity() {
		velocity.x = 0;
		velocity.y = 0;
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

			// Overlapped -> then seperate bodies
			if(velocity.x != 0)
				x += info.m_mtd.x;
			if(velocity.y != 0)
				y += info.m_mtd.y;
			
			if(player.velocity.x != 0)
				player.x -= info.m_mtd.x;
			if(player.velocity.y != 0)
				player.y -= info.m_mtd.y;

		}
		else if(info.m_collided)
		{
			float m_tcoll = info.m_tenter;
			System.out.println(m_tcoll);
			
			// Move to time of collision		
			x += velocity.x * m_tcoll;
			y += velocity.y * m_tcoll;
			
			player.x += player.velocity.x * m_tcoll;
			player.y += player.velocity.y * m_tcoll;		
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

			// Overlapped -> then seperate bodies
			x += info.m_mtd.x;
			y += info.m_mtd.y;

		}
		else if(info.m_collided)
		{

		}
	}
	
	// Player-StaticObject collision detection
	public void check_consumable_collision(Consumable consumable) {
		CollisionInfo info = new CollisionInfo();
			
		Entity thisEntity = entity;
		thisEntity.transform(x, y, a);
		
		Entity otherEntity = consumable.entity;
		otherEntity.transform(consumable.x, consumable.y, consumable.a);
		
		Vector2f delta = new Vector2f();
		Vector2f.sub(velocity, consumable.velocity, delta);
		
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

			// Effect function
			
			consumable.effect(this);

		}
		else if(info.m_collided)
		{

		}
	}
	
	// Set player spawn position
	public void set_spawn_position(float xx, float yy, float aa) {
		x = xx;
		y = yy;
		a = aa;
	}
	
	public void regenerate_energy(int delta) {
		current_energy += 0.025 * delta;
		
		if(current_energy > max_energy)
			current_energy = max_energy;
	}
	
	public void change_health(int amount) {
		current_health += amount;
		
		if(current_health > max_health)
			current_health = max_health;
		else if(current_health <= 0) {
			current_health = max_health;
			current_energy = max_energy;
			game.set_player_death_spawn(this);
		}
	}
	
	public void change_energy(int amount) {
		current_energy += amount;
		
		if(current_energy > max_energy)
			current_energy = max_energy;
		else if(current_energy < 0) {
			current_energy = 0;
		}
	}
	
	public void attack() {
		if(System.currentTimeMillis() - last_fire > last_firing_interval) {
			last_fire = System.currentTimeMillis();
						
			float projectileX = (float) (x-radius*Math.sin(a));
			float projectileY = (float) (y-radius*Math.cos(a));
			
			if(current_projectile == Projectile.FIREBALL && current_energy >= Fireball.energy_cost) {
				projectile[m_projectile++ % (max_projectiles)] = new Fireball(projectileX, projectileY, a, ID);
				last_firing_interval = Fireball.last_firing_interval;
				current_energy -= Fireball.energy_cost;
			}
			else if(current_projectile == Projectile.FIREARROW && current_energy >= FireArrow.energy_cost) {
				projectile[m_projectile++ % (max_projectiles)] = new FireArrow(projectileX, projectileY, (float)(a-Math.PI/32), ID);
				projectile[m_projectile++ % (max_projectiles)] = new FireArrow(projectileX, projectileY, a, ID);
				projectile[m_projectile++ % (max_projectiles)] = new FireArrow(projectileX, projectileY, (float)(a+Math.PI/32), ID);
				last_firing_interval = FireArrow.last_firing_interval;
				current_energy -= FireArrow.energy_cost;
			}
			else if(current_projectile == Projectile.LIGHTNINGSTRIKE && current_energy >= LightningStrike.energy_cost) {
				projectile[m_projectile++ % (max_projectiles)] = new LightningStrike(projectileX, projectileY, a, ID);
				last_firing_interval = LightningStrike.last_firing_interval;
				current_energy -= LightningStrike.energy_cost;
			}
			else if(current_projectile == Projectile.SHOCKLASER && current_energy >= ShockLaser.energy_cost) {
				projectile[m_projectile++ % (max_projectiles)] = new ShockLaser(projectileX, projectileY, a, ID);
				last_firing_interval = ShockLaser.last_firing_interval;
				current_energy -= ShockLaser.energy_cost;
			}
			else if(current_projectile == Projectile.FLAMEBURST && current_energy >= FlameBurst.energy_cost) {
				float angle = (float) ((1-2*Math.random())*Math.PI/12);
				projectile[m_projectile++ % (max_projectiles)] = new FlameBurst(projectileX, projectileY, a + angle, ID);
				last_firing_interval = FlameBurst.last_firing_interval;
				current_energy -= FlameBurst.energy_cost;
			}
		}
	}
	
	public void paintAimer(float x, float y) {
		GL11.glColor3f(0.1f, 0.1f, 0.1f);
		GL11.glBegin(GL11.GL_POINTS);
			for(int i = 0; i < (y-radius); i+=5) {
				GL11.glVertex2f(x, i);
				GL11.glVertex2f(x, i);
				GL11.glVertex2f(x, i);
			}
		GL11.glEnd();
	}
	
	public void paintHUD() {
		
		// Health Bar
		GL11.glPushMatrix();
			GL11.glTranslatef(20, 520, 0);
			
			GL11.glColor3f(.3f, 0.1f, 0.1f);
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex2f(0, 20);
				GL11.glVertex2f(0, 40);
				GL11.glVertex2f(60, 40);
				GL11.glVertex2f(60, 20);
			GL11.glEnd();
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex2f(20, 0);
				GL11.glVertex2f(40, 0);
				GL11.glVertex2f(40, 60);
				GL11.glVertex2f(20, 60);
			GL11.glEnd();
			
			double yy = 60 - (current_health * 60 / max_health);
			GL11.glColor3f(.8f, 0.4f, 0.4f);
			if(yy < 40) {
				GL11.glBegin(GL11.GL_QUADS);
					if(yy > 20) {
						GL11.glVertex2d(0, yy);
						GL11.glVertex2d(60, yy);
					} 
					else {
						GL11.glVertex2f(0, 20);
						GL11.glVertex2f(60, 20);
					}
						GL11.glVertex2f(60, 40);
						GL11.glVertex2f(0, 40);
					
				GL11.glEnd();
			}
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex2d(20, yy);
				GL11.glVertex2d(40, yy);
				GL11.glVertex2f(40, 60);
				GL11.glVertex2f(20, 60);
			GL11.glEnd();
			
			
			GL11.glColor3f(1.0f, 1.0f, 1.0f);
			GL11.glBegin(GL11.GL_LINE_LOOP);
				GL11.glVertex2f(20, 0);
				GL11.glVertex2f(40, 0);
				GL11.glVertex2f(40, 20);
				GL11.glVertex2f(60, 20);
				GL11.glVertex2f(60, 40);
				GL11.glVertex2f(40, 40);
				GL11.glVertex2f(40, 60);
				GL11.glVertex2f(20, 60);
				GL11.glVertex2f(20, 40);
				GL11.glVertex2f(0, 40);
				GL11.glVertex2f(0, 20);
				GL11.glVertex2f(20, 20);
			GL11.glEnd();
		GL11.glPopMatrix();
		
		// Energy Bar
		GL11.glPushMatrix();
			GL11.glTranslatef(120, 550, 0);
			
			GL11.glColor3f(0.1f, 0.1f, 0.3f);
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glVertex2f(0, 0);
				for(double angleD = 0; angleD <= 2*Math.PI; angleD += Math.PI/36) {
					GL11.glVertex2d(Math.sin(angleD) * 30, Math.cos(angleD) * 30);
				}
			GL11.glEnd();
			
			yy = 30 - current_energy * 60 / max_energy;
			
			double angle = Math.asin(yy/30);
			double xx = 30*Math.cos(angle);
			
			GL11.glColor3f(0.4f, 0.4f, 0.8f);
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glVertex2f(0, 0);
				GL11.glVertex2d(xx, yy);
				for(double angleD = -angle + Math.PI/2; angleD > -(-angle + Math.PI/2); angleD -= Math.PI/72) {						
					GL11.glVertex2d(Math.sin(angleD) * 30, Math.cos(angleD) * 30);
				}
				GL11.glVertex2d(-xx, yy);
			GL11.glEnd();
			
			if(yy > 0)
				GL11.glColor3f(0.1f, 0.1f, 0.3f);
			
			
			else
				GL11.glColor3f(0.4f, 0.4f, 0.8f);	

			GL11.glBegin(GL11.GL_TRIANGLES);
				GL11.glVertex2f(0, 0);
				GL11.glVertex2d(xx, yy);
				GL11.glVertex2d(-xx, yy);
			GL11.glEnd();

			
			GL11.glColor3f(1.0f, 1.0f, 1.0f);
			GL11.glBegin(GL11.GL_LINE_LOOP);
				for(double angleD = 0; angleD <= 2*Math.PI; angleD += Math.PI/36) {
					GL11.glVertex2d(Math.sin(angleD) * 30, Math.cos(angleD) * 30);
				}
			GL11.glEnd();
		GL11.glPopMatrix();
	}

	public void paintSelf() {
		// Circle
		GL11.glColor3f(0.3f+0.3f*ID/8, 0.5f+0.5f*(8-ID)/8, 0.8f*ID/8);
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glVertex2f(0, 0);
			for(double angle = 0; angle <= 2*Math.PI; angle += Math.PI/36) {
				GL11.glVertex2d(Math.sin(angle) * radius, Math.cos(angle) * radius);
			}
		GL11.glEnd();
		
		GL11.glColor3f(0.05f, 0.05f, 0.4f);
		GL11.glBegin(GL11.GL_LINE_LOOP);
			for(double angle = 0; angle <= 2*Math.PI; angle += Math.PI/36) {
				GL11.glVertex2d(Math.sin(angle) * radius, Math.cos(angle) * radius);
			}
		GL11.glEnd();
		
		// Triangle
		if(team_ID == 0)
			GL11.glColor3f(0.8f, 0.3f, 0.3f);
		else if(team_ID == 1)
			GL11.glColor3f(0.3f, 0.3f, 0.8f);
		else	
			GL11.glColor3f(0.3f, 0.8f, 0.3f);
		
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glVertex2f(0, -radius);
			GL11.glVertex2d(-radius*Math.sin(Math.PI/6), radius*Math.cos(Math.PI/6));
			GL11.glVertex2f(0, 0);
			GL11.glVertex2d(radius*Math.sin(Math.PI/6), radius*Math.cos(Math.PI/6));
		GL11.glEnd();
		
		GL11.glColor3f(0.0f, 0.0f, 0.0f);
		GL11.glBegin(GL11.GL_LINE_LOOP);
			GL11.glVertex2f(0, -radius);
			GL11.glVertex2d(-radius*Math.sin(Math.PI/6), radius*Math.cos(Math.PI/6));
			GL11.glVertex2f(0, 0);
			GL11.glVertex2d(radius*Math.sin(Math.PI/6), radius*Math.cos(Math.PI/6));
		GL11.glEnd();
	}
	
	public void paint() {
		// Circle
		GL11.glPushMatrix();
			GL11.glTranslatef(x, y, 0);
			GL11.glRotatef((float) -Math.toDegrees(a), 0, 0, 1);
		
			GL11.glColor3f(0.3f+0.3f*ID/8, 0.5f+0.5f*(8-ID)/8, 0.8f*ID/8);
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glVertex2f(0, 0);
				for(double angle = 0; angle <= 2*Math.PI; angle += Math.PI/36) {
					GL11.glVertex2d(Math.sin(angle) * radius, Math.cos(angle) * radius);
				}
			GL11.glEnd();
			
			GL11.glColor3f(0.05f, 0.05f, 0.4f);
			GL11.glBegin(GL11.GL_LINE_LOOP);
				for(double angle = 0; angle <= 2*Math.PI; angle += Math.PI/36) {
					GL11.glVertex2d(Math.sin(angle) * radius, Math.cos(angle) * radius);
				}
			GL11.glEnd();
			
			// Triangle
			if(team_ID == 0)
				GL11.glColor3f(0.8f, 0.3f, 0.3f);
			else if(team_ID == 1)
				GL11.glColor3f(0.3f, 0.3f, 0.8f);
			else	
				GL11.glColor3f(0.3f, 0.8f, 0.3f);
			
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glVertex2f(0, -radius);
				GL11.glVertex2d(-radius*Math.sin(Math.PI/6), radius*Math.cos(Math.PI/6));
				GL11.glVertex2f(0, 0);
				GL11.glVertex2d(radius*Math.sin(Math.PI/6), radius*Math.cos(Math.PI/6));
			GL11.glEnd();
			
			GL11.glColor3f(0.0f, 0.0f, 0.0f);
			GL11.glBegin(GL11.GL_LINE_LOOP);
				GL11.glVertex2f(0, -radius);
				GL11.glVertex2d(-radius*Math.sin(Math.PI/6), radius*Math.cos(Math.PI/6));
				GL11.glVertex2f(0, 0);
				GL11.glVertex2d(radius*Math.sin(Math.PI/6), radius*Math.cos(Math.PI/6));
			GL11.glEnd();
			
		GL11.glPopMatrix();
	}
}
