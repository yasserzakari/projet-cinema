package org.sid.cinema.web;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.http.MediaType;
import org.sid.cinema.dao.FilmRepository;
import org.sid.cinema.dao.TicketRepository;
import org.sid.cinema.entities.Film;
import org.sid.cinema.entities.Ticket;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
public class CinemaRestController {
	@Autowired
	private FilmRepository filmRepository;
	@Autowired
	private TicketRepository ticketRepository;
	
 @GetMapping(path = "/imageFilm/{id}", produces =MediaType.IMAGE_JPEG_VALUE)	
// Tableau de Byte pour les Images
public byte[] image(@PathVariable (name="id")Long id) throws Exception {

	Film f=filmRepository.findById(id).get();
	String photoName=f.getPhoto();
	File file=new File(System.getProperty("user.home")+"/cinema/images/"+photoName);
	Path path=Paths.get(file.toURI());
	return Files.readAllBytes(path);
	
}
 @PostMapping("/payerTickets")
 @Transactional
 // les données de tickets sont envoyé au format JSON à l'aide de l'annotation RequestBody
 public List<Ticket> payerTicket(@RequestBody TicketFrom ticketFrom) {
	 List<Ticket> listTickets=new ArrayList<>();
	 ticketFrom.getTickets().forEach(idTicket->{
		// System.out.println(idTicket);
		 Ticket ticket =ticketRepository.findById(idTicket).get();
		 ticket.setNomClient(ticketFrom.getNomClient());
		 ticket.setReserve(true);
		 ticket.setCodePayement(ticketFrom.getCodePayement());
		 ticketRepository.save(ticket);
		 listTickets.add(ticket);
	 });
	 return listTickets;
 }
}
@Data
class TicketFrom{
	
	private String nomClient;
    private int codePayement;
    private List<Long> tickets=new ArrayList<>();
    
}

