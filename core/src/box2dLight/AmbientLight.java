package box2dLight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.Mesh.VertexDataType;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import box2dLight.Light;

public class AmbientLight extends Light {

	public AmbientLight(RayHandler rayHandler, int rays, Color color,
			float distance, float directionDegree) {
		super(rayHandler, rays, color, distance, directionDegree);
		
		lightMesh = new Mesh(
				VertexDataType.VertexArray, staticLight, vertexNum, 0,
				new VertexAttribute(Usage.Position, 2, "vertex_positions"),
				new VertexAttribute(Usage.ColorPacked, 4, "quad_colors"),
				new VertexAttribute(Usage.Generic, 1, "s"));
		
		softShadowMesh = new Mesh(
				VertexDataType.VertexArray, staticLight, vertexNum, 0,
				new VertexAttribute(Usage.Position, 2, "vertex_positions"),
				new VertexAttribute(Usage.ColorPacked, 4, "quad_colors"),
				new VertexAttribute(Usage.Generic, 1, "s"));
		
		update();
	}

	@Override
	void update() {
		
//		for (int i = 0; i < arraySize; i++) {
//			segments[size++] = start[i].x;
//			segments[size++] = start[i].y;
//			segments[size++] = colorF;
//			segments[size++] = 1f;
//			segments[size++] = mx[i];
//			segments[size++] = my[i];
//			segments[size++] = colorF;
//			segments[size++] = 1f;
//		}
		
		int size = 0;
		float width = 72;
		float height = 48;
		segments = new float[4 * 4];
		segments[size++] = -width/2f;
		segments[size++] = -height/2f;
		segments[size++] = colorF;
		segments[size++] = 1f;
		
		segments[size++] = -width/2f;
		segments[size++] = height/2f;
		segments[size++] = colorF;
		segments[size++] = 1f;
		
		segments[size++] = width/2f;
		segments[size++] = -height/2f;
		segments[size++] = colorF;
		segments[size++] = 1f;
		
		segments[size++] = width/2f;
		segments[size++] = height/2f;
		segments[size++] = colorF;
		segments[size++] = 1f;
		
		lightMesh.setVertices(segments, 0, size);
		
		vertexNum = size / 4;
	}

	@Override
	void render() {
		lightMesh.render(
				rayHandler.lightShader, GL20.GL_TRIANGLE_STRIP, 0, vertexNum);
	}

	@Override
	public void setDistance(float dist) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDirection(float directionDegree) {
		// TODO Auto-generated method stub

	}

	@Override
	public void attachToBody(Body body) {
		// TODO Auto-generated method stub

	}

	@Override
	public Body getBody() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPosition(float x, float y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPosition(Vector2 position) {
		// TODO Auto-generated method stub

	}

	@Override
	public float getX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getY() {
		// TODO Auto-generated method stub
		return 0;
	}

}
