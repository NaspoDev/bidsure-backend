package dev.naspo.bidsure_auction_service;

import dev.naspo.bidsure_auction_service.models.Auction;
import dev.naspo.bidsure_auction_service.models.User;
import jakarta.annotation.PreDestroy;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Service;

// Manages the Hibernate SessionFactory.
@Service
public class HibernateManager {

    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = buildSessionFactory();
        }
        return sessionFactory;
    }

    // Creates and configures the SessionFactory with db info, HikariCP, and other.
    private SessionFactory buildSessionFactory() {
        // Building the JDBC connection url from environment variables.
        String JDBC_URL = "jdbc:mysql://"
                + System.getenv("DATABASE_HOST") + ":"
                + System.getenv("DATABASE_PORT") + "/"
                + System.getenv("DATABASE_NAME");

        return new Configuration()
                // DB related properties
                .setProperty(AvailableSettings.JAKARTA_JDBC_URL, JDBC_URL)
                .setProperty(AvailableSettings.JAKARTA_JDBC_USER, System.getenv("DATABASE_USER"))
                .setProperty(AvailableSettings.JAKARTA_JDBC_PASSWORD, System.getenv("DATABASE_PASSWORD"))
                .setProperty(AvailableSettings.CONNECTION_PROVIDER, "com.zaxxer.hikari.hibernate.HikariConnectionProvider")
                // SQL logging properties
                .setProperty(AvailableSettings.SHOW_SQL, true)
                .setProperty(AvailableSettings.FORMAT_SQL, true)
                .setProperty(AvailableSettings.HIGHLIGHT_SQL, true)
                .addAnnotatedClass(Auction.class)
                .addAnnotatedClass(User.class)
                .buildSessionFactory();
    }

    @PreDestroy
    public void shutdown() {
        System.out.println("shutting down");
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}