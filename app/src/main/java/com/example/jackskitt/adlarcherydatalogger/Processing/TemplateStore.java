package com.example.jackskitt.adlarcherydatalogger.Processing;

import com.example.jackskitt.adlarcherydatalogger.Collection.Sample;
import com.example.jackskitt.adlarcherydatalogger.Collection.Sequence;
import com.example.jackskitt.adlarcherydatalogger.Profiles.Profile;
import com.example.jackskitt.adlarcherydatalogger.R;
import com.example.jackskitt.adlarcherydatalogger.UI.MainActivity;

import java.io.InputStream;

/**
 * Created by Jack Skitt on 18/04/2017.
 */

public class TemplateStore {
    public static TemplateStore instance;

    private InputStream drawTemplate = MainActivity.getInstance().getApplicationContext().getResources().openRawResource(R.raw.bowdrawtemplate);
    private InputStream   shotTemplate;
    private InputStream   releaseTemplate;
    private EventSearch[] patternMatchers;

    private int releaseRecordTime = 70;

    public TemplateStore() {
        instance = this;
        patternMatchers = new EventSearch[3];
        populateTemplate();

    }

    public void resetTemplateEvent(int i) {
        patternMatchers[i].resetForEvent();
    }

    public void checkTemplates(Sample toAdd, Sequence seq) {
        if (!seq.isBowDrawFound()) {
            patternMatchers[0].testingSequence = seq;
            patternMatchers[0].searchForEvent(patternMatchers[0].getRemovedValue(toAdd));
        } else {
            patternMatchers[1].testingSequence = seq;
            patternMatchers[1].searchForEvent(patternMatchers[1].getRemovedValue(toAdd));
        }

        if (seq.isBowShotFound()) {

            if (seq.shotEvent.endTime < seq.sequenceData[0].getSamples().size() - releaseRecordTime) {
                Sequence newSequence = seq.splitSequence(seq.drawEvent.get(seq.drawEvent.size()).startTime, seq.shotEvent.endTime + releaseRecordTime);
                Profile.instance.sequenceStore.allSequences.add(newSequence);
            }
        }
    }

    public void resetTemplate(int i) {
        patternMatchers[i].resetTemplate();
    }

    private void populateTemplate() {
        for (int i = 0; i < 3; i++) {
            switch (i) {
                case 0:
                    patternMatchers[i] = new PatternMatcher(drawTemplate);
                    break;
                case 1:
                    patternMatchers[i] = new VarienceMatcher(3, 0.9, 1);
            }
        }
    }

}
