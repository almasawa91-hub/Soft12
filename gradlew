#!/usr/bin/env sh
# Lightweight Gradle bootstrap launcher for this repository.
# It downloads the pinned Gradle distribution when it is not cached.
set -eu

GRADLE_VERSION="8.4"
GRADLE_USER_HOME="${GRADLE_USER_HOME:-$HOME/.gradle}"
INSTALL_ROOT="$GRADLE_USER_HOME/wrapper/dists/gradle-$GRADLE_VERSION-bin/soft12"
GRADLE_HOME="$INSTALL_ROOT/gradle-$GRADLE_VERSION"
GRADLE_BIN="$GRADLE_HOME/bin/gradle"

if [ ! -x "$GRADLE_BIN" ]; then
  mkdir -p "$INSTALL_ROOT"
  ARCHIVE="$INSTALL_ROOT/gradle-$GRADLE_VERSION-bin.zip"
  if [ ! -f "$ARCHIVE" ]; then
    if command -v curl >/dev/null 2>&1; then
      curl --fail --location --retry 3 "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip" --output "$ARCHIVE"
    elif command -v wget >/dev/null 2>&1; then
      wget -O "$ARCHIVE" "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
    else
      echo "Error: curl or wget is required to download Gradle." >&2
      exit 1
    fi
  fi
  if ! command -v unzip >/dev/null 2>&1; then
    echo "Error: unzip is required to extract Gradle." >&2
    exit 1
  fi
  unzip -q -o "$ARCHIVE" -d "$INSTALL_ROOT"
  rm -f "$ARCHIVE"
fi

exec "$GRADLE_BIN" "$@"
