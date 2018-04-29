package uk.co.openkappa.bitrules.config;

import uk.co.openkappa.bitrules.Rule;
import uk.co.openkappa.bitrules.nodes.ComparableRule;

import java.util.Comparator;
import java.util.function.Function;

/**
 * Creates a column of constraints with equality and order semantics
 * @param <T> the type of the classified objects
 * @param <U> the type of the attribute
 */
public class ComparableAttribute<T, U> implements Attribute<T> {

  private final Comparator<U> comparator;
  private final Function<T, U> accessor;

  ComparableAttribute(Comparator<U> comparator, Function<T, U> accessor) {
    this.comparator = comparator;
    this.accessor = accessor;
  }

  @Override
  public Rule<T> toRule() {
    return new ComparableRule<>(accessor, comparator);
  }
}
