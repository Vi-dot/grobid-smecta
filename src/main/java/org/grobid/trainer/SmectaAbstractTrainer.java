package org.grobid.trainer;

import java.io.File;

import org.grobid.core.GrobidModel;
import org.grobid.core.utilities.GrobidProperties;
import org.grobid.trainer.evaluation.EvaluationUtilities;

public abstract class SmectaAbstractTrainer extends AbstractTrainer {

	public SmectaAbstractTrainer(final GrobidModel model) {
		super(model);
	}
	
	public void setParams(double epsilon, int window, int nbMaxIterations) {
		this.epsilon = epsilon;
		this.window = window;
		this.nbMaxIterations = nbMaxIterations;
	}
	
	public String splitTrainEvaluate(Double split, boolean random) {
    	System.out.println("Paths :\n"+getCorpusPath()+"\n"+GrobidProperties.getModelPath(model).getAbsolutePath()+"\n"+getTempTrainingDataPath().getAbsolutePath()+"\n"+getTempEvaluationDataPath().getAbsolutePath()+" \nrand "+random);
        
        File trainDataPath = getTempTrainingDataPath();
        File evalDataPath = getTempEvaluationDataPath();
        
        final File dataPath = trainDataPath;
        createCRFPPData(getCorpusPath(), dataPath, evalDataPath, split);
        GenericTrainer trainer = TrainerFactory.getTrainer();

        if (epsilon != 0.0) 
            trainer.setEpsilon(epsilon);
        if (window != 0)
            trainer.setWindow(window);
        if (nbMaxIterations != 0)
            trainer.setNbMaxIterations(nbMaxIterations);
        
        final File tempModelPath = new File(GrobidProperties.getModelPath(model).getAbsolutePath() + NEW_MODEL_EXT);
        final File oldModelPath = GrobidProperties.getModelPath(model);

        trainer.train(getTemplatePath(), dataPath, tempModelPath, GrobidProperties.getNBThreads(), model);

        // if we are here, that means that training succeeded
        renameModels(oldModelPath, tempModelPath);

        return EvaluationUtilities.evaluateStandard(evalDataPath.getAbsolutePath(), getTagger());
    }

}
