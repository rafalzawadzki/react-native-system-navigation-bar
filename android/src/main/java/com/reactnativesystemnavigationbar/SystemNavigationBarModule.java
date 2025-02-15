package com.reactnativesystemnavigationbar;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.IllegalViewOperationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ReactModule(name = SystemNavigationBarModule.NAME)
public class SystemNavigationBarModule extends ReactContextBaseJavaModule {

  public static final String NAME = "NavigationBar";
  public static final Integer NO_MODE = -1;
  public static final Integer LIGHT = 0;
  public static final Integer DARK = 1;
  public static final Integer NAVIGATION_BAR = 2;
  public static final Integer STATUS_BAR = 3;
  public static final Integer NAVIGATION_BAR_STATUS_BAR = 4;

  public SystemNavigationBarModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();
    constants.put("NO_MODE", NO_MODE);
    constants.put("LIGHT", LIGHT);
    constants.put("DARK", DARK);
    constants.put("NAVIGATION_BAR", NAVIGATION_BAR);
    constants.put("STATUS_BAR", STATUS_BAR);
    constants.put("NAVIGATION_BAR_STATUS_BAR", NAVIGATION_BAR_STATUS_BAR);
    return constants;
  }

  /* Navigation Hide */
  @ReactMethod
  public void navigationHide(Promise promise) {
    setSystemUIFlags(
      View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
      View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
      View.SYSTEM_UI_FLAG_HIDE_NAVIGATION,
      promise
    );
  }

  /* Navigation Show */
  @ReactMethod
  public void navigationShow(Promise promise) {
    setSystemUIFlags(View.SYSTEM_UI_FLAG_VISIBLE, promise);
  }

  @ReactMethod
  public void fullScreen(Boolean enable, Promise promise) {
    if (enable) {
      setSystemUIFlags(
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
        View.SYSTEM_UI_FLAG_IMMERSIVE |
        View.SYSTEM_UI_FLAG_FULLSCREEN,
        promise
      );
    } else {
      setSystemUIFlags(
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN,
        promise
      );
    }
  }

  /* Lean Back */
  @ReactMethod
  public void leanBack(Promise promise) {
    setSystemUIFlags(
      View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION,
      promise
    );
  }

  /* Immersive */
  @ReactMethod
  public void immersive(Promise promise) {
    setSystemUIFlags(
      View.SYSTEM_UI_FLAG_IMMERSIVE |
      View.SYSTEM_UI_FLAG_FULLSCREEN |
      View.SYSTEM_UI_FLAG_HIDE_NAVIGATION,
      promise
    );
  }

  /* Sticky Immersive */
  @ReactMethod
  public void stickyImmersive(Promise promise) {
    setSystemUIFlags(
      View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
      View.SYSTEM_UI_FLAG_FULLSCREEN |
      View.SYSTEM_UI_FLAG_HIDE_NAVIGATION,
      promise
    );
  }

  /* Low Profile */
  @ReactMethod
  public void lowProfile(Promise promise) {
    setSystemUIFlags(View.SYSTEM_UI_FLAG_LOW_PROFILE, promise);
  }

  @ReactMethod
  public void setBarMode(Integer modeStyle, Integer bar, Promise promise) {
    boolean isLight = modeStyle.equals(LIGHT);
    setModeStyle(!isLight, bar, promise);
  }

  /* Set Navigation Color */
  @ReactMethod
  public void setNavigationColor(
    Integer color,
    Boolean isTranslucent,
    Integer modeStyle,
    Integer bar,
    Promise promise
  ) {
    try {
      int requiredVersion = Build.VERSION_CODES.LOLLIPOP;
      if (Build.VERSION.SDK_INT < requiredVersion) {
        promise.reject("Error: ", errorMessage(requiredVersion));
        return;
      }
      final Activity currentActivity = getCurrentActivity();
      if (currentActivity == null) {
        promise.reject("Error: ", "current activity is null");
        return;
      }
      final Window view = currentActivity.getWindow();

      runOnUiThread(
        () -> {
          view.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
          view.clearFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
          );

          if (color.equals(0)) {
            view.setFlags(
              WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
              WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
          } else if (isTranslucent) {
            view.setFlags(
              WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
              WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            );
          }

          view.setNavigationBarColor(color);

          if (!Objects.equals(modeStyle, NO_MODE)) {
            boolean isLight = modeStyle.equals(LIGHT);
            setModeStyle(!isLight, bar);
          }
        }
      );
      promise.resolve("true");
    } catch (IllegalViewOperationException e) {
      e.printStackTrace();
      promise.reject("Error: ", e.getMessage());
    }
  }

  /* Set NavigationBar Divider Color */
  @ReactMethod
  public void setNavigationBarDividerColor(Integer color, Promise promise) {
    try {
      int requiredVersion = Build.VERSION_CODES.P;
      if (Build.VERSION.SDK_INT < requiredVersion) {
        promise.reject("Error: ", errorMessage(requiredVersion));
        return;
      }
      final Activity currentActivity = getCurrentActivity();
      if (currentActivity == null) {
        promise.reject("Error: ", "current activity is null");
        return;
      }
      final Window view = currentActivity.getWindow();
      runOnUiThread(
        () -> {
          view.setNavigationBarDividerColor(color);
          view
            .getDecorView()
            .setSystemUiVisibility(
              WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS |
              WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            );
        }
      );
      promise.resolve("true");
    } catch (IllegalViewOperationException e) {
      e.printStackTrace();
      promise.reject("Error: ", e.getMessage());
    }
  }

  /* Set NavigationBar Contrast Enforced */
  @ReactMethod
  public void setNavigationBarContrastEnforced(
    Boolean enforceContrast,
    Promise promise
  ) {
    try {
      int requiredVersion = Build.VERSION_CODES.Q;
      if (Build.VERSION.SDK_INT < requiredVersion) {
        promise.reject("Error: ", errorMessage(requiredVersion));
        return;
      }
      final Activity currentActivity = getCurrentActivity();
      if (currentActivity == null) {
        promise.reject("Error: ", "current activity is null");
        return;
      }
      final Window view = currentActivity.getWindow();
      runOnUiThread(
        () -> view.setNavigationBarContrastEnforced(enforceContrast)
      );
      promise.resolve("true");
    } catch (IllegalViewOperationException e) {
      e.printStackTrace();
      promise.reject("Error: ", e.getMessage());
    }
  }

  /* Private Method */
  private void setSystemUIFlags(int visibility, Promise promise) {
    try {
      runOnUiThread(
        () -> {
          int requiredVersion = Build.VERSION_CODES.LOLLIPOP;
          if (Build.VERSION.SDK_INT < requiredVersion) {
            promise.reject("Error: ", errorMessage(requiredVersion));
            return;
          }
          Activity currentActivity = getCurrentActivity();
          if (currentActivity == null) {
            promise.reject("Error: ", "current activity is null");
            return;
          }
          View decorView = currentActivity.getWindow().getDecorView();

          decorView.setSystemUiVisibility(visibility);
        }
      );
      promise.resolve("true");
    } catch (IllegalViewOperationException e) {
      e.printStackTrace();
      promise.reject("Error: ", e.getMessage());
    }
  }

  private void setBarStyle(Boolean light, int visibility) {
    if (getCurrentActivity() == null) {
      throw new IllegalViewOperationException("current activity is null");
    }
    View decorView = getCurrentActivity().getWindow().getDecorView();
    int bit = decorView.getSystemUiVisibility();

    if (light) {
      bit |= visibility;
    } else {
      bit &= ~visibility;
    }

    decorView.setSystemUiVisibility(bit);
  }

  private void setModeStyle(Boolean light, Integer bar) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      if (getCurrentActivity() == null) {
        throw new IllegalViewOperationException("current activity is null");
      }

      if (bar.equals(NAVIGATION_BAR)) {
        setBarStyle(light, View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
      } else if (bar.equals(STATUS_BAR)) {
        setBarStyle(light, View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
      } else if (bar.equals(NAVIGATION_BAR_STATUS_BAR)) {
        setBarStyle(light, View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
      }
    }
  }

  private void setModeStyle(Boolean light, Integer bar, Promise promise) {
    try {
      runOnUiThread(
        () -> {
          setModeStyle(light, bar);
          promise.resolve("true");
        }
      );
    } catch (IllegalViewOperationException e) {
      promise.reject("Error: ", e.getMessage());
    }
  }

  private String errorMessage(int version) {
    return "Your device version: " + Build.VERSION.SDK_INT + ". Supported API Level: " + version;
  }
}
