package uk.co.openkappa.bitrules.matchers;

import org.junit.Test;
import uk.co.openkappa.bitrules.TinyMask;
import uk.co.openkappa.bitrules.Operation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static uk.co.openkappa.bitrules.TinyMask.contiguous;
import static uk.co.openkappa.bitrules.Mask.with;
import static uk.co.openkappa.bitrules.Mask.without;

public class IntNodeTestTiny {
  @Test
  public void testGreaterThan() {
    IntNode<TinyMask> node = build(5, Operation.GT);
    TinyMask mask = contiguous(5);
    assertTrue(node.apply(0, mask.clone()).isEmpty());
    assertEquals(with(new TinyMask(), 0), node.apply(1, mask.clone()));
  }


  @Test
  public void testGreaterThanOrEqual() {
    IntNode<TinyMask> node = build(5, Operation.GE);
    TinyMask mask = contiguous(5);
    assertTrue(node.apply(-1, mask.clone()).isEmpty());
    assertEquals(with(new TinyMask(), 0), node.apply(0, mask.clone()));
    assertEquals(with(with(new TinyMask(), 1), 0), node.apply(10, mask.clone()));
  }

  @Test
  public void testEqual() {
    IntNode<TinyMask> node = build(5, Operation.EQ);
    TinyMask mask = contiguous(5);
    assertTrue(node.apply(-1, mask.clone()).isEmpty());
    assertEquals(with(new TinyMask(), 0), node.apply(0, mask.clone()));
    assertEquals(with(new TinyMask(), 1), node.apply(10, mask.clone()));
  }


  @Test
  public void testLessThanOrEqual() {
    IntNode<TinyMask> node = build(5, Operation.LE);
    TinyMask mask = contiguous(5);
    assertTrue(node.apply(1001, mask.clone()).isEmpty());
    assertEquals(mask, node.apply(0, mask.clone()));
    assertEquals(without(mask.clone(), 0), node.apply(10, mask.clone()));
  }

  @Test
  public void testLessThan() {
    IntNode<TinyMask> node = build(5, Operation.LT);
    TinyMask mask = contiguous(5);
    assertTrue(node.apply(1001, mask.clone()).isEmpty());
    assertEquals(without(mask.clone(), 0), node.apply(0, mask.clone()));
    assertEquals(without(without(mask.clone(), 0), 1), node.apply(10, mask.clone()));
  }

  @Test
  public void testGreaterThanRev() {
    IntNode<TinyMask> node = buildRev(5, Operation.GT);
    TinyMask mask = contiguous(5);
    assertTrue(node.apply(0, mask.clone()).isEmpty());
    assertEquals(with(new TinyMask(), 0), node.apply(1, mask.clone()));
  }


  @Test
  public void testGreaterThanOrEqualRev() {
    IntNode<TinyMask> node = buildRev(5, Operation.GE);
    TinyMask mask = contiguous(5);
    assertTrue(node.apply(-1, mask.clone()).isEmpty());
    assertEquals(with(new TinyMask(), 0), node.apply(0, mask.clone()));
    assertEquals(with(with(new TinyMask(), 0), 1), node.apply(10, mask.clone()));
  }

  @Test
  public void testEqualRev() {
    IntNode<TinyMask> node = buildRev(5, Operation.EQ);
    TinyMask mask = contiguous(5);
    assertTrue(node.apply(-1, mask.clone()).isEmpty());
    assertEquals(with(new TinyMask(), 0), node.apply(0, mask.clone()));
    assertEquals(with(new TinyMask(), 1), node.apply(10, mask.clone()));
  }


  @Test
  public void testLessThanOrEqualRev() {
    IntNode<TinyMask> node = buildRev(5, Operation.LE);
    TinyMask mask = contiguous(5);
    assertTrue(node.apply(1001, mask.clone()).isEmpty());
    assertEquals(mask, node.apply(0, mask.clone()));
    assertEquals(without(mask.clone(), 0), node.apply(10, mask.clone()));
  }

  @Test
  public void testLessThanRev() {
    IntNode<TinyMask> node = buildRev(5, Operation.LT);
    TinyMask mask = contiguous(5);
    assertTrue(node.apply(1001, mask.clone()).isEmpty());
    assertEquals(without(mask.clone(), 0), node.apply(0, mask.clone()));
    assertEquals(without(without(mask.clone(), 0), 1), node.apply(10, mask.clone()));
  }

  @Test
  public void testBuildNode() {
    IntNode<TinyMask> node = new IntNode<>(Operation.EQ, new TinyMask());
    node.add(0, (short)0);
    assertEquals(contiguous( 1), node.apply(0, contiguous( 1)));
    node.add(0, (short)1);
    assertEquals(contiguous( 2), node.apply(0, contiguous( 2)));
  }

  private IntNode<TinyMask> build(int count, Operation relation) {
    IntNode<TinyMask> node = new IntNode<>(relation, new TinyMask());
    for (int i = 0; i < count; ++i) {
      node.add(i * 10, i);
    }
    return node.optimise();
  }

  private IntNode<TinyMask> buildRev(int count, Operation relation) {
    IntNode<TinyMask> node = new IntNode<>(relation, new TinyMask());
    for (int i = count - 1; i >= 0; --i) {
      node.add(i * 10, i);
    }
    return node.optimise();
  }
}
