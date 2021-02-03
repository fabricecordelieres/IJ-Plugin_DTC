import java.awt.AWTEvent;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import DTC.GUI.GUIPanel;
import DTC.GUI.OutputGUI;
import DTC.tools.colocalizer;
import DTC.tools.detector;
import DTC.tools.tracker;
import DTC.tools.dataHandler.PointSerie;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.plugin.ChannelSplitter;
import ij.plugin.Duplicator;
import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.ImageProcessor;

/**
 * This class implements the generic GUI, to launch the plugin
 * @author fab
 *
 */
public class DTC_ implements ExtendedPlugInFilter, DialogListener, ChangeListener{
	private int flags = DOES_ALL|CONVERT_TO_FLOAT;
	
	int[][] params=null;
	
	PlugInFilterRunner pfr=null;
	GenericDialog gd=null;
	GUIPanel gpl=null;
	
	@Override
	public int setup(String arg, ImagePlus imp) {
		if(imp==null) {
			IJ.error("An input image is required (should be a 2 channels, 2D+t hyperstack)");
			return DONE;
		}
		
		if(!imp.isComposite() || imp.getNChannels()!=2 || imp.getNSlices()!=1) {
			IJ.error("The input image should be a 2 channels, 2D+t hyperstack");
			return DONE;
		}
		
		imp.setC(1);
		IJ.run(imp, "Red", "");
		imp.setC(2);
		IJ.run(imp, "Green", "");
		
		return flags;
	}
	
	@Override
	public void run(ImageProcessor ip) {
		if(gd.isVisible()) {
			preview();
		}
		//Nothing more to add: otherwise will loop for all the individual slices
	}

	@Override
	public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
		this.pfr=pfr;
		gd=new GenericDialog("Detect, Track, Colocalize - fabrice.cordelieres@gmail.com - v1.0.0 21-02-01");
		gd.setResizable(false);
		gpl= new GUIPanel();
		gd.add(gpl);
		
		gd.addMessage("");
		gd.addPreviewCheckbox(pfr, "Preview");
		
		
		gpl.addChangeListener(this);
		gd.addDialogListener(this);
		
		gd.showDialog();
		
		if(gd.wasOKed()) {
			params=gpl.getValues();
			gpl.storePreferences();
			process();
		}
		
		return IJ.setupDialog(imp, flags);
	}

	@Override
	public void setNPasses(int nPasses) {
		// TODO Auto-generated method stub	
	}

	/**
	 * This function allows previewing the effects of the current parameters on detection and co-localization
	 */
	public void preview() {
		params=gpl.getValues();
		
		//Preview
		ImagePlus imp=WindowManager.getCurrentImage();
		
		PointSerie[] prevDetections=new PointSerie[2];
		
		imp.setOverlay(null);
		
		//Get only the current timepoint
		Roi roi=imp.getRoi();
		imp.killRoi();
		
		ImagePlus previewIp=new Duplicator().run(imp, 1, 2, 1, 1, imp.getFrame(), imp.getFrame());
				
		//Preview detections
		for(int i=0; i<2; i++) {
			previewIp.setC(i+1);
			detector.setParameters(params[i][0], params[i][1], params[i][2]==1, 0, detector.COLORS[i], true);
			previewIp.setRoi(roi);
			prevDetections[i]=detector.previewDetections(previewIp, roi);
		}
		
		//Transfer overlay from the preview to the actual image
		Overlay ov=previewIp.getOverlay();
		imp.setOverlay(ov);
		imp.setRoi(roi);
		
		//Preview colocalization
		colocalizer.setParameters(params[2][0], params[2][1]);
		colocalizer.previewColoc(prevDetections[0], prevDetections[1], new double[]{params[0][0], params[1][0]});
	}
	
	/**
	 * This function actually performs the full processing on the input hyperstack
	 */
	public void process() {
		Roi roi=WindowManager.getCurrentImage().getRoi();
		
		ImagePlus[] imp=ChannelSplitter.split(WindowManager.getCurrentImage());
		PointSerie[][] detections=new PointSerie[imp.length][];
		ArrayList<ArrayList<PointSerie>> tracks=new ArrayList<ArrayList<PointSerie>>();
		
		//Detection
		for(int i=0; i<imp.length; i++) {
			detector.setParameters(params[i][0], params[i][1], params[i][2]==1, i+1, detector.COLORS[i], true);
			detections[i]=detector.detect(imp[i], roi, params[i][0]);
		}
		
		//Coloc
		colocalizer.setParameters(params[2][0], params[2][1]);
		colocalizer.tag(detections);

		//Tracking
		for(int i=0; i<detections.length; i++) {
			tracker.setParameters(params[i][3], params[i][4], i+1, detector.COLORS[i]);
			tracks.add(tracker.doNearestNeighbor(detections[i]));
		}
		
		
		imp=null;

		OutputGUI og=new OutputGUI(params, detections, tracks);
		og.setVisible(true);
	}
	
	@Override
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
		return true;
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		if(gd.getPreviewCheckbox().getState()) preview();
	}
		
	public String getRecorderArgument() {
		String out="";
		String[] categories=new String[] {"filter", "tolerance", "tune", "max_displacement", "min_nFrames"};
		
		for(int i=0; i<2; i++) {
			for(int j=0; j<categories.length; j++) out+=categories[j]+"_C"+(i+1)+"="+params[i][j]+" ";
		}
		
		out+="proximity="+params[2][0]+" coloc="+params[2][1];
		
		return out;
	}
}
