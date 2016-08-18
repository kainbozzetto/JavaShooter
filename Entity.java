import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.opengl.GL11;

public class Entity {

	// Entity shapes
	static int CIRCLE = 0;
	static int POLYGON = 1;
	
	// Required?
	static int max_vectors = 32;
	
	// Vertices vectors
	int m_count;
	Vector2f[] m_orig_vertices;
	Vector2f[] m_vertices;
	
	// Type of entity shape
	int type;
	
	// For circles only
	int radius;
	
	public Entity(int Type, int Radius) {
		type = Type;
		
		radius = Radius;
		
		m_count = 1;
		m_vertices = new Vector2f[m_count];
		m_orig_vertices = new Vector2f[m_count];
		
		m_orig_vertices[0] = new Vector2f(0, 0);
	}
	
	public Entity(int Type, int[] xx, int[] yy) {
		type = Type;
		
		m_count = xx.length;
		m_vertices = new Vector2f[m_count];
		m_orig_vertices = new Vector2f[m_count];
		
		for(int i = 0; i < m_count; i++) {
			m_orig_vertices[i] = new Vector2f(xx[i], yy[i]);
		}
	}
	
	public void load_vertices() {
		for(int i = 0; i < m_count; i++) {
			m_vertices[i] = new Vector2f(m_orig_vertices[i].x, m_orig_vertices[i].y);
		}
	}
	
