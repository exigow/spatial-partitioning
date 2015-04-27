package main.renderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import main.points.Partition;

public class PartitionRenderer {

  public static void render(Partition partition, ShapeRenderer renderer, BitmapFont font, SpriteBatch batch) {
    float cellSize = partition.worldSize / partition.cellCount;
    renderGridLines(partition, renderer, cellSize);
    renderMoleculesCount(partition, font, batch, cellSize);
  }

  private static void renderGridLines(Partition partition, ShapeRenderer renderer, float cellSize) {
    renderer.setColor(Color.DARK_GRAY);
    renderer.begin(ShapeRenderer.ShapeType.Line);
    for (int ix = 0; ix <= partition.cellCount; ix++) {
      float x = ix * cellSize;
      renderer.line(x, 0, x, partition.worldSize);
    }
    for (int iy = 0; iy <= partition.cellCount; iy++) {
      float y = iy * cellSize;
      renderer.line(0, y, partition.worldSize, y);
    }
    renderer.end();
  }

  private static void renderMoleculesCount(Partition partition, BitmapFont font, SpriteBatch batch, float cellSize) {
    batch.begin();
    for (int ix = 0; ix < partition.cellCount; ix++) {
      for (int iy = 0; iy < partition.cellCount; iy++) {
        float x = ix * cellSize + 1;
        float y = (iy + 1) * cellSize - 1;
        int size = partition.get(ix, iy).size();
        if (size != 0)
          font.draw(batch, "" + size, x, y);
      }
    }
    batch.end();
  }

}
