# IJ-Plugin_DTC
_Detect, Track, Colocalize_

## What does it do ?
This plugin is aimed at Detecting small object (small in reference to the optical resolution), Track each individual object over the expected 2 colors, 2D+time images, and Co-localize/check for proximity between detections from both channels, over time.

## How does it work ?
### Detection
As objects are supposed to be small relative to the optical resolution, the choice has been made to rely on local maxima detection, taking benefit from the already implemented ImageJ's function. To help with detection, a two steps filtering process is applied: first a median filter will be performed to lower the noise contribution. As this first step will locally homogeneize the intensities, a gaussian filter is applied in the same vicinity to reshape locally homogeneized intensity profiles into "dome-like" profiles. The local maxima detection can now be performed, using an appropriate tolerance, defining how far from the baseline a local maximum intensity should be to be taken into account.
All parameters are adjustable individually for each channel.

![Detection principle](https://github.com/fabricecordelieres/IJ-Plugin_DTC/blob/master/images/Detection_principle.png)

### Tracking
Tracking is performed in the most simple way, for each channel. Individual detections are stored into list, one list per timepoint. Starting from the first timepoint, the first detection is taken into account. All detections for the second timepoint are considered: distances for the first detection to all detections from timepoint 2 are computed, the minimum distance being retained. This distance is then compared to the maximum expected displacement: in case the distance is smaller than this limit, both detections are considered to be connected/to belong to the same track. The linked detection is then erased from the list of detections for timepoint 2, and used to look for its connected conterpart within the list of detections for timepoint 3. In case no connection if found or if the user-defined displacement limit is reached, the track is considered as completed.
The process is then repeated for each individual detection within the timepoint-lists.

![Tracking principle](https://github.com/fabricecordelieres/IJ-Plugin_DTC/blob/master/images/Tracking_principle.png)

### Co-localization
Based on user-defined distances, two types of interactions can be monitored: proximity and co-localization. Two reference distances have to be defined: d<sub>coloc</sub> and d<sub>prox</sub>. Two detections (one from each channel) are considered to be in proximity when their distance is in the following range: ]d<sub>coloc</sub>; d<sub>prox</sub>]. Two detections (one from each channel) are considered to be co-localizing when their distance is equal to or below d<sub>coloc</sub>. All detections belonging to tracks will be investigated for both proximity and colocalization, in turn. If found, a tag will be added to the track, for the current timepoint. The tag consist of both a flag (Prox or Coloc) and a distance to the object triggering the tag. In case multiple objects are found,the Coloc tag is privileged over the Prox tag, and the the distances added to the tag is  aloways the shortest distance in case multiple Prox/Coloc pairs have been found for a single object.

