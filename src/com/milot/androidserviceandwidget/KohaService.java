package com.milot.androidserviceandwidget;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class KohaService extends Service {
	String value = "s'ka info";
	  
	  @Override
	  public void onStart(Intent intent, int startId) {
	
		/* START -- Pjesa e Widget -- */  
	    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
		String url = "http://api.openweathermap.org/data/2.5/weather?q=Pristina,Kosovo&units=metric";
		
		JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				
				try {
					String temperatura = response.getJSONObject("main").getString("temp").toString();
					String qyteti = response.getString("name").toString();
					String gjendja = response.getJSONArray("weather").getJSONObject(0).getString("main");
					value = "Temperatura " + temperatura + "C"+ "\nQyteti: " + qyteti + "\nGjendja: " + gjendja;
					
					SharedPreferences settings = getSharedPreferences("koha", 0);
				    SharedPreferences.Editor editor = settings.edit();
				    editor.putString("kohaValue", value);
				    editor.commit();
				    
				    
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});

		queue.add(jsObjRequest);
	    
		
	    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this
	        .getApplicationContext());
	
	    int[] allWidgetIds = intent
	        .getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
	
	    SharedPreferences settings = getSharedPreferences("koha", 0);
	    String koha = settings.getString("kohaValue", "s'ka hala");
	    
	    for (int widgetId : allWidgetIds) {
	      final RemoteViews remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.widget_layout);
			
			remoteViews.setTextViewText(R.id.update, koha);
			
			Intent clickIntent = new Intent(this.getApplicationContext(),
	    		  ShembullWidgetProvider.class);
	
			clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
	
	      PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, clickIntent,
	          PendingIntent.FLAG_UPDATE_CURRENT);
	      
	      remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent);
	      appWidgetManager.updateAppWidget(widgetId, remoteViews);
	    }
	    
		/* END -- Pjesa e Widget -- */  
	    
	    
	    NotificationCompat.Builder mBuilder =
	            new NotificationCompat.Builder(this)
	            .setSmallIcon(R.drawable.sunny)
	            .setContentTitle("Koha!")
	            .setContentText(koha);
	    
	    Intent resultIntent = new Intent(this, MainActivity.class);
	    resultIntent.putExtra("kohaValue", koha);
	    
	    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
	    stackBuilder.addParentStack(MainActivity.class);
	    stackBuilder.addNextIntent(resultIntent);
	    PendingIntent resultPendingIntent =
	            stackBuilder.getPendingIntent(
	                0,
	                PendingIntent.FLAG_UPDATE_CURRENT
	            );
	    mBuilder.setContentIntent(resultPendingIntent);
	    
	    NotificationManager mNotificationManager =
	        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	    mNotificationManager.notify(0, mBuilder.build());
	    
	    stopSelf();
	  }
	
	  @Override
	  public IBinder onBind(Intent intent) {
	    return null;
	  }
}
