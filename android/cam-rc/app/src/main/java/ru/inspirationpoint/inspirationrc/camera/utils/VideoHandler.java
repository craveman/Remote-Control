package ru.inspirationpoint.inspirationrc.camera.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.BasicContainer;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.inspirationpoint.inspirationrc.camera.MainCameraActivity;

public class VideoHandler {

    private static final int VIDEO_TYPE_CUTTED = 1;
    private static final int VIDEO_TYPE_MERGED = 2;

    private Context context;
    private List<String> videoSegmentsPaths = new ArrayList<>();
    private List<String> videoSegmentsMergedPaths = new ArrayList<>();
    private List<String> videoCuttedPaths = new ArrayList<>();
    private String videoMergedPathNew;
    private String videoMergedPathOld;
    private VideoListener listener;
    private boolean isMerging = false;
    private boolean isCutting = false;
    private boolean isNewAviilable = false;
    private boolean isCutNeeded = false;
    private int partNum = 0;
    private boolean isFirstMerge = true;
    private String cuttedFileName;
    public boolean isNeedToUpload = false;

    public void setNeedToUpload(boolean needToUpload) {
        this.isNeedToUpload = needToUpload;
    }

    public VideoHandler(Context context) {
        this.context = context;
    }

    public void setListener (VideoListener l) {
        listener = l;
    }

    public void onSegmentCommand(String path, String filename) {
        isNewAviilable = true;
        cuttedFileName = filename;
        videoSegmentsPaths.add(path);
        cutVideoEnd();
    }

