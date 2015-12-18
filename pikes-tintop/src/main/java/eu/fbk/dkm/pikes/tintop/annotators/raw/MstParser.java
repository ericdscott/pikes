package eu.fbk.dkm.pikes.tintop.annotators.raw;

import eu.fbk.dkm.pikes.tintop.annotators.DepParseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: alessio
 * Date: 21/07/14
 * Time: 17:15
 * To change this template use File | Settings | File Templates.
 */

public class MstParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(MstParser.class);
    private int port;
    private String server;

    public MstParser(String server, int port) {
        this.port = port;
        this.server = server;
    }

    public DepParseInfo tag(List<String> tokens, List<String> poss) throws Exception {
        if (tokens.size() != poss.size()) {
            LOGGER.error("The token and pos collections must have the same size");
            return null;
        }

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            String pos = poss.get(i);

            sb.append(token.trim().replaceAll("\\s+", ""));
            sb.append("_");
            sb.append(pos.trim().replaceAll("\\s+", ""));
            sb.append(" ");
        }

        return tag(sb.toString().trim());
    }

    synchronized public DepParseInfo tag(String text) throws Exception {

        String modifiedSentence;

        Socket clientSocket = new Socket(server, port);
        OutputStreamWriter writer = new OutputStreamWriter(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        writer.write(text + '\n' + '*' + '\n');
        writer.flush();

        HashMap<Integer, Integer> depParents = new HashMap<>();
        HashMap<Integer, String> depLabels = new HashMap<>();

        try {
            while ((modifiedSentence = inFromServer.readLine()) != null) {

                modifiedSentence = modifiedSentence.trim();
                if (modifiedSentence.length() == 0) {
                    continue;
                }

                String[] parts = modifiedSentence.split("\\s+");

                if (parts.length >= 8) {
                    int id = Integer.parseInt(parts[0]);
                    depParents.put(id, Integer.parseInt(parts[6]));
                    depLabels.put(id, parts[7]);
                }

//                int i = 0;
//                int id = 0;
//                while (i < parts.length) {
//
//                    int unit = i % 10;
//                    int dec = (i - unit) / 10;
//
//                    if (unit == 0) {
//                        id = Integer.parseInt(parts[i]);
//                    }
//
//                    if (unit == 6) {
//                        depParents.put(id, Integer.parseInt(parts[i]));
//                    }
//                    if (unit == 7) {
//                        depLabels.put(id, parts[i]);
//                    }
//                    i++;
//                }
            }
        } catch (Exception e) {
            LOGGER.error("Error in text: {}", text);
            e.printStackTrace();
        } finally {
            clientSocket.close();
        }

        return new DepParseInfo(depParents, depLabels);

//        System.out.println(Arrays.toString(parts));
//        System.out.println(depLabels);
//        System.out.println(depParents);

//        SemaforResponse response = mapper.readValue(modifiedSentence, SemaforResponse.class);

//        return null;

    }

    public static void main(String[] args) {

        MstParser mstParser = new MstParser("localhost", 8012);
        try {
            String text = "Andrija_NNP Mohorovičić_NNP and_CC the_DT Mohorovičić_NNP Discontinuity_NNP ._.";
            System.out.println(text);
            DepParseInfo tag = mstParser.tag(text);
            System.out.println(tag);
//            SemaforResponse tag = semafor.tag(text);
//            System.out.println(tag);

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
        }
    }
}
