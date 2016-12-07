package Model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by achaillot on 05/12/16.
 */
public abstract class Task implements Serializable {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getEcheance() {
        return echeance;
    }

    public LocalDate getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(LocalDate doneDate) {
        this.doneDate = doneDate;
    }

    protected String name = "";
    protected String contenu = "";
    protected LocalDate echeance = LocalDate.now();
    protected LocalDate doneDate = null;
    protected int progress = 0;


    public Task(String name){
        if (name.isEmpty()){
            throw new IllegalArgumentException();
        }
        this.name = name;
        Category c = Category.categoryAucune();
        c.addTask(this);
    }

    public Task(String name,Category c){
        this(name);
        c.addTask(this);
    }

    public void setContenu(String contenu){
        this.contenu = contenu;
    }

    public void setEcheance(LocalDate ld) throws IllegalArgumentException{
        if (!ld.isAfter(LocalDate.now())){
            throw new IllegalArgumentException("Une tâche ne peut être repoussée que dans le futur");
        }
        echeance = ld;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        if(progress == 100){
            doneDate = LocalDate.now();
        }
        this.progress = progress;
        //Todo : remove Task de la category : envoyer un notify (a la fin)
    }


    public boolean isLate(){
        return LocalDate.now().isAfter(echeance) && progress < 100;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        if (!(o.getClass() == this.getClass())) return false;

        Task task = (Task) o;

        return name.equals(task.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     *
     * @return la liste des tâches ordonnée par date d'écheance croissante
     */
    public static void sortByDueDate(ArrayList<Task> tasks){
        tasks.sort(new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                if (o1.echeance.equals(o2.echeance)) return 0;
                return (o1.echeance.isBefore(o2.echeance)) ? -1 : 1;
            }
        });
    }

    public boolean isBetween(LocalDate start,LocalDate end){

        if(echeance.isEqual(start) || echeance.isEqual(end)){
            return true;
        }
        else if(echeance.isBefore(end) && echeance.isAfter(start)){
            return true;
        }else{
            return false;
        }
    }

    public boolean releasedLate(){
            return doneDate.isAfter(echeance);
    }

    public String getContenu() {
        return contenu;
    }

    public String toString(){
        return "["+echeance+"] : " + name;
    }

    /**
     *
     * @return toutes les tâches existantes
     */
    public static ArrayList<Task> allTasks(){
        ArrayList<Task> tasks = new ArrayList<>();
        for (Category category : Category.getCategories()){
            for (Task t : category.getTasks()){
                tasks.add(t);
            }
        }
        return tasks;
    }
}
