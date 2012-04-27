package edu.berkeley.cs194.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.berkeley.cs194.audio.SoundPlayer;
import edu.berkeley.cs194.deprecated.RecorderThread;

public class Utils {
	public static int[] textToMorse(String text) {
		boolean lastWasWhitespace;
		int strlen = text.length();

		ArrayList<Integer> result = new ArrayList<Integer>();
		lastWasWhitespace = true;
		for (int i = 0; i < SoundPlayer.CONTROL_TONE_NUM; i++) {
			result.add(SoundPlayer.HIGH);
		}
		for (int i = 0; i < strlen; i++) {
			char c = text.charAt(i);
			if (Character.isWhitespace(c)) {
				if (!lastWasWhitespace) {
					result.add(SoundPlayer.SILENT);
					result.add(SoundPlayer.SILENT);
					lastWasWhitespace = true;
				}
			} else {
				if (!lastWasWhitespace) {
					result.add(SoundPlayer.SILENT);
				}
				lastWasWhitespace = false;
				int[] letter = pattern(c);
				for (int j = 0; j < letter.length; j++) {
					result.add(letter[j]);
				}
			}
		}
		return convertIntegers(result);
	}

	/** The characters from 'A' to 'Z' */
	public static final int[][] LETTERS = new int[][] {
			/* A */new int[] { SoundPlayer.HIGH, SoundPlayer.LOW },
			/* B */new int[] { SoundPlayer.LOW, SoundPlayer.HIGH,
					SoundPlayer.HIGH, SoundPlayer.HIGH },
			/* C */new int[] { SoundPlayer.LOW, SoundPlayer.HIGH,
					SoundPlayer.LOW, SoundPlayer.HIGH },
			/* D */new int[] { SoundPlayer.LOW, SoundPlayer.HIGH,
					SoundPlayer.HIGH },
			/* E */new int[] { SoundPlayer.HIGH },
			/* F */new int[] { SoundPlayer.HIGH, SoundPlayer.HIGH,
					SoundPlayer.LOW, SoundPlayer.HIGH },
			/* G */new int[] { SoundPlayer.LOW, SoundPlayer.LOW,
					SoundPlayer.HIGH },
			/* H */new int[] { SoundPlayer.HIGH, SoundPlayer.HIGH,
					SoundPlayer.HIGH, SoundPlayer.HIGH },
			/* I */new int[] { SoundPlayer.HIGH, SoundPlayer.HIGH },
			/* J */new int[] { SoundPlayer.HIGH, SoundPlayer.LOW,
					SoundPlayer.LOW, SoundPlayer.LOW },
			/* K */new int[] { SoundPlayer.LOW, SoundPlayer.HIGH,
					SoundPlayer.LOW },
			/* L */new int[] { SoundPlayer.HIGH, SoundPlayer.LOW,
					SoundPlayer.HIGH, SoundPlayer.HIGH },
			/* M */new int[] { SoundPlayer.LOW, SoundPlayer.LOW },
			/* N */new int[] { SoundPlayer.LOW, SoundPlayer.HIGH },
			/* O */new int[] { SoundPlayer.LOW, SoundPlayer.LOW,
					SoundPlayer.LOW },
			/* P */new int[] { SoundPlayer.HIGH, SoundPlayer.LOW,
					SoundPlayer.LOW, SoundPlayer.HIGH },
			/* Q */new int[] { SoundPlayer.LOW, SoundPlayer.LOW,
					SoundPlayer.HIGH, SoundPlayer.LOW },
			/* R */new int[] { SoundPlayer.HIGH, SoundPlayer.LOW,
					SoundPlayer.HIGH },
			/* S */new int[] { SoundPlayer.HIGH, SoundPlayer.HIGH,
					SoundPlayer.HIGH },
			/* T */new int[] { SoundPlayer.LOW },
			/* U */new int[] { SoundPlayer.HIGH, SoundPlayer.HIGH,
					SoundPlayer.LOW },
			/* V */new int[] { SoundPlayer.HIGH, SoundPlayer.HIGH,
					SoundPlayer.LOW },
			/* W */new int[] { SoundPlayer.HIGH, SoundPlayer.LOW,
					SoundPlayer.LOW },
			/* X */new int[] { SoundPlayer.LOW, SoundPlayer.HIGH,
					SoundPlayer.HIGH, SoundPlayer.LOW },
			/* Y */new int[] { SoundPlayer.LOW, SoundPlayer.HIGH,
					SoundPlayer.LOW, SoundPlayer.LOW },
			/* Z */new int[] { SoundPlayer.LOW, SoundPlayer.LOW,
					SoundPlayer.HIGH, SoundPlayer.HIGH }, };

