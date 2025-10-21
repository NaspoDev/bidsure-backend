package dev.naspo.bidsure_user_service;

import dev.naspo.bidsure_user_service.models.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static SessionFactory sessionFactory = buildSessionFactory();

    // Building the JDBC connection url from environment variables.
    private static final String JDBC_URL = "jdbc:mysql://"
            + System.getenv("DATABASE_HOST") + ":"
            + System.getenv("DATABASE_PORT") + "/"
            + System.getenv("DATABASE_NAME");

    private HibernateUtil() {
    }

    private static SessionFactory buildSessionFactory() {
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
                .addAnnotatedClass(User.class)
                .buildSessionFactory();
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
