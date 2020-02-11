import dao.Sql2oRangerDao;
import models.Animal;
import models.Ranger;
import dao.Sql2oAnimalDao;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.sql2o.Sql2o;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;
import static spark.Spark.*;

public class App {
    public static void main(String[] args) {
        staticFileLocation("/public");
        String connectionString = "jdbc:h2:~/wildlife_tracker.db;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        Sql2oAnimalDao animalDao = new Sql2oAnimalDao(sql2o);
        Sql2oRangerDao rangerDao = new Sql2oRangerDao(sql2o);

//        get: show all rangers
    get("/", (req, res) ->{
        Map<String, Object> model = new HashMap<>();
        List<Ranger> rangers = rangerDao.getAll();
        model.put("rangers", rangers);
        return new ModelAndView(model, "index.hbs");
    }, new HandlebarsTemplateEngine());

//    get: show all sighted animals
    get("/all/animals", (req, res) ->{
        Map<String, Object> model = new HashMap<>();
        List<Animal> animals = animalDao.getAll();
        model.put("animals", animals);
        return new ModelAndView(model, "all-animals.hbs");
    }, new HandlebarsTemplateEngine());

//    get: show new Ranger from
     get("/rangers/new", (req, res) ->{
         Map<String, Object> model = new HashMap<>();
         return new ModelAndView(model, "ranger-form.hbs");
     }, new HandlebarsTemplateEngine());

//     post: process new Ranger form
     post("/rangers/new", (req, res) ->{
         Map<String, Object> model = new HashMap<>();
         String rangerName = req.queryParams("rangerName");
         String rangerLocation = req.queryParams("rangerLocation");
         Ranger newRanger = new Ranger(rangerName, rangerLocation);
         rangerDao.add(newRanger);
         model.put("ranger", newRanger);
         return new ModelAndView(model, "success.hbs");
     }, new HandlebarsTemplateEngine());

//      get: show all animals sighted by a Ranger
        get("/rangers/:id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfRangerToView = Integer.parseInt(req.params("id"));
            Ranger foundRanger = rangerDao.findById(idOfRangerToView);
            model.put("ranger", foundRanger);
            List<Animal> allAnimalsByThisRanger = rangerDao.getAllAnimalsScoutedByRanger(idOfRangerToView);
            model.put("animals", allAnimalsByThisRanger);
            model.put("rangers", rangerDao.getAll());
            return new ModelAndView(model, "ranger-detail.hbs");
        }, new HandlebarsTemplateEngine());

//        show new animal-form

        get("animals/new", (req, res) ->{
            Map<String, Object> model = new HashMap<>();
            List<Ranger> allRangers = rangerDao.getAll();
            model.put("rangers", allRangers);
            return new ModelAndView(model, "animal-form.hbs");
        }, new HandlebarsTemplateEngine());

//        post: process new animal-form
        post("/animals/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Ranger> allRangers = rangerDao.getAll();
            model.put("rangers", allRangers);
            String animalName = req.queryParams("animalName");
            int rangerId = Integer.parseInt(req.queryParams("rangerId"));
            Animal newAnimal = new Animal(animalName, rangerId );
            animalDao.add(newAnimal);
            return new ModelAndView(model, "success.hbs");
        }, new HandlebarsTemplateEngine());


    }
}