	public void transform(float x, float y, float a) {
		load_vertices();

		for(int i = 0; i < m_count; i++) {
			double xx = m_vertices[i].x * Math.cos(a) + m_vertices[i].y * Math.sin(a);
			double yy = -m_vertices[i].x * Math.sin(a) + m_vertices[i].y * Math.cos(a);
			m_vertices[i].x = (float) xx;
			m_vertices[i].y = (float) yy;
			m_vertices[i].translate(x, y);
		}
	}
/*	
	public CollisionInfo collide(Entity entity, Vector2f delta) {
		
		CollisionInfo info = new CollisionInfo();
		
		// Reset info
		info.m_overlapped = true;
		info.m_collided = true;
		info.m_mtdLengthSquared = -1.0f;
		info.m_tenter = 1.0f;
		info.m_tleave = 0.0f;
		
		
		Vector2f edge = new Vector2f();
		Vector2f axis = new Vector2f();
		
		Vector2f v0;
		Vector2f v1;
				
		for(int j = m_count-1, i = 0; i < m_count; j = i, i++) {
			if(type != CIRCLE) {
				v0 = m_vertices[j];
				v1 = m_vertices[i];
				
				Vector2f.sub(v1, v0, edge);			
				edge.normalise(axis);
			
				if (seperatedByAxis(axis, entity, delta, info))
					return (new CollisionInfo());
			}
			if(entity.type == CIRCLE) {
				v0 = entity.m_vertices[0];
				v1 = m_vertices[i];
				
				Vector2f.sub(v1, v0, edge);			
				edge.normalise(axis);
			
				if (seperatedByAxis(axis, entity, delta, info))
					return (new CollisionInfo());
			}
		}
		
		for(int j = entity.m_count-1, i = 0; i < entity.m_count; j = i, i++) {

			if(type == CIRCLE) {
				v0 = m_vertices[0];
				v1 = entity.m_vertices[i];
				
				Vector2f.sub(v1, v0, edge);			
				edge.normalise(axis);
			
				if (seperatedByAxis(axis, entity, delta, info))
					return (new CollisionInfo());
			}
			if(entity.type != CIRCLE) {
				v0 = entity.m_vertices[j];
				v1 = entity.m_vertices[i];
				
				
				Vector2f.sub(v1, v0, edge);			
				edge.normalise(axis);
				
				if (seperatedByAxis(axis, entity, delta, info))
					return (new CollisionInfo());
			}
			
		}
			
		assert(!(info.m_overlapped) || (info.m_mtdLengthSquared >= 0.0f));
		assert(!(info.m_collided) || (info.m_tenter <= info.m_tleave));

		// sanity checks
		info.m_overlapped &= (info.m_mtdLengthSquared >= 0.0f);
		info.m_collided   &= (info.m_tenter <= info.m_tleave);	

		// normalise normals
		if(info.m_Nenter.x != 0 && info.m_Nenter.y != 0)
			info.m_Nenter.normalise();
		if(info.m_Nleave.x != 0 && info.m_Nleave.y != 0)
			info.m_Nleave.normalise();
		
		return info;
	}
	
	public float[] calculateInterval(Vector2f axis) {
		float min = 0, max = 0;
		
		if(type == CIRCLE)
		{
			min = Vector2f.dot(m_vertices[0], axis) - radius;
			max = Vector2f.dot(m_vertices[0], axis) + radius;
		}
		
		else {
			min = max = Vector2f.dot(m_vertices[0], axis);
			
			for (int i = 1; i < m_count; i++) {
				float d = Vector2f.dot(m_vertices[i], axis);
				if (d < min)
					min = d;
				else if (d > max)
					max = d;
			}
		}
		
		float[] minmax = new float[2];
		minmax[0] = min;
		minmax[1] = max;
		
		return minmax;
	}
	
	public boolean intervalsSeperated(float mina, float maxa, float minb, float maxb) {
		return (mina > maxb) || (minb > maxa);
	}
	
	public boolean seperatedByAxis(Vector2f axis, Entity entity, Vector2f delta, CollisionInfo info) {
		float mina = 0, maxa = 0;
		float minb = 0, maxb = 0;
		
		float[] a = new float[2];
		float[] b = new float[2];
		
		a = calculateInterval(axis);
		b = entity.calculateInterval(axis);
		
		mina = a[0];
		maxa = a[1];
		minb = b[0];
		maxb = b[1];
		
		float d0 = (maxb - mina);
		float d1 = (minb - maxa);
		float v = Vector2f.dot(axis, delta);
		
		boolean sep_overlap = seperatedByAxis_overlap(axis, d0, d1, info);
		boolean sep_swept = seperatedByAxis_swept(axis, d0, d1, v, info);
		
		return (sep_overlap && sep_swept);
	}
	
	boolean seperatedByAxis_overlap(Vector2f axis, float d0, float d1, CollisionInfo info) {
		
		if(!info.m_overlapped)
			return true;
		
		if(d0 < 0.0f || d1 > 0.0f) {
			info.m_overlapped = false;
			return true;
		}
	
		float overlap = (d0 < -d1)? d0 : d1;
		
		float axis_length_squared = axis.lengthSquared();
		assert(axis_length_squared > 0.00001f);
		
		Vector2f sep = new Vector2f();
		sep.set(axis);
		sep.scale(overlap/axis_length_squared);
		
		float sep_length_squared = sep.lengthSquared();

		if(sep_length_squared < info.m_mtdLengthSquared || info.m_mtdLengthSquared < 0.0f)
		{
			info.m_mtdLengthSquared = sep_length_squared;
			info.m_mtd = sep;
		}
		
		return false;
	}
	
	boolean seperatedByAxis_swept(Vector2f axis, float d0, float d1, float v, CollisionInfo info)
	{
		if(!info.m_collided)
			return true;

		// projection too small. ignore test
		if(Math.abs(v) < 0.0000001f) return true;

		Vector2f N0 = new Vector2f();
		N0.set(axis);
		Vector2f N1 = new Vector2f(-axis.x, -axis.y);
		
		float t0 = d0 / v;   // estimated time of collision to the 'left' side
		float t1 = d1 / v;  // estimated time of collision to the 'right' side

		// sort values on axis
		// so we have a valid swept interval
		if(t0 > t1) 
		{
			float ttemp = t0;
			t0 = t1;
			t1 = ttemp;
			
			Vector2f Ntemp = new Vector2f();
			Ntemp.set(N0);
			N0.set(N1);
			N1.set(Ntemp);
		}
		
		// swept interval outside [0, 1] boundaries. 
		// polygons are too far apart
		if(t0 > 1.0f || t1 < 0.0f)
		{
			info.m_collided = false;
			return true;
		}

		// the swept interval of the collison result hasn't been
		// performed yet.
		if(info.m_tenter > info.m_tleave)
		{
			info.m_tenter = t0;
			info.m_tleave = t1;
			info.m_Nenter = N0;
			info.m_Nleave = N1;
			// not separated
			return false;
		}
		// else, make sure our current interval is in 
		// range [info.m_tenter, info.m_tleave];
		else
		{
			// separated.
			if(t0 > info.m_tleave || t1 < info.m_tenter)
			{
				info.m_collided = false;
				return true;
			}

			// reduce the collison interval
			// to minima
			if (t0 > info.m_tenter)
			{
				info.m_tenter = t0;
				info.m_Nenter = N0;
			}
			if (t1 < info.m_tleave)
			{
				info.m_tleave = t1;
				info.m_Nleave = N1;
			}			
			// not separated
			return false;
		}
	}
	*/
public CollisionInfo collide(Entity entity, Vector2f delta) {
		
		CollisionInfo info = new CollisionInfo();
		info.m_mtdLengthSquared = -1.0f;
		
		Vector2f edge = new Vector2f();
		Vector2f axis = new Vector2f();
		
		Vector2f v0;
		Vector2f v1;
				
		for(int j = m_count-1, i = 0; i < m_count; j = i, i++) {
			if(type != CIRCLE) {
				v0 = m_vertices[j];
				v1 = m_vertices[i];
				
				Vector2f.sub(v1, v0, edge);			
				edge.normalise(axis);
			
				if (seperatedByAxis(axis, entity, info))
					return (new CollisionInfo());
			}
			if(entity.type == CIRCLE) {
				v0 = entity.m_vertices[0];
				v1 = m_vertices[i];
				
				Vector2f.sub(v1, v0, edge);			
				edge.normalise(axis);
			
				if (seperatedByAxis(axis, entity, info))
					return (new CollisionInfo());
			}
		}
		
		for(int j = entity.m_count-1, i = 0; i < entity.m_count; j = i, i++) {

			if(type == CIRCLE) {
				v0 = m_vertices[0];
				v1 = entity.m_vertices[i];
				
				Vector2f.sub(v1, v0, edge);			
				edge.normalise(axis);
			
				if (seperatedByAxis(axis, entity, info))
					return (new CollisionInfo());
			}
			if(entity.type != CIRCLE) {
				v0 = entity.m_vertices[j];
				v1 = entity.m_vertices[i];
				
				
				Vector2f.sub(v1, v0, edge);			
				edge.normalise(axis);
				
				if (seperatedByAxis(axis, entity, info))
					return (new CollisionInfo());
			}
			
		}
		
		info.m_overlapped = true;
		return info;
	}
	
