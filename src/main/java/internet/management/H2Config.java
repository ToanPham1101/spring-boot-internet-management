package internet.management;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class H2Config {

    private static final Logger log = LoggerFactory.getLogger(H2Config.class);

    @PostConstruct
    public void startDbManager() {
        try {
            Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092").start();
        } catch (SQLException e) {
            log.warn("H2 TCP server could not start (port 9092 may be in use): {}", e.getMessage());
        }
    }
}
