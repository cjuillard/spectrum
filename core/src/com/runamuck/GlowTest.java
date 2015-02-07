package com.runamuck;

import shaders.Gaussian;
import shaders.WithoutShadowShader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.net.SocketHints;

public class GlowTest {
	FrameBuffer frameBuffer;
	private FrameBuffer pingPongBuffer;
	private Mesh lightMapMesh;
	private ShaderProgram blurShader;
	private int blurPasses = 3;
	private ShaderProgram basicShader;
	
	public GlowTest(int fboWidth, int fboHeight) {

		if (fboWidth <= 0)
			fboWidth = 1;
		if (fboHeight <= 0)
			fboHeight = 1;
		frameBuffer = new FrameBuffer(Format.RGBA8888, fboWidth,
				fboHeight, false);
		pingPongBuffer = new FrameBuffer(Format.RGBA8888, fboWidth,
				fboHeight, false);

		lightMapMesh = createLightMapMesh();
		
		SocketHints hints = new SocketHints();
//		hints.
//		Gdx.net.newClientSocket(Protocol.TCP, "host", 1010, );

		blurShader = Gaussian.createBlurShader(fboWidth, fboHeight);
		basicShader = createBasicShader();
	}
	
	public void gaussianBlur() {
		Gdx.gl20.glDisable(GL20.GL_BLEND);
		for (int i = 0; i < blurPasses; i++) {
			frameBuffer.getColorBufferTexture().bind(0);
			// horizontal
			pingPongBuffer.begin();
			{
				blurShader.begin();
		//		blurShader.setUniformi("u_texture", 0);
				blurShader.setUniformf("dir", 1f, 0f);
				lightMapMesh.render(blurShader, GL20.GL_TRIANGLE_FAN, 0, 4);
				blurShader.end();
			}
			pingPongBuffer.end();

			pingPongBuffer.getColorBufferTexture().bind(0);
			// vertical
			frameBuffer.begin();
			{
				blurShader.begin();
			//	blurShader.setUniformi("u_texture", 0);
				blurShader.setUniformf("dir", 0f, 1f);
				lightMapMesh.render(blurShader, GL20.GL_TRIANGLE_FAN, 0, 4);
				blurShader.end();

			}
			frameBuffer.end();
		}

		Gdx.gl20.glEnable(GL20.GL_BLEND);
	}
	
	private Mesh createLightMapMesh() {
		float[] verts = new float[VERT_SIZE];
		// vertex coord
		verts[X1] = -1;
		verts[Y1] = -1;

		verts[X2] = 1;
		verts[Y2] = -1;

		verts[X3] = 1;
		verts[Y3] = 1;

		verts[X4] = -1;
		verts[Y4] = 1;

		// tex coords
		verts[U1] = 0f;
		verts[V1] = 0f;

		verts[U2] = 1f;
		verts[V2] = 0f;

		verts[U3] = 1f;
		verts[V3] = 1f;

		verts[U4] = 0f;
		verts[V4] = 1f;

		Mesh tmpMesh = new Mesh(true, 4, 0, new VertexAttribute(
				Usage.Position, 2, "a_position"), new VertexAttribute(
				Usage.TextureCoordinates, 2, "a_texCoord"));

		tmpMesh.setVertices(verts);
		return tmpMesh;

	}

	static public final int VERT_SIZE = 16;
	static public final int X1 = 0;
	static public final int Y1 = 1;
	static public final int U1 = 2;
	static public final int V1 = 3;
	static public final int X2 = 4;
	static public final int Y2 = 5;
	static public final int U2 = 6;
	static public final int V2 = 7;
	static public final int X3 = 8;
	static public final int Y3 = 9;
	static public final int U3 = 10;
	static public final int V3 = 11;
	static public final int X4 = 12;
	static public final int Y4 = 13;
	static public final int U4 = 14;
	static public final int V4 = 15;

	static final public ShaderProgram createBasicShader() {
		final String vertexShader = "attribute vec4 a_position;\n" //
				+ "attribute vec2 a_texCoord;\n" //
				+ "varying vec2 v_texCoords;\n" //
				+ "\n" //
				+ "void main()\n" //
				+ "{\n" //
				+ "   v_texCoords = a_texCoord;\n" //
				+ "   gl_Position = a_position;\n" //
				+ "}\n";
		
		final String fragmentShader = "#ifdef GL_ES\n" //
			+ "precision lowp float;\n" //
			+ "#define MED mediump\n"
			+ "#else\n"
			+ "#define MED \n"
			+ "#endif\n" //
				+ "varying MED vec2 v_texCoords;\n" //
				+ "uniform sampler2D u_texture;\n" //
				+ "void main()\n"//
				+ "{\n" //
				+ "gl_FragColor = vec4(texture2D(u_texture, v_texCoords).rgb, 1.0);\n"				
				+ "}\n";
		ShaderProgram.pedantic = false;
		ShaderProgram woShadowShader = new ShaderProgram(vertexShader,
				fragmentShader);
		if (woShadowShader.isCompiled() == false) {
			Gdx.app.log("ERROR", woShadowShader.getLog());

		}

		return woShadowShader;
	}
	
	public void render() {		
		gaussianBlur();
		
//		Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
//		Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		frameBuffer.getColorBufferTexture().bind(0);
		
		basicShader.begin();
//			withoutShadowShader.setUniformi("u_texture", 0);
		lightMapMesh.render(basicShader, GL20.GL_TRIANGLE_FAN);
		basicShader.end();
	}
}