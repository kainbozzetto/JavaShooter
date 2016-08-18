import org.lwjgl.util.vector.Vector2f;

public class CollisionInfo {
	
	// Overlaps
	float m_mtdLengthSquared;
	Vector2f m_mtd;
	boolean m_overlapped;
	
	// Swept
	boolean m_collided;
	Vector2f m_Nenter;
	Vector2f m_Nleave;
	float m_tenter;
	float m_tleave;
	
	public CollisionInfo() {
		// Overlap test results
		m_mtdLengthSquared = 0.0f;
		m_mtd = new Vector2f(0, 0);
		m_overlapped = false;
		
		// Swept test results
		m_collided = false;
		m_Nenter = new Vector2f(0, 0);
		m_Nleave = new Vector2f(0, 0);
		m_tenter = 0.0f;
		m_tleave = 0.0f;
	}
}
