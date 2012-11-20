package com.oopl.breadcrumbs;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Shader;
import android.graphics.RadialGradient;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

public class AnimationView extends View {

    private static final int RED = 0xffFF8080;
    private static final int BLUE = 0xff8080FF;
    private static final int CYAN = 0xff80ffff;
    private static final int GREEN = 0xff80ff80;

    public final ArrayList<ShapeHolder> balls = new ArrayList<ShapeHolder>();
    AnimatorSet animation = null;

    public AnimationView(Context context) {
        super(context);

        // Animate background color
        // Note that setting the background color will automatically invalidate the
        // view, so that the animated color, and the bouncing balls, get redisplayed on
        // every frame of the animation.
        
        ValueAnimator colorAnim = ObjectAnimator.ofFloat(this, "alpha", 1.0f, 0.8f);
        colorAnim.setDuration(3000);
        //colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setRepeatCount(ValueAnimator.INFINITE);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();
        
    }

    
    public void play() {

        
        float h = (float)getHeight();
        float w = (float)getWidth();
        ShapeHolder newBall = addBall(w/2, h/2);

        ValueAnimator XAnim = ObjectAnimator.ofFloat(newBall, "x", newBall.getX(), newBall.getX() - 500f);
        XAnim.setDuration(750);
        XAnim.setInterpolator(new DecelerateInterpolator());
        
        ValueAnimator YAnim = ObjectAnimator.ofFloat(newBall, "y",  newBall.getY(), newBall.getY() - 500f);
        YAnim.setDuration(750);
        YAnim.setInterpolator(new DecelerateInterpolator());
        
        ValueAnimator scaleXAnim = ObjectAnimator.ofFloat(newBall, "width", newBall.getWidth(),
                newBall.getWidth() + 1000);
        scaleXAnim.setDuration(750);
        scaleXAnim.setInterpolator(new DecelerateInterpolator());
        
        ValueAnimator scaleYAnim = ObjectAnimator.ofFloat(newBall, "height", newBall.getHeight(),
                newBall.getHeight() + 1000);
        scaleYAnim.setDuration(750);
        scaleYAnim.setInterpolator(new DecelerateInterpolator());
        
        // Fading animation - remove the ball when the animation is done
        ValueAnimator fadeAnim = ObjectAnimator.ofFloat(newBall, "alpha", 1f, 0f);
        fadeAnim.setDuration(750);
        fadeAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                balls.remove(((ObjectAnimator)animation).getTarget());

            }
        });

        AnimatorSet center = new AnimatorSet();
        center.play(XAnim).with(YAnim);
        
        AnimatorSet grow = new AnimatorSet();
        grow.play(XAnim).before(scaleXAnim);
        grow.play(scaleXAnim).with(scaleYAnim);
        
        

        
        AnimatorSet both = new AnimatorSet();
        grow.play(center).with(grow);
        
        // Sequence the two animations to play one after the other
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(center).with(scaleXAnim);
        animatorSet.play(fadeAnim).with(scaleXAnim);
        animatorSet.play(scaleYAnim).with(scaleXAnim);

        // Start the animation
        animatorSet.start();

    }

    private ShapeHolder addBall(float x, float y) {
        OvalShape circle = new OvalShape();
        circle.resize(50f, 50f);
        ShapeDrawable drawable = new ShapeDrawable(circle);
        ShapeHolder shapeHolder = new ShapeHolder(drawable);
        shapeHolder.setX(x - 25f);
        shapeHolder.setY(y - 25f);

        Paint paint = drawable.getPaint(); //new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setStyle(Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(25);
        
        shapeHolder.setPaint(paint);
        
        balls.add(shapeHolder);
        
        return shapeHolder;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < balls.size(); ++i) {
            ShapeHolder shapeHolder = balls.get(i);
            canvas.save();
            canvas.translate(shapeHolder.getX(), shapeHolder.getY());
            shapeHolder.getShape().draw(canvas);
            canvas.restore();
        }
    }
}