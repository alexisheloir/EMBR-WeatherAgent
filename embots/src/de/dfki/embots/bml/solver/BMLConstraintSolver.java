/*
 * BMLConstraintSolver.java
 *
 * (c) 2009 Michael Kipp, DFKI, Germany, kipp@dfki.de
 * Created on 03.09.2009, 01:44:55
 */
package de.dfki.embots.bml.solver;

import JaCoP.constraints.Constraint;
import JaCoP.constraints.Distance;
import JaCoP.constraints.PrimitiveConstraint;
import JaCoP.constraints.Sum;
import JaCoP.constraints.XeqC;
import JaCoP.constraints.XplusCeqZ;
import JaCoP.constraints.XplusYeqZ;
import JaCoP.core.IntVar;
import JaCoP.core.Store;
import JaCoP.core.Var;
import JaCoP.search.DepthFirstSearch;
import JaCoP.search.IndomainMin;
import JaCoP.search.InputOrderSelect;
import JaCoP.search.Search;
import JaCoP.search.SelectChoicePoint;
import de.dfki.embots.bml.BMLBlock;
import de.dfki.embots.bml.BMLElement;
import de.dfki.embots.bml.behavior.BMLBehavior;
import de.dfki.embots.bml.behavior.BMLNonverbalBehavior;
import de.dfki.embots.bml.exception.BMLBehaviorException;
import de.dfki.embots.bml.lex.BehaviorLexeme;
import de.dfki.embots.bml.sync.BMLRelativeSyncPoint;
import de.dfki.embots.bml.sync.BMLSyncLabel;
import de.dfki.embots.bml.sync.BMLSyncPoint;
import de.dfki.embots.bml.sync.BMLVirtualSyncPoint;
import de.dfki.embots.embrscript.EMBRPose;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

/**
 * Current version is based on the Java Constraint Solver (JaCoP 3 RC)
 * NB: did not work properly in 2.4!
 *
 * http://jacop.osolpro.com
 *
 * TODO:
 * - umstieg auf zufügen nur notwendiger sync points
 * - klären ob strecken/komp. mit constraints gelöst werden soll oder separat
 *
 * CHANGES:
 * - allowed negative times as output!
 *
 * @author Michael Kipp
 */
public class BMLConstraintSolver
{

    private static final boolean DEBUG = false;
    private static final int MIN_TIME_MSEC = -60000; // 1 min negative time
    private static final int MAX_TIME_MSEC = 600000; // 10 mins of total time
    private static final int MIN_TIME_EPS_MSEC = 0;
    private static final int MAX_TIME_EPS_MSEC = 30000; // 30 sec of "stretch room"
    private BMLBlock _bmlBlock;
    private Store _store;
    private HashMap<BMLSyncPoint, IntVar> _syncPointToVariable = new HashMap<BMLSyncPoint, IntVar>();
    private List<Var> _additionalVariables = new ArrayList<Var>();
    private List<BehaviorData> _behaviorDataList = new ArrayList<BehaviorData>();
    private ArrayList<IntVar> _epsVars = new ArrayList<IntVar>();

    public BMLConstraintSolver(BMLBlock block)
    {
        _bmlBlock = block;
        _store = new Store();
        //Switches.trace = true;
//        System.out.println("####################### CONSTRAINT SOLVER INIT");
//        System.out.println("store = " + _store.getDescription() + " " + _store.toString());
//        System.out.println("store constraints: " + _store.numberConstraints());
//        System.out.println("store id: " + _store.getName());
//        System.out.println("con trace = " + Store.constraintsToTrace.size());
//        System.out.println("var trace = " + Store.variablesToTrace.size());
//        _store.print();

    }

