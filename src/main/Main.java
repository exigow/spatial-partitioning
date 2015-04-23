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

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;

public class Main implements ApplicationListener {

  private static final float WORLD_SIZE = 512;
  private final Partition partition = new Partition(8, WORLD_SIZE);
  private final Collection<Molecule> molecules = new ArrayList<>();
  private ShapeRenderer renderer;
  private OrthographicCamera camera;
  //private BitmapFont font;
  //private SpriteBatch batch;
  private int counter = 60;

  @Override
  public void create() {
    renderer = new ShapeRenderer();
    //batch = new SpriteBatch();
    //font = new BitmapFont();
    camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.position.add(WORLD_SIZE / 2, WORLD_SIZE / 2, 0);
    camera.update();
    for (int i = 0; i < 1024; i++)
      molecules.add(new Molecule(WORLD_SIZE));
  }

  @Override
  public void render() {
    updatePoints(partition, molecules);
    Gdx.graphics.getGL20().glClearColor(.125f, .125f, .125f, 1f);
    Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    renderer.setProjectionMatrix(camera.combined);
    renderPoints(renderer, molecules);
    //batch.setProjectionMatrix(camera.combined);
    //PartitionRenderer.render(partition, renderer, font, batch);
    if (counter++ >= 60) {
      partition.clear();
      molecules.forEach(partition::put);
      counter = 0;
    }
  }

  private static void renderPoints(ShapeRenderer renderer, Collection<Molecule> molecules) {
    renderer.begin(ShapeRenderer.ShapeType.Point);
    renderer.setColor(Color.WHITE);
    for (Molecule mole : molecules)
      renderer.point(mole.position.x, mole.position.y, 0);
    renderer.end();
  }

  private static void updatePoints(Partition partition, Collection<Molecule> molecules) {
    for (Molecule mole : molecules)
      update(partition, mole);
  }

  private static void update(Partition partition, Molecule mole) {
    float size = WORLD_SIZE - 16;
    if (mole.position.x > size) {
      mole.move.x *= -1;
      mole.position.x = size;
    }
    if (mole.position.x < 0) {
      mole.move.x *= -1;
      mole.position.x = 0;
    }
    if (mole.position.y > size) {
      mole.move.y *= -1;
      mole.position.y = size;
    }
    if (mole.position.y < 0) {
      mole.move.y *= -1;
      mole.position.y = 0;
    }
    int x = partition.positionFor(mole.position.x);
    int y = partition.positionFor(mole.position.y);
    for (int ix = -1; ix <= 1; ix++)
      for (int iy = -1; iy <= 1; iy++)
        computeFor(mole, partition.get(x + ix, y + iy));
    mole.position.add(mole.move);
  }

  private static void computeFor(Molecule mole, Collection<Molecule> others) {
    if (others == null)
      return;
    for (Molecule other : others) {
      if (mole == other)
        continue;
      float dist = (float) Math.pow(1f - Math.min(64, Vector2.dst(mole.position.x, mole.position.y, other.position.x, other.position.y)) / 64, 2f) * .00125f;
      float dy = other.position.y - mole.position.y;
      float dx = other.position.x - mole.position.x;
      float angle = MathUtils.atan2(dy, dx);
      float x = (float) Math.cos(angle) * dist;
      float y = (float) Math.sin(angle) * dist;
      mole.move.add(x, y);
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
