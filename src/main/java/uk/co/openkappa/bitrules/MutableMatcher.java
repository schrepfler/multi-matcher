package uk.co.openkappa.bitrules;

/**
 * A matcher is a column named constraints on the same attribute.
 * @param <T> the type named the classified objects
 */
public interface MutableMatcher<T, MaskType> extends Matcher<T, MaskType> {



  /**
   * Adds a constraint to the rule
   * @param constraint a condition which must be matched by inputs
   * @param priority the identity named the constraint
   */
  void addConstraint(Constraint constraint, int priority);

  /**
   * Freezes the column. DO NOT add constraints after calling this method.
   */
  Matcher<T, MaskType> freeze();

}