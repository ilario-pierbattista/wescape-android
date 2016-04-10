package com.dii.ids.application.main.authentication;

import android.support.design.widget.TextInputLayout;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.dii.ids.application.CustomMatchers;
import com.dii.ids.application.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginFragmentEspressoTest {
    public static final String validEmail = "prova@qualcosa";
    public static final String invalidEmail = "prova";
    public static final String validPassword = "A13f4";
    public static final String invalidPassword = "a";

    @Rule
    public ActivityTestRule<AuthenticationActivity> activityTestRule =
            new ActivityTestRule<AuthenticationActivity>(AuthenticationActivity.class);

    @Test
    public void sendEmptyCredentials() {
        onView(withId(R.id.login_signin_button))
                .perform(click());
        onView(withId(R.id.login_email_text_input_layout))
                .check(matches(CustomMatchers.hasTextInputLayoutErrorText(
                        activityTestRule.getActivity().getString(R.string.error_field_required)
                )));
        onView(withId(R.id.login_password_text_input_layout))
                .check(matches(CustomMatchers.hasTextInputLayoutErrorText(
                        activityTestRule.getActivity().getString(R.string.error_field_required)
                )));
    }

    @Test
    public void sendOnlyInvalidEmail() {
        onView(withId(R.id.login_email_text_input))
                .perform(clearText(), typeText(invalidEmail));
        onView(withId(R.id.login_password_text_input))
                .perform(clearText());
        onView(withId(R.id.login_signin_button))
                .perform(scrollTo(), click());

        onView(withId(R.id.login_email_text_input_layout))
                .check(matches(CustomMatchers.hasTextInputLayoutErrorText(
                        activityTestRule.getActivity().getString(R.string.error_invalid_email)
                )));
        onView(withId(R.id.login_password_text_input_layout))
                .check(matches(CustomMatchers.hasTextInputLayoutErrorText(
                        activityTestRule.getActivity().getString(R.string.error_field_required)
                )));
    }

    @Test
    public void sendOnlyValidEmail() {
        onView(withId(R.id.login_email_text_input))
                .perform(clearText(), typeText(validEmail));
        onView(withId(R.id.login_password_text_input))
                .perform(clearText());
        onView(withId(R.id.login_signin_button))
                .perform(scrollTo(), click());

        onView(withId(R.id.login_password_text_input_layout))
                .check(matches(CustomMatchers.hasTextInputLayoutErrorText(
                        activityTestRule.getActivity().getString(R.string.error_field_required)
                )));
    }

    @Test
    public void sendOnlyInvalidPassword() {
        onView(withId(R.id.login_email_text_input))
                .perform(clearText());
        onView(withId(R.id.login_password_text_input))
                .perform(clearText(), typeText(invalidPassword));
        onView(withId(R.id.login_signin_button))
                .perform(scrollTo(), click());

        onView(withId(R.id.login_email_text_input_layout))
                .check(matches(CustomMatchers.hasTextInputLayoutErrorText(
                        activityTestRule.getActivity().getString(R.string.error_field_required)
                )));
        onView(withId(R.id.login_password_text_input_layout))
                .check(matches(CustomMatchers.hasTextInputLayoutErrorText(
                        activityTestRule.getActivity().getString(R.string.error_invalid_password)
                )));
    }

    @Test
    public void sendOnlyValidPassword() {
        onView(withId(R.id.login_email_text_input))
                .perform(clearText());
        onView(withId(R.id.login_password_text_input))
                .perform(clearText(), typeText(validPassword));
        onView(withId(R.id.login_signin_button))
                .perform(scrollTo(), click());

        onView(withId(R.id.login_email_text_input_layout))
                .check(matches(CustomMatchers.hasTextInputLayoutErrorText(
                        activityTestRule.getActivity().getString(R.string.error_field_required)
                )));
    }

    @Test
    public void sendInvalidEmailAndPassword() {
        onView(withId(R.id.login_email_text_input))
                .perform(clearText(), typeText(invalidEmail));
        onView(withId(R.id.login_password_text_input))
                .perform(clearText(), typeText(invalidPassword));
        onView(withId(R.id.login_signin_button))
                .perform(scrollTo(), click());

        onView(withId(R.id.login_email_text_input_layout))
                .check(matches(CustomMatchers.hasTextInputLayoutErrorText(
                        activityTestRule.getActivity().getString(R.string.error_invalid_email)
                )));
        onView(withId(R.id.login_password_text_input_layout))
                .check(matches(CustomMatchers.hasTextInputLayoutErrorText(
                        activityTestRule.getActivity().getString(R.string.error_invalid_password)
                )));
    }

    @Test
    public void sendValidEmailAndInvalidPassword() {
        onView(withId(R.id.login_email_text_input))
                .perform(clearText(), typeText(validEmail));
        onView(withId(R.id.login_password_text_input))
                .perform(clearText(), typeText(invalidPassword));
        onView(withId(R.id.login_signin_button))
                .perform(scrollTo(), click());

        onView(withId(R.id.login_password_text_input_layout))
                .check(matches(CustomMatchers.hasTextInputLayoutErrorText(
                        activityTestRule.getActivity().getString(R.string.error_invalid_password)
                )));
    }

    @Test
    public void sendInvalidEmailAndValidPassword() {
        onView(withId(R.id.login_email_text_input))
                .perform(clearText(), typeText(invalidEmail));
        onView(withId(R.id.login_password_text_input))
                .perform(clearText(), typeText(validPassword));
        onView(withId(R.id.login_signin_button))
                .perform(scrollTo(), click());

        onView(withId(R.id.login_email_text_input_layout))
                .check(matches(CustomMatchers.hasTextInputLayoutErrorText(
                        activityTestRule.getActivity().getString(R.string.error_invalid_email)
                )));
    }
}