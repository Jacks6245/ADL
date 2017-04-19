package com.example.jackskitt.adlarcherydatalogger.Processing;

import com.example.jackskitt.adlarcherydatalogger.Collection.Event;
import com.example.jackskitt.adlarcherydatalogger.Collection.Sample;
import com.example.jackskitt.adlarcherydatalogger.Profiles.Profile;
import com.example.jackskitt.adlarcherydatalogger.R;
import com.example.jackskitt.adlarcherydatalogger.UI.MainActivity;

import java.io.InputStream;

/**
 * Created by Jack Skitt on 18/04/2017.
 */

public class TemplateStore {
    public static TemplateStore instance;
    private boolean     bowDrawFound      = false;
    private boolean     bowShotFound      = false;
    private boolean     gloveReleaseFound = false;
    private InputStream drawTemplate      = MainActivity.getInstance().getApplicationContext().getResources().openRawResource(R.raw.bowdrawtemplate);
    private InputStream shotTemplate;
    private InputStream releaseTemplate;
    private Template[]  templates;

    public TemplateStore() {
        instance = this;
        templates = new Template[3];
        populateTemplate();

    }


    public void checkTemplate() {

    }

    public void checkTemplates(Sample toAdd) {
        if (!Profile.instance.profileCurrentSequence.isBowDrawFound()) {
            templates[0].getCorrelation(templates[0].getRemovedValue(toAdd));
        }
    }

    public void reset() {
        bowDrawFound = false;
        bowShotFound = false;
        gloveReleaseFound = false;
    }

    private void populateTemplate() {
        for (int i = 0; i < 3; i++) {
            switch (i) {
                case 0:
                    templates[i] = new Template(drawTemplate);
                    break;
            }
        }
    }

}
