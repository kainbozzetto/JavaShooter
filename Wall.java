import org.lwjgl.opengl.GL11;

public class Wall extends StaticObject {
	
	float height = 50, width = 50;
	
	public Wall(float xx, float yy) {
		x = xx;
		y = yy;
		a = 0;
		
		int[] xxx = new int[4];
		int[] yyy = new int[4];
		
		xxx[0] = -25;
		xxx[1] = -25;
		xxx[2] = 25;
		xxx[3] = 25;
		
		yyy[0] = -25;
		yyy[1] = 25;
		yyy[2] = 25;
		yyy[3] = -25;
		
		entity = new Entity(Entity.POLYGON, xxx, yyy);
	}
	
	public void paint() {
		// Paint Wall
		
		// Circle
		GL11.glColor3f(0.5f, 0.5f, 0.5f);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(x-width/2, y-height/2);
			GL11.glVertex2f(x-width/2, y+height/2);
			GL11.glVertex2f(x+width/2, y+height/2);
			GL11.glVertex2f(x+width/2, y-height/2);
		GL11.glEnd();
		
		GL11.glColor3f(0.05f, 0.05f, 0.05f);
		GL11.glBegin(GL11.GL_LINE_LOOP);
			GL11.glVertex2f(x-width/2, y-height/2);
			GL11.glVertex2f(x-width/2, y+height/2);
			GL11.glVertex2f(x+width/2, y+height/2);
			GL11.glVertex2f(x+width/2, y-height/2);
		GL11.glEnd();
	}
}