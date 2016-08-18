import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;


// Client side functions are:
// * Setting up and sending the input packet (packet to server)
// * Receiving and processing the output packet (packet from server)
// * Display and rendering graphics (main processed functions from output packet)
// * Other client side functions such as menus and options
//
// Everything else is server side.

public class Game {

	// Screen width and height
	static int screen_width = 800;
	static int screen_height = 600;
	
	// Input variables
	static int KEY_W = 0x01;
	static int KEY_S = 0x02;
	static int KEY_A = 0x04;
	static int KEY_D = 0x08;
	static int KEY_1 = 0x10;
	static int KEY_2 = 0x20;
	static int KEY_3 = 0x40;
	static int KEY_4 = 0x80;
	static int KEY_5 = 0x100;
	static int KEY_6 = 0x200;
	static int MOUSE_1 = 0x400;
	static int MOUSE_2 = 0x800;
	
	// Max number of players
	static int max_players = 6;
	static int max_static_objects = 255;
	static int max_spawn_positions = 8;
	static int max_consumables = 50;

	// Client variables
	int ID;
	float mouse_sensitivity; // mouse sensitivity
	boolean aimer;
	
	
	// Server variables
	long lastFrame; // Time at last frame
	int delta; // Time between frames
	static boolean debug_entities = false;
	
	//TrueTypeFont font;
	
	// Players
	Player[] player;
	
	// Static objects
	StaticObject[] static_object;
	int m_static_object;
	
	// Consumables
	Consumable[] consumable;
	int m_consumable;
	
	// Spawn positions
	Vector3f[] spawn_positions;
	int m_spawn_positions;
	
	// Initialise display, graphics, map, players, projectile etc
	public void start() {
		initDisplay();		// Display
		initGL();			// Graphics
		initServerVars();	// Server variables
		initClientVars();	// Client variables
		initMapObjects();	// Map
		initPlayers();		// Players
		initProjectiles();	// Projectiles
	}
	
