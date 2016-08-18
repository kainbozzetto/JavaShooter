import org.lwjgl.util.vector.Vector2f;

public class SupportPoints {
	static int MAX_SUPPORTS = 4;
	Vector2f[] m_support;
	int m_count;
	
	public SupportPoints() {
		m_support = new Vector2f[MAX_SUPPORTS];
		m_count = 0;
	}
}
