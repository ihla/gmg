package co.joyatwork.grid.tests;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.lights.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;
import com.badlogic.gdx.utils.JsonReader;


public class DiffuseTextureShaderTest implements ApplicationListener {
    private PerspectiveCamera cam;
    private CameraInputController camController;
    private Shader shader;
    private RenderContext renderContext;
    private Model model;
    private Renderable renderable;
	private Lights lights;

    @Override
    public void create () {
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(3f, 3f, 3f);
        cam.lookAt(0,0,0);
        cam.near = 0.1f;
        cam.far = 300f;
        cam.update();
         
        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(camController);
        
        ModelLoader modelLoader = new G3dModelLoader(new JsonReader());
        ModelData modelData = modelLoader.loadModelData(Gdx.files.internal("data/texture_test_object.g3dj"));
        model = new Model(modelData, new TextureProvider.FileTextureProvider());
        Gdx.app.log("DifuseTextureShader", "model.meshes.size " + model.meshes.size);
        Gdx.app.log("DifuseTextureShader", "model.meshParts.size " + model.meshParts.size);
        Gdx.app.log("DifuseTextureShader", "model.materials.size " + model.materials.size);
        Gdx.app.log("DifuseTextureShader", "model.nodes.size " + model.nodes.size);
        Gdx.app.log("DifuseTextureShader", "model.nodes.get(0).children.size " + model.nodes.get(0).children.size);
        Gdx.app.log("DifuseTextureShader", "model.nodes.get(0).children.get(0).parts.size " + model.nodes.get(0).children.get(0).parts.size);
        Gdx.app.log("DifuseTextureShader", "model.nodes.get(0).parts.size " + model.nodes.get(0).parts.size);

        lights = new Lights();
        lights.ambientLight.set(0.2f, 0.2f, 0.2f, 1f);
        lights.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        //NodePart blockPart = model.nodes.get(0).parts.get(0);
        NodePart blockPart = model.nodes.get(0).children.get(0).parts.get(0);
          
        renderable = new Renderable();
        blockPart.setRenderable(renderable);
        renderable.lights = lights;
        renderable.worldTransform.idt();
          
        renderContext = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.WEIGHTED, 1));
        shader = new DiffuseTextureShader();
        shader.init();
    }
     
    @Override
    public void render () {
        camController.update();
         
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
 
        renderContext.begin();
        shader.begin(cam, renderContext);
        shader.render(renderable);
        shader.end();
        renderContext.end();
    }
     
    @Override
    public void dispose () {
        shader.dispose();
        model.dispose();
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
