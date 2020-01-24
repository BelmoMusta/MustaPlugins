package musta.belmo.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextUtils {

    public static final boolean UPPER_CASE = true;
    public static final boolean LOWER_CASE = false;
    private static final String CAMELCASE_REGEX = "(?<!(^|[A-Z\\d]))((?=[A-Z\\d])|[A-Z](?=[\\d]))|(?<!^)(?=[A-Z\\d][a-z])";
    private static final String SYMBOLS_REGEX = "[^\\p{L}\\p{Nd} ]+";


    public static String capitalizeEachWord(String text) {
        StringBuilder sb = new StringBuilder();
        Stream.of(text.split("\\s+")).forEach(str -> sb.append(capitalize(str)).append(' '));
        return sb.toString().trim();
    }

    public static String changeCase(String text, boolean upper) {
        return Optional.ofNullable(text)
                .filter(str -> upper)
                .map(String::toUpperCase)
                .orElseGet(() ->
                        Optional.ofNullable(text)
                                .map(String::toLowerCase)
                                .orElse(null));

    }

    public static String capitalize(String text) {
        return Optional.ofNullable(text)
                .map(string -> Character.toUpperCase(string.charAt(0)) + string.substring(1))
                .orElse(null);
    }

    public static String uncapitalize(String text) {
        return Optional.ofNullable(text)
                .map(string -> Character.toLowerCase(string.charAt(0)) + string.substring(1))
                .orElse(null);
    }

    public static String deleteEmptyLines(String inputText) {
        return inputText.replaceAll("(?m)^[ \t]*\r?\n", "");
    }

    public static String camelCase(String input) {
        return Arrays.stream(input.split("\\s+")).reduce((a, b) ->
                StringUtils.capitalize(a) + StringUtils.capitalize(b)).get();
    }

    public static String reduceWhiteSpaces(String input) {
        String ret = input;
        String[] whiteSpaceRegex = {" {2,}", "\\t{2,}", "\\n{2,}", "\\r{2,}"};
        String[] replacement = {" ", "\t", "\n", "\r"};

        for (int i = 0; i < whiteSpaceRegex.length; i++) {
            ret = ret.replaceAll(whiteSpaceRegex[i], replacement[i]);
        }
        return ret;
    }

    public static List<HighlightPosition> getHighlights(String inputText, String regex) {
        List<HighlightPosition> highlightPositions = new ArrayList<>();
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(inputText);

        while (m.find()) {
            HighlightPosition highlightPosition = new HighlightPosition(m.start(), m.end());
            highlightPositions.add(highlightPosition);
        }
        return highlightPositions;
    }

    public static String encode64(String input) {
        return new String(Base64.getEncoder().encode(input.getBytes()));

    }

    public static String decode64(String input) {
        return new String(Base64.getDecoder().decode(input.getBytes()));
    }

    public static String indent(String input) {
        final StringBuilder sb = new StringBuilder();
        Scanner scanner = new Scanner(input);
        while (scanner.hasNextLine()) {

            sb.append('\t')
                    .append(scanner.nextLine()).append('\n');
        }
        scanner.close();
        return sb.toString();

    }

    public static String splitCamelCase(String input) {
        return Stream.of(input.split(CAMELCASE_REGEX))
                .map(TextUtils::uncapitalize)
                .collect(Collectors.joining(" "));
    }

    public static String replaceAccentedLetters(String input) {
        String string = Normalizer.normalize(input, Normalizer.Form.NFD);
        string = string.replaceAll("[^\\p{ASCII}]", "");
        return string;
    }

    public static String deleteSymbols(String input) {
        return TextLinesUtils.delete(input, SYMBOLS_REGEX);
    }


    public static String randomString(int length) {
        return randomString(length, true, true, true);
    }

    private static String randomString(int length,
                                       boolean withNumeric,
                                       boolean withLowerCase,
                                       boolean withUpperCase) {
        StringBuilder sb = new StringBuilder();

        while (sb.length() < length) {
            char randomUpperCaseChar = (char) ('A' + new Random().nextInt('Z' - 'A'));
            char randomLowerCaseChar = (char) ('a' + new Random().nextInt('z' - 'a'));
            char randomNumChar = (char) ('0' + new Random().nextInt('9' - '0'));


            int randomChoice = new Random().nextInt(3);

            if (withUpperCase && randomChoice % 3 == 0)
                sb.append(randomUpperCaseChar);
            else if (withLowerCase && randomChoice % 3 == 1)
                sb.append(randomLowerCaseChar);
            else if (withNumeric && randomChoice % 3 == 2)
                sb.append(randomNumChar);
        }
        return sb.toString();
    }

    public static String formatXML(String text) {
        try {
            Source xmlInput = new StreamSource(new StringReader(removeCommentsFromXML(text)));
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 4);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString();
        } catch (TransformerException e) {
            throw new RuntimeException(e); // simple exception handling, please review it
        }

    }


    public static String formatJSON(String input) {
        final String json;
        Object json_ = new JSONTokener(input).nextValue();
        if (json_ instanceof JSONObject) {
            json = new LinkedJSONObject(input).toString(2);
        } else if (json_ instanceof JSONArray) {
            json = new JSONArray(input).toString(2);
        } else {
            json = input;
        }

        return json;
    }

    public static String jsonDiff(String jsonA, String jsonB) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNodeA = mapper.readTree(jsonA);
            JsonNode jsonNodeB = mapper.readTree(jsonB);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    private static String removeCommentsFromXML(String text) {
        return text.replaceAll("<!--.*-->", "");
    }

    public static String deleteDuplicateLines(String text) {
        final Set<String> setOfLines = new LinkedHashSet<>();
        final Scanner sc = new Scanner(text);
        while (sc.hasNextLine()) {
            final String nextLine = sc.nextLine();
            setOfLines.add(nextLine);
        }
        return String.join("\n", setOfLines);
    }

    public static String unicodify(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char ch = string.charAt(i);
            if (ch >= 127) {
                String hexDigits = Integer.toHexString(ch);
                String escapedCh = "\\u" + "0000".substring(hexDigits.length()) + hexDigits;
                stringBuilder.append(escapedCh);
            } else {
                stringBuilder.append(ch);
            }
        }
        return stringBuilder.toString();
    }
}
