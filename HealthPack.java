import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

public class HealthPack extends Consumable  {
	
	int[] xxx = new int[4];
	int[] yyy = new int[4];
	
	public HealthPack(float xx, float yy, float aa) {
		x = xx;
		y = yy;
		a = aa;
		
		xxx[0] = -12;
		xxx[1] = -12;
		xxx[2] = 12;
		xxx[3] = 12;

		yyy[0] = 12;
		yyy[1] = -12;
		yyy[2] = -12;
		yyy[3] = 12;
		
		used = false;
		
		entity = new Entity(Entity.POLYGON, xxx, yyy);
		
		velocity = new Vector2f();
	}
	
	public void effect(Player player) {
		if(player.current_health < player.max_health) {
			used = true;
			consume_time = System.currentTimeMillis();
			player.change_health((int) (player.max_health/2));
		}
	}
	
	public void respawn() {
		if(System.currentTimeMillis() - consume_time > 5000) {
			used = false;
		}
	}
	
	public void paint() {
		// Paint Health Pack
		
		GL11.glPushMatrix();
			GL11.glTranslatef(x, y, 0);
			GL11.glRotatef((float)Math.toDegrees(-a), 0, 0, 1);
		
			GL11.glColor3f(0.7f, 0.3f, 0.3f);
			GL11.glBegin(GL11.GL_QUADS);
				for(int i = 0; i < 4; i++) {
					GL11.glVertex2f(xxx[i], yyy[i]);
				}
			GL11.glEnd();
			
			GL11.glColor3f(1.0f, 1.0f, 1.0f);
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex2f(-2, -10);
				GL11.glVertex2f(2, -10);
				GL11.glVertex2f(2, 10);
				GL11.glVertex2f(-2, 10);
			GL11.glEnd();
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex2f(-10, -2);
				GL11.glVertex2f(10, -2);
				GL11.glVertex2f(10, 2);
				GL11.glVertex2f(-10, 2);
			GL11.glEnd();
			
			GL11.glColor3f(0.05f, 0.05f, 0.05f);
			GL11.glBegin(GL11.GL_LINE_LOOP);
				for(int i = 0; i < 4; i++) {
					GL11.glVertex2f(xxx[i], yyy[i]);
				}
			GL11.glEnd();
		GL11.glPopMatrix();
	}

}
