package main;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import main.points.PositionBuffer;

public class PointsRenderer {

  public static void render(ShapeRenderer renderer, PositionBuffer buffer) {
    renderer.begin(ShapeRenderer.ShapeType.Point);
    renderer.setColor(Color.WHITE);
    for (int i = 0; i < buffer.allocationsArray().length; i++)
      renderer.point(buffer.getX(i), buffer.getY(i), 0);
    renderer.end();
  }

}
