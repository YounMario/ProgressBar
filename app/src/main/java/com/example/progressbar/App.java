package com.example.progressbar;

import android.app.Application;

/**
 * Created by 龙泉 on 2016/12/19.
 */

public class App extends Application{


    private static App instance;

    public static App getInstance() {
        return instance;
    }

    public App(){
        instance = this;
    }

}
