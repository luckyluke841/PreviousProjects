package cyfitpackage.cyfit.other;

/**
 * Created by victordasilva on 3/17/17.
 */

public class WorkoutGoal {

    public WorkoutGoal(){

    }

    private int goalId;
    private String name;
    private String description;
    private int complete;

    public int getGoalId(){
        return goalId;
    }
    public String getName(){
        return name;
    }
    public String getDescription(){
        return description;
    }
    public int getComplete() { return complete; }

    public void setGoalId(int goalId){
        this.goalId = goalId;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public void setComplete(int complete){ this.complete = complete; }
}
