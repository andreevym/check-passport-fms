package ru.android_studio.check_passport;

import android.app.Activity;
import android.graphics.Point;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import java.lang.reflect.ParameterizedType;

import ru.android_studio.check_passport.pageObject.CaptchaPage;
import ru.android_studio.check_passport.pageObject.HistoryPage;
import ru.android_studio.check_passport.pageObject.RequestPage;

/**
 * Класс предоставляет осномные возможности необходимые для тестирования интерфейса
 */
@RunWith(AndroidJUnit4.class)
public class TestUI<T extends Activity> {

    @Rule
    public ActivityTestRule<T> activityTestRule = new ActivityTestRule<T>(clazz());

    private Class<T> clazz() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected Activity activity;

    protected CaptchaPage captchaPage;
    protected RequestPage requestPage;
    protected HistoryPage historyPage;

    protected DatabaseHelper database;

    @Before
    public void setUp() {
        System.out.println("Before");
        wakeUpDevice();
        load();
    }

    /*
    * Оживляем телефон прежде чем запустить тест
    * */
    private void load() {
        activity = activityTestRule.getActivity();

        // init Page objects
        captchaPage = new CaptchaPage(activity);
        requestPage = new RequestPage(activity);
        historyPage = new HistoryPage(activity);

        activity.deleteDatabase(DatabaseHelper.DB_NAME);
        database = new DatabaseHelper(activity);

    }

    private void wakeUpDevice() {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        Point[] coordinates = new Point[4];
        coordinates[0] = new Point(248, 1520);
        coordinates[1] = new Point(248, 929);
        coordinates[2] = new Point(796, 1520);
        coordinates[3] = new Point(796, 929);
        try {
            if (!uiDevice.isScreenOn()) {
                uiDevice.wakeUp();
                uiDevice.swipe(coordinates, 10);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws Exception {
        database.clear();
        database.close();
    }
}