![Co-localization principle](https://github.com/fabricecordelieres/IJ-Plugin_DTC/blob/master/images/Co-localization_principle.png)

## How to install the plugin ?
1. Navigate to the [Release section](https://github.com/fabricecordelieres/IJ-Plugin_DTC/releases)
2. Identify the latest release.
3. Unfold the "Assets" section and look for file DTC_.jar.
4. Click on it: the file should start being downloaded.
5. With ImageJ opened, drag-and-drop the file to the toolbar as you would do to open an image.
6. As you'll be presented with a Save Dialog box, simply hit Ok: the file will be saved under ImageJ/Plugins folder.
7. Restart ImageJ: the plugin should now be displayed under Plugins/Detect, Track, Co-localize

## How to use the plugin ?
### Setting the parameters
1. First, an image should be opened before the plugin is launched. This plugin works on a ROI: in case a ROI is present on the image, the analysis will only take place in this area.
2. Go to **plugins/Detect, Track, Colocalize**. The GUI (Graphical User interface) should pop-up:

![Main_GUI](https://github.com/fabricecordelieres/IJ-Plugin_DTC/blob/master/images/GUI_Red.png)

4. You may want to activate the _Preview_ checkbox at the bottom of the window: it will help visualizing the result when tweaking the parameters.
5. Navigate to the **Channel 1** tab: several options are available that should be adjusted depending on the structures of interest:
    1. **_Detection parameters, filter size:_** the radius of the filters to be applied, in pixels (see the [Detection section](#detection))
    2. **_Detection parameters, detection tolerance:_** the tolerance of intensity to consider a pixel as a local maximum (see the [Detection section](#detection))
    3. **_Detection parameters, re-tune detection:_** as the local maxima detection is used, the objects are approximated by the position of this individual pixel. Its coordinates are then integer values. In order to tune a bit the location, when this box is ticked, a circle will be placed around this detected point (radius equal to the filtering radius), its centre of mass will be extracted and used as the detected point. 
    4. **_Tracking parameters, maximum displacement:_** the tracks are build by looking over time the closest point between time t and t+1 (see the [Tracking section](#tracking)). This could lead to artefactual long distance travelling when sparse objects are considered: this parameters allows retricting the search/endind a track if the coupling is occuring on a too large distance.
    5. **_Tracking parameters, minimum number of frames:_** this parameters allows filtering out tracks for which the number of frames would be lower than this threshold value.
6. Repeat the operation for the **Channel 2** tab.
7. Navigate to the **Colocalization** tab: this tab allows setting two parameters
![Coloc_tab](https://github.com/fabricecordelieres/IJ-Plugin_DTC/blob/master/images/GUI_Coloc.png)
    1. **_Prox. max distance:_** this is the reference distance in pixels to be used to define that two objects, from two channels and same timepoint, are in proximity one from the other (see the [Co-localization section](#co-localization))
    2. **_Coloc. max distance:_** this is the reference distance in pixels to be used to define that two objects, from two channels and same timepoint, are colocalized (see the [Co-localization section](#co-localization))


### Waiting for the analysis to complete
While waiting for the analysis to complete, you may not be able to insteract with the image/interface. The processing has been implemented to take benefit of your full CPU capability (multi-threading) and will hopefully not take too long.


### Using the output options
1. Once the process is over, the Output GUI should pop-up:

![Output GUI](https://github.com/fabricecordelieres/IJ-Plugin_DTC/blob/master/images/GUI_Output.png)

2. The interface allows setting 4 different parameters related to display and output:
    1.**_Show tracks:_** This option will push to the ROI manager the selected tracks. Each track is displayed with a color code corresponding to the channel on which the object has been tracked (Red for channel 1, Green for channel 2). Depending on the  option selected (only one can be ticked at a time); the following tracks will be displayed: none, non proximal/non coloc., if has >=1 non prox. tag, if has >=1 coloc. tag,  if all timepoints are tagged as prox. (prox. ONLY), if all timepoints are tagged as coloc. (coloc. ONLY), all.
       
    2.**_Show detections:_** This option will push to the ROI manager the detections selected by ticking on or more boxes (non proximal/non coloc., proximal, coloc.). The ROIs are associated to their timepoint, meaning they will appear/disappear as the user is browsing through the temporal stack. Each ROI is color coded, depending on its associated tag: single detections (non proximal/non coloc.) will appear as yellow circle, proximal detections will appear as magenta crosses, coloc. detections will appear as cyan crosses.
       
    3.**_Individual detection options:_** All those options are applicable to the display generated when from interactions with a results table (see below).
    - *Zoom in:* When ticked, as a row gets selected in the table, the relevent detection or track will be centered on the image and zoomed in. Note that in cas the detection is close to the side of an image, the detection or track might be displayed off-centered.
    - *Zoom (%):* Allows setting the zoom to be used for the zoom in option.
    - *Line width (1=0.1 pix.):* Line width used to display tracks, expressed in tenth of pixels.
    - *Radius (pix.):* Radius used to display detections as circular ROIs, expressed in pixels.
        
    5.**_Exports:_** Exports takes the form of results table all (except stats containing the following informations): track number, timepoint number, channel number, X and Y coordinates, Average intensity (collected in a circle which dimension is the same as the filter radius used for the channel), distance travelled from the previous timepoint, status (prox/coloc or none), distance to other: in case of prox. or coloc. shortest distance to a detection in the other channel. Filtered data could be exported, based on event types (non proximal/non coloc., proximal, coloc., no filter). **All tables are interactive:** when clicking on a row, the detection will be displayed on the image (the image being eventually zoomed in depending on the options ticked n the GUI). As the up and down keys are already linked by ImageJ with shifting the table up and down, two shortcuts have been created, **u/d to switch from one line to another, therefore allowing navigating the table**. The *Stats* table summarizes few information about the tracks: number of timepoints, number of timepoints without tag, number of timepoints where proximity occurs, number of timepoints where colocalization occurs, distance travelled when no having tag, distance travelled when proximity occurs, distance travelled when colocalization occurs. The last three columns concern sequences: a sequence is a unique or group of timepoints where the tag is continuously identical. The number of sequences carrying no tag/prox or coloc tag are logged. Clicking on one row of this table will automaticcaly display the relevent track on the image.
