// This file was generated by PermissionsDispatcher. Do not modify!
package com.hc_06.yj.bluetoothapplication.ui.activities;

import android.support.v4.app.ActivityCompat;
import java.lang.String;
import permissions.dispatcher.PermissionUtils;

final class MapActivityPermissionsDispatcher {
  private static final int REQUEST_INITMAP = 0;

  private static final String[] PERMISSION_INITMAP = new String[] {"android.permission.ACCESS_COARSE_LOCATION","android.permission.ACCESS_FINE_LOCATION","android.permission.WRITE_EXTERNAL_STORAGE"};

  private MapActivityPermissionsDispatcher() {
  }

  static void initMapWithCheck(MapActivity target) {
    if (PermissionUtils.hasSelfPermissions(target, PERMISSION_INITMAP)) {
      target.initMap();
    } else {
      ActivityCompat.requestPermissions(target, PERMISSION_INITMAP, REQUEST_INITMAP);
    }
  }

  static void onRequestPermissionsResult(MapActivity target, int requestCode, int[] grantResults) {
    switch (requestCode) {
      case REQUEST_INITMAP:
      if (PermissionUtils.getTargetSdkVersion(target) < 23 && !PermissionUtils.hasSelfPermissions(target, PERMISSION_INITMAP)) {
        return;
      }
      if (PermissionUtils.verifyPermissions(grantResults)) {
        target.initMap();
      }
      break;
      default:
      break;
    }
  }
}
