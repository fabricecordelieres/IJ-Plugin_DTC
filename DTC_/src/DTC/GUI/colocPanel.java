package DTC.GUI;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ij.Prefs;

import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

/**
 * This class handles the part of GUI dedicated to getting the proximity/co-localization parameters
 */
public class colocPanel extends JPanel implements ChangeListener{
	/** Maximum number of pixels two detections should be separated apart to be considered as at proximity **/
	int maxProximity=6;
	
	/** Maximum number of pixels two detections should be separated apart to be considered as at co-localized **/
	int maxColoc=3;
	
	
	private static final long serialVersionUID = 1L;
	private JSpinner spinnerProximity;
	private JSpinner spinnerColoc;
	
	/**
	 * Creates a new panel for proximity/co-localization parameters input
	 */
	public colocPanel() {
		getPrefs();
		
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		JLabel lblColocParam = new JLabel("Co-localization parameters");
		springLayout.putConstraint(SpringLayout.WEST, lblColocParam, 48, SpringLayout.WEST, this);
		add(lblColocParam);
		lblColocParam.setFont(new Font("Lucida Grande", Font.BOLD | Font.ITALIC, 13));
		
		JLabel lblProximity = new JLabel("Proximity: max. distance (pixels)");
		springLayout.putConstraint(SpringLayout.NORTH, lblProximity, 86, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, lblProximity, 10, SpringLayout.WEST, this);
		add(lblProximity);
		
		spinnerProximity = new JSpinner();
		springLayout.putConstraint(SpringLayout.NORTH, spinnerProximity, 81, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.SOUTH, lblColocParam, -17, SpringLayout.NORTH, spinnerProximity);
		springLayout.putConstraint(SpringLayout.WEST, spinnerProximity, 6, SpringLayout.EAST, lblProximity);
		springLayout.putConstraint(SpringLayout.EAST, spinnerProximity, -10, SpringLayout.EAST, this);
		SpinnerModel modelProximity = new SpinnerNumberModel(maxProximity, 0, 65535, 1);    
		spinnerProximity.setModel(modelProximity);
		spinnerProximity.addChangeListener(this);
		add(spinnerProximity);
		
		JLabel lblColoc = new JLabel("Coloc: max. distance (pixels)");
		springLayout.putConstraint(SpringLayout.WEST, lblColoc, 0, SpringLayout.WEST, lblProximity);
		add(lblColoc);
		
		spinnerColoc = new JSpinner();
		springLayout.putConstraint(SpringLayout.WEST, spinnerColoc, 31, SpringLayout.EAST, lblColoc);
		springLayout.putConstraint(SpringLayout.EAST, spinnerColoc, -10, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.NORTH, lblColoc, 5, SpringLayout.NORTH, spinnerColoc);
		springLayout.putConstraint(SpringLayout.NORTH, spinnerColoc, 6, SpringLayout.SOUTH, spinnerProximity);
		SpinnerModel modelColoc = new SpinnerNumberModel(maxColoc, 0, 65535, 1);    
		spinnerColoc.setModel(modelColoc);
		spinnerColoc.addChangeListener(this);
		add(spinnerColoc);
	}
	
	/**
	 * Reads the stored parameters and uses them as default values
	 */
	public void getPrefs() {
		maxProximity=(int) Prefs.get("Coloc_And_Track_proximity.double", 6);
		maxColoc=(int) Prefs.get("Coloc_And_Track_coloc.double", 3);
	}
	
	/**
	 * Stores parameters displayed within the panel
	 */
	public void storePreferences() {
		Prefs.set("Coloc_And_Track_proximity.double", (int) spinnerProximity.getValue());
		Prefs.set("Coloc_And_Track_coloc.double", (int) spinnerColoc.getValue());
	}
	
	/**
	 * Retrieves the proximity parameter
	 * @return the proximity parameter
	 */
	public int getProximity() {
		return (int) spinnerProximity.getValue();
	}
	
	/**
	 * Retrieves the co-localization parameter
	 * @return the co-localization parameter
	 */
	public int getColoc() {
		return (int) spinnerColoc.getValue();
	}
	
	/**
	 * Retrieves all the parameters: proximity and co-localization maximum distance
	 * @return all the values as an array of integers
	 */
	public int[] getValues() {
		return new int[] {(int) spinnerProximity.getValue(), (int) spinnerColoc.getValue()};
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		for (ChangeListener listener : getChangeListeners()) {
			String source="_Coloc_Tab";
			if(e.getSource().equals(spinnerProximity)) source="Proximity"+source;
			if(e.getSource().equals(spinnerColoc)) source="Coloc"+source;
			
			ChangeEvent ev=new ChangeEvent(source);
			listener.stateChanged(ev);
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
