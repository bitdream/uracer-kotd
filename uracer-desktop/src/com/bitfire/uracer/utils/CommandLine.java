
package com.bitfire.uracer.utils;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import com.bitfire.uracer.configuration.BootConfig;
import com.bitfire.uracer.configuration.BootConfig.BootConfigFlag;

public final class CommandLine {

	private CommandLine () {
	}

	private static boolean isInt (String value) {
		if (value != null) {
			int len = value.length();
			for (int i = 0; i < len; i++) {
				if (!Character.isDigit(value.charAt(i))) {
					return false;
				}
			}

			return true;
		}

		return false;
	}

	public static boolean applyLaunchFlags (String[] argv, BootConfig boot) {
		int c;
		String arg;

		//@off
		LongOpt[] opts = {
			new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'),
			new LongOpt("resolution", LongOpt.REQUIRED_ARGUMENT, null, 'r'),
			new LongOpt("no-vsync", LongOpt.NO_ARGUMENT, null, 'v'),
			new LongOpt("cpusync", LongOpt.NO_ARGUMENT, null, 'c'),
			new LongOpt("fullscreen", LongOpt.NO_ARGUMENT, null, 'f'),
			new LongOpt("undecorated", LongOpt.NO_ARGUMENT, null, 'u'),
			//new LongOpt("right-screen", LongOpt.NO_ARGUMENT, null, 't')
		};
		//@on

		Getopt g = new Getopt("URacer", argv, ":hr:vcfu", opts);
		g.setOpterr(false);
		while ((c = g.getopt()) != -1) {
			arg = g.getOptarg();

			switch (c) {
			case 'r':
				String[] res = arg.split("x");
				int w = 0;
				int h = 0;

				if (res.length == 2 && isInt(res[0]) && isInt(res[1])) {
					w = Integer.parseInt(res[0]);
					h = Integer.parseInt(res[1]);
				} else {
					if (arg.equals("low")) {
						w = 800;
						h = 480;
					} else if (arg.equals("mid")) {
						w = 1280;
						h = 800;
					} else if (arg.equals("high")) {
						w = 1920;
						h = 1080;
					} else {
						System.out.print("Invalid resolution specified (" + arg + "), defaulting to " + (w + "x" + h));
					}
				}

				if (w > 0 && h > 0) {
					boot.setInt(BootConfigFlag.WIDTH, w);
					boot.setInt(BootConfigFlag.HEIGHT, h);

					// automatically compute the default x/y window position (centered) if not present already

					if (boot.getWindowX() == -1) {
						boot.setWindowX(AwtUtils.getCenteredXOnDisplay(w));
					}

					if (boot.getWindowY() == -1) {
						boot.setWindowY(AwtUtils.getCenteredYOnDisplay(h));
					}
				}

				break;
			case 'h':
				System.out.println("Valid command-line options:");
				System.out.println("  -h, --help\t\tshows this help");
				System.out.println("  -r, --resolution=RES\tspecify the resolution to use: you can either specify");
				System.out.println("  \t\t\ta real resolution =, e.g. --resolution=800x600, or use ");
				System.out.println("  \t\t\ta built-in shortcut (one of \"low\", \"mid\" or \"high\").");
				System.out.println("  \t\t\t(low=800x480, mid=1280x800, high=1920x1080)");
				System.out.println("  -v, --no-vsync\tdisable VSync");
				System.out.println("  -c, --cpusync\t\tenable CPU sync");
				System.out.println("  -f, --fullscreen\tenable fullscreen");
				System.out.println("  -u, --undecorated\tdraw window without the window manager's decorations");
				// System.out
				// .println("  -t, --right-screen\treposition the game's window to the screen on the right,\n\t\t\tif available.");
				System.out.println("");
				return false;
			case 'v':
				boot.setBoolean(BootConfigFlag.VSYNC, false);
				break;
			case 'c':
				boot.setBoolean(BootConfigFlag.CPUSYNC, true);
				break;
			case 'f':
				boot.setBoolean(BootConfigFlag.FULLSCREEN, true);
				break;
			case 'u':
				boot.setBoolean(BootConfigFlag.UNDECORATED, true);
				break;
			// case 't':
			// flags.useRightScreen = true;
			// break;
			case '?':
				System.out.print("The specified parameter is not valid.\nTry --help for a list of valid parameters.");
				return false;
			case ':':
				System.out.print("The specified argument is missing some values.\nTry --help for more information.");
				return false;
			default:
				System.out.print("getopt() returned " + c + " (" + (char)c + ")\n");
				return false;
			}
		}

		return true;
	}
}
