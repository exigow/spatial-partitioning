package main.points;

import java.util.ArrayList;
import java.util.Collection;

public class Partition<T> {

  public final int cellCount;
  public final float worldSize;
  private final Collection<T>[][] table;
  private final int maxArrayIndex;

  public Partition(int cellCount, float worldSize) {
    this.cellCount = cellCount;
    this.worldSize = worldSize;
    maxArrayIndex = cellCount - 1;
    table = initializeTable(cellCount);
  }

  @SuppressWarnings("unchecked")
  private Collection<T>[][] initializeTable(int capacity) {
    Collection<T>[][] table = new ArrayList[capacity][capacity];
    for (int ix = 0; ix < capacity; ix++)
      for (int iy = 0; iy < capacity; iy++)
        table[ix][iy] = new ArrayList<>();
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
    int result = (int) (dimension / (worldSize / cellCount));
    if (result > maxArrayIndex)
      return maxArrayIndex;
    if (result < 0)
      return 0;
    return result;
  }

}
