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

