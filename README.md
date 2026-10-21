[Video](https://www.youtube.com/watch?v=iENK5EDO2iU)

This tutorial shows you how to add a Google map to your Android app. The map
includes a marker, also called a pin, to indicate a specific location.

Follow the tutorial to build an Android app using the
Maps SDK for Android. The recommended development environment is
[Android Studio](https://developer.android.com/studio/).

## Get the code

![](https://developers.google.com/static/maps/documentation/android-sdk/images/map-with-marker.png)

[Clone or download the Google Maps Android API v2 Samples repository](https://github.com/googlemaps-samples/android-samples)
from GitHub.

View the Java version of the activity:
<button type="button" class="button-red button expand-control">Show / Hide Code</button>

```
    // Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.mapwithmarker;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/** An activity that displays a Google map with a marker (pin) to indicate a particular location. */
public class MapsMarkerActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map when it's available. The API invokes this callback when the map is ready
     * to be used. This is where we can add markers or lines, add listeners or move the camera. In
     * this case, we just add a marker near Sydney, Australia. If Google Play services is not
     * installed on the device, the user receives a prompt to install Play services inside the
     * SupportMapFragment. The API invokes this method after the user has installed Google Play
     * services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng sydney = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}

    
```

View the Kotlin version of the activity:
<button type="button" class="button-red button expand-control">Show / Hide Code</button>

```
    // Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.mapwithmarker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * An activity that displays a Google map with a marker (pin) to indicate a particular location.
 */
class MapsMarkerActivity :
    AppCompatActivity(),
    OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps)

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val sydney = LatLng(-33.852, 151.211)
        googleMap.addMarker(
            MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney"),
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}

    
```

## Set up your development project

Follow these steps to create the tutorial project in Android Studio.

1. [Download](https://developer.android.com/studio/index.html) and [install](https://developer.android.com/studio/install.html?pkg=studio) Android Studio.
2. [Add the **Google Play services** package](https://developer.android.com/studio/intro/update.html#sdk-manager) to Android Studio.
3. [Clone or download the Google Maps Android API v2 Samples repository](https://github.com/googlemaps-samples/android-samples) if you didn't do that when you started reading this tutorial.
4. Import the tutorial project:

   - In Android Studio, select **File \> New \> Import Project**.
   - Go to the location where you saved the Google Maps Android API v2 Samples repository after downloading it.
   - Find the **MapWithMarker** project at this location:  
     `PATH-TO-SAVED-REPO/android-samples/tutorials/java/MapWithMarker` (Java) or   
     `PATH-TO-SAVED-REPO/android-samples/tutorials/kotlin/MapWithMarker` (Kotlin)
   - Select the project directory, then click **Open**. Android Studio now builds your project, using the Gradle build tool.

## Enable the necessary APIs and get an API key

To complete this tutorial, you need a Google Cloud project with the necessary
APIs enabled and an API key that's authorized to use the Maps SDK for Android.
For more details, see:

- [Set up your Google Cloud project](https://developers.google.com/maps/documentation/android-sdk/cloud-setup)
- [Use an API key](https://developers.google.com/maps/documentation/android-sdk/get-api-key)

## Add the API key to your app

1. Open your project's `local.properties` file.
2. Add the following string and then replace `YOUR_API_KEY` with the value of
   your API key:

       MAPS_API_KEY=YOUR_API_KEY

   When you build your app, the
   [Secrets Gradle Plugin for Android](https://github.com/google/secrets-gradle-plugin)
   will copy the API key and make it available as a build variable in the
   Android manifest, as explained [below](https://developers.google.com/maps/documentation/android-sdk/map-with-marker#manifest).

## Build and run your app

To build and run the app:

1. Connect an Android device to your computer. Follow the
   [instructions](https://developer.android.com/studio/run/device.html) to enable developer options on your Android
   device and configure your system to detect the device.

   Alternatively, you can use the [Android Virtual Device (AVD) Manager](https://developer.android.com/studio/run/managing-avds.html)
   to configure a virtual device. When choosing an emulator, make sure you pick
   an image that includes the Google APIs. For more details, see
   [Set Up an Android Studio Project](https://developers.google.com/maps/documentation/android-sdk/config).
2. In Android Studio, click the **Run** menu option (or the play button icon).
   Choose a device as prompted.

Android Studio invokes Gradle to build the app, and then runs the app on
the device or on the emulator. You should see a map with a marker pointing at
Sydney on the east coast of Australia, similar to the image on this page.

Troubleshooting:

- If you don't see a map, check that you've obtained an API key and added it to the app, [as described above](https://developers.google.com/maps/documentation/android-sdk/map-with-marker#api-key). Check the log in Android Studio's [Android Monitor](https://developer.android.com/studio/debug/am-logcat.html) for error messages about the API key.
- Use the [Android Studio debugging tools](https://developer.android.com/studio/debug/am-logcat.html) to view logs and debug the app.

## Understand the code

This part of the tutorial explains the most significant parts of the
**MapWithMarker** app, to help you understand how to build a similar
app.

### Check your Android manifest

Note the following elements in your app's `AndroidManifest.xml` file:

- Add a `meta-data` element to embed the version of Google Play services that
  the app was compiled with.

      <meta-data
          android:name="com.google.android.gms.version"
          android:value="@integer/google_play_services_version" />

- Add a `meta-data` element specifying your API key. The sample accompanying
  this tutorial maps the value for the API key to a build variable matching
  the name of the key you defined earlier, `MAPS_API_KEY`.
  When you build your app, the [Secrets Gradle Plugin for Android](https://github.com/google/secrets-gradle-plugin)
  will make the keys in your `local.properties` file available as manifest build
  variables.

      <meta-data
        android:name="com.google.android.geo.API_KEY"
        android:value="${MAPS_API_KEY}" />

  In your `build.gradle` file, the following line passes your API key to your
  Android manifest.

        id 'com.google.android.libraries.mapsplatform.secrets-gradle-plugin'

Below is an example of a full manifest:

```
<?xml version="1.0" encoding="utf-8"?>
<!--
 Copyright 2020 Google LLC

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->

<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">

        <meta-data
            android:name="com.google.android.gms.version"
            android:value="@integer/google_play_services_version" />

        <!--
             The API key for Google Maps-based APIs.
        -->
        <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="${MAPS_API_KEY}" />

        <activity
            android:name=".MapsMarkerActivity"
            android:label="@string/title_activity_maps"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

### Add a map

Display a map, using the Maps SDK for Android.

1. Add a `<fragment>` element to your activity's layout file,
   `activity_maps.xml`. This element defines a
   [`SupportMapFragment`](https://developers.google.com/android/reference/com/google/android/gms/maps/SupportMapFragment) to act as a container for the map
   and to provide access to the `GoogleMap` object. The tutorial uses the
   Android support library version of the map fragment, to ensure backward
   compatibility with earlier versions of the Android framework.

   ```
   <!--
    Copyright 2020 Google LLC

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
   -->

   <fragment xmlns:android="http://schemas.android.com/apk/res/android"
       xmlns:tools="http://schemas.android.com/tools"
       android:id="@+id/map"
       android:name="com.google.android.gms.maps.SupportMapFragment"
       android:layout_width="match_parent"
       android:layout_height="match_parent"
       tools:context="com.example.mapwithmarker.MapsMarkerActivity" />
   ```
2. In your activity's `onCreate()` method, set the layout file as the content
   view. Get a handle to the map fragment by calling
   [`FragmentManager.findFragmentById()`](https://developer.android.com/reference/android/app/FragmentManager.html#findFragmentById(int)). Then use
   [`getMapAsync()`](https://developers.google.com/android/reference/com/google/android/gms/maps/MapFragment#getMapAsync(com.google.android.gms.maps.OnMapReadyCallback)) to register for the map callback:

   ### Java

   ```java
   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       // Retrieve the content view that renders the map.
       setContentView(R.layout.activity_maps);

       // Get the SupportMapFragment and request notification when the map is ready to be used.
       SupportMapFragment mapFragment =
               (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
       mapFragment.getMapAsync(this);
   }
   ```

   ### Kotlin

   ```kotlin
   override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
       // Retrieve the content view that renders the map.
       setContentView(R.layout.activity_maps)

       // Get the SupportMapFragment and request notification when the map is ready to be used.
       val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
       mapFragment?.getMapAsync(this)
   }
   ```
3. Implement the [`OnMapReadyCallback`](https://developers.google.com/android/reference/com/google/android/gms/maps/OnMapReadyCallback) interface and
   override the `onMapReady()` method, to set up the map when the `GoogleMap`
   object is available:

   ### Java

   ```java
   public class MapsMarkerActivity extends AppCompatActivity implements OnMapReadyCallback {

       // ...

       @Override
       public void onMapReady(GoogleMap googleMap) {
           LatLng sydney = new LatLng(-33.852, 151.211);
           googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
       }
   }
   ```

   ### Kotlin

   ```kotlin
   class MapsMarkerActivity :
       AppCompatActivity(),
       OnMapReadyCallback {

       // ...

       override fun onMapReady(googleMap: GoogleMap) {
           val sydney = LatLng(-33.852, 151.211)
           googleMap.addMarker(
               MarkerOptions()
                   .position(sydney)
                   .title("Marker in Sydney"),
           )
       }
   }
   ```

By default, the Maps SDK for Android displays the content of the
info window when the user taps a marker. There's no need to add a click listener
for the marker if you're happy to use the default behavior.

> [!NOTE]
> **Congratulations!** You've built an Android app that displays a Google map with a marker to indicate a particular location. You've also learned how to use the [Maps SDK for Android](https://developers.google.com/maps/documentation/android-sdk).

## Next steps

Learn more about the [map object](https://developers.google.com/maps/documentation/android-sdk/map) and what you can do with
[markers](https://developers.google.com/maps/documentation/android-sdk/marker).
