package com.example.kathrin1.vokabeltrainer_newlayout;

import android.app.Application;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

/**
 * Serves as the root application object for the entire app.  This has been added in order to
 * initialize a crash reporting library for the app.
 */

@ReportsCrashes(
        formKey = "",
        formUri = "https://cgdilley.cloudant.com/acra-vokabeln/_design/acra-storage/_update/report",
        reportType = org.acra.sender.HttpSender.Type.JSON,
        httpMethod = org.acra.sender.HttpSender.Method.PUT,
        formUriBasicAuthLogin="outityfifichichislausumb",
        formUriBasicAuthPassword="51fce9fa4e1a2ab02fdb4d4de25d69cb18743357")
public class MainApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        ACRA.init(this);
    }
}
