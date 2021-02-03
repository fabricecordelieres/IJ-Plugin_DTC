package DCT.GUI;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import ij.Prefs;

import javax.swing.JLabel;

import java.awt.Font;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

/**
 * This calss is used to retrieve the parameters used for detection and tracking
 * @author fab
 *
 */
public class paramPanel extends JPanel implements ChangeListener{
	/** Channel number, used to tag the stored preferences **/
	int channel=1;
	
	/** Radius used for filtering, in pixels **/
	int radius=3;
	
	/** Noise tolerance, to be used for local maxima retrieval **/
	int tolerance=6;
	
	/** When set to true, allows refining the vesicule's localization by using its center of mass **/
	boolean doTuning=false;
	
	/** Maximum distance expected between two frames when tracking **/
	int maxJump=10;
	
	/** Filtering of tracks: any track lasting for less than this number will be skipped **/
	int minNFrames=5;
	
	
	private static final long serialVersionUID = 1L;
	private JSpinner spinnerFilterSize;
	private JSpinner spinnerDetectionTolerance;
	private JCheckBox chckbxTuneDetection;
	private JSpinner spinnerMaximumDisplacement;
	private JSpinner spinnerMinNbFrames;
	
	/**
	 * Creates a parameter panel
	 * @param channelNb the channel number: it will be used to store preferences as a label for the current channel
	 */
	public paramPanel(int channelNb) {
		getPrefs(channelNb);
		
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		JLabel lblDetection = new JLabel("Detection parameters");
		springLayout.putConstraint(SpringLayout.SOUTH, lblDetection, -186, SpringLayout.SOUTH, this);
		add(lblDetection);
		lblDetection.setFont(new Font("Lucida Grande", Font.BOLD | Font.ITALIC, 13));
		
		JLabel lblFilterSize = new JLabel("Filter size (pixels)");
		springLayout.putConstraint(SpringLayout.NORTH, lblFilterSize, 6, SpringLayout.SOUTH, lblDetection);
		springLayout.putConstraint(SpringLayout.WEST, lblFilterSize, 44, SpringLayout.WEST, this);
		add(lblFilterSize);
		
		spinnerFilterSize = new JSpinner();
		SpinnerModel modelFilterSize = new SpinnerNumberModel(radius, 0, 65535, 1);    
		spinnerFilterSize.setModel(modelFilterSize);
		springLayout.putConstraint(SpringLayout.NORTH, spinnerFilterSize, 1, SpringLayout.SOUTH, lblDetection);
		springLayout.putConstraint(SpringLayout.WEST, spinnerFilterSize, 16, SpringLayout.EAST, lblFilterSize);
		springLayout.putConstraint(SpringLayout.EAST, spinnerFilterSize, -55, SpringLayout.EAST, this);
		spinnerFilterSize.addChangeListener(this);
		add(spinnerFilterSize);
		
		JLabel lblDetectionTolerance = new JLabel("Detection tolerance");
		springLayout.putConstraint(SpringLayout.WEST, lblDetectionTolerance, 0, SpringLayout.WEST, lblFilterSize);
		add(lblDetectionTolerance);
		
		spinnerDetectionTolerance = new JSpinner();
		SpinnerModel modelDetectionTolerance = new SpinnerNumberModel(tolerance, 0, 65535, 1);    
		spinnerDetectionTolerance.setModel(modelDetectionTolerance);
		springLayout.putConstraint(SpringLayout.NORTH, spinnerDetectionTolerance, 28, SpringLayout.SOUTH, lblDetection);
		springLayout.putConstraint(SpringLayout.EAST, spinnerDetectionTolerance, -55, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.NORTH, lblDetectionTolerance, 5, SpringLayout.NORTH, spinnerDetectionTolerance);
		springLayout.putConstraint(SpringLayout.WEST, spinnerDetectionTolerance, 16, SpringLayout.EAST, lblFilterSize);
		spinnerDetectionTolerance.addChangeListener(this);
		add(spinnerDetectionTolerance);
		
		chckbxTuneDetection = new JCheckBox("Re-tune detection");
		chckbxTuneDetection.setOpaque(false);
		chckbxTuneDetection.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				stateChanged(new ChangeEvent(e.getSource()));
			}
		});
		chckbxTuneDetection.setSelected(doTuning);
		springLayout.putConstraint(SpringLayout.NORTH, chckbxTuneDetection, 6, SpringLayout.SOUTH, spinnerDetectionTolerance);
		springLayout.putConstraint(SpringLayout.WEST, chckbxTuneDetection, 67, SpringLayout.WEST, this);
		add(chckbxTuneDetection);
		
		JLabel lblTrackingParameters = new JLabel("Tracking parameters");
		springLayout.putConstraint(SpringLayout.EAST, lblTrackingParameters, 0, SpringLayout.EAST, lblDetection);
		add(lblTrackingParameters);
		lblTrackingParameters.setFont(new Font("Lucida Grande", Font.BOLD | Font.ITALIC, 13));
		
		JLabel lblMaximumDisplacementpixels = new JLabel("Maximum displacement (pixels)");
		springLayout.putConstraint(SpringLayout.SOUTH, lblTrackingParameters, -6, SpringLayout.NORTH, lblMaximumDisplacementpixels);
		springLayout.putConstraint(SpringLayout.NORTH, lblMaximumDisplacementpixels, 158, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.EAST, lblDetection, 0, SpringLayout.EAST, lblMaximumDisplacementpixels);
		springLayout.putConstraint(SpringLayout.WEST, lblMaximumDisplacementpixels, 10, SpringLayout.WEST, this);
		add(lblMaximumDisplacementpixels);
		
		spinnerMaximumDisplacement = new JSpinner();
		SpinnerModel modelMaximumDisplacement = new SpinnerNumberModel(maxJump, 0, 65535, 1);    
		spinnerMaximumDisplacement.setModel(modelMaximumDisplacement);
		springLayout.putConstraint(SpringLayout.NORTH, spinnerMaximumDisplacement, -5, SpringLayout.NORTH, lblMaximumDisplacementpixels);
		springLayout.putConstraint(SpringLayout.WEST, spinnerMaximumDisplacement, 6, SpringLayout.EAST, lblMaximumDisplacementpixels);
		springLayout.putConstraint(SpringLayout.EAST, spinnerMaximumDisplacement, -10, SpringLayout.EAST, this);
		spinnerMaximumDisplacement.addChangeListener(this);
		add(spinnerMaximumDisplacement);
		
		JLabel lblMinNbFrames = new JLabel("Minimum number of frames");
		springLayout.putConstraint(SpringLayout.WEST, lblMinNbFrames, 0, SpringLayout.WEST, lblMaximumDisplacementpixels);
		add(lblMinNbFrames);
		
		spinnerMinNbFrames = new JSpinner();
		SpinnerModel modelMinNbFrames = new SpinnerNumberModel(minNFrames, 0, 65535, 1);    
		spinnerMinNbFrames.setModel(modelMinNbFrames);
		springLayout.putConstraint(SpringLayout.WEST, spinnerMinNbFrames, 31, SpringLayout.EAST, lblMinNbFrames);
		springLayout.putConstraint(SpringLayout.EAST, spinnerMinNbFrames, -10, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.NORTH, lblMinNbFrames, 5, SpringLayout.NORTH, spinnerMinNbFrames);
		springLayout.putConstraint(SpringLayout.NORTH, spinnerMinNbFrames, 6, SpringLayout.SOUTH, lblMaximumDisplacementpixels);
		spinnerMinNbFrames.addChangeListener(this);
		add(spinnerMinNbFrames);
	}
	
	/**
	 * Reads the stored parameters for the input channel and uses them as default values when displaying the paramPanel
	 * @param channelNb the channel number: it will be used as a label for the current channel to retrieved preferences
	 */
	public void getPrefs(int channelNb) {
		channel=channelNb;
		radius=(int) Prefs.get("Coloc_And_Track_filterSizeC"+channelNb+".double", 3);
		tolerance=(int) Prefs.get("Coloc_And_Track_toleranceC"+channelNb+".double", 6);
		doTuning=Prefs.get("Coloc_And_Track_doTuningC"+channelNb+".boolean", false);
		maxJump=(int) Prefs.get("Coloc_And_Track_maxJumpC"+channelNb+".double", 10);
		minNFrames=(int) Prefs.get("Coloc_And_Track_minNFramesC"+channelNb+".double", 5);
	}
	
	/**
	 * Stores parameters for the input channel displayed within the paramPanel
	 * @param channelNb the channel number: it will be used as a label for the current channel to retrieved preferences
	 */
	public void storePreferences() {
		Prefs.set("Coloc_And_Track_filterSizeC"+channel+".double", (int) spinnerFilterSize.getValue());
		Prefs.set("Coloc_And_Track_toleranceC"+channel+".double", (int) spinnerDetectionTolerance.getValue());
		Prefs.set("Coloc_And_Track_doTuningC"+channel+".boolean", (boolean) chckbxTuneDetection.isSelected());
		Prefs.set("Coloc_And_Track_maxJumpC"+channel+".double", (int) spinnerMaximumDisplacement.getValue());
		Prefs.set("Coloc_And_Track_minNFramesC"+channel+".double", (int) spinnerMinNbFrames.getValue());
	}
	
	/**
	 * Retrieves the filter size
	 * @return the filter size
	 */
	public int getFilterSize() {
		return (int) spinnerFilterSize.getValue();
	}
	
	/**
	 * Retrieves the detection tolerance
	 * @return the detection tolerance
	 */
	public int getDetectionTolerance() {
		return (int) spinnerDetectionTolerance.getValue();
	}
	
	/**
	 * Retrieves the tune detection parameter status
	 * @return the tune detection parameter status
	 */
	public boolean getTuneDetectionState() {
		return chckbxTuneDetection.isSelected();
	}
	
	/**
	 * Retrieves the maximum distance expected between two frames when tracking
	 * @return the maximum distance expected between two frames when tracking
	 */
	public int getMaxDisplacement() {
		return (int) spinnerMaximumDisplacement.getValue();
	}
	
	/**
	 * Retrieves the minimum number of frames a vesicule should be tracked for to be taken into account
	 * @return the minimum number of frames a vesicule should be tracked for to be taken into account
	 */
	public int getMinNbFrames() {
		return (int) spinnerMinNbFrames.getValue();
	}
	
	/**
	 * Retrieves all the parameters: filter size, tolerance, detection tuning state, maximum displacement and minimum number of frames for a track
	 * @return all the values as an array of integers
	 */
	public int[] getValues() {
		return new int[] {(int) spinnerFilterSize.getValue(), (int) spinnerDetectionTolerance.getValue(), (boolean) chckbxTuneDetection.isSelected()?1:0, (int) spinnerMaximumDisplacement.getValue(), (int) spinnerMinNbFrames.getValue()};
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		for (ChangeListener listener : getChangeListeners()) {
			String source="_Channel_"+channel+"_Tab";
			if(e.getSource().equals(spinnerFilterSize)) source="Filter_Size"+source;
			if(e.getSource().equals(spinnerDetectionTolerance)) source="Detection_Tolerance"+source;
			if(e.getSource().equals(spinnerMaximumDisplacement)) source="Max_Displacement"+source;
			if(e.getSource().equals(spinnerMinNbFrames)) source="Min_Nb_Frames"+source;
			if(e.getSource().equals(chckbxTuneDetection)) source="Tune_Detection"+source;
			
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
