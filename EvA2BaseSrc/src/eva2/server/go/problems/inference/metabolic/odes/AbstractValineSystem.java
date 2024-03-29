package eva2.server.go.problems.inference.metabolic.odes;

import eva2.tools.math.des.DESystem;

/**
 * This class contains the necessary splines for selected metabolites of the
 * valine/leucin reaction network in C. glutamicum.
 *
 * @author Andreas Dr&auml;ger (draeger) <andreas.draeger@uni-tuebingen.de>
 *         Copyright (c) ZBiT, University of T&uuml;bingen, Germany Compiler:
 *         JDK 1.6.0
 * @date
 * @date 2006-12-06
 * @since 2.0
 */
public abstract class AbstractValineSystem implements DESystem {

    /**
     * The metabolite concenterations in mM at the reference state transformed
     * in the intervall [0,1] according to table 3 from the valine paper.
     */
    protected final double pyr = 0.0430; // .689

    protected final double akg = 0.0763; // 5.12

    protected final double ala = 0.0787; // 1.05

    protected final double nad = 0.5301; // .528

    protected final double nadp = 0.1757; // .0175

    protected final double glut = 0.3732; // 38.7

    protected double[] p;

    //	private HashMap<Double,Double> pyrHash=null;
    /*
	 * (non-Javadoc)
	 *
	 * @see eva2.server.go.OptimizationProblems.InferenceRegulatoryNetworks.Des.DESystem#getDESSystemOrder()
	 */
    public int getDESystemDimension() {
        return 7;
    }

    /**
     * Allows to set the parameters in this system to the designated value.
     *
     * @param x the new parameters for this system.
     */
    public void setParameters(double[] params) {
        if (params.length == getNumberOfParameters())
            this.p = params;
        else
            throw new IllegalArgumentException("This system requires exactly "
                    + getNumberOfParameters() + " parameters but received "
                    + params.length + ".");
    }

    /**
     * This method tells the required number of parameters of this system.
     *
     * @return
     */
    public abstract int getNumberOfParameters();

    /**
     * @param v
     * @return
     */
    public double[] linearCombinationOfVelocities(double[] v) {
        return new double[]{v[1] - v[2], // DHIV
                v[6] - v[7], // 2IPM
                v[0] - v[1], // AcLac
                v[3] + v[4] - v[5], // Val
                v[8] - v[9], // Leu
                v[2] - v[3] - v[4] - v[6], // KIV
                v[7] - v[8] // KIC
        };
    }

    public void linearCombinationOfVelocities(double[] v, double[] res) {
        res[0] = v[1] - v[2]; // DHIV
        res[1] = v[6] - v[7]; // 2IPM
        res[2] = v[0] - v[1]; // AcLac
        res[3] = v[3] + v[4] - v[5]; // Val
        res[4] = v[8] - v[9]; // Leu
        res[5] = v[2] - v[3] - v[4] - v[6]; // KIV
        res[6] = v[7] - v[8]; // KIC
    }

    /**
     * Returns the approximation spline for akg or the default value 5.12 mM
     * with S=26 and all weights equal to one.
     *
     * @param t
     * @return
     */
    public double getAKG(double t) {
        double y = this.akg;

        if ((-3.894 <= t) && (t <= -3.429))
            y = 0.00969646 + 0.0299456 * (t + 3.894) + 0
                    * Math.pow(t + 3.894, 2) + 0.0624106
                    * Math.pow(t + 3.894, 3);
        else if (t <= -2.964)
            y = 0.0298962 + 0.0704298 * (t + 3.429) + 8.706283e-02
                    * Math.pow(t + 3.429, 2) - 0.110594
                    * Math.pow(t + 3.429, 3);
        else if (t <= -2.499)
            y = 0.0703517 + 0.0796589 * (t + 2.964) - 6.721530e-02
                    * Math.pow(t + 2.964, 2) - 0.0462005
                    * Math.pow(t + 2.964, 3);
        else if (t <= -2.037)
            y = 0.0882142 - 0.0128204 * (t + 2.499) - 1.316651e-01
                    * Math.pow(t + 2.499, 2) + 0.11841 * Math.pow(t + 2.499, 3);
        else if (t <= -1.572)
            y = 0.0658646 - 0.0586573 * (t + 2.037) + 3.245109e-02
                    * Math.pow(t + 2.037, 2) + 0.104831
                    * Math.pow(t + 2.037, 3);
        else if (t <= -1.107)
            y = 0.0561459 + 0.0395234 * (t + 1.572) + 1.786901e-01
                    * Math.pow(t + 1.572, 2) - 0.194164
                    * Math.pow(t + 1.572, 3);
        else if (t <= -0.642)
            y = 0.0936394 + 0.0797561 * (t + 1.107) - 9.216813e-02
                    * Math.pow(t + 1.107, 2) - 0.019546
                    * Math.pow(t + 1.107, 3);
        else if (t <= 0.363)
            y = 0.108832 - 0.0186393 * (t + 0.642) - 1.194349e-01
                    * Math.pow(t + 0.642, 2) + 0.154951
                    * Math.pow(t + 0.642, 3);
        else if (t <= 0.828)
            y = 0.126754 + 0.21081 * (t - 0.363) + 3.477431e-01
                    * Math.pow(t - 0.363, 2) + 0.219188
                    * Math.pow(t - 0.363, 3);
        else if (t <= 1.293)
            y = 0.32201 + 0.676393 * (t - 0.828) + 6.535099e-01
                    * Math.pow(t - 0.828, 2) - 1.12127 * Math.pow(t - 0.828, 3);
        else if (t <= 1.758)
            y = 0.665101 + 0.556819 * (t - 1.293) - 9.106580e-01
                    * Math.pow(t - 1.293, 2) + 0.691096
                    * Math.pow(t - 1.293, 3);
        else if (t <= 2.22)
            y = 0.7966 + 0.158204 * (t - 1.758) + 5.342020e-02
                    * Math.pow(t - 1.758, 2) - 0.288946
                    * Math.pow(t - 1.758, 3);
        else if (t <= 2.685)
            y = 0.852599 + 0.0225423 * (t - 2.22) - 3.470596e-01
                    * Math.pow(t - 2.22, 2) + 0.240586 * Math.pow(t - 2.22, 3);
        else if (t <= 3.15)
            y = 0.812228 - 0.144161 * (t - 2.685) - 1.144258e-02
                    * Math.pow(t - 2.685, 2) + 0.0968603
                    * Math.pow(t - 2.685, 3);
        else if (t <= 4.62)
            y = 0.752458 - 0.0919719 * (t - 3.15) + 1.236776e-01
                    * Math.pow(t - 3.15, 2) - 0.0669395 * Math.pow(t - 3.15, 3);
        else if (t <= 5.085)
            y = 0.671879 - 0.162309 * (t - 4.62) - 1.715257e-01
                    * Math.pow(t - 4.62, 2) + 0.494727 * Math.pow(t - 4.62, 3);
        else if (t <= 5.55)
            y = 0.609059 - 0.00091066 * (t - 5.085) + 5.186183e-01
                    * Math.pow(t - 5.085, 2) - 0.394601
                    * Math.pow(t - 5.085, 3);
        else if (t <= 6.015)
            y = 0.681099 + 0.225436 * (t - 5.55) - 3.185070e-02
                    * Math.pow(t - 5.55, 2) + 0.0649101 * Math.pow(t - 5.55, 3);
        else if (t <= 6.477)
            y = 0.785567 + 0.237921 * (t - 6.015) + 5.869895e-02
                    * Math.pow(t - 6.015, 2) - 0.358464
                    * Math.pow(t - 6.015, 3);
        else if (t <= 6.942)
            y = 0.872666 + 0.0626224 * (t - 6.477) - 4.381325e-01
                    * Math.pow(t - 6.477, 2) + 0.378394
                    * Math.pow(t - 6.477, 3);
        else if (t <= 7.407)
            y = 0.845096 - 0.0993862 * (t - 6.942) + 8.972702e-02
                    * Math.pow(t - 6.942, 2) + 0.0910474
                    * Math.pow(t - 6.942, 3);
        else if (t <= 7.872)
            y = 0.827437 + 0.0431201 * (t - 7.407) + 2.167382e-01
                    * Math.pow(t - 7.407, 2) - 0.39346 * Math.pow(t - 7.407, 3);
        else if (t <= 8.877)
            y = 0.854792 - 0.0105413 * (t - 7.872) - 3.321390e-01
                    * Math.pow(t - 7.872, 2) + 0.164397
                    * Math.pow(t - 7.872, 3);
        else if (t <= 9.342)
            y = 0.675605 - 0.180004 * (t - 8.877) + 1.635190e-01
                    * Math.pow(t - 8.877, 2) + 0.121832
                    * Math.pow(t - 8.877, 3);
        else if (t <= 9.807)
            y = 0.639509 + 0.0510979 * (t - 9.342) + 3.334752e-01
                    * Math.pow(t - 9.342, 2) - 0.371766
                    * Math.pow(t - 9.342, 3);
        else if (t <= 10.272)
            y = 0.697996 + 0.120074 * (t - 9.807) - 1.851385e-01
                    * Math.pow(t - 9.807, 2) - 0.0565306
                    * Math.pow(t - 9.807, 3);
        else if (t <= 10.734)
            y = 0.708115 - 0.0887743 * (t - 10.272) - 2.639986e-01
                    * Math.pow(t - 10.272, 2) + 0.426366
                    * Math.pow(t - 10.272, 3);
        else if (t <= 11.199)
            y = 0.652797 - 0.059693 * (t - 10.734) + 3.269452e-01
                    * Math.pow(t - 10.734, 2) - 0.101866
                    * Math.pow(t - 10.734, 3);
        else if (t <= 11.664)
            y = 0.685492 + 0.178288 * (t - 11.199) + 1.848428e-01
                    * Math.pow(t - 11.199, 2) - 0.552669
                    * Math.pow(t - 11.199, 3);
        else if (t <= 12.129)
            y = 0.752796 - 0.00831021 * (t - 11.664) - 5.861302e-01
                    * Math.pow(t - 11.664, 2) + 0.303168
                    * Math.pow(t - 11.664, 3);
        else if (t <= 13.134)
            y = 0.652677 - 0.356754 * (t - 12.129) - 1.632107e-01
                    * Math.pow(t - 12.129, 2) + 0.250424
                    * Math.pow(t - 12.129, 3);
        else if (t <= 13.599)
            y = 0.383492 + 0.0739952 * (t - 13.134) + 5.918165e-01
                    * Math.pow(t - 13.134, 2) - 0.603056
                    * Math.pow(t - 13.134, 3);
        else if (t <= 14.064)
            y = 0.485231 + 0.233197 * (t - 13.599) - 2.494464e-01
                    * Math.pow(t - 13.599, 2) - 0.0945359
                    * Math.pow(t - 13.599, 3);
        else if (t <= 14.529)
            y = 0.530226 - 0.060111 * (t - 14.064) - 3.813240e-01
                    * Math.pow(t - 14.064, 2) + 0.240277
                    * Math.pow(t - 14.064, 3);
        else if (t <= 14.991)
            y = 0.443981 - 0.258881 * (t - 14.529) - 4.613781e-02
                    * Math.pow(t - 14.529, 2) + 0.380038
                    * Math.pow(t - 14.529, 3);
        else if (t <= 15.456)
            y = 0.352007 - 0.0581613 * (t - 14.991) + 4.805954e-01
                    * Math.pow(t - 14.991, 2) - 0.555397
                    * Math.pow(t - 14.991, 3);
        else if (t <= 15.921)
            y = 0.373036 + 0.0285202 * (t - 15.456) - 2.941836e-01
                    * Math.pow(t - 15.456, 2) - 0.0154123
                    * Math.pow(t - 15.456, 3);
        else if (t <= 16.386)
            y = 0.321138 - 0.255068 * (t - 15.921) - 3.156838e-01
                    * Math.pow(t - 15.921, 2) + 0.629819
                    * Math.pow(t - 15.921, 3);
        else if (t <= 17.391)
            y = 0.197598 - 0.140106 * (t - 16.386) + 5.629136e-01
                    * Math.pow(t - 16.386, 2) - 0.326519
                    * Math.pow(t - 16.386, 3);
        else if (t <= 17.856)
            y = 0.293907 + 0.00197248 * (t - 17.391) - 4.215417e-01
                    * Math.pow(t - 17.391, 2) + 0.315744
                    * Math.pow(t - 17.391, 3);
        else if (t <= 18.321)
            y = 0.235422 - 0.185246 * (t - 17.856) + 1.892171e-02
                    * Math.pow(t - 17.856, 2) + 0.145936
                    * Math.pow(t - 17.856, 3);
        else if (t <= 18.786)
            y = 0.168047 - 0.0729833 * (t - 18.321) + 2.225031e-01
                    * Math.pow(t - 18.321, 2) - 0.134407
                    * Math.pow(t - 18.321, 3);
        else if (t <= 19.248)
            y = 0.168707 + 0.0467579 * (t - 18.786) + 3.500489e-02
                    * Math.pow(t - 18.786, 2) - 0.106218
                    * Math.pow(t - 18.786, 3);
        else if (t <= 19.713)
            y = 0.187306 + 0.0110874 * (t - 19.248) - 1.122138e-01
                    * Math.pow(t - 19.248, 2) + 0.203081
                    * Math.pow(t - 19.248, 3);
        else if (t <= 20.178)
            y = 0.188617 + 0.0384625 * (t - 19.713) + 1.710848e-01
                    * Math.pow(t - 19.713, 2) - 0.237274
                    * Math.pow(t - 19.713, 3);
        else if (t <= 20.643)
            y = 0.219639 + 0.0436577 * (t - 20.178) - 1.599124e-01
                    * Math.pow(t - 20.178, 2) + 0.114633
                    * Math.pow(t - 20.178, 3);

        return y * (13.0516 - 4.4648) + 4.4648;
    }

    /**
     * Computes the approximation spline for ala or returns the default value
     * 1.05 if t is not in the measured time intervall. Parameter for the
     * spline: weights all equal to one, S=5.9718 (= 2*(max(y) - min(y))
     *
     * @param t
     * @return
     */
    public double getAla(double t) {
        double y = this.ala;

        if ((-3.894 <= t) && (t <= -3.429))
            y = 0.0111417 + 0.0976193 * (t + 3.894) + 0
                    * Math.pow(t + 3.894, 2) - 0.0137179
                    * Math.pow(t + 3.894, 3);
        else if (t <= -2.964)
            y = 0.0551554 + 0.0887208 * (t + 3.429) - 1.913651e-02
                    * Math.pow(t + 3.429, 2) - 0.0163295
                    * Math.pow(t + 3.429, 3);
        else if (t <= -2.499)
            y = 0.0906309 + 0.0603313 * (t + 2.964) - 4.191618e-02
                    * Math.pow(t + 2.964, 2) - 0.0165692
                    * Math.pow(t + 2.964, 3);
        else if (t <= -2.037)
            y = 0.107956 + 0.0106012 * (t + 2.499) - 6.503016e-02
                    * Math.pow(t + 2.499, 2) + 0.0299227
                    * Math.pow(t + 2.499, 3);
        else if (t <= -1.572)
            y = 0.101924 - 0.0303262 * (t + 2.037) - 2.355734e-02
                    * Math.pow(t + 2.037, 2) + 0.033286
                    * Math.pow(t + 2.037, 3);
        else if (t <= -1.107)
            y = 0.0860752 - 0.0306427 * (t + 1.572) + 2.287664e-02
                    * Math.pow(t + 1.572, 2) - 0.0105259
                    * Math.pow(t + 1.572, 3);
        else if (t <= -0.642)
            y = 0.0757145 - 0.0161953 * (t + 1.107) + 8.193019e-03
                    * Math.pow(t + 1.107, 2) + 0.0192259
                    * Math.pow(t + 1.107, 3);
        else if (t <= 0.363)
            y = 0.0718883 + 0.00389554 * (t + 0.642) + 3.501316e-02
                    * Math.pow(t + 0.642, 2) + 0.045535
                    * Math.pow(t + 0.642, 3);
        else if (t <= 0.828)
            y = 0.157389 + 0.212246 * (t - 0.363) + 1.723011e-01
                    * Math.pow(t - 0.363, 2) - 0.0393216
                    * Math.pow(t - 0.363, 3);
        else if (t <= 1.293)
            y = 0.289386 + 0.346979 * (t - 0.828) + 1.174474e-01
                    * Math.pow(t - 0.828, 2) - 0.234153
                    * Math.pow(t - 0.828, 3);
        else if (t <= 1.758)
            y = 0.452583 + 0.304317 * (t - 1.293) - 2.091957e-01
                    * Math.pow(t - 1.293, 2) + 0.0742497
                    * Math.pow(t - 1.293, 3);
        else if (t <= 2.22)
            y = 0.556323 + 0.157928 * (t - 1.758) - 1.056173e-01
                    * Math.pow(t - 1.758, 2) + 0.0351834
                    * Math.pow(t - 1.758, 3);
        else if (t <= 2.685)
            y = 0.610212 + 0.0828671 * (t - 2.22) - 5.685313e-02
                    * Math.pow(t - 2.22, 2) - 0.0065701 * Math.pow(t - 2.22, 3);
        else if (t <= 3.15)
            y = 0.635791 + 0.0257318 * (t - 2.685) - 6.601841e-02
                    * Math.pow(t - 2.685, 2) + 0.0430014
                    * Math.pow(t - 2.685, 3);
        else if (t <= 4.62)
            y = 0.637805 - 0.00777138 * (t - 3.15) - 6.031459e-03
                    * Math.pow(t - 3.15, 2) + 0.0091822 * Math.pow(t - 3.15, 3);
        else if (t <= 5.085)
            y = 0.642515 + 0.0340216 * (t - 4.62) + 3.446205e-02
                    * Math.pow(t - 4.62, 2) - 0.000758179
                    * Math.pow(t - 4.62, 3);
        else if (t <= 5.55)
            y = 0.665711 + 0.0655795 * (t - 5.085) + 3.340439e-02
                    * Math.pow(t - 5.085, 2) - 0.104524
                    * Math.pow(t - 5.085, 3);
        else if (t <= 6.015)
            y = 0.692919 + 0.0288435 * (t - 5.55) - 1.124066e-01
                    * Math.pow(t - 5.55, 2) + 0.0559302 * Math.pow(t - 5.55, 3);
        else if (t <= 6.477)
            y = 0.687649 - 0.0394141 * (t - 6.015) - 3.438390e-02
                    * Math.pow(t - 6.015, 2) + 0.0394099
                    * Math.pow(t - 6.015, 3);
        else if (t <= 6.942)
            y = 0.665987 - 0.0459494 * (t - 6.477) + 2.023820e-02
                    * Math.pow(t - 6.477, 2) + 0.0644611
                    * Math.pow(t - 6.477, 3);
        else if (t <= 7.407)
            y = 0.655478 + 0.0146864 * (t - 6.942) + 1.101615e-01
                    * Math.pow(t - 6.942, 2) - 0.0584419
                    * Math.pow(t - 6.942, 3);
        else if (t <= 7.872)
            y = 0.680251 + 0.0792268 * (t - 7.407) + 2.863499e-02
                    * Math.pow(t - 7.407, 2) - 0.105164
                    * Math.pow(t - 7.407, 3);
        else if (t <= 8.877)
            y = 0.712709 + 0.0376398 * (t - 7.872) - 1.180694e-01
                    * Math.pow(t - 7.872, 2) + 0.075549
                    * Math.pow(t - 7.872, 3);
        else if (t <= 9.342)
            y = 0.707972 + 0.0292394 * (t - 8.877) + 1.097108e-01
                    * Math.pow(t - 8.877, 2) - 0.0180929
                    * Math.pow(t - 8.877, 3);
        else if (t <= 9.807)
            y = 0.743471 + 0.119534 * (t - 9.342) + 8.447119e-02
                    * Math.pow(t - 9.342, 2) - 0.106349
                    * Math.pow(t - 9.342, 3);
        else if (t <= 10.272)
            y = 0.806627 + 0.129106 * (t - 9.807) - 6.388543e-02
                    * Math.pow(t - 9.807, 2) - 0.0296704
                    * Math.pow(t - 9.807, 3);
        else if (t <= 10.734)
            y = 0.849864 + 0.0504465 * (t - 10.272) - 1.052756e-01
                    * Math.pow(t - 10.272, 2) + 0.102264
                    * Math.pow(t - 10.272, 3);
        else if (t <= 11.199)
            y = 0.860785 + 0.0186546 * (t - 10.734) + 3.646185e-02
                    * Math.pow(t - 10.734, 2) - 0.0173023
                    * Math.pow(t - 10.734, 3);
        else if (t <= 11.664)
            y = 0.875603 + 0.0413405 * (t - 11.199) + 1.232517e-02
                    * Math.pow(t - 11.199, 2) - 0.0739829
                    * Math.pow(t - 11.199, 3);
        else if (t <= 12.129)
            y = 0.890053 + 0.00481211 * (t - 11.664) - 9.088094e-02
                    * Math.pow(t - 11.664, 2) - 0.0193171
                    * Math.pow(t - 11.664, 3);
        else if (t <= 13.134)
            y = 0.870698 - 0.0922377 * (t - 12.129) - 1.178283e-01
                    * Math.pow(t - 12.129, 2) + 0.119448
                    * Math.pow(t - 12.129, 3);
        else if (t <= 13.599)
            y = 0.780238 + 0.0328649 * (t - 13.134) + 2.423084e-01
                    * Math.pow(t - 13.134, 2) - 0.245131
                    * Math.pow(t - 13.134, 3);
        else if (t <= 14.064)
            y = 0.823267 + 0.0992016 * (t - 13.599) - 9.964895e-02
                    * Math.pow(t - 13.599, 2) - 0.0802879
                    * Math.pow(t - 13.599, 3);
        else if (t <= 14.529)
            y = 0.839777 - 0.0455527 * (t - 14.064) - 2.116505e-01
                    * Math.pow(t - 14.064, 2) + 0.116983
                    * Math.pow(t - 14.064, 3);
        else if (t <= 14.991)
            y = 0.784593 - 0.166504 * (t - 14.529) - 4.845904e-02
                    * Math.pow(t - 14.529, 2) + 0.155386
                    * Math.pow(t - 14.529, 3);
        else if (t <= 15.456)
            y = 0.712648 - 0.111781 * (t - 14.991) + 1.669064e-01
                    * Math.pow(t - 14.991, 2) - 0.193567
                    * Math.pow(t - 14.991, 3);
        else if (t <= 15.921)
            y = 0.677297 - 0.0821204 * (t - 15.456) - 1.031202e-01
                    * Math.pow(t - 15.456, 2) + 0.00967204
                    * Math.pow(t - 15.456, 3);
        else if (t <= 16.386)
            y = 0.617786 - 0.171748 * (t - 15.921) - 8.962768e-02
                    * Math.pow(t - 15.921, 2) + 0.166928
                    * Math.pow(t - 15.921, 3);
        else if (t <= 17.391)
            y = 0.535327 - 0.14682 * (t - 16.386) + 1.432362e-01
                    * Math.pow(t - 16.386, 2) - 0.0670531
                    * Math.pow(t - 16.386, 3);
        else if (t <= 17.856)
            y = 0.464381 - 0.0620913 * (t - 17.391) - 5.892878e-02
                    * Math.pow(t - 17.391, 2) + 0.0720023
                    * Math.pow(t - 17.391, 3);
        else if (t <= 18.321)
            y = 0.430006 - 0.0701889 * (t - 17.856) + 4.151449e-02
                    * Math.pow(t - 17.856, 2) + 0.104259
                    * Math.pow(t - 17.856, 3);
        else if (t <= 18.786)
            y = 0.416827 + 0.0360499 * (t - 18.321) + 1.869560e-01
                    * Math.pow(t - 18.321, 2) - 0.13932
                    * Math.pow(t - 18.321, 3);
        else if (t <= 19.248)
            y = 0.460007 + 0.119546 * (t - 18.786) - 7.395260e-03
                    * Math.pow(t - 18.786, 2) - 0.0936091
                    * Math.pow(t - 18.786, 3);
        else if (t <= 19.713)
            y = 0.504428 + 0.0527714 * (t - 19.248) - 1.371375e-01
                    * Math.pow(t - 19.248, 2) + 0.0750712
                    * Math.pow(t - 19.248, 3);
        else if (t <= 20.178)
            y = 0.506862 - 0.0260696 * (t - 19.713) - 3.241318e-02
                    * Math.pow(t - 19.713, 2) - 0.00426263
                    * Math.pow(t - 19.713, 3);
        else if (t <= 20.643)
            y = 0.487302 - 0.058979 * (t - 20.178) - 3.835956e-02
                    * Math.pow(t - 20.178, 2) + 0.0274979
                    * Math.pow(t - 20.178, 3);

        return y * (3.8009 - .8150) + .8150;
    }

