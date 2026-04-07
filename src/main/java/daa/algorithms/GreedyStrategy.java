package daa.algorithms;

import daa.model.Instance;
import daa.model.Solution;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GreedyStrategy implements InstanceSolveStrategy {

  @Override
  public Solution solve(Instance instance) {
    Solution sol = new Solution(instance);
    int numWarehouses = instance.getNumWarehouses();
    int numStores = instance.getNumStores();

    // --- FASE 1: SELECCIÓN DE INSTALACIONES [cite: 153] ---
    // 1. Ordenar F en orden ascendente según coste fijo f_j
    List<Integer> sortedWarehouses = new ArrayList<>();
    for (int j = 0; j < numWarehouses; j++) sortedWarehouses.add(j);
    sortedWarehouses.sort(Comparator.comparingDouble(instance::getFixedCost));

    double totalDemand = 0;
    for (int i = 0; i < numStores; i++) totalDemand += instance.getDemand(i); // [cite: 155]

    double accumulatedCapacity = 0;
    List<Integer> fOpen = new ArrayList<>();

    // Seleccionar instalaciones hasta cubrir la demanda total [cite: 158-165]
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

    // Añadir holgura por incompatibilidad (k=5) [cite: 151, 166-169]
    int k = 5;
    for (int count = 1; count <= k && (lastSelectedIdx + count) < sortedWarehouses.size(); count++) {
      int extraJ = sortedWarehouses.get(lastSelectedIdx + count);
      fOpen.add(extraJ);
      sol.open[extraJ] = true;
    }

    // --- FASE 2: ASIGNACIÓN DE CLIENTES [cite: 170] ---
    // Nota: residualCap y clientsOf ya se inicializan en el constructor de tu clase Solution

    for (int i = 0; i < numStores; i++) { // [cite: 174]
      // Ordenar instalaciones abiertas por coste unitario de transporte c_ij [cite: 184]
      final int currentStore = i;
      List<Integer> sortedOpen = new ArrayList<>(fOpen);
      sortedOpen.sort(Comparator.comparingDouble(j -> instance.getTransportCost(currentStore, j)));

      double remainingStoreDemand = instance.getDemand(i);

      for (int j : sortedOpen) { // [cite: 185]
        if (remainingStoreDemand <= 0) break;

        // Verificar compatibilidad [cite: 188]
        if (isCompatible(instance, sol, i, j)) {
          double q = Math.min(remainingStoreDemand, sol.residualCap[j]); // [cite: 190]

          if (q > 0) {
            // Asignar fracción de la demanda [cite: 191]
            double fraction = q / instance.getDemand(i);
            sol.x[i][j] = fraction;
            sol.w[i][j] = true;

            // Actualizar estados [cite: 192-194]
            remainingStoreDemand -= q;
            sol.residualCap[j] -= q;
            sol.clientsOf[j].add(i);
            sol.facilitiesOf[i].add(j);
          }
        }
      }
    }

    sol.calculateCosts(); // [cite: 195-198]
    return sol; // [cite: 199]
  }

  private boolean isCompatible(Instance instance, Solution sol, int storeId, int warehouseId) {
    for (int clientInHub : sol.clientsOf[warehouseId]) {
      if (instance.areIncompatible(storeId, clientInHub)) {
        return false; //
      }
    }
    return true;
  }
}