	public float[] calculateInterval(Vector2f axis) {
		float min = 0, max = 0;
		
		if(type == CIRCLE)
		{
			min = Vector2f.dot(m_vertices[0], axis) - radius;
			max = Vector2f.dot(m_vertices[0], axis) + radius;
		}
		
		else {
			min = max = Vector2f.dot(m_vertices[0], axis);
			
			for (int i = 1; i < m_count; i++) {
				float d = Vector2f.dot(m_vertices[i], axis);
				if (d < min)
					min = d;
				else if (d > max)
					max = d;
			}
		}
		
		float[] minmax = new float[2];
		minmax[0] = min;
		minmax[1] = max;
		
		return minmax;
	}
	
	public boolean intervalsSeperated(float mina, float maxa, float minb, float maxb) {
		return (mina > maxb) || (minb > maxa);
	}
	
	public boolean seperatedByAxis(Vector2f axis, Entity entity, CollisionInfo info) {
		float mina = 0, maxa = 0;
		float minb = 0, maxb = 0;
		
		float[] a = new float[2];
		float[] b = new float[2];
		
		a = calculateInterval(axis);
		b = entity.calculateInterval(axis);
		
		mina = a[0];
		maxa = a[1];
		minb = b[0];
		maxb = b[1];
		
		float d0 = (maxb - mina);
		float d1 = (minb - maxa);
		
		if(d0 < 0.0f || d1 > 0.0f) return true;
		
		float overlap = (d0 < -d1)? d0 : d1;
		
		float axis_length_squared = axis.lengthSquared();
		assert(axis_length_squared > 0.00001f);
		
		Vector2f sep = new Vector2f();
		sep.set(axis);
		sep.scale(overlap/axis_length_squared);
		
		float sep_length_squared = sep.lengthSquared();

		if(sep_length_squared < info.m_mtdLengthSquared || info.m_mtdLengthSquared < 0.0f)
		{
			info.m_mtdLengthSquared = sep_length_squared;
			info.m_mtd = sep;
		}
		
		return false;
	}
	// Debug paint
	public void paint()
	{
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glBegin(GL11.GL_LINE_LOOP);
			for(int i = 0; i < m_count; i++) {
				GL11.glVertex2f(m_vertices[i].x, m_vertices[i].y);
			}
		GL11.glEnd();
	}
}
