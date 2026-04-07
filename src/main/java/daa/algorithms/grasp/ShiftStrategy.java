package daa.algorithms.grasp;

import daa.model.Instance;
import daa.model.Solution;

/**
 * Implementation of the Shift local search movement for the MS-CFLP-CI problem.
 * * The Shift movement attempts to improve the solution by moving a single client
 * from its current facility to a different open facility that is cheaper,
 * while respecting capacity and incompatibility constraints.
 * @author Jose Angel Portillo Garcia
 * @version 2025-2026
 */
public class ShiftStrategy implements LocalSearchStrategy {

  /**
   * Executes the Shift local search. It looks for the first improvement
   * (First Improvement strategy) found by reassigning a store to a different hub.
   * * @param sol The current solution to be improved.
   * @param instance The problem instance data containing costs and constraints.
   * @return {@code true} if an improvement was found and applied; {@code false} otherwise.
   */
  @Override
  public boolean improve(Solution sol, Instance instance) {
    for (int i = 0; i < instance.getNumStores(); i++) {
      // Check if the store is currently assigned
      if (sol.facilitiesOf[i].isEmpty()) {
        continue;
      }

      // Get the current facility serving store i
      int currentJ = sol.facilitiesOf[i].get(0);

      for (int targetJ = 0; targetJ < instance.getNumWarehouses(); targetJ++) {
        // The target facility must be open and different from the current one
        if (sol.open[targetJ] && targetJ != currentJ) {

          // 1. Feasibility Check: Capacity and Incompatibility
          if (sol.residualCap[targetJ] >= instance.getDemand(i) && sol.isCompatible(i, targetJ)) {

            double currentCost = instance.getTransportCost(i, currentJ);
            double targetCost = instance.getTransportCost(i, targetJ);

            // 2. Improvement Check: Is the new assignment cheaper?
            if (targetCost < currentCost) {
              applyShift(sol, instance, i, currentJ, targetJ);
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  /**
   * Applies the Shift movement by updating all solution structures:
   * assignment matrices, residual capacities, and incompatibility counters.
   * * @param sol The solution object to update.
   * @param instance The problem instance data.
   * @param i The ID of the store being moved.
   * @param oldJ The ID of the current facility.
   * @param newJ The ID of the target facility.
   */
  private void applyShift(Solution sol, Instance instance, int i, int oldJ, int newJ) {
    // Update Assignment and Indicator matrices
    sol.x[i][oldJ] = 0;
    sol.w[i][oldJ] = false;
    sol.x[i][newJ] = 1.0;
    sol.w[i][newJ] = true;

    // Update Capacities
    sol.residualCap[oldJ] += instance.getDemand(i);
    sol.residualCap[newJ] -= instance.getDemand(i);

    // Update Relationship lists
    sol.clientsOf[oldJ].remove(Integer.valueOf(i));
    sol.clientsOf[newJ].add(i);
    sol.facilitiesOf[i].clear();
    sol.facilitiesOf[i].add(newJ);

    // Update Incompatibility tracking counters for the involved hubs
    for (int k = 0; k < instance.getNumStores(); k++) {
      if (instance.areIncompatible(i, k)) {
        sol.incompCount[k][oldJ]--;
        sol.incompCount[k][newJ]++;
      }
    }
  }
}