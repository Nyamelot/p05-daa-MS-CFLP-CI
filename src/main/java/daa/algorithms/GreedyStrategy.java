package daa.algorithms;

import daa.model.Instance;
import daa.model.Solution;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Implementation of the Greedy constructive algorithm for the MS-CFLP-CI problem.
 * * This strategy follows a two-phase approach:
 * 1. Facility selection based on ascending fixed costs with a slack factor (k=5).
 * 2. Customer assignment based on transportation costs while respecting
 * capacity and customer incompatibility constraints.
 * @author Jose Angel Portillo Garcia
 * @version 2025-2026
 */
public class GreedyStrategy implements InstanceSolveStrategy {

  /**
   * Solves the MS-CFLP-CI instance using a greedy approach.
   * * The algorithm first opens enough warehouses to cover total demand plus a slack
   * of k extra warehouses. Then, it assigns each store to the cheapest available
   * and compatible warehouses.
   * * @param instance The problem instance data containing costs, demands, and constraints.
   * @return A {@link Solution} object representing the facility states and store assignments.
   */
  @Override
  public Solution solve(Instance instance) {
    Solution sol = new Solution(instance);
    int numWarehouses = instance.getNumWarehouses();
    int numStores = instance.getNumStores();

    List<Integer> sortedWarehouses = new ArrayList<>();
    for (int j = 0; j < numWarehouses; j++) {
      sortedWarehouses.add(j);
    }
    sortedWarehouses.sort(Comparator.comparingDouble(instance::getFixedCost));

    double totalDemand = 0;
    for (int i = 0; i < numStores; i++) {
      totalDemand += instance.getDemand(i);
    }

    double accumulatedCapacity = 0;
    List<Integer> fOpen = new ArrayList<>();

    int lastSelectedIdx = -1;
    for (int idx = 0; idx < sortedWarehouses.size(); idx++) {
      if (accumulatedCapacity < totalDemand) {
        int j = sortedWarehouses.get(idx);
        fOpen.add(j);
        sol.open[j] = true;
        accumulatedCapacity += instance.getCapacity(j);
        lastSelectedIdx = idx;
      } else {
        break;
      }
    }

    int k = 5;
    for (int count = 1; count <= k && (lastSelectedIdx + count) < sortedWarehouses.size(); count++) {
      int extraJ = sortedWarehouses.get(lastSelectedIdx + count);
      fOpen.add(extraJ);
      sol.open[extraJ] = true;
    }

    for (int i = 0; i < numStores; i++) {
      final int currentStore = i;
      List<Integer> sortedOpen = new ArrayList<>(fOpen);
      sortedOpen.sort(Comparator.comparingDouble(j -> instance.getTransportCost(currentStore, j)));

      double remainingStoreDemand = instance.getDemand(i);

      for (int j : sortedOpen) {
        if (remainingStoreDemand <= 0) {
          break;
        }

        if (isCompatible(instance, sol, i, j)) {
          double q = Math.min(remainingStoreDemand, sol.residualCap[j]);

          if (q > 0) {
            double fraction = q / instance.getDemand(i);
            sol.x[i][j] = fraction;
            sol.w[i][j] = true;

            remainingStoreDemand -= q;
            sol.residualCap[j] -= q;
            sol.clientsOf[j].add(i);
            sol.facilitiesOf[i].add(j);
          }
        }
      }
    }

    sol.calculateCosts();
    return sol;
  }

  /**
   * Checks if a store can be assigned to a warehouse without violating
   * incompatibility constraints.
   * * @param instance The problem instance data containing the incompatibility matrix.
   * @param sol The current partial solution containing existing assignments.
   * @param storeId The ID of the store (customer) to be assigned.
   * @param warehouseId The ID of the warehouse (hub) candidate.
   * @return {@code true} if the store is compatible with all clients currently
   * assigned to the hub; {@code false} otherwise.
   */
  private boolean isCompatible(Instance instance, Solution sol, int storeId, int warehouseId) {
    for (int clientInHub : sol.clientsOf[warehouseId]) {
      if (instance.areIncompatible(storeId, clientInHub)) {
        return false;
      }
    }
    return true;
  }
}