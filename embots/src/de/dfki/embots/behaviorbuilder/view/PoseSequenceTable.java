package de.dfki.embots.behaviorbuilder.view;

import de.dfki.embots.behaviorbuilder.model.PoseModel;
import de.dfki.embots.behaviorbuilder.model.SimpleSyncTag;
import de.dfki.embots.behaviorbuilder.*;
import de.dfki.embots.behaviorbuilder.model.PoseSequenceModel;
import de.dfki.embots.embrscript.*;
import java.awt.EventQueue;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 * A suitable adaption of JAVA's {@link DefaultTableModel} for this
 * application's pose sequence table.
 * 
 * @author Oliver Schoenleben
 */
@SuppressWarnings("serial")
public class PoseSequenceTable extends JTable
{

    public static String[] COLUMN_HEADERS;
    public static Class<?>[] COLUMN_CLASSES;
    private static final int POSE_COL = 0, ACTIVE_COL = 1, PHASE_COL = 2,
            START_FRAME_COL = 3, START_TIME_COL = 4,
            HOLD_COL = 5, WARP_COL = 6,
            WARP_VAL = 7, COMMENT_COL = 8;
    private BehaviorBuilder _owner;
    private ListSelectionModel _lsm = getSelectionModel();

    /**
     * A very own TableModel for the Poser GUI, determining the columns'
     * class types, tooltips, and the column names (for the header captions).
     */
    protected class PoseSequenceTableModel extends DefaultTableModel
    {

        public PoseSequenceTableModel(Object[][] data, Object[] columnNames)
        {
            super(data, columnNames);
        }

        /**
         * Return information about the table's columns' data types.
         * These are defined in the outer class' configuration properties.
         *
         * @param columnIndex the index number of the column of interest
         * @return the type of the specified column, as configured
         */
        @Override
        public Class<?> getColumnClass(int columnIndex)
        {
            return COLUMN_CLASSES[columnIndex];
        }

        // TODO ~ remove?
        // the below dunn werk, and also can setValueAt() be overridden to
        // avoid rebuilding of the table (save re-ordering)

