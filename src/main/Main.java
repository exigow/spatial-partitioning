package main;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import main.points.Partition;
import main.points.PartitionUpdater;
import main.points.PositionBuffer;
import main.renderers.PartitionRenderer;
import main.renderers.PositionsRenderer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main implements ApplicationListener {

  private final static int CHUNKS = 8;
  private final static float WORLD_SIZE = 512;
  private final Partition<Fly> partition = new Partition<>(CHUNKS, WORLD_SIZE);
  private ShapeRenderer renderer;
  private OrthographicCamera camera;
  private BitmapFont font;
  private SpriteBatch batch;
  private final static int COUNTER_MAX = 15;
  private int counter = COUNTER_MAX;
  private final static int WORLD_BORDER = 16;
  private final PositionBuffer buffer = new PositionBuffer(1024);
  private final Collection<Fly> flies = new ArrayList<>();
  private final ExecutorService executor = Executors.newFixedThreadPool(1);
  private PositionsRenderer positionsRenderer;

  @Override
  public void create() {
    positionsRenderer = new PositionsRenderer(buffer, loadPointsShader());
    renderer = new ShapeRenderer();
    batch = new SpriteBatch();
    font = new BitmapFont(Gdx.files.internal("data/comfortaa.fnt"));
    camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.position.add(WORLD_SIZE / 2, WORLD_SIZE / 2, 0);
    camera.update();
    for (int i = 0; i < buffer.allocationsArray().length; i++) {
      float x = randomWorldPoint();
      float y = randomWorldPoint();
      int pointer = buffer.allocate(x, y);
      Fly fly = new Fly(pointer);
      flies.add(fly);
    }
  }

  private static float randomWorldPoint() {
    return WORLD_BORDER + rnd() * (WORLD_SIZE - WORLD_BORDER * 2);
  }

  private static float rnd() {
    return (float) Math.random();
  }

  @Override
  public void render() {
    update();
    clearBackground();
    renderer.setProjectionMatrix(camera.combined);
    batch.setProjectionMatrix(camera.combined);
    PartitionRenderer.render(partition, renderer, font, batch);
    positionsRenderer.render(camera.combined);
    renderFps(batch, font);
  }

  private static ShaderProgram loadPointsShader() {
    String vertexShader = Gdx.files.internal("data/points.vert").readString();
    String fragmentShader = Gdx.files.internal("data/points.frag").readString();
    return new ShaderProgram(vertexShader, fragmentShader);
  }

  private void update() {
    if (counter++ >= COUNTER_MAX) {
      PartitionUpdater.update(partition, flies, buffer);
      counter = 0;
    }
    Collection<ParallelSimulation> simulations = Collections.singleton(new ParallelSimulation(partition, buffer, partition.asWorldWideChunk()));
    try {
      executor.invokeAll(simulations);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static void clearBackground() {
    Gdx.gl20.glClearColor(.125f, .125f, .125f, 1f);
    Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
  }

  private static void renderFps(SpriteBatch batch, BitmapFont font) {
    int fps = Gdx.graphics.getFramesPerSecond();
    batch.begin();
    font.draw(batch, "FPS = " + fps, 0, 512 + 32);
    batch.end();
  }

  @Override
  public void resize(int i, int i2) {
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void dispose() {
  }

}
