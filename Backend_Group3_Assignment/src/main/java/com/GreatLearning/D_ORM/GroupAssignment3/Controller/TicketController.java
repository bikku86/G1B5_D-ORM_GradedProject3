package com.GreatLearning.D_ORM.GroupAssignment3.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.GreatLearning.D_ORM.GroupAssignment3.Entity.Ticket;
import com.GreatLearning.D_ORM.GroupAssignment3.Exception.TicketNotFoundException;
import com.GreatLearning.D_ORM.GroupAssignment3.ServiceImplementation.TicketingServiceImplementation;

@Controller
public class TicketController {

	@Autowired
	TicketingServiceImplementation ticketService;

	@GetMapping("/")
	public ModelAndView redirectToHomepage() {
		return new ModelAndView("redirect:/homePage");
	}

	@GetMapping("/homePage")
	public String homePage(Model model) {
		try {
			List<Ticket> tickets = ticketService.getAllTickets();
			model.addAttribute("listOfTickets", tickets);
		} catch (Exception ex) {
			model.addAttribute("error", "Error loading tickets. Please try again later.");
		}
		return "tickets";
	}

	@GetMapping("/ticket/edit/{id}")
	public String editTicket(@PathVariable int id, Model model) {
		try {
			Ticket ticketObject = ticketService.getTicketById(id);
			if (ticketObject == null) {
				throw new TicketNotFoundException("Ticket with ID " + id + " not found.");
			}
			model.addAttribute("ticketId", ticketObject);
		} catch (TicketNotFoundException ex) {
			model.addAttribute("error", ex.getMessage());
			return "errorPage"; // Redirect to an error page
		} catch (Exception ex) {
			model.addAttribute("error", "Error editing ticket. Please try again later.");
			return "errorPage"; // Redirect to an error page
		}
		return "edit_ticket";
	}

	@PostMapping("/ticket/{id}")
	public String updateTicket(@PathVariable int id, @ModelAttribute("ticket") Ticket newValues) {
		try {
			Ticket ticket = ticketService.getTicketById(id);
			if (ticket == null) {
				throw new TicketNotFoundException("Ticket with ID " + id + " not found.");
			}
			ticket.setTitle(newValues.getTitle());
			ticket.setDescription(newValues.getDescription());
			ticket.setCreateDate(newValues.getCreateDate());
			ticket.setContent(newValues.getContent());
			ticketService.saveTicket(ticket);
		} catch (TicketNotFoundException ex) {
			return "redirect:/error?msg=" + ex.getMessage(); // Redirect with error message
		} catch (DataAccessException ex) {
			return "redirect:/error?msg=Database error occurred. Please try again later."; // Redirect with error
																							// message
		}
		return "redirect:/homePage";
	}

	@GetMapping("/ticket/new")
	public String createTicket(Model model) {
		try {
			Ticket ticket = new Ticket();
			model.addAttribute("blankTicket", ticket);
		} catch (Exception ex) {
			model.addAttribute("error", "Error creating ticket form. Please try again later.");
		}
		return "create_ticket";
	}

	@PostMapping("/newTicket")
	public String newTicket(@ModelAttribute("ticket") Ticket newTicket) {
		try {
			ticketService.saveTicket(newTicket);
		} catch (DataAccessException ex) {
			return "redirect:/error?msg=Database error occurred while creating ticket."; // Redirect with error message
		} catch (Exception ex) {
			return "redirect:/error?msg=An error occurred while creating ticket."; // Redirect with error message
		}
		return "redirect:/homePage";
	}

	@GetMapping("/ticket/delete/{id}")
	public String deleteTicket(@PathVariable int id) {
		try {
			ticketService.deleteTicketById(id);
		} catch (TicketNotFoundException ex) {
			return "redirect:/error?msg=" + ex.getMessage(); // Redirect with error message
		} catch (DataAccessException ex) {
			return "redirect:/error?msg=Database error occurred while deleting ticket."; // Redirect with error message
		}
		return "redirect:/homePage";
	}

	@GetMapping("/ticket/view/{id}")
	public String viewTicket(@PathVariable int id, Model model) {
		try {
			Ticket ticket = ticketService.getTicketById(id);
			if (ticket == null) {
				throw new TicketNotFoundException("Ticket with ID " + id + " not found.");
			}
			model.addAttribute("ticketDetail", ticket);
		} catch (TicketNotFoundException ex) {
			model.addAttribute("error", ex.getMessage());
			return "errorPage"; // Redirect to an error page
		} catch (Exception ex) {
			model.addAttribute("error", "Error loading ticket details. Please try again later.");
			return "errorPage"; // Redirect to an error page
		}
		return "viewTicketDetails";
	}

	@PostMapping("/ticket/search")
	public String searchTicket(@RequestParam("query") String query, Model model) {
		try {
			if (query.isEmpty()) {
				return "redirect:/homePage";
			}
			List<Ticket> tickets = ticketService.getAllTickets();
			List<Ticket> searchResults = null;

			for (Ticket t1 : tickets) {
				if (t1.getTitle().trim().equalsIgnoreCase(query)) {
					searchResults = ticketService.getTicketByTitle(query);
					break;
				} else if (t1.getDescription().trim().equalsIgnoreCase(query)) {
					searchResults = ticketService.getTicketByDescription(query);
					break;
				}
			}

			if (searchResults == null) {
				model.addAttribute("error", "No results found for query: " + query);
			} else {
				model.addAttribute("searchResults", searchResults);
			}
		} catch (Exception ex) {
			model.addAttribute("error", "Error occurred while searching for tickets.");
			return "errorPage"; // Redirect to an error page
		}

		return "searchresults";
	}

	// Exception handler for specific exceptions in this controller
	@ExceptionHandler(TicketNotFoundException.class)
	public String handleTicketNotFoundException(TicketNotFoundException ex, Model model) {
		model.addAttribute("error", ex.getMessage());
		return "errorPage"; // Custom error page
	}

	@ExceptionHandler(DataAccessException.class)
	public String handleDataAccessException(DataAccessException ex, Model model) {
		model.addAttribute("error", "Database error occurred. Please try again later.");
		return "errorPage"; // Custom error page
	}

	@ExceptionHandler(Exception.class)
	public String handleGeneralException(Exception ex, Model model) {
		model.addAttribute("error", "An unexpected error occurred. Please try again later.");
		return "errorPage"; // Custom error page
	}
}
