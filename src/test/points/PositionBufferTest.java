package test.points;

import main.points.PositionBuffer;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class PositionBufferTest {

  private static final float[] ZERO_POSITIONS = new float[]{0, 0, 0, 0};
  private static final float[] FILLED_POSITIONS = new float[]{1, 2, 3, 4};
  private static final boolean[] ALL_DEALLOCATED = new boolean[]{false, false};
  private static final boolean[] ALL_ALLOCATED = new boolean[]{true, true};

  @Test
  public void initialiseEmpty() {
    PositionBuffer buffer = new PositionBuffer(2);
    assertEquals(buffer.positionsArray(), ZERO_POSITIONS);
    assertEquals(buffer.allocationsArray(), ALL_DEALLOCATED);
  }

  @Test
  public void initialiseFromArray() {
    PositionBuffer buffer = new PositionBuffer(FILLED_POSITIONS);
    assertEquals(buffer.positionsArray(), FILLED_POSITIONS);
    assertEquals(buffer.allocationsArray(), ALL_ALLOCATED);
  }


  @Test(expectedExceptions = RuntimeException.class)
  public void initialiseWithInvalidArraySize() {
    new PositionBuffer(new float[]{1, 2, 3});
  }

  @Test
  public void putOnEmpty() {
    PositionBuffer buffer = new PositionBuffer(2);
    int id = buffer.allocate(1, 2);
    assertEquals(id, 0);
    assertEquals(buffer.positionsArray(), new float[]{1, 2, 0, 0});
  }

  @Test
  public void putOnEmptyTwice() {
    PositionBuffer buffer = new PositionBuffer(2);
    int first = buffer.allocate(1, 2);
    assertEquals(first, 0);
    assertEquals(buffer.allocationsArray(), new boolean[]{true, false});
    int second = buffer.allocate(3, 4);
    assertEquals(second, 1);
    assertEquals(buffer.allocationsArray(), new boolean[]{true, true});
    assertEquals(buffer.positionsArray(), new float[]{1, 2, 3, 4});
  }

  @Test
  public void deallocate() {
    PositionBuffer buffer = new PositionBuffer(FILLED_POSITIONS);
    buffer.deallocate(0);
    assertEquals(buffer.allocationsArray(), new boolean[]{false, true});
    buffer.deallocate(1);
    assertEquals(buffer.allocationsArray(), ALL_DEALLOCATED);
  }

  @Test
  public void positionGetters() {
    PositionBuffer buffer = new PositionBuffer(FILLED_POSITIONS);
    assertEquals((int) buffer.getX(0), 1);
    assertEquals((int) buffer.getY(0), 2);
    assertEquals((int) buffer.getX(1), 3);
    assertEquals((int) buffer.getY(1), 4);
  }

}