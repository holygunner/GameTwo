package com.holygunner.halves_into_whole;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
public class StartGameActivityTest {
    @Rule
    public ActivityTestRule<StartGameActivity> mActivityTestRule
            = new ActivityTestRule<>(StartGameActivity.class);

    @Test
    public void showButtonTitleExists(){
        onView(withText("About"))
                .check(matches(anything()));
    }

    @Test
    public void testClickOnSelectLevel(){
        onView(withId(R.id.choose_level_button)).perform(click());
    }

    @Test
    public void testClickOnAboutButton(){
        onView(withId(R.id.about_button)).perform(click());
    }
}