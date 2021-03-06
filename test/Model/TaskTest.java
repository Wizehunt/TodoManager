package Model;

import Controller.MainFrameController;
import View.MainFrame;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class TaskTest{

    private Category aucune, base, travail, perso;

    private Task endsToday, t;
    private Task endsTomorrow;
    private Task endedYesterday;
    private Task aMonthAgo;
    private Task inAMonth;

    @Before
        public void setUp() throws Exception {
//Initialisation comme dans le main
        TodoManager tm = new TodoManager(); // L'instance du gestionnaire de TodoList

        MainFrameController mainFrameController = new MainFrameController(tm); //La fenêtre de l'appi et son contrôleur
        MainFrame mainFrame = new MainFrame(mainFrameController);

        //Categories statiques, or le @Before les initialisera à chaque fois, lancant des Exceptions
        //On veut rester dans un contexte non statique pour l'initialisation (pas de @BeforeClass)
        // --> le try/catch ne fais lancer les initialisation que pour le premier test
        try{
            Category.getAucune();
        }catch (IndexOutOfBoundsException e){
            aucune = new Category("Aucune");
            base = new Category("Base");
            travail = new Category("Travail");
            perso = new Category("Personnel");

        }

        Category.getAucune().getTasks().clear();

        t = new Task("Tâche");

        endsToday = new Task("today");
        endsTomorrow = new Task("tomorrow");
        endedYesterday = new Task("yesterday");
        aMonthAgo = new LongTask("A month ago");
        inAMonth = new Task("In a month");

        endsToday.setProgress(100);

        endsTomorrow.echeance = (LocalDate.now().plusDays(1));
        endedYesterday.echeance = (LocalDate.now().minusDays(1));
        aMonthAgo.echeance = (LocalDate.now().minusMonths(1));
        inAMonth.echeance = (LocalDate.now().plusMonths(1));


    }

    @Test (expected=IllegalArgumentException.class)
    public void testNomNonvide(){
        Task nomVide = new Task(""); //argument chaîne vide interdit
    }

    @Test (expected=IllegalArgumentException.class)
    public void testNomDejaPris(){
        Task nomDejaPris = new Task("today");
    }

    @Test
    public void testSetContenu() throws Exception {

        endsToday.setContenu("du contenu");
        assertEquals("du contenu", endsToday.getContenu());
    }

    @Test
    public void testSetEcheance() throws Exception {
        endedYesterday.setEcheance(LocalDate.now().plusDays(1));
        assertEquals(LocalDate.now().plusDays(1), endedYesterday.echeance);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSetEcheanceTropTot() throws Exception {
        endsTomorrow.setEcheance(LocalDate.now().minusDays(2));
    }

    @Test
    public void testGetProgress() throws Exception {
        assertThat(endsToday.getProgress(), is(100));
    }

    @Test
    public void testSetProgress() throws Exception {
        inAMonth.setProgress(10);
        assertThat(inAMonth.getProgress(), is(10));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSetProgressNegative() throws Exception {
        inAMonth.setProgress(-1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSetProgressTooHigh() throws Exception {
        inAMonth.setProgress(999);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSetProgressWithRegression() throws Exception {
        inAMonth.setProgress(24);
        inAMonth.setProgress(2);
    }

    @Test
    public void testIsLate() throws Exception {
        assertTrue(endedYesterday.isLate()); //sensée finir hier, avancement à 0 --> en retard
    }

    @Test
    public void testLongIsLate() throws Exception {
        LongTask late = new LongTask("en retard");
        late.setStartDate(LocalDate.now().minusDays(10)); // début il y a 10 jours, fin aujourd'hui
        late.setProgress(50); //Avancement : 50% --> pas assez : en retard

        assertTrue(late.isLate());

    }

    @Test
    public void testLongIsNotLate() throws Exception {
        assertFalse(inAMonth.isLate());
    }


    @Test
    public void testSortByDueDate() throws Exception {

        ArrayList<Task> tasks  = new ArrayList<>();
        tasks.add(endsToday);
        tasks.add(endedYesterday);

        Task.sortByDueDate(tasks);


        LocalDate first = tasks.get(0).echeance,
                second = tasks.get(1).echeance;

        assertFalse(first.isAfter(second)); //L'élément second est postérieur ou simultané au premier
    }

    @Test (expected = IllegalArgumentException.class)
    public void teststartDateCoherente(){
        LongTask task = new LongTask("weirdo");
        task.setStartDate(LocalDate.now().plusMonths(1));
    }

    @Test
    public void testIsBetween() throws Exception {
        assertTrue(endsToday.isBetween(
                LocalDate.now(), LocalDate.now().plusDays(1) // Aujourd'hui est bien entre now() et demain
        ));

    }

    @Test
    public void testIsNotBetweenButAfter() throws Exception {
        assertFalse(endsTomorrow.isBetween(
                LocalDate.now().minusDays(1), LocalDate.now() // Demain n'est pas entre hier et aujourd'hui
        ));
    }

    @Test
    public void testIsNotBetweenButBefore() throws Exception {
        assertFalse(endedYesterday.isBetween(
                LocalDate.now(), LocalDate.now().plusDays(1) // Hier n'est pas entre aujourd'hui et demain
        ));

    }

    @Test
    public void testNotReleasedLate() throws Exception {
        assertFalse(endsToday.releasedLate()); // complétée aujourd'hui, en temps et en heure
    }
}