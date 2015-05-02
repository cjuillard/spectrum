package com.runamuck.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.Mesh.VertexDataType;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.NumberUtils;

public class DeformablePlane implements IRenderable {

	static final int VERTEX_SIZE = 2 + 1 + 2;
	
	private Mesh mesh;
	private Texture texture;
	private float[] verts;
	
	private int numQuads;
	private int quadRows;	// quad rows
	private int quadCols;	// quad columns
	private int vertRows;
	private int vertCols;
	private int numVerts;

	private Rectangle bounds;

	private MeshInfo meshInfo;

	private class MeshInfo {
		VertexInfo[][] verts;
		
		public MeshInfo() {
			verts = new VertexInfo[vertRows][vertCols];
			for(int row = 0; row < vertRows; row++) {
				for(int col = 0; col < vertCols; col++) {
					verts[row][col] = new VertexInfo(row, col);
				}
			}
		}
	}
	
	private class VertexInfo {
		private int row;
		private int col;
		public Vector2 vel = new Vector2();
		public final float originalX;
		public final float originalY;
		public final Color originalC;
		public final float originalU;
		public final float originalV;
		
		private static final int X_OFF = 0;
		private static final int Y_OFF = 1;
		private static final int C_OFF = 2;
		private static final int U_OFF = 3;
		private static final int V_OFF = 4;
		
		public VertexInfo(int row, int col) {
			this.row = row;
			this.col = col;
			
			this.originalX = getX();
			this.originalY = getY();
			this.originalC = new Color(NumberUtils.floatToIntColor(getColor()));
			this.originalU = getU();
			this.originalV = getV();
		}
		
		public void setX(float x) {
			verts[(row * (quadCols+1) + col) * VERTEX_SIZE + X_OFF] = x;
		}
		
		public float getX() {
			return verts[(row * (quadCols+1) + col) * VERTEX_SIZE + X_OFF];
		}
		
		public void setY(float y) {
			verts[(row * (quadCols+1) + col) * VERTEX_SIZE + Y_OFF] = y;
		}
		
		public float getY() {
			return verts[(row * (quadCols+1) + col) * VERTEX_SIZE + Y_OFF];
		}
		
		public void setColor(float colorBits) {
			verts[(row * (quadCols+1) + col) * VERTEX_SIZE + C_OFF] = colorBits;
		}
		
		public float getColor() {
			return verts[(row * (quadCols+1) + col) * VERTEX_SIZE + C_OFF];
		}
		
		public void setU(float u) {
			verts[(row * (quadCols+1) + col) * VERTEX_SIZE + U_OFF] = u;
		}
		
		public float getU() {
			return verts[(row * (quadCols+1) + col) * VERTEX_SIZE + U_OFF];
		}
		
		public void setV(float v) {
			verts[(row * (quadCols+1) + col) * VERTEX_SIZE + V_OFF] = v;
		}
		
		public float getV() {
			return verts[(row * (quadCols+1) + col) * VERTEX_SIZE + V_OFF];
		}

		public void update(float elapsed) {
			setX(getX() + vel.x * elapsed);
			setY(getY() + vel.y * elapsed);
			
			float currSpeed = vel.len();
			vel.nor().scl(currSpeed - (.95f * elapsed * currSpeed));
		}
	}
	
	public DeformablePlane(Texture texture, float x, float y, float width, float height) {
		this.texture = texture;
		
		this.bounds = new Rectangle(x, y, width, height);
		
		this.quadCols = 50;
		this.quadRows = 50;
		this.vertCols = quadCols + 1;
		this.vertRows = quadRows + 1;
		this.numQuads = quadCols * quadRows;
		this.numVerts = vertCols * vertRows;
		
		mesh = new Mesh(VertexDataType.VertexArray, false, numVerts, numQuads * 6, new VertexAttribute(Usage.Position, 2,
				ShaderProgram.POSITION_ATTRIBUTE), new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
				new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));
		
		float repeatXDist = width / quadCols;
		float repeatYDist = height / quadRows;
		
		this.verts = new float[numVerts * VERTEX_SIZE];
		int indx = 0;
		for(int row = 0; row < quadRows+1; row++) {
			for(int col = 0; col < quadCols+1; col++) {
				verts[indx++] = x + col * (width / (quadCols));
				verts[indx++] = y + row * (height / (quadRows));
				
//				verts[indx++] = new Color(row / (float)meshRows, 0, 0, .2f).toFloatBits();
				verts[indx++] = new Color(1, 1, 1, .2f).toFloatBits();
				
//				verts[indx++] = (float)col / meshCols;
//				verts[indx++] = (float)row / meshRows;
				verts[indx++] = (x + col * (width / (quadCols))) / repeatXDist;
				verts[indx++] = (y + row * (height / (quadRows))) / repeatYDist;
				
				float val = (x + col * (width / (quadCols))) / repeatXDist;
			}
		}
		mesh.setVertices(verts);
		