    /**
     * Computes the spline equation for pyr with the parameter S=2.5 and all
     * weights equal to one. For values of t smaller or greater than the
     * considered time the default steady-state value of 0.689 mM will be
     * returned.
     *
     * @param t
     * @return y the concentration of pyr at the given time t.
     */
    public double getPyr(double t) {
        double y = this.pyr;

        if ((-3.894 <= t) && (t <= -3.429))
            y = 0.0468378 - 0.0255021 * (t + 3.894) + 0
                    * Math.pow(t + 3.894, 2) + 0.00825634
                    * Math.pow(t + 3.894, 3);
        else if (t <= -2.964)
            y = 0.0358095 - 0.0201464 * (t + 3.429) + 1.151759e-02
                    * Math.pow(t + 3.429, 2) + 0.0344174
                    * Math.pow(t + 3.429, 3);
        else if (t <= -2.499)
            y = 0.0323923 + 0.0128907 * (t + 2.964) + 5.952992e-02
                    * Math.pow(t + 2.964, 2) - 0.0132711
                    * Math.pow(t + 2.964, 3);
        else if (t <= -2.037)
            y = 0.0499239 + 0.0596449 * (t + 2.499) + 4.101675e-02
                    * Math.pow(t + 2.499, 2) - 0.0960409
                    * Math.pow(t + 2.499, 3);
        else if (t <= -1.572)
            y = 0.076764 + 0.0360463 * (t + 2.037) - 9.209594e-02
                    * Math.pow(t + 2.037, 2) - 0.0232984
                    * Math.pow(t + 2.037, 3);
        else if (t <= -1.107)
            y = 0.0712695 - 0.0647161 * (t + 1.572) - 1.245973e-01
                    * Math.pow(t + 1.572, 2) + 0.10468 * Math.pow(t + 1.572, 3);
        else if (t <= -0.642)
            y = 0.0247605 - 0.112688 * (t + 1.107) + 2.143171e-02
                    * Math.pow(t + 1.107, 2) + 0.0271642
                    * Math.pow(t + 1.107, 3);
        else if (t <= 0.363)
            y = -0.0202741 - 0.0751358 * (t + 0.642) + 5.932581e-02
                    * Math.pow(t + 0.642, 2) + 0.10294 * Math.pow(t + 0.642, 3);
        else if (t <= 0.828)
            y = 0.0686263 + 0.356024 * (t - 0.363) + 3.696886e-01
                    * Math.pow(t - 0.363, 2) + 0.0489817
                    * Math.pow(t - 0.363, 3);
        else if (t <= 1.293)
            y = 0.319038 + 0.731607 * (t - 0.828) + 4.380181e-01
                    * Math.pow(t - 0.828, 2) - 0.897988
                    * Math.pow(t - 0.828, 3);
        else if (t <= 1.758)
            y = 0.663658 + 0.556462 * (t - 1.293) - 8.146750e-01
                    * Math.pow(t - 1.293, 2) + 0.359103
                    * Math.pow(t - 1.293, 3);
        else if (t <= 2.22)
            y = 0.782366 + 0.031755 * (t - 1.758) - 3.137269e-01
                    * Math.pow(t - 1.758, 2) + 0.302952
                    * Math.pow(t - 1.758, 3);
        else if (t <= 2.685)
            y = 0.759948 - 0.064139 * (t - 2.22) + 1.061641e-01
                    * Math.pow(t - 2.22, 2) - 0.0663651 * Math.pow(t - 2.22, 3);
        else if (t <= 3.15)
            y = 0.746406 - 0.00845576 * (t - 2.685) + 1.358482e-02
                    * Math.pow(t - 2.685, 2) - 0.0362597
                    * Math.pow(t - 2.685, 3);
        else if (t <= 4.62)
            y = 0.741766 - 0.0193426 * (t - 3.15) - 3.699747e-02
                    * Math.pow(t - 3.15, 2) + 0.00191105
                    * Math.pow(t - 3.15, 3);
        else if (t <= 5.085)
            y = 0.639454 - 0.115726 * (t - 4.62) - 2.856973e-02
                    * Math.pow(t - 4.62, 2) + 0.206882 * Math.pow(t - 4.62, 3);
        else if (t <= 5.55)
            y = 0.600265 - 0.00809734 * (t - 5.085) + 2.600302e-01
                    * Math.pow(t - 5.085, 2) - 0.229079
                    * Math.pow(t - 5.085, 3);
        else if (t <= 6.015)
            y = 0.629692 + 0.0851332 * (t - 5.55) - 5.953446e-02
                    * Math.pow(t - 5.55, 2) - 0.0383844 * Math.pow(t - 5.55, 3);
        else if (t <= 6.477)
            y = 0.652547 + 0.00486712 * (t - 6.015) - 1.130807e-01
                    * Math.pow(t - 6.015, 2) + 0.0637543
                    * Math.pow(t - 6.015, 3);
        else if (t <= 6.942)
            y = 0.636946 - 0.0587955 * (t - 6.477) - 2.471716e-02
                    * Math.pow(t - 6.477, 2) + 0.0556625
                    * Math.pow(t - 6.477, 3);
        else if (t <= 7.407)
            y = 0.609858 - 0.0456756 * (t - 6.942) + 5.293199e-02
                    * Math.pow(t - 6.942, 2) + 0.117416
                    * Math.pow(t - 6.942, 3);
        else if (t <= 7.872)
            y = 0.61187 + 0.0797159 * (t - 7.407) + 2.167272e-01
                    * Math.pow(t - 7.407, 2) - 0.30684 * Math.pow(t - 7.407, 3);
        else if (t <= 8.877)
            y = 0.664948 + 0.0822328 * (t - 7.872) - 2.113145e-01
                    * Math.pow(t - 7.872, 2) + 0.123173
                    * Math.pow(t - 7.872, 3);
        else if (t <= 9.342)
            y = 0.659189 + 0.0307141 * (t - 8.877) + 1.600521e-01
                    * Math.pow(t - 8.877, 2) - 0.0857489
                    * Math.pow(t - 8.877, 3);
        else if (t <= 9.807)
            y = 0.699457 + 0.123939 * (t - 9.342) + 4.043249e-02
                    * Math.pow(t - 9.342, 2) - 0.159848
                    * Math.pow(t - 9.342, 3);
        else if (t <= 10.272)
            y = 0.74976 + 0.0578521 * (t - 9.807) - 1.825560e-01
                    * Math.pow(t - 9.807, 2) + 0.0836857
                    * Math.pow(t - 9.807, 3);
        else if (t <= 10.734)
            y = 0.745602 - 0.0576401 * (t - 10.272) - 6.581435e-02
                    * Math.pow(t - 10.272, 2) + 0.200744
                    * Math.pow(t - 10.272, 3);
        else if (t <= 11.199)
            y = 0.72472 + 0.0100902 * (t - 10.734) + 2.124167e-01
                    * Math.pow(t - 10.734, 2) - 0.18139
                    * Math.pow(t - 10.734, 3);
        else if (t <= 11.664)
            y = 0.757104 + 0.0899747 * (t - 11.199) - 4.062200e-02
                    * Math.pow(t - 11.199, 2) - 0.307885
                    * Math.pow(t - 11.199, 3);
        else if (t <= 12.129)
            y = 0.759203 - 0.147521 * (t - 11.664) - 4.701216e-01
                    * Math.pow(t - 11.664, 2) + 0.34237
                    * Math.pow(t - 11.664, 3);
        else if (t <= 13.134)
            y = 0.623377 - 0.362647 * (t - 12.129) + 7.484435e-03
                    * Math.pow(t - 12.129, 2) + 0.104455
                    * Math.pow(t - 12.129, 3);
        else if (t <= 13.599)
            y = 0.372505 - 0.0310977 * (t - 13.134) + 3.224157e-01
                    * Math.pow(t - 13.134, 2) - 0.260175
                    * Math.pow(t - 13.134, 3);
        else if (t <= 14.064)
            y = 0.4016 + 0.0999801 * (t - 13.599) - 4.052806e-02
                    * Math.pow(t - 13.599, 2) - 0.0959657
                    * Math.pow(t - 13.599, 3);
        else if (t <= 14.529)
            y = 0.429678 + 3.84408e-05 * (t - 14.064) - 1.744002e-01
                    * Math.pow(t - 14.064, 2) + 0.126879
                    * Math.pow(t - 14.064, 3);
        else if (t <= 14.991)
            y = 0.404744 - 0.0798503 * (t - 14.529) + 2.596388e-03
                    * Math.pow(t - 14.529, 2) + 0.278383
                    * Math.pow(t - 14.529, 3);
        else if (t <= 15.456)
            y = 0.395859 + 0.100806 * (t - 14.991) + 3.884357e-01
                    * Math.pow(t - 14.991, 2) - 0.614904
                    * Math.pow(t - 14.991, 3);
        else if (t <= 15.921)
            y = 0.464898 + 0.0631788 * (t - 15.456) - 4.693555e-01
                    * Math.pow(t - 15.456, 2) + 0.275445
                    * Math.pow(t - 15.456, 3);
        else if (t <= 16.386)
            y = 0.420484 - 0.194647 * (t - 15.921) - 8.510932e-02
                    * Math.pow(t - 15.921, 2) + 0.240611
                    * Math.pow(t - 15.921, 3);
        else if (t <= 17.391)
            y = 0.335763 - 0.117721 * (t - 16.386) + 2.505425e-01
                    * Math.pow(t - 16.386, 2) - 0.156845
                    * Math.pow(t - 16.386, 3);
        else if (t <= 17.856)
            y = 0.311298 - 0.0893822 * (t - 17.391) - 2.223447e-01
                    * Math.pow(t - 17.391, 2) + 0.222271
                    * Math.pow(t - 17.391, 3);
        else if (t <= 18.786)
            y = 0.244007 - 0.151981 * (t - 17.856) + 8.772290e-02
                    * Math.pow(t - 17.856, 2) + 0.0427783
                    * Math.pow(t - 17.856, 3);
        else if (t <= 19.248)
            y = 0.212945 + 0.12218 * (t - 18.786) + 2.070743e-01
                    * Math.pow(t - 18.786, 2) - 0.297219
                    * Math.pow(t - 18.786, 3);
        else if (t <= 19.713)
            y = 0.284281 + 0.123198 * (t - 19.248) - 2.048717e-01
                    * Math.pow(t - 19.248, 2) + 0.0274136
                    * Math.pow(t - 19.248, 3);
        else if (t <= 20.178)
            y = 0.300026 - 0.0495505 * (t - 19.713) - 1.666297e-01
                    * Math.pow(t - 19.713, 2) + 0.134468
                    * Math.pow(t - 19.713, 3);
        else if (t <= 20.643)
            y = 0.254476 - 0.11729 * (t - 20.178) + 2.095292e-02
                    * Math.pow(t - 20.178, 2) - 0.01502
                    * Math.pow(t - 20.178, 3);

        return y * (2.3507 - 0.6144) + 0.6144; // Rücktransformation.
    }

    /**
     * Computes the approximation spline for glut or returns the default value
     * 38.7 mM if t is not in the measured time intervall. Parameter for the
     * spline: weights all equal to one, S=798.4982 (= 16.5*(max(y) - min(y))).
     *
     * @param t
     * @return
     */
    public double getGlut(double t) {
        double y = this.glut;

        if ((-3.894 <= t) && (t <= -3.429))
            y = 0.346152 + 0.0125877 * (t + 3.894) + 0 * Math.pow(t + 3.894, 2)
                    + 0.00386753 * Math.pow(t + 3.894, 3);
        else if (t <= -2.964)
            y = 0.352394 + 0.0150965 * (t + 3.429) + 5.395202e-03
                    * Math.pow(t + 3.429, 2) - 0.000730397
                    * Math.pow(t + 3.429, 3);
        else if (t <= -2.499)
            y = 0.360507 + 0.0196402 * (t + 2.964) + 4.376298e-03
                    * Math.pow(t + 2.964, 2) - 0.00400789
                    * Math.pow(t + 2.964, 3);
        else if (t <= -2.037)
            y = 0.370183 + 0.0211104 * (t + 2.499) - 1.214705e-03
                    * Math.pow(t + 2.499, 2) + 0.00141752
                    * Math.pow(t + 2.499, 3);
        else if (t <= -1.572)
            y = 0.379816 + 0.0208957 * (t + 2.037) + 7.499822e-04
                    * Math.pow(t + 2.037, 2) + 0.000813723
                    * Math.pow(t + 2.037, 3);
        else if (t <= -1.107)
            y = 0.389777 + 0.022121 * (t + 1.572) + 1.885125e-03
                    * Math.pow(t + 1.572, 2) + 0.00104503
                    * Math.pow(t + 1.572, 3);
        else if (t <= -0.642)
            y = 0.400576 + 0.0245521 * (t + 1.107) + 3.342938e-03
                    * Math.pow(t + 1.107, 2) - 0.00292541
                    * Math.pow(t + 1.107, 3);
        else if (t <= 0.363)
            y = 0.412421 + 0.0257634 * (t + 0.642) - 7.380070e-04
                    * Math.pow(t + 0.642, 2) - 0.00118342
                    * Math.pow(t + 0.642, 3);
        else if (t <= 0.828)
            y = 0.436367 + 0.0206941 * (t - 0.363) - 4.306028e-03
                    * Math.pow(t - 0.363, 2) - 0.00258859
                    * Math.pow(t - 0.363, 3);
        else if (t <= 1.293)
            y = 0.444798 + 0.0150103 * (t - 0.828) - 7.917118e-03
                    * Math.pow(t - 0.828, 2) - 1.90838e-05
                    * Math.pow(t - 0.828, 3);
        else if (t <= 1.758)
            y = 0.450064 + 0.00763503 * (t - 1.293) - 7.943740e-03
                    * Math.pow(t - 1.293, 2) + 0.00394034
                    * Math.pow(t - 1.293, 3);
        else if (t <= 2.22)
            y = 0.452293 + 0.00280336 * (t - 1.758) - 2.446961e-03
                    * Math.pow(t - 1.758, 2) - 0.000120479
                    * Math.pow(t - 1.758, 3);
        else if (t <= 2.685)
            y = 0.453054 + 0.00046522 * (t - 2.22) - 2.613945e-03
                    * Math.pow(t - 2.22, 2) + 0.00947111
                    * Math.pow(t - 2.22, 3);
        else if (t <= 3.15)
            y = 0.453657 + 0.00417793 * (t - 2.685) + 1.059826e-02
                    * Math.pow(t - 2.685, 2) + 0.00512564
                    * Math.pow(t - 2.685, 3);
        else if (t <= 4.62)
            y = 0.458407 + 0.0173592 * (t - 3.15) + 1.774853e-02
                    * Math.pow(t - 3.15, 2) - 0.000709047
                    * Math.pow(t - 3.15, 3);
        else if (t <= 5.085)
            y = 0.520026 + 0.0649433 * (t - 4.62) + 1.462163e-02
                    * Math.pow(t - 4.62, 2) - 0.00895174
                    * Math.pow(t - 4.62, 3);
        else if (t <= 5.55)
            y = 0.552486 + 0.0727347 * (t - 5.085) + 2.133952e-03
                    * Math.pow(t - 5.085, 2) - 0.0005377
                    * Math.pow(t - 5.085, 3);
        else if (t <= 6.015)
            y = 0.586715 + 0.0743704 * (t - 5.55) + 1.383860e-03
                    * Math.pow(t - 5.55, 2) + 0.000484168
                    * Math.pow(t - 5.55, 3);
        else if (t <= 6.477)
            y = 0.621645 + 0.0759715 * (t - 6.015) + 2.059275e-03
                    * Math.pow(t - 6.015, 2) - 0.00426907
                    * Math.pow(t - 6.015, 3);
        else if (t <= 6.942)
            y = 0.656762 + 0.0751406 * (t - 6.477) - 3.857662e-03
                    * Math.pow(t - 6.477, 2) - 0.00352595
                    * Math.pow(t - 6.477, 3);
        else if (t <= 7.407)
            y = 0.690514 + 0.0692658 * (t - 6.942) - 8.776359e-03
                    * Math.pow(t - 6.942, 2) - 0.00658733
                    * Math.pow(t - 6.942, 3);
        else if (t <= 7.872)
            y = 0.720163 + 0.0568308 * (t - 7.407) - 1.796568e-02
                    * Math.pow(t - 7.407, 2) - 0.00779921
                    * Math.pow(t - 7.407, 3);
        else if (t <= 8.877)
            y = 0.74192 + 0.0350635 * (t - 7.872) - 2.884559e-02
                    * Math.pow(t - 7.872, 2) + 0.0113354
                    * Math.pow(t - 7.872, 3);
        else if (t <= 9.342)
            y = 0.759531 + 0.0114309 * (t - 8.877) + 5.330512e-03
                    * Math.pow(t - 8.877, 2) + 0.00795504
                    * Math.pow(t - 8.877, 3);
        else if (t <= 9.807)
            y = 0.766798 + 0.0215485 * (t - 9.342) + 1.642779e-02
                    * Math.pow(t - 9.342, 2) - 0.0127692
                    * Math.pow(t - 9.342, 3);
        else if (t <= 10.272)
            y = 0.779087 + 0.0285432 * (t - 9.807) - 1.385314e-03
                    * Math.pow(t - 9.807, 2) - 0.0138529
                    * Math.pow(t - 9.807, 3);
        else if (t <= 10.734)
            y = 0.790667 + 0.0182689 * (t - 10.272) - 2.071006e-02
                    * Math.pow(t - 10.272, 2) + 0.00135784
                    * Math.pow(t - 10.272, 3);
        else if (t <= 11.199)
            y = 0.794821 + 2.26621e-06 * (t - 10.734) - 1.882810e-02
                    * Math.pow(t - 10.734, 2) - 0.0030271
                    * Math.pow(t - 10.734, 3);
        else if (t <= 11.664)
            y = 0.790446 - 0.0194715 * (t - 11.199) - 2.305090e-02
                    * Math.pow(t - 11.199, 2) - 0.00202981
                    * Math.pow(t - 11.199, 3);
        else if (t <= 12.129)
            y = 0.776204 - 0.0422255 * (t - 11.664) - 2.588248e-02
                    * Math.pow(t - 11.664, 2) + 0.00539561
                    * Math.pow(t - 11.664, 3);
        else if (t <= 13.134)
            y = 0.751515 - 0.0627962 * (t - 12.129) - 1.835561e-02
                    * Math.pow(t - 12.129, 2) + 0.00129954
                    * Math.pow(t - 12.129, 3);
        else if (t <= 13.599)
            y = 0.671184 - 0.0957533 * (t - 13.134) - 1.443750e-02
                    * Math.pow(t - 13.134, 2) - 0.00348381
                    * Math.pow(t - 13.134, 3);
        else if (t <= 14.064)
            y = 0.623187 - 0.11144 * (t - 13.599) - 1.929741e-02
                    * Math.pow(t - 13.599, 2) - 0.00494472
                    * Math.pow(t - 13.599, 3);
        else if (t <= 14.529)
            y = 0.566697 - 0.132594 * (t - 14.064) - 2.619529e-02
                    * Math.pow(t - 14.064, 2) + 0.00487863
                    * Math.pow(t - 14.064, 3);
        else if (t <= 14.991)
            y = 0.499868 - 0.153791 * (t - 14.529) - 1.938961e-02
                    * Math.pow(t - 14.529, 2) + 0.00949794
                    * Math.pow(t - 14.529, 3);
        else if (t <= 15.456)
            y = 0.425614 - 0.165625 * (t - 14.991) - 6.225460e-03
                    * Math.pow(t - 14.991, 2) + 0.0139063
                    * Math.pow(t - 14.991, 3);
        else if (t <= 15.921)
            y = 0.348651 - 0.162394 * (t - 15.456) + 1.317376e-02
                    * Math.pow(t - 15.456, 2) + 0.0117237
                    * Math.pow(t - 15.456, 3);
        else if (t <= 16.386)
            y = 0.277164 - 0.142538 * (t - 15.921) + 2.952836e-02
                    * Math.pow(t - 15.921, 2) + 0.00969481
                    * Math.pow(t - 15.921, 3);
        else if (t <= 17.391)
            y = 0.218244 - 0.108788 * (t - 16.386) + 4.305262e-02
                    * Math.pow(t - 16.386, 2) - 0.00229767
                    * Math.pow(t - 16.386, 3);
        else if (t <= 17.856)
            y = 0.150064 - 0.029214 * (t - 17.391) + 3.612515e-02
                    * Math.pow(t - 17.391, 2) - 0.0043217
                    * Math.pow(t - 17.391, 3);
        else if (t <= 18.321)
            y = 0.143856 + 0.00157903 * (t - 17.856) + 3.009639e-02
                    * Math.pow(t - 17.856, 2) - 0.00908315
                    * Math.pow(t - 17.856, 3);
        else if (t <= 18.786)
            y = 0.150185 + 0.0236767 * (t - 18.321) + 1.742540e-02
                    * Math.pow(t - 18.321, 2) - 0.0202182
                    * Math.pow(t - 18.321, 3);
        else if (t <= 19.248)
            y = 0.16293 + 0.0267673 * (t - 18.786) - 1.077895e-02
                    * Math.pow(t - 18.786, 2) - 0.00537863
                    * Math.pow(t - 18.786, 3);
        else if (t <= 19.713)
            y = 0.172465 + 0.0133634 * (t - 19.248) - 1.823373e-02
                    * Math.pow(t - 19.248, 2) + 0.0066547
                    * Math.pow(t - 19.248, 3);
        else if (t <= 20.178)
            y = 0.175405 + 0.000722759 * (t - 19.713) - 8.950425e-03
                    * Math.pow(t - 19.713, 2) + 0.00522627
                    * Math.pow(t - 19.713, 3);
        else if (t <= 20.643)
            y = 0.174332 - 0.00421098 * (t - 20.178) - 1.659774e-03
                    * Math.pow(t - 20.178, 2) + 0.0011898
                    * Math.pow(t - 20.178, 3);

        return y * (69.0310 - 20.6372) + 20.6372;
    }

    /**
     * Computes the approximation spline for nadp or returns the default value
     * 0.0175 mM if t is not in the measured time intervall. Parameter for the
     * spline: weights all equal to one, S=0.0003
     *
     * @param t
     * @return
     */
    public double getNADP(double t) {
        double y = this.nadp;

        if ((-3.429 <= t) && (t <= -2.964))
            y = 0.152191 - 0.100851 * (t + 3.429) + 0 * Math.pow(t + 3.429, 2)
                    + 0.359178 * Math.pow(t + 3.429, 3);
        else if (t <= -2.499)
            y = 0.141409 + 0.132138 * (t + 2.964) + 5.010527e-01
                    * Math.pow(t + 2.964, 2) - 0.430333
                    * Math.pow(t + 2.964, 3);
        else if (t <= -2.037)
            y = 0.267926 + 0.318971 * (t + 2.499) - 9.926155e-02
                    * Math.pow(t + 2.499, 2) - 0.234737
                    * Math.pow(t + 2.499, 3);
        else if (t <= -1.572)
            y = 0.370956 + 0.0769439 * (t + 2.037) - 4.246067e-01
                    * Math.pow(t + 2.037, 2) + 0.342541
                    * Math.pow(t + 2.037, 3);
        else if (t <= -1.107)
            y = 0.349365 - 0.0957427 * (t + 1.572) + 5.323777e-02
                    * Math.pow(t + 1.572, 2) + 0.0754021
                    * Math.pow(t + 1.572, 3);
        else if (t <= -0.642)
            y = 0.323937 + 0.00267988 * (t + 1.107) + 1.584237e-01
                    * Math.pow(t + 1.107, 2) + 0.223708
                    * Math.pow(t + 1.107, 3);
        else if (t <= 0.363)
            y = 0.381931 + 0.295128 * (t + 0.642) + 4.704962e-01
                    * Math.pow(t + 0.642, 2) - 0.426272
                    * Math.pow(t + 0.642, 3);
        else if (t <= 0.828)
            y = 0.721049 - 0.0508122 * (t - 0.363) - 8.147149e-01
                    * Math.pow(t - 0.363, 2) + 0.339015
                    * Math.pow(t - 0.363, 3);
        else if (t <= 1.293)
            y = 0.555345 - 0.588587 * (t - 0.828) - 3.417892e-01
                    * Math.pow(t - 0.828, 2) + 0.843539
                    * Math.pow(t - 0.828, 3);
        else if (t <= 1.758)
            y = 0.292562 - 0.359268 * (t - 1.293) + 8.349473e-01
                    * Math.pow(t - 1.293, 2) - 0.579768
                    * Math.pow(t - 1.293, 3);
        else if (t <= 2.22)
            y = 0.247747 + 0.0411519 * (t - 1.758) + 2.617095e-02
                    * Math.pow(t - 1.758, 2) + 0.311203
                    * Math.pow(t - 1.758, 3);
        else if (t <= 2.685)
            y = 0.303033 + 0.264607 * (t - 2.22) + 4.574978e-01
                    * Math.pow(t - 2.22, 2) - 1.27356 * Math.pow(t - 2.22, 3);
        else if (t <= 3.15)
            y = 0.396948 - 0.136044 * (t - 2.685) - 1.319114e+00
                    * Math.pow(t - 2.685, 2) + 1.5105 * Math.pow(t - 2.685, 3);
        else if (t <= 4.62)
            y = 0.200335 - 0.382996 * (t - 3.15) + 7.880343e-01
                    * Math.pow(t - 3.15, 2) - 0.244541 * Math.pow(t - 3.15, 3);
        else if (t <= 5.085)
            y = 0.563404 + 0.348539 * (t - 4.62) - 2.903913e-01
                    * Math.pow(t - 4.62, 2) + 0.122972 * Math.pow(t - 4.62, 3);
        else if (t <= 5.55)
            y = 0.675049 + 0.158244 * (t - 5.085) - 1.188447e-01
                    * Math.pow(t - 5.085, 2) - 0.177319
                    * Math.pow(t - 5.085, 3);
        else if (t <= 6.015)
            y = 0.705106 - 0.0673041 * (t - 5.55) - 3.662051e-01
                    * Math.pow(t - 5.55, 2) + 0.265379 * Math.pow(t - 5.55, 3);
        else if (t <= 6.477)
            y = 0.62131 - 0.23573 * (t - 6.015) + 3.998480e-03
                    * Math.pow(t - 6.015, 2) + 0.405744
                    * Math.pow(t - 6.015, 3);
        else if (t <= 6.942)
            y = 0.553267 + 0.0277755 * (t - 6.477) + 5.663602e-01
                    * Math.pow(t - 6.477, 2) - 0.572376
                    * Math.pow(t - 6.477, 3);
        else if (t <= 7.407)
            y = 0.631094 + 0.183205 * (t - 6.942) - 2.321041e-01
                    * Math.pow(t - 6.942, 2) - 0.283917
                    * Math.pow(t - 6.942, 3);
        else if (t <= 7.872)
            y = 0.637551 - 0.216822 * (t - 7.407) - 6.281689e-01
                    * Math.pow(t - 7.407, 2) + 1.27563 * Math.pow(t - 7.407, 3);
        else if (t <= 8.877)
            y = 0.529161 + 0.026452 * (t - 7.872) + 1.151340e+00
                    * Math.pow(t - 7.872, 2) - 0.845774
                    * Math.pow(t - 7.872, 3);
        else if (t <= 9.342)
            y = 0.860103 - 0.222113 * (t - 8.877) - 1.398668e+00
                    * Math.pow(t - 8.877, 2) + 1.2507 * Math.pow(t - 8.877, 3);
        else if (t <= 9.807)
            y = 0.580145 - 0.711574 * (t - 9.342) + 3.460638e-01
                    * Math.pow(t - 9.342, 2) + 0.300835
                    * Math.pow(t - 9.342, 3);
        else if (t <= 10.272)
            y = 0.354338 - 0.194591 * (t - 9.807) + 7.657292e-01
                    * Math.pow(t - 9.807, 2) - 0.442497
                    * Math.pow(t - 9.807, 3);
        else if (t <= 10.734)
            y = 0.384933 + 0.230501 * (t - 10.272) + 1.484461e-01
                    * Math.pow(t - 10.272, 2) - 0.0370582
                    * Math.pow(t - 10.272, 3);
        else if (t <= 11.199)
            y = 0.519455 + 0.343936 * (t - 10.734) + 9.708344e-02
                    * Math.pow(t - 10.734, 2) - 1.27811
                    * Math.pow(t - 10.734, 3);
        else if (t <= 11.664)
            y = 0.57187 - 0.394852 * (t - 11.199) - 1.685874e+00
                    * Math.pow(t - 11.199, 2) + 1.81482
                    * Math.pow(t - 11.199, 3);
        else if (t <= 12.129)
            y = 0.206206 - 0.785485 * (t - 11.664) + 8.458034e-01
                    * Math.pow(t - 11.664, 2) - 0.240625
                    * Math.pow(t - 11.664, 3);
        else if (t <= 13.134)
            y = -0.000354184 - 0.154975 * (t - 12.129) + 5.101319e-01
                    * Math.pow(t - 12.129, 2) - 0.235317
                    * Math.pow(t - 12.129, 3);
        else if (t <= 13.599)
            y = 0.120277 + 0.157362 * (t - 13.134) - 1.993487e-01
                    * Math.pow(t - 13.134, 2) + 0.0888371
                    * Math.pow(t - 13.134, 3);
        else if (t <= 14.064)
            y = 0.159278 + 0.0295938 * (t - 13.599) - 7.542095e-02
                    * Math.pow(t - 13.599, 2) + 0.192695
                    * Math.pow(t - 13.599, 3);
        else if (t <= 14.529)
            y = 0.176106 + 0.0844486 * (t - 14.064) + 1.933881e-01
                    * Math.pow(t - 14.064, 2) + 0.000891114
                    * Math.pow(t - 14.064, 3);
        else if (t <= 14.991)
            y = 0.257279 + 0.264878 * (t - 14.529) + 1.946312e-01
                    * Math.pow(t - 14.529, 2) - 0.374409
                    * Math.pow(t - 14.529, 3);
        else if (t <= 15.456)
            y = 0.384275 + 0.204971 * (t - 14.991) - 3.242994e-01
                    * Math.pow(t - 14.991, 2) - 0.320716
                    * Math.pow(t - 14.991, 3);
        else if (t <= 15.921)
            y = 0.377218 - 0.304668 * (t - 15.456) - 7.716985e-01
                    * Math.pow(t - 15.456, 2) + 1.15579
                    * Math.pow(t - 15.456, 3);
        else if (t <= 17.391)
            y = 0.184896 - 0.272614 * (t - 15.921) + 8.406312e-01
                    * Math.pow(t - 15.921, 2) - 0.356506
                    * Math.pow(t - 15.921, 3);
        else if (t <= 17.856)
            y = 0.468224 - 0.112279 * (t - 17.391) - 7.315592e-01
                    * Math.pow(t - 17.391, 2) + 0.588669
                    * Math.pow(t - 17.391, 3);
        else if (t <= 18.321)
            y = 0.317021 - 0.410774 * (t - 17.856) + 8.963419e-02
                    * Math.pow(t - 17.856, 2) + 0.674812
                    * Math.pow(t - 17.856, 3);
        else if (t <= 18.786)
            y = 0.213241 + 0.11032 * (t - 18.321) + 1.030997e+00
                    * Math.pow(t - 18.321, 2) - 1.08278
                    * Math.pow(t - 18.321, 3);
        else if (t <= 19.248)
            y = 0.378599 + 0.366776 * (t - 18.786) - 4.794776e-01
                    * Math.pow(t - 18.786, 2) + 0.632722
                    * Math.pow(t - 18.786, 3);
        else if (t <= 19.713)
            y = 0.508102 + 0.328891 * (t - 19.248) + 3.974756e-01
                    * Math.pow(t - 19.248, 2) - 1.44145
                    * Math.pow(t - 19.248, 3);
        else if (t <= 20.178)
            y = 0.60205 - 0.236491 * (t - 19.713) - 1.613351e+00
                    * Math.pow(t - 19.713, 2) + 1.89565
                    * Math.pow(t - 19.713, 3);
        else if (t <= 20.643)
            y = 0.333833 - 0.507245 * (t - 20.178) + 1.031084e+00
                    * Math.pow(t - 20.178, 2) - 0.739129
                    * Math.pow(t - 20.178, 3);

        return y * (0.0384 - 0.0130) + 0.0130;
    }

