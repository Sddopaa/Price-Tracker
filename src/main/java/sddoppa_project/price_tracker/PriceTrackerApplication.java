package sddoppa_project.price_tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PriceTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PriceTrackerApplication.class, args);
        System.out.println("""
            ╔══════════════════════════════════════════╗
            ║  "He who possesses the Spear of Destiny  ║
            ║   holds the fate of the world in his     ║
            ║   hands."                                ║
            ║                                          ║
            ║            Price Tracker v1.0            ║
            ╚══════════════════════════════════════════╝
        """);
	}

}
