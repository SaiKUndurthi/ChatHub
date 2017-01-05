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

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;


import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.sfsu.csc780.chathub.ui.DrawingActivity;
import static android.support.test.espresso.Espresso.onView;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DrawingActivityEspressoTest {

    @Rule
    public IntentsTestRule<DrawingActivity> mIntentRule =
            new IntentsTestRule<>(DrawingActivity.class);

    // Add instrumentation test here

    @Test
    public void newButtonDrawingYesClickedDialog()   {
        /**
         * This method checks if the "Yes" option in the AlertDialog for new canvas works in Drawing Activity.
         */
        // Click on the new button in our app that opens the "New File". Validates if the title of AlertDialog matches.
        onView(withId(R.id.new_btn)).perform(click());
        onView(withText(R.string.newdrawing))
                .check(matches(isDisplayed()));

        //  Validates if the message of AlertDialog matches.
        onView(withText(R.string.alert_message))
                .check(matches(isDisplayed()));

        //  Validates if the positive button has text "Yes" of AlertDialog.
        onView(withId(android.R.id.button1))
                .check(matches(withText("Yes")))
                .check(matches(isDisplayed()));

        //  Validates if the negative button has text "Cancel" of AlertDialog. And performs "YES" operation so a new canvas comes up.
        onView(withId(android.R.id.button2))
                .check(matches(withText("Cancel")))
                .check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());
    }


    @Test
    public void testBrushSizeDialog(){
        /**
         * This method checks for the intended activity when Brushes button is clicked.
         */
        intending(hasComponent(hasShortClassName(".DrawingActivity")));
    }

    @Test
    public void newButtonDrawingNoClickedDialog()   {
        /**
         * This method checks if the "Cancel" option in the AlertDialog for new canvas works in Drawing Activity.
         */
        // Click on the new button in our app that opens the "New File". Validates if the title of AlertDialog matches.
        onView(withId(R.id.new_btn)).perform(click());
        onView(withText(R.string.newdrawing))
                .check(matches(isDisplayed()));

        //  Validates if the message of AlertDialog matches.
        onView(withText(R.string.alert_message))
                .check(matches(isDisplayed()));

        //  Validates if the positive button has text "Yes" of AlertDialog.
        onView(withId(android.R.id.button1))
                .check(matches(withText("Yes")))
                .check(matches(isDisplayed()));

        //  Validates if the negative button has text "Cancel" of AlertDialog.
        // And performs "Cancel" operation so there is no change to the canvas.
        onView(withId(android.R.id.button2))
                .check(matches(withText("Cancel")))
                .check(matches(isDisplayed()));
        onView(withId(android.R.id.button2)).perform(click());
    }


}
