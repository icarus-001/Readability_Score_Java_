package readability;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
            String text;
            try (FileReader fr = new FileReader(args[0]); BufferedReader br = new BufferedReader(fr)) {
                StringBuilder stringBuilder = new StringBuilder();
                String ls = System.getProperty("line.separator");
                while ((text = br.readLine()) != null) {
                    stringBuilder.append(text);
                    stringBuilder.append(ls);
                }
                text = stringBuilder.toString();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            text = text.trim();
            String[] words = text.split(" ");
            String[] sentences = text.split("[.?!]");
            double wordCount = words.length;
            double sentenceCount = sentences.length;
            text = text.replaceAll(" ", "");
            text = text.trim();
            double characterCount = text.length();
            int syllablesCount = 0;
            ArrayList<String> polysyllables = new ArrayList<>();
            for (int i = 0; i < words.length; i++) {
                syllablesCount += countSyllables(words[i]);
                if (isPolysyllable(words[i])) {
                    if (!polysyllables.contains(words[i])) {
                        polysyllables.add(words[i]);
                    }
                }
            }
            int polysyllablesCount = polysyllables.size();

            System.out.println("Words: " + (int) wordCount);
            System.out.println("Sentences: " + (int) sentenceCount);
            System.out.println("Characters: " + (int) characterCount);
            System.out.println("Syllables: " + syllablesCount);
            System.out.println("Polysyllables: " + polysyllablesCount);
            System.out.println();

            System.out.print("Enter the score you want to calculate ((ARI, FK, SMOG, CL, all)) ");
            String input = sc.next();
        System.out.println();
            switch (input) {
                case "ARI":
                    System.out.println(automatedRIS(characterCount, wordCount, sentenceCount));
                    System.out.println();
                    break;

                case "FK":
                    System.out.println(fKTestS(wordCount,sentenceCount, syllablesCount));
                    System.out.println();
                    break;

                case "SMOG":
                    System.out.println(sMOGS(polysyllablesCount, sentenceCount));
                    System.out.println();
                    break;

                case "CL":
                    System.out.println(coiemanLiauIndexS(characterCount, wordCount, sentenceCount));
                    System.out.println();
                    break;

                case "all":
                    System.out.println(automatedRIS(characterCount, wordCount, sentenceCount));
                    System.out.println(fKTestS(wordCount,sentenceCount, syllablesCount));
                    System.out.println(sMOGS(polysyllablesCount, sentenceCount));
                    System.out.println(coiemanLiauIndexS(characterCount, wordCount, sentenceCount));
                    System.out.println();
                    break;

                default:
                    System.out.println("Invalid input");

            }
        double average = (readabilityAge(automatedRI(characterCount, wordCount, sentenceCount)) +
                readabilityAge(fKTest(wordCount,sentenceCount, syllablesCount)) +
                readabilityAge(sMOG(polysyllablesCount, sentenceCount)) +
                readabilityAge(coiemanLiauIndex(characterCount, wordCount, sentenceCount))) / 4;
        System.out.println("This text should be understood in average by " + average + "-year-olds.");

    }

    public static boolean isPolysyllable (String s) {
        return countSyllables(s) > 2;
    }

    public static double automatedRI (double characters, double words, double sentences) {
        return Math.floor((4.71 *  (characters / words) + 0.5 *  (words / sentences) - 21.43) * 100) / 100.0;
    }

    public static String automatedRIS (double characters, double words, double sentences) {
        return "Automated Readability Index: " + automatedRI(characters, words, sentences) + " (about " + (int)readabilityAge(automatedRI(characters, words, sentences)) + "-year-olds).";

    }

    public static double coiemanLiauIndex (double characters, double words, double sentences) {
        return Math.floor((0.0588 * (characters / (words / 100)) - 0.296 * (sentences / (words / 100)) - 15.8) * 100) / 100.0;

    }

    public static String coiemanLiauIndexS (double characters, double words, double sentences) {
        return "Coleman–Liau index: " + coiemanLiauIndex(characters, words, sentences) + " (about " + (int)readabilityAge(coiemanLiauIndex(characters, words, sentences)) + "-year-olds).";
    }

    public static double sMOG (double polysyllables, double sentences) {
        return Math.floor((1.043 * (Math.sqrt(polysyllables * (30 / sentences))) + 3.1291) * 100) / 100.0;
    }

    public static String sMOGS (double polysyllables, double sentences) {
        return "Simple Measure of Gobbledygook: " + sMOG(polysyllables, sentences) + " (about " + (int)readabilityAge(sMOG(polysyllables, sentences)) + "-year-olds).";
    }

    public static double fKTest (double words, double sentences, double syllables) {
        return Math.floor((0.39 * (words / sentences) + 11.8 * (syllables / words) - 15.59) * 100) / 100.0;
    }

    public static String fKTestS (double words, double sentences, double syllables) {
        return "Flesch–Kincaid readability tests: " + fKTest(words,sentences, syllables) + " (about " + (int)readabilityAge(fKTest(words,sentences, syllables)) + "-year-olds).";
    }

    public static int countSyllables (String s) {
        try {
            Integer.parseInt(s);
            return 0;
        } catch (NumberFormatException ignored) {

        }
        if (countVowels(s) == 0) {
            return 1;
        } else return countVowels(s);
    }

    public static int countVowels(String s) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (i == s.length() - 1 && s.charAt(i) == 'e') {
                continue;
            }
            if (i != 0) {
                if (isVowel(s.charAt(i - 1))) {
                    continue;
                }
            }

            if (i > 1) {
                if (isVowel(s.charAt(i-1)) && isVowel(s.charAt(i-2))) {
                    continue;
                }
            }
                if (isVowel(c)) {
                    count++;
                }

        }
        return count;
    }

    public static boolean isVowel (char c) {
        return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' || c == 'y' ;
        //|| c == 'Y' || c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U'
    }

    public static double readabilityAge (double score) {
        score = Math.ceil(score);
        double readability = 0;
        if (score == 1) {
            readability = 6;
        } else if (score == 2) {
            readability = 7;
        } else if (score == 3) {
            readability = 8;
        } else if (score == 4) {
            readability = 9;
        } else if (score == 5) {
            readability = 10;
        } else if (score == 6) {
            readability = 11;
        } else if (score == 7) {
            readability = 12;
        } else if (score == 8) {
            readability = 13;
        } else if (score == 9) {
            readability = 14;
        } else if (score == 10) {
            readability = 15;
        } else if (score == 11) {
            readability = 16;
        } else if (score == 12) {
            readability = 18;
        } else if (score == 13) {
            readability = 17;
        } else if (score == 14) {
            readability = 22;
        }
        return (double) readability;

    }

}
