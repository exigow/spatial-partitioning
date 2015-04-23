package main;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import main.points.Partition;
import main.points.PartitionRenderer;
import main.points.PartitionUpdater;
import main.points.PositionBuffer;

import java.util.ArrayList;
import java.util.Collection;

public class Main implements ApplicationListener {

  private static final float WORLD_SIZE = 512;
  private final Partition partition = new Partition(16, WORLD_SIZE);
  private ShapeRenderer renderer;
  private OrthographicCamera camera;
  private BitmapFont font;
  private SpriteBatch batch;
  private int counter = 60;
  private final static int WORLD_BORDER = 16;
  private final PositionBuffer buffer = new PositionBuffer(2048);
  private final Collection<Fly> flies = new ArrayList<>();

  @Override
  public void create() {
    renderer = new ShapeRenderer();
    batch = new SpriteBatch();
    font = new BitmapFont(Gdx.files.internal("data/hehe.fnt"));
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
    updatePoints(partition, flies, buffer);
    Gdx.graphics.getGL20().glClearColor(.25f, .25f, .25f, 1f);
    Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    renderer.setProjectionMatrix(camera.combined);
    renderPoints(renderer, flies, buffer);
    batch.setProjectionMatrix(camera.combined);
    PartitionRenderer.render(partition, renderer, font, batch);
    if (counter++ >= 60) {
      PartitionUpdater.update(partition, flies, buffer);
      counter = 0;
    }
  }

  private static void renderPoints(ShapeRenderer renderer, Collection<Fly> flies, PositionBuffer buffer) {
    renderer.begin(ShapeRenderer.ShapeType.Point);
    renderer.setColor(Color.WHITE);
    for (Fly fly : flies) {
      float x = buffer.getX(fly.positionPivot);
      float y = buffer.getY(fly.positionPivot);
      renderer.point(x, y, 0);
    }
    renderer.end();
  }

  private static void updatePoints(Partition partition, Collection<Fly> files, PositionBuffer buffer) {
    for (Fly fly : files)
      update(partition, fly, buffer);
  }

  private static void update(Partition partition, Fly fly, PositionBuffer buffer) {
    int pivot = fly.positionPivot;
    performCage(pivot, buffer);
    float x = buffer.getX(pivot);
    float y = buffer.getY(pivot);
    int cellX = partition.positionFor(x);
    int cellY = partition.positionFor(y);
    int dist = 2;
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

  private static void computeFor(Fly fly, Collection<Integer> othersPivots, PositionBuffer buffer) {
    if (othersPivots == null)
      return;
    for (Integer otherPivot : othersPivots) {
      int pivot = fly.positionPivot;
      if (otherPivot == pivot)
        continue;
      float x = buffer.getX(pivot);
      float y = buffer.getY(pivot);
      float otherX = buffer.getX(otherPivot);
      float otherY = buffer.getY(otherPivot);
      float dist = (float) Math.pow(1f - Math.min(64, Vector2.dst(x, y, otherX, otherY)) / 64, 2f) * .00125f;
      float dy = otherY - y;
      float dx = otherX - x;
      float angle = MathUtils.atan2(dy, dx);
      float vx = MathUtils.cos(angle) * dist;
      float vy = MathUtils.sin(angle) * dist;
      fly.move.add(vx, vy);
    }
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
