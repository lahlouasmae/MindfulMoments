# Use the official Ubuntu as the base image
FROM ubuntu:22.04

# Prevent interactive prompts during package installation
ENV DEBIAN_FRONTEND=noninteractive

# Set environment variables
ENV SDK_URL="https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip" \
    ANDROID_HOME="/usr/local/android-sdk" \
    ANDROID_SDK_ROOT="/usr/local/android-sdk" \
    PATH="${PATH}:/usr/local/android-sdk/cmdline-tools/latest/bin:/usr/local/android-sdk/platform-tools"

# Install required packages
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    openjdk-17-jdk \
    curl \
    git \
    && rm -rf /var/lib/apt/lists/*

# Download and install Android SDK
RUN mkdir -p ${ANDROID_HOME}/cmdline-tools && \
    wget -q ${SDK_URL} -O android_tools.zip && \
    unzip -q android_tools.zip -d ${ANDROID_HOME}/cmdline-tools && \
    mv ${ANDROID_HOME}/cmdline-tools/cmdline-tools ${ANDROID_HOME}/cmdline-tools/latest && \
    rm android_tools.zip

# Accept licenses and install required Android SDK packages
RUN yes | sdkmanager --licenses && \
    sdkmanager "platform-tools" \
    "platforms;android-34" \
    "build-tools;34.0.0" \
    "extras;android;m2repository" \
    "extras;google;m2repository"

# Create app directory
WORKDIR /app

# Copy the project files
COPY . .

# Grant execute permission to gradlew
RUN chmod +x ./gradlew

# Build the project
CMD ["./gradlew", "assembleDebug"]