    /**
     * Computes the approximation spline for nad or returns the default value
     * 0.528 mM if t is not in the measured time intervall. Parameter for the
     * spline: weights all equal to one, S=0.0838 (= std(y))
     *
     * @param t
     * @return
     */
    public double getNAD(double t) {
        double y = this.nad;

        if ((-3.894 <= t) && (t <= -3.429))
            y = 0.26415 + 0.195236 * (t + 3.894) + 0 * Math.pow(t + 3.894, 2)
                    + 0.0529289 * Math.pow(t + 3.894, 3);
        else if (t <= -2.964)
            y = 0.360257 + 0.22957 * (t + 3.429) + 7.383579e-02
                    * Math.pow(t + 3.429, 2) - 0.09885 * Math.pow(t + 3.429, 3);
        else if (t <= -2.499)
            y = 0.473033 + 0.234116 * (t + 2.964) - 6.406001e-02
                    * Math.pow(t + 2.964, 2) - 0.0487923
                    * Math.pow(t + 2.964, 3);
        else if (t <= -2.037)
            y = 0.56314 + 0.142889 * (t + 2.499) - 1.321253e-01
                    * Math.pow(t + 2.499, 2) + 0.131891
                    * Math.pow(t + 2.499, 3);
        else if (t <= -1.572)
            y = 0.613959 + 0.105259 * (t + 2.037) + 5.067510e-02
                    * Math.pow(t + 2.037, 2) - 0.179837
                    * Math.pow(t + 2.037, 3);
        else if (t <= -1.107)
            y = 0.65578 + 0.0357315 * (t + 1.572) - 2.001977e-01
                    * Math.pow(t + 1.572, 2) + 0.210804
                    * Math.pow(t + 1.572, 3);
        else if (t <= -0.642)
            y = 0.650303 - 0.0137091 * (t + 1.107) + 9.387388e-02
                    * Math.pow(t + 1.107, 2) - 0.139083
                    * Math.pow(t + 1.107, 3);
        else if (t <= 0.363)
            y = 0.650242 - 0.0166259 * (t + 0.642) - 1.001466e-01
                    * Math.pow(t + 0.642, 2) + 0.0820861
                    * Math.pow(t + 0.642, 3);
        else if (t <= 0.828)
            y = 0.615706 + 0.0308066 * (t - 0.363) + 1.473431e-01
                    * Math.pow(t - 0.363, 2) - 0.108785
                    * Math.pow(t - 0.363, 3);
        else if (t <= 1.293)
            y = 0.650953 + 0.0972696 * (t - 0.828) - 4.411873e-03
                    * Math.pow(t - 0.828, 2) - 0.329967
                    * Math.pow(t - 0.828, 3);
        else if (t <= 1.758)
            y = 0.662053 - 0.120875 * (t - 1.293) - 4.647152e-01
                    * Math.pow(t - 1.293, 2) + 0.701928
                    * Math.pow(t - 1.293, 3);
        else if (t <= 2.22)
            y = 0.575938 - 0.0977365 * (t - 1.758) + 5.144744e-01
                    * Math.pow(t - 1.758, 2) - 0.803084
                    * Math.pow(t - 1.758, 3);
        else if (t <= 2.685)
            y = 0.561402 - 0.136602 * (t - 2.22) - 5.985996e-01
                    * Math.pow(t - 2.22, 2) + 0.239797 * Math.pow(t - 2.22, 3);
        else if (t <= 3.15)
            y = 0.39256 - 0.53775 * (t - 2.685) - 2.640832e-01
                    * Math.pow(t - 2.685, 2) + 0.516334
                    * Math.pow(t - 2.685, 3);
        else if (t <= 4.62)
            y = 0.13732 - 0.448414 * (t - 3.15) + 4.562028e-01
                    * Math.pow(t - 3.15, 2) - 0.0687614 * Math.pow(t - 3.15, 3);
        else if (t <= 5.085)
            y = 0.245537 + 0.447062 * (t - 4.62) + 1.529651e-01
                    * Math.pow(t - 4.62, 2) - 0.595339 * Math.pow(t - 4.62, 3);
        else if (t <= 5.55)
            y = 0.426638 + 0.203138 * (t - 5.085) - 6.775328e-01
                    * Math.pow(t - 5.085, 2) + 0.535904
                    * Math.pow(t - 5.085, 3);
        else if (t <= 6.015)
            y = 0.42848 - 0.0793393 * (t - 5.55) + 7.005394e-02
                    * Math.pow(t - 5.55, 2) + 0.682327 * Math.pow(t - 5.55, 3);
        else if (t <= 6.477)
            y = 0.475339 + 0.428419 * (t - 6.015) + 1.021900e+00
                    * Math.pow(t - 6.015, 2) - 1.40739 * Math.pow(t - 6.015, 3);
        else if (t <= 6.942)
            y = 0.752603 + 0.471457 * (t - 6.477) - 9.287462e-01
                    * Math.pow(t - 6.477, 2) + 0.605586
                    * Math.pow(t - 6.477, 3);
        else if (t <= 7.407)
            y = 0.8319 + 0.000551332 * (t - 6.942) - 8.395334e-02
                    * Math.pow(t - 6.942, 2) + 0.277409
                    * Math.pow(t - 6.942, 3);
        else if (t <= 7.872)
            y = 0.841896 + 0.102423 * (t - 7.407) + 3.030328e-01
                    * Math.pow(t - 7.407, 2) - 0.354633
                    * Math.pow(t - 7.407, 3);
        else if (t <= 8.877)
            y = 0.919389 + 0.154202 * (t - 7.872) - 1.916809e-01
                    * Math.pow(t - 7.872, 2) - 0.100478
                    * Math.pow(t - 7.872, 3);
        else if (t <= 9.342)
            y = 0.778767 - 0.535532 * (t - 8.877) - 4.946216e-01
                    * Math.pow(t - 8.877, 2) + 0.849276
                    * Math.pow(t - 8.877, 3);
        else if (t <= 9.807)
            y = 0.508185 - 0.444626 * (t - 9.342) + 6.901186e-01
                    * Math.pow(t - 9.342, 2) - 0.35446 * Math.pow(t - 9.342, 3);
        else if (t <= 10.272)
            y = 0.415016 - 0.0327449 * (t - 9.807) + 1.956475e-01
                    * Math.pow(t - 9.807, 2) - 0.209782
                    * Math.pow(t - 9.807, 3);
        else if (t <= 10.734)
            y = 0.421001 + 0.0131269 * (t - 10.272) - 9.699846e-02
                    * Math.pow(t - 10.272, 2) + 0.262128
                    * Math.pow(t - 10.272, 3);
        else if (t <= 11.199)
            y = 0.432211 + 0.0913491 * (t - 10.734) + 2.663105e-01
                    * Math.pow(t - 10.734, 2) - 0.365611
                    * Math.pow(t - 10.734, 3);
        else if (t <= 11.664)
            y = 0.495511 + 0.101855 * (t - 11.199) - 2.437172e-01
                    * Math.pow(t - 11.199, 2) - 0.105576
                    * Math.pow(t - 11.199, 3);
        else if (t <= 12.129)
            y = 0.47956 - 0.193287 * (t - 11.664) - 3.909959e-01
                    * Math.pow(t - 11.664, 2) + 0.67975
                    * Math.pow(t - 11.664, 3);
        else if (t <= 13.134)
            y = 0.373484 - 0.115976 * (t - 12.129) + 5.572554e-01
                    * Math.pow(t - 12.129, 2) - 0.36999
                    * Math.pow(t - 12.129, 3);
        else if (t <= 13.599)
            y = 0.444203 - 0.116991 * (t - 13.134) - 5.582651e-01
                    * Math.pow(t - 13.134, 2) + 0.803056
                    * Math.pow(t - 13.134, 3);
        else if (t <= 14.064)
            y = 0.349834 - 0.115255 * (t - 13.599) + 5.619983e-01
                    * Math.pow(t - 13.599, 2) - 0.391353
                    * Math.pow(t - 13.599, 3);
        else if (t <= 14.529)
            y = 0.37841 + 0.153543 * (t - 14.064) + 1.606073e-02
                    * Math.pow(t - 14.064, 2) - 0.0653872
                    * Math.pow(t - 14.064, 3);
        else if (t <= 14.991)
            y = 0.446706 + 0.126064 * (t - 14.529) - 7.515447e-02
                    * Math.pow(t - 14.529, 2) - 0.187084
                    * Math.pow(t - 14.529, 3);
        else if (t <= 15.456)
            y = 0.470458 - 0.0631743 * (t - 14.991) - 3.344527e-01
                    * Math.pow(t - 14.991, 2) - 0.0305672
                    * Math.pow(t - 14.991, 3);
        else if (t <= 15.921)
            y = 0.365691 - 0.394043 * (t - 15.456) - 3.770939e-01
                    * Math.pow(t - 15.456, 2) + 0.547005
                    * Math.pow(t - 15.456, 3);
        else if (t <= 16.386)
            y = 0.155923 - 0.389912 * (t - 15.921) + 3.859784e-01
                    * Math.pow(t - 15.921, 2) + 0.139954
                    * Math.pow(t - 15.921, 3);
        else if (t <= 17.391)
            y = 0.0721433 + 0.0598327 * (t - 16.386) + 5.812147e-01
                    * Math.pow(t - 16.386, 2) - 0.428449
                    * Math.pow(t - 16.386, 3);
        else if (t <= 17.856)
            y = 0.284409 - 0.0701584 * (t - 17.391) - 7.105590e-01
                    * Math.pow(t - 17.391, 2) + 0.672417
                    * Math.pow(t - 17.391, 3);
        else if (t <= 18.321)
            y = 0.165752 - 0.294798 * (t - 17.856) + 2.274630e-01
                    * Math.pow(t - 17.856, 2) + 0.325128
                    * Math.pow(t - 17.856, 3);
        else if (t <= 18.786)
            y = 0.110544 + 0.127645 * (t - 18.321) + 6.810168e-01
                    * Math.pow(t - 18.321, 2) - 0.574329
                    * Math.pow(t - 18.321, 3);
        else if (t <= 19.248)
            y = 0.259406 + 0.388438 * (t - 18.786) - 1.201718e-01
                    * Math.pow(t - 18.786, 2) - 0.355037
                    * Math.pow(t - 18.786, 3);
        else if (t <= 19.713)
            y = 0.378204 + 0.050058 * (t - 19.248) - 6.122524e-01
                    * Math.pow(t - 19.248, 2) + 0.331958
                    * Math.pow(t - 19.248, 3);
        else if (t <= 20.178)
            y = 0.302473 - 0.304004 * (t - 19.713) - 1.491710e-01
                    * Math.pow(t - 19.713, 2) + 0.636016
                    * Math.pow(t - 19.713, 3);
        else if (t <= 20.643)
            y = 0.192805 - 0.0301656 * (t - 20.178) + 7.380708e-01
                    * Math.pow(t - 20.178, 2) - 0.529083
                    * Math.pow(t - 20.178, 3);

        return y * (0.6904 - 0.3447) + 0.3447;
    }

    /*
	 * Alternative Splines.
	 * ------------------------------------------------------------------------------
	 */

    /**
     * Computes the approximation spline for nad or returns the default value
     * 5.12 mM if t is not in the measured time intervall. Parameter for the
     * spline: weights all equal to one, S=1.
     *
     * @param t
     * @return
     */
    public double getAKG_2(double t) {
        double y = this.akg;

        if ((-3.894 <= t) && (t <= -3.429))
            y = 0.0103873 + 0.0245023 * (t + 3.894) + 0
                    * Math.pow(t + 3.894, 2) + 0.000889259
                    * Math.pow(t + 3.894, 3);
        else if (t <= -2.964)
            y = 0.0218703 + 0.0250792 * (t + 3.429) + 1.240516e-003
                    * Math.pow(t + 3.429, 2) - 0.00103745
                    * Math.pow(t + 3.429, 3);
        else if (t <= -2.499)
            y = 0.0336961 + 0.0255599 * (t + 2.964) - 2.067336e-004
                    * Math.pow(t + 2.964, 2) + 0.0031721
                    * Math.pow(t + 2.964, 3);
        else if (t <= -2.037)
            y = 0.0458557 + 0.0274253 * (t + 2.499) + 4.218348e-003
                    * Math.pow(t + 2.499, 2) + 0.00940976
                    * Math.pow(t + 2.499, 3);
        else if (t <= -1.572)
            y = 0.0603544 + 0.0373484 * (t + 2.037) + 1.726027e-002
                    * Math.pow(t + 2.037, 2) + 0.00968846
                    * Math.pow(t + 2.037, 3);
        else if (t <= -1.107)
            y = 0.0824277 + 0.0596851 * (t + 1.572) + 3.077568e-002
                    * Math.pow(t + 1.572, 2) + 0.0028213
                    * Math.pow(t + 1.572, 3);
        else if (t <= -0.642)
            y = 0.117119 + 0.0901366 * (t + 1.107) + 3.471139e-002
                    * Math.pow(t + 1.107, 2) + 0.00341112
                    * Math.pow(t + 1.107, 3);
        else if (t <= 0.363)
            y = 0.166881 + 0.124631 * (t + 0.642) + 3.946990e-002
                    * Math.pow(t + 0.642, 2) + 0.00095361
                    * Math.pow(t + 0.642, 3);
        else if (t <= 0.828)
            y = 0.332969 + 0.206855 * (t - 0.363) + 4.234504e-002
                    * Math.pow(t - 0.363, 2) - 0.0162354
                    * Math.pow(t - 0.363, 3);
        else if (t <= 1.293)
            y = 0.43668 + 0.235704 * (t - 0.828) + 1.969664e-002
                    * Math.pow(t - 0.828, 2) - 0.0467443
                    * Math.pow(t - 0.828, 3);
        else if (t <= 1.758)
            y = 0.545842 + 0.2237 * (t - 1.293) - 4.551161e-002
                    * Math.pow(t - 1.293, 2) - 0.00864697
                    * Math.pow(t - 1.293, 3);
        else if (t <= 2.22)
            y = 0.639152 + 0.175765 * (t - 1.758) - 5.757413e-002
                    * Math.pow(t - 1.758, 2) - 0.00969614
                    * Math.pow(t - 1.758, 3);
        else if (t <= 2.685)
            y = 0.707111 + 0.116358 * (t - 2.22) - 7.101299e-002
                    * Math.pow(t - 2.22, 2) + 0.0111824 * Math.pow(t - 2.22, 3);
        else if (t <= 3.15)
            y = 0.746987 + 0.0575699 * (t - 2.685) - 5.541349e-002
                    * Math.pow(t - 2.685, 2) + 0.014742
                    * Math.pow(t - 2.685, 3);
        else if (t <= 4.62)
            y = 0.763257 + 0.0155981 * (t - 3.15) - 3.484845e-002
                    * Math.pow(t - 3.15, 2) + 0.0112969 * Math.pow(t - 3.15, 3);
        else if (t <= 5.085)
            y = 0.746768 - 0.0136219 * (t - 4.62) + 1.497086e-002
                    * Math.pow(t - 4.62, 2) + 0.0132501 * Math.pow(t - 4.62, 3);
        else if (t <= 5.55)
            y = 0.745003 + 0.00889596 * (t - 5.085) + 3.345475e-002
                    * Math.pow(t - 5.085, 2) - 0.012265
                    * Math.pow(t - 5.085, 3);
        else if (t <= 6.015)
            y = 0.75514 + 0.0320529 * (t - 5.55) + 1.634507e-002
                    * Math.pow(t - 5.55, 2) - 0.0117923 * Math.pow(t - 5.55, 3);
        else if (t <= 6.477)
            y = 0.772393 + 0.0396044 * (t - 6.015) - 1.051669e-004
                    * Math.pow(t - 6.015, 2) - 0.0170771
                    * Math.pow(t - 6.015, 3);
        else if (t <= 6.942)
            y = 0.788984 + 0.0285723 * (t - 6.477) - 2.377399e-002
                    * Math.pow(t - 6.477, 2) + 0.00151285
                    * Math.pow(t - 6.477, 3);
        else if (t <= 7.407)
            y = 0.797281 + 0.0074438 * (t - 6.942) - 2.166356e-002
                    * Math.pow(t - 6.942, 2) + 0.00135068
                    * Math.pow(t - 6.942, 3);
        else if (t <= 7.872)
            y = 0.796194 - 0.0118272 * (t - 7.407) - 1.977937e-002
                    * Math.pow(t - 7.407, 2) - 0.00327297
                    * Math.pow(t - 7.407, 3);
        else if (t <= 8.877)
            y = 0.786089 - 0.0323451 * (t - 7.872) - 2.434516e-002
                    * Math.pow(t - 7.872, 2) + 0.0112722
                    * Math.pow(t - 7.872, 3);
        else if (t <= 9.342)
            y = 0.740435 - 0.0471232 * (t - 8.877) + 9.640591e-003
                    * Math.pow(t - 8.877, 2) + 0.00491285
                    * Math.pow(t - 8.877, 3);
        else if (t <= 9.807)
            y = 0.721101 - 0.0349706 * (t - 9.342) + 1.649402e-002
                    * Math.pow(t - 9.342, 2) - 0.0097896
                    * Math.pow(t - 9.342, 3);
        else if (t <= 10.272)
            y = 0.707422 - 0.0259814 * (t - 9.807) + 2.837523e-003
                    * Math.pow(t - 9.807, 2) - 0.00582093
                    * Math.pow(t - 9.807, 3);
        else if (t <= 10.734)
            y = 0.695369 - 0.0271184 * (t - 10.272) - 5.282677e-003
                    * Math.pow(t - 10.272, 2) + 0.00265349
                    * Math.pow(t - 10.272, 3);
        else if (t <= 11.199)
            y = 0.681974 - 0.0303005 * (t - 10.734) - 1.604944e-003
                    * Math.pow(t - 10.734, 2) - 0.0079586
                    * Math.pow(t - 10.734, 3);
        else if (t <= 11.664)
            y = 0.666737 - 0.0369556 * (t - 11.199) - 1.270719e-002
                    * Math.pow(t - 11.199, 2) - 0.0131693
                    * Math.pow(t - 11.199, 3);
        else if (t <= 12.129)
            y = 0.645481 - 0.0573159 * (t - 11.664) - 3.107839e-002
                    * Math.pow(t - 11.664, 2) + 0.0093138
                    * Math.pow(t - 11.664, 3);
        else if (t <= 13.134)
            y = 0.613046 - 0.0801772 * (t - 12.129) - 1.808564e-002
                    * Math.pow(t - 12.129, 2) + 0.0120022
                    * Math.pow(t - 12.129, 3);
        else if (t <= 13.599)
            y = 0.526384 - 0.0801617 * (t - 13.134) + 1.810107e-002
                    * Math.pow(t - 13.134, 2) - 0.0135793
                    * Math.pow(t - 13.134, 3);
        else if (t <= 14.064)
            y = 0.491658 - 0.0721363 * (t - 13.599) - 8.420971e-004
                    * Math.pow(t - 13.599, 2) - 0.00640392
                    * Math.pow(t - 13.599, 3);
        else if (t <= 14.529)
            y = 0.457288 - 0.0770735 * (t - 14.064) - 9.775570e-003
                    * Math.pow(t - 14.064, 2) + 0.00511879
                    * Math.pow(t - 14.064, 3);
        else if (t <= 14.991)
            y = 0.41985 - 0.0828443 * (t - 14.529) - 2.634851e-003
                    * Math.pow(t - 14.529, 2) + 0.00937238
                    * Math.pow(t - 14.529, 3);
        else if (t <= 15.456)
            y = 0.381938 - 0.0792775 * (t - 14.991) + 1.035527e-002
                    * Math.pow(t - 14.991, 2) - 0.0075053
                    * Math.pow(t - 14.991, 3);
        else if (t <= 15.921)
            y = 0.346558 - 0.0745156 * (t - 15.456) - 1.146279e-004
                    * Math.pow(t - 15.456, 2) + 0.00304791
                    * Math.pow(t - 15.456, 3);
        else if (t <= 16.386)
            y = 0.31219 - 0.0726451 * (t - 15.921) + 4.137208e-003
                    * Math.pow(t - 15.921, 2) + 0.0136591
                    * Math.pow(t - 15.921, 3);
        else if (t <= 17.391)
            y = 0.280678 - 0.0599372 * (t - 16.386) + 2.319161e-002
                    * Math.pow(t - 16.386, 2) - 0.00821908
                    * Math.pow(t - 16.386, 3);
        else if (t <= 17.856)
            y = 0.235522 - 0.0382265 * (t - 17.391) - 1.588930e-003
                    * Math.pow(t - 17.391, 2) + 0.00670205
                    * Math.pow(t - 17.391, 3);
        else if (t <= 18.321)
            y = 0.218077 - 0.0353567 * (t - 17.856) + 7.760434e-003
                    * Math.pow(t - 17.856, 2) + 0.00564499
                    * Math.pow(t - 17.856, 3);
        else if (t <= 18.786)
            y = 0.203882 - 0.0244778 * (t - 18.321) + 1.563519e-002
                    * Math.pow(t - 18.321, 2) - 0.00177981
                    * Math.pow(t - 18.321, 3);
        else if (t <= 19.248)
            y = 0.195702 - 0.0110916 * (t - 18.786) + 1.315236e-002
                    * Math.pow(t - 18.786, 2) - 0.00372881
                    * Math.pow(t - 18.786, 3);
        else if (t <= 19.713)
            y = 0.193017 - 0.00132647 * (t - 19.248) + 7.984227e-003
                    * Math.pow(t - 19.248, 2) + 0.00047682
                    * Math.pow(t - 19.248, 3);
        else if (t <= 20.178)
            y = 0.194174 + 0.00640816 * (t - 19.713) + 8.649391e-003
                    * Math.pow(t - 19.713, 2) - 0.00671658
                    * Math.pow(t - 19.713, 3);
        else if (t <= 20.643)
            y = 0.198349 + 0.0100952 * (t - 20.178) - 7.202434e-004
                    * Math.pow(t - 20.178, 2) + 0.000516303
                    * Math.pow(t - 20.178, 3);

        return (13.05163236 - 4.464821914) * y + 4.464821914;
    }

