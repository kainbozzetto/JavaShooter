import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

public class Haste extends Consumable  {
	
	int radius;
	
	Player player_affected;
	float orig_speed;
	
	public Haste (float xx, float yy, float aa) {
		x = xx;
		y = yy;
		a = aa;
		
		radius = 12;
		
		used = false;
		
		entity = new Entity(Entity.CIRCLE, radius);
		
		velocity = new Vector2f();
	}
	
	public void effect(Player player) {
		used = true;
		consume_time = System.currentTimeMillis();
		player_affected = player;
		orig_speed = player.speed;
		player_affected.speed = (float) (orig_speed * 2);
	}
	
	public void respawn() {
		if(System.currentTimeMillis() - consume_time > 15000) {
			used = false;
		}
		if(System.currentTimeMillis() - consume_time > 10000) {
			player_affected.speed = orig_speed;
		}
	}
	
	public void paint() {
		// Paint Haste
		
		GL11.glPushMatrix();
			GL11.glTranslatef(x, y, 0);
			GL11.glRotatef((float)Math.toDegrees(-a), 0, 0, 1);
		
			GL11.glColor3f(0.7f, 0.7f, 0.3f);
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glVertex2f(0, -radius);
				GL11.glVertex2d(-radius*Math.sin(Math.PI/6), radius*Math.cos(Math.PI/6));
				GL11.glVertex2f(0, 0);
				GL11.glVertex2d(radius*Math.sin(Math.PI/6), radius*Math.cos(Math.PI/6));
			GL11.glEnd();
			
			GL11.glColor3f(0.05f, 0.05f, 0.05f);
			GL11.glBegin(GL11.GL_LINE_LOOP);
				GL11.glVertex2f(0, -radius);
				GL11.glVertex2d(-radius*Math.sin(Math.PI/6), radius*Math.cos(Math.PI/6));
				GL11.glVertex2f(0, 0);
				GL11.glVertex2d(radius*Math.sin(Math.PI/6), radius*Math.cos(Math.PI/6));
			GL11.glEnd();
			
		GL11.glPopMatrix();
	}

}
