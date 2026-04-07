package daa.algorithms.grasp;

import daa.algorithms.InstanceSolveStrategy;
import daa.model.Instance;
import daa.model.Solution;

import java.util.*;

public class GraspStrategy implements InstanceSolveStrategy {
  private final int rclSize;
  private final int maxIterations;
  private final List<LocalSearchStrategy> searchMovements;
  private final Random random = new Random();

  public GraspStrategy(int rclSize, int maxIterations, List<LocalSearchStrategy> movements) {
    this.rclSize = rclSize;
    this.maxIterations = maxIterations;
    this.searchMovements = movements;
  }

  @Override
  public Solution solve(Instance instance) {
    Solution bestGlobal = null;
    for (int i = 0; i < maxIterations; i++) {
      // 1. Constructive Phase (Greedy Randomized)
      Solution current = constructivePhase(instance);

      // 2. Improvement Phase (Local Search)
      applyLocalSearch(current, instance);

      // Update the best solution found
      if (bestGlobal == null || current.getTotalCost() < bestGlobal.getTotalCost()) {
        bestGlobal = current;
      }
    }
    return bestGlobal;
  }

  private Solution constructivePhase(Instance instance) {
    Solution sol = new Solution(instance);

    // Strategy: Open facilities based on a Cost-to-Capacity ratio (Heuristic)
    List<Integer> facilityCandidates = new ArrayList<>();
    for (int j = 0; j < instance.getNumWarehouses(); j++) facilityCandidates.add(j);

    facilityCandidates.sort(Comparator.comparingDouble(j ->
      instance.getFixedCost(j) / (double) instance.getCapacity(j)));

    // Randomized selection from RCL for facility opening
    int limit = Math.min(rclSize, facilityCandidates.size());
    for (int k = 0; k < 10; k++) { // Open a subset of facilities
      int chosenFacility = facilityCandidates.get(random.nextInt(limit));
      sol.open[chosenFacility] = true;
    }

    // Greedy assignment of clients to the opened facilities
    assignClients(sol, instance);

    sol.calculateCosts();
    return sol;
  }

  private void applyLocalSearch(Solution sol, Instance instance) {
    boolean improved = true;
    while (improved) {
      improved = false;
      // Iterate through the strategy patterns for local search (Shift, Swap)
      for (LocalSearchStrategy movement : searchMovements) {
        if (movement.improve(sol, instance)) {
          sol.calculateCosts();
          improved = true;
          break; // First Improvement Strategy
        }
      }
    }
  }

  private void assignClients(Solution sol, Instance instance) {
    // For each client, find the cheapest compatible open facility
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

      // Simple greedy assignment for the constructive phase
      if (bestJ != -1) {
        sol.x[i][bestJ] = 1.0;
        sol.w[i][bestJ] = true;
        sol.residualCap[bestJ] -= instance.getDemand(i);
        sol.clientsOf[bestJ].add(i);
        sol.facilitiesOf[i].add(bestJ);

        // Update incompatibility tracking for efficiency
        for (int other = 0; other < instance.getNumStores(); other++) {
          if (instance.areIncompatible(i, other)) {
            sol.incompCount[other][bestJ]++;
          }
        }
      }
    }
  }
}