    /**
     * Computes the approximation spline for nad or returns the default value
     * 1.05 mM if t is not in the measured time intervall. Parameter for the
     * spline: weights all equal to one, S=1.
     *
     * @param t
     * @return
     */
    public double getAla_2(double t) {
        double y = this.ala;
        // An alternative way might look like this
        // v1 = -0.0257788
        // v2 = 0.0757032
        // v3 = 3.894
        // v4 = 8.46192e-005
//		if ((-3.894 <= t) && (t <= -3.429))
//			y = calcAla(t, -0.0257788, 0.0757032, 3.894, 8.46192e-005);
        if ((-3.894 <= t) && (t <= -3.429))
            y = -0.0257788 + 0.0757032 * (t + 3.894) + 0
                    * Math.pow(t + 3.894, 2) + 8.46192e-005
                    * Math.pow(t + 3.894, 3);
        else if (t <= -2.964)
            y = 0.00943169 + 0.0757581 * (t + 3.429) + 1.180437e-004
                    * Math.pow(t + 3.429, 2) + 0.000227745
                    * Math.pow(t + 3.429, 3);
        else if (t <= -2.499)
            y = 0.0447076 + 0.0760156 * (t + 2.964) + 4.357480e-004
                    * Math.pow(t + 2.964, 2) + 0.00037785
                    * Math.pow(t + 2.964, 3);
        else if (t <= -2.037)
            y = 0.0801871 + 0.0766659 * (t + 2.499) + 9.628484e-004
                    * Math.pow(t + 2.499, 2) + 0.00059295
                    * Math.pow(t + 2.499, 3);
        else if (t <= -1.572)
            y = 0.115871 + 0.0779353 * (t + 2.037) + 1.784677e-003
                    * Math.pow(t + 2.037, 2) + 0.000556136
                    * Math.pow(t + 2.037, 3);
        else if (t <= -1.107)
            y = 0.152552 + 0.0799558 * (t + 1.572) + 2.560487e-003
                    * Math.pow(t + 1.572, 2) + 0.000221119
                    * Math.pow(t + 1.572, 3);
        else if (t <= -0.642)
            y = 0.190308 + 0.0824805 * (t + 1.107) + 2.868948e-003
                    * Math.pow(t + 1.107, 2) - 7.57144e-005
                    * Math.pow(t + 1.107, 3);
        else if (t <= 0.363)
            y = 0.229274 + 0.0850995 * (t + 0.642) + 2.763327e-003
                    * Math.pow(t + 0.642, 2) - 0.000522193
                    * Math.pow(t + 0.642, 3);
        else if (t <= 0.828)
            y = 0.31706 + 0.0890715 * (t - 0.363) + 1.188915e-003
                    * Math.pow(t - 0.363, 2) - 0.00127255
                    * Math.pow(t - 0.363, 3);
        else if (t <= 1.293)
            y = 0.358607 + 0.0893517 * (t - 0.828) - 5.862872e-004
                    * Math.pow(t - 0.828, 2) - 0.0020192
                    * Math.pow(t - 0.828, 3);
        else if (t <= 1.758)
            y = 0.399826 + 0.0874966 * (t - 1.293) - 3.403066e-003
                    * Math.pow(t - 1.293, 2) - 0.0010238
                    * Math.pow(t - 1.293, 3);
        else if (t <= 2.22)
            y = 0.439673 + 0.0836677 * (t - 1.758) - 4.831271e-003
                    * Math.pow(t - 1.758, 2) - 0.000745052
                    * Math.pow(t - 1.758, 3);
        else if (t <= 2.685)
            y = 0.477223 + 0.0787265 * (t - 2.22) - 5.863913e-003
                    * Math.pow(t - 2.22, 2) - 0.000419832
                    * Math.pow(t - 2.22, 3);
        else if (t <= 3.15)
            y = 0.512521 + 0.0730007 * (t - 2.685) - 6.449578e-003
                    * Math.pow(t - 2.685, 2) + 0.000116966
                    * Math.pow(t - 2.685, 3);
        else if (t <= 4.62)
            y = 0.545083 + 0.0670785 * (t - 3.15) - 6.286411e-003
                    * Math.pow(t - 3.15, 2) + 0.000331164
                    * Math.pow(t - 3.15, 3);
        else if (t <= 5.085)
            y = 0.631156 + 0.0507433 * (t - 4.62) - 4.825979e-003
                    * Math.pow(t - 4.62, 2) + 0.000341949
                    * Math.pow(t - 4.62, 3);
        else if (t <= 5.55)
            y = 0.653743 + 0.0464769 * (t - 5.085) - 4.348961e-003
                    * Math.pow(t - 5.085, 2) + 0.00010459
                    * Math.pow(t - 5.085, 3);
        else if (t <= 6.015)
            y = 0.674425 + 0.0425003 * (t - 5.55) - 4.203058e-003
                    * Math.pow(t - 5.55, 2) + 0.000593076
                    * Math.pow(t - 5.55, 3);
        else if (t <= 6.477)
            y = 0.693338 + 0.0389761 * (t - 6.015) - 3.375717e-003
                    * Math.pow(t - 6.015, 2) + 0.000530359
                    * Math.pow(t - 6.015, 3);
        else if (t <= 6.942)
            y = 0.710677 + 0.0361966 * (t - 6.477) - 2.640640e-003
                    * Math.pow(t - 6.477, 2) + 0.000450453
                    * Math.pow(t - 6.477, 3);
        else if (t <= 7.407)
            y = 0.726982 + 0.034033 * (t - 6.942) - 2.012259e-003
                    * Math.pow(t - 6.942, 2) - 0.000111927
                    * Math.pow(t - 6.942, 3);
        else if (t <= 7.872)
            y = 0.742361 + 0.032089 * (t - 7.407) - 2.168397e-003
                    * Math.pow(t - 7.407, 2) - 0.00044037
                    * Math.pow(t - 7.407, 3);
        else if (t <= 8.877)
            y = 0.75677 + 0.0297867 * (t - 7.872) - 2.782713e-003
                    * Math.pow(t - 7.872, 2) - 0.000103208
                    * Math.pow(t - 7.872, 3);
        else if (t <= 9.342)
            y = 0.78379 + 0.0238807 * (t - 8.877) - 3.093887e-003
                    * Math.pow(t - 8.877, 2) - 0.000601735
                    * Math.pow(t - 8.877, 3);
        else if (t <= 9.807)
            y = 0.794165 + 0.0206131 * (t - 9.342) - 3.933308e-003
                    * Math.pow(t - 9.342, 2) - 0.00100343
                    * Math.pow(t - 9.342, 3);
        else if (t <= 10.272)
            y = 0.802799 + 0.0163042 * (t - 9.807) - 5.333096e-003
                    * Math.pow(t - 9.807, 2) - 0.000786438
                    * Math.pow(t - 9.807, 3);
        else if (t <= 10.734)
            y = 0.809148 + 0.0108343 * (t - 10.272) - 6.430177e-003
                    * Math.pow(t - 10.272, 2) - 0.000301043
                    * Math.pow(t - 10.272, 3);
        else if (t <= 11.199)
            y = 0.812751 + 0.00470002 * (t - 10.734) - 6.847422e-003
                    * Math.pow(t - 10.734, 2) - 0.000462141
                    * Math.pow(t - 10.734, 3);
        else if (t <= 11.664)
            y = 0.81341 - 0.00196787 * (t - 11.199) - 7.492109e-003
                    * Math.pow(t - 11.199, 2) - 0.000409103
                    * Math.pow(t - 11.199, 3);
        else if (t <= 12.129)
            y = 0.810833 - 0.0092009 * (t - 11.664) - 8.062808e-003
                    * Math.pow(t - 11.664, 2) - 3.32225e-006
                    * Math.pow(t - 11.664, 3);
        else if (t <= 13.134)
            y = 0.804811 - 0.0167015 * (t - 12.129) - 8.067443e-003
                    * Math.pow(t - 12.129, 2) + 0.000582906
                    * Math.pow(t - 12.129, 3);
        else if (t <= 13.599)
            y = 0.78047 - 0.0311508 * (t - 13.134) - 6.309980e-003
                    * Math.pow(t - 13.134, 2) - 0.000389839
                    * Math.pow(t - 13.134, 3);
        else if (t <= 14.064)
            y = 0.764581 - 0.0372719 * (t - 13.599) - 6.853806e-003
                    * Math.pow(t - 13.599, 2) + 0.000242278
                    * Math.pow(t - 13.599, 3);
        else if (t <= 14.529)
            y = 0.745792 - 0.0434888 * (t - 14.064) - 6.515828e-003
                    * Math.pow(t - 14.064, 2) + 0.00107672
                    * Math.pow(t - 14.064, 3);
        else if (t <= 14.991)
            y = 0.724269 - 0.0488501 * (t - 14.529) - 5.013806e-003
                    * Math.pow(t - 14.529, 2) + 0.00137712
                    * Math.pow(t - 14.529, 3);
        else if (t <= 15.456)
            y = 0.700766 - 0.052601 * (t - 14.991) - 3.105123e-003
                    * Math.pow(t - 14.991, 2) + 0.000485789
                    * Math.pow(t - 14.991, 3);
        else if (t <= 15.921)
            y = 0.675684 - 0.0551737 * (t - 15.456) - 2.427447e-003
                    * Math.pow(t - 15.456, 2) + 0.00103293
                    * Math.pow(t - 15.456, 3);
        else if (t <= 16.386)
            y = 0.649607 - 0.0567612 * (t - 15.921) - 9.865110e-004
                    * Math.pow(t - 15.921, 2) + 0.00134773
                    * Math.pow(t - 15.921, 3);
        else if (t <= 17.391)
            y = 0.623135 - 0.0568044 * (t - 16.386) + 8.935673e-004
                    * Math.pow(t - 16.386, 2) + 0.00043569
                    * Math.pow(t - 16.386, 3);
        else if (t <= 17.856)
            y = 0.567392 - 0.0536882 * (t - 17.391) + 2.207173e-003
                    * Math.pow(t - 17.391, 2) + 0.000468284
                    * Math.pow(t - 17.391, 3);
        else if (t <= 18.321)
            y = 0.542951 - 0.0513317 * (t - 17.856) + 2.860429e-003
                    * Math.pow(t - 17.856, 2) + 0.000183539
                    * Math.pow(t - 17.856, 3);
        else if (t <= 18.786)
            y = 0.519719 - 0.0485525 * (t - 18.321) + 3.116466e-003
                    * Math.pow(t - 18.321, 2) - 0.000803598
                    * Math.pow(t - 18.321, 3);
        else if (t <= 19.248)
            y = 0.497735 - 0.0461754 * (t - 18.786) + 1.995447e-003
                    * Math.pow(t - 18.786, 2) - 0.000805573
                    * Math.pow(t - 18.786, 3);
        else if (t <= 19.713)
            y = 0.476748 - 0.0448475 * (t - 19.248) + 8.789230e-004
                    * Math.pow(t - 19.248, 2) - 0.000265004
                    * Math.pow(t - 19.248, 3);
        else if (t <= 20.178)
            y = 0.456058 - 0.044202 * (t - 19.713) + 5.092420e-004
                    * Math.pow(t - 19.713, 2) - 0.000309747
                    * Math.pow(t - 19.713, 3);
        else if (t <= 20.643)
            y = 0.435583 - 0.0439293 * (t - 20.178) + 7.714514e-005
                    * Math.pow(t - 20.178, 2) - 5.53012e-005
                    * Math.pow(t - 20.178, 3);

        return (3.800862229 - 0.814981525) * y + 0.814981525;
    }

    // Unused - just a test
    private double calcAla(double t, double v1, double v2, double v3, double v4) {
        // v1 = -0.0257788
        // v2 = 0.0757032
        // v3 = 3.894
        // v4 = 8.46192e-005
        double v, u = t + v3;
        return v1 + v2 * u + 0
                * (v = u * u) + v4
                * v * u;
    }

    // Unused - just a test
    private double getPyr_2_alt(double t) {
        /*
		if ((-3.894 <= t) && (t <= -3.429))
			y = 0.00709166 + 0.0109621 * (t + 3.894) + 0
					* Math.pow(t + 3.894, 2) + 0.00196669
					* Math.pow(t + 3.894, 3);
		 */
        double y = this.pyr;
//		System.out.println(t + " , " + y);
        double u = (t + 3.894);
        if ((-3.894 <= t) && (t <= -3.429))
            y = 0.00709166 + 0.0109621 * u + 0
                    * u * u + 0.00196669
                    * u * u * u;
//		System.out.println(y);
        return (2.350729829 - 0.614400017) * y + 0.614400017;
    }

    // Unused - just a test
    private double getPyr_2_altalt(double t) {
        double y = this.pyr;
        double v, u = (t + 3.894);
        if ((-3.894 <= t) && (t <= -3.429))
            y = 0.00709166 + 0.0109621 * u + 0
                    * (v = u * u) + 0.00196669
                    * v * u;
        return (2.350729829 - 0.614400017) * y + 0.614400017;
    }
    // Unused - just a test
//	private double getPyr_2_hashed(double t) {
//		if (pyrHash==null) pyrHash=new HashMap<Double,Double>();
//		Double hRes = pyrHash.get(t);
//		if (hRes == null) {
//			hRes = getPyr_2(t);
//			pyrHash.put(t, hRes);
//		}
//		return hRes;
//	}

    /**
     * Computes the spline equation for pyr with the parameter S=1 and all
     * weights equal to one. For values of t smaller or greater than the
     * considered time the default steady-state value of 0.689 mM will be
     * returned.
     *
     * @param t
     * @return y the concentration of pyr at the given time t.
     */
    public double getPyr_2(double t) {
        double y = this.pyr;

        if ((-3.894 <= t) && (t <= -3.429))
            y = 0.00709166 + 0.0109621 * (t + 3.894) + 0
                    * Math.pow(t + 3.894, 2) + 0.00196669
                    * Math.pow(t + 3.894, 3);
        else if (t <= -2.964)
            y = 0.0123868 + 0.0122378 * (t + 3.429) + 2.743530e-003
                    * Math.pow(t + 3.429, 2) + 0.00339276
                    * Math.pow(t + 3.429, 3);
        else if (t <= -2.499)
            y = 0.0190117 + 0.0169901 * (t + 2.964) + 7.476430e-003
                    * Math.pow(t + 2.964, 2) + 0.00342188
                    * Math.pow(t + 2.964, 3);
        else if (t <= -2.037)
            y = 0.0288727 + 0.0261629 * (t + 2.499) + 1.224995e-002
                    * Math.pow(t + 2.499, 2) + 0.00337058
                    * Math.pow(t + 2.499, 3);
        else if (t <= -1.572)
            y = 0.043907 + 0.0396401 * (t + 2.037) + 1.692158e-002
                    * Math.pow(t + 2.037, 2) + 0.00582311
                    * Math.pow(t + 2.037, 3);
        else if (t <= -1.107)
            y = 0.066584 + 0.0591545 * (t + 1.572) + 2.504482e-002
                    * Math.pow(t + 1.572, 2) + 0.00764784
                    * Math.pow(t + 1.572, 3);
        else if (t <= -0.642)
            y = 0.100275 + 0.0874071 * (t + 1.107) + 3.571356e-002
                    * Math.pow(t + 1.107, 2) + 0.00313583
                    * Math.pow(t + 1.107, 3);
        else if (t <= 0.363)
            y = 0.148957 + 0.122655 * (t + 0.642) + 4.008804e-002
                    * Math.pow(t + 0.642, 2) - 0.00384667
                    * Math.pow(t + 0.642, 3);
        else if (t <= 0.828)
            y = 0.30881 + 0.191576 * (t - 0.363) + 2.849033e-002
                    * Math.pow(t - 0.363, 2) - 0.0157823
                    * Math.pow(t - 0.363, 3);
        else if (t <= 1.293)
            y = 0.402467 + 0.207835 * (t - 0.828) + 6.474088e-003
                    * Math.pow(t - 0.828, 2) - 0.0315699
                    * Math.pow(t - 0.828, 3);
        else if (t <= 1.758)
            y = 0.497336 + 0.193377 * (t - 1.293) - 3.756588e-002
                    * Math.pow(t - 1.293, 2) - 0.00800701
                    * Math.pow(t - 1.293, 3);
        else if (t <= 2.22)
            y = 0.578328 + 0.153247 * (t - 1.758) - 4.873566e-002
                    * Math.pow(t - 1.758, 2) + 0.000853208
                    * Math.pow(t - 1.758, 3);
        else if (t <= 2.685)
            y = 0.63881 + 0.108761 * (t - 2.22) - 4.755311e-002
                    * Math.pow(t - 2.22, 2) + 0.00189972
                    * Math.pow(t - 2.22, 3);
        else if (t <= 3.15)
            y = 0.679293 + 0.0657692 * (t - 2.685) - 4.490300e-002
                    * Math.pow(t - 2.685, 2) + 0.00542329
                    * Math.pow(t - 2.685, 3);
        else if (t <= 4.62)
            y = 0.700711 + 0.0275273 * (t - 3.15) - 3.733752e-002
                    * Math.pow(t - 3.15, 2) + 0.00782647
                    * Math.pow(t - 3.15, 3);
        else if (t <= 5.085)
            y = 0.685355 - 0.0315083 * (t - 4.62) - 2.822794e-003
                    * Math.pow(t - 4.62, 2) + 0.00824557
                    * Math.pow(t - 4.62, 3);
        else if (t <= 5.55)
            y = 0.670922 - 0.0287848 * (t - 5.085) + 8.679771e-003
                    * Math.pow(t - 5.085, 2) - 0.00053434
                    * Math.pow(t - 5.085, 3);
        else if (t <= 6.015)
            y = 0.65936 - 0.0210592 * (t - 5.55) + 7.934367e-003
                    * Math.pow(t - 5.55, 2) + 0.000466607
                    * Math.pow(t - 5.55, 3);
        else if (t <= 6.477)
            y = 0.65133 - 0.0133776 * (t - 6.015) + 8.585283e-003
                    * Math.pow(t - 6.015, 2) + 0.00180465
                    * Math.pow(t - 6.015, 3);
        else if (t <= 6.942)
            y = 0.64716 - 0.00428923 * (t - 6.477) + 1.108653e-002
                    * Math.pow(t - 6.477, 2) + 0.00122435
                    * Math.pow(t - 6.477, 3);
        else if (t <= 7.407)
            y = 0.647686 + 0.00681544 * (t - 6.942) + 1.279450e-002
                    * Math.pow(t - 6.942, 2) + 0.000225641
                    * Math.pow(t - 6.942, 3);
        else if (t <= 7.872)
            y = 0.653645 + 0.0188607 * (t - 7.407) + 1.310927e-002
                    * Math.pow(t - 7.407, 2) - 0.00705357
                    * Math.pow(t - 7.407, 3);
        else if (t <= 8.877)
            y = 0.66454 + 0.0264768 * (t - 7.872) + 3.269535e-003
                    * Math.pow(t - 7.872, 2) - 0.00164124
                    * Math.pow(t - 7.872, 3);
        else if (t <= 9.342)
            y = 0.692786 + 0.0280755 * (t - 8.877) - 1.678801e-003
                    * Math.pow(t - 8.877, 2) - 0.00583638
                    * Math.pow(t - 8.877, 3);
        else if (t <= 9.807)
            y = 0.704891 + 0.0227283 * (t - 9.342) - 9.820550e-003
                    * Math.pow(t - 9.342, 2) - 0.00702045
                    * Math.pow(t - 9.342, 3);
        else if (t <= 10.272)
            y = 0.71263 + 0.00904123 * (t - 9.807) - 1.961407e-002
                    * Math.pow(t - 9.807, 2) - 0.00222559
                    * Math.pow(t - 9.807, 3);
        else if (t <= 10.734)
            y = 0.71237 - 0.0106435 * (t - 10.272) - 2.271877e-002
                    * Math.pow(t - 10.272, 2) + 0.000800336
                    * Math.pow(t - 10.272, 3);
        else if (t <= 11.199)
            y = 0.702682 - 0.0311232 * (t - 10.734) - 2.160950e-002
                    * Math.pow(t - 10.734, 2) - 0.00295931
                    * Math.pow(t - 10.734, 3);
        else if (t <= 11.664)
            y = 0.68324 - 0.0531397 * (t - 11.199) - 2.573774e-002
                    * Math.pow(t - 11.199, 2) - 0.00108335
                    * Math.pow(t - 11.199, 3);
        else if (t <= 12.129)
            y = 0.652856 - 0.0777785 * (t - 11.664) - 2.724901e-002
                    * Math.pow(t - 11.664, 2) + 0.0120572
                    * Math.pow(t - 11.664, 3);
        else if (t <= 13.134)
            y = 0.612009 - 0.0952989 * (t - 12.129) - 1.042920e-002
                    * Math.pow(t - 12.129, 2) + 0.00960616
                    * Math.pow(t - 12.129, 3);
        else if (t <= 13.599)
            y = 0.515451 - 0.0871542 * (t - 13.134) + 1.853336e-002
                    * Math.pow(t - 13.134, 2) - 0.00166772
                    * Math.pow(t - 13.134, 3);
        else if (t <= 14.064)
            y = 0.478764 - 0.071 * (t - 13.599) + 1.620689e-002
                    * Math.pow(t - 13.599, 2) - 0.00322537
                    * Math.pow(t - 13.599, 3);
        else if (t <= 14.529)
            y = 0.448929 - 0.0580198 * (t - 14.064) + 1.170750e-002
                    * Math.pow(t - 14.064, 2) - 0.00133285
                    * Math.pow(t - 14.064, 3);
        else if (t <= 14.991)
            y = 0.424347 - 0.0479964 * (t - 14.529) + 9.848170e-003
                    * Math.pow(t - 14.529, 2) - 0.000351634
                    * Math.pow(t - 14.529, 3);
        else if (t <= 15.456)
            y = 0.40424 - 0.0391218 * (t - 14.991) + 9.360805e-003
                    * Math.pow(t - 14.991, 2) - 0.0119481
                    * Math.pow(t - 14.991, 3);
        else if (t <= 15.921)
            y = 0.386871 - 0.0381667 * (t - 15.456) - 7.306757e-003
                    * Math.pow(t - 15.456, 2) + 0.00287619
                    * Math.pow(t - 15.456, 3);
        else if (t <= 16.386)
            y = 0.367833 - 0.0430963 * (t - 15.921) - 3.294470e-003
                    * Math.pow(t - 15.921, 2) + 0.00490737
                    * Math.pow(t - 15.921, 3);
        else if (t <= 17.391)
            y = 0.347574 - 0.0429768 * (t - 16.386) + 3.551307e-003
                    * Math.pow(t - 16.386, 2) - 0.000631183
                    * Math.pow(t - 16.386, 3);
        else if (t <= 17.856)
            y = 0.307329 - 0.0377513 * (t - 17.391) + 1.648289e-003
                    * Math.pow(t - 17.391, 2) + 0.0043097
                    * Math.pow(t - 17.391, 3);
        else if (t <= 18.786)
            y = 0.290564 - 0.0334227 * (t - 17.856) + 7.660326e-003
                    * Math.pow(t - 17.856, 2) - 0.000123905
                    * Math.pow(t - 17.856, 3);
        else if (t <= 19.248)
            y = 0.266007 - 0.019496 * (t - 18.786) + 7.314632e-003
                    * Math.pow(t - 18.786, 2) - 0.00687547
                    * Math.pow(t - 18.786, 3);
        else if (t <= 19.713)
            y = 0.257883 - 0.0171399 * (t - 19.248) - 2.214775e-003
                    * Math.pow(t - 19.248, 2) - 0.0015665
                    * Math.pow(t - 19.248, 3);
        else if (t <= 20.178)
            y = 0.249277 - 0.0202158 * (t - 19.713) - 4.400043e-003
                    * Math.pow(t - 19.713, 2) + 0.0021551
                    * Math.pow(t - 19.713, 3);
        else if (t <= 20.643)
            y = 0.239141 - 0.0229099 * (t - 20.178) - 1.393672e-003
                    * Math.pow(t - 20.178, 2) + 0.000999048
                    * Math.pow(t - 20.178, 3);

        return (2.350729829 - 0.614400017) * y + 0.614400017;
    }

