package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.Entity.PaymentGatewayAccountResponceDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ClientAdminPaymentGatewayService {
    private final WebClient webClient;

    @Autowired
    public ClientAdminPaymentGatewayService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<List<PaymentGatewayAccountResponceDTO>> getPaymentGatewayDetails(String branchCode) {
        try {
            HttpServletRequest request =
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

            String token = request.getHeader(HttpHeaders.AUTHORIZATION);
            return webClient.get().uri(uriBuilder -> uriBuilder
                            .path("/getAccountsForPaymentGateway")
                            .queryParam("branchCode", branchCode)
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, token)  // pass it as-is
                    .retrieve().
                    onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
                            .flatMap(error -> Mono.error(new RuntimeException("Payment Gateway Details Fetching Failed: " + error))))
                .bodyToMono(new ParameterizedTypeReference<List<PaymentGatewayAccountResponceDTO>>() {});
        }
        catch (Exception e){
            return Mono.error(e);
        }
    }
}
