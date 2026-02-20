package vn.id.luannv.auth_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AuditClient {
    
    private static final Logger logger = LoggerFactory.getLogger(AuditClient.class);
    
    @Value("${audit.service.url:http://localhost:8082}")
    private String auditServiceUrl;
    
    private final RestTemplate restTemplate;
    
    public AuditClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public void logAudit(AuditLogRequest request) {
        if (request == null) {
            return;
        }
        
        try {
            String url = auditServiceUrl + "/api/v1/audits";
            restTemplate.postForObject(url, request, Void.class);
            logger.debug("Audit logged: {} - {}", request.getSourceService(), request.getActionType());
        } catch (Exception e) {
            logger.warn("Failed to log audit: {} - {}", request.getSourceService(), e.getMessage());
        }
    }
}