	public void initDisplay() {
		try {
			Display.setDisplayMode(new DisplayMode(screen_width, screen_height));
			Display.create();
			Mouse.setGrabbed(true);
			Mouse.setCursorPosition(screen_width/2, screen_height/2);
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void initGL() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, screen_width, screen_height, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		// Background Colour
		GL11.glClearColor(0.6275f, 0.9765f, 0.4373f, 0.0f);
	}
	
	public void initServerVars() {
		// Player init
		player = new Player[max_players];
		
		// Static Object init
		static_object = new StaticObject[max_static_objects];
		
		// Consumable init
		consumable = new Consumable[max_consumables];
		
		// Spawn positions init
		spawn_positions = new Vector3f[max_spawn_positions];
		
		// Update the last frame time
		lastFrame = getTime();
	}
	
	public void initClientVars() {
		// Clients current ID to control player with corresponding ID
		ID = 0;
		
		// Mouse sensitivity
		mouse_sensitivity = 0.15f;
		
		// Aimer
		aimer = false;
	}
	
	public void initMapObjects() {
		m_static_object = 0;
		
		for(int i = 0; i <= 20; i++) {
			static_object[m_static_object++] = new Wall(-500,-500+50*i);
		}
		for(int i = 0; i <= 20; i++) {
			static_object[m_static_object++] = new Wall(-500+50*i,500);
		}
		for(int i = 0; i <= 20; i++) {
			static_object[m_static_object++] = new Wall(500,-500+50*i);
		}
		for(int i = 0; i <= 20; i++) {
			static_object[m_static_object++] = new Wall(-500+50*i,-500);
		}
		
		static_object[m_static_object++] = new Wall(-100,-100);
		static_object[m_static_object++] = new Wall(-300,-300);
		static_object[m_static_object++] = new Wall(300,300);
		static_object[m_static_object++] = new Wall(100,100);
		static_object[m_static_object++] = new Wall(-100,100);
		static_object[m_static_object++] = new Wall(-300,300);
		static_object[m_static_object++] = new Wall(300,-300);
		static_object[m_static_object++] = new Wall(100,-100);

		m_spawn_positions = 0;
		
		spawn_positions[m_spawn_positions++] = new Vector3f(-200, -200, 0);
		spawn_positions[m_spawn_positions++] = new Vector3f(200, -200, 0);
		spawn_positions[m_spawn_positions++] = new Vector3f(200, 200, 0);
		spawn_positions[m_spawn_positions++] = new Vector3f(-200, 200, 0);
		spawn_positions[m_spawn_positions++] = new Vector3f(0, -200, 0);
		spawn_positions[m_spawn_positions++] = new Vector3f(-200, 0, 0);
		spawn_positions[m_spawn_positions++] = new Vector3f(0, 200, 0);
		spawn_positions[m_spawn_positions++] = new Vector3f(200, 0, 0);
		
		m_consumable = 0;
		
		consumable[m_consumable++] = new HealthPack(0, 275, 0);
		consumable[m_consumable++] = new HealthPack(0, -275, 0);
		consumable[m_consumable++] = new EnergyPack(275, 0, 0);
		consumable[m_consumable++] = new EnergyPack(-275, 0, 0);
		consumable[m_consumable++] = new HealthPack(450, 450, 0);
		consumable[m_consumable++] = new HealthPack(-450, -450, 0);
		consumable[m_consumable++] = new EnergyPack(450, -450, 0);
		consumable[m_consumable++] = new EnergyPack(-450, 450, 0);
		consumable[m_consumable++] = new Haste(0, 0, 0);
		
		
	}
	
	public void initPlayers() {
		for(int i = 0; i < max_players; i++) {
			player[i] = new Player(i, i % 2, this);
			//player[i].set_spawn_position(100, 100*i, (float) Math.PI*0*i);
			player[i].set_spawn_position(spawn_positions[i].x, spawn_positions[i].y, spawn_positions[i].z);
		}
	}
	
	public void initProjectiles() {
		// Might not be required
		for(int i = 0; i < max_players; i++) {
			for(int j = 0; j < Player.max_projectiles; j++) {
				player[i].projectile[j] = new Projectile();
			}
		}
	}
	
	public void loop() {
		// Main game loop
		while (!Display.isCloseRequested()) {
			// Time since last frame
			delta = getDelta();
			
			// Input function (client to server)
			input();
			
			// Update function
			for(int i = 0; i < max_players; i++) {
				player[i].regenerate_energy(delta);
				for(int j = 0; j < Player.max_projectiles; j++) {
					if(!player[i].projectile[j].used)
						player[i].projectile[j].update_projectile(delta);
				}
			}
			
			for(int i = 0; i < m_consumable; i++) {
				if(consumable[i].used)
					consumable[i].respawn();
			}
			

			// Collision detection and response
			collision();
			
			// Output function  (server to client)
			
			// Render graphic function (client side)
			renderGL();
			
			// Display update
			Display.update();
			// Cap FPS to 60 FPS
			Display.sync(60);
		}
	}
	
	// Set player spawn upon death
	
	public void set_player_death_spawn(Player thisPlayer) {
		double d = 0;
		int k = 0;
		
		for(int i = 0; i < m_spawn_positions; i++) {
			for(int j = 0; j < max_players; j++) {
				double dd = Math.sqrt((spawn_positions[i].x - player[j].x)*(spawn_positions[i].x - player[j].x) + (spawn_positions[i].y - player[j].y)*(spawn_positions[i].y - player[j].y)); 
				if(dd > d) {
					d = dd;
					k = i;
				}
			}
		}
		
		thisPlayer.set_spawn_position(spawn_positions[k].x, spawn_positions[k].y, spawn_positions[k].z);
	}
	
	// Input function
	public void input() {
		for(int i = 0; i < max_players; i++) {
			handle_input(player[i], get_input(player[i]), get_mouse_movement(player[i]));
		}
	}
	
	public void collision() {
		// Player-player collisions
		for(int i = 0; i < max_players; i++) {
			for(int j = i+1; j < max_players; j++) {
				if(player[i].team_ID != player[j].team_ID)
					player[i].check_player_collision(player[j]);
			}
		}
		
		// Player-static object collisions
		for(int i = 0; i < max_players; i++) {
			for(int j = 0; j < m_static_object; j++) {
				player[i].check_static_object_collision(static_object[j]);
			}
		}
		
		// Player-consumable collisions
		for(int i = 0; i < max_players; i++) {
			for(int j = 0; j < m_consumable; j++) {
				if(!consumable[j].used)
					player[i].check_consumable_collision(consumable[j]);
			}
		}
		
		// Projectile-player collisions
		for(int i = 0; i < max_players; i++) {
			for(int j = 0; j < Player.max_projectiles; j++) {
				for(int k = 0; k < max_players; k++) {
					if(!player[i].projectile[j].used && i != k && player[i].team_ID != player[k].team_ID)
						player[i].projectile[j].check_player_collision(player[k]);
				}
			}
		}
		
		// Projectile-static object collisions
		for(int i = 0; i < max_players; i++) {
			for(int j = 0; j < Player.max_projectiles; j++) {
				for(int k = 0; k < m_static_object; k++) {
					if(!player[i].projectile[j].used)
						player[i].projectile[j].check_static_object_collision(static_object[k]);
				}
			}
		}

	}
	
	// Client function
	// Setup and sends the input packet (packet to server)
	public int get_input(Player player) {
		int input = 0;
		
		// Human player [player.ID == ID] has normal input
		// Computer players [player.ID != ID] can have set input
		// When introducing multiplayer all players will be subject to normal input
		
		if(player.ID == ID) {
			// Keyboard input
			if(Keyboard.isKeyDown(Keyboard.KEY_W))
				input += KEY_W;
			if(Keyboard.isKeyDown(Keyboard.KEY_S))
				input += KEY_S;
			if(Keyboard.isKeyDown(Keyboard.KEY_A))
				input += KEY_A;
			if(Keyboard.isKeyDown(Keyboard.KEY_D))
				input += KEY_D;
			if(Keyboard.isKeyDown(Keyboard.KEY_1))
				input += KEY_1;
			if(Keyboard.isKeyDown(Keyboard.KEY_2))
				input += KEY_2;
			if(Keyboard.isKeyDown(Keyboard.KEY_3))
				input += KEY_3;
			if(Keyboard.isKeyDown(Keyboard.KEY_4))
				input += KEY_4;
			if(Keyboard.isKeyDown(Keyboard.KEY_5))
				input += KEY_5;
			if(Keyboard.isKeyDown(Keyboard.KEY_6))
				input += KEY_6;
			
			// Mouse input
			if(Mouse.isButtonDown(0))
				input += MOUSE_1;
			if(Mouse.isButtonDown(1))
				input += MOUSE_2;
			
			// Single key events
			while(Keyboard.next()) {
				
				// Menu (currently ungrabs or grabs mouse)
				if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE && Keyboard.getEventKeyState()) {
					Mouse.setGrabbed(!Mouse.isGrabbed());
					
					if(Mouse.isGrabbed())
						Mouse.setCursorPosition(screen_width/2, screen_height/2);
				}
				
				if(Keyboard.getEventKey() == Keyboard.KEY_F && Keyboard.getEventKeyState()) {
					aimer = !aimer;
				}
					
				// Test Keys (temporary for the purposes of testing)
				if(Keyboard.getEventKey() == Keyboard.KEY_LEFT && Keyboard.getEventKeyState()) {
					ID--;
					if(ID < 0)
						ID = 0;
				}
				if(Keyboard.getEventKey() == Keyboard.KEY_RIGHT && Keyboard.getEventKeyState()) {
					ID++;
					if(ID >= max_players)
						ID = max_players - 1;
				}
			}
		}
		else {
			input += KEY_W;
			input += MOUSE_1;
		}
		
		return input;
	}
	
