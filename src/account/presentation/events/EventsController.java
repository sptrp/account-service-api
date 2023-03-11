package account.presentation.events;

import account.business.model.entities.Event;
import account.persistence.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EventsController {

    @Autowired
    EventRepository eventRepository;

    @GetMapping("api/security/events")
    public List<Event> getEvents() {
        return eventRepository.findAllByOrderByIdAsc();
    }
}
