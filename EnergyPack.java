import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

public class EnergyPack extends Consumable  {
	
	int radius;
	
	public EnergyPack (float xx, float yy, float aa) {
		x = xx;
		y = yy;
		a = aa;
		
		radius = 12;
		
		used = false;
		
		entity = new Entity(Entity.CIRCLE, radius);
		
		velocity = new Vector2f();
	}
	
	public void effect(Player player) {
		if(player.current_energy < player.max_energy) {
			used = true;
			consume_time = System.currentTimeMillis();
			player.change_energy((int) (player.max_energy/4));
		}
	}
	
	public void respawn() {
		if(System.currentTimeMillis() - consume_time > 5000) {
			used = false;
		}
	}
	
	public void paint() {
		// Paint Energy Pack
		
		GL11.glPushMatrix();
			GL11.glTranslatef(x, y, 0);
			GL11.glRotatef((float)Math.toDegrees(-a), 0, 0, 1);
		
			GL11.glColor3f(0.3f, 0.3f, 0.7f);
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glVertex2f(0, 0);
				for(double angle = 0; angle <= 2*Math.PI; angle += Math.PI/36) {
					GL11.glVertex2d(Math.sin(angle) * radius, Math.cos(angle) * radius);
				}
			GL11.glEnd();
			
			GL11.glColor3f(0.05f, 0.05f, 0.05f);
			GL11.glBegin(GL11.GL_LINE_LOOP);
			for(double angle = 0; angle <= 2*Math.PI; angle += Math.PI/36) {
				GL11.glVertex2d(Math.sin(angle) * radius, Math.cos(angle) * radius);
			}
		GL11.glEnd();
			
		GL11.glPopMatrix();
	}

}
