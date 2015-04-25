package main.chunks;

public class Chunk {

  public final int fromX;
  public final int fromY;
  public final int toX;
  public final int toY;

  public Chunk(int fromX, int fromY, int toX, int toY) {
    this.fromX = fromX;
    this.fromY = fromY;
    this.toX = toX;
    this.toY = toY;
  }

}
