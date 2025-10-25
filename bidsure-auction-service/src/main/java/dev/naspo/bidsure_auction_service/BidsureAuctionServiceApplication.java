package dev.naspo.bidsure_auction_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BidsureAuctionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BidsureAuctionServiceApplication.class, args);
	}

}
