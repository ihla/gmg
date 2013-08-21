package co.joyatwork.grid;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.lights.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.materials.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;
import com.badlogic.gdx.utils.JsonReader;

public class GridGame implements ApplicationListener {
	private PerspectiveCamera cam;
	private Model model;
	private ModelInstance instance;
	private ModelBatch modelBatch;
	private Lights lights;
	private CameraInputController camController;
	
    private final String vert =
	"attribute vec3 a_position;" +
	"attribute vec3 a_normal;" +
	"attribute vec2 a_texCoord0;" +
	"uniform mat4 u_worldTrans;" +
	"uniform mat4 u_projTrans;" +
	"varying vec2 v_texCoord0;" +
	"void main() {" +
	"    v_texCoord0 = a_texCoord0;" +
	"    gl_Position = u_projTrans * u_worldTrans * vec4(a_position, 1.0);" +
	"}";
    
    String frag =
	"precision mediump float;" +
	"varying vec2 v_texCoord0;" +
	"void main() {" +
	"    gl_FragColor = vec4(v_texCoord0, 0.0, 1.0);" +
	"}";
	private Renderable renderable;
	private RenderContext renderContext;
	private DefaultShader shader;

	
	@Override
	public void create() {		
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		// looking through the top-right corner of a cube into its center
        cam.position.set(3f, 3f, 3f);
        cam.lookAt(0,0,0); // [0,0,0] is center of world
        //<<
        cam.near = 1f; //  distance to near clipping plane
        cam.far = 100f; // distance to far clipping plane
        cam.update();
        /**/
        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createBox(1f, 1f, 1f, 
            new Material(ColorAttribute.createDiffuse(Color.GREEN)),
            Usage.Position | Usage.Normal);
        
        /*
        ModelLoader modelLoader = new G3dModelLoader(new JsonReader());
        ModelData modelData = modelLoader.loadModelData(Gdx.files.internal("data/texture_test_object.g3dj"));
        model = new Model(modelData, new TextureProvider.FileTextureProvider());
        */
        lights = new Lights();
        lights.ambientLight.set(0.4f, 0.4f, 0.4f, 1f);
        lights.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        lights.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 1f, 0.8f, 0.2f));

        /*
        instance = new ModelInstance(model, 0f, 0f, 0f);
        modelBatch = new ModelBatch(new DefaultShaderProvider());
        */
        NodePart blockPart = model.nodes.get(0).parts.get(0);
        
        renderable = new Renderable();
        blockPart.setRenderable(renderable);
        renderable.lights = lights;
        renderable.worldTransform.idt();
          
        renderContext = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.WEIGHTED, 1));
        shader = new DefaultShader(vert, frag, renderable, true, false, 2, 0, 0, 0);
        shader.init();

        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(camController);

	}

	@Override
	public void dispose() {
		modelBatch.dispose();
		model.dispose();
        shader.dispose();

	}

	@Override
	public void render() {		
		camController.update();

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        /*
        modelBatch.begin(cam);
        modelBatch.render(instance, lights);
        modelBatch.end();
        */
        renderContext.begin();
        shader.begin(cam, renderContext);
        shader.render(renderable);
        shader.end();
        renderContext.end();

	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
