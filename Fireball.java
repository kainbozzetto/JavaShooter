import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

public class Fireball extends Projectile {
	
	// Fireball variables
	static int last_firing_interval = 650;
	static int energy_cost = 30;
	
	int radius;
	
	public Fireball(float xx, float yy, float aa, int id) {
		// Fireball variables
		
		radius = 5;
		
		// Projectile variables
		x = initialX = xx;
		y = initialY = yy;
		a = aa;
		
		ID = id;
		
		speed = 0.35f;
		max_distance = 300;
		
		used = false;
		collision = 0;
		
		entity = new Entity(Entity.CIRCLE, radius);
		velocity = new Vector2f();
	}
	
	public void player_collision(Player player) {
		player.change_health(-20);
	}
	
	public void static_object_collision(StaticObject object) { }
	
	public void paint() {
		if(!used) {
			GL11.glPushMatrix();
				GL11.glTranslatef(x, y, 0);
				GL11.glRotatef((float)Math.toDegrees(-a), 0, 0, 1);
		
				GL11.glColor3f(0.8f, 0.5f, 0.1f);
				GL11.glBegin(GL11.GL_TRIANGLE_FAN);
					GL11.glVertex2f(0, 0);
					for(double angle = 0; angle <= 2*Math.PI; angle += Math.PI/36) {
						GL11.glVertex2d(Math.sin(angle) * radius, Math.cos(angle) * radius);
					}
				GL11.glEnd();
				
				GL11.glColor3f(0.4f, 0.05f, 0.05f);
				GL11.glBegin(GL11.GL_LINE_LOOP);
					for(double angle = 0; angle <= 2*Math.PI; angle += Math.PI/36) {
						GL11.glVertex2d(Math.sin(angle) * radius, Math.cos(angle) * radius);
					}
				GL11.glEnd();
			GL11.glPopMatrix();
		}
		// Explosion animation
		else if(used && collision > 0) {
			GL11.glPushMatrix();
				GL11.glTranslatef(x, y, 0);
				GL11.glRotatef((float)Math.toDegrees(-a), 0, 0, 1);

				GL11.glColor3f(0.8f, 0.8f, 0.1f);
				GL11.glBegin(GL11.GL_TRIANGLE_FAN);
					GL11.glVertex2f(0, 0);
					for(double angle = 0; angle <= 2*Math.PI; angle += Math.PI/36) {
						GL11.glVertex2d(-radius*Math.sin(a) + Math.sin(angle) * 20, -radius*Math.cos(a) + Math.cos(angle) * 20);
					}
				GL11.glEnd();
			GL11.glPopMatrix();
			
			collision--;
		}
	}

}
