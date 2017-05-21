package cyfitpackage.cyfit.other;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Food {

    private Long ID;
    private String name;
    private String brand;
    private String description;
    private FatSecretAPI api;

    public Food(String ID, String name, String brand, String description) {
        this.ID = Long.parseLong(ID);
        this.name = name;
        this.brand = brand;
        this.description = description;
        api = new FatSecretAPI();
    }

    public String getName() { return this.name; }

    public String getBrand() { return this.brand; }

    public String getDescription() { return this.description; }

    public Long getID() { return this.ID; }

    public Object getDetailedData() {
        try {
            JSONObject food = api.getNutritionData(this.ID);
            JSONObject servings = food.getJSONObject("servings");
            JSONArray data = servings.optJSONArray("serving");
            return (data == null) ? servings.optJSONObject("serving") : data ;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
