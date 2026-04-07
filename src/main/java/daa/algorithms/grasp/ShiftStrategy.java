package daa.algorithms.grasp;

import daa.model.Instance;
import daa.model.Solution;

public class ShiftStrategy implements LocalSearchStrategy {
  @Override
  public boolean improve(Solution sol, Instance instance) {
    for (int i = 0; i < instance.getNumStores(); i++) {
      // Get the current facility serving client i
      // (Simplification: assuming single-source for the basic movement)
      if (sol.facilitiesOf[i].isEmpty()) continue;
      int currentJ = sol.facilitiesOf[i].get(0);

      for (int targetJ = 0; targetJ < instance.getNumWarehouses(); targetJ++) {
        if (sol.open[targetJ] && targetJ != currentJ) {
          // 1. Feasibility Check: Capacity and Incompatibility
          if (sol.residualCap[targetJ] >= instance.getDemand(i) && sol.isCompatible(i, targetJ)) {

            double currentCost = instance.getTransportCost(i, currentJ);
            double targetCost = instance.getTransportCost(i, targetJ);

            // 2. Improvement Check
            if (targetCost < currentCost) {
              applyShift(sol, instance, i, currentJ, targetJ);
              return true; // First Improvement found
            }
          }
        }
      }
    }
    return false;
  }

  private void applyShift(Solution sol, Instance instance, int i, int oldJ, int newJ) {
    // Update Primary Structures
    sol.x[i][oldJ] = 0; sol.w[i][oldJ] = false;
    sol.x[i][newJ] = 1.0; sol.w[i][newJ] = true;

    // Update Auxiliary Structures
    sol.residualCap[oldJ] += instance.getDemand(i);
    sol.residualCap[newJ] -= instance.getDemand(i);
    sol.clientsOf[oldJ].remove(Integer.valueOf(i));
    sol.clientsOf[newJ].add(i);
    sol.facilitiesOf[i].clear();
    sol.facilitiesOf[i].add(newJ);

    // Update Incompatibility Matrix
    for (int k = 0; k < instance.getNumStores(); k++) {
      if (instance.areIncompatible(i, k)) {
        sol.incompCount[k][oldJ]--;
        sol.incompCount[k][newJ]++;
      }
    }
  }
}
