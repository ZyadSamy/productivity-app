package com.barmjz.productivityapp.pomodoro;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pomo")
@AllArgsConstructor
@CrossOrigin
public class PomodoroController {
    private final PomodoroService pomoService;
    private final PomodoroUserProxy proxy;

    @GetMapping("/")
    public ResponseEntity<Pomodoro> get() {
        try {
            System.out.println("Get pomo called");
            return ResponseEntity.ok(pomoService.get(proxy.getCurrentUser()));
        } catch (Exception e) {
            System.out.println("----" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/")
    public ResponseEntity<String> start() {
        try {
            return ResponseEntity.ok(pomoService.start(proxy.getCurrentUser()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("{remainingTime}/pause")
    public ResponseEntity<String> pause(@PathVariable Long remainingTime) {
        try {
            return ResponseEntity.ok(pomoService.pause(remainingTime,proxy.getCurrentUserId()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/resume")
    public ResponseEntity<String> resume() {
        try {
            return ResponseEntity.ok(pomoService.resume(proxy.getCurrentUserId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/endStudy")
    public ResponseEntity<String> endStudy() {
        try {
            return ResponseEntity.ok(pomoService.endStudy(proxy.getCurrentUserId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{studyTime}/{breakTime}")
    public ResponseEntity<String> set(@PathVariable int studyTime,@PathVariable int breakTime) {
        try {
            return ResponseEntity.ok(pomoService.set(studyTime,breakTime,proxy.getCurrentUserId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<String> reset() {
        try {
            return ResponseEntity.ok(pomoService.reset(proxy.getCurrentUser()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}