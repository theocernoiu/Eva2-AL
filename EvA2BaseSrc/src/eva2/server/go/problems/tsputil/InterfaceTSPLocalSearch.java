package eva2.server.go.problems.tsputil;

import eva2.server.go.individuals.InterfaceDataTypePermutation;


/**
 * <p>Title: EvA2</p>
 * <p/>
 * <p>Description: </p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p/>
 * <p>Company: </p>
 * Defines an interface for localsearch algorithms applied to the Travelling Salesman Problem.
 *
 * @author planatsc
 * @version 1.0
 */
public interface InterfaceTSPLocalSearch {

    /**
     * doLocalSearch does a local search on a instance of the TSP. A starting point (permutation) for the search is given. If the local search archives
     * a better result than the starting point, the starting permutation is updated to the better one.
     *
     * @param tspinst InterfaceTSPInstance
     * @param perm    InterfaceDataTypePermutation
     */
    public void doLocalSearch(InterfaceTSPInstance tspinst, InterfaceDataTypePermutation perm);

}
