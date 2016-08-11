package ly.generalassemb.prep;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

public class TutorSessionActivity extends AppCompatActivity {

    private Handler mHandler;
    private boolean mStarted;

    Chronometer mChronometer;
    Button startButton;
    Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_session);



        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        mChronometer.setFormat("HH:MM:SS");

        // Watch for button clicks.
        startButton = (Button) findViewById(R.id.start);
        startButton.setOnClickListener(mStartListener);

        stopButton = (Button) findViewById(R.id.stop);
        stopButton.setOnClickListener(mStopListener);

//        button = (Button) findViewById(R.id.reset);
//        button.setOnClickListener(mResetListener);
//
//        button = (Button) findViewById(R.id.set_format);
//        button.setOnClickListener(mSetFormatListener);
//
//        button = (Button) findViewById(R.id.clear_format);
//        button.setOnClickListener(mClearFormatListener);
    }

    View.OnClickListener mStartListener = new View.OnClickListener() {
        public void onClick(View v) {
            mChronometer.start();
        }
    };

    View.OnClickListener mStopListener = new View.OnClickListener() {
        public void onClick(View v) {
            mChronometer.stop();
        }
    };

//    View.OnClickListener mResetListener = new View.OnClickListener() {
//        public void onClick(View v) {
//            mChronometer.setBase(SystemClock.elapsedRealtime());
//        }
//    };
//
//    View.OnClickListener mSetFormatListener = new View.OnClickListener() {
//        public void onClick(View v) {
//            mChronometer.setFormat("Formatted time (%s)");
//        }
//    };
//
//    View.OnClickListener mClearFormatListener = new View.OnClickListener() {
//        public void onClick(View v) {
//            mChronometer.setFormat(null);
//        }
//    };
}






//    private final Runnable mRunnable = new Runnable() {
//        @Override
//        public void run() {
//            if (mStarted) {
//                long seconds = (System.currentTimeMillis() - t) / 1000;
//                statusBar.setText(String.format("%02d:%02d", seconds / 60, seconds % 60));
//                mHandler.postDelayed(mRunnable, 1000L);
//            }
//        }
//
//    };
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        mStarted = true;
//        mHandler.postDelayed(mRunnable, 1000L);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        mStarted = false;
//        mHandler.removeCallbacks(mRunnable);
//    }

//}