    private void mergeVideos() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                    try {
                        isMerging = true;
                        Log.wtf("MERGE START", "+");
                        Movie finalMovie = new Movie();
                        Track[] tracks = new Track[isFirstMerge ? videoCuttedPaths.size() :
                                videoCuttedPaths.size() + 1];
                        if (!isFirstMerge) {
                            videoMergedPathOld = String.copyValueOf(videoMergedPathNew.toCharArray());
                            tracks[0] = MovieCreator.build(videoMergedPathNew).getTracks().get(0);
                        }
                        for (int i = 0; i < videoCuttedPaths.size(); i++) {
                            Movie movie = MovieCreator.build(videoCuttedPaths.get(i));
                            tracks[isFirstMerge ? i : i+1] = movie.getTracks().get(0);
                            videoSegmentsMergedPaths.add(videoCuttedPaths.get(i));
                        }
                        videoMergedPathNew = getVideoFilePath(VIDEO_TYPE_MERGED);
                        finalMovie.addTrack(new AppendTrack(tracks));
                        FileOutputStream fos = new FileOutputStream(videoMergedPathNew);
                        BasicContainer container = (BasicContainer) new DefaultMp4Builder().build(finalMovie);
                        container.writeContainer(fos.getChannel());
                        Log.wtf("MERGE SUCCESS", videoMergedPathNew);
                        listener.onVideoMerged();
                        isMerging = false;
                        isNewAviilable = false;
                        if (!isFirstMerge) {
                            File temp = new File(videoMergedPathOld);
                            boolean deleted = temp.delete();
                        }
                        for (String s : videoSegmentsMergedPaths) {
                            File file = new File(s);
                            boolean deleted2 = file.delete();
                            videoCuttedPaths.remove(s);
                        }
                        videoSegmentsMergedPaths.clear();
                        if (isCutNeeded) {
                            cutVideoEnd();
                        }
                        isFirstMerge = false;
                    } catch (IOException e) {
                        Log.wtf("MERGE FAILED", e);
                    }
            }
        }).start();
    }

    private void cutVideoEnd() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!isMerging && !isCutting) {
                    try {
                        isCutting = true;
                        Movie cuttedMovie = new Movie();
                        String cuttedPath = videoSegmentsPaths.get(videoSegmentsPaths.size() - 1);
                        Movie movie = MovieCreator.build(cuttedPath);
                        videoCuttedPaths.add(cuttedPath);
                        Track track = movie.getTracks().get(0);
                        IsoFile isoFile = new IsoFile(cuttedPath);
                        double lengthInSeconds = (double)
                                isoFile.getMovieBox().getMovieHeaderBox().getDuration() /
                                isoFile.getMovieBox().getMovieHeaderBox().getTimescale();
                        double startTime1 = lengthInSeconds - 10;
                        double endTime1 = lengthInSeconds;
                        Log.wtf("Before correct", startTime1 + "|" + endTime1);
                        startTime1 = correctTimeToSyncSample(track, startTime1, false);
                        endTime1 = correctTimeToSyncSample(track, endTime1, true);
                        Log.wtf("After correct", startTime1 + "|" + endTime1);
                        long currentSample = 0;
                        double currentTime = 0;
                        double lastTime = -1;
                        long startSample1 = -1;
                        long endSample1 = -1;

                        for (int i = 0; i < track.getSampleDurations().length; i++) {
                            long delta = track.getSampleDurations()[i];


                            if (currentTime > lastTime && currentTime <= startTime1) {
                                startSample1 = currentSample;
                            }
                            if (currentTime > lastTime && currentTime <= endTime1) {
                                endSample1 = currentSample;
                            }
                            lastTime = currentTime;
                            currentTime += (double) delta / (double) track.getTrackMetaData().getTimescale();
                            currentSample++;
                        }
                        cuttedMovie.addTrack(new AppendTrack(new CroppedTrack(track, startSample1, endSample1)));
                        long start1 = System.currentTimeMillis();
                        Container out = new DefaultMp4Builder().build(cuttedMovie);
                        long start2 = System.currentTimeMillis();
                        partNum++;
                        String cutPath = getVideoFilePath(VIDEO_TYPE_CUTTED);
                        FileOutputStream fos = new FileOutputStream(cutPath);
                        FileChannel fc = fos.getChannel();
                        out.writeContainer(fc);

                        fc.close();
                        fos.close();
                        long start3 = System.currentTimeMillis();
                        Log.wtf("Building IsoFile took : ", (start2 - start1) + "ms");
                        Log.wtf("Writing IsoFile took  : ", (start3 - start2) + "ms");
                        listener.onVideoCutted(cutPath);
                        isCutting = false;
                        if (isNeedToUpload) {
                            Log.wtf("IS NEED TO UPL", "+");
                            ((MainCameraActivity) context).uploadToCloud();
                            setNeedToUpload(false);
                        }
//                        if (partNum >= 2) {
//                            mergeVideos();
//                        }
                        File file = new File(cuttedPath);
                        if (file.getAbsoluteFile().delete()){
                            Log.wtf("FILE DELETED", "+");
                        } else {
                            Log.wtf("FILE DELETED", "-----");
                        };
                    } catch (IOException e) {
                        Log.wtf("CUT FAILED", e);
                    }
                } else {
                    isCutNeeded = true;
                }
            }
        }).start();
    }

    private static double correctTimeToSyncSample(Track track, double cutHere, boolean next) {
        double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
        long currentSample = 0;
        double currentTime = 0;
        for (int i = 0; i < track.getSampleDurations().length; i++) {
            long delta = track.getSampleDurations()[i];

            if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {
                // samples always start with 1 but we start with zero therefore +1
                timeOfSyncSamples[Arrays.binarySearch(track.getSyncSamples(), currentSample + 1)] = currentTime;
            }
            currentTime += (double) delta / (double) track.getTrackMetaData().getTimescale();
            currentSample++;

        }
        double previous = 0;
        for (double timeOfSyncSample : timeOfSyncSamples) {
            if (timeOfSyncSample > cutHere) {
                if (next) {
                    return timeOfSyncSample;
                } else {
                    return previous;
                }
            }
            previous = timeOfSyncSample;
        }
        return timeOfSyncSamples[timeOfSyncSamples.length - 1];
    }

    private String getVideoFilePath(int type) {
        final File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        return (dir == null ? "" : (dir.getAbsolutePath() + "/"))
                + (type==VIDEO_TYPE_CUTTED ? cuttedFileName : "merged") + (type==VIDEO_TYPE_MERGED ? (partNum - 1 + "_" + partNum) : "") + ".mp4";
    }

    public interface VideoListener {
        void onVideoCutted(String filename);
        void onVideoMerged();
    }
}
