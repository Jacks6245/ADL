package com.example.jackskitt.adlarcherydatalogger.Processing.Matchers;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.example.jackskitt.adlarcherydatalogger.Collection.Sample;
import com.example.jackskitt.adlarcherydatalogger.Collection.Sequence;
import com.example.jackskitt.adlarcherydatalogger.Processing.SimilarityTesters.SimilarityTester;
import com.example.jackskitt.adlarcherydatalogger.Processing.TemplateStore;
import com.example.jackskitt.adlarcherydatalogger.UI.MainActivity;

/**
 * Created by Jack Skitt on 22/04/2017.
 */

public abstract class EventSearch {

    public int lengthOfTemplate = 0;
    public int sensorLookupId   = 0;

    public SimilarityTester similarityTester;
    public Sequence         testingSequence;
    public    String  name           = "";
    protected int     highestRIndex  = 0;
    protected double  highestR       = 0;
    protected float   highThreshold  = 0;
    protected boolean startCountdown = false;
    protected float   lowThreadhold  = 0;
    private TemplateType type;

    public EventSearch(String name) {
        this.name = name;
    }


    public void resetForEvent() {
        startCountdown = false;
        highestRIndex = 0;
        highestR = lowThreadhold;
    }

    public void setEvent(int start, int end, double confidence) {
        Log.i("EVENT_FOUND", type.toString() + " : " + start + " : " + end + " : " + confidence);
        try {
            Uri      notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r            = RingtoneManager.getRingtone(MainActivity.getInstance().getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (type) {
            case BOW_DRAW:
                testingSequence.setBowDrawFound(start, end, confidence);

                break;
            case BOW_SHOT:
                testingSequence.setBowShotFound(start, end, confidence);
                TemplateStore.instance.bowShotindex = end;
                break;
            case GLOVE_RELEASE:
                testingSequence.setGloveReleaseFound(start, end, confidence);
                break;
        }
    }


    public void resetTemplate() {
        similarityTester.reset();
        resetForEvent();
    }

    //gets the most recent data for the patternMatcher match
    public double getRemovedValue(Sample value) {
        if (getType() == PatternMatcher.TemplateType.BOW_DRAW || getType() == PatternMatcher.TemplateType.BOW_SHOT) {
            return value.acce.x;
        }
        return value.acce.x;
    }

    public PatternMatcher.TemplateType getType() {
        return type;
    }

    public void setType(PatternMatcher.TemplateType type) {
        if (type == PatternMatcher.TemplateType.BOW_DRAW || type == PatternMatcher.TemplateType.BOW_SHOT) {
            sensorLookupId = 0;
        } else {
            sensorLookupId = 1;
        }
        this.type = type;
    }

    public abstract void searchForEvent(double removedValue);

    public enum TemplateType {
        BOW_SHOT,
        BOW_DRAW,
        GLOVE_RELEASE,
        OTHER
    }


}
