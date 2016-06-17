package ru.android_studio.check_passport;

import android.graphics.Point;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingRootException;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;

import com.squareup.spoon.Spoon;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.android_studio.check_passport.data.CaptchaParam;
import ru.android_studio.check_passport.data.RequestParam;
import ru.android_studio.check_passport.pageObject.CaptchaPage;
import ru.android_studio.check_passport.pageObject.HistoryPage;
import ru.android_studio.check_passport.pageObject.RequestPage;
import ru.android_studio.check_passport.utill.DBUtil;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Тестирование страницы кода подтверждения
 * */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class CaptchaFragmentTest {

    @Rule
    public ActivityTestRule<PassportActivity> activityActivityTestRule = new ActivityTestRule<>(PassportActivity.class);

    private PassportActivity activity;

    private CaptchaPage captchaPage;
    private RequestPage requestPage;
    private HistoryPage historyPage;


    @Before
    public void setUp() {
        activity = activityActivityTestRule.getActivity();

        // init Page objects
        captchaPage = new CaptchaPage(activity);
        requestPage = new RequestPage(activity);
        historyPage = new HistoryPage(activity);
    }

    /*
    * Оживляем телефон прежде чем запустить тест
    * */
    @Before
    public void init() {
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

    /*
    *   Тестирование загрузки капчи
    * */
    @Test
    public void testCaptcha() {
        Spoon.screenshot(activity, "start");

        DBUtil.clearAppInfo(activity);
        requestPage.writeSeries(RequestParam.EXISTED);
        requestPage.writeNumber(RequestParam.EXISTED);
        requestPage.clickRequestBtn();
        captchaPage.isDisplayed();
        captchaPage.writeCaptcha(CaptchaParam.INVALID);
        captchaPage.clickCheckCaptchaBtn();

        boolean exceptionCaptured = false;
        try {
            onView(withText(R.string.toast_error_captcha_not_valid))
                    .inRoot(withDecorView(not(is(activity.getWindow().getDecorView()))))
                    .check(doesNotExist());

        } catch (NoMatchingRootException e) {
            exceptionCaptured = true;
        } finally {
            assertTrue(exceptionCaptured);
        }

        Spoon.screenshot(activity, "end");
    }
}