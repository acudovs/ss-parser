package ss.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.yaml.snakeyaml.Yaml;

import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RssRegexTest {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: testRegex -PadType=<type> [-Pregex=<regex>]");
            System.err.println("  type: car, flat-sell, flat-rent, home");
            System.exit(1);
        }

        String adType = args[0];

        Yaml yaml = new Yaml();
        Map<String, Object> root = yaml.load(Files.newInputStream(Path.of("src/main/resources/application.yml")));
        Map<String, Object> ssParser = (Map<String, Object>) root.get("ss-parser");
        Map<String, Object> adConfig = (Map<String, Object>) ssParser.get(adType);

        if (adConfig == null) {
            System.err.println("Unknown ad type: " + adType + ". Available: car, flat-sell, flat-rent, home");
            System.exit(1);
        }

        String url = (String) adConfig.get("url");
        String regex = args.length > 1 ? args[1] : (String) adConfig.get("regex");
        int timeoutMs = parseTimeout((String) adConfig.get("timeout"));

        System.out.println("URL:   " + url);
        System.out.println("Regex: " + regex);
        System.out.println();

        List<String> groupNames = new ArrayList<>();
        Matcher groupFinder = Pattern.compile("\\(\\?<(\\w+)>").matcher(regex);
        while (groupFinder.find()) {
            groupNames.add(groupFinder.group(1));
        }

        URLConnection con = new URL(url).openConnection();
        con.setConnectTimeout(timeoutMs);
        con.setReadTimeout(timeoutMs);
        con.setRequestProperty("User-Agent", System.getenv().getOrDefault("HTTP_AGENT",
                "Java/" + System.getProperty("java.version")));

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(con.getInputStream());
        NodeList items = doc.getDocumentElement().getElementsByTagName("item");

        Pattern pattern = Pattern.compile(regex);
        int matched = 0;

        for (int i = 0; i < items.getLength(); i++) {
            Element item = (Element) items.item(i);
            String title = item.getElementsByTagName("title").item(0).getTextContent();
            String description = item.getElementsByTagName("description").item(0).getTextContent();
            Matcher matcher = pattern.matcher(description);

            System.out.println("Item: " + title);
            if (matcher.find()) {
                matched++;
                for (String group : groupNames) {
                    System.out.println("  " + group + ": " + matcher.group(group));
                }
            } else {
                System.out.println("  NO MATCH");
                System.out.println("  " + description);
            }
            System.out.println();
        }

        System.out.println("Matched: " + matched + "/" + items.getLength());
    }

    private static int parseTimeout(String timeout) {
        if (timeout == null) return 10_000;
        if (timeout.endsWith("s")) return Integer.parseInt(timeout.replace("s", "")) * 1000;
        if (timeout.endsWith("m")) return Integer.parseInt(timeout.replace("m", "")) * 60_000;
        return Integer.parseInt(timeout);
    }
}
