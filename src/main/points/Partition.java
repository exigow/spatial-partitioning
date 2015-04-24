package main.points;

import main.Fly;

import java.util.ArrayList;
import java.util.Collection;

public class Partition {

  public final int cellCount;
  public final float worldSize;
  private final Collection<Fly>[][] table;

  public Partition(int cellCount, float worldSize) {
    this.cellCount = cellCount;
    this.worldSize = worldSize;
    table = initializeFlyable(cellCount);
  }

  @SuppressWarnings("unchecked")
  private static Collection<Fly>[][] initializeFlyable(int capacity) {
    Collection<Fly>[][] table = new ArrayList[capacity][capacity];
    for (int ix = 0; ix < capacity; ix++)
      for (int iy = 0; iy < capacity; iy++)
        table[ix][iy] = new ArrayList<>();
    return table;
  }

  public void put(Fly pivot, float x, float y) {
    int cellX = positionFor(x);
    int cellY = positionFor(y);
    table[cellX][cellY].add(pivot);
  }

  public Collection<Fly> get(int x, int y) {
    if (x < 0 || y < 0 || x >= cellCount || y >= cellCount)
      return null;
    return table[x][y];
  }

  public void clear() {
    for (int ix = 0; ix < cellCount; ix++)
      for (int iy = 0; iy < cellCount; iy++)
        table[ix][iy].clear();
  }

  public int positionFor(float dimension) {
    return (int) (dimension / (worldSize / cellCount));
  }

}