    /**
     * Uses linear programming (LP) to resolve the times of the relative
     * sync points. Uses JaCoP constraint solver.
     *
     * Note that times are in seconds for BML, but in milliseconds (integers)
     * for JaCoP constraints.
     *
     * Note that this is considered the last operation on this object.
     * All data is deleted afterwards
     */
    public boolean solveTiming()
    {
        // make sure every behavior without sync points has a start tag
        preprocessBMLBlock();

        // all sync points explicitly specified in the BML block
        BMLSyncPoint[] allSyncPoints = _bmlBlock.collectSyncPoints();

        if (DEBUG) {
            for (BMLSyncPoint s : allSyncPoints) {
                System.out.println("(sync point) " + s.toScript());
            }
        }

        // only continue if there is at least one sync point
        if (allSyncPoints.length > 0) {

            // create one Var per sync point
            for (int i = 0; i < allSyncPoints.length; i++) {
                createSyncPointVariable(allSyncPoints[i]);
            }

            // create sync points for all sync points in the lexeme
            createVirtualLexemeSyncPoints();

            // create intermediate data structure for all NV behaviors
            createBehaviorData();

            // create constraints for distances between sync points
            createIntraBehaviorConstraints();

            // create inter-behavior constraints
            createConstraints(allSyncPoints);

            // checking consistency
            // NB: when commenting this out, does not work any more!
            boolean consistent = _store.consistency();
            if (DEBUG) {
                System.out.println("JACOP Store consistency=" + consistent);
            }

            Search label = new DepthFirstSearch<IntVar>();

            // some settings
            label.setOptimize(true);
//            label.setSolutionListener(new PrintOutListener<IntVar>());
//            label.getSolutionListener().searchAll(true);
//            label.getSolutionListener().recordSolutions(false);
//            label.setConsistencyListener(new ConsistencyListener()
//            {
//
//                int count = 0;
//
//                @Override
//                public boolean executeAfterConsistency(boolean consistent)
//                {
//                    System.out.print("." + count++);
//                    if (count % 500 == 0) {
//                        System.out.println("");
//                    }
//                    return consistent;
//                }
//
//                @Override
//                public void setChildrenListeners(ConsistencyListener[] children)
//                {
//                }
//
//                @Override
//                public void setChildrenListeners(ConsistencyListener child)
//                {
//                }
//            });

            label.setPrintInfo(DEBUG);

            // collect all relevant variables
            List<Var> allVar = new ArrayList<Var>(_additionalVariables);
            allVar.addAll(_syncPointToVariable.values());

            IntVar[] varArr = new IntVar[allVar.size()];
            varArr = (IntVar[]) allVar.toArray(varArr);
            SelectChoicePoint<IntVar> select =
                    new InputOrderSelect<IntVar>(_store, varArr, new IndomainMin<IntVar>());
            //SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(varArr, null, new IndomainMin<IntVar>());

            if (DEBUG) {
                System.out.println("Num. of store constraints = " + _store.numberConstraints());
            }

            // define cost function as sum of epsilons
            IntVar epsSum = new IntVar(_store, "eps_sum", 0, MAX_TIME_MSEC);
            Constraint sumConstraint = new Sum(_epsVars, epsSum);
            _store.impose(sumConstraint);

            // DEBUG
//            System.out.println("####################### BEFORE SOLVING");
//            System.out.println("store = " + _store.getDescription() + " " + _store.toString());
//            System.out.println("store: " + _store.numberConstraints());
//            System.out.println("store: " + _store.getName());
//            _store.print();


            // only compute if there are constraints
            if (_store.numberConstraints() > 0) {

                boolean isSolved = label.labeling(_store, select, epsSum);

                // write back results
                for (BMLSyncPoint sy : _syncPointToVariable.keySet()) {
                    if (sy instanceof BMLRelativeSyncPoint || sy instanceof BMLVirtualSyncPoint) {
                        sy.setTime((double) (_syncPointToVariable.get(sy).value() / 1000d));
                    }
                }

                if (DEBUG) {
                    printSolution();
                }

                _bmlBlock = null;
                _store = null;

                return isSolved;
            } else {
                // otherwise we're done!

                _bmlBlock = null;
                _store = null;

                return true;
            }
        }

        // case: there was nothing to resolve
        return true;
    }

