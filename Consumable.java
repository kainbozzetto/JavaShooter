import org.lwjgl.util.vector.Vector2f;

public class Consumable {
	
	static int HEALTHPACK = 0;
	static int ENERGYPACK = 1;
	
	float x;
	float y;
	float a;
	
	boolean used;
	
	long consume_time;
	
	Entity entity;
	Vector2f velocity;
	
	public void effect(Player player) {}
	
	public void respawn() {}
	
	public void paint() {}

}
