package com.nascent.maven.plugin.bee.utils;

import java.util.Locale;

/** @author hengyunabc 2018-11-08 */
public class OsUtils {

  public static final String MSYSTEM = "MSYSTEM";
  public static final String MINGW = "MINGW";
  public static final String SHELL = "SHELL";
  public static final String BIN_BASH = "/bin/bash";
  public static final String LINUX = "linux";
  public static final String MAC = "mac";
  public static final String DARWIN = "darwin";
  public static final String WINDOWS = "windows";
  private static final String OPERATING_SYSTEM_NAME =
      System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
  static PlatformEnum platform;

  static {
    if (OPERATING_SYSTEM_NAME.startsWith(LINUX)) {
      platform = PlatformEnum.LINUX;
    } else if (OPERATING_SYSTEM_NAME.startsWith(MAC) || OPERATING_SYSTEM_NAME.startsWith(DARWIN)) {
      platform = PlatformEnum.MACOSX;
    } else if (OPERATING_SYSTEM_NAME.startsWith(WINDOWS)) {
      platform = PlatformEnum.WINDOWS;
    } else {
      platform = PlatformEnum.UNKNOWN;
    }
  }

  private OsUtils() {}

  public static boolean isWindows() {
    return platform == PlatformEnum.WINDOWS;
  }

  public static boolean isLinux() {
    return platform == PlatformEnum.LINUX;
  }

  public static boolean isMac() {
    return platform == PlatformEnum.MACOSX;
  }

  @SuppressWarnings("java:S1066")
  public static boolean isCygwinOrMinGW() {
    if (isWindows()) {
      if ((System.getenv(MSYSTEM) != null && System.getenv(MSYSTEM).startsWith(MINGW))
          || BIN_BASH.equals(System.getenv(SHELL))) {
        return true;
      }
    }
    return false;
  }
}
