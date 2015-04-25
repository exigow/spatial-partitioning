package main.points;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Partition<T> {

  public final int cellCount;
  public final float worldSize;
  private final Collection<T>[][] table;

  public Partition(int cellCount, float worldSize) {
    this.cellCount = cellCount;
    this.worldSize = worldSize;
    table = initializeTable(cellCount);
  }

  @SuppressWarnings("unchecked")
  private Collection<T>[][] initializeTable(int capacity) {
    Collection<T>[][] table = new ConcurrentLinkedDeque[capacity][capacity];
    for (int ix = 0; ix < capacity; ix++)
      for (int iy = 0; iy < capacity; iy++)
        table[ix][iy] = new ConcurrentLinkedDeque<>();
    return table;
  }

  public void put(T ref, float x, float y) {
    int cellX = positionFor(x);
    int cellY = positionFor(y);
    table[cellX][cellY].add(ref);
  }

  public Collection<T> get(int x, int y) {
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
