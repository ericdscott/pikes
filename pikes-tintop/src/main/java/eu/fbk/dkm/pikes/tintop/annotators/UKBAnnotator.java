package eu.fbk.dkm.pikes.tintop.annotators;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.util.ArraySet;
import edu.stanford.nlp.util.CoreMap;
import eu.fbk.dkm.pikes.tintop.annotators.models.UKBModel;
import eu.fbk.dkm.pikes.tintop.annotators.raw.UKB_MT;
import eu.fbk.utils.core.PropertiesUtils;

import java.io.IOException;
import java.util.*;

/**
 * Created by alessio on 06/05/15.
 */

public class UKBAnnotator implements Annotator {

	private UKB_MT tagger;
	int maxLen;

	public UKBAnnotator(String annotatorName, Properties props) {
		Properties newProps = PropertiesUtils.dotConvertedProperties(props, annotatorName);
		maxLen = Defaults.getInteger(newProps.getProperty("maxlen"), Defaults.MAXLEN);
		try {
			tagger = UKBModel.getInstance(newProps).getTagger();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void annotate(Annotation annotation) {
		if (annotation.has(CoreAnnotations.SentencesAnnotation.class)) {
			for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
				List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
				if (maxLen > 0 && tokens.size() > maxLen) {
					continue;
				}

				ArrayList<HashMap<String, String>> terms = new ArrayList<>();
				for (CoreLabel token : tokens) {
					HashMap<String, String> term = new HashMap<>();

					term.put("simple_pos", token.get(PikesAnnotations.SimplePosAnnotation.class));
					term.put("lemma", token.get(CoreAnnotations.LemmaAnnotation.class));

					terms.add(term);
				}

				try {
					tagger.run(terms);
				} catch (IOException e) {
					e.printStackTrace();
				}

				for (int i = 0, sz = tokens.size(); i < sz; i++) {
					CoreLabel thisToken = tokens.get(i);
					String wn = terms.get(i).get("wordnet");
					thisToken.set(PikesAnnotations.UKBAnnotation.class, wn);
				}
			}
		}
		else {
			throw new RuntimeException("unable to find words/tokens in: " + annotation);
		}
	}

	@Override
	public Set<Requirement> requirementsSatisfied() {
		return Collections.singleton(PikesAnnotations.WORDNET_REQUIREMENT);
	}

	@Override
	public Set<Requirement> requires() {
		return Collections.unmodifiableSet(new ArraySet<Requirement>(TOKENIZE_REQUIREMENT, SSPLIT_REQUIREMENT, LEMMA_REQUIREMENT, PikesAnnotations.SIMPLEPOS_REQUIREMENT));
	}
}
