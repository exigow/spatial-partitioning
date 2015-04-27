package main;

import main.chunks.Chunk;
import main.points.Partition;
import main.points.PositionBuffer;

import java.util.Collection;
import java.util.concurrent.Callable;

public class ParallelSimulation implements Callable<Long> {

  private final static float GRAVITATIONAL_CONST = 6.67384e-11f;
  private final Partition<Fly> partition;
  private final PositionBuffer buffer;
  private final Chunk chunk;

  public ParallelSimulation(Partition<Fly> partition, PositionBuffer buffer, Chunk chunk) {
    this.partition = partition;
    this.buffer = buffer;
    this.chunk = chunk;
  }

  @Override
  public Long call() throws Exception {
    long start = System.currentTimeMillis();
    for (int x = chunk.fromX; x < chunk.toX; x++)
      for (int y = chunk.fromY; y < chunk.toY; y++)
        updateCell(partition, buffer, x, y);
    long end = System.currentTimeMillis();
    return end - start;
  }

  public static void updateCell(Partition<Fly> partition, PositionBuffer buffer, int x, int y) {
    Collection<Fly> files = partition.get(x, y);
    for (Fly fly : files)
      update(partition, fly, buffer);
  }

  private static void update(Partition<Fly> partition, Fly fly, PositionBuffer buffer) {
    int pivot = fly.positionPivot;
    performCage(pivot, partition, buffer);
    float x = buffer.getX(pivot);
    float y = buffer.getY(pivot);
    int cellX = partition.positionFor(x);
    int cellY = partition.positionFor(y);
    int dist = 1;
    for (int ix = -dist; ix <= dist; ix++)
      for (int iy = -dist; iy <= dist; iy++)
        computeFor(fly, partition.get(cellX + ix, cellY + iy), buffer);
    buffer.updatePosition(pivot, x + fly.move.x, y + fly.move.y);
  }

  private static void performCage(int pivot, Partition<Fly> partition, PositionBuffer buffer) {
    float x = buffer.getX(pivot);
    float y = buffer.getY(pivot);
    float max = partition.worldSize;
    if (x > max)
      buffer.updateX(pivot, 0);
    if (x < 0)
      buffer.updateX(pivot, max);
    if (y > max)
      buffer.updateY(pivot, 0);
    if (y < 0)
      buffer.updateY(pivot, max);
  }

  private static void computeFor(Fly fly, Collection<Fly> othersFlies, PositionBuffer buffer) {
    if (othersFlies == null)
      return;
    for (Fly otherFly : othersFlies) {
      int pivot = fly.positionPivot;
      int otherPivot = otherFly.positionPivot;
      if (otherPivot == pivot)
        continue;
      float x = buffer.getX(pivot);
      float y = buffer.getY(pivot);
      float otherX = buffer.getX(otherPivot);
      float otherY = buffer.getY(otherPivot);
      float dy = otherY - y;
      float dx = otherX - x;
      float distanceSquared = (float) Math.sqrt(dx * dx + dy * dy);
      float force =  GRAVITATIONAL_CONST * 100000000f / distanceSquared;
      float vx = dx * force / distanceSquared;
      float vy = dy * force / distanceSquared;
      fly.move.add(vx, vy);
    }
    fly.move.scl(.9975f);
  }

}
