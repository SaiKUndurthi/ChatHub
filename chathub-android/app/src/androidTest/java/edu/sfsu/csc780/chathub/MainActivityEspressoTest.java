/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.sfsu.csc780.chathub;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.test.suitebuilder.annotation.LargeTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.sfsu.csc780.chathub.ui.MainActivity;
import static android.support.test.espresso.Espresso.onView;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityEspressoTest {

    @Rule
    public IntentsTestRule<MainActivity> mIntentRule =
            new IntentsTestRule<>(MainActivity.class);
    private MainActivity mainActivity;
    @Before
    public void setActivity() {
        mainActivity = mIntentRule.getActivity();
    }
    @After
    public void tearDown() {
        mainActivity.finish();
    }

    // Add instrumentation test here
    @Test
    public void testSend(){
        /**
         * This method checks if the text message sent matches with what's being displayed.
         */
        // Typing text on the EditText icon to perform a text message test.
        onView(withId(R.id.messageEditText)).perform(typeText("UI testing using Espresso."),closeSoftKeyboard());

        // Click on the button in our app that sends the message and validate the text match .
        onView(withId(R.id.sendButton)).perform(click()).check(matches(isDisplayed()));
    }



    @Test
    public void testDrawingSend(){
        /**
         * This method if the Drawing application starts on clicking the paint button.
         */
        // Click on the button in our app that opens the "Drawing Application".
        onView(withId(R.id.paintimageButton)).perform(click());

        // Validating that the explicit intent "Drawing Activity" has started.
        intending(hasComponent(hasShortClassName(".DrawingActivity")));
    }


   @Test
    public void testImagePicker(){
       /**
        * This method selects the App launcher icon as the image and sends it through as Image message.
        */

        //onView(withId(R.id.button_take_photo)).perform(click());
        // Create a bitmap we can use for our image. Used our app launcher icon for testing.
        Bitmap icon = BitmapFactory.decodeResource(
                InstrumentationRegistry.getTargetContext().getResources(),
                R.mipmap.ic_launcher);

        // Build a result to return from the Camera app
        Intent resultData = new Intent();
        resultData.putExtra("data", icon);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        // Stub out the Camera. When an intent is sent to the Camera, this tells Espresso to respond
        // with the ActivityResult we just created. Assuming that we're using Google Default Camera.
        intending(toPackage("com.google.android.GoogleCamera")).respondWith(result);

        // Now that we have the stub in place, click on the button in our app that launches into the Camera
        onView(withId(R.id.cameraButton)).perform(click());

        // We can also validate that an intent resolving to the "camera" activity has been sent out by our app
        intended(toPackage("com.google.android.GoogleCamera"));

    }


}