    private void createConstraints(BMLSyncPoint[] allSyncPoints)
    {
        for (int i = 0; i < allSyncPoints.length; i++) {

            // relative sync point
            if (allSyncPoints[i] instanceof BMLRelativeSyncPoint) {
                BMLRelativeSyncPoint rel = (BMLRelativeSyncPoint) allSyncPoints[i];
                if (rel.getRefConstraint() == null) {
                    // problem: reference behavior does not exist
                    _bmlBlock.addException(new BMLBehaviorException(rel.getBehavior().getID(),
                            "Could not resolve reference in relative sync point \""
                            + rel.getSyncLabel().toString() + "\""));
                } else {
                    PrimitiveConstraint pc = makeConstraint(rel);
                    _store.impose(pc);
                }
            } else if (!(allSyncPoints[i] instanceof BMLVirtualSyncPoint)) {
                IntVar v = _syncPointToVariable.get(allSyncPoints[i]);
                int time = (int) (1000 * allSyncPoints[i].getTime());

                // absolute sync point
                PrimitiveConstraint pc = new XeqC(v, time);
                if (DEBUG) {
                    System.out.println("# Abs. Constraint: " + v.id() + " = " + time);
                }
                _store.impose(pc);
            }
        }
    }

    /**
     * Prints only interesting results.
     */
    private void printSolution()
    {
        // print results
        for (Var v : _store.vars) {
            if (v != null) {
                if (v.id.indexOf("eps") > -1) {
                    System.out.println("[solution] " + v);
                }
            }
        }
    }

    private class SyncPointData
    {

        BMLSyncPoint syncPoint;
        long timePoint;
        long distanceToPrevious = 0;

        public SyncPointData(BMLSyncPoint sp, long time)
        {
            syncPoint = sp;
            timePoint = time;
        }
    }

    /**
     * Stores behaviors in terms of sync points and their in-between distances.
     */
    private class BehaviorData
    {

        BMLNonverbalBehavior behavior;
        EnumMap<BMLSyncLabel, SyncPointData> syncPointTable = new EnumMap(BMLSyncLabel.class);

        public BehaviorData(BMLNonverbalBehavior beh)
        {
            behavior = beh;
        }

        public void print()
        {
            for (int i = 0; i < BMLSyncLabel.getAllLabels().length; i++) {
                BMLSyncLabel label = BMLSyncLabel.getAllLabels()[i];
                SyncPointData dat = syncPointTable.get(label);
                if (dat != null) {
                    System.out.println("BehaviorData [" + behavior.getBMLTag() + " " + behavior.getID() + "] "
                            + label + ": " + dat.timePoint + " d=" + dat.distanceToPrevious);
                }
            }
        }
    }

    /**
     * Creates constraint variable for given sync point.
     */
    private Var createSyncPointVariable(BMLSyncPoint sync)
    {
        String varName = sync.getBehavior().getID() + "_" + sync.getID();
        if (DEBUG) {
            System.out.println("# Variable:   " + varName);
        }
        IntVar v = new IntVar(_store, varName,
                MIN_TIME_MSEC, MAX_TIME_MSEC);
        _syncPointToVariable.put(sync, v);
        return v;
    }

