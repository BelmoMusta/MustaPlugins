package musta.belmo.plugins.util;

import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class TextUtils {
    /**
     * uncapitalize the inout String
     *
     * @param input @link String}
     * @return String
     */
    public static String capitalize(String input) {
        String output = input;
        if (input != null && !input.isEmpty()) {
            output = Character.toUpperCase(input.charAt(0)) + input.substring(1);
        }
        return output;
    }

    public static Collection<String> capitalize(Collection<String> input) {
        return input.stream()
                .map(TextUtils::capitalize)
                .toList();
    }

    public static String getPath(String urlString) {
        int index = urlString.indexOf('?');
        if (index > 0) {
            return urlString.substring(0, index);
        }
        return urlString;
    }
    public static String getQuery(String urlString) {
        urlString = urlString.replaceAll("[{}]", "");
        URI url = URI.create(urlString);
        return url.getQuery();
    }
    public static Map<String, String> getQueryParams(String urlString) {
        urlString = urlString.replaceAll("[{}]", "");
        URI url = URI.create(urlString);
        Map<String, String> queryPairs = new LinkedHashMap<>();
        Optional.ofNullable(url.getQuery())
                .map(query -> query.split("&"))
                .stream().flatMap(Stream::of)
                .forEach(pair -> {
                    int idx = pair.indexOf("=");
                    try {
                        queryPairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                                URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
                    } catch (Exception ignored) {

                    }
                });
        return queryPairs;
    }
    public static Map<String, String> getMappedPathVariables(String url) {
        final String mappedVariables = mapPathVariablesToTheirResources(url);
        final List<String> originalPathVariables = getPathVariables(url);
        final List<String> mappedPathVariables = getPathVariables(mappedVariables);
        final Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < originalPathVariables.size(); i++) {
            if (map.containsKey(originalPathVariables.get(i))) {
                map.put("_incorrect_ boolkB" + mappedPathVariables.get(i), mappedPathVariables.get(i));
            } else {
                map.put(originalPathVariables.get(i), mappedPathVariables.get(i));
            }
        }
        return map;
    }

    public static List<String> getPathVariables(String string) {
        final String regex = "(?<=\\{)(.+?)(?=\\})";// take every {xxxx} element
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(string);
        List<String> pathVariables = new ArrayList<>();
        while (matcher.find()) {
            pathVariables.add(matcher.group(0));
        }
        return pathVariables;
    }

    public static String mapPathVariablesToTheirResources(String url) {
        boolean startsWithSlash = url.startsWith("/");
        List<String> elements = Stream.of(url.split("/"))
                .filter(str -> !str.isEmpty())
                .toList();
        if (elements.size() <= 1 || elements.size() == 2) {
            return url;
        }

        List<String> urlParts = new ArrayList<>();
        String commonPrevious = "";
        urlParts.add(elements.get(0));
        for (int i = 1; i < elements.size(); i++) {
            String previous = elements.get(i - 1);
            if (!previous.contains("{")) {
                commonPrevious = previous;
            }
            String current = elements.get(i);
            if (!current.toLowerCase().contains(previous.toLowerCase()) &&
                    current.contains("{")) {
                current = "{" +
                        commonPrevious + TextUtils.capitalize(current.replaceAll("[{}]", ""))
                        + "}";
            }
            urlParts.add(current);
        }
        String joinedUrlParts = String.join("/", urlParts);
        if (startsWithSlash) {
            joinedUrlParts = "/" + joinedUrlParts;
        }
        return joinedUrlParts;
    }
}
