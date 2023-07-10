package com.javaunit3.springmvc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.javaunit3.springmvc.model.MovieEntity;
import com.javaunit3.springmvc.model.VoteEntity;

@Controller
public class MovieController {

    private BestMovieService bestMovieService;
    private SessionFactory sessionFactory;

    @Autowired
    public MovieController(BestMovieService bestMovieService, SessionFactory sessionFactory) {
        this.bestMovieService = bestMovieService;
        this.sessionFactory = sessionFactory;
    }

    @RequestMapping("/")
    public String getIndexPage() {
        return "index";
    }

    @RequestMapping("/bestMovie")
    public String getBestMoviePage(Model model) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        List<MovieEntity> movieEntities = session.createQuery("from MovieEntity").list();

        if (movieEntities.size() == 0){
            model.addAttribute("bestMovie", "No Movies Yet");
            model.addAttribute("bestMovieVoters", "No Votes Yet");

            session.getTransaction().commit();
            
            return "bestMovie";
        }

        movieEntities.sort(Comparator.comparing(movieEntity  -> movieEntity.getVotes().size()));

        MovieEntity movieWithMostVotes = movieEntities.get(movieEntities.size() - 1);
        List<String> voterNames = new ArrayList<>();

        for (VoteEntity vote: movieWithMostVotes.getVotes()){
            voterNames.add(vote.getVoterName());
        }

        String voterNamesList = String.join(",", voterNames);

        model.addAttribute("bestMovie", movieWithMostVotes.getTitle());
        model.addAttribute("bestMovieVoters", voterNamesList);

        session.getTransaction().commit();
        
        return "bestMovie";
    }

    @RequestMapping("/voteForBestMovieForm")
    public String voteForBestMovieFormPage(Model model) {
        Session session = sessionFactory.getCurrentSession();

        session.beginTransaction();
    
        List<MovieEntity> movieEntityList = session.createQuery("from MovieEntity").list();
    
        session.getTransaction().commit();
    
        model.addAttribute("movies", movieEntityList);
    
        return "voteForTheBestMovie";
    }

    @RequestMapping("/voteForBestMovie")
    public String voteForBestMovie(HttpServletRequest request, Model model) {

        String movieId = request.getParameter("movieId");
        String voterName = request.getParameter("voterName");

        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        MovieEntity movieEntity = (MovieEntity) session.get(MovieEntity.class, Integer.parseInt(movieId));
        VoteEntity voteEntity = new VoteEntity();
        voteEntity.setVoterName(voterName);

        movieEntity.addVote(voteEntity);

        session.update(movieEntity);
        session.getTransaction().commit();

        return "voteForTheBestMovie";
    }

    @RequestMapping("/addMovieForm")
    public String addMovieForm() {
        return "addMovie";
    }

    @RequestMapping("/addMovie")
    public String addMovie(HttpServletRequest request, Model model) {

        String title = request.getParameter("movieTitle");
        String maturityRating = request.getParameter("maturityRating");
        String genre = request.getParameter("genre");
        
        MovieEntity movieEntity = new MovieEntity(title, maturityRating, genre);

        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.save(movieEntity);
        session.getTransaction().commit(); 

        return "addMovie";
    }
}
