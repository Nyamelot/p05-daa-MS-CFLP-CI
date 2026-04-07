package daa.algorithms.grasp;

import daa.algorithms.InstanceSolveStrategy;
import daa.model.Instance;
import daa.model.Solution;
import java.util.*;

/**
 * Implementation of the GRASP (Greedy Randomized Adaptive Search Procedure) strategy
 * for the MS-CFLP-CI problem.
 * * This strategy iteratively executes a randomized constructive phase followed by
 * an improvement phase (Local Search) to find high-quality solutions.
 * @author Jose Angel Portillo Garcia
 * @version 2025-2026
 */
public class GraspStrategy implements InstanceSolveStrategy {
  private final int rclSize;
  private final int maxIterations;
  private final List<LocalSearchStrategy> searchMovements;
  private final Random random = new Random();

  /**
   * Constructs a GraspStrategy with the specified parameters.
   * * @param rclSize The size of the Restricted Candidate List used during construction.
   * @param maxIterations The number of GRASP iterations to perform.
   * @param movements A list of local search strategies (e.g., Shift, Swap) to apply.
   */
  public GraspStrategy(int rclSize, int maxIterations, List<LocalSearchStrategy> movements) {
    this.rclSize = rclSize;
    this.maxIterations = maxIterations;
    this.searchMovements = movements;
  }

  /**
   * Solves the MS-CFLP-CI instance using the GRASP metaheuristic.
   * * @param instance The problem instance data.
   * @return The best {@link Solution} found across all iterations.
   */
  @Override
  public Solution solve(Instance instance) {
    Solution bestGlobal = null;
    for (int i = 0; i < maxIterations; i++) {
      Solution current = constructivePhase(instance);
      applyLocalSearch(current, instance);

      if (bestGlobal == null || current.getTotalCost() < bestGlobal.getTotalCost()) {
        bestGlobal = current;
      }
    }
    return bestGlobal;
  }

  /**
   * Performs the Greedy Randomized construction phase.
   * * Facilities are ranked based on a Cost-to-Capacity ratio, and a randomized
   * selection is made from the Restricted Candidate List (RCL).
   * * @param instance The problem instance data.
   * @return A randomly constructed initial solution.
   */
  private Solution constructivePhase(Instance instance) {
    Solution sol = new Solution(instance);

    List<Integer> facilityCandidates = new ArrayList<>();
    for (int j = 0; j < instance.getNumWarehouses(); j++) {
      facilityCandidates.add(j);
    }

    facilityCandidates.sort(Comparator.comparingDouble(j ->
      instance.getFixedCost(j) / (double) instance.getCapacity(j)));

    int limit = Math.min(rclSize, facilityCandidates.size());
    for (int k = 0; k < 10; k++) {
      int chosenFacility = facilityCandidates.get(random.nextInt(limit));
      sol.open[chosenFacility] = true;
    }

    assignClients(sol, instance);
    sol.calculateCosts();
    return sol;
  }

  /**
   * Applies the improvement phase using the provided local search movements.
   * * Uses a First Improvement strategy: the first movement that finds a
   * better solution restarts the search cycle.
   * * @param sol The solution to improve.
   * @param instance The problem instance data.
   */
  private void applyLocalSearch(Solution sol, Instance instance) {
    boolean improved = true;
    while (improved) {
      improved = false;
      for (LocalSearchStrategy movement : searchMovements) {
        if (movement.improve(sol, instance)) {
          sol.calculateCosts();
          improved = true;
          break;
        }
      }
    }
  }

  /**
   * Greedily assigns clients to opened facilities based on transportation costs,
   * considering capacity and customer incompatibility constraints.
   * * @param sol The solution object where assignments will be stored.
   * @param instance The problem instance data.
   */
  private void assignClients(Solution sol, Instance instance) {
    for (int i = 0; i < instance.getNumStores(); i++) {
      int bestJ = -1;
      double minCost = Double.MAX_VALUE;

      for (int j = 0; j < instance.getNumWarehouses(); j++) {
        if (sol.open[j] && sol.isCompatible(i, j) && sol.residualCap[j] >= instance.getDemand(i)) {
          double cost = instance.getTransportCost(i, j);
          if (cost < minCost) {
            minCost = cost;
            bestJ = j;
          }
        }
      }

      if (bestJ != -1) {
        sol.x[i][bestJ] = 1.0;
        sol.w[i][bestJ] = true;
        sol.residualCap[bestJ] -= instance.getDemand(i);
        sol.clientsOf[bestJ].add(i);
        sol.facilitiesOf[i].add(bestJ);

        for (int other = 0; other < instance.getNumStores(); other++) {
          if (instance.areIncompatible(i, other)) {
            sol.incompCount[other][bestJ]++;
          }
        }
      }
    }
  }
}