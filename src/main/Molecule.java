package main;

import com.badlogic.gdx.math.Vector2;

public class Molecule {

  public final Vector2 position;
  public final Vector2 move = new Vector2();

  public Molecule(float range) {
    position = randomVector().scl(range);
  }

  private static Vector2 randomVector() {
    return new Vector2(randomValue(), randomValue());
  }

  private static float randomValueNormalized() {
    return -1f + (float) Math.random() * 2f;
  }

  private static float randomValue() {
    return (float) Math.random();
  }

}
