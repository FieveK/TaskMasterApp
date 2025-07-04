package com.example.taskmaster;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.taskmaster.Activity.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
public class LoginFragmentTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> rule = new ActivityScenarioRule<>(LoginActivity.class);


    @Test
    public void testLoginFormDisplayedAndCanSubmit() throws InterruptedException {
        // Cek apakah email dan password input muncul
        onView(withId(R.id.emailInput)).check(matches(isDisplayed()));
        onView(withId(R.id.passwordInput)).check(matches(isDisplayed()));
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()));

        // Isi form login
        onView(withId(R.id.emailInput)).perform(typeText("test@example.com"), closeSoftKeyboard());
        onView(withId(R.id.passwordInput)).perform(typeText("password123"), closeSoftKeyboard());

        // Klik tombol login
        onView(withId(R.id.loginButton)).perform(click());

        // Tunggu 2 detik (optional, untuk lihat hasil atau pindah activity)
        Thread.sleep(1000); // setelah klik, untuk delay animasi fragment (hanya jika perlu)

        // (Opsional) Periksa hasil, misalnya tampilan baru atau Toast
        // onView(withText("Welcome")).check(matches(isDisplayed()));  // jika ada
    }
}
