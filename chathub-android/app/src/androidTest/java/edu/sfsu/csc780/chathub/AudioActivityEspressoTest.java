package edu.sfsu.csc780.chathub;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import edu.sfsu.csc780.chathub.ui.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasCategories;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasType;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.core.AllOf.allOf;

public class AudioActivityEspressoTest {
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
    @Test
    public void testAudioSend(){
        /**
         * This method checks if the click on the Audio button let's you to choose audio files.
         */
        // Click on the button in our app that opens the "audio file chooser".
        onView(withId(R.id.audioimageButton)).perform(click());

        // We can also validate that an intent resolving to the "audio picker" activity has been sent out by our app
        intending(toPackage("com.android.documentsui"));

        // Validating that the intent with file type as "audio/*" has started.
        intended(allOf(hasAction(equalTo(Intent.ACTION_GET_CONTENT)),
                hasCategories(hasItem(equalTo(Intent.CATEGORY_OPENABLE))),
                hasType(equalTo("audio/*"))));
    }

}