        /*
        public JTableHeader createDefaultTableHeader()
        {
        return new JTableHeader(columnModel)
        {

        @Override
        public String getToolTipText(MouseEvent ev)
        {
        String tip = null;
        java.awt.Point p = ev.getPoint();
        int index = columnModel.getColumnIndexAtX(p.x);
        int ix = columnModel.getColumn(index).getModelIndex();
        return SEQTBL_TOOLTIPS[ix];
        }
        };
        }
         *
         */
        @Override
        public boolean isCellEditable(int row, int col)
        {
            if (col == POSE_COL || col == START_FRAME_COL) {
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * Generate poses data for the pose sequence table, cell-wise.
     *
     * @return the 2-dimensional data array to be fed into the pose sequence table
     */
    private Object[][] getPosesArray(PoseSequenceModel seq)
    {
        Object[][] r = new Object[seq.size()][COLUMN_HEADERS.length];
        PoseModel[] poses = seq.getPoses().toArray(new PoseModel[0]);
        Arrays.sort(poses);
        int i = 0;
        for (PoseModel p : poses) {
            r[i][POSE_COL] = i;
            r[i][ACTIVE_COL] = p.isActive();
            r[i][PHASE_COL] = p.getSimplePhaseType();
            r[i][START_FRAME_COL] = (long) (p.getTime() / 40);
            r[i][START_TIME_COL] = p.getTime();
            r[i][HOLD_COL] = p.getHoldDuration();
            r[i][WARP_COL] = p.getTimeWarp().key;
            r[i][WARP_VAL] = p.getTimeWarp().sigma;
            r[i][COMMENT_COL] = p.getComment();
            p.setId(i++);        //: implicitly restructs
        }
        return r;
    }

    /**
     * Constructs the sequence table. Initializes the types, captions etc.
     *
     * @param owner the application this table belongs to.
     */
    public PoseSequenceTable(BehaviorBuilder owner)
    { // throws ClassNotFoundException
        _owner = owner;

        COLUMN_CLASSES = new Class<?>[]{
                    Integer.class, Boolean.class, SimpleSyncTag.class, Long.class,
                    Long.class, Long.class,
                    EMBRTimeWarpKey.class, Double.class, String.class
                };

        COLUMN_HEADERS = new String[]{
                    "pose", "active", "phase", "frame", "time",
                    "hold", "warp", "warp val", "comment"};
    }

    /**
     * Re-generate the table when its contents were edited.
     *
     * TODO ~ there is probably a better way than to reconstruct everything.
     */
    public void updateTable()
    {
        System.out.println("updateTable... " + EventQueue.isDispatchThread());
        _lsm.removeListSelectionListener(this);
        if (getModel() != null) {
            getModel().removeTableModelListener(this);
        }
        setModel(new PoseSequenceTableModel(getPosesArray(_owner.getCurrentSequence()),
                COLUMN_HEADERS));
        getModel().addTableModelListener(this);

        final JComboBox cPhaseType = new JComboBox(SimpleSyncTag.values());
        final JComboBox cTimeWarp = new JComboBox(EMBRTimeWarpKey.values());
        getColumnModel().getColumn(PHASE_COL).setCellEditor(new DefaultCellEditor(cPhaseType));
        getColumnModel().getColumn(WARP_COL).setCellEditor(new DefaultCellEditor(cTimeWarp));

        if (_owner.getCurrentPose().getId() > -1) {
            System.out.println("row= " + _owner.getCurrentPose().getId());
            System.out.println("total=" + _owner.getCurrentSequence().size());
            setRowSelectionInterval(_owner.getCurrentPose().getId(), _owner.getCurrentPose().getId());

            // TODO ~ The following 3 away?
//        _owner.updatePose();
//        _owner.updateScript();

            _owner._poseControlPane.updateControlPanel(_owner.getCurrentPose());
        }
        _lsm.addListSelectionListener(this);
    }

    @Override
    public void valueChanged(ListSelectionEvent ev)
    {
        System.out.println("value changed...." + EventQueue.isDispatchThread());
        super.valueChanged(ev);

        if (getRowCount() < 1) {
            return;
        }

        int row = _lsm.getLeadSelectionIndex();
        if (row < 0) {
            return;
        }

        try {
            int poseId = (Integer) (getModel().getValueAt(row, POSE_COL));
            if (0 > poseId) {
                return;
            }
            PoseModel newPose = _owner.getCurrentSequence().getPoseById(poseId);
            _owner.selectPose(newPose);
        } catch (ArrayIndexOutOfBoundsException x) {
            /*
             * This happens when a pose (=row) was deleted, since the
             * selection index is still on that row.
             * It is okay to ignore this exception.
             */
            System.err.println("Info: Value-changed pose was deleted");
        }
    }

    @Override
    public void tableChanged(TableModelEvent ev)
    {
        System.out.println("table changed..." + EventQueue.isDispatchThread());
        super.tableChanged(ev);

        TableModel model = getModel();

        int row = ev.getFirstRow();
        if (0 > row) {
            return;
        }

        // retrieve selected index
        int id = (Integer) (model.getValueAt(row, POSE_COL));

        // get pose model by index
        PoseModel p = _owner.getCurrentSequence().getPoseById(id);
        p.setInvoked((Boolean) (model.getValueAt(row, ACTIVE_COL)));
        p.setSimplePhaseType((SimpleSyncTag) model.getValueAt(row, PHASE_COL));

        // change 
//        long frame = (Long)model.getValueAt(row, START_FRAME_COL) * 40;
        long time = (Long) model.getValueAt(row, START_TIME_COL);
        p.setTime(time);

        //p.setTime(((Long) (model.getValueAt(row, START_COL))) * 40);




        p.setHoldDuration((Long) (model.getValueAt(row, HOLD_COL)));
        p.setTimeWarp(new EMBRTimeWarpConstraint((EMBRTimeWarpKey) model.getValueAt(row, WARP_COL), (Double) model.getValueAt(row, WARP_VAL)));
        //p.setTimeWarp((EMBRTimeWarpConstraint) model.getValueAt(row, 5));
        p.setComment((String) (model.getValueAt(row, COMMENT_COL)));
//        _owner.updateScript();
        _owner.scheduleReindexing();
    }
}
