package main;

import java.util.ArrayList;
import java.util.Collection;

public class Partition {

  public final int cellCount;
  public final float worldSize;
  private final Collection<Molecule>[][] table;

  public Partition(int cellCount, float worldSize) {
    this.cellCount = cellCount;
    this.worldSize = worldSize;
    table = initializeTable(cellCount);
  }

  @SuppressWarnings("unchecked")
  private static Collection<Molecule>[][] initializeTable(int capacity) {
    Collection<Molecule>[][] table = new ArrayList[capacity][capacity];
    for (int ix = 0; ix < capacity; ix++)
      for (int iy = 0; iy < capacity; iy++)
        table[ix][iy] = new ArrayList<>();
    return table;
  }

  public void put(Molecule mole) {
    int x = positionFor(mole.position.x);
    int y = positionFor(mole.position.y);
    table[x][y].add(mole);
  }

  public Collection<Molecule> get(int x, int y) {
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
