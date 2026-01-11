package gr.teipir.studyrooms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller για την αρχική σελίδα της εφαρμογής.
 * Εδώ απλώς επιστρέφουμε το template του Home.
 */
@Controller
public class HomeController {

    /**
     * GET /
     * Επιστρέφουμε το index.html από τα templates.
     */
    @GetMapping("/")
    public String home() {
        // Επιστρέφουμε το όνομα του template της αρχικής σελίδας
        return "index"; // θα ψάξει το index.html στα templates
    }
}


