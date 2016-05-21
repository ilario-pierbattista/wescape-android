package com.dii.ids.application.main.authentication;

import android.content.Context;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.dii.ids.application.CustomMatchers;
import com.dii.ids.application.R;
import com.dii.ids.application.api.auth.wescape.TokenStorage;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SignupFragmentTest {

    @Rule
    public ActivityTestRule<AuthenticationActivity> activityTestRule =
            new ActivityTestRule<>(AuthenticationActivity.class);

    @Before
    public void deleteToken() {
        Context context = activityTestRule.getActivity();
        TokenStorage tokenStorage = new TokenStorage(context);
        tokenStorage.delete();
    }

    @Before
    public void reachFragment() {
        onView(withId(R.id.sign_up_text))
                .perform(scrollTo(), click());
    }

    @Test
    public void sendEmptyCredentials() {
        onView(withId(R.id.signup_email_text_input))
                .perform(clearText());
        onView(withId(R.id.signup_password_text_input))
                .perform(clearText());
        onView(withId(R.id.signup_password_confirm_text_input))
                .perform(clearText());
        onView(withId(R.id.signup_button))
                .perform(click());

        onView(withId(R.id.signup_email_text_input_layout))
                .check(matches(CustomMatchers.hasTextInputLayoutErrorText(
                        activityTestRule.getActivity().getString(R.string.error_field_required)
                )));
        onView(withId(R.id.signup_password_text_input_layout))
                .check(matches(CustomMatchers.hasTextInputLayoutErrorText(
                        activityTestRule.getActivity().getString(R.string.error_field_required)
                )));
    }
}