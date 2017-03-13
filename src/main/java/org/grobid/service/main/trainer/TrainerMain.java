package org.grobid.service.main.trainer;

import org.grobid.trainer.AstroTrainer;

public class TrainerMain {

	public static void main(String[] args) {
		
		// Make your trainer extending from SmectaAbstractTrainer
		AstroTrainer trainer = new AstroTrainer();
		
		try {
			TrainerService.runTrainer(args, trainer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
