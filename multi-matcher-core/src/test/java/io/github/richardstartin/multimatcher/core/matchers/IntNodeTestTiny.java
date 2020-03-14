package io.github.richardstartin.multimatcher.core.matchers;

import io.github.richardstartin.multimatcher.core.Operation;
import io.github.richardstartin.multimatcher.core.masks.WordMask;
import io.github.richardstartin.multimatcher.core.matchers.nodes.IntNode;
import org.junit.jupiter.api.Test;

import static io.github.richardstartin.multimatcher.core.Mask.with;
import static io.github.richardstartin.multimatcher.core.Mask.without;
import static io.github.richardstartin.multimatcher.core.masks.WordMask.FACTORY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IntNodeTestTiny {
  @Test
  public void testGreaterThan() {
    IntNode<WordMask> node = build(5, Operation.GT);
    WordMask mask = FACTORY.contiguous(5);
    assertTrue(node.apply(0, mask.clone()).isEmpty());
    assertEquals(with(new WordMask(), 0), node.apply(1, mask.clone()));
  }


  @Test
  public void testGreaterThanOrEqual() {
    IntNode<WordMask> node = build(5, Operation.GE);
    WordMask mask = FACTORY.contiguous(5);
    assertTrue(node.apply(-1, mask.clone()).isEmpty());
    assertEquals(with(new WordMask(), 0), node.apply(0, mask.clone()));
    assertEquals(with(with(new WordMask(), 1), 0), node.apply(10, mask.clone()));
  }

  @Test
  public void testEqual() {
    IntNode<WordMask> node = build(5, Operation.EQ);
    WordMask mask = FACTORY.contiguous(5);
    assertTrue(node.apply(-1, mask.clone()).isEmpty());
    assertEquals(with(new WordMask(), 0), node.apply(0, mask.clone()));
    assertEquals(with(new WordMask(), 1), node.apply(10, mask.clone()));
  }


  @Test
  public void testLessThanOrEqual() {
    IntNode<WordMask> node = build(5, Operation.LE);
    WordMask mask = FACTORY.contiguous(5);
    assertTrue(node.apply(1001, mask.clone()).isEmpty());
    assertEquals(mask, node.apply(0, mask.clone()));
    assertEquals(without(mask.clone(), 0), node.apply(10, mask.clone()));
  }

  @Test
  public void testLessThan() {
    IntNode<WordMask> node = build(5, Operation.LT);
    WordMask mask = FACTORY.contiguous(5);
    assertTrue(node.apply(1001, mask.clone()).isEmpty());
    assertEquals(without(mask.clone(), 0), node.apply(0, mask.clone()));
    assertEquals(without(without(mask.clone(), 0), 1), node.apply(10, mask.clone()));
  }

  @Test
  public void testGreaterThanRev() {
    IntNode<WordMask> node = buildRev(5, Operation.GT);
    WordMask mask = FACTORY.contiguous(5);
    assertTrue(node.apply(0, mask.clone()).isEmpty());
    assertEquals(with(new WordMask(), 0), node.apply(1, mask.clone()));
  }


  @Test
  public void testGreaterThanOrEqualRev() {
    IntNode<WordMask> node = buildRev(5, Operation.GE);
    WordMask mask = FACTORY.contiguous(5);
    assertTrue(node.apply(-1, mask.clone()).isEmpty());
    assertEquals(with(new WordMask(), 0), node.apply(0, mask.clone()));
    assertEquals(with(with(new WordMask(), 0), 1), node.apply(10, mask.clone()));
  }

  @Test
  public void testEqualRev() {
    IntNode<WordMask> node = buildRev(5, Operation.EQ);
    WordMask mask = FACTORY.contiguous(5);
    assertTrue(node.apply(-1, mask.clone()).isEmpty());
    assertEquals(with(new WordMask(), 0), node.apply(0, mask.clone()));
    assertEquals(with(new WordMask(), 1), node.apply(10, mask.clone()));
  }


  @Test
  public void testLessThanOrEqualRev() {
    IntNode<WordMask> node = buildRev(5, Operation.LE);
    WordMask mask = FACTORY.contiguous(5);
    assertTrue(node.apply(1001, mask.clone()).isEmpty());
    assertEquals(mask, node.apply(0, mask.clone()));
    assertEquals(without(mask.clone(), 0), node.apply(10, mask.clone()));
  }

  @Test
  public void testLessThanRev() {
    IntNode<WordMask> node = buildRev(5, Operation.LT);
    WordMask mask = FACTORY.contiguous(5);
    assertTrue(node.apply(1001, mask.clone()).isEmpty());
    assertEquals(without(mask.clone(), 0), node.apply(0, mask.clone()));
    assertEquals(without(without(mask.clone(), 0), 1), node.apply(10, mask.clone()));
  }

  @Test
  public void testBuildNode() {
    IntNode<WordMask> node = new IntNode<>(WordMask.FACTORY, Operation.EQ);
    node.add(0, 0);
    assertEquals(FACTORY.contiguous( 1), node.apply(0, FACTORY.contiguous( 1)));
    node.add(0, 1);
    assertEquals(FACTORY.contiguous( 2), node.apply(0, FACTORY.contiguous( 2)));
  }

  private IntNode<WordMask> build(int count, Operation relation) {
    IntNode<WordMask> node = new IntNode<>(WordMask.FACTORY, relation);
    for (int i = 0; i < count; ++i) {
      node.add(i * 10, i);
    }
    return node.optimise();
  }

  private IntNode<WordMask> buildRev(int count, Operation relation) {
    IntNode<WordMask> node = new IntNode<>(WordMask.FACTORY, relation);
    for (int i = count - 1; i >= 0; --i) {
      node.add(i * 10, i);
    }
    return node.optimise();
  }
}