	// Client function
	// Returns the mouse movement
	public int get_mouse_movement(Player player) {
		int movement = 0;
		
		// Human player [player.ID == ID] has normal input
		// Computer players [player.ID != ID] can have set input
		// When introducing multiplayer all players will be subject to normal input
		
		if(player.ID == ID) {
			// Update mouse position when ingame (i.e. not when in menu etc)
			if(Mouse.isGrabbed()) {
				movement = Mouse.getX() - screen_width/2;
				Mouse.setCursorPosition(screen_width/2, screen_height/2);
			}
		}
		else {
			movement = -3 + 6*(player.ID);
		}
		
		return movement;
	}
	
	public void handle_input(Player player, final int input, final int mouse_movement) // final required?
	{			
		// Mouse movement
		player.a -= Math.toRadians(mouse_sensitivity * mouse_movement);
		
		// Movement input
		player.reset_velocity();
		
		if((input & KEY_W) + (input & KEY_A) == KEY_W + KEY_A)
			player.move_forwardleft(delta);
		else if((input & KEY_W) + (input & KEY_D) == KEY_W + KEY_D)
			player.move_forwardright(delta);
		else if((input & KEY_S) + (input & KEY_A) == KEY_S + KEY_A)
			player.move_backwardleft(delta);
		else if((input & KEY_S) + (input & KEY_D) == KEY_S + KEY_D)
			player.move_backwardright(delta);
		else if((input & KEY_W) == KEY_W)
			player.move_forward(delta);
		else if((input & KEY_S) == KEY_S)
			player.move_backward(delta);
		else if((input & KEY_A) == KEY_A)
			player.move_left(delta);
		else if((input & KEY_D) == KEY_D)
			player.move_right(delta);
		
		// Weapon selection
		if((input & KEY_1) == KEY_1) {
			player.current_projectile = Projectile.FIREBALL;
		}
		else if((input & KEY_2) == KEY_2) {
			player.current_projectile = Projectile.FIREARROW;
		}
		else if((input & KEY_3) == KEY_3) {
			player.current_projectile = Projectile.LIGHTNINGSTRIKE;
		}
		else if((input & KEY_4) == KEY_4) {
			player.current_projectile = Projectile.SHOCKLASER;
		}
		else if((input & KEY_5) == KEY_5) {
			player.current_projectile = Projectile.FLAMEBURST;
		}
		else if((input & KEY_6) == KEY_6) {
			
		}
		
		// Attack
		if((input & MOUSE_1) == MOUSE_1) {
			player.attack();
		}
		
		// Utility
		if((input & MOUSE_2) == MOUSE_2) {
			
		}			
	}
	
