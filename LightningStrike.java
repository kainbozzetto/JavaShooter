import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

public class LightningStrike extends Projectile {
	
	// LightningStrike variables
	
	static int last_firing_interval = 1600;
	static int energy_cost = 90;
	
	int[] xxx = new int[4];
	int[] yyy = new int[4];
	
	public LightningStrike(float xx, float yy, float aa, int id) {
		// LightningStrike variables
		
		xxx[0] = 2;
		xxx[1] = -2;
		xxx[2] = -2;
		xxx[3] = 2;
		
		yyy[0] = 0;
		yyy[1] = 0;
		yyy[2] = -40;
		yyy[3] = -40;	
		
		// Projectile variables
		x = initialX = xx;
		y = initialY = yy;
		a = aa;
		
		ID = id;
		
		speed = 0.65f;
		max_distance = 1000;
		
		used = false;
		collision = 0;	
		
		entity = new Entity(Entity.POLYGON, xxx, yyy);
		velocity = new Vector2f();
	}
	
	public void player_collision(Player player) {
		player.change_health(-45);
	}
	
	public void static_object_collision(StaticObject object) { }
	
	public void paint() {
		if(!used) {
			GL11.glPushMatrix();
				GL11.glTranslatef(x, y, 0);
				GL11.glRotatef((float)Math.toDegrees(-a), 0, 0, 1);
				
				GL11.glColor3f(1.0f, 1.0f, 1.0f);
				GL11.glBegin(GL11.GL_QUADS);
					for(int i = 0; i < 4; i++) {
						GL11.glVertex2f(xxx[i], yyy[i]);
					}
				GL11.glEnd();
				
				GL11.glColor3f(0.7f, 0.7f, 0.2f);
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
				
				GL11.glColor3f(1.0f, 1.0f, 1.0f);
				for(double i = 0; i < 2*Math.PI; i += Math.PI/8) {
					GL11.glBegin(GL11.GL_LINES);
						GL11.glVertex2f(0, -40);
						GL11.glVertex2d(15*Math.cos(i), -40+15*Math.sin(i));
					GL11.glEnd();
				}
			GL11.glPopMatrix();
			
			collision--;
		}
	}

}
