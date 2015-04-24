package main;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import main.points.Partition;
import main.points.PartitionRenderer;
import main.points.PartitionUpdater;
import main.points.PositionBuffer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main implements ApplicationListener {

  private final static float WORLD_SIZE = 512;
  private final Partition<Fly> partition = new Partition<>(8, WORLD_SIZE);
  private ShapeRenderer renderer;
  private OrthographicCamera camera;
  private BitmapFont font;
  private SpriteBatch batch;
  private final static int COUNTER_MAX = 15;
  private int counter = COUNTER_MAX;
  private final static int WORLD_BORDER = 16;
  private final PositionBuffer buffer = new PositionBuffer(1024);
  private final Collection<Fly> flies = new ArrayList<>();
  private final ExecutorService executor = Executors.newFixedThreadPool(4);

  @Override
  public void create() {
    renderer = new ShapeRenderer();
    batch = new SpriteBatch();
    font = new BitmapFont(Gdx.files.internal("data/hehe.fnt"));
    font.setColor(Color.DARK_GRAY);
    camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.position.add(WORLD_SIZE / 2, WORLD_SIZE / 2, 0);
    camera.update();
    for (int i = 0; i < buffer.allocationsArray().length; i++) {
      float x = WORLD_BORDER + rnd() * (WORLD_SIZE - WORLD_BORDER * 2);
      float y = WORLD_BORDER + rnd() * (WORLD_SIZE - WORLD_BORDER * 2);
      int pointer = buffer.allocate(x, y);
      Fly fly = new Fly(pointer);
      flies.add(fly);
    }
  }

  private static float rnd() {
    return (float) Math.random();
  }

  @Override
  public void render() {
    if (counter++ >= COUNTER_MAX) {
      PartitionUpdater.update(partition, flies, buffer);
      counter = 0;
    }
    executor.execute(new ParallelSimulation(partition, buffer, 0, 0, 4, 4));
    executor.execute(new ParallelSimulation(partition, buffer, 4, 0, 8, 4));
    executor.execute(new ParallelSimulation(partition, buffer, 4, 4, 8, 8));
    executor.execute(new ParallelSimulation(partition, buffer, 0, 4, 4, 8));
    Gdx.graphics.getGL20().glClearColor(.125f, .125f, .125f, 1f);
    Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    renderer.setProjectionMatrix(camera.combined);
    batch.setProjectionMatrix(camera.combined);
    PartitionRenderer.render(partition, renderer, font, batch);
    PointsRenderer.render(renderer, buffer);
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