    /**
     * Computes the approximation spline for glut or returns the default value
     * 38.7 mM if t is not in the measured time intervall. Parameter for the
     * spline: weights all equal to one, S=1.
     *
     * @param t
     * @return
     */
    public double getGlut_2(double t) {
        double y = this.glut;

        if ((-3.894 <= t) && (t <= -3.429))
            y = 0.345758 + 0.0308006 * (t + 3.894) + 0 * Math.pow(t + 3.894, 2)
                    + 1.46278e-005 * Math.pow(t + 3.894, 3);
        else if (t <= -2.964)
            y = 0.360082 + 0.03081 * (t + 3.429) + 2.040578e-005
                    * Math.pow(t + 3.429, 2) - 4.77212e-006
                    * Math.pow(t + 3.429, 3);
        else if (t <= -2.499)
            y = 0.374413 + 0.0308259 * (t + 2.964) + 1.374867e-005
                    * Math.pow(t + 2.964, 2) - 2.09457e-005
                    * Math.pow(t + 2.964, 3);
        else if (t <= -2.037)
            y = 0.388748 + 0.0308251 * (t + 2.499) - 1.547064e-005
                    * Math.pow(t + 2.499, 2) - 5.74626e-006
                    * Math.pow(t + 2.499, 3);
        else if (t <= -1.572)
            y = 0.402985 + 0.0308071 * (t + 2.037) - 2.343495e-005
                    * Math.pow(t + 2.037, 2) - 1.44612e-005
                    * Math.pow(t + 2.037, 3);
        else if (t <= -1.107)
            y = 0.417304 + 0.030776 * (t + 1.572) - 4.360827e-005
                    * Math.pow(t + 1.572, 2) - 2.12542e-005
                    * Math.pow(t + 1.572, 3);
        else if (t <= -0.642)
            y = 0.431603 + 0.0307216 * (t + 1.107) - 7.325784e-005
                    * Math.pow(t + 1.107, 2) - 4.47943e-005
                    * Math.pow(t + 1.107, 3);
        else if (t <= 0.363)
            y = 0.445868 + 0.0306244 * (t + 0.642) - 1.357459e-004
                    * Math.pow(t + 0.642, 2) - 4.75641e-005
                    * Math.pow(t + 0.642, 3);
        else if (t <= 0.828)
            y = 0.47646 + 0.0302075 * (t - 0.363) - 2.791515e-004
                    * Math.pow(t - 0.363, 2) - 6.39979e-005
                    * Math.pow(t - 0.363, 3);
        else if (t <= 1.293)
            y = 0.49044 + 0.0299063 * (t - 0.828) - 3.684286e-004
                    * Math.pow(t - 0.828, 2) - 6.70553e-005
                    * Math.pow(t - 0.828, 3);
        else if (t <= 1.758)
            y = 0.50426 + 0.0295202 * (t - 1.293) - 4.619707e-004
                    * Math.pow(t - 1.293, 2) - 6.72759e-005
                    * Math.pow(t - 1.293, 3);
        else if (t <= 2.22)
            y = 0.51788 + 0.0290469 * (t - 1.758) - 5.558206e-004
                    * Math.pow(t - 1.758, 2) - 0.000100774
                    * Math.pow(t - 1.758, 3);
        else if (t <= 2.685)
            y = 0.531172 + 0.0284688 * (t - 2.22) - 6.954937e-004
                    * Math.pow(t - 2.22, 2) - 8.65101e-005
                    * Math.pow(t - 2.22, 3);
        else if (t <= 3.15)
            y = 0.54425 + 0.0277659 * (t - 2.685) - 8.161753e-004
                    * Math.pow(t - 2.685, 2) - 0.000128037
                    * Math.pow(t - 2.685, 3);
        else if (t <= 4.62)
            y = 0.556972 + 0.0269238 * (t - 3.15) - 9.947864e-004
                    * Math.pow(t - 3.15, 2) - 0.000177372
                    * Math.pow(t - 3.15, 3);
        else if (t <= 5.085)
            y = 0.593837 + 0.0228493 * (t - 4.62) - 1.776998e-003
                    * Math.pow(t - 4.62, 2) - 0.000228858
                    * Math.pow(t - 4.62, 3);
        else if (t <= 5.55)
            y = 0.604055 + 0.0210482 * (t - 5.085) - 2.096254e-003
                    * Math.pow(t - 5.085, 2) - 0.000211625
                    * Math.pow(t - 5.085, 3);
        else if (t <= 6.015)
            y = 0.613368 + 0.0189614 * (t - 5.55) - 2.391471e-003
                    * Math.pow(t - 5.55, 2) - 0.000215207
                    * Math.pow(t - 5.55, 3);
        else if (t <= 6.477)
            y = 0.621646 + 0.0165978 * (t - 6.015) - 2.691685e-003
                    * Math.pow(t - 6.015, 2) - 0.000233051
                    * Math.pow(t - 6.015, 3);
        else if (t <= 6.942)
            y = 0.628717 + 0.0139614 * (t - 6.477) - 3.014694e-003
                    * Math.pow(t - 6.477, 2) - 0.000222455
                    * Math.pow(t - 6.477, 3);
        else if (t <= 7.407)
            y = 0.634535 + 0.0110134 * (t - 6.942) - 3.325019e-003
                    * Math.pow(t - 6.942, 2) - 0.000218367
                    * Math.pow(t - 6.942, 3);
        else if (t <= 7.872)
            y = 0.638915 + 0.00777953 * (t - 7.407) - 3.629641e-003
                    * Math.pow(t - 7.407, 2) - 0.000200303
                    * Math.pow(t - 7.407, 3);
        else if (t <= 8.877)
            y = 0.641727 + 0.00427403 * (t - 7.872) - 3.909065e-003
                    * Math.pow(t - 7.872, 2) - 0.000100588
                    * Math.pow(t - 7.872, 3);
        else if (t <= 9.342)
            y = 0.641972 - 0.00388798 * (t - 8.877) - 4.212339e-003
                    * Math.pow(t - 8.877, 2) - 8.05589e-005
                    * Math.pow(t - 8.877, 3);
        else if (t <= 9.807)
            y = 0.639246 - 0.00785771 * (t - 9.342) - 4.324718e-003
                    * Math.pow(t - 9.342, 2) - 0.000122855
                    * Math.pow(t - 9.342, 3);
        else if (t <= 10.272)
            y = 0.634644 - 0.0119594 * (t - 9.807) - 4.496101e-003
                    * Math.pow(t - 9.807, 2) - 8.67214e-005
                    * Math.pow(t - 9.807, 3);
        else if (t <= 10.734)
            y = 0.628102 - 0.016197 * (t - 10.272) - 4.617077e-003
                    * Math.pow(t - 10.272, 2) + 1.56232e-005
                    * Math.pow(t - 10.272, 3);
        else if (t <= 11.199)
            y = 0.619635 - 0.0204532 * (t - 10.734) - 4.595424e-003
                    * Math.pow(t - 10.734, 2) + 4.79204e-005
                    * Math.pow(t - 10.734, 3);
        else if (t <= 11.664)
            y = 0.609136 - 0.0246959 * (t - 11.199) - 4.528575e-003
                    * Math.pow(t - 11.199, 2) + 0.000102127
                    * Math.pow(t - 11.199, 3);
        else if (t <= 12.129)
            y = 0.596683 - 0.0288412 * (t - 11.664) - 4.386108e-003
                    * Math.pow(t - 11.664, 2) + 0.000179965
                    * Math.pow(t - 11.664, 3);
        else if (t <= 13.134)
            y = 0.582342 - 0.0328035 * (t - 12.129) - 4.135056e-003
                    * Math.pow(t - 12.129, 2) + 0.000211674
                    * Math.pow(t - 12.129, 3);
        else if (t <= 13.599)
            y = 0.545413 - 0.0404736 * (t - 13.134) - 3.496860e-003
                    * Math.pow(t - 13.134, 2) + 0.000228722
                    * Math.pow(t - 13.134, 3);
        else if (t <= 14.064)
            y = 0.525859 - 0.0435773 * (t - 13.599) - 3.177793e-003
                    * Math.pow(t - 13.599, 2) + 0.000250327
                    * Math.pow(t - 13.599, 3);
        else if (t <= 14.529)
            y = 0.504934 - 0.0463703 * (t - 14.064) - 2.828587e-003
                    * Math.pow(t - 14.064, 2) + 0.000304393
                    * Math.pow(t - 14.064, 3);
        else if (t <= 14.991)
            y = 0.482791 - 0.0488034 * (t - 14.529) - 2.403960e-003
                    * Math.pow(t - 14.529, 2) + 0.000326486
                    * Math.pow(t - 14.529, 3);
        else if (t <= 15.456)
            y = 0.459763 - 0.0508156 * (t - 14.991) - 1.951450e-003
                    * Math.pow(t - 14.991, 2) + 0.00033353
                    * Math.pow(t - 14.991, 3);
        else if (t <= 15.921)
            y = 0.435745 - 0.0524141 * (t - 15.456) - 1.486176e-003
                    * Math.pow(t - 15.456, 2) + 0.000301097
                    * Math.pow(t - 15.456, 3);
        else if (t <= 16.386)
            y = 0.411081 - 0.0536009 * (t - 15.921) - 1.066146e-003
                    * Math.pow(t - 15.921, 2) + 0.000256208
                    * Math.pow(t - 15.921, 3);
        else if (t <= 17.391)
            y = 0.385952 - 0.0544263 * (t - 16.386) - 7.087358e-004
                    * Math.pow(t - 16.386, 2) + 0.000164513
                    * Math.pow(t - 16.386, 3);
        else if (t <= 17.856)
            y = 0.330705 - 0.0553523 * (t - 17.391) - 2.127293e-004
                    * Math.pow(t - 17.391, 2) + 0.000106639
                    * Math.pow(t - 17.391, 3);
        else if (t <= 18.321)
            y = 0.304931 - 0.055481 * (t - 17.856) - 6.396824e-005
                    * Math.pow(t - 17.856, 2) + 4.39342e-005
                    * Math.pow(t - 17.856, 3);
        else if (t <= 18.786)
            y = 0.279123 - 0.055512 * (t - 18.321) - 2.679996e-006
                    * Math.pow(t - 18.321, 2) - 3.37517e-005
                    * Math.pow(t - 18.321, 3);
        else if (t <= 19.248)
            y = 0.253306 - 0.0555364 * (t - 18.786) - 4.976359e-005
                    * Math.pow(t - 18.786, 2) - 3.19922e-006
                    * Math.pow(t - 18.786, 3);
        else if (t <= 19.713)
            y = 0.227637 - 0.0555844 * (t - 19.248) - 5.419771e-005
                    * Math.pow(t - 19.248, 2) + 2.66171e-005
                    * Math.pow(t - 19.248, 3);
        else if (t <= 20.178)
            y = 0.201781 - 0.0556175 * (t - 19.713) - 1.706686e-005
                    * Math.pow(t - 19.713, 2) + 1.3914e-005
                    * Math.pow(t - 19.713, 3);
        else if (t <= 20.643)
            y = 0.175917 - 0.0556244 * (t - 20.178) + 2.343138e-006
                    * Math.pow(t - 20.178, 2) - 1.67967e-006
                    * Math.pow(t - 20.178, 3);

        return (69.03104101 - 20.63720808) * y + 20.63720808;
    }

    /**
     * Computes the approximation spline for nadp or returns the default value
     * 0.0175 mM if t is not in the measured time intervall. Parameter for the
     * spline: weights all equal to one, S=1
     *
     * @param t
     * @return
     */
    public double getNADP_2(double t) {
        double y = this.nadp;

        if ((-3.429 <= t) && (t <= -2.964))
            y = 0.13595 + 0.0971795 * (t + 3.429) + 0 * Math.pow(t + 3.429, 2)
                    + 0.05691 * Math.pow(t + 3.429, 3);
        else if (t <= -2.499)
            y = 0.186861 + 0.134096 * (t + 2.964) + 7.938952e-002
                    * Math.pow(t + 2.964, 2) - 0.0819871
                    * Math.pow(t + 2.964, 3);
        else if (t <= -2.037)
            y = 0.258138 + 0.154745 * (t + 2.499) - 3.498256e-002
                    * Math.pow(t + 2.499, 2) - 0.0496607
                    * Math.pow(t + 2.499, 3);
        else if (t <= -1.572)
            y = 0.317266 + 0.0906217 * (t + 2.037) - 1.038122e-001
                    * Math.pow(t + 2.037, 2) + 0.0808513
                    * Math.pow(t + 2.037, 3);
        else if (t <= -1.107)
            y = 0.345087 + 0.0465225 * (t + 1.572) + 8.975385e-003
                    * Math.pow(t + 1.572, 2) + 0.0616748
                    * Math.pow(t + 1.572, 3);
        else if (t <= -0.642)
            y = 0.374862 + 0.0948765 * (t + 1.107) + 9.501172e-002
                    * Math.pow(t + 1.107, 2) + 0.00360399
                    * Math.pow(t + 1.107, 3);
        else if (t <= 0.363)
            y = 0.439886 + 0.185575 * (t + 0.642) + 1.000393e-001
                    * Math.pow(t + 0.642, 2) - 0.139812
                    * Math.pow(t + 0.642, 3);
        else if (t <= 0.828)
            y = 0.585512 - 0.0369863 * (t - 0.363) - 3.214935e-001
                    * Math.pow(t - 0.363, 2) + 0.1243 * Math.pow(t - 0.363, 3);
        else if (t <= 1.293)
            y = 0.511296 - 0.255345 * (t - 0.828) - 1.480948e-001
                    * Math.pow(t - 0.828, 2) + 0.234296
                    * Math.pow(t - 0.828, 3);
        else if (t <= 1.758)
            y = 0.384096 - 0.241091 * (t - 1.293) + 1.787485e-001
                    * Math.pow(t - 1.293, 2) - 0.0296831
                    * Math.pow(t - 1.293, 3);
        else if (t <= 2.22)
            y = 0.307654 - 0.0941094 * (t - 1.758) + 1.373406e-001
                    * Math.pow(t - 1.758, 2) - 0.0303251
                    * Math.pow(t - 1.758, 3);
        else if (t <= 2.685)
            y = 0.2905 + 0.0133752 * (t - 2.22) + 9.531002e-002
                    * Math.pow(t - 2.22, 2) - 0.162261 * Math.pow(t - 2.22, 3);
        else if (t <= 3.15)
            y = 0.301013 - 0.00324113 * (t - 2.685) - 1.310440e-001
                    * Math.pow(t - 2.685, 2) + 0.236466
                    * Math.pow(t - 2.685, 3);
        else if (t <= 4.62)
            y = 0.294947 + 0.0282775 * (t - 3.15) + 1.988259e-001
                    * Math.pow(t - 3.15, 2) - 0.0632008 * Math.pow(t - 3.15, 3);
        else if (t <= 5.085)
            y = 0.565399 + 0.203114 * (t - 4.62) - 7.988958e-002
                    * Math.pow(t - 4.62, 2) - 0.0313141 * Math.pow(t - 4.62, 3);
        else if (t <= 5.55)
            y = 0.639424 + 0.108504 * (t - 5.085) - 1.235727e-001
                    * Math.pow(t - 5.085, 2) - 0.00925389
                    * Math.pow(t - 5.085, 3);
        else if (t <= 6.015)
            y = 0.662228 - 0.0124214 * (t - 5.55) - 1.364819e-001
                    * Math.pow(t - 5.55, 2) + 0.0932451 * Math.pow(t - 5.55, 3);
        else if (t <= 6.477)
            y = 0.636317 - 0.0788638 * (t - 6.015) - 6.404951e-003
                    * Math.pow(t - 6.015, 2) + 0.0852613
                    * Math.pow(t - 6.015, 3);
        else if (t <= 6.942)
            y = 0.606922 - 0.0301864 * (t - 6.477) + 1.117673e-001
                    * Math.pow(t - 6.477, 2) - 0.083061
                    * Math.pow(t - 6.477, 3);
        else if (t <= 7.407)
            y = 0.608701 + 0.0198776 * (t - 6.942) - 4.102769e-003
                    * Math.pow(t - 6.942, 2) - 0.0241232
                    * Math.pow(t - 6.942, 3);
        else if (t <= 7.872)
            y = 0.614632 + 0.000413894 * (t - 7.407) - 3.775461e-002
                    * Math.pow(t - 7.407, 2) + 0.15561 * Math.pow(t - 7.407, 3);
        else if (t <= 8.877)
            y = 0.622307 + 0.0662424 * (t - 7.872) + 1.793212e-001
                    * Math.pow(t - 7.872, 2) - 0.176584
                    * Math.pow(t - 7.872, 3);
        else if (t <= 9.342)
            y = 0.690753 - 0.108384 * (t - 8.877) - 3.530788e-001
                    * Math.pow(t - 8.877, 2) + 0.261112
                    * Math.pow(t - 8.877, 3);
        else if (t <= 9.807)
            y = 0.590264 - 0.26737 * (t - 9.342) + 1.117277e-002
                    * Math.pow(t - 9.342, 2) + 0.157079
                    * Math.pow(t - 9.342, 3);
        else if (t <= 10.272)
            y = 0.484146 - 0.155086 * (t - 9.807) + 2.302982e-001
                    * Math.pow(t - 9.807, 2) - 0.0968507
                    * Math.pow(t - 9.807, 3);
        else if (t <= 10.734)
            y = 0.452089 - 0.00373366 * (t - 10.272) + 9.519140e-002
                    * Math.pow(t - 10.272, 2) - 0.153609
                    * Math.pow(t - 10.272, 3);
        else if (t <= 11.199)
            y = 0.455535 - 0.0141377 * (t - 10.734) - 1.177110e-001
                    * Math.pow(t - 10.734, 2) - 0.180352
                    * Math.pow(t - 10.734, 3);
        else if (t <= 11.664)
            y = 0.405375 - 0.240598 * (t - 11.199) - 3.693013e-001
                    * Math.pow(t - 11.199, 2) + 0.347415
                    * Math.pow(t - 11.199, 3);
        else if (t <= 12.129)
            y = 0.248575 - 0.358689 * (t - 11.664) + 1.153421e-001
                    * Math.pow(t - 11.664, 2) + 0.0933175
                    * Math.pow(t - 11.664, 3);
        else if (t <= 13.134)
            y = 0.116107 - 0.190889 * (t - 12.129) + 2.455201e-001
                    * Math.pow(t - 12.129, 2) - 0.071016
                    * Math.pow(t - 12.129, 3);
        else if (t <= 13.599)
            y = 0.100159 + 0.087423 * (t - 13.134) + 3.140679e-002
                    * Math.pow(t - 13.134, 2) - 0.0119264
                    * Math.pow(t - 13.134, 3);
        else if (t <= 14.064)
            y = 0.146403 + 0.108895 * (t - 13.599) + 1.476952e-002
                    * Math.pow(t - 13.599, 2) + 0.0161065
                    * Math.pow(t - 13.599, 3);
        else if (t <= 14.529)
            y = 0.201852 + 0.133079 * (t - 14.064) + 3.723807e-002
                    * Math.pow(t - 14.064, 2) - 0.0384482
                    * Math.pow(t - 14.064, 3);
        else if (t <= 14.991)
            y = 0.267919 + 0.14277 * (t - 14.529) - 1.639710e-002
                    * Math.pow(t - 14.529, 2) - 0.0889529
                    * Math.pow(t - 14.529, 3);
        else if (t <= 15.456)
            y = 0.321607 + 0.0706593 * (t - 14.991) - 1.396858e-001
                    * Math.pow(t - 14.991, 2) + 0.00481567
                    * Math.pow(t - 14.991, 3);
        else if (t <= 15.921)
            y = 0.324744 - 0.0561247 * (t - 15.456) - 1.329680e-001
                    * Math.pow(t - 15.456, 2) + 0.218536
                    * Math.pow(t - 15.456, 3);
        else if (t <= 17.391)
            y = 0.291868 - 0.0380259 * (t - 15.921) + 1.718902e-001
                    * Math.pow(t - 15.921, 2) - 0.0756988
                    * Math.pow(t - 15.921, 3);
        else if (t <= 17.856)
            y = 0.366948 - 0.0234014 * (t - 17.391) - 1.619415e-001
                    * Math.pow(t - 17.391, 2) + 0.156911
                    * Math.pow(t - 17.391, 3);
        else if (t <= 18.321)
            y = 0.336828 - 0.0722227 * (t - 17.856) + 5.694952e-002
                    * Math.pow(t - 17.856, 2) + 0.137013
                    * Math.pow(t - 17.856, 3);
        else if (t <= 18.786)
            y = 0.329334 + 0.0696174 * (t - 18.321) + 2.480828e-001
                    * Math.pow(t - 18.321, 2) - 0.193298
                    * Math.pow(t - 18.321, 3);
        else if (t <= 19.248)
            y = 0.395912 + 0.174947 * (t - 18.786) - 2.156847e-002
                    * Math.pow(t - 18.786, 2) - 0.0557802
                    * Math.pow(t - 18.786, 3);
        else if (t <= 19.713)
            y = 0.466634 + 0.119299 * (t - 19.248) - 9.887981e-002
                    * Math.pow(t - 19.248, 2) - 0.192986
                    * Math.pow(t - 19.248, 3);
        else if (t <= 20.178)
            y = 0.481324 - 0.097844 * (t - 19.713) - 3.680952e-001
                    * Math.pow(t - 19.713, 2) + 0.293063
                    * Math.pow(t - 19.713, 3);
        else if (t <= 20.643)
            y = 0.385701 - 0.25007 * (t - 20.178) + 4.072711e-002
                    * Math.pow(t - 20.178, 2) - 0.0291951
                    * Math.pow(t - 20.178, 3);

        return (0.038425509 - 0.013039703) * y + 0.013039703;
    }

    /**
     * Computes the approximation spline for nad or returns the default value
     * 0.528 mM if t is not in the measured time intervall. Parameter for the
     * spline: weights all equal to one, S=1
     *
     * @param t
     * @return
     */
    public double getNAD_2(double t) {
        double y = this.nad;

        if ((-3.894 <= t) && (t <= -3.429))
            y = 0.285913 + 0.197505 * (t + 3.894) + 0 * Math.pow(t + 3.894, 2)
                    - 0.00471653 * Math.pow(t + 3.894, 3);
        else if (t <= -2.964)
            y = 0.377278 + 0.194445 * (t + 3.429) - 6.579559e-003
                    * Math.pow(t + 3.429, 2) - 0.0157447
                    * Math.pow(t + 3.429, 3);
        else if (t <= -2.499)
            y = 0.464689 + 0.178113 * (t + 2.964) - 2.854341e-002
                    * Math.pow(t + 2.964, 2) - 0.0112637
                    * Math.pow(t + 2.964, 3);
        else if (t <= -2.037)
            y = 0.540208 + 0.144261 * (t + 2.499) - 4.425629e-002
                    * Math.pow(t + 2.499, 2) + 0.00268962
                    * Math.pow(t + 2.499, 3);
        else if (t <= -1.572)
            y = 0.597675 + 0.105091 * (t + 2.037) - 4.052848e-002
                    * Math.pow(t + 2.037, 2) - 0.00411981
                    * Math.pow(t + 2.037, 3);
        else if (t <= -1.107)
            y = 0.637365 + 0.0647267 * (t + 1.572) - 4.627561e-002
                    * Math.pow(t + 1.572, 2) + 0.0164141
                    * Math.pow(t + 1.572, 3);
        else if (t <= -0.642)
            y = 0.659107 + 0.0323377 * (t + 1.107) - 2.337800e-002
                    * Math.pow(t + 1.107, 2) + 0.000399521
                    * Math.pow(t + 1.107, 3);
        else if (t <= 0.363)
            y = 0.66913 + 0.0108554 * (t + 0.642) - 2.282067e-002
                    * Math.pow(t + 0.642, 2) + 0.00296352
                    * Math.pow(t + 0.642, 3);
        else if (t <= 0.828)
            y = 0.659998 - 0.0260345 * (t - 0.363) - 1.388565e-002
                    * Math.pow(t - 0.363, 2) - 0.0179798
                    * Math.pow(t - 0.363, 3);
        else if (t <= 1.293)
            y = 0.643082 - 0.0506112 * (t - 0.828) - 3.896752e-002
                    * Math.pow(t - 0.828, 2) - 0.0239499
                    * Math.pow(t - 0.828, 3);
        else if (t <= 1.758)
            y = 0.608714 - 0.102387 * (t - 1.293) - 7.237756e-002
                    * Math.pow(t - 1.293, 2) + 0.0317423
                    * Math.pow(t - 1.293, 3);
        else if (t <= 2.22)
            y = 0.548646 - 0.149107 * (t - 1.758) - 2.809708e-002
                    * Math.pow(t - 1.758, 2) - 0.0169992
                    * Math.pow(t - 1.758, 3);
        else if (t <= 2.685)
            y = 0.472084 - 0.185954 * (t - 2.22) - 5.165802e-002
                    * Math.pow(t - 2.22, 2) + 0.0502322 * Math.pow(t - 2.22, 3);
        else if (t <= 3.15)
            y = 0.379497 - 0.201412 * (t - 2.685) + 1.841597e-002
                    * Math.pow(t - 2.685, 2) + 0.0647767
                    * Math.pow(t - 2.685, 3);
        else if (t <= 4.62)
            y = 0.296335 - 0.142266 * (t - 3.15) + 1.087795e-001
                    * Math.pow(t - 3.15, 2) - 0.00660796
                    * Math.pow(t - 3.15, 3);
        else if (t <= 5.085)
            y = 0.301275 + 0.134708 * (t - 4.62) + 7.963841e-002
                    * Math.pow(t - 4.62, 2) - 0.0438438 * Math.pow(t - 4.62, 3);
        else if (t <= 5.55)
            y = 0.376726 + 0.180332 * (t - 5.085) + 1.847627e-002
                    * Math.pow(t - 5.085, 2) + 0.0145632
                    * Math.pow(t - 5.085, 3);
        else if (t <= 6.015)
            y = 0.46604 + 0.206962 * (t - 5.55) + 3.879198e-002
                    * Math.pow(t - 5.55, 2) + 0.00851535
                    * Math.pow(t - 5.55, 3);
        else if (t <= 6.477)
            y = 0.571521 + 0.248562 * (t - 6.015) + 5.067090e-002
                    * Math.pow(t - 6.015, 2) - 0.10061 * Math.pow(t - 6.015, 3);
        else if (t <= 6.942)
            y = 0.687251 + 0.230958 * (t - 6.477) - 8.877391e-002
                    * Math.pow(t - 6.477, 2) - 0.00393058
                    * Math.pow(t - 6.477, 3);
        else if (t <= 7.407)
            y = 0.775056 + 0.145849 * (t - 6.942) - 9.425707e-002
                    * Math.pow(t - 6.942, 2) + 0.00117341
                    * Math.pow(t - 6.942, 3);
        else if (t <= 7.872)
            y = 0.822613 + 0.0589508 * (t - 7.407) - 9.262016e-002
                    * Math.pow(t - 7.407, 2) - 0.0168782
                    * Math.pow(t - 7.407, 3);
        else if (t <= 8.877)
            y = 0.828301 - 0.0381344 * (t - 7.872) - 1.161653e-001
                    * Math.pow(t - 7.872, 2) + 0.0209354
                    * Math.pow(t - 7.872, 3);
        else if (t <= 9.342)
            y = 0.693897 - 0.208191 * (t - 8.877) - 5.304513e-002
                    * Math.pow(t - 8.877, 2) + 0.0832539
                    * Math.pow(t - 8.877, 3);
        else if (t <= 9.807)
            y = 0.59399 - 0.203518 * (t - 9.342) + 6.309407e-002
                    * Math.pow(t - 9.342, 2) + 0.0109974
                    * Math.pow(t - 9.342, 3);
        else if (t <= 10.272)
            y = 0.514102 - 0.137707 * (t - 9.807) + 7.843539e-002
                    * Math.pow(t - 9.807, 2) - 0.014136
                    * Math.pow(t - 9.807, 3);
        else if (t <= 10.734)
            y = 0.465606 - 0.0739317 * (t - 10.272) + 5.871560e-002
                    * Math.pow(t - 10.272, 2) - 0.00999596
                    * Math.pow(t - 10.272, 3);
        else if (t <= 11.199)
            y = 0.442997 - 0.0260793 * (t - 10.734) + 4.486120e-002
                    * Math.pow(t - 10.734, 2) - 0.0371792
                    * Math.pow(t - 10.734, 3);
        else if (t <= 11.664)
            y = 0.436832 - 0.00847559 * (t - 11.199) - 7.003852e-003
                    * Math.pow(t - 11.199, 2) - 0.00916083
                    * Math.pow(t - 11.199, 3);
        else if (t <= 12.129)
            y = 0.430455 - 0.0209316 * (t - 11.664) - 1.978321e-002
                    * Math.pow(t - 11.664, 2) + 0.0358549
                    * Math.pow(t - 11.664, 3);
        else if (t <= 13.134)
            y = 0.420049 - 0.0160718 * (t - 12.129) + 3.023440e-002
                    * Math.pow(t - 12.129, 2) - 0.0184213
                    * Math.pow(t - 12.129, 3);
        else if (t <= 13.599)
            y = 0.415736 - 0.0111184 * (t - 13.134) - 2.530570e-002
                    * Math.pow(t - 13.134, 2) + 0.0349448
                    * Math.pow(t - 13.134, 3);
        else if (t <= 14.064)
            y = 0.408608 - 0.0119849 * (t - 13.599) + 2.344233e-002
                    * Math.pow(t - 13.599, 2) - 0.0286015
                    * Math.pow(t - 13.599, 3);
        else if (t <= 14.529)
            y = 0.405228 - 0.00873664 * (t - 14.064) - 1.645683e-002
                    * Math.pow(t - 14.064, 2) - 0.0245074
                    * Math.pow(t - 14.064, 3);
        else if (t <= 14.991)
            y = 0.395143 - 0.0399389 * (t - 14.529) - 5.064472e-002
                    * Math.pow(t - 14.529, 2) - 0.0131914
                    * Math.pow(t - 14.529, 3);
        else if (t <= 15.456)
            y = 0.36458 - 0.0951814 * (t - 14.991) - 6.892794e-002
                    * Math.pow(t - 14.991, 2) + 0.0254845
                    * Math.pow(t - 14.991, 3);
        else if (t <= 15.921)
            y = 0.307979 - 0.142753 * (t - 15.456) - 3.337702e-002
                    * Math.pow(t - 15.456, 2) + 0.065268
                    * Math.pow(t - 15.456, 3);
        else if (t <= 16.386)
            y = 0.240944 - 0.131456 * (t - 15.921) + 5.767177e-002
                    * Math.pow(t - 15.921, 2) + 0.0235208
                    * Math.pow(t - 15.921, 3);
        else if (t <= 17.391)
            y = 0.194652 - 0.0625641 * (t - 16.386) + 9.048331e-002
                    * Math.pow(t - 16.386, 2) - 0.0359447
                    * Math.pow(t - 16.386, 3);
        else if (t <= 17.856)
            y = 0.186679 + 0.0103924 * (t - 17.391) - 1.788984e-002
                    * Math.pow(t - 17.391, 2) + 0.03609
                    * Math.pow(t - 17.391, 3);
        else if (t <= 18.321)
            y = 0.191272 + 0.0171655 * (t - 17.856) + 3.245578e-002
                    * Math.pow(t - 17.856, 2) + 0.015007
                    * Math.pow(t - 17.856, 3);
        else if (t <= 18.786)
            y = 0.207781 + 0.0570841 * (t - 18.321) + 5.339052e-002
                    * Math.pow(t - 18.321, 2) - 0.0492235
                    * Math.pow(t - 18.321, 3);
        else if (t <= 19.248)
            y = 0.24092 + 0.0748072 * (t - 18.786) - 1.527625e-002
                    * Math.pow(t - 18.786, 2) - 0.0351776
                    * Math.pow(t - 18.786, 3);
        else if (t <= 19.713)
            y = 0.268752 + 0.0381666 * (t - 19.248) - 6.403238e-002
                    * Math.pow(t - 19.248, 2) + 0.0247573
                    * Math.pow(t - 19.248, 3);
        else if (t <= 20.178)
            y = 0.275143 - 0.00532401 * (t - 19.713) - 2.949588e-002
                    * Math.pow(t - 19.713, 2) + 0.0447577
                    * Math.pow(t - 19.713, 3);
        else if (t <= 20.643)
            y = 0.27079 - 0.00372197 * (t - 20.178) + 3.294111e-002
                    * Math.pow(t - 20.178, 2) - 0.0236137
                    * Math.pow(t - 20.178, 3);

        return (0.690441264 - 0.344747388) * y + 0.344747388;
    }

