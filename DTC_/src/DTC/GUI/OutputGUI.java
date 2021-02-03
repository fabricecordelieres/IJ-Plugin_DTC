package DCT.GUI;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import DCT.tools.detector;
import DCT.tools.tracker;
import DCT.tools.dataHandler.PointSerie;
import ij.ImagePlus;
import ij.plugin.frame.RoiManager;

import javax.swing.SpringLayout;
import javax.swing.JCheckBox;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.awt.event.ItemEvent;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;

public class OutputGUI extends JDialog {

	boolean doDebug=true;
	
	private int[][] params=null;
	private PointSerie[][] detections=null;
	private ArrayList<ArrayList<PointSerie>> tracks=null;
	
	private static final long serialVersionUID = 1L;
	private final JPanel displayPanel = new JPanel();
	
	private JCheckBox chckbxShowNonProxColocDetections;
	private JCheckBox chckbxShowProxDetections;
	private JCheckBox chckbxShowColocDetections;
	
	private JRadioButton radioShowNonProxColocTracks;
	private JRadioButton radioShowProxTracks;
	private JRadioButton radioShowColocTracks;
	private JRadioButton radioShowColocOnlyTracks;
	private JRadioButton radioShowAllTracks;
	private JRadioButton radioShowProxOnlyTracks;
	private JButton closeButton;
	
	
	boolean showNonProxColocDetections=false;
	boolean showProxDetections=false;
	boolean showColocDetections=false;
	
	boolean showNonProxColocTracks=false;
	boolean showProxTracks=false;
	boolean showColocTracks=false;
	
	boolean showProxOnlyTracks=false;
	boolean showColocOnlyTracks=false;
	boolean showAllTracks=false;
	private JRadioButton radioShowNoTracks;
	private JPanel exportPanel;
	private SpringLayout sl_buttonPane;
	
	
	

