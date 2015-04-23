package main;

import com.badlogic.gdx.math.Vector2;

public class Fly {

  public final int positionPivot;
  public final Vector2 move = new Vector2();

  public Fly(int positionPivot) {
    this.positionPivot = positionPivot;
  }

}
