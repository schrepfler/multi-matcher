package uk.co.openkappa.bitrules;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class TinyMask implements Mask<TinyMask> {

  public static final int MAX_CAPACITY = 64;

  public static TinyMask contiguous(int to) {
    return new TinyMask(((1L << to) - 1));
  }

  public static TinyMask full() {
    return new TinyMask(-1L);
  }

  private long mask;

  public TinyMask(long mask) {
    this.mask = mask;
  }

  public TinyMask() { }

  public void add(int bit) {
    mask |= 1L << bit;
  }

  @Override
  public void remove(int id) {
    mask ^= 1L << id;
  }

  public TinyMask and(TinyMask other) {
    return new TinyMask(this.mask & other.mask);
  }

  @Override
  public TinyMask andNot(TinyMask other) {
    return new TinyMask(mask &~ other.mask);
  }

  public TinyMask inPlaceAnd(TinyMask other) {
    this.mask &= other.mask;
    return this;
  }

  public TinyMask or(TinyMask other) {
    return new TinyMask(this.mask | other.mask);
  }

  public TinyMask inPlaceOr(TinyMask other) {
    this.mask |= other.mask;
    return this;
  }

  @Override
  public IntStream stream() {
    return LongStream.iterate(mask, mask -> mask & (mask - 1))
                     .limit(Long.bitCount(mask))
                     .mapToInt(Long::numberOfTrailingZeros);
  }

  @Override
  public int first() {
    if (!isEmpty()) {
      return Long.numberOfTrailingZeros(mask);
    }
    throw new NoSuchElementException("empty mask");
  }

  @Override
  public TinyMask clone() {
    return new TinyMask(mask);
  }

  @Override
  public void optimise() {

  }

  @Override
  public boolean isEmpty() {
    return mask == 0L;
  }

  @Override
  public String toString() {
    return Long.toBinaryString(mask);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TinyMask tinyMask = (TinyMask) o;
    return mask == tinyMask.mask;
  }

  @Override
  public int hashCode() {
    return Objects.hash(mask);
  }
}
