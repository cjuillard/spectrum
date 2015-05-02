package com.runamuck.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.Mesh.VertexDataType;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class DeformablePlane implements IRenderable {

	static final int VERTEX_SIZE = 2 + 1 + 2;
	
	private Mesh mesh;
	private Texture texture;
	private float[] verts;
	
	private int numQuads;
	private int meshRows;
	private int meshCols;
	private int numVerts;

	public DeformablePlane(Texture texture, float x, float y, float width, float height) {
		this.texture = texture;

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
	}
	
	@Override
	public void update(float elapsed) {
		
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
