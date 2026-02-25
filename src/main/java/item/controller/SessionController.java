package item.controller;

import item.model.SessionResult;
import item.service.SessionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/session")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/start/{userId}")
    public SessionResult startSession(@PathVariable Integer userId) {
        return sessionService.startSession(userId);
    }

    @PostMapping("/end/{userId}")
    public SessionResult endSession(@PathVariable Integer userId) {
        return sessionService.endSession(userId);
    }

    @GetMapping("/time-remaining/{userId}")
    public SessionResult.TimeRemainingResult getTimeRemaining(@PathVariable Integer userId) {
        return sessionService.getTimeRemaining(userId);
    }

    @GetMapping("/history/{userId}")
    public List<SessionResult> getSessionHistory(@PathVariable Integer userId) {
        return sessionService.getSessionHistory(userId);
    }
}

