package main.points;

import main.Fly;

import java.util.Collection;

public class PartitionUpdater {

  public static void update(Partition<Fly> partition, Collection<Fly> flies, PositionBuffer buffer) {
    partition.clear();
    for (Fly fly : flies) {
      int pivot = fly.positionPivot;
      float x = buffer.getX(pivot);
      float y = buffer.getY(pivot);
      partition.put(fly, x, y);
    }
  }

}
