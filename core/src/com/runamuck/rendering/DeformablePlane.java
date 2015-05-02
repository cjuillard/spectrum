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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.NumberUtils;

public class DeformablePlane implements IRenderable {

	static final int VERTEX_SIZE = 2 + 1 + 2;
	
	private Mesh mesh;
	private Texture texture;
	private float[] verts;
	
	private int numQuads;
	private int meshRows;	// quad rows
	private int meshCols;	// quad columns
	private int numVerts;

	private Rectangle bounds;

	private MeshInfo meshInfo;

	private class MeshInfo {
		VertexInfo[][] verts;
		
		public MeshInfo() {
			verts = new VertexInfo[meshRows][meshCols];
			for(int row = 0; row < meshRows; row++) {
				for(int col = 0; col < meshCols; col++) {
					verts[row][col] = new VertexInfo(row, col);
				}
			}
		}
	}
	
	private class VertexInfo {
		private int row;
		private int col;
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
			verts[(row * (meshCols+1) + col + X_OFF) * VERTEX_SIZE] = x;
		}
		
		public float getX() {
			return verts[(row * (meshCols+1) + col + X_OFF) * VERTEX_SIZE];
		}
		
		public void setY(float y) {
			verts[(row * (meshCols+1) + col + Y_OFF) * VERTEX_SIZE] = y;
		}
		
		public float getY() {
			return verts[(row * (meshCols+1) + col + Y_OFF) * VERTEX_SIZE];
		}
		
		public void setColor(float colorBits) {
			verts[(row * (meshCols+1) + col + C_OFF) * VERTEX_SIZE] = colorBits;
		}
		
		public float getColor() {
			return verts[(row * (meshCols+1) + col + C_OFF) * VERTEX_SIZE];
		}
		
		public void setU(float u) {
			verts[(row * (meshCols+1) + col + U_OFF) * VERTEX_SIZE] = u;
		}
		
		public float getU() {
			return verts[(row * (meshCols+1) + col + U_OFF) * VERTEX_SIZE];
		}
		
		public void setV(float v) {
			verts[(row * (meshCols+1) + col + V_OFF) * VERTEX_SIZE] = v;
		}
		
		public float getV() {
			return verts[(row * (meshCols+1) + col + V_OFF) * VERTEX_SIZE];
		}
	}
	
	public DeformablePlane(Texture texture, float x, float y, float width, float height) {
		this.texture = texture;
		
		this.bounds = new Rectangle(x, y, width, height);
		
		this.meshCols = 50;
		this.meshRows = 50;
		this.numQuads = meshCols * meshRows;
		this.numVerts = (meshCols+1) * (meshRows+1);
		
		mesh = new Mesh(VertexDataType.VertexArray, false, numVerts, numQuads * 6, new VertexAttribute(Usage.Position, 2,
				ShaderProgram.POSITION_ATTRIBUTE), new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
				new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));
		
		float repeatXDist = width / meshCols;
		float repeatYDist = height / meshRows;
		
		this.verts = new float[numVerts * VERTEX_SIZE];
		int indx = 0;
		for(int row = 0; row < meshRows+1; row++) {
			for(int col = 0; col < meshCols+1; col++) {
				verts[indx++] = x + col * (width / (meshCols));
				verts[indx++] = y + row * (height / (meshRows));
				
//				verts[indx++] = new Color(row / (float)meshRows, 0, 0, .2f).toFloatBits();
				verts[indx++] = new Color(1, 1, 1, .2f).toFloatBits();
				
//				verts[indx++] = (float)col / meshCols;
//				verts[indx++] = (float)row / meshRows;
				verts[indx++] = (x + col * (width / (meshCols))) / repeatXDist;
				verts[indx++] = (y + row * (height / (meshRows))) / repeatYDist;
				
				float val = (x + col * (width / (meshCols))) / repeatXDist;
				System.out.println(val);
			}
		}
		mesh.setVertices(verts);
		
		int len = numQuads * 6;
		short[] indices = new short[len];
		indx = 0;
		for(int row = 0; row < meshRows; row++) {
			for(int col = 0; col < meshCols; col++) {
				indices[indx++] = (short)(row * (meshCols+1) + col);
				indices[indx++] = (short)(row * (meshCols+1) + 1 + col);
				indices[indx++] = (short)((row+1) * (meshCols+1) + col);
				
				indices[indx++] = (short)(row * (meshCols+1) + 1 + col);
				indices[indx++] = (short)((row+1) * (meshCols+1) + 1 + col);
				indices[indx++] = (short)((row+1) * (meshCols+1) + col);
			}
		}

		mesh.setIndices(indices);
		
		this.meshInfo = new MeshInfo();
	}
	
	private float totalTimeElapsed;
	@Override
	public void update(float elapsed) {
		totalTimeElapsed += elapsed;
//		VertexInfo info = meshInfo.verts[0][10];
////		info.setX(info.originalX + MathUtils.sinDeg(totalTimeElapsed * 90) * bounds.width / 10f);
//
//		info.setU(info.originalU + MathUtils.sinDeg(totalTimeElapsed * 90));
//		mesh.setVertices(verts);
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