    /**
     * Adds start sync point.
     */
    private void preprocessBMLBlock()
    {
        if (DEBUG) {
            System.out.println("*** PREPROCESSING");
        }
        // step through all behaviors
        for (BMLElement el : _bmlBlock.getBehaviors()) {
            if (el instanceof BMLNonverbalBehavior) {

                BMLNonverbalBehavior nvb = (BMLNonverbalBehavior) el;

                // case: no sync points => add start point
                if (nvb.getSyncPoints().isEmpty()) {
                    BMLSyncPoint sp = new BMLSyncPoint((BMLBehavior) el,
                            BMLSyncLabel.START.toString(), 0);
                    ((BMLNonverbalBehavior) el).addSyncPoint(sp);
                }

                // check if behavior is a hold lexeme
                BehaviorLexeme lex = nvb.getLexeme();

                if (lex != null && lex.isHoldBehavior()) {
                    if (DEBUG) {
                        System.out.println("-> " + nvb + " is a hold!!!");
                    }
                    // map stroke_start to ready
                    //BMLSyncPoint ready = nvb.getSyncPoint(BMLSyncLabel.READY);
                    //BMLSyncPoint strokeStart = nvb.getSyncPoint(BMLSyncLabel.STROKE_START);

                    // TODO: check if old is overwritten => exception !!!
                    nvb.changeSyncLabel(BMLSyncLabel.STROKE_START, BMLSyncLabel.READY);

                    // map stroke_end+relax to stroke_start
                    nvb.changeSyncLabel(BMLSyncLabel.STROKE_END, BMLSyncLabel.STROKE_START);
                    nvb.changeSyncLabel(BMLSyncLabel.RELAX, BMLSyncLabel.STROKE_START);
                }
            }
        }
    }

    /**
     * Create sync points for those sync points specified in the corresponding
     * lexeme (EMBRScript template)
     */
    private void createVirtualLexemeSyncPoints()
    {
        for (BMLBehavior b : _bmlBlock.getBehaviors()) {
            if (b instanceof BMLNonverbalBehavior && b.getLexeme() != null) {
                for (EMBRPose p : b.getLexeme().getEMBRScript().getPoses()) {

                    // ignore label UNDEFINED
                    if (p.getSemantics() != null && (!p.getSemantics().equals(BMLSyncLabel.UNDEFINED))
                            && b.getSyncPoint(p.getSemantics().toString()) == null) {
                        BMLVirtualSyncPoint sp = new BMLVirtualSyncPoint(b, p.getSemantics().toString());
                        b.addSyncPoint(sp);
                        createSyncPointVariable(sp);
                        if (p.getSemantics().holdFollows() && b.getSyncPoint(p.getSemantics().subsequent().toString()) == null) {
                            BMLSyncPoint sy = new BMLVirtualSyncPoint(b, p.getSemantics().subsequent().toString());
                            b.addSyncPoint(sy);
                            createSyncPointVariable(sy);
                        }

                    }
                }
            }
        }
    }

    /**
     * Create behavior data structure that simplifies further processing
     */
    private void createBehaviorData()
    {
        for (BMLBehavior b : _bmlBlock.getBehaviors()) {

            // single behavior
            if (b instanceof BMLNonverbalBehavior && b.getLexeme() != null) {

                BehaviorData behData = new BehaviorData((BMLNonverbalBehavior) b);
                long prevTime = 0;

                // step through poses
                for (EMBRPose p : b.getLexeme().getEMBRScript().getPoses()) {

                    // ignore UNDEFINED
                    if (p.getSemantics() != null && (!p.getSemantics().equals(BMLSyncLabel.UNDEFINED))) {
                        BMLSyncPoint sy = b.getSyncPoint(p.getSemantics().toString());
                        SyncPointData spData = new SyncPointData(sy, p.getTime());
                        behData.syncPointTable.put(sy.getSyncLabel(), spData);
                        spData.distanceToPrevious = p.getTime() - prevTime;
                        prevTime = p.getTime();

                        // create point for follow-on hold
                        if (p.getSemantics().holdFollows()) {
                            BMLSyncPoint s2 = b.getSyncPoint(p.getSemantics().
                                    subsequent().toString());

                            if (s2 != null) {
                                SyncPointData sp2 = new SyncPointData(s2,
                                        p.getTime() + p.getHoldDuration());
                                sp2.distanceToPrevious = p.getHoldDuration();
                                behData.syncPointTable.put(s2.getSyncLabel(), sp2);
                                prevTime += p.getHoldDuration();
                            }
                        }
                    }
                }
                _behaviorDataList.add(behData);
                if (DEBUG) {
                    behData.print();
                }
            }
        }
    }

