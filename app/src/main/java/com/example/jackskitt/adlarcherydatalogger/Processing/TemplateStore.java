package com.example.jackskitt.adlarcherydatalogger.Processing;

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

public class TemplateStore {
    public static TemplateStore instance;
    public static int releaseRecordTime = 200;//amount to record after the bow is shot
    public static int backsearchTime    = 300;//this is the  aiming time. needs to be tweaked per user
    public        int recordStartIndex  = 0;
    public EventSearch[] patternMatchers;
    public  int         bowShotindex = 0;
    private InputStream drawTemplate = MainActivity.getInstance().getApplicationContext().getResources().openRawResource(R.raw.bowdrawtemplate);
    private int         releaseCount = 0;

    public TemplateStore() {
        instance = this;
        patternMatchers = new EventSearch[3];
        populateTemplate();

    }

    public void resetTemplateEvent(int i) {
        patternMatchers[i].resetForEvent();
    }

    public void checkTemplates(Sample toAdd, Sequence seq) {

        if (!seq.isBowShotFound()) {
            patternMatchers[0].testingSequence = seq;
            patternMatchers[0].searchForEvent(patternMatchers[0].getRemovedValue(toAdd));
        } else if (!seq.isBowDrawFound()) {
            patternMatchers[1].similarityTester.searchStart = patternMatchers[0].similarityTester.start - (backsearchTime + patternMatchers[1].lengthOfTemplate);
            patternMatchers[1].testingSequence = seq;

            for (int i = 0; i < backsearchTime + (patternMatchers[1].lengthOfTemplate); i++) {
                if (seq.isBowDrawFound()) {
                    break;
                }
                double startBackSearch = patternMatchers[1].getRemovedValue(seq.sequenceData[0].getSamples().get(i + patternMatchers[1].similarityTester.searchStart));
                patternMatchers[1].searchForEvent(startBackSearch);
            }
            if (!seq.isBowDrawFound()) {
                seq.removeShotFlag();
            }
        }

        if (seq.isBowDrawFound() && seq.isBowShotFound()) {
            releaseCount++;

            if (releaseCount >= releaseRecordTime || (bowShotindex + releaseCount) >= seq.sequenceData[0].sizeOfDataset) {
                seq.resetEventFlags();
                releaseCount = 0;

                setSplitEvent(seq.drawEvent.get(seq.drawEvent.size() - 1).startTime,
                        seq.shotEvent.get(seq.shotEvent.size() - 1).endTime + releaseRecordTime,
                        seq.drawEvent.get(seq.drawEvent.size() - 1).probability, seq);
            }
        }
    }

    public void setSplitEvent(int start, int end, double confidence, Sequence seq) {
        patternMatchers[0].resetTemplate();
        patternMatchers[0].similarityTester.start = seq.sequenceData[0].getSamples().size();
        patternMatchers[1].resetTemplate();

        seq.splitEvent.add(new Event(start,
                end,
                confidence));
        seq.resetEventFlags();
    }

    public void resetTemplate(int i) {
        patternMatchers[i].resetTemplate();
    }

    public void resetForNewSequence() {
        patternMatchers[0].resetTemplate();
        patternMatchers[1].resetTemplate();
    }

    private void populateTemplate() {
        for (int i = 0; i < 3; i++) {
            switch (i) {
                case 0:
                    //bow shot as first template
                    patternMatchers[i] = new DifferenceMatcher(1f, 1.5f, EventSearch.TemplateType.BOW_SHOT);
                    break;
                case 1:
                    patternMatchers[i] = new PatternMatcher(drawTemplate);
                    break;
            }
        }
    }

    public EventSearch getSearcher(int i) {
        if (i < patternMatchers.length) {
            return patternMatchers[i];
        }
        return null;
    }

}
