package org.sid.cinema.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import java.util.Date;
import java.util.List;

import org.sid.cinema.dao.CategorieRepository;
import org.sid.cinema.dao.CinemaRepository;
import org.sid.cinema.dao.FilmRepository;
import org.sid.cinema.dao.PlaceRepository;
import org.sid.cinema.dao.ProjectionRepository;
import org.sid.cinema.dao.SalleRepository;
import org.sid.cinema.dao.SeanceRepository;
import org.sid.cinema.dao.TicketRepository;
import org.sid.cinema.dao.VilleRepository;
import org.sid.cinema.entities.Categorie;
import org.sid.cinema.entities.Cinema;
import org.sid.cinema.entities.Film;
import org.sid.cinema.entities.Place;
import org.sid.cinema.entities.Projection;
import org.sid.cinema.entities.Salle;
import org.sid.cinema.entities.Seance;
import org.sid.cinema.entities.Ticket;
import org.sid.cinema.entities.Ville;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sun.el.parser.ParseException;


@Service
@Transactional
public  class CinemaInitServiceImpl implements ICinemainitService {
	@Autowired
	private VilleRepository VilleRepository;
	@Autowired
	private CinemaRepository cinemaRepository;
	@Autowired
	private SalleRepository SalleRepository;
	@Autowired
	private PlaceRepository PlaceRepository;
	@Autowired
	private SeanceRepository SeanceRepository;
	@Autowired
	private FilmRepository FilmRepository;
	@Autowired
	private ProjectionRepository ProjectionRepository;
	@Autowired
	private CategorieRepository CategorieRepository;
	@Autowired
	private TicketRepository TicketRepository;
	
	
	@Override
	
	public void initVilles() {
		Stream.of("Casablanca","Marrakech","Rabat","Tanger").forEach(nameVille->{
			Ville ville=new Ville();
			ville.setName(nameVille);
			VilleRepository.save(ville);
		});
		
	}

	
	public void initCinemas() {
		
		VilleRepository.findAll().forEach(v->{
			Stream.of("MegaRama","IMAX","FOUNOUN","CHAHRAZAD","DAOULIZ")
			.forEach(nameCinema->{
				Cinema cinema=new Cinema();
				cinema.setName(nameCinema);
				cinema.setNamberSalles(6+(int)Math.random()*7);
				cinema.setVille(v);
				cinemaRepository.save(cinema);
			
		});
		});
	}

	@Override
	public void initSalles() {
		cinemaRepository.findAll().forEach(cinema->{
		for(int i=0;i<cinema.getNamberSalles();i++) {
			Salle salle=new Salle();
			salle.setName("salle"+(i+1));
			salle.setCinema(cinema);
			salle.setNombrePlace(15+(int)(Math.random()*20));
			SalleRepository.save(salle);
			
		}
	});
}

	@Override
	public void initPlaces() {
		SalleRepository.findAll().forEach(salle->{
			for(int i=0;i<salle.getNombrePlace();i++) {
				Place place =new Place();
				place.setNumero(i+1);
				place.setSalle(salle);
				PlaceRepository.save(place);
			}
		});
		
	}

	@Override
	public void initSeances() {
		DateFormat dateFormat=new SimpleDateFormat("HH:mm");
		Stream.of("12:00","15:00","17:00","19:00","21:00").forEach(s->{
			Seance seance=new Seance();
			try {
				seance.setHeureDebut(dateFormat.parse(s));
				SeanceRepository.save(seance);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
	}

	@Override
	public void initCategories() {
		Stream.of("Histoire","Actions","Fiction","Drama").forEach(cat->{
			Categorie categorie=new Categorie();
			categorie.setName(cat);
			CategorieRepository.save(categorie);
		});
		
	}

	
	public void initfilms() {
		double[]durees= new double[] {1,1.5,2,2.5,3};
		List<Categorie> categories=CategorieRepository.findAll();
		Stream.of("12 hommes en colère","Forrest gump","greenbook","la ligne verte","le Parrain","seigneurdesanneaux","spiderman").forEach(titreFilm->{
			Film film = new Film();
			film.setTitre(titreFilm);
			film.setDuree(durees[new Random().nextInt(durees.length)]);
			film.setPhoto(titreFilm.replaceAll(" ", " ")+".jpg");
			film.setCategorie(categories.get(new Random().nextInt(categories.size())));
			FilmRepository.save(film);
		});
		
	}

	@Override
	public void initProjections() {
		double[] prices=new double[] {30,50,60,70,90,100};
		List<Film> films =FilmRepository.findAll();
		VilleRepository.findAll().forEach(ville->{
			
			ville.getCinemas().forEach(cinema->{
				cinema.getSalles().forEach(salle->{
					int index = new Random().nextInt(films.size());
					Film film= films.get(index);
					//FilmRepository.findAll().forEach(film->{
						SeanceRepository.findAll().forEach(seance->{
							Projection projection = new Projection();
							projection.setDateProjection(new Date());
							projection.setFilm(film);
							projection.setPrix(prices[new Random().nextInt(prices.length)]);
							projection.setSalle(salle);
							projection.setSeance(seance);
							ProjectionRepository.save(projection);
						});
					
					});
				});
			});
	
		
	}

	@Override
	public void initTickets() {
		ProjectionRepository.findAll().forEach(p->{
			p.getSalle().getPlaces().forEach(place->{
				Ticket ticket = new Ticket();
				ticket.setPlace(place);
				ticket.setPrix(p.getPrix());
				ticket.setProjection(p);
				ticket.setReserve(false);
				TicketRepository.save(ticket);
			});
		});
		
	}

}
