package com.sadashivsinha.scanandgo.Application;


import android.app.Application;

import com.sadashivsinha.scanandgo.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by sadashivsinha on 20/07/18.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/fontRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
