package edu.sfsu.csc780.chathub;

import android.provider.MediaStore;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import edu.sfsu.csc780.chathub.ui.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.AllOf.allOf;

public class ImageClickEspressoTest {
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
    public void testImagePicker(){
     /**
      * This method checks if the click on the Gallery button let's you to choose image files.
      */
     // Click on the button in our app that opens the "camera".
        onView(withId(R.id.cameraButton)).perform(click());

     // We can also validate that an intent resolving to the "image picker" activity has been sent out by our app
     intended(toPackage("com.google.android.GoogleCamera"));

      // Validating that the intent with IMAGE CAPTURE has started.
        intended(allOf(hasAction(equalTo(MediaStore.ACTION_IMAGE_CAPTURE))));
    }


}
