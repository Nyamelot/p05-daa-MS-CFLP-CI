package daa.algorithms.grasp;

import daa.model.Instance;
import daa.model.Solution;

/**
 * Implementation of the Swap local search movement for the MS-CFLP-CI problem.
 * * The Swap movement attempts to improve the solution by exchanging the assigned
 * facilities of two different stores. It verifies that the exchange is both
 * feasible regarding capacity and incompatibility constraints, and that it
 * results in a lower total transportation cost.
 * @author Jose Angel Portillo Garcia
 * @version 2025-2026
 */
public class SwapStrategy implements LocalSearchStrategy {

  /**
   * Executes the Swap local search using a First Improvement strategy.
   * * @param sol The current solution to be improved.
   * @param instance The problem instance data.
   * @return {@code true} if a successful swap was performed; {@code false} otherwise.
   */
  @Override
  public boolean improve(Solution sol, Instance instance) {
    for (int i1 = 0; i1 < instance.getNumStores(); i1++) {
      if (sol.facilitiesOf[i1].isEmpty()) continue;
      int j1 = sol.facilitiesOf[i1].get(0);

      for (int i2 = i1 + 1; i2 < instance.getNumStores(); i2++) {
        if (sol.facilitiesOf[i2].isEmpty()) continue;
        int j2 = sol.facilitiesOf[i2].get(0);

        if (j1 == j2) continue;

        if (checkFeasibility(sol, instance, i1, j2, i2, j1)) {
          double currentCost = instance.getTransportCost(i1, j1) + instance.getTransportCost(i2, j2);
          double swapCost = instance.getTransportCost(i1, j2) + instance.getTransportCost(i2, j1);

          if (swapCost < currentCost) {
            applySwap(sol, instance, i1, j1, i2, j2);
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Verifies if swapping the assignments of two stores is feasible.
   * * @param sol The current solution.
   * @param instance The problem instance data.
   * @param i1 ID of the first store.
   * @param targetJ1 ID of the facility intended for the first store.
   * @param i2 ID of the second store.
   * @param targetJ2 ID of the facility intended for the second store.
   * @return {@code true} if the swap respects capacity and incompatibility; {@code false} otherwise.
   */
  private boolean checkFeasibility(Solution sol, Instance instance, int i1, int targetJ1, int i2, int targetJ2) {
    boolean cap1 = (sol.residualCap[targetJ1] + instance.getDemand(i2)) >= instance.getDemand(i1);
    boolean cap2 = (sol.residualCap[targetJ2] + instance.getDemand(i1)) >= instance.getDemand(i2);

    return cap1 && cap2 && sol.isCompatible(i1, targetJ1) && sol.isCompatible(i2, targetJ2);
  }

  /**
   * Updates the solution state to reflect the exchanged store assignments.
   * * @param sol The solution object to update.
   * @param instance The problem instance data.
   * @param i1 ID of the first store.
   * @param j1 ID of the original facility for the first store.
   * @param i2 ID of the second store.
   * @param j2 ID of the original facility for the second store.
   */
  private void applySwap(Solution sol, Instance instance, int i1, int j1, int i2, int j2) {
    sol.x[i1][j1] = 0; sol.w[i1][j1] = false;
    sol.x[i2][j2] = 0; sol.w[i2][j2] = false;

    sol.x[i1][j2] = 1.0; sol.w[i1][j2] = true;
    sol.x[i2][j1] = 1.0; sol.w[i2][j1] = true;

    sol.residualCap[j1] = sol.residualCap[j1] + instance.getDemand(i1) - instance.getDemand(i2);
    sol.residualCap[j2] = sol.residualCap[j2] + instance.getDemand(i2) - instance.getDemand(i1);

    sol.clientsOf[j1].remove(Integer.valueOf(i1));
    sol.clientsOf[j2].remove(Integer.valueOf(i2));
    sol.clientsOf[j1].add(i2);
    sol.clientsOf[j2].add(i1);

    sol.facilitiesOf[i1].clear();
    sol.facilitiesOf[i1].add(j2);
    sol.facilitiesOf[i2].clear();
    sol.facilitiesOf[i2].add(j1);

    for (int k = 0; k < instance.getNumStores(); k++) {
      if (instance.areIncompatible(i1, k)) {
        sol.incompCount[k][j1]--;
        sol.incompCount[k][j2]++;
      }
      if (instance.areIncompatible(i2, k)) {
        sol.incompCount[k][j2]--;
        sol.incompCount[k][j1]++;
      }
    }
  }
}