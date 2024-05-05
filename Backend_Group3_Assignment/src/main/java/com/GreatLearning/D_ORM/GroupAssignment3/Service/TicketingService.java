package com.GreatLearning.D_ORM.GroupAssignment3.Service;

import java.util.List;

import com.GreatLearning.D_ORM.GroupAssignment3.Entity.Ticket;

public interface TicketingService {

	public List<Ticket> getAllTickets();

	public Ticket getTicketById(int id);

	public void saveTicket(Ticket newTicket);

	public void deleteTicketById(int id);

	public List<Ticket> getTicketByTitle(String title);

	public List<Ticket> getTicketByDescription(String description);

}
