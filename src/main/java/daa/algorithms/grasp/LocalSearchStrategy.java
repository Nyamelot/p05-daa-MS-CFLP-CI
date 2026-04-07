package daa.algorithms.grasp;

import daa.model.Instance;
import daa.model.Solution;

/**
 * Interface for local search improvement strategies in the GRASP metaheuristic.
 * * Implementations of this interface define specific neighborhood movements
 * (such as Shift or Swap) to explore and improve a given solution for the
 * MS-CFLP-CI problem.
 * @author Jose Angel Portillo Garcia
 * @version 2025-2026
 */
public interface LocalSearchStrategy {

  /**
   * Attempts to improve the current solution by exploring its neighborhood.
   * * @param solution The current solution to be improved.
   * @param instance The problem instance data containing constraints and costs.
   * @return {@code true} if a better solution was found and the input solution
   * object was modified; {@code false} otherwise.
   */
  boolean improve(Solution solution, Instance instance);
}