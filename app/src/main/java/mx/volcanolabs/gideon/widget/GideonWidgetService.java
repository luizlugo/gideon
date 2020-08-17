package mx.volcanolabs.gideon.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class GideonWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }
}
