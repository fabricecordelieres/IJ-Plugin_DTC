package DTC.GUI;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.Color;
import java.awt.Dimension;

public class GUIPanel extends JPanel implements ChangeListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** Channel 1 parameters panel **/
	private paramPanel paramPanel_C1;
	
	/** Channel 2 parameters panel **/
	private paramPanel paramPanel_C2;
	
	/** Proximity/co-localization parameters panel **/
	private colocPanel colocPanel;

	/**
	 * Creates the panel
	 */
	public GUIPanel() {
		setPreferredSize(new Dimension(340, 266));
		setMinimumSize(new Dimension(340, 266));
		setMaximumSize(new Dimension(340, 266));
		setSize(new Dimension(340, 266));
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		springLayout.putConstraint(SpringLayout.NORTH, tabbedPane, 0, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, tabbedPane, 0, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, tabbedPane, 0, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, tabbedPane, 0, SpringLayout.EAST, this);
		add(tabbedPane);
		
		paramPanel_C1 = new paramPanel(1);
		paramPanel_C1.addChangeListener(this);
		paramPanel_C1.setBackground(new Color(255, 196, 196));
		tabbedPane.addTab("Channel 1", null, paramPanel_C1, null);
		tabbedPane.setBackgroundAt(0, Color.RED);
		
		paramPanel_C2 = new paramPanel(2);
		paramPanel_C2.addChangeListener(this);
		paramPanel_C2.setBackground(new Color(196, 255, 196));
		tabbedPane.addTab("Channel 2", null, paramPanel_C2, null);
		tabbedPane.setBackgroundAt(1, Color.GREEN);
		
		colocPanel = new colocPanel();
		colocPanel.addChangeListener(this);
		colocPanel.setBackground(new Color(255, 255, 196));
		tabbedPane.addTab("Track/Co-localize", null, colocPanel, null);
		tabbedPane.setBackgroundAt(2, Color.YELLOW);
	}
	
	public void storePreferences() {
		paramPanel_C1.storePreferences();
		paramPanel_C2.storePreferences();
		colocPanel.storePreferences();
	}
	
	public int[][] getValues() {
		return new int[][] {paramPanel_C1.getValues(), paramPanel_C2.getValues(), colocPanel.getValues()};
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		for (ChangeListener listener : getChangeListeners()) {
	        listener.stateChanged(e);
	    }
	}
	
	public void addChangeListener(ChangeListener listener) {
		listenerList.add(ChangeListener.class, listener);
	}
	
	public void removeChangeListener(ChangeListener listener) {
	    listenerList.remove(ChangeListener.class, listener);
	}

	public ChangeListener[] getChangeListeners() {
	    return listenerList.getListeners(ChangeListener.class);
	}

	protected void fireChangeListeners() {
	    ChangeEvent event = new ChangeEvent(this);
	    for (ChangeListener listener : getChangeListeners()) {
	        listener.stateChanged(event);
	    }
	}
}