	/**
	 * Create the dialog.
	 */
	public OutputGUI(int[][] params, PointSerie[][] detections, ArrayList<ArrayList<PointSerie>> tracks) {
		setResizable(false);
		setTitle("DCT Display");
		this.params=params;
		this.detections=detections;
		this.tracks=tracks;
		
		setBounds(100, 100, 512, 419);
		SpringLayout springLayout = new SpringLayout();
		springLayout.putConstraint(SpringLayout.NORTH, displayPanel, 0, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, displayPanel, 0, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, displayPanel, 358, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, displayPanel, 256, SpringLayout.WEST, getContentPane());
		getContentPane().setLayout(springLayout);
		getContentPane().add(displayPanel);
		SpringLayout sl_displayPanel = new SpringLayout();
		displayPanel.setLayout(sl_displayPanel);
		
		JLabel lblShowDetections = new JLabel("Show detections");
		sl_displayPanel.putConstraint(SpringLayout.NORTH, lblShowDetections, 12, SpringLayout.NORTH, displayPanel);
		sl_displayPanel.putConstraint(SpringLayout.WEST, lblShowDetections, 12, SpringLayout.WEST, displayPanel);
		lblShowDetections.setFont(new Font("Lucida Grande", Font.BOLD | Font.ITALIC, 13));
		displayPanel.add(lblShowDetections);
		
		chckbxShowNonProxColocDetections = new JCheckBox("Show non proximal/non coloc.");
		sl_displayPanel.putConstraint(SpringLayout.NORTH, chckbxShowNonProxColocDetections, 6, SpringLayout.SOUTH, lblShowDetections);
		sl_displayPanel.putConstraint(SpringLayout.WEST, chckbxShowNonProxColocDetections, 0, SpringLayout.WEST, lblShowDetections);
		chckbxShowNonProxColocDetections.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				showHideROIs();
			}
		});
		
		displayPanel.add(chckbxShowNonProxColocDetections);
		
		chckbxShowProxDetections = new JCheckBox("Show proximal");
		chckbxShowProxDetections.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				showHideROIs();
			}
		});
		sl_displayPanel.putConstraint(SpringLayout.NORTH, chckbxShowProxDetections, 6, SpringLayout.SOUTH, chckbxShowNonProxColocDetections);
		sl_displayPanel.putConstraint(SpringLayout.WEST, chckbxShowProxDetections, 0, SpringLayout.WEST, chckbxShowNonProxColocDetections);
		displayPanel.add(chckbxShowProxDetections);
		
		chckbxShowColocDetections = new JCheckBox("Show coloc.");
		chckbxShowColocDetections.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				showHideROIs();
			}
		});
		sl_displayPanel.putConstraint(SpringLayout.NORTH, chckbxShowColocDetections, 6, SpringLayout.SOUTH, chckbxShowProxDetections);
		sl_displayPanel.putConstraint(SpringLayout.WEST, chckbxShowColocDetections, 0, SpringLayout.WEST, chckbxShowNonProxColocDetections);
		displayPanel.add(chckbxShowColocDetections);
		
		JLabel lblShowTracks = new JLabel("Show tracks");
		sl_displayPanel.putConstraint(SpringLayout.NORTH, lblShowTracks, 12, SpringLayout.SOUTH, chckbxShowColocDetections);
		lblShowTracks.setFont(new Font("Lucida Grande", Font.BOLD | Font.ITALIC, 13));
		sl_displayPanel.putConstraint(SpringLayout.WEST, lblShowTracks, 0, SpringLayout.WEST, chckbxShowNonProxColocDetections);
		displayPanel.add(lblShowTracks);
		
		
		ButtonGroup bg=new ButtonGroup();
		radioShowNonProxColocTracks = new JRadioButton("Show non proximal/non coloc.");
		radioShowNonProxColocTracks.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				showHideROIs();
			}
		});
		sl_displayPanel.putConstraint(SpringLayout.NORTH, radioShowNonProxColocTracks, 6, SpringLayout.SOUTH, lblShowTracks);
		sl_displayPanel.putConstraint(SpringLayout.WEST, radioShowNonProxColocTracks, 0, SpringLayout.WEST, lblShowTracks);
		displayPanel.add(radioShowNonProxColocTracks);
		
		radioShowProxTracks = new JRadioButton("Show if has >=1 prox. tag");
		radioShowProxTracks.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				showHideROIs();
			}
		});
		sl_displayPanel.putConstraint(SpringLayout.NORTH, radioShowProxTracks, 6, SpringLayout.SOUTH, radioShowNonProxColocTracks);
		sl_displayPanel.putConstraint(SpringLayout.WEST, radioShowProxTracks, 0, SpringLayout.WEST, lblShowTracks);
		displayPanel.add(radioShowProxTracks);
		
		radioShowColocTracks = new JRadioButton("Show if has >=1 coloc. tag");
		radioShowColocTracks.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				showHideROIs();
			}
		});
		sl_displayPanel.putConstraint(SpringLayout.NORTH, radioShowColocTracks, 6, SpringLayout.SOUTH, radioShowProxTracks);
		sl_displayPanel.putConstraint(SpringLayout.WEST, radioShowColocTracks, 0, SpringLayout.WEST, lblShowTracks);
		displayPanel.add(radioShowColocTracks);
		
		radioShowProxOnlyTracks = new JRadioButton("Show prox. ONLY");
		radioShowProxOnlyTracks.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				showHideROIs();
			}
		});
		sl_displayPanel.putConstraint(SpringLayout.NORTH, radioShowProxOnlyTracks, 6, SpringLayout.SOUTH, radioShowColocTracks);
		sl_displayPanel.putConstraint(SpringLayout.WEST, radioShowProxOnlyTracks, 0, SpringLayout.WEST, lblShowTracks);
		displayPanel.add(radioShowProxOnlyTracks);
		
		radioShowColocOnlyTracks = new JRadioButton("Show coloc. ONLY");
		radioShowColocOnlyTracks.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				showHideROIs();
			}
		});
		sl_displayPanel.putConstraint(SpringLayout.NORTH, radioShowColocOnlyTracks, 6, SpringLayout.SOUTH, radioShowProxOnlyTracks);
		sl_displayPanel.putConstraint(SpringLayout.WEST, radioShowColocOnlyTracks, 0, SpringLayout.WEST, lblShowTracks);
		displayPanel.add(radioShowColocOnlyTracks);
		
		radioShowAllTracks = new JRadioButton("Show all");
		radioShowAllTracks.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				showHideROIs();
			}
		});
		sl_displayPanel.putConstraint(SpringLayout.NORTH, radioShowAllTracks, 6, SpringLayout.SOUTH, radioShowColocOnlyTracks);
		sl_displayPanel.putConstraint(SpringLayout.WEST, radioShowAllTracks, 0, SpringLayout.WEST, lblShowTracks);
		displayPanel.add(radioShowAllTracks);
		
		radioShowNoTracks = new JRadioButton("Show none");
		radioShowNoTracks.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				showHideROIs();
			}
		});
		sl_displayPanel.putConstraint(SpringLayout.NORTH, radioShowNoTracks, 6, SpringLayout.SOUTH, radioShowAllTracks);
		sl_displayPanel.putConstraint(SpringLayout.WEST, radioShowNoTracks, 0, SpringLayout.WEST, lblShowTracks);
		radioShowNoTracks.setSelected(true);
		displayPanel.add(radioShowNoTracks);
		
		bg.add(radioShowNonProxColocTracks);
		bg.add(radioShowProxTracks);
		bg.add(radioShowColocTracks);
		bg.add(radioShowProxOnlyTracks);
		bg.add(radioShowColocOnlyTracks);
		bg.add(radioShowAllTracks);
		bg.add(radioShowNoTracks);
		
		exportPanel = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, exportPanel, 0, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, exportPanel, 0, SpringLayout.EAST, displayPanel);
		springLayout.putConstraint(SpringLayout.SOUTH, exportPanel, 0, SpringLayout.SOUTH, displayPanel);
		springLayout.putConstraint(SpringLayout.EAST, exportPanel, 0, SpringLayout.EAST, getContentPane());
		getContentPane().add(exportPanel);
		SpringLayout sl_exportPanel = new SpringLayout();
		exportPanel.setLayout(sl_exportPanel);
		
		{
			JPanel buttonPane = new JPanel();
			springLayout.putConstraint(SpringLayout.NORTH, buttonPane, 0, SpringLayout.SOUTH, displayPanel);
			springLayout.putConstraint(SpringLayout.SOUTH, buttonPane, 0, SpringLayout.SOUTH, getContentPane());
			springLayout.putConstraint(SpringLayout.EAST, buttonPane, 0, SpringLayout.EAST, getContentPane());
			
			JLabel lblExportTables = new JLabel("Export tables");
			sl_exportPanel.putConstraint(SpringLayout.NORTH, lblExportTables, 36, SpringLayout.NORTH, exportPanel);
			sl_exportPanel.putConstraint(SpringLayout.WEST, lblExportTables, 12, SpringLayout.WEST, exportPanel);
			lblExportTables.setFont(new Font("Lucida Grande", Font.BOLD | Font.ITALIC, 13));
			exportPanel.add(lblExportTables);
			
			JButton btnExportNonProxColoc = new JButton("Export non prox./non coloc.");
			btnExportNonProxColoc.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tracker.sendTracksToResultsTable(tracks, "NonProxColoc");
				}
			});
			sl_exportPanel.putConstraint(SpringLayout.NORTH, btnExportNonProxColoc, 24, SpringLayout.SOUTH, lblExportTables);
			sl_exportPanel.putConstraint(SpringLayout.WEST, btnExportNonProxColoc, 0, SpringLayout.WEST, lblExportTables);
			exportPanel.add(btnExportNonProxColoc);
			
			JButton btnExportProx = new JButton("Export if has >=1 prox. tag");
			btnExportProx.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tracker.sendTracksToResultsTable(tracks, "Prox");
				}
			});
			sl_exportPanel.putConstraint(SpringLayout.NORTH, btnExportProx, 6, SpringLayout.SOUTH, btnExportNonProxColoc);
			sl_exportPanel.putConstraint(SpringLayout.WEST, btnExportProx, 0, SpringLayout.WEST, lblExportTables);
			sl_exportPanel.putConstraint(SpringLayout.EAST, btnExportProx, 0, SpringLayout.EAST, btnExportNonProxColoc);
			exportPanel.add(btnExportProx);
			
			JButton btnExportColoc = new JButton("Export if has >=1 coloc. tag");
			btnExportColoc.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tracker.sendTracksToResultsTable(tracks, "Coloc");
				}
			});
			sl_exportPanel.putConstraint(SpringLayout.NORTH, btnExportColoc, 6, SpringLayout.SOUTH, btnExportProx);
			sl_exportPanel.putConstraint(SpringLayout.WEST, btnExportColoc, 0, SpringLayout.WEST, lblExportTables);
			sl_exportPanel.putConstraint(SpringLayout.EAST, btnExportColoc, 0, SpringLayout.EAST, btnExportNonProxColoc);
			exportPanel.add(btnExportColoc);
			
			JButton btnExportProxOnly = new JButton("Export prox. ONLY");
			btnExportProxOnly.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tracker.sendTracksToResultsTable(tracks, "ProxOnly");
				}
			});
			sl_exportPanel.putConstraint(SpringLayout.NORTH, btnExportProxOnly, 6, SpringLayout.SOUTH, btnExportColoc);
			sl_exportPanel.putConstraint(SpringLayout.WEST, btnExportProxOnly, 0, SpringLayout.WEST, lblExportTables);
			sl_exportPanel.putConstraint(SpringLayout.EAST, btnExportProxOnly, 0, SpringLayout.EAST, btnExportNonProxColoc);
			exportPanel.add(btnExportProxOnly);
			
			JButton btnExportColocOnly = new JButton("Export coloc. ONLY");
			btnExportColocOnly.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tracker.sendTracksToResultsTable(tracks, "ColocOnly");
				}
			});
			sl_exportPanel.putConstraint(SpringLayout.NORTH, btnExportColocOnly, 6, SpringLayout.SOUTH, btnExportProxOnly);
			sl_exportPanel.putConstraint(SpringLayout.WEST, btnExportColocOnly, 0, SpringLayout.WEST, lblExportTables);
			sl_exportPanel.putConstraint(SpringLayout.EAST, btnExportColocOnly, 0, SpringLayout.EAST, btnExportNonProxColoc);
			exportPanel.add(btnExportColocOnly);
			
			JButton btnExportAll = new JButton("Export all");
			btnExportAll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tracker.sendTracksToResultsTable(tracks, "All");
				}
			});
			sl_exportPanel.putConstraint(SpringLayout.NORTH, btnExportAll, 6, SpringLayout.SOUTH, btnExportColocOnly);
			sl_exportPanel.putConstraint(SpringLayout.WEST, btnExportAll, 0, SpringLayout.WEST, lblExportTables);
			sl_exportPanel.putConstraint(SpringLayout.EAST, btnExportAll, 0, SpringLayout.EAST, btnExportNonProxColoc);
			exportPanel.add(btnExportAll);
			springLayout.putConstraint(SpringLayout.WEST, buttonPane, 0, SpringLayout.WEST, getContentPane());
			getContentPane().add(buttonPane);
			{
				closeButton = new JButton("Close");
				closeButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				sl_buttonPane = new SpringLayout();
				sl_buttonPane.putConstraint(SpringLayout.NORTH, closeButton, 5, SpringLayout.NORTH, buttonPane);
				sl_buttonPane.putConstraint(SpringLayout.WEST, closeButton, 218, SpringLayout.WEST, buttonPane);
				buttonPane.setLayout(sl_buttonPane);
				closeButton.setActionCommand("OK");
				buttonPane.add(closeButton);
				getRootPane().setDefaultButton(closeButton);
			}
		}
		
	}
	
	/**
	 * This function reacts to the checking of tick boxes and radio buttons. It basically performs filtering of detections/tracks and sends the remaining to the RoiManager
	 */
	public void showHideROIs() {
		updateStatus();
		
		RoiManager rm=RoiManager.getInstance();
		if(rm==null) {
			rm=new RoiManager();
			rm.setVisible(true);
		}else {
			rm.reset();
		}
		
		if(showNonProxColocDetections) detector.sendFilteredDetectionsToRoiManager(detections, params, detector.CIRCLE, "NonProxColoc");
		if(showProxDetections) detector.sendFilteredDetectionsToRoiManager(detections, params, detector.CROSS, "Prox");
		if(showColocDetections) detector.sendFilteredDetectionsToRoiManager(detections, params, detector.CROSS, "Coloc");
		
		if(showNonProxColocTracks) tracker.sendTracksToRoiManager(tracks, "NonProxColoc");
		if(showProxTracks) tracker.sendTracksToRoiManager(tracks, "Prox");
		if(showColocTracks) tracker.sendTracksToRoiManager(tracks, "Coloc");
		
		if(showProxOnlyTracks) tracker.sendTracksToRoiManager(tracks, "ProxOnly");
		if(showColocOnlyTracks) tracker.sendTracksToRoiManager(tracks, "ColocOnly");
		if(showAllTracks) tracker.sendTracksToRoiManager(tracks, "All");
		
		//Display
		if(rm!=null) {
			rm.runCommand((ImagePlus) null,"Remove Channel Info");
			rm.runCommand("Associate", "true");
			rm.runCommand((ImagePlus) null,"Show All");
		}
		
		
		
	}
	
	/**
	 * Get an updated view of the tick boxes and radio buttons
	 */
	public void updateStatus() {
		showNonProxColocDetections=chckbxShowNonProxColocDetections.isSelected();
		showProxDetections=chckbxShowProxDetections.isSelected();
		showColocDetections=chckbxShowColocDetections.isSelected();
		
		showNonProxColocTracks=radioShowNonProxColocTracks.isSelected();
		showProxTracks=radioShowProxTracks.isSelected();
		showColocTracks=radioShowColocTracks.isSelected();
		
		showProxOnlyTracks=radioShowProxOnlyTracks.isSelected();
		showColocOnlyTracks=radioShowColocOnlyTracks.isSelected();
		showAllTracks=radioShowAllTracks.isSelected();
	}
}
