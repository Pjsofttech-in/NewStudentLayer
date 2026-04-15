package Layer.NewStudentManagement.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ No token or invalid header.");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            // Validate token first
            if (!jwtUtil.validateToken(token)) {
                System.out.println("❌ Invalid Token: Token validation failed");
                filterChain.doFilter(request, response);
                return;
            }

            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token);
            String branchCode = jwtUtil.extractBranchCode(token);

            // Email is required, but role and branchCode can be null
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Default role if not present in token
                String roleToUse = role != null ? role : "USER";
                
                UserDetails userDetails = User.withUsername(email)
                        .password("")
                        .roles(roleToUse)
                        .build();

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // Store extra data in details
                Map<String, Object> details = new java.util.HashMap<>();
                details.put("email", email);
                details.put("role", roleToUse);
                if (branchCode != null) {
                    details.put("branchCode", branchCode);
                }
                authToken.setDetails(details);

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        } catch (Exception e) {
            System.out.println("❌ Invalid Token: " + e.getMessage());
            // Clear authentication on error
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
