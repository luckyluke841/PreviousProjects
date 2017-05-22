package cyfitpackage.cyfit.other;

/**
 * Created by skywa on 3/2/2017.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;



public class SlidingRelativeLayout extends RelativeLayout {
    private float yFraction = 0;

    /**
     *
     * @param context
     */
    public SlidingRelativeLayout(Context context) {
        super(context);

    }

    /**
     *
     * @param context
     * @param attrs
     */
    public SlidingRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    /**
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public SlidingRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    /**
     * initializes
     */
    public void init()
    {

    }

    private ViewTreeObserver.OnPreDrawListener preDrawListener = null;

    /**
     * sets the yFraction position of the layout relative to the main view
     * @param fraction
     */
    public void setYFraction(float fraction) {

        this.yFraction = fraction;

        if (getHeight() == 0) {
            if (preDrawListener == null) {
                preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        getViewTreeObserver().removeOnPreDrawListener(preDrawListener);
                        setYFraction(yFraction);
                        return true;
                    }
                };
                getViewTreeObserver().addOnPreDrawListener(preDrawListener);
            }
            return;
        }

        float translationY = getHeight() * fraction;
        setTranslationY(translationY);
    }

    /**
     *
     * @return the yFraction
     */
    public float getYFraction() {
        return this.yFraction;
    }
}