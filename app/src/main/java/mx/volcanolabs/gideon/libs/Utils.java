package mx.volcanolabs.gideon.libs;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;

import com.google.android.gms.location.Geofence;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import mx.volcanolabs.gideon.R;
import mx.volcanolabs.gideon.widget.GideonWidget;

import static mx.volcanolabs.gideon.Constants.due_date_time_format;

public class Utils {

    public static long calculateDurationBetweenDates(String dueDate) {
        try {
            dueDate += " 23:59:59";
            SimpleDateFormat dateFormat = new SimpleDateFormat(due_date_time_format, Locale.US);
            Date endDate = dateFormat.parse(dueDate);
            Date today = new Date();

            if (endDate != null) {
                return endDate.getTime() - today.getTime();
            } else {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    public static void updateGideonWidgets(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(
                new ComponentName(context, GideonWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_tasks);
    }
}