	/** The characters from '0' to '9' */
	public static final int[][] NUMBERS = new int[][] {
			/* 0 */new int[] { SoundPlayer.LOW, SoundPlayer.LOW,
					SoundPlayer.LOW, SoundPlayer.LOW, SoundPlayer.LOW },
			/* 1 */new int[] { SoundPlayer.HIGH, SoundPlayer.LOW,
					SoundPlayer.LOW, SoundPlayer.LOW, SoundPlayer.LOW },
			/* 2 */new int[] { SoundPlayer.HIGH, SoundPlayer.HIGH,
					SoundPlayer.LOW, SoundPlayer.LOW, SoundPlayer.LOW },
			/* 3 */new int[] { SoundPlayer.HIGH, SoundPlayer.HIGH,
					SoundPlayer.HIGH, SoundPlayer.LOW, SoundPlayer.LOW },
			/* 4 */new int[] { SoundPlayer.HIGH, SoundPlayer.HIGH,
					SoundPlayer.HIGH, SoundPlayer.HIGH, SoundPlayer.LOW },
			/* 5 */new int[] { SoundPlayer.HIGH, SoundPlayer.HIGH,
					SoundPlayer.HIGH, SoundPlayer.HIGH, SoundPlayer.HIGH },
			/* 6 */new int[] { SoundPlayer.LOW, SoundPlayer.HIGH,
					SoundPlayer.HIGH, SoundPlayer.HIGH, SoundPlayer.HIGH },
			/* 7 */new int[] { SoundPlayer.LOW, SoundPlayer.LOW,
					SoundPlayer.HIGH, SoundPlayer.HIGH, SoundPlayer.HIGH },
			/* 8 */new int[] { SoundPlayer.LOW, SoundPlayer.LOW,
					SoundPlayer.LOW, SoundPlayer.HIGH, SoundPlayer.HIGH },
			/* 9 */new int[] { SoundPlayer.LOW, SoundPlayer.LOW,
					SoundPlayer.LOW, SoundPlayer.LOW, SoundPlayer.HIGH }, };

	public static int[] pattern(char c) {
		/** Return the pattern data for a given character */
		if (c >= 'A' && c <= 'Z') {
			return LETTERS[c - 'A'];
		}
		if (c >= 'a' && c <= 'z') {
			return LETTERS[c - 'a'];
		} else if (c >= '0' && c <= '9') {
			return NUMBERS[c - '0'];
		} else {
			return null;
		}
	}

	public static int[] convertIntegers(List<Integer> integers) {
		int[] ret = new int[integers.size()];
		Iterator<Integer> iterator = integers.iterator();
		for (int i = 0; i < ret.length; i++) {
			ret[i] = iterator.next().intValue();
		}
		return ret;
	}

	public static String morseToText(List<Integer> samples) {
		String input = "";
		for (int i = 0; i < samples.size(); i++) {
			input += samples.get(i).intValue() == SoundPlayer.HIGH ? "1" : "2";
		}

		String output = "";
		if (input.equals("12")) {
			output = "A";
		} else if (input.equals("2111")) {
			output = "B";
		} else if (input.equals("2121")) {
			output = "C";
		} else if (input.equals("211")) {
			output = "D";
		} else if (input.equals("1")) {
			output = "E";
		} else if (input.equals("1121")) {
			output = "F";
		} else if (input.equals("221")) {
			output = "G";
		} else if (input.equals("1111")) {
			output = "H";
		} else if (input.equals("11")) {
			output = "I";
		} else if (input.equals("1222")) {
			output = "J";
		} else if (input.equals("212")) {
			output = "K";
		} else if (input.equals("1211")) {
			output = "L";
		} else if (input.equals("22")) {
			output = "M";
		} else if (input.equals("21")) {
			output = "N";
		} else if (input.equals("222")) {
			output = "O";
		} else if (input.equals("1221")) {
			output = "P";
		} else if (input.equals("2212")) {
			output = "Q";
		} else if (input.equals("121")) {
			output = "R";
		} else if (input.equals("111")) {
			output = "S";
		} else if (input.equals("2")) {
			output = "T";
		} else if (input.equals("112")) {
			output = "U";
		} else if (input.equals("1112")) {
			output = "V";
		} else if (input.equals("122")) {
			output = "W";
		} else if (input.equals("2112")) {
			output = "X";
		} else if (input.equals("2122")) {
			output = "Y";
		} else if (input.equals("2211")) {
			output = "Z";
		} else if (input.equals("22222")) {
			output = "0";
		} else if (input.equals("12222")) {
			output = "1";
		} else if (input.equals("11222")) {
			output = "2";
		} else if (input.equals("11122")) {
			output = "3";
		} else if (input.equals("11112")) {
			output = "4";
		} else if (input.equals("11111")) {
			output = "5";
		} else if (input.equals("21111")) {
			output = "6";
		} else if (input.equals("22111")) {
			output = "7";
		} else if (input.equals("22211")) {
			output = "8";
		} else if (input.equals("22221")) {
			output = "9";
		}

		return output;
	}
}
