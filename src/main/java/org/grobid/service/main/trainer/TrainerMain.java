package org.grobid.service.main.trainer;

import org.grobid.core.main.LibraryLoader;
import org.grobid.core.mock.MockContext;
import org.grobid.core.utilities.GrobidProperties;
import org.grobid.core.utils.SmectaProperties;
import org.grobid.trainer.AstroTrainer;

public class TrainerMain {

	public static void main(String[] args) {

		try {
			String grobidHome = SmectaProperties.get("grobid.home");
			String grobidProperties = SmectaProperties.get("grobid.properties");

			MockContext.setInitialContext(grobidHome, grobidProperties);
			GrobidProperties.getInstance();

			LibraryLoader.load();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		// Make your trainer extending from SmectaAbstractTrainer
		AstroTrainer trainer = new AstroTrainer();
		
		try {			
			TrainerService.runTrainer(args, trainer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
