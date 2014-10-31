package com.boxsorter.tryptical.boxsorterajl;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import domain.Square;
import gesture.RotationGestureDetector;
import gesture.RotationGestureListener;


public class BoxSorter extends Activity{

    private RelativeLayout mRootLayout;
    private int _xDelta;
    private int _yDelta;
    private ImageView mBlackHole;
    private List<ImageView> mImageViewList;

    private float scale = 1f;

    private static Integer DEFAULT_SQUARE_SIZE = 150;

    // For understanding why mRotationGestureDetector is never used, go to line number 373
    private RotationGestureDetector mRotationGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_sorter);

        // Black Hole and Buttons
        mBlackHole = (ImageView) findViewById(R.id.black_hole);
        Button mAddShapeButton = (Button) findViewById(R.id.add_shape);
        Button mSuckInButton = (Button) findViewById(R.id.suck_in);
        // ImageViews list
        mImageViewList = new ArrayList<ImageView>();
        // Relative Layout
        mRootLayout = (RelativeLayout) findViewById(R.id.relative_box_sorter_layout);


        //Create 3 random squares
        for (int i=0; i<3; i++){
            createSquareRandomly();
        }

        // Create squares from JSON
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            for(Square sq: getSquaresFromJSON(obj)){
                createSquareFromJson(sq);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Suck In button listener
        mSuckInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ImageView imageView : mImageViewList){
                    suckingAnimation(imageView);
                }
            }
        });
        // Add Shape button listener
        mAddShapeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSquareRandomly();
            }
        });

    }

    // Animation: decreasing the size of the square while pulling it into the centre of the circle
    private void suckingAnimation(View view){
        // Properties of the black hole:
        int[] img_coordinates = new int[2];
        double x_center = (double)img_coordinates[0] + mBlackHole.getWidth()/2.5;
        double y_center = (double)img_coordinates[1] + mBlackHole.getHeight()*2.0;
        if(view.getWidth()>DEFAULT_SQUARE_SIZE){
            x_center = (double)img_coordinates[0] + mBlackHole.getWidth()/4.0;
            y_center = (double)img_coordinates[1] + mBlackHole.getHeight()*1.5;
        }
        mBlackHole.getLocationOnScreen(img_coordinates);
        // Going to the center of the hole with .x(float) and .y(float)
        ViewPropertyAnimator animator = view.animate().x((float)x_center).y((float)y_center);
        // Resizing
        animator.scaleX(0.0f);
        animator.scaleY(0.0f);
        // Setting up duration and starting the animation
        animator.setDuration(2000);
        animator.start();
    }

    // This method generates random squares
    private void createSquareRandomly(){
        final ImageView imgView = new ImageView(this);
        // Setting its length and width
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(DEFAULT_SQUARE_SIZE, DEFAULT_SQUARE_SIZE);
        // Setting its position
        Random random = new Random();
        Integer LOWER_STARTING_POSITION = 25;
        Integer HIGHER_STARTING_POSITION = 80;
        int randomX = random.nextInt(HIGHER_STARTING_POSITION - LOWER_STARTING_POSITION) + LOWER_STARTING_POSITION;
        int randomY = random.nextInt(HIGHER_STARTING_POSITION - LOWER_STARTING_POSITION) + LOWER_STARTING_POSITION;
        layoutParams.setMargins(randomX,randomY, 0,0);
        // Applying random colour to the ImageView
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        imgView.setBackground(new ColorDrawable(color));
        // Applying the size and position to the ImageView
        imgView.setLayoutParams(layoutParams);
        // Adding to the view
        mRootLayout.addView(imgView);
        // Adding to a List to manage all the ImageViews created
        mImageViewList.add(imgView);
        // Applying all the gestures needed for double-tapping, dragging...
        setUpGestures(imgView);
    }

    // This method generates squares from JSON file's data
    private void createSquareFromJson(Square square){
        final ImageView imgView = new ImageView(this);
        // Setting its length and width
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(square.getSize(), square.getSize());
        // Setting its position
        layoutParams.setMargins(square.getX(),square.getY(), 0,0);
        // Applying colour
        int intColour = Color.parseColor(square.getColour());
        imgView.setBackground(new ColorDrawable(intColour));
        // Applying the size and position to the ImageView
        imgView.setLayoutParams(layoutParams);
        //Adding to the view
        mRootLayout.addView(imgView);
        // Adding to a List to manage all the ImageViews created
        mImageViewList.add(imgView);
        // Applying all the gestures needed for double-tapping, dragging...
        setUpGestures(imgView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.box_sorter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // An initial layout of squares can be built from json.
    // Loading the JSON file
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("squares.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    // Generating squares from JSON file
    public List<Square> getSquaresFromJSON(JSONObject obj) throws JSONException {
        JSONArray m_jArry = obj.getJSONArray("squares");
        List<Square> squares = new ArrayList<Square>();

        for (int i = 0; i < m_jArry.length(); i++)
        {
            JSONObject jo_inside = m_jArry.getJSONObject(i);
            int x = jo_inside.getInt("x");
            int y = jo_inside.getInt("y");
            String colour = jo_inside.getString("colour");
            int size = jo_inside.getInt("size");

            // Creating a square and adding to the list
            Square square = new Square(x,y,colour,size);
            squares.add(square);
        }
        return squares;
    }


    // Setting up the gestures needed:
    public void setUpGestures(ImageView imageView){
        final ImageView imgView = imageView;
        // Gesture detection (for the double tap and the fling)
        final GestureDetector detector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }
            //Changes color square
            @Override
            public boolean onDoubleTap(MotionEvent arg0) {
                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                imgView.setBackground(new ColorDrawable(color));
                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent arg0) {
                return false;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent arg0) {
                return false;
            }

            @Override
            public boolean onDown(MotionEvent arg0){
                return true;
            }
            private final int SWIPE_MIN_DISTANCE = 120;
            private final int SWIPE_THRESHOLD_VELOCITY = 100;
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    // FLING Right to left
                    ObjectAnimator flingAnimator = ObjectAnimator.ofFloat(imgView, "translationX", e1.getX(), e2.getX());
                    flingAnimator.setDuration(1000);
                    flingAnimator.start();
                    return true;
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) >  SWIPE_THRESHOLD_VELOCITY) {
                    // Left to right
                    ObjectAnimator flingAnimator = ObjectAnimator.ofFloat(imgView, "translationX", e1.getX(), e2.getX());
                    flingAnimator.setDuration(1000);
                    flingAnimator.start();
                    return true;
                }
                if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    // Bottom to top, your code here
                    ObjectAnimator flingAnimator = ObjectAnimator.ofFloat(imgView, "translationY", e1.getY(), e2.getY());
                    flingAnimator.setDuration(1000);
                    flingAnimator.start();
                    return true;
                } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    // Top to bottom
                    ObjectAnimator flingAnimator = ObjectAnimator.ofFloat(imgView, "translationY", e1.getY(), e2.getY());
                    flingAnimator.setDuration(1000);
                    flingAnimator.start();
                    return true;
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent event) {
            }

        });

        // ScaleGestureDetector (for resizing with a pich gesture)
        final ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.OnScaleGestureListener() {

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scale = detector.getScaleFactor();
                scale = Math.max(0.1f, Math.min(scale, 5.0f));
                int actualWidth = imgView.getLayoutParams().width;
                int actualHeigth = imgView.getLayoutParams().height;
                int width = Math.round(scale*actualWidth);
                int length = Math.round(scale*actualHeigth);
                //Restriction for making easies its use
                if(width >= 2*actualWidth || length >= 2*actualHeigth){
                    width = 500;
                    length = 500;
                }
                if(width <= 50 || length <= 50){
                    width = 50;
                    length = 50;
                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, length);
                imgView.setLayoutParams(layoutParams);
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
            }
        });

        // Listener for dragging around the square
        View.OnTouchListener gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                final int X = (int) event.getRawX();
                final int Y = (int) event.getRawY();

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
                        _xDelta = X - lParams.leftMargin;
                        _yDelta = Y - lParams.topMargin;
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v
                                .getLayoutParams();
                        layoutParams.leftMargin = X - _xDelta;
                        layoutParams.topMargin = Y - _yDelta;
                        layoutParams.rightMargin = -250;
                        layoutParams.bottomMargin = -250;
                        v.setLayoutParams(layoutParams);
                        break;
                }

                // Properties of the black hole:
                int[] img_coordinates = new int[2];
                mBlackHole.getLocationOnScreen(img_coordinates);
                // If a user finishes a drag with a square overlapping the black circle it should be “sucked in” to the circle and leave the scene.
                if((v.getX()>= img_coordinates[0] && v.getX()<= img_coordinates[0]+mBlackHole.getWidth()) && (v.getY()>=(img_coordinates[1]-mBlackHole.getHeight()) && v.getY()<=img_coordinates[1])){
                    suckingAnimation(v);
                    mImageViewList.remove(imgView);
                }
                mRootLayout.invalidate();
                if (detector.onTouchEvent(event)) {
                    return true;
                }
                // Applying also the ScaleGesture and te RotationGesture
                scaleGestureDetector.onTouchEvent(event);

                // Uncomment the following line to allow the rotation
                //mRotationGestureDetector.onTouchEvent(event);
                return false;
            }
        };

        imgView.setOnTouchListener(gestureListener);

        // RotationGestureDetector (rotate a square with a rotation gesture)
        mRotationGestureDetector = new RotationGestureDetector(new RotationGestureListener() {
            @Override
            public void onRotation(RotationGestureDetector detector) {
                float mAngle = detector.getDeltaAngle();
                imgView.setRotation(mAngle);
            }

            @Override
            public void onRotationBegin(RotationGestureDetector detector) {
            }

            @Override
            public void onRotationEnd(RotationGestureDetector detector) {
            }
        });
    }
}