		int len = numQuads * 6;
		short[] indices = new short[len];
		indx = 0;
		for(int row = 0; row < quadRows; row++) {
			for(int col = 0; col < quadCols; col++) {
				indices[indx++] = (short)(row * (quadCols+1) + col);
				indices[indx++] = (short)(row * (quadCols+1) + 1 + col);
				indices[indx++] = (short)((row+1) * (quadCols+1) + col);
				
				indices[indx++] = (short)(row * (quadCols+1) + 1 + col);
				indices[indx++] = (short)((row+1) * (quadCols+1) + 1 + col);
				indices[indx++] = (short)((row+1) * (quadCols+1) + col);
			}
		}

		mesh.setIndices(indices);
		
		this.meshInfo = new MeshInfo();
	}
	
	private Vector2 tmpVec2 = new Vector2();
	private float totalTimeElapsed;
	@Override
	public void update(float elapsed) {
		totalTimeElapsed += elapsed;
//		VertexInfo info = meshInfo.verts[0][10];
////		info.setX(info.originalX + MathUtils.sinDeg(totalTimeElapsed * 90) * bounds.width / 10f);
//
//		info.setU(info.originalU + MathUtils.sinDeg(totalTimeElapsed * 90));
//		mesh.setVertices(verts);
		
		// Elasticity
		final float elasticity = 10f;
//		VertexInfo info = meshInfo.verts[0][0];
////		info.setX(info.getX() + 1f);
////		info.setY(info.getY() + 1f);
//		tmpVec2.set(info.originalX, info.originalY).sub(info.getX(), info.getY());
//		
//		final float dampeningLen2 = 250f;
//		float distAway = tmpVec2.len2();
//		float dampeningMult = Math.min(dampeningLen2, distAway) / dampeningLen2;
//		
//		tmpVec2.nor().scl(elasticity * dampeningMult);
//		info.vel.add(tmpVec2);
//		
//		info.update(elapsed);
		
		for(int row = 0; row < vertRows; row++) {
			for(int col = 0; col < vertCols; col++) {
				VertexInfo info = meshInfo.verts[row][col];
//				info.vel.x = .1f;
				tmpVec2.set(info.originalX, info.originalY).sub(info.getX(), info.getY());
				tmpVec2.nor().scl(elasticity * elapsed);
				info.vel.add(tmpVec2);
				
				info.update(elapsed);
			}
		}
	}
	
	public void applyForce(float x, float y, float strength) {
		final float dampeningLen = bounds.width / 5f;
		for(int row = 0; row < vertRows; row++) {
			for(int col = 0; col < vertCols; col++) {
				VertexInfo info = meshInfo.verts[row][col];
				tmpVec2.set(info.getX(), info.getY()).sub(x, y);
				float len = tmpVec2.len();
				
				float dampeningMult = MathUtils.clamp((dampeningLen - len) / dampeningLen, 0, 1);
				tmpVec2.nor().scl(dampeningMult * strength);
				info.vel.add(tmpVec2);
			}
		}
	}

	@Override
	public void render(RenderContext renderContext) {
//		ShaderProgram shader = renderContext.getUnshadedShader();
//		shader.begin();
//		
//		texture.bind(0);
//		mesh.setVertices(verts, 0, verts.length);
//		mesh.render(shader, GL20.GL_TRIANGLES);
//		
//		shader.end();
	}

	@Override
	public void renderAboveFog(RenderContext renderContext) {
		ShaderProgram shader = renderContext.getUnshadedShader();
//		Gdx.gl.glDepthMask(false);
//		private int blendSrcFunc = GL20.GL_SRC_ALPHA;
//		private int blendDstFunc = GL20.GL_ONE_MINUS_SRC_ALPHA;
		Gdx.gl.glEnable(GL20.GL_BLEND);
//		if (blendSrcFunc != -1) Gdx.gl.glBlendFunc(blendSrcFunc, blendDstFunc);
		
		shader.begin();
		
		texture.bind(0);
		shader.setUniformMatrix("u_projTrans", renderContext.getCamera().combined);
		shader.setUniformi("u_texture", 0);
		
		mesh.setVertices(verts, 0, verts.length);
		mesh.render(shader, GL20.GL_TRIANGLES);
		
		shader.end();
	}

}
