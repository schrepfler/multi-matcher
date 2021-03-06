package io.github.richardstartin.multimatcher.core.masks;

import io.github.richardstartin.multimatcher.core.Mask;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class WordMask implements Mask<WordMask> {

    public static MaskStore<WordMask> store(int max) {
        return max <= 32
                ? new IntStore()
                : new LongStore();
    }

    public static final int MAX_CAPACITY = 64;

    private long mask;

    public WordMask(long mask) {
        this.mask = mask;
    }

    public WordMask(int mask) {
        this(mask & 0xFFFFFFFFL);
    }

    public WordMask() {
    }

    public void add(int bit) {
        mask |= 1L << bit;
    }

    @Override
    public void remove(int id) {
        mask ^= (1L << id);
    }

    @Override
    public WordMask inPlaceAndNot(WordMask other) {
        this.mask &= ~other.mask;
        return this;
    }

    public WordMask inPlaceAnd(WordMask other) {
        this.mask &= other.mask;
        return this;
    }

    public WordMask or(WordMask other) {
        return new WordMask(this.mask | other.mask);
    }

    public WordMask inPlaceOr(WordMask other) {
        this.mask |= other.mask;
        return this;
    }

    @Override
    public WordMask inPlaceNot(int max) {
        this.mask = (~mask) & ((1L << max) -1);
        return this;
    }

    @Override
    public WordMask resetTo(Mask<WordMask> other) {
        this.mask = other.unwrap().mask;
        return this;
    }

    @Override
    public void clear() {
        this.mask = 0;
    }

    @Override
    public WordMask unwrap() {
        return this;
    }

    @Override
    public IntStream stream() {
        return LongStream.iterate(mask, mask -> mask & (mask - 1))
                .limit(Long.bitCount(mask))
                .mapToInt(Long::numberOfTrailingZeros);
    }

    @Override
    public void forEach(IntConsumer consumer) {
        long word = mask;
        while (word != 0) {
            consumer.accept(Long.numberOfTrailingZeros(word));
            word &= (word - 1);
        }
    }

    @Override
    public int first() {
        if (!isEmpty()) {
            return Long.numberOfTrailingZeros(mask);
        }
        throw new NoSuchElementException("empty mask");
    }

    @Override
    public WordMask clone() {
        return new WordMask(mask);
    }

    @Override
    public void optimise() {

    }

    @Override
    public boolean isEmpty() {
        return mask == 0L;
    }

    @Override
    public int cardinality() {
        return Long.bitCount(mask);
    }

    @Override
    public String toString() {
        return Long.toBinaryString(mask);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordMask wordMask = (WordMask) o;
        return mask == wordMask.mask;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mask);
    }

    private static final class LongStore implements MaskStore<WordMask> {

        private long[] masks = new long[4];

        private int maskId = 0;

        @Override
        public WordMask newMask() {
            return new WordMask(0L);
        }

        @Override
        public int newMaskId() {
            ensureCapacity(++maskId);
            return maskId;
        }

        @Override
        public int storeMask(WordMask mask) {
            ensureCapacity(++maskId);
            masks[maskId] = mask.mask;
            return maskId;
        }

        @Override
        public WordMask getMask(int id) {
            return new WordMask(masks[id & (masks.length - 1)]);
        }

        @Override
        public void add(int id, int bit) {
            masks[id & (masks.length - 1)] |= (1L << bit);
        }

        @Override
        public void remove(int id, int bit) {
            masks[id & (masks.length - 1)] ^= (1L << bit);
        }

        @Override
        public void or(int from, int into) {
            masks[into & (masks.length - 1)] |= masks[from & (masks.length - 1)];
        }

        @Override
        public WordMask getTemp(int copyAddress) {
            return new WordMask(masks[copyAddress & (masks.length - 1)]);
        }

        @Override
        public void orInto(WordMask mask, int id) {
            mask.mask |= masks[id & (masks.length - 1)];
        }

        @Override
        public void andInto(WordMask mask, int id) {
            mask.mask &= masks[id & (masks.length - 1)];
        }

        @Override
        public WordMask contiguous(int max) {
            return new WordMask(((1L << max) - 1));
        }

        @Override
        public int newContiguousMaskId(int max) {
            ensureCapacity(++maskId);
            masks[maskId] = (1L << max) - 1;
            return maskId;
        }

        @Override
        public boolean isEmpty(int id) {
            return masks[id & (masks.length - 1)] == 0L;
        }

        @Override
        public WordMask of(int... values) {
            long word = 0L;
            for (int v : values) {
                word |= (1L << v);
            }
            return new WordMask(word);
        }

        @Override
        public double averageSelectivity(int[] ids, int min, int max) {
            int cardinality = 0;
            for (int i = min; i < max; ++i) {
                cardinality += Long.bitCount(this.masks[ids[i]]);
            }
            return ((double)cardinality)/masks.length;
        }

        @Override
        public double averageSelectivity(int[] masks) {
            int cardinality = 0;
            for (int i : masks) {
                cardinality += Long.bitCount(this.masks[i]);
            }
            return ((double)cardinality)/masks.length;
        }

        private void ensureCapacity(int maskId) {
            if (maskId >= masks.length) {
                masks = Arrays.copyOf(masks, masks.length * 2);
            }
        }
    }

    private static final class IntStore implements MaskStore<WordMask> {
        private int[] masks = new int[4];

        private int maskId = 0;

        @Override
        public WordMask newMask() {
            return new WordMask(0L);
        }

        @Override
        public int newMaskId() {
            ensureCapacity(++maskId);
            return maskId;
        }

        @Override
        public int storeMask(WordMask mask) {
            ensureCapacity(++maskId);
            masks[maskId] = (int)mask.mask;
            return maskId;
        }

        @Override
        public WordMask getMask(int id) {
            return new WordMask(masks[id & (masks.length - 1)]);
        }

        @Override
        public void add(int id, int bit) {
            masks[id & (masks.length - 1)] |= (1 << bit);
        }

        @Override
        public void remove(int id, int bit) {
            masks[id & (masks.length - 1)] ^= (1 << bit);
        }

        @Override
        public void or(int from, int into) {
            masks[into & (masks.length - 1)] |= masks[from & (masks.length - 1)];
        }

        @Override
        public WordMask getTemp(int copyAddress) {
            return new WordMask(masks[copyAddress & (masks.length - 1)]);
        }

        @Override
        public void orInto(WordMask mask, int id) {
            mask.mask |= masks[id & (masks.length - 1)];
        }

        @Override
        public void andInto(WordMask mask, int id) {
            mask.mask &= masks[id & (masks.length - 1)];
        }

        @Override
        public WordMask contiguous(int max) {
            return new WordMask(((1 << max) - 1));
        }

        @Override
        public int newContiguousMaskId(int max) {
            ensureCapacity(++maskId);
            masks[maskId] = (1 << max) - 1;
            return maskId;
        }

        @Override
        public boolean isEmpty(int id) {
            return masks[id & (masks.length - 1)] == 0;
        }

        @Override
        public WordMask of(int... values) {
            int word = 0;
            for (int v : values) {
                word |= (1 << v);
            }
            return new WordMask(word);
        }

        @Override
        public double averageSelectivity(int[] ids, int min, int max) {
            int cardinality = 0;
            for (int i = min; i < max; ++i) {
                cardinality += Integer.bitCount(this.masks[ids[i]]);
            }
            return ((double)cardinality)/masks.length;
        }

        @Override
        public double averageSelectivity(int[] masks) {
            int cardinality = 0;
            for (int i : masks) {
                cardinality += Integer.bitCount(this.masks[i]);
            }
            return ((double)cardinality)/masks.length;
        }

        private void ensureCapacity(int maskId) {
            if (maskId >= masks.length) {
                masks = Arrays.copyOf(masks, masks.length * 2);
            }
        }
    }
}