    /**
     * Creates behavior-internal constraints between every pair of neighboring
     * sync points, using the durations of the lexeme.
     */
    private void createIntraBehaviorConstraints()
    {
        for (BehaviorData dat : _behaviorDataList) {
            IntVar prevVar = null;
            IntVar currVar = null;
            for (SyncPointData spDat : dat.syncPointTable.values()) {
                currVar = _syncPointToVariable.get(spDat.syncPoint);
                if (prevVar != null) {
                    createIntraBehaviorConstraints(dat.behavior,
                            spDat.syncPoint.getSyncLabel().toString(),
                            prevVar, currVar, (int) spDat.distanceToPrevious);
                }
                prevVar = currVar;
            }
        }
    }

    /**
     * Creates constraint between neighboring sync points.
     *
     * @param b BML behavior
     * @param postfix How to name the resulting variables
     * @param v1 first sync point
     * @param v2 second sync point
     * @param poseDelta distance between points in original template
     */
    private void createIntraBehaviorConstraints(BMLBehavior b, String postfix, IntVar v1, IntVar v2, int poseDelta)
    {
        // delta models the final distance between the variables
        IntVar delta = new IntVar(_store, b.getID() + "_realdelta_" + postfix, 0, poseDelta + MAX_TIME_MSEC);

        // whereas eps models the distance to the original distance in the lexeme
        IntVar eps = new IntVar(_store, b.getID() + "_eps_" + postfix, MIN_TIME_EPS_MSEC, MAX_TIME_EPS_MSEC);

        // introduce variable for pose time
        IntVar poseDeltaVar = new IntVar(_store, b.getID() + "_orgdelta_" + postfix, 0, poseDelta + MAX_TIME_MSEC);

        // set this absolutely
        PrimitiveConstraint c0 = new XeqC(poseDeltaVar, poseDelta);

        // relationships between delta and eps
        PrimitiveConstraint c1 = new XplusYeqZ(v1, delta, v2);

        Constraint c2 = new Distance(poseDeltaVar, delta, eps);

        _store.impose(c0);
        _store.impose(c1);
        _store.impose(c2);

        // order matters: tries to minimize eps. *first*

        _additionalVariables.add(delta);
        _additionalVariables.add(poseDeltaVar);

        _additionalVariables.add(eps);
        _epsVars.add(eps);

        // debug
        if (DEBUG) {
            System.out.println("# Intra Constraint: " + poseDeltaVar.id() + " = " + poseDelta);
            System.out.println("# Intra Constraint: " + v1.id() + " + " + delta.id() + " = " + v2.id());
            System.out.println("# Intra Constraint: dist(" + poseDeltaVar.id() + ", " + delta.id() + ") = " + eps.id());
            System.out.println("# Add var:    " + poseDeltaVar.id());
            System.out.println("# Add var:    " + delta.id());
            System.out.println("# Add var:    " + eps.id());
        }
    }

    /**
     * Translates BML relative sync point to a JaCoP constraint.
     *
     * @param sync BML relative sync point
     */
    private PrimitiveConstraint makeConstraint(BMLRelativeSyncPoint sync)
    {
        IntVar x = null, z = null;
        int c = 0;
        System.out.println("makeConstraint for " + sync.toXML() + ": " + sync.getRefConstraint());
        if (sync.getRelation().equals(BMLRelativeSyncPoint.Relation.EQUAL)
                || sync.getRelation().equals(BMLRelativeSyncPoint.Relation.BEFORE)) {
            x = _syncPointToVariable.get(sync);
            z = _syncPointToVariable.get(sync.getRefConstraint());
            c = -(int) (1000 * sync.getOffset());
        } else if (sync.getRelation().equals(BMLRelativeSyncPoint.Relation.AFTER)) {
            x = _syncPointToVariable.get(sync.getRefConstraint());
            z = _syncPointToVariable.get(sync);
            c = (int) (1000 * sync.getOffset());
        }
        if (DEBUG) {
            System.out.println("# Rel. Constraint: " + x.id() + " + " + c + " = " + z.id());
        }
        return new XplusCeqZ(x, c, z);
    }
}
