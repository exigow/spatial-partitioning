package main.buffers;

import java.util.Arrays;

public class PositionBuffer {

  private final int count;
  private final float[] buffer;
  private final boolean[] allocated;

  public PositionBuffer(float[] variables) {
    int length = variables.length;
    if (length % 2 != 0)
      throw new RuntimeException();
    count = length / 2;
    buffer = initialise(count);
    System.arraycopy(variables, 0, buffer, 0, length);
    allocated = new boolean[count];
    Arrays.fill(allocated, true);
  }

  public PositionBuffer(int count) {
    this.count = count;
    buffer = initialise(count);
    allocated = new boolean[count];
  }

  private static float[] initialise(int count) {
    return new float[count * 2];
  }

  public int allocate(float x, float y) {
    int pivot = findFree();
    buffer[pivot * 2] = x;
    buffer[pivot * 2 + 1] = y;
    allocated[pivot] = true;
    return pivot;
  }

  public void deallocate(int pivot) {
    buffer[pivot * 2] = 0;
    buffer[pivot * 2 + 1] = 0;
    allocated[pivot] = false;
  }

  public int findFree() {
    for (int i = 0; i < allocated.length; i++)
      if (!allocated[i])
        return i;
    return -1;
  }

  public float getX(int pivot) {
    return buffer[pivot];
  }

  public float getY(int pivot) {
    return buffer[pivot] + 1;
  }

  public float[] positionsArray() {
    return buffer;
  }

  public boolean[] allocationsArray() {
    return allocated;
  }

}
