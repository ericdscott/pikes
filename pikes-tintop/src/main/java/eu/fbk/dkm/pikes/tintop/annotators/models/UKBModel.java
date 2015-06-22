package eu.fbk.dkm.pikes.tintop.annotators.models;

import eu.fbk.dkm.pikes.tintop.annotators.raw.UKB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Created by alessio on 27/05/15.
 */

public class UKBModel {

	private static UKBModel instance;
	private UKB tagger;
	private static final Logger LOGGER = LoggerFactory.getLogger(UKBModel.class);

	private UKBModel(Map properties) throws IOException {
		tagger = new UKB(properties);
	}

	public static UKBModel getInstance(Map properties) throws IOException {
		if (instance == null) {
			LOGGER.info("Loading model for UKB");
			instance = new UKBModel(properties);
		}

		return instance;
	}

	public UKB getTagger() {
		return tagger;
	}

}
