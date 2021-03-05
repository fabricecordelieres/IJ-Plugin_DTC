package DTC.GUI;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.WindowManager;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;

import javax.swing.SpringLayout;

import DTC.tools.detector;
import DTC.tools.tracker;
import DTC.tools.dataHandler.PointSerie;

import javax.swing.JCheckBox;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.awt.event.ItemEvent;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import java.awt.Color;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class OutputGUI extends JDialog {

	boolean doDebug=true;
	
	private int[][] params=null;
	private PointSerie[][] detections=null;
	private ArrayList<ArrayList<PointSerie>> tracks=null;
	
	private static final long serialVersionUID = 1L;
	private final JPanel panelShowDetections = new JPanel();
	
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
	
	boolean doZoomIn=false;
	int zoomInValue=100;
	int lineWidth=1;
	int roiRadius=2;
	
	private JRadioButton radioShowNoTracks;
	private JPanel panelExport;
	private SpringLayout sl_buttonPane;
	private JCheckBox chckbxZoomIn;
	private JLabel lblLineWidth;
	private JLabel lblZoom;
	private JSlider sliderZoom;
	private JButton btnExportStats;
	private JPanel buttonPane;
	private JPanel panelIndividualDetectionOptions;
	private JButton btnExportNonProxColoc;
	private ButtonGroup bg;
	private JSlider sliderWidth;
	private JSlider sliderRadius;
	
	
	

	/**
	 * Create the dialog.
	 */
	public OutputGUI(int[][] params, PointSerie[][] detections, ArrayList<ArrayList<PointSerie>> tracks) {
		getPrefs();
		
		setResizable(false);
		setTitle("DTC Display");
		this.params=params;
		this.detections=detections;
		this.tracks=tracks;
		
		setBounds(100, 100, 512, 550);
		SpringLayout springLayout = new SpringLayout();
		springLayout.putConstraint(SpringLayout.NORTH, panelShowDetections, 0, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, panelShowDetections, 0, SpringLayout.EAST, getContentPane());
		getContentPane().setLayout(springLayout);
		
		JPanel panelShowTracks = new JPanel();
		springLayout.putConstraint(SpringLayout.WEST, panelShowDetections, 0, SpringLayout.EAST, panelShowTracks);
		springLayout.putConstraint(SpringLayout.SOUTH, panelShowTracks, 212, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, panelShowTracks, 0, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, panelShowTracks, 0, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, panelShowTracks, 256, SpringLayout.WEST, getContentPane());
		panelShowTracks.setBorder(new TitledBorder(null, "Show tracks", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		getContentPane().add(panelShowTracks);
		SpringLayout sl_panelShowTracks = new SpringLayout();
		panelShowTracks.setLayout(sl_panelShowTracks);
		
		radioShowNoTracks = new JRadioButton("Show none");
		sl_panelShowTracks.putConstraint(SpringLayout.NORTH, radioShowNoTracks, 12, SpringLayout.NORTH, panelShowTracks);
		sl_panelShowTracks.putConstraint(SpringLayout.WEST, radioShowNoTracks, 6, SpringLayout.WEST, panelShowTracks);
		sl_panelShowTracks.putConstraint(SpringLayout.EAST, radioShowNoTracks, -6, SpringLayout.EAST, panelShowTracks);
		panelShowTracks.add(radioShowNoTracks);
		radioShowNoTracks.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				savePrefs();
			}
		});
		radioShowNoTracks.setSelected(true);
		radioShowNonProxColocTracks = new JRadioButton("Show non proximal/non coloc.");
		sl_panelShowTracks.putConstraint(SpringLayout.NORTH, radioShowNonProxColocTracks, 0, SpringLayout.SOUTH, radioShowNoTracks);
		sl_panelShowTracks.putConstraint(SpringLayout.WEST, radioShowNonProxColocTracks, 0, SpringLayout.WEST, radioShowNoTracks);
		sl_panelShowTracks.putConstraint(SpringLayout.EAST, radioShowNonProxColocTracks, 0, SpringLayout.EAST, radioShowNoTracks);
		panelShowTracks.add(radioShowNonProxColocTracks);
		radioShowNonProxColocTracks.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				savePrefs();
			}
		});
		
		radioShowProxTracks = new JRadioButton("Show if has >=1 prox. tag");
		sl_panelShowTracks.putConstraint(SpringLayout.NORTH, radioShowProxTracks, 0, SpringLayout.SOUTH, radioShowNonProxColocTracks);
		sl_panelShowTracks.putConstraint(SpringLayout.WEST, radioShowProxTracks, 0, SpringLayout.WEST, radioShowNoTracks);
		sl_panelShowTracks.putConstraint(SpringLayout.EAST, radioShowProxTracks, 0, SpringLayout.EAST, radioShowNoTracks);
		panelShowTracks.add(radioShowProxTracks);
		radioShowProxTracks.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				savePrefs();
			}
		});
		
		radioShowColocTracks = new JRadioButton("Show if has >=1 coloc. tag");
		sl_panelShowTracks.putConstraint(SpringLayout.NORTH, radioShowColocTracks, 0, SpringLayout.SOUTH, radioShowProxTracks);
		sl_panelShowTracks.putConstraint(SpringLayout.WEST, radioShowColocTracks, 0, SpringLayout.WEST, radioShowNoTracks);
		sl_panelShowTracks.putConstraint(SpringLayout.EAST, radioShowColocTracks, 0, SpringLayout.EAST, radioShowNoTracks);
		panelShowTracks.add(radioShowColocTracks);
		radioShowColocTracks.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				savePrefs();
			}
		});
		
		radioShowProxOnlyTracks = new JRadioButton("Show prox. ONLY");
		sl_panelShowTracks.putConstraint(SpringLayout.NORTH, radioShowProxOnlyTracks, 0, SpringLayout.SOUTH, radioShowColocTracks);
		sl_panelShowTracks.putConstraint(SpringLayout.WEST, radioShowProxOnlyTracks, 0, SpringLayout.WEST, radioShowNoTracks);
		sl_panelShowTracks.putConstraint(SpringLayout.EAST, radioShowProxOnlyTracks, 0, SpringLayout.EAST, radioShowNoTracks);
		panelShowTracks.add(radioShowProxOnlyTracks);
		radioShowProxOnlyTracks.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				savePrefs();
			}
		});
		
		radioShowColocOnlyTracks = new JRadioButton("Show coloc. ONLY");
		sl_panelShowTracks.putConstraint(SpringLayout.NORTH, radioShowColocOnlyTracks, 0, SpringLayout.SOUTH, radioShowProxOnlyTracks);
		sl_panelShowTracks.putConstraint(SpringLayout.WEST, radioShowColocOnlyTracks, 0, SpringLayout.WEST, radioShowNoTracks);
		sl_panelShowTracks.putConstraint(SpringLayout.EAST, radioShowColocOnlyTracks, 0, SpringLayout.EAST, radioShowNoTracks);
		panelShowTracks.add(radioShowColocOnlyTracks);
		radioShowColocOnlyTracks.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				savePrefs();
			}
		});
		
		radioShowAllTracks = new JRadioButton("Show all");
		sl_panelShowTracks.putConstraint(SpringLayout.NORTH, radioShowAllTracks, 0, SpringLayout.SOUTH, radioShowColocOnlyTracks);
		sl_panelShowTracks.putConstraint(SpringLayout.WEST, radioShowAllTracks, 0, SpringLayout.WEST, radioShowNoTracks);
		sl_panelShowTracks.putConstraint(SpringLayout.EAST, radioShowAllTracks, 0, SpringLayout.EAST, radioShowNoTracks);
		panelShowTracks.add(radioShowAllTracks);
		radioShowAllTracks.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				savePrefs();
			}
		});
		
		bg=new ButtonGroup();
		bg.add(radioShowAllTracks);
		bg.add(radioShowColocOnlyTracks);
		bg.add(radioShowColocTracks);
		bg.add(radioShowNonProxColocTracks);
		bg.add(radioShowNoTracks);
		bg.add(radioShowProxOnlyTracks);
		bg.add(radioShowProxTracks);
		
		panelIndividualDetectionOptions = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, panelIndividualDetectionOptions, 6, SpringLayout.SOUTH, panelShowTracks);
		springLayout.putConstraint(SpringLayout.WEST, panelIndividualDetectionOptions, 0, SpringLayout.WEST, getContentPane());
		panelIndividualDetectionOptions.setBorder(new TitledBorder(null, "Individual detection options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		getContentPane().add(panelIndividualDetectionOptions);
		SpringLayout sl_panelIndividualDetectionOptions = new SpringLayout();
		panelIndividualDetectionOptions.setLayout(sl_panelIndividualDetectionOptions);
		
		chckbxZoomIn = new JCheckBox("Zoom in");
		chckbxZoomIn.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				savePrefs();
			}
		});
		chckbxZoomIn.setSelected(doZoomIn);
		sl_panelIndividualDetectionOptions.putConstraint(SpringLayout.NORTH, chckbxZoomIn, 6, SpringLayout.NORTH, panelIndividualDetectionOptions);
		sl_panelIndividualDetectionOptions.putConstraint(SpringLayout.WEST, chckbxZoomIn, 6, SpringLayout.WEST, panelIndividualDetectionOptions);
		sl_panelIndividualDetectionOptions.putConstraint(SpringLayout.EAST, chckbxZoomIn, -6, SpringLayout.EAST, panelIndividualDetectionOptions);
		panelIndividualDetectionOptions.add(chckbxZoomIn);
		
		lblZoom = new JLabel("Zoom (%)");
		sl_panelIndividualDetectionOptions.putConstraint(SpringLayout.NORTH, lblZoom, 6, SpringLayout.SOUTH, chckbxZoomIn);
		sl_panelIndividualDetectionOptions.putConstraint(SpringLayout.WEST, lblZoom, 0, SpringLayout.WEST, chckbxZoomIn);
		sl_panelIndividualDetectionOptions.putConstraint(SpringLayout.EAST, lblZoom, 0, SpringLayout.EAST, chckbxZoomIn);
		panelIndividualDetectionOptions.add(lblZoom);
		lblZoom.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		
		sliderZoom = new JSlider();
		sliderZoom.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				savePrefs();
			}
		});
		sl_panelIndividualDetectionOptions.putConstraint(SpringLayout.NORTH, sliderZoom, 0, SpringLayout.SOUTH, lblZoom);
		sl_panelIndividualDetectionOptions.putConstraint(SpringLayout.WEST, sliderZoom, 0, SpringLayout.WEST, chckbxZoomIn);
		sl_panelIndividualDetectionOptions.putConstraint(SpringLayout.EAST, sliderZoom, 0, SpringLayout.EAST, chckbxZoomIn);
		panelIndividualDetectionOptions.add(sliderZoom);
		sliderZoom.setSnapToTicks(true);
		sliderZoom.setPaintTicks(true);
		sliderZoom.setPaintLabels(true);
		sliderZoom.setMinorTickSpacing(50);
		sliderZoom.setMinimum(100);
		sliderZoom.setMaximum(850);
		sliderZoom.setMajorTickSpacing(150);
		sliderZoom.setValue(zoomInValue);
		sliderZoom.setEnabled(doZoomIn);
		
		lblLineWidth = new JLabel("Line width (1=0.1 pix.)");
		sl_panelIndividualDetectionOptions.putConstraint(SpringLayout.NORTH, lblLineWidth, 6, SpringLayout.SOUTH, sliderZoom);
		sl_panelIndividualDetectionOptions.putConstraint(SpringLayout.WEST, lblLineWidth, 0, SpringLayout.WEST, chckbxZoomIn);
		sl_panelIndividualDetectionOptions.putConstraint(SpringLayout.EAST, lblLineWidth, 0, SpringLayout.EAST, chckbxZoomIn);
		panelIndividualDetectionOptions.add(lblLineWidth);
		lblLineWidth.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		
		sliderWidth = new JSlider();
		sliderWidth.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				savePrefs();
			}
		});
		sl_panelIndividualDetectionOptions.putConstraint(SpringLayout.NORTH, sliderWidth, 0, SpringLayout.SOUTH, lblLineWidth);
		sl_panelIndividualDetectionOptions.putConstraint(SpringLayout.WEST, sliderWidth, 0, SpringLayout.WEST, lblLineWidth);
		sl_panelIndividualDetectionOptions.putConstraint(SpringLayout.EAST, sliderWidth, 0, SpringLayout.EAST, lblLineWidth);
		panelIndividualDetectionOptions.add(sliderWidth);
		sliderWidth.setSnapToTicks(true);
		sliderWidth.setMajorTickSpacing(3);
		sliderWidth.setPaintLabels(true);
		sliderWidth.setMinorTickSpacing(1);
		sliderWidth.setMinimum(1);
		sliderWidth.setMaximum(10);
		sliderWidth.setPaintTicks(true);
		sliderWidth.setValue(1);
		
		JLabel lblRadius = new JLabel("Radius (pix.)");
		sl_panelIndividualDetectionOptions.putConstraint(SpringLayout.NORTH, lblRadius, 6, SpringLayout.SOUTH, sliderWidth);
		sl_panelIndividualDetectionOptions.putConstraint(SpringLayout.WEST, lblRadius, 0, SpringLayout.WEST, chckbxZoomIn);
		sl_panelIndividualDetectionOptions.putConstraint(SpringLayout.EAST, lblRadius, 0, SpringLayout.EAST, chckbxZoomIn);
		panelIndividualDetectionOptions.add(lblRadius);
		
		sliderRadius = new JSlider();
		sliderRadius.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				savePrefs();
			}
		});
		sl_panelIndividualDetectionOptions.putConstraint(SpringLayout.NORTH, sliderRadius, 0, SpringLayout.SOUTH, lblRadius);
		sl_panelIndividualDetectionOptions.putConstraint(SpringLayout.WEST, sliderRadius, 0, SpringLayout.WEST, chckbxZoomIn);
		sl_panelIndividualDetectionOptions.putConstraint(SpringLayout.EAST, sliderRadius, 0, SpringLayout.EAST, chckbxZoomIn);
		panelIndividualDetectionOptions.add(sliderRadius);
		sliderRadius.setSnapToTicks(true);
		sliderRadius.setMajorTickSpacing(3);
		sliderRadius.setPaintLabels(true);
		sliderRadius.setMinorTickSpacing(1);
		sliderRadius.setMinimum(1);
		sliderRadius.setMaximum(16);
		sliderRadius.setPaintTicks(true);
		sliderRadius.setValue(1);
		
		panelShowDetections.setBorder(new TitledBorder(null, "Show detections", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		getContentPane().add(panelShowDetections);
		
		chckbxShowNonProxColocDetections = new JCheckBox("Show non proximal/non coloc.");
		chckbxShowNonProxColocDetections.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				savePrefs();
			}
		});
		SpringLayout sl_panelShowDetections = new SpringLayout();
		sl_panelShowDetections.putConstraint(SpringLayout.NORTH, chckbxShowNonProxColocDetections, 56, SpringLayout.NORTH, panelShowDetections);
		sl_panelShowDetections.putConstraint(SpringLayout.WEST, chckbxShowNonProxColocDetections, 6, SpringLayout.WEST, panelShowDetections);
		sl_panelShowDetections.putConstraint(SpringLayout.EAST, chckbxShowNonProxColocDetections, -6, SpringLayout.EAST, panelShowDetections);
		panelShowDetections.setLayout(sl_panelShowDetections);
		
		panelShowDetections.add(chckbxShowNonProxColocDetections);
		
		chckbxShowProxDetections = new JCheckBox("Show proximal");
		sl_panelShowDetections.putConstraint(SpringLayout.NORTH, chckbxShowProxDetections, 0, SpringLayout.SOUTH, chckbxShowNonProxColocDetections);
		sl_panelShowDetections.putConstraint(SpringLayout.WEST, chckbxShowProxDetections, 0, SpringLayout.WEST, chckbxShowNonProxColocDetections);
		sl_panelShowDetections.putConstraint(SpringLayout.EAST, chckbxShowProxDetections, 0, SpringLayout.EAST, chckbxShowNonProxColocDetections);
		chckbxShowProxDetections.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				savePrefs();
			}
		});
		panelShowDetections.add(chckbxShowProxDetections);
		
		chckbxShowColocDetections = new JCheckBox("Show coloc.");
		sl_panelShowDetections.putConstraint(SpringLayout.NORTH, chckbxShowColocDetections, 0, SpringLayout.SOUTH, chckbxShowProxDetections);
		sl_panelShowDetections.putConstraint(SpringLayout.WEST, chckbxShowColocDetections, 0, SpringLayout.WEST, chckbxShowNonProxColocDetections);
		sl_panelShowDetections.putConstraint(SpringLayout.EAST, chckbxShowColocDetections, 0, SpringLayout.EAST, chckbxShowNonProxColocDetections);
		chckbxShowColocDetections.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				savePrefs();
			}
		});
		panelShowDetections.add(chckbxShowColocDetections);
		
		panelExport = new JPanel();
		springLayout.putConstraint(SpringLayout.SOUTH, panelShowDetections, -6, SpringLayout.NORTH, panelExport);
		springLayout.putConstraint(SpringLayout.EAST, panelIndividualDetectionOptions, 0, SpringLayout.WEST, panelExport);
		springLayout.putConstraint(SpringLayout.NORTH, panelExport, 218, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, panelExport, 256, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, panelExport, 0, SpringLayout.EAST, getContentPane());
		panelExport.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Exports", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		getContentPane().add(panelExport);
		
		{
			buttonPane = new JPanel();
			springLayout.putConstraint(SpringLayout.SOUTH, panelIndividualDetectionOptions, -6, SpringLayout.NORTH, buttonPane);
			springLayout.putConstraint(SpringLayout.SOUTH, panelExport, -6, SpringLayout.NORTH, buttonPane);
			springLayout.putConstraint(SpringLayout.NORTH, buttonPane, 493, SpringLayout.NORTH, getContentPane());
			springLayout.putConstraint(SpringLayout.SOUTH, buttonPane, 0, SpringLayout.SOUTH, getContentPane());
			springLayout.putConstraint(SpringLayout.WEST, buttonPane, 0, SpringLayout.WEST, getContentPane());
			springLayout.putConstraint(SpringLayout.EAST, buttonPane, 0, SpringLayout.EAST, getContentPane());
			
			btnExportNonProxColoc = new JButton("Export non prox./non coloc.");
			btnExportNonProxColoc.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tracker.sendTracksToResultsTable(tracks, "NonProxColoc");
				}
			});
			SpringLayout sl_panelExport = new SpringLayout();
			sl_panelExport.putConstraint(SpringLayout.NORTH, btnExportNonProxColoc, 12, SpringLayout.NORTH, panelExport);
			sl_panelExport.putConstraint(SpringLayout.WEST, btnExportNonProxColoc, 6, SpringLayout.WEST, panelExport);
			sl_panelExport.putConstraint(SpringLayout.EAST, btnExportNonProxColoc, -6, SpringLayout.EAST, panelExport);
			panelExport.setLayout(sl_panelExport);
			panelExport.add(btnExportNonProxColoc);
			
			JButton btnExportProx = new JButton("Export if has >=1 prox. tag");
			sl_panelExport.putConstraint(SpringLayout.NORTH, btnExportProx, 0, SpringLayout.SOUTH, btnExportNonProxColoc);
			sl_panelExport.putConstraint(SpringLayout.WEST, btnExportProx, 0, SpringLayout.WEST, btnExportNonProxColoc);
			sl_panelExport.putConstraint(SpringLayout.EAST, btnExportProx, 0, SpringLayout.EAST, btnExportNonProxColoc);
			btnExportProx.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tracker.sendTracksToResultsTable(tracks, "Prox");
				}
			});
			panelExport.add(btnExportProx);
			
			JButton btnExportColoc = new JButton("Export if has >=1 coloc. tag");
			sl_panelExport.putConstraint(SpringLayout.NORTH, btnExportColoc, 0, SpringLayout.SOUTH, btnExportProx);
			sl_panelExport.putConstraint(SpringLayout.WEST, btnExportColoc, 0, SpringLayout.WEST, btnExportNonProxColoc);
			sl_panelExport.putConstraint(SpringLayout.EAST, btnExportColoc, 0, SpringLayout.EAST, btnExportNonProxColoc);
			btnExportColoc.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tracker.sendTracksToResultsTable(tracks, "Coloc");
				}
			});
			panelExport.add(btnExportColoc);
			
			JButton btnExportProxOnly = new JButton("Export prox. ONLY");
			sl_panelExport.putConstraint(SpringLayout.NORTH, btnExportProxOnly, 0, SpringLayout.SOUTH, btnExportColoc);
			sl_panelExport.putConstraint(SpringLayout.WEST, btnExportProxOnly, 0, SpringLayout.WEST, btnExportNonProxColoc);
			sl_panelExport.putConstraint(SpringLayout.EAST, btnExportProxOnly, 0, SpringLayout.EAST, btnExportNonProxColoc);
			btnExportProxOnly.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tracker.sendTracksToResultsTable(tracks, "ProxOnly");
				}
			});
			panelExport.add(btnExportProxOnly);
			
			JButton btnExportColocOnly = new JButton("Export coloc. ONLY");
			sl_panelExport.putConstraint(SpringLayout.NORTH, btnExportColocOnly, 0, SpringLayout.SOUTH, btnExportProxOnly);
			sl_panelExport.putConstraint(SpringLayout.WEST, btnExportColocOnly, 0, SpringLayout.WEST, btnExportNonProxColoc);
			sl_panelExport.putConstraint(SpringLayout.EAST, btnExportColocOnly, 0, SpringLayout.EAST, btnExportNonProxColoc);
			btnExportColocOnly.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tracker.sendTracksToResultsTable(tracks, "ColocOnly");
				}
			});
			panelExport.add(btnExportColocOnly);
			
			JButton btnExportAll = new JButton("Export all");
			sl_panelExport.putConstraint(SpringLayout.NORTH, btnExportAll, 0, SpringLayout.SOUTH, btnExportColocOnly);
			sl_panelExport.putConstraint(SpringLayout.WEST, btnExportAll, 0, SpringLayout.WEST, btnExportNonProxColoc);
			sl_panelExport.putConstraint(SpringLayout.EAST, btnExportAll, 0, SpringLayout.EAST, btnExportNonProxColoc);
			btnExportAll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tracker.sendTracksToResultsTable(tracks, "All");
				}
			});
			panelExport.add(btnExportAll);
			
			btnExportStats = new JButton("Export stats");
			sl_panelExport.putConstraint(SpringLayout.NORTH, btnExportStats, 12, SpringLayout.SOUTH, btnExportAll);
			sl_panelExport.putConstraint(SpringLayout.WEST, btnExportStats, 0, SpringLayout.WEST, btnExportNonProxColoc);
			sl_panelExport.putConstraint(SpringLayout.EAST, btnExportStats, 0, SpringLayout.EAST, btnExportNonProxColoc);
			btnExportStats.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tracker.sendTracksToResultsTable(tracks, "Stats");
				}
			});
			panelExport.add(btnExportStats);
			
			getContentPane().add(buttonPane);
			{
				closeButton = new JButton("Close");
				closeButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						WindowManager.getCurrentImage().setOverlay(null);
						dispose();
					}
				});
				sl_buttonPane = new SpringLayout();
				sl_buttonPane.putConstraint(SpringLayout.NORTH, closeButton, 5, SpringLayout.NORTH, buttonPane);
				sl_buttonPane.putConstraint(SpringLayout.WEST, closeButton, 220, SpringLayout.WEST, buttonPane);
				sl_buttonPane.putConstraint(SpringLayout.SOUTH, closeButton, -5, SpringLayout.SOUTH, buttonPane);
				sl_buttonPane.putConstraint(SpringLayout.EAST, closeButton, -220, SpringLayout.EAST, buttonPane);
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
		if(isVisible()) {
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
	
	/**
	 * Save preferences
	 */
	public void savePrefs() {
		if(isVisible()) {
			Roi roi=null;
			doZoomIn=chckbxZoomIn.isSelected();
			sliderZoom.setEnabled(doZoomIn);
			zoomInValue=sliderZoom.getValue();
			lineWidth=sliderWidth.getValue();
			roiRadius=sliderRadius.getValue();
			
			Prefs.set("Coloc_And_Track_doZoomIn.boolean", doZoomIn);
			Prefs.set("Coloc_And_Track_ZoomInValue.double", zoomInValue);
			Prefs.set("Coloc_And_Track_lineWidth.double", lineWidth);
			Prefs.set("Coloc_And_Track_roiRadius.double", roiRadius);
			
			ImagePlus ip=WindowManager.getCurrentImage();
			if(ip!=null)  roi=ip.getRoi();
			
			showHideROIs();
			
			if(roi!=null) {
				Color color= roi.getStrokeColor();
				
				if(roi.getType()==Roi.OVAL) {
					int x=roi.getBounds().x+roi.getBounds().width/2;
					int y=roi.getBounds().y+roi.getBounds().height/2;
					roi=new OvalRoi(x-roiRadius, y-roiRadius, 2*roiRadius+1, 2*roiRadius+1);
					if(doZoomIn)  IJ.run("Set... ", "zoom="+zoomInValue+" x="+x+" y="+y);
				}
				
				roi.setStrokeColor(color);
				roi.setStrokeWidth(lineWidth/10.0);
				ip.setRoi(roi);
				
				if(roi.getType()!=Roi.OVAL) if(doZoomIn)  IJ.run("To Selection", "");
			}
		}
	}
	
	/**
	 * Restore saved preferences
	 */
	public void getPrefs() {
		doZoomIn=Prefs.get("Coloc_And_Track_doZoomIn.boolean", false);
		zoomInValue=(int) Prefs.get("Coloc_And_Track_ZoomInValue.double", 400);
		lineWidth=(int) Prefs.get("Coloc_And_Track_lineWidth.double", 1);
		roiRadius=(int) Prefs.get("Coloc_And_Track_roiRadius.double", 2);
	}
}
