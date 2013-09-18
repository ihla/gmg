package co.joyatwork.grid.tests;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class CustomShader extends BaseShader {

	private final static String vertexShader = 
			"attribute vec3 a_position;\n"+
			"attribute vec3 a_normal;\n"+
			"uniform mat4 u_projTrans;\n"+
			"uniform mat4 u_worldTrans;\n"+
			"uniform mat3 u_normalMatrix;\n"+
			"uniform vec3 u_color;\n"+
			"varying vec3 v_normal;\n"+
			"varying vec3 v_diffuseColor;\n"+
			"varying vec3 v_ambientLight;\n"+
			"varying vec3 v_diffuseLight;\n"+
			"void main() {\n"+
			"	v_normal = normalize(u_normalMatrix * a_normal);\n"+
			"	v_diffuseColor = u_color;\n"+
			"	v_ambientLight = vec3(0.5, 0.5, 0.5);\n"+
			"	v_diffuseLight = vec3(0.5, 0.5, 0.5);\n"+
			"	gl_Position = u_projTrans * u_worldTrans * vec4(a_position, 1.0);\n"+
			"}\n";
	private final static String fragmentShader = 
			"varying vec3 v_diffuseColor;\n"+
			"varying vec3 v_ambientLight;\n"+
			"varying vec3 v_diffuseLight;\n"+
			"void main() {\n" +
			"	gl_FragColor.rgb = v_diffuseColor * (v_ambientLight + v_diffuseLight);\n" +
			"	gl_FragColor.a = 1.0;\n" +
			"}\n";
	
	protected final int u_projTrans	= register(new Uniform("u_projTrans"));
	protected final int u_worldTrans = register(new Uniform("u_worldTrans"));
	protected final int u_color = register(new Uniform("u_color"));
	protected final ShaderProgram program;
	
	public CustomShader () {
		super();
		program = new ShaderProgram(vertexShader, fragmentShader);
		if (!program.isCompiled())
			throw new GdxRuntimeException("Couldn't compile shader " + program.getLog());
	}
	
	@Override
	public void init() {
		super.init(program, null);
	}

	@Override
	public void begin (Camera camera, RenderContext context) {
		program.begin();
		set(u_projTrans, camera.combined);
		set(u_color, 0.2f, 0.2f, 0.2f);
	}

	@Override
	public void render (Renderable renderable) {
		set(u_worldTrans, renderable.worldTransform);
		renderable.mesh.render(program, renderable.primitiveType, renderable.meshPartOffset, renderable.meshPartSize);
	}

	@Override
	public void end () {
		program.end();
	}
	
	@Override
	public void dispose () {
		super.dispose();
		program.dispose();
	}

	@Override
	public int compareTo(Shader other) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canRender(Renderable instance) {
		return true;
	}

}