    /*
	 * Splines of the data which should be fit normally.
	 */

    /**
     * Computes the approximation spline for Leu or returns the default value
     * 0.209 mM if t is not in the measured time intervall. Parameter for the
     * spline: weights all equal to one, S=1
     *
     * @param t
     * @return
     */
    public double getLeu(double t) {
        double y = 0.209 / (0.523231194 - 0.146383634) - 1
                / (0.523231194 - 0.146383634) * 0.146383634;

        if ((-3.894 <= t) && (t <= -3.429))
            y = 0.254575 + 0.00234099 * (t + 3.894) + 0
                    * Math.pow(t + 3.894, 2) + 0.00081644
                    * Math.pow(t + 3.894, 3);
        else if (t <= -2.964)
            y = 0.255745 + 0.0028706 * (t + 3.429) + 1.138934e-003
                    * Math.pow(t + 3.429, 2) + 0.000514239
                    * Math.pow(t + 3.429, 3);
        else if (t <= -2.499)
            y = 0.257378 + 0.00426338 * (t + 2.964) + 1.856298e-003
                    * Math.pow(t + 2.964, 2) - 4.84395e-005
                    * Math.pow(t + 2.964, 3);
        else if (t <= -2.037)
            y = 0.259757 + 0.00595832 * (t + 2.499) + 1.788725e-003
                    * Math.pow(t + 2.499, 2) + 0.000191175
                    * Math.pow(t + 2.499, 3);
        else if (t <= -1.572)
            y = 0.26291 + 0.00773351 * (t + 2.037) + 2.053693e-003
                    * Math.pow(t + 2.037, 2) + 0.000506726
                    * Math.pow(t + 2.037, 3);
        else if (t <= -1.107)
            y = 0.267001 + 0.00997215 * (t + 1.572) + 2.760576e-003
                    * Math.pow(t + 1.572, 2) + 0.000130631
                    * Math.pow(t + 1.572, 3);
        else if (t <= -0.642)
            y = 0.272249 + 0.0126242 * (t + 1.107) + 2.942806e-003
                    * Math.pow(t + 1.107, 2) + 0.000206773
                    * Math.pow(t + 1.107, 3);
        else if (t <= 0.363)
            y = 0.278776 + 0.0154952 * (t + 0.642) + 3.231255e-003
                    * Math.pow(t + 0.642, 2) + 0.000562468
                    * Math.pow(t + 0.642, 3);
        else if (t <= 0.828)
            y = 0.298183 + 0.0236943 * (t - 0.363) + 4.927094e-003
                    * Math.pow(t - 0.363, 2) - 0.000122853
                    * Math.pow(t - 0.363, 3);
        else if (t <= 1.293)
            y = 0.310254 + 0.0281968 * (t - 0.828) + 4.755714e-003
                    * Math.pow(t - 0.828, 2) - 0.00023605
                    * Math.pow(t - 0.828, 3);
        else if (t <= 1.758)
            y = 0.32437 + 0.0324665 * (t - 1.293) + 4.426424e-003
                    * Math.pow(t - 1.293, 2) + 0.000571918
                    * Math.pow(t - 1.293, 3);
        else if (t <= 2.22)
            y = 0.340482 + 0.0369541 * (t - 1.758) + 5.224250e-003
                    * Math.pow(t - 1.758, 2) + 0.000349374
                    * Math.pow(t - 1.758, 3);
        else if (t <= 2.685)
            y = 0.358704 + 0.042005 * (t - 2.22) + 5.708482e-003
                    * Math.pow(t - 2.22, 2) - 0.000457706
                    * Math.pow(t - 2.22, 3);
        else if (t <= 3.15)
            y = 0.379425 + 0.047017 * (t - 2.685) + 5.069983e-003
                    * Math.pow(t - 2.685, 2) - 0.00122241
                    * Math.pow(t - 2.685, 3);
        else if (t <= 4.62)
            y = 0.402261 + 0.0509391 * (t - 3.15) + 3.364716e-003
                    * Math.pow(t - 3.15, 2) - 0.00138177
                    * Math.pow(t - 3.15, 3);
        else if (t <= 5.085)
            y = 0.480023 + 0.0518738 * (t - 4.62) - 2.728891e-003
                    * Math.pow(t - 4.62, 2) - 0.00128619
                    * Math.pow(t - 4.62, 3);
        else if (t <= 5.55)
            y = 0.503425 + 0.0485016 * (t - 5.085) - 4.523131e-003
                    * Math.pow(t - 5.085, 2) - 0.00136501
                    * Math.pow(t - 5.085, 3);
        else if (t <= 6.015)
            y = 0.524863 + 0.0434096 * (t - 5.55) - 6.427323e-003
                    * Math.pow(t - 5.55, 2) - 0.00137626
                    * Math.pow(t - 5.55, 3);
        else if (t <= 6.477)
            y = 0.54352 + 0.0365395 * (t - 6.015) - 8.347206e-003
                    * Math.pow(t - 6.015, 2) - 0.00181134
                    * Math.pow(t - 6.015, 3);
        else if (t <= 6.942)
            y = 0.558441 + 0.0276668 * (t - 6.477) - 1.085772e-002
                    * Math.pow(t - 6.477, 2) + 6.90083e-005
                    * Math.pow(t - 6.477, 3);
        else if (t <= 7.407)
            y = 0.568965 + 0.0176139 * (t - 6.942) - 1.076145e-002
                    * Math.pow(t - 6.942, 2) + 0.000455085
                    * Math.pow(t - 6.942, 3);
        else if (t <= 7.872)
            y = 0.574875 + 0.00790092 * (t - 7.407) - 1.012661e-002
                    * Math.pow(t - 7.407, 2) + 0.000371085
                    * Math.pow(t - 7.407, 3);
        else if (t <= 8.877)
            y = 0.576396 - 0.00127611 * (t - 7.872) - 9.608945e-003
                    * Math.pow(t - 7.872, 2) + 0.00127423
                    * Math.pow(t - 7.872, 3);
        else if (t <= 9.342)
            y = 0.566702 - 0.0167291 * (t - 8.877) - 5.767152e-003
                    * Math.pow(t - 8.877, 2) + 0.00168058
                    * Math.pow(t - 8.877, 3);
        else if (t <= 9.807)
            y = 0.557845 - 0.0210024 * (t - 9.342) - 3.422739e-003
                    * Math.pow(t - 9.342, 2) + 0.000255359
                    * Math.pow(t - 9.342, 3);
        else if (t <= 10.272)
            y = 0.547364 - 0.0240199 * (t - 9.807) - 3.066513e-003
                    * Math.pow(t - 9.807, 2) + 0.000486931
                    * Math.pow(t - 9.807, 3);
        else if (t <= 10.734)
            y = 0.535581 - 0.0265559 * (t - 10.272) - 2.387245e-003
                    * Math.pow(t - 10.272, 2) + 0.000578468
                    * Math.pow(t - 10.272, 3);
        else if (t <= 11.199)
            y = 0.52286 - 0.0283913 * (t - 10.734) - 1.585488e-003
                    * Math.pow(t - 10.734, 2) - 0.000443537
                    * Math.pow(t - 10.734, 3);
        else if (t <= 11.664)
            y = 0.50927 - 0.0301535 * (t - 11.199) - 2.204222e-003
                    * Math.pow(t - 11.199, 2) - 0.0010496
                    * Math.pow(t - 11.199, 3);
        else if (t <= 12.129)
            y = 0.494667 - 0.0328843 * (t - 11.664) - 3.668415e-003
                    * Math.pow(t - 11.664, 2) + 0.000223556
                    * Math.pow(t - 11.664, 3);
        else if (t <= 13.134)
            y = 0.478605 - 0.0361509 * (t - 12.129) - 3.356554e-003
                    * Math.pow(t - 12.129, 2) + 0.000163983
                    * Math.pow(t - 12.129, 3);
        else if (t <= 13.599)
            y = 0.43905 - 0.0424007 * (t - 13.134) - 2.862147e-003
                    * Math.pow(t - 13.134, 2) + 1.01014e-005
                    * Math.pow(t - 13.134, 3);
        else if (t <= 14.064)
            y = 0.418715 - 0.0450559 * (t - 13.599) - 2.848055e-003
                    * Math.pow(t - 13.599, 2) + 0.000100977
                    * Math.pow(t - 13.599, 3);
        else if (t <= 14.529)
            y = 0.397159 - 0.0476391 * (t - 14.064) - 2.707192e-003
                    * Math.pow(t - 14.064, 2) + 0.00132926
                    * Math.pow(t - 14.064, 3);
        else if (t <= 14.991)
            y = 0.374555 - 0.0492946 * (t - 14.529) - 8.528782e-004
                    * Math.pow(t - 14.529, 2) + 0.00113715
                    * Math.pow(t - 14.529, 3);
        else if (t <= 15.456)
            y = 0.351711 - 0.0493545 * (t - 14.991) + 7.232178e-004
                    * Math.pow(t - 14.991, 2) + 0.000573763
                    * Math.pow(t - 14.991, 3);
        else if (t <= 15.921)
            y = 0.328975 - 0.0483097 * (t - 15.456) + 1.523618e-003
                    * Math.pow(t - 15.456, 2) + 0.000622783
                    * Math.pow(t - 15.456, 3);
        else if (t <= 16.386)
            y = 0.306903 - 0.0464887 * (t - 15.921) + 2.392399e-003
                    * Math.pow(t - 15.921, 2) + 0.000534396
                    * Math.pow(t - 15.921, 3);
        else if (t <= 17.391)
            y = 0.285857 - 0.0439171 * (t - 16.386) + 3.137882e-003
                    * Math.pow(t - 16.386, 2) - 0.00061327
                    * Math.pow(t - 16.386, 3);
        else if (t <= 17.856)
            y = 0.244267 - 0.0394683 * (t - 17.391) + 1.288872e-003
                    * Math.pow(t - 17.391, 2) - 0.000638732
                    * Math.pow(t - 17.391, 3);
        else if (t <= 18.321)
            y = 0.226129 - 0.0386839 * (t - 17.856) + 3.978406e-004
                    * Math.pow(t - 17.856, 2) - 0.000462192
                    * Math.pow(t - 17.856, 3);
        else if (t <= 18.786)
            y = 0.20818 - 0.0386138 * (t - 18.321) - 2.469173e-004
                    * Math.pow(t - 18.321, 2) - 0.000260252
                    * Math.pow(t - 18.321, 3);
        else if (t <= 19.248)
            y = 0.190145 - 0.0390122 * (t - 18.786) - 6.099692e-004
                    * Math.pow(t - 18.786, 2) + 0.000103577
                    * Math.pow(t - 18.786, 3);
        else if (t <= 19.713)
            y = 0.172002 - 0.0395095 * (t - 19.248) - 4.664113e-004
                    * Math.pow(t - 19.248, 2) + 0.000399364
                    * Math.pow(t - 19.248, 3);
        else if (t <= 20.178)
            y = 0.153569 - 0.0396842 * (t - 19.713) + 9.070190e-005
                    * Math.pow(t - 19.713, 2) - 0.000254598
                    * Math.pow(t - 19.713, 3);
        else if (t <= 20.643)
            y = 0.13511 - 0.039765 * (t - 20.178) - 2.644617e-004
                    * Math.pow(t - 20.178, 2) + 0.000189578
                    * Math.pow(t - 20.178, 3);

        return (0.523231194 - 0.146383634) * y + 0.146383634;
    }

    /**
     * Computes the approximation spline for AcLac or returns the default value
     * 0.236 mM if t is not in the measured time intervall. Parameter for the
     * spline: weights all equal to one, S=1
     *
     * @param t
     * @return
     */
    public double getAcLac(double t) {
        double y = 0.236 / (0.611504694 - 0.19610573) - 0.19610573
                / (0.611504694 - 0.19610573);

        if ((-3.894 <= t) && (t <= -3.429))
            y = 0.0509318 + 0.0425125 * (t + 3.894) + 0
                    * Math.pow(t + 3.894, 2) + 3.31769e-005
                    * Math.pow(t + 3.894, 3);
        else if (t <= -2.964)
            y = 0.0707035 + 0.0425341 * (t + 3.429) + 4.628182e-005
                    * Math.pow(t + 3.429, 2) + 0.000151112
                    * Math.pow(t + 3.429, 3);
        else if (t <= -2.499)
            y = 0.090507 + 0.0426751 * (t + 2.964) + 2.570833e-004
                    * Math.pow(t + 2.964, 2) + 0.000180795
                    * Math.pow(t + 2.964, 3);
        else if (t <= -2.037)
            y = 0.110425 + 0.0430315 * (t + 2.499) + 5.092922e-004
                    * Math.pow(t + 2.499, 2) + 0.000385144
                    * Math.pow(t + 2.499, 3);
        else if (t <= -1.107)
            y = 0.130452 + 0.0437487 * (t + 2.037) + 1.043102e-003
                    * Math.pow(t + 2.037, 2) + 0.000330749
                    * Math.pow(t + 2.037, 3);
        else if (t <= -0.642)
            y = 0.172306 + 0.0465471 * (t + 1.107) + 1.965893e-003
                    * Math.pow(t + 1.107, 2) + 0.000118734
                    * Math.pow(t + 1.107, 3);
        else if (t <= 0.363)
            y = 0.194388 + 0.0484524 * (t + 0.642) + 2.131527e-003
                    * Math.pow(t + 0.642, 2) - 6.48454e-006
                    * Math.pow(t + 0.642, 3);
        else if (t <= 0.828)
            y = 0.245229 + 0.0527171 * (t - 0.363) + 2.111976e-003
                    * Math.pow(t - 0.363, 2) - 0.000142806
                    * Math.pow(t - 0.363, 3);
        else if (t <= 1.293)
            y = 0.270185 + 0.0545886 * (t - 0.828) + 1.912761e-003
                    * Math.pow(t - 0.828, 2) - 6.09975e-005
                    * Math.pow(t - 0.828, 3);
        else if (t <= 1.758)
            y = 0.295976 + 0.0563279 * (t - 1.293) + 1.827670e-003
                    * Math.pow(t - 1.293, 2) - 0.000197946
                    * Math.pow(t - 1.293, 3);
        else if (t <= 2.685)
            y = 0.322543 + 0.0578992 * (t - 1.758) + 1.551535e-003
                    * Math.pow(t - 1.758, 2) - 0.000443952
                    * Math.pow(t - 1.758, 3);
        else if (t <= 3.15)
            y = 0.377196 + 0.0596313 * (t - 2.685) + 3.169039e-004
                    * Math.pow(t - 2.685, 2) - 0.000757317
                    * Math.pow(t - 2.685, 3);
        else if (t <= 4.62)
            y = 0.404917 + 0.0594347 * (t - 3.15) - 7.395529e-004
                    * Math.pow(t - 3.15, 2) - 0.000469621
                    * Math.pow(t - 3.15, 3);
        else if (t <= 5.085)
            y = 0.489196 + 0.054216 * (t - 4.62) - 2.810583e-003
                    * Math.pow(t - 4.62, 2) - 0.000383922
                    * Math.pow(t - 4.62, 3);
        else if (t <= 5.55)
            y = 0.51376 + 0.0513531 * (t - 5.085) - 3.346154e-003
                    * Math.pow(t - 5.085, 2) - 0.000500431
                    * Math.pow(t - 5.085, 3);
        else if (t <= 6.015)
            y = 0.536865 + 0.0479166 * (t - 5.55) - 4.044255e-003
                    * Math.pow(t - 5.55, 2) - 0.000254017
                    * Math.pow(t - 5.55, 3);
        else if (t <= 6.477)
            y = 0.558246 + 0.0439907 * (t - 6.015) - 4.398609e-003
                    * Math.pow(t - 6.015, 2) - 0.000388999
                    * Math.pow(t - 6.015, 3);
        else if (t <= 6.942)
            y = 0.577593 + 0.0396773 * (t - 6.477) - 4.937762e-003
                    * Math.pow(t - 6.477, 2) - 0.000299882
                    * Math.pow(t - 6.477, 3);
        else if (t <= 7.407)
            y = 0.594945 + 0.0348906 * (t - 6.942) - 5.356098e-003
                    * Math.pow(t - 6.942, 2) - 0.000545857
                    * Math.pow(t - 6.942, 3);
        else if (t <= 7.872)
            y = 0.609956 + 0.0295554 * (t - 7.407) - 6.117569e-003
                    * Math.pow(t - 7.407, 2) - 0.000382667
                    * Math.pow(t - 7.407, 3);
        else if (t <= 8.877)
            y = 0.622338 + 0.0236178 * (t - 7.872) - 6.651389e-003
                    * Math.pow(t - 7.872, 2) + 8.20287e-005
                    * Math.pow(t - 7.872, 3);
        else if (t <= 9.342)
            y = 0.639439 + 0.0104971 * (t - 8.877) - 6.404073e-003
                    * Math.pow(t - 8.877, 2) + 6.89544e-006
                    * Math.pow(t - 8.877, 3);
        else if (t <= 9.807)
            y = 0.642936 + 0.00454574 * (t - 9.342) - 6.394454e-003
                    * Math.pow(t - 9.342, 2) - 0.000265083
                    * Math.pow(t - 9.342, 3);
        else if (t <= 10.272)
            y = 0.643641 - 0.00157305 * (t - 9.807) - 6.764245e-003
                    * Math.pow(t - 9.807, 2) + 0.000121087
                    * Math.pow(t - 9.807, 3);
        else if (t <= 11.199)
            y = 0.641459 - 0.00778526 * (t - 10.272) - 6.595329e-003
                    * Math.pow(t - 10.272, 2) + 0.000427353
                    * Math.pow(t - 10.272, 3);
        else if (t <= 11.664)
            y = 0.628915 - 0.0189113 * (t - 11.199) - 5.406861e-003
                    * Math.pow(t - 11.199, 2) + 0.000486639
                    * Math.pow(t - 11.199, 3);
        else if (t <= 12.129)
            y = 0.619001 - 0.023624 * (t - 11.664) - 4.727999e-003
                    * Math.pow(t - 11.664, 2) + 0.000578078
                    * Math.pow(t - 11.664, 3);
        else if (t <= 13.599)
            y = 0.607052 - 0.027646 * (t - 12.129) - 3.921580e-003
                    * Math.pow(t - 12.129, 2) + 0.000195159
                    * Math.pow(t - 12.129, 3);
        else if (t <= 14.064)
            y = 0.558558 - 0.0379103 * (t - 13.599) - 3.060930e-003
                    * Math.pow(t - 13.599, 2) + 0.000134031
                    * Math.pow(t - 13.599, 3);
        else if (t <= 14.529)
            y = 0.540281 - 0.0406701 * (t - 14.064) - 2.873957e-003
                    * Math.pow(t - 14.064, 2) + 0.000338369
                    * Math.pow(t - 14.064, 3);
        else if (t <= 15.456)
            y = 0.520782 - 0.0431233 * (t - 14.529) - 2.401931e-003
                    * Math.pow(t - 14.529, 2) + 0.000422293
                    * Math.pow(t - 14.529, 3);
        else if (t <= 15.921)
            y = 0.479079 - 0.0464879 * (t - 15.456) - 1.227535e-003
                    * Math.pow(t - 15.456, 2) + 0.000404677
                    * Math.pow(t - 15.456, 3);
        else if (t <= 17.391)
            y = 0.457237 - 0.047367 * (t - 15.921) - 6.630106e-004
                    * Math.pow(t - 15.921, 2) + 0.000286727
                    * Math.pow(t - 15.921, 3);
        else if (t <= 17.856)
            y = 0.387086 - 0.0474575 * (t - 17.391) + 6.014537e-004
                    * Math.pow(t - 17.391, 2) + 0.000196116
                    * Math.pow(t - 17.391, 3);
        else if (t <= 18.321)
            y = 0.365168 - 0.0467709 * (t - 17.856) + 8.750362e-004
                    * Math.pow(t - 17.856, 2) + 5.04267e-005
                    * Math.pow(t - 17.856, 3);
        else if (t <= 18.786)
            y = 0.343614 - 0.0459244 * (t - 18.321) + 9.453814e-004
                    * Math.pow(t - 18.321, 2) - 0.000240636
                    * Math.pow(t - 18.321, 3);
        else if (t <= 19.248)
            y = 0.322439 - 0.0452013 * (t - 18.786) + 6.096940e-004
                    * Math.pow(t - 18.786, 2) - 0.000241968
                    * Math.pow(t - 18.786, 3);
        else if (t <= 20.178)
            y = 0.301663 - 0.0447929 * (t - 19.248) + 2.743264e-004
                    * Math.pow(t - 19.248, 2) - 6.72243e-005
                    * Math.pow(t - 19.248, 3);
        else if (t <= 20.643)
            y = 0.260188 - 0.044457 * (t - 20.178) + 8.677062e-005
                    * Math.pow(t - 20.178, 2) - 6.22012e-005
                    * Math.pow(t - 20.178, 3);

        return (0.611504694 - 0.19610573) * y + 0.19610573;
    }

