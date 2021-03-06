package com.example.jackskitt.adlarcherydatalogger.Processing;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import com.example.jackskitt.adlarcherydatalogger.Collection.Event;
import com.example.jackskitt.adlarcherydatalogger.Collection.Sample;
import com.example.jackskitt.adlarcherydatalogger.Collection.Sequence;
import com.example.jackskitt.adlarcherydatalogger.Processing.Matchers.DifferenceMatcher;
import com.example.jackskitt.adlarcherydatalogger.Processing.Matchers.EventSearch;
import com.example.jackskitt.adlarcherydatalogger.Processing.Matchers.PatternMatcher;
import com.example.jackskitt.adlarcherydatalogger.R;
import com.example.jackskitt.adlarcherydatalogger.UI.MainActivity;

import java.io.InputStream;

/**
 * Created by Jack Skitt on 18/04/2017.
 */

public class FeatureSelectorStore {
    public static FeatureSelectorStore instance;
    public static int releaseRecordTime = 300;//amount to record after the bow is shot
    public static int backsearchTime    = 150;//this is the  aiming time. needs to be tweaked per user
    public        int recordStartIndex  = 0;
    public EventSearch[] eventSearchers;
    public int bowShotindex = 0;
    private InputStream drawTemplate;
    private int releaseCount     = 0;
    private int postRemovalDelay = 0;
    private int trueNegatives    = 0;

    public FeatureSelectorStore() {
        if (MainActivity.getInstance() != null) {
            drawTemplate = MainActivity.getInstance().getApplicationContext().getResources().openRawResource(R.raw.bowdrawtemplate);
        }
        //allows for a globably accessed variable
        instance = this;
        eventSearchers = new EventSearch[3];
        //load the blank templates  into the array
        populateTemplate();

    }

    public void resetTemplateEvent(int i) {
        eventSearchers[i].resetForEvent();
    }

    //This is the main method that checks for an event when a sample is  added, it first looks to find
    //a bow shot, once this is found it loops back a set amount (backsearchTime), once it's found it breaks the loop
    //if it's not found within the loopback then reset the bow shot flag to research for the shot event
    //if both of them are found them it resets the flags for the event and adds a split event to the current sequence
    public void checkTemplates(Sample toAdd, Sequence seq) {

        if (!seq.isBowShotFound()) {
            if (postRemovalDelay <= 0) {
                eventSearchers[0].testingSequence = seq;
                eventSearchers[0].searchForEvent(eventSearchers[0].getRemovedValue(toAdd));
            } else {
                postRemovalDelay--;
            }
        } else if (!seq.isBowDrawFound()) {
            eventSearchers[1].similarityTester.searchStart = eventSearchers[0].similarityTester.start - (backsearchTime + eventSearchers[1].lengthOfTemplate);
            eventSearchers[1].testingSequence = seq;

            for (int i = 0; i < backsearchTime + (eventSearchers[1].lengthOfTemplate); i++) {
                if (seq.isBowDrawFound()) {
                    break;
                }
                double startBackSearch = eventSearchers[1].getRemovedValue(seq.sequenceData[0].getSamples().get(i + eventSearchers[1].similarityTester.searchStart));
                eventSearchers[1].searchForEvent(startBackSearch);
            }
            if (!seq.isBowDrawFound()) {
                seq.removeShotFlag();
                resetAfterFalseResult();
            }
        }

        if (seq.isBowDrawFound() && seq.isBowShotFound()) {
            releaseCount++;

            if (releaseCount >= releaseRecordTime || (bowShotindex + releaseCount) >= seq.sequenceData[0].getSamples().size()) {
                seq.resetEventFlags();
                releaseCount = 0;

                setSplitEvent(seq.drawEvent.get(seq.drawEvent.size() - 1).startTime,
                        seq.shotEvent.get(seq.shotEvent.size() - 1).endTime + releaseRecordTime,
                        seq.drawEvent.get(seq.drawEvent.size() - 1).probability, seq);
                Uri      notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r            = RingtoneManager.getRingtone(MainActivity.getInstance().getApplicationContext(), notification);
                r.play();
            }
        }
    }

    //if a shot is detected but a draw event  is not it resets the searcher
    private void resetAfterFalseResult() {
        trueNegatives++;
        postRemovalDelay = 50;
        int storedIndex = eventSearchers[0].similarityTester.start;
        eventSearchers[0].resetTemplate();
        eventSearchers[0].similarityTester.start = storedIndex + 50;
        eventSearchers[1].resetTemplate();
    }

    //adds a split event to the sequence and resets the searcher at the same time;
    public void setSplitEvent(int start, int end, double confidence, Sequence seq) {
        eventSearchers[0].resetTemplate();
        eventSearchers[0].similarityTester.start = end + (trueNegatives * 50);
        eventSearchers[1].resetTemplate();

        seq.splitEvent.add(new Event(start,
                end,
                confidence));
        seq.resetEventFlags();
    }

    public void resetTemplate(int i) {
        eventSearchers[i].resetTemplate();
    }

    public void resetForNewSequence() {
        eventSearchers[0].resetTemplate();
        eventSearchers[1].resetTemplate();
    }

    //adds new templates to the store
    private void populateTemplate() {
        for (int i = 0; i < 2; i++) {
            switch (i) {
                case 0:
                    //bow shot as first template
                    eventSearchers[i] = new DifferenceMatcher(1f, 1.5f, EventSearch.TemplateType.BOW_SHOT);
                    break;
                case 1:
                    eventSearchers[i] = new PatternMatcher(drawTemplate);
                    break;
            }
        }
    }

    public EventSearch getSearcher(int i) {
        if (i < eventSearchers.length) {
            return eventSearchers[i];
        }
        return null;
    }

}
