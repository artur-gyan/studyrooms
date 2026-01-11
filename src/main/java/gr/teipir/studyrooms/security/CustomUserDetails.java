package gr.teipir.studyrooms.security;

import gr.teipir.studyrooms.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Κλάση που υλοποιεί το UserDetails για την Spring Security.
 * Χρησιμοποιούμε για να μετατρέψουμε το αντικείμενο User σε Security User.
 */
public class CustomUserDetails implements UserDetails {

    private final User user; // Ο χρήστης που συσχετίζεται με αυτό το UserDetails

    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * Επιστρέφουμε τους ρόλους του χρήστη ως GrantedAuthority.
     * Προσθέτουμε πάντα το prefix "ROLE_" σύμφωνα με το Spring Security.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = "ROLE_" + user.getRole().name();
        return List.of(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // Κωδικός χρήστη
    }

    @Override
    public String getUsername() {
        return user.getUsername(); // Όνομα χρήστη
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Πάντα ενεργός, δεν χρησιμοποιούμε expiration
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Ποτέ δεν κλειδώνουμε λογαριασμό
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Τα credentials δεν λήγουν
    }

    @Override
    public boolean isEnabled() {
        return true; // Ο χρήστης είναι ενεργός
    }

    /**
     * Επιστρέφουμε το αντικείμενο User που συσχετίσαμε
     */
    public User getUser() {
        return user;
    }
}


