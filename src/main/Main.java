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

public class Main implements ApplicationListener {

  private final static float GRAVITATIONAL_CONST = 6.67384e-11f;
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
    updateCell(partition, buffer, 1, 1);
    Gdx.graphics.getGL20().glClearColor(.125f, .125f, .125f, 1f);
    Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    renderer.setProjectionMatrix(camera.combined);
    batch.setProjectionMatrix(camera.combined);
    PartitionRenderer.render(partition, renderer, font, batch);
    PointsRenderer.render(renderer, buffer);
  }

  private static void updateCell(Partition<Fly> partition, PositionBuffer buffer, int x, int y) {
    Collection<Fly> files = partition.get(x, y);
    for (Fly fly : files)
      update(partition, fly, buffer);
  }

  private static void update(Partition<Fly> partition, Fly fly, PositionBuffer buffer) {
    int pivot = fly.positionPivot;
    performCage(pivot, buffer);
    float x = buffer.getX(pivot);
    float y = buffer.getY(pivot);
    int cellX = partition.positionFor(x);
    int cellY = partition.positionFor(y);
    int dist = 1;
    for (int ix = -dist; ix <= dist; ix++)
      for (int iy = -dist; iy <= dist; iy++)
        computeFor(fly, partition.get(cellX + ix, cellY + iy), buffer);
    buffer.updatePosition(pivot, x + fly.move.x, y + fly.move.y);
  }

  private static void performCage(int pivot, PositionBuffer buffer) {
    float x = buffer.getX(pivot);
    float y = buffer.getY(pivot);
    float max = WORLD_SIZE - WORLD_BORDER;
    float min = WORLD_BORDER;
    if (x > max)
      buffer.updateX(pivot, min);
    if (x < min)
      buffer.updateX(pivot, max);
    if (y > max)
      buffer.updateY(pivot, min);
    if (y < min)
      buffer.updateY(pivot, max);
  }

  private static void computeFor(Fly fly, Collection<Fly> othersFlies, PositionBuffer buffer) {
    if (othersFlies == null)
      return;
    for (Fly otherFly : othersFlies) {
      int pivot = fly.positionPivot;
      int otherPivot = otherFly.positionPivot;
      if (otherPivot == pivot)
        continue;
      float x = buffer.getX(pivot);
      float y = buffer.getY(pivot);
      float otherX = buffer.getX(otherPivot);
      float otherY = buffer.getY(otherPivot);
      float dy = otherY - y;
      float dx = otherX - x;
      float distanceSquared = (float) Math.sqrt(dx * dx + dy * dy);
      float force =  GRAVITATIONAL_CONST / distanceSquared * 100000000f;
      float vx = dx / distanceSquared * force;
      float vy = dy / distanceSquared * force;
      fly.move.add(vx, vy);
    }
    fly.move.scl(.9975f);
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
