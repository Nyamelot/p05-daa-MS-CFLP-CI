package daa.algorithms;

import daa.model.Instance;
import daa.model.Solution;

/**
 * Common interface for all algorithm strategies used to solve the MS-CFLP-CI problem.
 * * This follows the Strategy Design Pattern, allowing the main application to
 * switch between different implementations (like Greedy or GRASP) seamlessly.
 * @author Jose Angel Portillo Garcia
 * @version 2025-2026
 */
public interface InstanceSolveStrategy {

  /**
   * Solves a given problem instance and returns the resulting solution.
   * * @param instance The problem instance data (costs, demands, capacities, etc.).
   * @return A {@link Solution} object containing the facility states and assignments.
   */
  Solution solve(Instance instance);
}