package com.milot.androidserviceandwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class ShembullWidgetProvider extends AppWidgetProvider {

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager,
      int[] appWidgetIds) {

    ComponentName thisWidget = new ComponentName(context,
    		ShembullWidgetProvider.class);
    
    int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

    Intent intent = new Intent(context.getApplicationContext(),
        KohaService.class);
    
    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

    context.startService(intent);
  }
}
