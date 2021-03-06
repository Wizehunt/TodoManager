package View;

import Model.Category;
import Model.Task;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Sur classe vue des catégories affichant toutes les catégories sur une même page.
 * @see Category,CategoryView,CategoriesPanel
 */
public class CategoriesPanel extends ObserverPanel{
    /**
     * Mise à jour de l'affichage de l'ensemble des catégories ainsi que les tâches qu'elles contiennent.
     *
     * @param o objet mis à jour
     * @param arg
     * @throws NullPointerException
     */
    public void update(Observable o, Object arg) {
        System.out.println("CategoriesPanel::update");
        removeAll();

        for (Category c : Category.getCategories()){
            try{
                CategoryView cv = c.getView();
                cv.getTasksPanel().removeAll();
                ArrayList<Task> tasks = c.getTasks();
                Task.sortByIntermediateDueDate(tasks);
                for (Task t : tasks){
                    if (t.getDoneDate() == null){
                        cv.getTasksPanel().add(t.getView());
                    }
                }


                cv.revalidate();
                cv.repaint();
                add(cv);

            }catch(NullPointerException e){
                System.err.println("CatView not found");
            }

        }
        repaint(); //Appel explicite à repaint() : sinon reliquats d'affichage de catégories supprimées
        revalidate();

    }
}
