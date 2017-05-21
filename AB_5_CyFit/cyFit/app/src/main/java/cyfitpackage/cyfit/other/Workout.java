package cyfitpackage.cyfit.other;

import android.content.Context;
import android.graphics.drawable.Drawable;

import cyfitpackage.cyfit.R;

/**
 * The workout object
 */
public class Workout {
    private String name;
    private String description;
    //protected Drawable picture;

    /**
     * Gets the name of the workout object
     *
     * @return name of workout object
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description for the workout object.
     *
     * @return description of workout object
     */
    public String getDesc() {
        return description;
    }

    /**
     * Sets the name for the workout object
     *
     * @param name the new name of the workout object
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the description for the workout object
     *
     * @param desc the new description for the workout object
     */
    public void setDesc(String desc) {
        this.description = desc;
    }
}