	public int getDelta() {
	    long time = getTime();
	    int Delta = (int) (time - lastFrame);
	    lastFrame = time;
 
	    return Delta;
	}
 
	public long getTime() {
	    return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	public static void sleep(long duration) {
		try {
			Thread.sleep((duration * Sys.getTimerResolution()) / 1000);
		} catch (InterruptedException inte) {
		}
	}
	
	public void renderGL() {
		// Clear The Screen And The Depth Buffer
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		
		// Remaining objects
		GL11.glPushMatrix();
			GL11.glTranslatef(screen_width/2, screen_height/2, 0);
			GL11.glRotatef((float) Math.toDegrees(player[ID].a), 0f, 0f, 1f); // Rotation in degrees
			GL11.glTranslated(-player[ID].x, -player[ID].y, 0);
			
			for(int i = 0; i < m_consumable; i++) {
				if(!consumable[i].used)
					consumable[i].paint();
			}
		
			for(int i = 0; i < max_players; i++) {
				for(int j = 0; j < Player.max_projectiles; j++) {
					player[i].projectile[j].paint();
					if(debug_entities && !player[i].projectile[j].used) {
						player[i].projectile[j].entity.paint();
					}
				}
			}
			
			for(int i = 0; i < max_players; i++) {
				if(i != ID)
					player[i].paint();
			}
			
			for(int i = 0; i < m_static_object; i++) {
					static_object[i].paint();
					if(debug_entities) {
						static_object[i].entity.paint();
					}	
			}
		GL11.glPopMatrix();
		
		
		// Player
		GL11.glPushMatrix();
			GL11.glTranslatef(screen_width/2, screen_height/2, 0);
			
			player[ID].paintSelf();
		GL11.glPopMatrix();
		
		player[ID].paintHUD();
		
		if(aimer)
			player[ID].paintAimer(screen_width/2, screen_height/2);
	}
	
	public void stop() {
		Display.destroy();
	}
	
	public static void main(String[] argv) {
		Game game = new Game();
		game.start();
		game.loop();
		game.stop();
	}
}