    /**
     * Computes the approximation spline for KIC or returns the default value
     * 0.0741 mM if t is not in the measured time intervall. Parameter for the
     * spline: weights all equal to one, S=1
     *
     * @param t
     * @return
     */
    public double getKIC(double t) {
        double y = 0.0741 / (0.160177175 - 0.044088231) - 0.044088231
                / (0.160177175 - 0.044088231);

        if ((-3.894 <= t) && (t <= -3.429))
            y = 0.59717 - 0.253511 * (t + 3.894) + 0 * Math.pow(t + 3.894, 2)
                    + 0.030027 * Math.pow(t + 3.894, 3);
        else if (t <= -2.964)
            y = 0.482307 - 0.234033 * (t + 3.429) + 4.188770e-002
                    * Math.pow(t + 3.429, 2) + 0.00577486
                    * Math.pow(t + 3.429, 3);
        else if (t <= -2.499)
            y = 0.38312 - 0.191331 * (t + 2.964) + 4.994363e-002
                    * Math.pow(t + 2.964, 2) - 0.0102897
                    * Math.pow(t + 2.964, 3);
        else if (t <= -2.037)
            y = 0.303915 - 0.151558 * (t + 2.499) + 3.558955e-002
                    * Math.pow(t + 2.499, 2) + 0.0218333
                    * Math.pow(t + 2.499, 3);
        else if (t <= -1.572)
            y = 0.243645 - 0.104693 * (t + 2.037) + 6.585046e-002
                    * Math.pow(t + 2.037, 2) + 0.0141498
                    * Math.pow(t + 2.037, 3);
        else if (t <= -1.107)
            y = 0.210624 - 0.0342735 * (t + 1.572) + 8.558938e-002
                    * Math.pow(t + 1.572, 2) - 0.0352163
                    * Math.pow(t + 1.572, 3);
        else if (t <= -0.642)
            y = 0.209652 + 0.0224808 * (t + 1.107) + 3.646268e-002
                    * Math.pow(t + 1.107, 2) - 0.00295964
                    * Math.pow(t + 1.107, 3);
        else if (t <= 0.363)
            y = 0.227692 + 0.0544712 * (t + 0.642) + 3.233398e-002
                    * Math.pow(t + 0.642, 2) + 0.00254454
                    * Math.pow(t + 0.642, 3);
        else if (t <= 0.828)
            y = 0.317677 + 0.127173 * (t - 0.363) + 4.000576e-002
                    * Math.pow(t - 0.363, 2) - 0.0129362
                    * Math.pow(t - 0.363, 3);
        else if (t <= 1.293)
            y = 0.384162 + 0.155987 * (t - 0.828) + 2.195974e-002
                    * Math.pow(t - 0.828, 2) - 0.0561687
                    * Math.pow(t - 0.828, 3);
        else if (t <= 1.758)
            y = 0.455796 + 0.139974 * (t - 1.293) - 5.639562e-002
                    * Math.pow(t - 1.293, 2) - 0.00820249
                    * Math.pow(t - 1.293, 3);
        else if (t <= 2.22)
            y = 0.507865 + 0.0822052 * (t - 1.758) - 6.783810e-002
                    * Math.pow(t - 1.758, 2) + 0.0262569
                    * Math.pow(t - 1.758, 3);
        else if (t <= 2.685)
            y = 0.533954 + 0.036336 * (t - 2.22) - 3.144602e-002
                    * Math.pow(t - 2.22, 2) + 0.0235729 * Math.pow(t - 2.22, 3);
        else if (t <= 3.15)
            y = 0.546421 + 0.0223823 * (t - 2.685) + 1.438178e-003
                    * Math.pow(t - 2.685, 2) + 0.00200063
                    * Math.pow(t - 2.685, 3);
        else if (t <= 4.62)
            y = 0.557341 + 0.0250176 * (t - 3.15) + 4.229058e-003
                    * Math.pow(t - 3.15, 2) + 0.00309548
                    * Math.pow(t - 3.15, 3);
        else if (t <= 5.085)
            y = 0.613088 + 0.0575181 * (t - 4.62) + 1.788011e-002
                    * Math.pow(t - 4.62, 2) - 0.0103307 * Math.pow(t - 4.62, 3);
        else if (t <= 5.55)
            y = 0.642661 + 0.0674453 * (t - 5.085) + 3.468802e-003
                    * Math.pow(t - 5.085, 2) - 0.0224198
                    * Math.pow(t - 5.085, 3);
        else if (t <= 6.015)
            y = 0.672519 + 0.0561282 * (t - 5.55) - 2.780675e-002
                    * Math.pow(t - 5.55, 2) - 0.00483441
                    * Math.pow(t - 5.55, 3);
        else if (t <= 6.477)
            y = 0.69212 + 0.0271319 * (t - 6.015) - 3.455076e-002
                    * Math.pow(t - 6.015, 2) - 0.00777974
                    * Math.pow(t - 6.015, 3);
        else if (t <= 6.942)
            y = 0.696513 - 0.0097746 * (t - 6.477) - 4.533348e-002
                    * Math.pow(t - 6.477, 2) + 0.0117564
                    * Math.pow(t - 6.477, 3);
        else if (t <= 7.407)
            y = 0.683348 - 0.0443086 * (t - 6.942) - 2.893330e-002
                    * Math.pow(t - 6.942, 2) + 0.019275
                    * Math.pow(t - 6.942, 3);
        else if (t <= 7.872)
            y = 0.658426 - 0.0587134 * (t - 7.407) - 2.044715e-003
                    * Math.pow(t - 7.407, 2) - 0.00188787
                    * Math.pow(t - 7.407, 3);
        else if (t <= 8.877)
            y = 0.630492 - 0.0618396 * (t - 7.872) - 4.678294e-003
                    * Math.pow(t - 7.872, 2) - 9.30383e-005
                    * Math.pow(t - 7.872, 3);
        else if (t <= 9.342)
            y = 0.563524 - 0.0715249 * (t - 8.877) - 4.958805e-003
                    * Math.pow(t - 8.877, 2) + 0.0222099
                    * Math.pow(t - 8.877, 3);
        else if (t <= 9.807)
            y = 0.531426 - 0.0617296 * (t - 9.342) + 2.602397e-002
                    * Math.pow(t - 9.342, 2) - 0.022373
                    * Math.pow(t - 9.342, 3);
        else if (t <= 10.272)
            y = 0.506099 - 0.0520401 * (t - 9.807) - 5.186390e-003
                    * Math.pow(t - 9.807, 2) - 0.00875147
                    * Math.pow(t - 9.807, 3);
        else if (t <= 10.734)
            y = 0.479899 - 0.0625403 * (t - 10.272) - 1.739469e-002
                    * Math.pow(t - 10.272, 2) + 0.0272157
                    * Math.pow(t - 10.272, 3);
        else if (t <= 11.199)
            y = 0.449976 - 0.061186 * (t - 10.734) + 2.032624e-002
                    * Math.pow(t - 10.734, 2) - 0.0227538
                    * Math.pow(t - 10.734, 3);
        else if (t <= 11.664)
            y = 0.423632 - 0.0570424 * (t - 11.199) - 1.141533e-002
                    * Math.pow(t - 11.199, 2) - 0.0117263
                    * Math.pow(t - 11.199, 3);
        else if (t <= 12.129)
            y = 0.39346 - 0.0752652 * (t - 11.664) - 2.777356e-002
                    * Math.pow(t - 11.664, 2) + 0.0185337
                    * Math.pow(t - 11.664, 3);
        else if (t <= 13.134)
            y = 0.35432 - 0.0890723 * (t - 12.129) - 1.918985e-003
                    * Math.pow(t - 12.129, 2) + 0.0246405
                    * Math.pow(t - 12.129, 3);
        else if (t <= 13.599)
            y = 0.287876 - 0.0182668 * (t - 13.134) + 7.237216e-002
                    * Math.pow(t - 13.134, 2) - 0.0167059
                    * Math.pow(t - 13.134, 3);
        else if (t <= 14.064)
            y = 0.293351 + 0.0382026 * (t - 13.599) + 4.906743e-002
                    * Math.pow(t - 13.599, 2) - 0.0121101
                    * Math.pow(t - 13.599, 3);
        else if (t <= 14.529)
            y = 0.320507 + 0.0759798 * (t - 14.064) + 3.217383e-002
                    * Math.pow(t - 14.064, 2) - 0.0148388
                    * Math.pow(t - 14.064, 3);
        else if (t <= 14.991)
            y = 0.361303 + 0.0962759 * (t - 14.529) + 1.147372e-002
                    * Math.pow(t - 14.529, 2) + 0.0191581
                    * Math.pow(t - 14.529, 3);
        else if (t <= 15.456)
            y = 0.41012 + 0.119145 * (t - 14.991) + 3.802679e-002
                    * Math.pow(t - 14.991, 2) - 0.0570551
                    * Math.pow(t - 14.991, 3);
        else if (t <= 15.921)
            y = 0.468009 + 0.1175 * (t - 15.456) - 4.156503e-002
                    * Math.pow(t - 15.456, 2) - 0.0132887
                    * Math.pow(t - 15.456, 3);
        else if (t <= 16.386)
            y = 0.512323 + 0.0702243 * (t - 15.921) - 6.010273e-002
                    * Math.pow(t - 15.921, 2) + 0.0551508
                    * Math.pow(t - 15.921, 3);
        else if (t <= 17.391)
            y = 0.537526 + 0.0501038 * (t - 16.386) + 1.683269e-002
                    * Math.pow(t - 16.386, 2) - 0.016235
                    * Math.pow(t - 16.386, 3);
        else if (t <= 17.856)
            y = 0.588402 + 0.0347443 * (t - 17.391) - 3.211571e-002
                    * Math.pow(t - 17.391, 2) + 0.00952971
                    * Math.pow(t - 17.391, 3);
        else if (t <= 18.321)
            y = 0.598572 + 0.0110584 * (t - 17.856) - 1.882176e-002
                    * Math.pow(t - 17.856, 2) + 0.000769216
                    * Math.pow(t - 17.856, 3);
        else if (t <= 18.786)
            y = 0.599722 - 0.00594685 * (t - 18.321) - 1.774871e-002
                    * Math.pow(t - 18.321, 2) + 0.0316399
                    * Math.pow(t - 18.321, 3);
        else if (t <= 19.248)
            y = 0.5963 - 0.00192916 * (t - 18.786) + 2.638891e-002
                    * Math.pow(t - 18.786, 2) + 0.00874813
                    * Math.pow(t - 18.786, 3);
        else if (t <= 19.713)
            y = 0.601904 + 0.0280559 * (t - 19.248) + 3.851381e-002
                    * Math.pow(t - 19.248, 2) + 0.00607335
                    * Math.pow(t - 19.248, 3);
        else if (t <= 20.178)
            y = 0.623889 + 0.0678134 * (t - 19.713) + 4.698613e-002
                    * Math.pow(t - 19.713, 2) - 0.0566996
                    * Math.pow(t - 19.713, 3);
        else if (t <= 20.643)
            y = 0.659881 + 0.0747309 * (t - 20.178) - 3.210978e-002
                    * Math.pow(t - 20.178, 2) + 0.0230178
                    * Math.pow(t - 20.178, 3);

        return (0.160177175 - 0.044088231) * y + 0.044088231;
    }

    /**
     * Computes the approximation spline for 2IPM or returns the default value
     * 0.0227 mM if t is not in the measured time intervall. Parameter for the
     * spline: weights all equal to one, S=1
     *
     * @param t
     * @return
     */
    public double getIPM(double t) {
        double y = 0.0227 / (0.036305123 - 0.015512247) - 0.015512247
                / (0.036305123 - 0.015512247);

        if ((-3.894 <= t) && (t <= -3.429))
            y = 0.0802829 + 0.147166 * (t + 3.894) + 0 * Math.pow(t + 3.894, 2)
                    - 0.00476735 * Math.pow(t + 3.894, 3);
        else if (t <= -2.964)
            y = 0.148236 + 0.144073 * (t + 3.429) - 6.650452e-003
                    * Math.pow(t + 3.429, 2) - 0.0159672
                    * Math.pow(t + 3.429, 3);
        else if (t <= -2.499)
            y = 0.212186 + 0.127531 * (t + 2.964) - 2.892476e-002
                    * Math.pow(t + 2.964, 2) - 0.00496333
                    * Math.pow(t + 2.964, 3);
        else if (t <= -2.037)
            y = 0.264735 + 0.0974111 * (t + 2.499) - 3.584861e-002
                    * Math.pow(t + 2.499, 2) + 0.00466081
                    * Math.pow(t + 2.499, 3);
        else if (t <= -1.572)
            y = 0.302547 + 0.0672715 * (t + 2.037) - 2.938873e-002
                    * Math.pow(t + 2.037, 2) + 0.0120304
                    * Math.pow(t + 2.037, 3);
        else if (t <= -1.107)
            y = 0.328683 + 0.0477438 * (t + 1.572) - 1.260631e-002
                    * Math.pow(t + 1.572, 2) + 0.00568681
                    * Math.pow(t + 1.572, 3);
        else if (t <= -0.642)
            y = 0.34873 + 0.0397088 * (t + 1.107) - 4.673207e-003
                    * Math.pow(t + 1.107, 2) - 6.69227e-006
                    * Math.pow(t + 1.107, 3);
        else if (t <= 0.363)
            y = 0.366183 + 0.0353584 * (t + 0.642) - 4.682543e-003
                    * Math.pow(t + 0.642, 2) + 0.00262408
                    * Math.pow(t + 0.642, 3);
        else if (t <= 0.828)
            y = 0.399652 + 0.0338976 * (t - 0.363) + 3.229045e-003
                    * Math.pow(t - 0.363, 2) - 0.0171221
                    * Math.pow(t - 0.363, 3);
        else if (t <= 1.293)
            y = 0.414391 + 0.0257939 * (t - 0.828) - 2.065624e-002
                    * Math.pow(t - 0.828, 2) - 0.0198038
                    * Math.pow(t - 0.828, 3);
        else if (t <= 1.758)
            y = 0.419928 - 0.00626258 * (t - 1.293) - 4.828252e-002
                    * Math.pow(t - 1.293, 2) + 0.000911555
                    * Math.pow(t - 1.293, 3);
        else if (t <= 2.22)
            y = 0.406668 - 0.050574 * (t - 1.758) - 4.701090e-002
                    * Math.pow(t - 1.758, 2) + 0.0126306
                    * Math.pow(t - 1.758, 3);
        else if (t <= 2.685)
            y = 0.374514 - 0.0859243 * (t - 2.22) - 2.950484e-002
                    * Math.pow(t - 2.22, 2) + 0.0240615 * Math.pow(t - 2.22, 3);
        else if (t <= 4.62)
            y = 0.330599 - 0.0977557 * (t - 2.685) + 4.060973e-003
                    * Math.pow(t - 2.685, 2) + 0.0170498
                    * Math.pow(t - 2.685, 3);
        else if (t <= 5.085)
            y = 0.280174 + 0.109475 * (t - 4.62) + 1.030351e-001
                    * Math.pow(t - 4.62, 2) - 0.00730802
                    * Math.pow(t - 4.62, 3);
        else if (t <= 5.55)
            y = 0.352624 + 0.200557 * (t - 5.085) + 9.284036e-002
                    * Math.pow(t - 5.085, 2) - 0.0489522
                    * Math.pow(t - 5.085, 3);
        else if (t <= 6.015)
            y = 0.461035 + 0.255145 * (t - 5.55) + 2.455205e-002
                    * Math.pow(t - 5.55, 2) - 0.020076 * Math.pow(t - 5.55, 3);
        else if (t <= 6.477)
            y = 0.582968 + 0.264955 * (t - 6.015) - 3.453901e-003
                    * Math.pow(t - 6.015, 2) - 0.0597257
                    * Math.pow(t - 6.015, 3);
        else if (t <= 6.942)
            y = 0.69875 + 0.22352 * (t - 6.477) - 8.623371e-002
                    * Math.pow(t - 6.477, 2) - 0.0241487
                    * Math.pow(t - 6.477, 3);
        else if (t <= 7.407)
            y = 0.781613 + 0.127658 * (t - 6.942) - 1.199211e-001
                    * Math.pow(t - 6.942, 2) + 0.00139353
                    * Math.pow(t - 6.942, 3);
        else if (t <= 7.872)
            y = 0.815184 + 0.017035 * (t - 7.407) - 1.179771e-001
                    * Math.pow(t - 7.407, 2) + 0.012143
                    * Math.pow(t - 7.407, 3);
        else if (t <= 8.877)
            y = 0.798817 - 0.0848069 * (t - 7.872) - 1.010377e-001
                    * Math.pow(t - 7.872, 2) + 0.0267053
                    * Math.pow(t - 7.872, 3);
        else if (t <= 9.342)
            y = 0.638643 - 0.206974 * (t - 8.877) - 2.052130e-002
                    * Math.pow(t - 8.877, 2) + 0.0393006
                    * Math.pow(t - 8.877, 3);
        else if (t <= 9.807)
            y = 0.541914 - 0.200565 * (t - 9.342) + 3.430310e-002
                    * Math.pow(t - 9.342, 2) + 0.0182556
                    * Math.pow(t - 9.342, 3);
        else if (t <= 10.272)
            y = 0.457904 - 0.156821 * (t - 9.807) + 5.976968e-002
                    * Math.pow(t - 9.807, 2) + 0.0154081
                    * Math.pow(t - 9.807, 3);
        else if (t <= 10.734)
            y = 0.399455 - 0.0912406 * (t - 10.272) + 8.126393e-002
                    * Math.pow(t - 10.272, 2) + 0.00115016
                    * Math.pow(t - 10.272, 3);
        else if (t <= 11.199)
            y = 0.374761 - 0.0154163 * (t - 10.734) + 8.285805e-002
                    * Math.pow(t - 10.734, 2) - 0.0285911
                    * Math.pow(t - 10.734, 3);
        else if (t <= 11.664)
            y = 0.382634 + 0.0430954 * (t - 11.199) + 4.297352e-002
                    * Math.pow(t - 11.199, 2) - 0.0330048
                    * Math.pow(t - 11.199, 3);
        else if (t <= 12.129)
            y = 0.408646 + 0.0616514 * (t - 11.664) - 3.068147e-003
                    * Math.pow(t - 11.664, 2) - 0.0116522
                    * Math.pow(t - 11.664, 3);
        else if (t <= 13.134)
            y = 0.435479 + 0.0512395 * (t - 12.129) - 1.932292e-002
                    * Math.pow(t - 12.129, 2) + 0.00415678
                    * Math.pow(t - 12.129, 3);
        else if (t <= 13.599)
            y = 0.471678 + 0.0249958 * (t - 13.134) - 6.790222e-003
                    * Math.pow(t - 13.134, 2) - 0.00667273
                    * Math.pow(t - 13.134, 3);
        else if (t <= 14.064)
            y = 0.481162 + 0.0143525 * (t - 13.599) - 1.609868e-002
                    * Math.pow(t - 13.599, 2) - 0.00529352
                    * Math.pow(t - 13.599, 3);
        else if (t <= 14.529)
            y = 0.483823 - 0.00405304 * (t - 14.064) - 2.348313e-002
                    * Math.pow(t - 14.064, 2) + 0.0139118
                    * Math.pow(t - 14.064, 3);
        else if (t <= 14.991)
            y = 0.478259 - 0.0168681 * (t - 14.529) - 4.076173e-003
                    * Math.pow(t - 14.529, 2) + 0.0177102
                    * Math.pow(t - 14.529, 3);
        else if (t <= 15.456)
            y = 0.471342 - 0.00929412 * (t - 14.991) + 2.047010e-002
                    * Math.pow(t - 14.991, 2) - 0.00568797
                    * Math.pow(t - 14.991, 3);
        else if (t <= 15.921)
            y = 0.470875 + 0.00605343 * (t - 15.456) + 1.253539e-002
                    * Math.pow(t - 15.456, 2) - 0.00718501
                    * Math.pow(t - 15.456, 3);
        else if (t <= 17.391)
            y = 0.475678 + 0.0130506 * (t - 15.921) + 2.512302e-003
                    * Math.pow(t - 15.921, 2) + 0.000255298
                    * Math.pow(t - 15.921, 3);
        else if (t <= 17.856)
            y = 0.501102 + 0.0220918 * (t - 17.391) + 3.638165e-003
                    * Math.pow(t - 17.391, 2) - 0.00625479
                    * Math.pow(t - 17.391, 3);
        else if (t <= 18.321)
            y = 0.511532 + 0.021418 * (t - 17.856) - 5.087271e-003
                    * Math.pow(t - 17.856, 2) - 0.00605154
                    * Math.pow(t - 17.856, 3);
        else if (t <= 18.786)
            y = 0.519783 + 0.0127613 * (t - 18.321) - 1.352917e-002
                    * Math.pow(t - 18.321, 2) + 0.004627
                    * Math.pow(t - 18.321, 3);
        else if (t <= 19.248)
            y = 0.523257 + 0.0031806 * (t - 18.786) - 7.074514e-003
                    * Math.pow(t - 18.786, 2) + 0.0194544
                    * Math.pow(t - 18.786, 3);
        else if (t <= 19.713)
            y = 0.525135 + 0.00910103 * (t - 19.248) + 1.988931e-002
                    * Math.pow(t - 19.248, 2) + 0.0103778
                    * Math.pow(t - 19.248, 3);
        else if (t <= 20.178)
            y = 0.534711 + 0.0343299 * (t - 19.713) + 3.436634e-002
                    * Math.pow(t - 19.713, 2) - 0.0213188
                    * Math.pow(t - 19.713, 3);
        else if (t <= 20.643)
            y = 0.555962 + 0.0524616 * (t - 20.178) + 4.626590e-003
                    * Math.pow(t - 20.178, 2) - 0.00331655
                    * Math.pow(t - 20.178, 3);

        return (0.036305123 - 0.015512247) * y + 0.015512247;
    }

    /**
     * Computes the approximation spline for Val or returns the default value
     * 29.4 mM if t is not in the measured time intervall. Parameter for the
     * spline: weights all equal to one, S=1
     *
     * @param t
     * @return
     */
    public double getVal(double t) {
        double y = 29.4 / (52.21398298 - 23.23780872) - 23.23780872
                / (52.21398298 - 23.23780872);

        if ((-3.894 <= t) && (t <= -3.429))
            y = 0.0870064 + 0.0543556 * (t + 3.894) + 0
                    * Math.pow(t + 3.894, 2) - 8.90776e-006
                    * Math.pow(t + 3.894, 3);
        else if (t <= -2.964)
            y = 0.112281 + 0.0543499 * (t + 3.429) - 1.242632e-005
                    * Math.pow(t + 3.429, 2) + 1.75913e-005
                    * Math.pow(t + 3.429, 3);
        else if (t <= -2.499)
            y = 0.137553 + 0.0543497 * (t + 2.964) + 1.211360e-005
                    * Math.pow(t + 2.964, 2) + 2.31931e-005
                    * Math.pow(t + 2.964, 3);
        else if (t <= -2.037)
            y = 0.16283 + 0.054376 * (t + 2.499) + 4.446802e-005
                    * Math.pow(t + 2.499, 2) + 9.65747e-005
                    * Math.pow(t + 2.499, 3);
        else if (t <= -1.572)
            y = 0.187971 + 0.054479 * (t + 2.037) + 1.783206e-004
                    * Math.pow(t + 2.037, 2) + 9.66717e-005
                    * Math.pow(t + 2.037, 3);
        else if (t <= -1.107)
            y = 0.213352 + 0.0547075 * (t + 1.572) + 3.131776e-004
                    * Math.pow(t + 1.572, 2) - 2.3386e-005
                    * Math.pow(t + 1.572, 3);
        else if (t <= -0.642)
            y = 0.238856 + 0.0549836 * (t + 1.107) + 2.805541e-004
                    * Math.pow(t + 1.107, 2) - 6.40501e-005
                    * Math.pow(t + 1.107, 3);
        else if (t <= 0.363)
            y = 0.264478 + 0.055203 * (t + 0.642) + 1.912042e-004
                    * Math.pow(t + 0.642, 2) - 5.51172e-005
                    * Math.pow(t + 0.642, 3);
        else if (t <= 0.828)
            y = 0.320094 + 0.0554203 * (t - 0.363) + 2.502589e-005
                    * Math.pow(t - 0.363, 2) - 0.000155647
                    * Math.pow(t - 0.363, 3);
        else if (t <= 1.293)
            y = 0.345854 + 0.0553426 * (t - 0.828) - 1.921018e-004
                    * Math.pow(t - 0.828, 2) - 0.000211048
                    * Math.pow(t - 0.828, 3);
        else if (t <= 1.758)
            y = 0.371526 + 0.055027 * (t - 1.293) - 4.865138e-004
                    * Math.pow(t - 1.293, 2) - 0.000182501
                    * Math.pow(t - 1.293, 3);
        else if (t <= 2.22)
            y = 0.39699 + 0.0544562 * (t - 1.758) - 7.411022e-004
                    * Math.pow(t - 1.758, 2) - 0.000172656
                    * Math.pow(t - 1.758, 3);
        else if (t <= 2.685)
            y = 0.421973 + 0.0536608 * (t - 2.22) - 9.804033e-004
                    * Math.pow(t - 2.22, 2) - 0.000243037
                    * Math.pow(t - 2.22, 3);
        else if (t <= 3.15)
            y = 0.446689 + 0.0525914 * (t - 2.685) - 1.319440e-003
                    * Math.pow(t - 2.685, 2) - 0.000377847
                    * Math.pow(t - 2.685, 3);
        else if (t <= 4.62)
            y = 0.470821 + 0.0511192 * (t - 3.15) - 1.846537e-003
                    * Math.pow(t - 3.15, 2) - 0.000398585
                    * Math.pow(t - 3.15, 3);
        else if (t <= 5.085)
            y = 0.54071 + 0.0431065 * (t - 4.62) - 3.604295e-003
                    * Math.pow(t - 4.62, 2) - 0.000432186
                    * Math.pow(t - 4.62, 3);
        else if (t <= 5.55)
            y = 0.559932 + 0.0394742 * (t - 5.085) - 4.207195e-003
                    * Math.pow(t - 5.085, 2) - 0.000381397
                    * Math.pow(t - 5.085, 3);
        else if (t <= 6.015)
            y = 0.577339 + 0.0353141 * (t - 5.55) - 4.739244e-003
                    * Math.pow(t - 5.55, 2) - 0.00029943
                    * Math.pow(t - 5.55, 3);
        else if (t <= 6.477)
            y = 0.592705 + 0.0307123 * (t - 6.015) - 5.156948e-003
                    * Math.pow(t - 6.015, 2) - 0.0002747
                    * Math.pow(t - 6.015, 3);
        else if (t <= 6.942)
            y = 0.605767 + 0.0257714 * (t - 6.477) - 5.537682e-003
                    * Math.pow(t - 6.477, 2) - 6.94366e-005
                    * Math.pow(t - 6.477, 3);
        else if (t <= 7.407)
            y = 0.616546 + 0.0205763 * (t - 6.942) - 5.634546e-003
                    * Math.pow(t - 6.942, 2) + 4.67127e-005
                    * Math.pow(t - 6.942, 3);
        else if (t <= 7.872)
            y = 0.6249 + 0.0153665 * (t - 7.407) - 5.569382e-003
                    * Math.pow(t - 7.407, 2) + 7.60338e-005
                    * Math.pow(t - 7.407, 3);
        else if (t <= 8.877)
            y = 0.630849 + 0.0102363 * (t - 7.872) - 5.463315e-003
                    * Math.pow(t - 7.872, 2) + 0.000283763
                    * Math.pow(t - 7.872, 3);
        else if (t <= 9.342)
            y = 0.635907 + 0.000114872 * (t - 8.877) - 4.607770e-003
                    * Math.pow(t - 8.877, 2) + 0.000347457
                    * Math.pow(t - 8.877, 3);
        else if (t <= 9.807)
            y = 0.634999 - 0.00394497 * (t - 9.342) - 4.123067e-003
                    * Math.pow(t - 9.342, 2) + 0.000216058
                    * Math.pow(t - 9.342, 3);
        else if (t <= 10.272)
            y = 0.632294 - 0.00763927 * (t - 9.807) - 3.821667e-003
                    * Math.pow(t - 9.807, 2) + 0.000216223
                    * Math.pow(t - 9.807, 3);
        else if (t <= 10.734)
            y = 0.627938 - 0.0110532 * (t - 10.272) - 3.520036e-003
                    * Math.pow(t - 10.272, 2) + 0.000263321
                    * Math.pow(t - 10.272, 3);
        else if (t <= 11.199)
            y = 0.622106 - 0.0141371 * (t - 10.734) - 3.155073e-003
                    * Math.pow(t - 10.734, 2) + 0.000162254
                    * Math.pow(t - 10.734, 3);
        else if (t <= 11.664)
            y = 0.614866 - 0.016966 * (t - 11.199) - 2.928729e-003
                    * Math.pow(t - 11.199, 2) + 0.000177556
                    * Math.pow(t - 11.199, 3);
        else if (t <= 12.129)
            y = 0.606361 - 0.0195746 * (t - 11.664) - 2.681039e-003
                    * Math.pow(t - 11.664, 2) + 0.000286342
                    * Math.pow(t - 11.664, 3);
        else if (t <= 13.134)
            y = 0.596708 - 0.0218822 * (t - 12.129) - 2.281591e-003
                    * Math.pow(t - 12.129, 2) + 0.000264022
                    * Math.pow(t - 12.129, 3);
        else if (t <= 13.599)
            y = 0.57268 - 0.0256682 * (t - 13.134) - 1.485566e-003
                    * Math.pow(t - 13.134, 2) + 0.00012945
                    * Math.pow(t - 13.134, 3);
        else if (t <= 14.064)
            y = 0.560436 - 0.0269658 * (t - 13.599) - 1.304983e-003
                    * Math.pow(t - 13.599, 2) + 7.46932e-005
                    * Math.pow(t - 13.599, 3);
        else if (t <= 14.529)
            y = 0.547623 - 0.028131 * (t - 14.064) - 1.200785e-003
                    * Math.pow(t - 14.064, 2) + 9.72656e-005
                    * Math.pow(t - 14.064, 3);
        else if (t <= 14.991)
            y = 0.534292 - 0.0291846 * (t - 14.529) - 1.065100e-003
                    * Math.pow(t - 14.529, 2) + 0.000148594
                    * Math.pow(t - 14.529, 3);
        else if (t <= 15.456)
            y = 0.520596 - 0.0300736 * (t - 14.991) - 8.591482e-004
                    * Math.pow(t - 14.991, 2) + 6.92071e-006
                    * Math.pow(t - 14.991, 3);
        else if (t <= 15.921)
            y = 0.506427 - 0.0308681 * (t - 15.456) - 8.494938e-004
                    * Math.pow(t - 15.456, 2) + 1.28187e-005
                    * Math.pow(t - 15.456, 3);
        else if (t <= 17.391)
            y = 0.49189 - 0.0316498 * (t - 15.921) - 8.316116e-004
                    * Math.pow(t - 15.921, 2) + 0.000101901
                    * Math.pow(t - 15.921, 3);
        else if (t <= 17.856)
            y = 0.443892 - 0.0334342 * (t - 17.391) - 3.822275e-004
                    * Math.pow(t - 17.391, 2) + 6.02688e-005
                    * Math.pow(t - 17.391, 3);
        else if (t <= 18.321)
            y = 0.428268 - 0.0337506 * (t - 17.856) - 2.981526e-004
                    * Math.pow(t - 17.856, 2) + 6.12216e-006
                    * Math.pow(t - 17.856, 3);
        else if (t <= 18.786)
            y = 0.41251 - 0.0340239 * (t - 18.321) - 2.896122e-004
                    * Math.pow(t - 18.321, 2) + 0.000113406
                    * Math.pow(t - 18.321, 3);
        else if (t <= 19.248)
            y = 0.396638 - 0.0342197 * (t - 18.786) - 1.314102e-004
                    * Math.pow(t - 18.786, 2) + 0.00010335
                    * Math.pow(t - 18.786, 3);
        else if (t <= 19.713)
            y = 0.380811 - 0.0342749 * (t - 19.248) + 1.183285e-005
                    * Math.pow(t - 19.248, 2) + 8.32616e-005
                    * Math.pow(t - 19.248, 3);
        else if (t <= 20.178)
            y = 0.364884 - 0.0342099 * (t - 19.713) + 1.279827e-004
                    * Math.pow(t - 19.713, 2) - 7.86715e-005
                    * Math.pow(t - 19.713, 3);
        else if (t <= 20.643)
            y = 0.348996 - 0.0341419 * (t - 20.178) + 1.823597e-005
                    * Math.pow(t - 20.178, 2) - 1.30724e-005
                    * Math.pow(t - 20.178, 3);

        return (52.21398298 - 23.23780872) * y + 23.23780872;
    }

