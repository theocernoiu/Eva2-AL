package eva2.server.go.problems.tsputil;


import eva2.server.go.individuals.InterfaceDataTypePermutation;


/**
 * <p>Title: EvA2</p>
 * <p/>
 * <p>Description: </p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p/>
 * <p>Company: </p> This local search method removes 2 edges, and connects the opposite nodes. If the tour is better the change is saved. This step is
 * repeated for all the edges in the graph.
 *
 * @author planatsc
 * @version 1.0
 */
public class TSPLocalSearch2OPT implements InterfaceTSPLocalSearch, java.io.Serializable {

    public TSPLocalSearch2OPT() {
    }

    public void doLocalSearch(InterfaceTSPInstance tspinst, InterfaceDataTypePermutation individual) {
        int[][] permm = ((InterfaceDataTypePermutation) individual).getPermutationData();
        int[] perm = permm[0];
        for (int i = 0; i < perm.length; i++) {
            for (int j = 0; j < perm.length; j++) {
                int cityi0 = (i == 0) ? 0 : perm[i - 1];
                int cityi1 = perm[i];
                int cityi2 = (i == (perm.length - 1)) ? 0 : perm[i + 1];
                int cityj0 = (j == 0) ? 0 : perm[j - 1];
                int cityj1 = perm[j];
                int cityj2 = (j == (perm.length - 1)) ? 0 : perm[j + 1];
                if ((tspinst.getDistance(cityi0, cityi1) +
                        tspinst.getDistance(cityi1, cityi2) +
                        tspinst.getDistance(cityj0, cityj1) +
                        tspinst.getDistance(cityj1, cityj2)
                ) >
                        (tspinst.getDistance(cityi0, cityj1) +
                                tspinst.getDistance(cityj1, cityi2) +
                                tspinst.getDistance(cityj0, cityi1) +
                                tspinst.getDistance(cityi1, cityj2)
                        )) {
                    int temp = perm[i];
                    perm[i] = perm[j];
                    perm[j] = temp;
                }
            }
        }
        ((InterfaceDataTypePermutation) individual).SetPermutationGenotype(permm);
    }

}
