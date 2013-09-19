package co.joyatwork.grid.tests;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * DiffuseShader implements vertex & fragment shader.
 * It calculates diffuse color with ambient and directional lighting.
 * <p>
 * WARNING: only one directional light supported in this version!!!
 */
public class DiffuseShader extends BaseShader {

	private final static String vertexShader = 
			"attribute vec3 a_position;\n"+
			"attribute vec3 a_normal;\n"+
			"uniform mat4 u_projTrans;\n"+
			"uniform mat4 u_worldTrans;\n"+
			"uniform mat3 u_normalMatrix;\n"+
			"uniform vec3 u_color;\n"+
			"uniform vec3 u_dirLightColor;\n"+
			"uniform vec3 u_dirLightDirection;\n"+
			"varying vec3 v_diffuseColor;\n"+
			"varying vec3 v_diffuseLight;\n"+
			"void main() {\n"+
			"	vec3 normal;\n"+
			"	normal = normalize(u_normalMatrix * a_normal);\n"+
			"	float NdotL = clamp(dot(normal, -u_dirLightDirection), 0.0, 1.0);\n"+
			"	v_diffuseLight = u_dirLightColor * NdotL;\n"+
			"	v_diffuseColor = u_color;\n"+
			"	gl_Position = u_projTrans * u_worldTrans * vec4(a_position, 1.0);\n"+
			"}\n";
	
	/*
	 * v_diffuseColor - diffuse material color;
	 * v_ambientLight - ambient light color;
	 * v_diffuseLight - diffuse light color intensity = dot(normal, lightDierction) * directionLightColor; 
	 */
	private final static String fragmentShader = 
			"varying vec3 v_diffuseColor;\n"+
			"varying vec3 v_diffuseLight;\n"+
			"uniform vec3 u_ambientLight;\n"+
			"void main() {\n" +
			"	gl_FragColor.rgb = v_diffuseColor * (u_ambientLight + v_diffuseLight);\n" +
			"	gl_FragColor.a = 1.0;\n" +
			"}\n";
	
	//TODO why protected????
	protected final int u_projTrans	= register(new Uniform("u_projTrans"));
	protected final int u_worldTrans = register(new Uniform("u_worldTrans"));
	protected final int u_color = register(new Uniform("u_color"));
	protected final int u_normalMatrix = register(new Uniform("u_normalMatrix"));
	protected final int u_dirLightColor = register(new Uniform("u_dirLightColor"));
	protected final int u_dirLightDirection = register(new Uniform("u_dirLightDirection"));
	protected final int u_ambientLight = register(new Uniform("u_ambientLight"));
	
	protected final ShaderProgram program;
	private final Matrix3 normalMatrix = new Matrix3();
	
	public DiffuseShader () {
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
		set(u_color, 0f, 1f, 0f);
		context.setDepthTest(GL20.GL_LEQUAL);
        context.setCullFace(GL20.GL_BACK);
	}

	@Override
	public void render (Renderable renderable) {
		//the following setters are implemented in DefaultShader class
		set(u_worldTrans, renderable.worldTransform);
		set(u_normalMatrix, normalMatrix.set(renderable.worldTransform).inv().transpose());
		
		//TODO what if dir light is not set and/or more lights are set???
		if (renderable.lights == null || renderable.lights.directionalLights == null || (renderable.lights.directionalLights.size == 0)) {
			set(u_dirLightColor, 0f, 0f, 0f);
			set(u_dirLightDirection, 0f, 0f, 0f);
		}
		else {
			set(u_dirLightColor, 
				renderable.lights.directionalLights.get(0).color.r, 
				renderable.lights.directionalLights.get(0).color.g, 
				renderable.lights.directionalLights.get(0).color.b);
			set(u_dirLightDirection, renderable.lights.directionalLights.get(0).direction);
		}
		if (renderable.lights == null || renderable.lights.ambientLight == null) {
			set(u_ambientLight, 0f, 0f, 0f);
		}
		else {
			set(u_ambientLight, 
				renderable.lights.ambientLight.r,
				renderable.lights.ambientLight.g,
				renderable.lights.ambientLight.b);
		}
			
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
