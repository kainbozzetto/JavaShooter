import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

public class FireArrow extends Projectile {
	
	// FireArrow variables
	
	static int last_firing_interval = 1000;
	static int energy_cost = 50;
	
	int[] xxx = new int[4];
	int[] yyy = new int[4];
	
	public FireArrow(float xx, float yy, float aa, int id) {
		// FireArrow variables
		
		xxx[0] = 1;
		xxx[1] = -1;
		xxx[2] = -1;
		xxx[3] = 1;
		
		yyy[0] = -3;
		yyy[1] = -3;
		yyy[2] = 3;
		yyy[3] = 3;	
		
		// Projectile variables
		x = initialX = xx;
		y = initialY = yy;
		a = aa;
		
		ID = id;
		
		speed = 0.45f;
		max_distance = 300;
		
		used = false;
		collision = 0;	
		
		entity = new Entity(Entity.POLYGON, xxx, yyy);
		velocity = new Vector2f();
	}
	
	public void player_collision(Player player) {
		player.change_health(-15);
	}
	
	public void static_object_collision(StaticObject object) { }
	
	public void paint() {
		if(!used) {
			GL11.glPushMatrix();
				GL11.glTranslatef(x, y, 0);
				GL11.glRotatef((float)Math.toDegrees(-a), 0, 0, 1);
				
				GL11.glColor3f(0.8f, 0.2f, 0.2f);
				GL11.glBegin(GL11.GL_QUADS);
					for(int i = 0; i < 4; i++) {
						GL11.glVertex2f(xxx[i], yyy[i]);
					}
				GL11.glEnd();
				
				GL11.glColor3f(0.4f, 0.05f, 0.05f);
				GL11.glBegin(GL11.GL_LINE_LOOP);
					for(int i = 0; i < 4; i++) {
						GL11.glVertex2f(xxx[i], yyy[i]);
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
						GL11.glVertex2d(Math.sin(angle) * 10, Math.cos(angle) * 10);
					}
				GL11.glEnd();
			GL11.glPopMatrix();
			
			collision--;
		}
	}

}