    /**
     * Computes the approximation spline for DHIV or returns the default value
     * 0.132 mM if t is not in the measured time intervall. Parameter for the
     * spline: weights all equal to one, S=1
     *
     * @param t
     * @return
     */
    public double getDHIV(double t) {
        double y = 0.132 / (1.19712426 - 0.077317889) - 0.077317889
                / (1.19712426 - 0.077317889);

        if ((-3.894 <= t) && (t <= -3.429))
            y = 0.0260752 + 0.0333816 * (t + 3.894) + 0
                    * Math.pow(t + 3.894, 2) + 0.000123214
                    * Math.pow(t + 3.894, 3);
        else if (t <= -2.964)
            y = 0.0416101 + 0.0334615 * (t + 3.429) + 1.718840e-004
                    * Math.pow(t + 3.429, 2) - 4.6372e-005
                    * Math.pow(t + 3.429, 3);
        else if (t <= -2.499)
            y = 0.0572022 + 0.0335913 * (t + 2.964) + 1.071950e-004
                    * Math.pow(t + 2.964, 2) - 0.000289114
                    * Math.pow(t + 2.964, 3);
        else if (t <= -2.037)
            y = 0.0728163 + 0.0335035 * (t + 2.499) - 2.961193e-004
                    * Math.pow(t + 2.499, 2) + 0.000359848
                    * Math.pow(t + 2.499, 3);
        else if (t <= -1.572)
            y = 0.0882672 + 0.0334603 * (t + 2.037) + 2.026304e-004
                    * Math.pow(t + 2.037, 2) - 2.17354e-006
                    * Math.pow(t + 2.037, 3);
        else if (t <= -1.107)
            y = 0.10387 + 0.0336473 * (t + 1.572) + 1.995983e-004
                    * Math.pow(t + 1.572, 2) - 0.00063401
                    * Math.pow(t + 1.572, 3);
        else if (t <= -0.642)
            y = 0.119495 + 0.0334217 * (t + 1.107) - 6.848463e-004
                    * Math.pow(t + 1.107, 2) - 0.000741887
                    * Math.pow(t + 1.107, 3);
        else if (t <= 0.363)
            y = 0.134814 + 0.0323035 * (t + 0.642) - 1.719778e-003
                    * Math.pow(t + 0.642, 2) - 0.000393869
                    * Math.pow(t + 0.642, 3);
        else if (t <= 0.828)
            y = 0.165142 + 0.0276533 * (t - 0.363) - 2.907293e-003
                    * Math.pow(t - 0.363, 2) - 0.000577236
                    * Math.pow(t - 0.363, 3);
        else if (t <= 1.293)
            y = 0.177314 + 0.0245751 * (t - 0.828) - 3.712538e-003
                    * Math.pow(t - 0.828, 2) + 0.000685274
                    * Math.pow(t - 0.828, 3);
        else if (t <= 1.758)
            y = 0.188008 + 0.021567 * (t - 1.293) - 2.756581e-003
                    * Math.pow(t - 1.293, 2) + 0.001929
                    * Math.pow(t - 1.293, 3);
        else if (t <= 2.22)
            y = 0.197634 + 0.0202546 * (t - 1.758) - 6.562414e-005
                    * Math.pow(t - 1.758, 2) + 0.00226985
                    * Math.pow(t - 1.758, 3);
        else if (t <= 2.685)
            y = 0.207202 + 0.0216475 * (t - 2.22) + 3.080384e-003
                    * Math.pow(t - 2.22, 2) + 0.00155965
                    * Math.pow(t - 2.22, 3);
        else if (t <= 3.15)
            y = 0.21809 + 0.0255239 * (t - 2.685) + 5.256091e-003
                    * Math.pow(t - 2.685, 2) + 0.000714349
                    * Math.pow(t - 2.685, 3);
        else if (t <= 4.62)
            y = 0.231167 + 0.0308755 * (t - 3.15) + 6.252608e-003
                    * Math.pow(t - 3.15, 2) - 0.000271997
                    * Math.pow(t - 3.15, 3);
        else if (t <= 5.085)
            y = 0.289202 + 0.0474948 * (t - 4.62) + 5.053102e-003
                    * Math.pow(t - 4.62, 2) - 0.00110133
                    * Math.pow(t - 4.62, 3);
        else if (t <= 5.55)
            y = 0.312269 + 0.0514798 * (t - 5.085) + 3.516751e-003
                    * Math.pow(t - 5.085, 2) - 0.00184457
                    * Math.pow(t - 5.085, 3);
        else if (t <= 6.015)
            y = 0.336782 + 0.0535539 * (t - 5.55) + 9.435794e-004
                    * Math.pow(t - 5.55, 2) - 0.00232587
                    * Math.pow(t - 5.55, 3);
        else if (t <= 6.477)
            y = 0.361654 + 0.0529227 * (t - 6.015) - 2.301005e-003
                    * Math.pow(t - 6.015, 2) - 0.00310515
                    * Math.pow(t - 6.015, 3);
        else if (t <= 6.942)
            y = 0.385307 + 0.0488082 * (t - 6.477) - 6.604749e-003
                    * Math.pow(t - 6.477, 2) - 0.00428991
                    * Math.pow(t - 6.477, 3);
        else if (t <= 7.407)
            y = 0.406144 + 0.039883 * (t - 6.942) - 1.258918e-002
                    * Math.pow(t - 6.942, 2) - 0.00315302
                    * Math.pow(t - 6.942, 3);
        else if (t <= 7.872)
            y = 0.42165 + 0.0261298 * (t - 7.407) - 1.698764e-002
                    * Math.pow(t - 7.407, 2) - 0.00115594
                    * Math.pow(t - 7.407, 3);
        else if (t <= 8.877)
            y = 0.430011 + 0.0095815 * (t - 7.872) - 1.860017e-002
                    * Math.pow(t - 7.872, 2) + 0.00231129
                    * Math.pow(t - 7.872, 3);
        else if (t <= 9.342)
            y = 0.4232 - 0.0208015 * (t - 8.877) - 1.163163e-002
                    * Math.pow(t - 8.877, 2) + 0.00400521
                    * Math.pow(t - 8.877, 3);
        else if (t <= 9.807)
            y = 0.411415 - 0.0290208 * (t - 9.342) - 6.044358e-003
                    * Math.pow(t - 9.342, 2) + 0.00244262
                    * Math.pow(t - 9.342, 3);
        else if (t <= 10.272)
            y = 0.396859 - 0.0330576 * (t - 9.807) - 2.636904e-003
                    * Math.pow(t - 9.807, 2) + 0.00186923
                    * Math.pow(t - 9.807, 3);
        else if (t <= 10.734)
            y = 0.381105 - 0.0342974 * (t - 10.272) - 2.933415e-005
                    * Math.pow(t - 10.272, 2) + 0.00120579
                    * Math.pow(t - 10.272, 3);
        else if (t <= 11.199)
            y = 0.365372 - 0.0335524 * (t - 10.734) + 1.641895e-003
                    * Math.pow(t - 10.734, 2) - 5.22663e-005
                    * Math.pow(t - 10.734, 3);
        else if (t <= 11.664)
            y = 0.35012 - 0.0320593 * (t - 11.199) + 1.568984e-003
                    * Math.pow(t - 11.199, 2) - 0.000303024
                    * Math.pow(t - 11.199, 3);
        else if (t <= 12.129)
            y = 0.335521 - 0.0307967 * (t - 11.664) + 1.146266e-003
                    * Math.pow(t - 11.664, 2) + 0.000186394
                    * Math.pow(t - 11.664, 3);
        else if (t <= 13.599)
            y = 0.321468 - 0.0296098 * (t - 12.129) + 1.406286e-003
                    * Math.pow(t - 12.129, 2) + 0.000205413
                    * Math.pow(t - 12.129, 3);
        else if (t <= 14.064)
            y = 0.281632 - 0.0241437 * (t - 13.599) + 2.312159e-003
                    * Math.pow(t - 13.599, 2) + 1.21887e-005
                    * Math.pow(t - 13.599, 3);
        else if (t <= 14.529)
            y = 0.270907 - 0.0219855 * (t - 14.064) + 2.329162e-003
                    * Math.pow(t - 14.064, 2) + 0.000331498
                    * Math.pow(t - 14.064, 3);
        else if (t <= 14.991)
            y = 0.261221 - 0.0196043 * (t - 14.529) + 2.791602e-003
                    * Math.pow(t - 14.529, 2) + 0.000948267
                    * Math.pow(t - 14.529, 3);
        else if (t <= 15.456)
            y = 0.252853 - 0.0164177 * (t - 14.991) + 4.105900e-003
                    * Math.pow(t - 14.991, 2) + 0.00039227
                    * Math.pow(t - 14.991, 3);
        else if (t <= 15.921)
            y = 0.246146 - 0.0123447 * (t - 15.456) + 4.653117e-003
                    * Math.pow(t - 15.456, 2) - 9.30071e-005
                    * Math.pow(t - 15.456, 3);
        else if (t <= 17.391)
            y = 0.241402 - 0.00807765 * (t - 15.921) + 4.523372e-003
                    * Math.pow(t - 15.921, 2) - 0.000264229
                    * Math.pow(t - 15.921, 3);
        else if (t <= 17.856)
            y = 0.238463 + 0.00350815 * (t - 17.391) + 3.358124e-003
                    * Math.pow(t - 17.391, 2) - 0.000570568
                    * Math.pow(t - 17.391, 3);
        else if (t <= 18.321)
            y = 0.240763 + 0.00626109 * (t - 17.856) + 2.562182e-003
                    * Math.pow(t - 17.856, 2) - 0.000894847
                    * Math.pow(t - 17.856, 3);
        else if (t <= 18.786)
            y = 0.244139 + 0.00806346 * (t - 18.321) + 1.313871e-003
                    * Math.pow(t - 18.321, 2) - 0.000591935
                    * Math.pow(t - 18.321, 3);
        else if (t <= 19.248)
            y = 0.248113 + 0.00890138 * (t - 18.786) + 4.881216e-004
                    * Math.pow(t - 18.786, 2) - 0.000289939
                    * Math.pow(t - 18.786, 3);
        else if (t <= 20.178)
            y = 0.252301 + 0.00916675 * (t - 19.248) + 8.626680e-005
                    * Math.pow(t - 19.248, 2) + 1.10962e-005
                    * Math.pow(t - 19.248, 3);
        else if (t <= 20.643)
            y = 0.26091 + 0.009356 * (t - 20.178) + 1.172251e-004
                    * Math.pow(t - 20.178, 2) - 8.40323e-005
                    * Math.pow(t - 20.178, 3);

        return (1.19712426 - 0.077317889) * y + 0.077317889;
    }

    /**
     * Computes the approximation spline for KIV or returns the default value
     * 13.1 mM if t is not in the measured time intervall. Parameter for the
     * spline: weights all equal to one, S=1
     *
     * @param t
     * @return
     */
    public double getKIV(double t) {
        double y = 13.1 / (16.97282365372 - 6.68584901645) - 6.68584901645
                / (16.97282365372 - 6.68584901645);
        ;

        if ((-3.894 <= t) && (t <= -3.429))
            y = 0.632703 - 0.200248 * (t + 3.894) + 0 * Math.pow(t + 3.894, 2)
                    - 0.00683388 * Math.pow(t + 3.894, 3);
        else if (t <= -2.964)
            y = 0.5389 - 0.204681 * (t + 3.429) - 9.533265e-03
                    * Math.pow(t + 3.429, 2) - 0.00451643
                    * Math.pow(t + 3.429, 3);
        else if (t <= -2.499)
            y = 0.441208 - 0.216476 * (t + 2.964) - 1.583368e-02
                    * Math.pow(t + 2.964, 2) + 0.0280129
                    * Math.pow(t + 2.964, 3);
        else if (t <= -2.037)
            y = 0.33994 - 0.21303 * (t + 2.499) + 2.324431e-02
                    * Math.pow(t + 2.499, 2) + 0.00505588
                    * Math.pow(t + 2.499, 3);
        else if (t <= -1.572)
            y = 0.24698 - 0.188315 * (t + 2.037) + 3.025176e-02
                    * Math.pow(t + 2.037, 2) + 0.03962 * Math.pow(t + 2.037, 3);
        else if (t <= -1.107)
            y = 0.169938 - 0.134481 * (t + 1.572) + 8.552163e-02
                    * Math.pow(t + 1.572, 2) - 0.0174838
                    * Math.pow(t + 1.572, 3);
        else if (t <= -0.642)
            y = 0.124138 - 0.0662868 * (t + 1.107) + 6.113172e-02
                    * Math.pow(t + 1.107, 2) - 0.0274919
                    * Math.pow(t + 1.107, 3);
        else if (t <= 0.363)
            y = 0.103769 - 0.0272676 * (t + 0.642) + 2.278052e-02
                    * Math.pow(t + 0.642, 2) + 0.0264526
                    * Math.pow(t + 0.642, 3);
        else if (t <= 0.828)
            y = 0.126225 + 0.0986746 * (t - 0.363) + 1.025350e-01
                    * Math.pow(t - 0.363, 2) - 0.0423241
                    * Math.pow(t - 0.363, 3);
        else if (t <= 1.293)
            y = 0.190024 + 0.166578 * (t - 0.828) + 4.349295e-02
                    * Math.pow(t - 0.828, 2) - 0.104603
                    * Math.pow(t - 0.828, 3);
        else if (t <= 1.758)
            y = 0.26637 + 0.139173 * (t - 1.293) - 1.024283e-01
                    * Math.pow(t - 1.293, 2) + 0.0573769
                    * Math.pow(t - 1.293, 3);
        else if (t <= 2.22)
            y = 0.314706 + 0.0811332 * (t - 1.758) - 2.238759e-02
                    * Math.pow(t - 1.758, 2) + 0.092514
                    * Math.pow(t - 1.758, 3);
        else if (t <= 2.685)
            y = 0.356534 + 0.119687 * (t - 2.22) + 1.058368e-01
                    * Math.pow(t - 2.22, 2) + 0.0101367 * Math.pow(t - 2.22, 3);
        else if (t <= 3.15)
            y = 0.436093 + 0.22469 * (t - 2.685) + 1.199775e-01
                    * Math.pow(t - 2.685, 2) - 0.107935
                    * Math.pow(t - 2.685, 3);
        else if (t <= 4.62)
            y = 0.555663 + 0.266255 * (t - 3.15) - 3.059157e-02
                    * Math.pow(t - 3.15, 2) - 0.0125683 * Math.pow(t - 3.15, 3);
        else if (t <= 5.085)
            y = 0.84103 + 0.0948394 * (t - 4.62) - 8.601761e-02
                    * Math.pow(t - 4.62, 2) - 0.012452 * Math.pow(t - 4.62, 3);
        else if (t <= 5.55)
            y = 0.865279 + 0.00676576 * (t - 5.085) - 1.033881e-01
                    * Math.pow(t - 5.085, 2) + 0.0609538
                    * Math.pow(t - 5.085, 3);
        else if (t <= 6.015)
            y = 0.852198 - 0.049846 * (t - 5.55) - 1.835756e-02
                    * Math.pow(t - 5.55, 2) + 0.00816093
                    * Math.pow(t - 5.55, 3);
        else if (t <= 6.477)
            y = 0.825871 - 0.0616247 * (t - 6.015) - 6.973056e-03
                    * Math.pow(t - 6.015, 2) - 0.0446731
                    * Math.pow(t - 6.015, 3);
        else if (t <= 6.942)
            y = 0.791507 - 0.0966734 * (t - 6.477) - 6.888994e-02
                    * Math.pow(t - 6.477, 2) + 0.0172469
                    * Math.pow(t - 6.477, 3);
        else if (t <= 7.407)
            y = 0.733392 - 0.149553 * (t - 6.942) - 4.483048e-02
                    * Math.pow(t - 6.942, 2) + 0.0734141
                    * Math.pow(t - 6.942, 3);
        else if (t <= 7.872)
            y = 0.661538 - 0.143624 * (t - 7.407) + 5.758222e-02
                    * Math.pow(t - 7.407, 2) - 0.054382
                    * Math.pow(t - 7.407, 3);
        else if (t <= 8.877)
            y = 0.601735 - 0.125349 * (t - 7.872) - 1.828061e-02
                    * Math.pow(t - 7.872, 2) + 0.0141796
                    * Math.pow(t - 7.872, 3);
        else if (t <= 9.342)
            y = 0.47169 - 0.119127 * (t - 8.877) + 2.447087e-02
                    * Math.pow(t - 8.877, 2) + 0.0322437
                    * Math.pow(t - 8.877, 3);
        else if (t <= 9.807)
            y = 0.424829 - 0.0754538 * (t - 9.342) + 6.945085e-02
                    * Math.pow(t - 9.342, 2) - 0.0325761
                    * Math.pow(t - 9.342, 3);
        else if (t <= 10.272)
            y = 0.401484 - 0.0319958 * (t - 9.807) + 2.400726e-02
                    * Math.pow(t - 9.807, 2) + 0.00495286
                    * Math.pow(t - 9.807, 3);
        else if (t <= 10.734)
            y = 0.392295 - 0.00645621 * (t - 10.272) + 3.091651e-02
                    * Math.pow(t - 10.272, 2) + 0.013472
                    * Math.pow(t - 10.272, 3);
        else if (t <= 11.199)
            y = 0.39724 + 0.0307372 * (t - 10.734) + 4.958866e-02
                    * Math.pow(t - 10.734, 2) - 0.0875034
                    * Math.pow(t - 10.734, 3);
        else if (t <= 11.664)
            y = 0.413457 + 0.0200934 * (t - 11.199) - 7.247858e-02
                    * Math.pow(t - 11.199, 2) - 0.0360727
                    * Math.pow(t - 11.199, 3);
        else if (t <= 12.129)
            y = 0.403502 - 0.0707112 * (t - 11.664) - 1.228001e-01
                    * Math.pow(t - 11.664, 2) + 0.0618963
                    * Math.pow(t - 11.664, 3);
        else if (t <= 13.134)
            y = 0.350292 - 0.144765 * (t - 12.129) - 3.645476e-02
                    * Math.pow(t - 12.129, 2) + 0.0517422
                    * Math.pow(t - 12.129, 3);
        else if (t <= 13.599)
            y = 0.220505 - 0.061256 * (t - 13.134) + 1.195480e-01
                    * Math.pow(t - 13.134, 2) - 0.0272394
                    * Math.pow(t - 13.134, 3);
        else if (t <= 14.064)
            y = 0.215132 + 0.0322542 * (t - 13.599) + 8.154909e-02
                    * Math.pow(t - 13.599, 2) - 0.0484171
                    * Math.pow(t - 13.599, 3);
        else if (t <= 14.529)
            y = 0.242895 + 0.0766879 * (t - 14.064) + 1.400728e-02
                    * Math.pow(t - 14.064, 2) - 0.0357363
                    * Math.pow(t - 14.064, 3);
        else if (t <= 14.991)
            y = 0.27799 + 0.0665334 * (t - 14.529) - 3.584491e-02
                    * Math.pow(t - 14.529, 2) + 0.0445836
                    * Math.pow(t - 14.529, 3);
        else if (t <= 15.456)
            y = 0.305474 + 0.061961 * (t - 14.991) + 2.594790e-02
                    * Math.pow(t - 14.991, 2) - 0.0774076
                    * Math.pow(t - 14.991, 3);
        else if (t <= 15.921)
            y = 0.332114 + 0.0358802 * (t - 15.456) - 8.203569e-02
                    * Math.pow(t - 15.456, 2) + 0.00889788
                    * Math.pow(t - 15.456, 3);
        else if (t <= 16.386)
            y = 0.331955 - 0.0346412 * (t - 15.921) - 6.962314e-02
                    * Math.pow(t - 15.921, 2) + 0.12207
                    * Math.pow(t - 15.921, 3);
        else if (t <= 17.391)
            y = 0.313066 - 0.020207 * (t - 16.386) + 1.006644e-01
                    * Math.pow(t - 16.386, 2) - 0.0358557
                    * Math.pow(t - 16.386, 3);
        else if (t <= 17.856)
            y = 0.358035 + 0.0734829 * (t - 17.391) - 7.440562e-03
                    * Math.pow(t - 17.391, 2) - 0.0299292
                    * Math.pow(t - 17.391, 3);
        else if (t <= 18.321)
            y = 0.387586 + 0.0471488 * (t - 17.856) - 4.919179e-02
                    * Math.pow(t - 17.856, 2) - 0.0599498
                    * Math.pow(t - 17.856, 3);
        else if (t <= 18.786)
            y = 0.392846 - 0.0374874 * (t - 18.321) - 1.328217e-01
                    * Math.pow(t - 18.321, 2) + 0.120032
                    * Math.pow(t - 18.321, 3);
        else if (t <= 19.248)
            y = 0.358764 - 0.0831502 * (t - 18.786) + 3.462234e-02
                    * Math.pow(t - 18.786, 2) + 0.053599
                    * Math.pow(t - 18.786, 3);
        else if (t <= 19.713)
            y = 0.333024 - 0.016838 * (t - 19.248) + 1.089105e-01
                    * Math.pow(t - 19.248, 2) + 0.0201839
                    * Math.pow(t - 19.248, 3);
        else if (t <= 20.178)
            y = 0.350773 + 0.0975416 * (t - 19.713) + 1.370670e-01
                    * Math.pow(t - 19.713, 2) - 0.122089
                    * Math.pow(t - 19.713, 3);
        else if (t <= 20.643)
            y = 0.413492 + 0.145818 * (t - 20.178) - 3.324745e-02
                    * Math.pow(t - 20.178, 2) + 0.0238333
                    * Math.pow(t - 20.178, 3);

        return (16.97282365372 - 6.68584901645) * y + 6.68584901645;
    }

}
