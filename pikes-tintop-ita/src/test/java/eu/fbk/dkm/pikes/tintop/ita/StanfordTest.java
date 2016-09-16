package eu.fbk.dkm.pikes.tintop.ita;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

/**
 * Created by alessio on 26/02/15.
 */

public class StanfordTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(StanfordTest.class);

    private static void printOutput(Annotation annotation) {
        List<CoreMap> sents = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap thisSent : sents) {

            List<CoreLabel> tokens = thisSent.get(CoreAnnotations.TokensAnnotation.class);
            for (CoreLabel token : tokens) {
                System.out.println("Token: " + token);
                System.out.println("Index: " + token.index());
                System.out.println("Sent index: " + token.sentIndex());
                System.out.println("Begin: " + token.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
                System.out.println("End: " + token.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
                System.out.println("NER: " + token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
                System.out.println();
            }

            System.out.println("---");
            System.out.println();
        }
    }

    public static void main(String[] args) throws IOException {

        String text = "Donald Trump set off a fierce new controversy Tuesday with remarks about the right to bear arms that were interpreted by many as a threat of violence against Hillary Clinton.";

        Properties props;
        Annotation annotation;

        props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, ner");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        annotation = new Annotation(text);
        pipeline.annotate(annotation);
        System.out.println(pipeline.timingInformation());

        if (text.length() < 1000) {
            printOutput(annotation);
        }

    }
}
