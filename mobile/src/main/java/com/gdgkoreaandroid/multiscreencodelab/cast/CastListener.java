package com.gdgkoreaandroid.multiscreencodelab.cast;

import android.os.Bundle;
import android.support.v7.media.MediaRouter;

import com.google.android.gms.common.ConnectionResult;

public interface CastListener {

    public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo route);
    public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo route);

    public void onConnected(Bundle bundle);
    public void onConnectionSuspended(int cause);
    public void onConnectionFailed(ConnectionResult connectionResult);

    public void onApplicationLaunched(boolean wasLaunched);
    public void onApplicationStatusChanged();
    public void onVolumeChanged();
    public void onApplicationDisconnected(int statusCode);
}
