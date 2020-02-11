package dao;

import models.Ranger;
import models.Animal;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import static org.junit.Assert.*;

public class Sql2oRangerDaoTest {
    private Sql2oRangerDao rangerDao;
    private Sql2oAnimalDao animalDao;
    private Connection conn;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        rangerDao = new Sql2oRangerDao(sql2o);
        animalDao = new Sql2oAnimalDao(sql2o);
        conn = sql2o.open();
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    public Ranger testRanger(){
        return new Ranger("Tarzan", "jungle");
    }

    @Test
    public void addingARangerSetsId() throws Exception{
        Ranger ranger = testRanger();
        int rangerId = ranger.getId();
        rangerDao.add(ranger);
        assertNotEquals(rangerId, ranger.getId());
    }

    @Test
    public void rangerCanFindRangerById() throws Exception{
        Ranger ranger = testRanger();
        rangerDao.add(ranger);
        Ranger foundRanger = rangerDao.findById(ranger.getId());
        assertEquals(ranger, foundRanger);
    }

    @Test
    public void getAllRangerRetunsAllAddedRangers() throws Exception{
        Ranger ranger = testRanger();
        Ranger anotherRanger = new Ranger("Moli", "riverside");
        rangerDao.add(ranger);
        rangerDao.add(anotherRanger);
        assertEquals(2, rangerDao.getAll().size());
    }

    @Test
    public void ifNoRangerAddedReturnsEmpty() throws Exception{
        assertEquals(0, rangerDao.getAll().size());
    }

    @Test
    public void updateRangersInformation() throws Exception{
        Ranger ranger = testRanger();
        rangerDao.add(ranger);
        rangerDao.update(ranger.getId(), "moli", "grasslands");
        Ranger newRanger = rangerDao.findById(ranger.getId());
        assertNotEquals(ranger.getRangerName(), newRanger.getRangerName());
        assertNotEquals(ranger.getRangerLocation(), newRanger.getRangerLocation());
    }

    @Test
    public void deleteByIdDeletesSingleRanger() throws Exception {
        Ranger ranger = testRanger();
        Ranger anotherRanger = new Ranger("Moli", "riverside");
        rangerDao.add(ranger);
        rangerDao.add(anotherRanger);
        rangerDao.deleteById(ranger.getId());
        assertEquals(1, rangerDao.getAll().size());
    }

    @Test
    public void deleteAllClearsAllRangers() throws Exception{
        Ranger ranger = testRanger();
        Ranger anotherRanger = new Ranger("Moli", "riverside");
        rangerDao.add(ranger);
        rangerDao.add(anotherRanger);
        rangerDao.clearAllRangers();
        assertEquals(0, rangerDao.getAll().size());
    }

    @Test
    public void getAllAnimalsScoutedBySpecificRangerReturnsAnimalCorrectly() throws Exception {
        Ranger ranger = testRanger();
        rangerDao.add(ranger);
        int rangerId = ranger.getId();
        Animal newAnimal = new Animal("Lion", rangerId);
        Animal newAnimal2 = new Animal("Simba", rangerId);
        Animal newAnimal3 = new Animal("Tiger", rangerId);
        animalDao.add(newAnimal);
        animalDao.add(newAnimal2);
        assertEquals(2, rangerDao.getAllAnimalsScoutedByRanger(rangerId).size());
        assertTrue(rangerDao.getAllAnimalsScoutedByRanger(rangerId).contains(newAnimal));
        assertTrue(rangerDao.getAllAnimalsScoutedByRanger(rangerId).contains(newAnimal2));
        assertFalse(rangerDao.getAllAnimalsScoutedByRanger(rangerId).contains(newAnimal3));
    }
}