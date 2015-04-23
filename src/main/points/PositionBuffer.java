package main.points;

import java.util.Arrays;

public class PositionBuffer {

  private final int count;
  private final float[] buffer;
  private final boolean[] allocated;

  public PositionBuffer(float[] variables) {
    int length = variables.length;
    if (!isEven(length))
      throw new RuntimeException();
    count = length / 2;
    buffer = initialize(count);
    System.arraycopy(variables, 0, buffer, 0, length);
    allocated = new boolean[count];
    Arrays.fill(allocated, true);
  }

  public PositionBuffer(int count) {
    this.count = count;
    buffer = initialize(count);
    allocated = new boolean[count];
  }

  private static boolean isEven(int val) {
    return val % 2 == 0;
  }

  private static float[] initialize(int count) {
    return new float[count * 2];
  }

  public int allocate(float x, float y) {
    int pivot = findFree();
    updatePosition(pivot, x, y);
    allocated[pivot] = true;
    return pivot;
  }

  public void deallocate(int pivot) {
    updatePosition(pivot, 0, 0);
    allocated[pivot] = false;
  }

  public void updatePosition(int pivot, float x, float y) {
    updateX(pivot, x);
    updateY(pivot, y);
  }

  public void updateX(int pivot, float x) {
    buffer[pivot * 2] = x;
  }

  public void updateY(int pivot, float y) {
    buffer[pivot * 2 + 1] = y;
  }

  public int findFree() {
    for (int i = 0; i < allocated.length; i++)
      if (!allocated[i])
        return i;
    return -1;
  }

  public float getX(int pivot) {
    return buffer[pivot * 2];
  }

  public float getY(int pivot) {
    return buffer[pivot * 2 + 1];
  }

  public float[] positionsArray() {
    return buffer;
  }

  public boolean[] allocationsArray() {
    return allocated;
  }

}
