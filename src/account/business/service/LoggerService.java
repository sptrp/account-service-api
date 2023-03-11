package account.business.service;

import account.business.model.entities.Event;
import account.persistence.EventRepository;
import account.util.enums.EventAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoggerService {

    @Autowired
    EventRepository eventRepository;

    public void logEvent(EventAction action, String subject, String object, String path) {
        Event event = new Event(action, subject, object, path);

        eventRepository.save(event);
    }
}
