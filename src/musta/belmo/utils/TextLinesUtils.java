package musta.belmo.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class TextLinesUtils {
    public static String addLinesAtPositions(String text, String string) {
        return addLinesAtPositions(text, convertToTextLines(string));
    }

    public static String addLinesAtPositions(String text, File file) {
        return addLinesAtPositions(text, convertToTextLines(file));
    }

    public static String addLinesAtPositions(String text, Map<Integer, String> linesToAdd) {
        return addLinesAtPositions(text, convertToTextLine(linesToAdd));
    }

    private static String addLinesAtPositions(String text, Set<TextLine> setOfLinesToAdd) {
        final List<String> listLines = convertStringToListOfLines(text);
        final List<String> mergedText = addLinesToPositions(setOfLinesToAdd, listLines);
        return convertLinesToString(mergedText);
    }

    private static String convertLinesToString(List<String> listLines) {
        return listLines.stream()
                .collect(Collectors.joining("\n"));
    }

    private static List<String> addLinesToPositions(Set<TextLine> setOfLinesToAdd, List<String> listLines) {
        final List<String> result = new ArrayList<>(listLines);
        int insertedLines = 0;
        Set<TextLine> textLinesWithPositivePositions = setOfLinesToAdd.stream()
                .filter(textLine -> textLine.getLineNumber() > 0)
                .collect(Collectors.toSet());
        for (TextLine line : textLinesWithPositivePositions) {
            int position = insertedLines + line.getLineNumber() - 1;
            if (result.size() > position) {
                result.add(position, line.getContent());
            } else {
                result.add(line.getContent());
            }
            insertedLines++;
        }
        List<String> negativeLines = addNegativeLinesAtFirst(setOfLinesToAdd);
        negativeLines.addAll(result);
        return negativeLines;
    }

    private static List<String> addNegativeLinesAtFirst(Set<TextLine> listLines) {
        return listLines.stream().filter((textLine -> textLine.getLineNumber() <= 0))
                .map(TextLine::getContent)
                .collect(Collectors.toList());

    }

    private static List<String> convertStringToListOfLines(String text) {
        return Optional.ofNullable(text)
                .map(txt -> txt.split("\\n"))
                .map(Arrays::asList)
                .orElse(Arrays.asList())
                .stream()
                .collect(Collectors.toList());
    }

    private static Set<TextLine> convertToTextLine(Map<Integer, String> linesToAdd) {
        return linesToAdd.entrySet().stream()
                .map(entry -> new TextLine(entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet());
    }

    private static Set<TextLine> convertToTextLines(File linesToAdd) {
        FileReader reader = null;
        try {
            reader = new FileReader(linesToAdd);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return convertReadableTextToTextLines(reader);
    }

    private static Set<TextLine> convertToTextLines(String linesToAdd) {
        final StringReader reader = new StringReader(linesToAdd);
        return convertReadableTextToTextLines(reader);
    }

    private static Set<TextLine> convertReadableTextToTextLines(Readable readable) {
        final Set<TextLine> setOfLines = new TreeSet<>();
        if (Objects.nonNull(readable)) {
            final Scanner sc = new Scanner(readable);
            while (sc.hasNextLine()) {
                TextLine textLine = new TextLine(sc.nextInt(), trimWitheSpacesAtStart(sc.nextLine()));
                setOfLines.add(textLine);
            }
        }
        return setOfLines;
    }

    private static String trimWitheSpacesAtStart(String input) {
        return delete(input, "^[\\t ]");
    }

    public static String delete(String old, String regex) {
        return Optional.ofNullable(old)
                .map(str -> str.replaceAll(regex, ""))
                .orElse(null);
    }

    public static String deleteLines(String text, Integer... lines) {

        StringBuilder sb = new StringBuilder();
        Scanner sc = new Scanner(text);

        List<Integer> list = Arrays.asList(lines);
        int counter = 1;
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (list.contains(Integer.valueOf(counter))) {
                counter++;
                continue;
            }

            sb.append(line).append('\n');
            counter++;
        }
        sc.close();
        return sb.toString();
    }
}
