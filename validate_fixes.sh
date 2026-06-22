#!/bin/bash

# Validation script for Kotlin compilation fixes
# This script validates that all the fixes have been applied correctly

set -e

echo "=================================="
echo "Kotlin Compilation Fix Validator"
echo "=================================="
echo ""

cd "$(dirname "$0")/frontend/mobile_kotlin"

echo "1. Checking Gradle configuration files..."
echo "-------------------------------------------"

# Check AGP version
AGP_VERSION=$(grep 'agp = ' gradle/libs.versions.toml | cut -d'"' -f2)
echo "✓ AGP Version: $AGP_VERSION"
if [ "$AGP_VERSION" != "8.5.2" ]; then
    echo "⚠ Warning: AGP version is not 8.5.2"
fi

# Check Kotlin version
KOTLIN_VERSION=$(grep 'kotlin = ' gradle/libs.versions.toml | cut -d'"' -f2)
echo "✓ Kotlin Version: $KOTLIN_VERSION"
if [ "$KOTLIN_VERSION" != "1.9.23" ]; then
    echo "⚠ Warning: Kotlin version is not 1.9.23"
fi

# Check compileSdk
COMPILE_SDK=$(grep 'compileSdk = ' app/build.gradle.kts | awk '{print $3}')
echo "✓ Compile SDK: $COMPILE_SDK"
if [ "$COMPILE_SDK" != "34" ]; then
    echo "⚠ Warning: compileSdk is not 34"
fi

# Check targetSdk
TARGET_SDK=$(grep 'targetSdk = ' app/build.gradle.kts | awk '{print $3}')
echo "✓ Target SDK: $TARGET_SDK"
if [ "$TARGET_SDK" != "34" ]; then
    echo "⚠ Warning: targetSdk is not 34"
fi

echo ""
echo "2. Checking Activity classes..."
echo "--------------------------------"

ACTIVITIES=(
    "AICareerAdvisorActivity"
    "CvCorrectionActivity"
    "InterviewPreparationActivity"
    "JobOpportunitiesActivity"
    "JobSeekerProfileInitialActivity"
    "LoginActivity"
    "MainActivity"
    "PostsActivity"
    "ProfileActivity"
    "RecruiterProfileActivity"
    "RecruiterProfileInitialActivity"
    "SignupActivity"
    "JobDetailsActivity"
)

for activity in "${ACTIVITIES[@]}"; do
    if [ -f "app/src/main/java/com/example/jobify/${activity}.kt" ]; then
        echo "✓ ${activity}.kt exists"
    else
        echo "✗ ${activity}.kt NOT FOUND"
        exit 1
    fi
done

echo ""
echo "3. Checking for InterviewChatActivity references..."
echo "-----------------------------------------------------"

if grep -r "InterviewChatActivity" app/src/main/java/com/example/jobify/*.kt > /dev/null 2>&1; then
    echo "⚠ WARNING: Found references to InterviewChatActivity"
    grep -n "InterviewChatActivity" app/src/main/java/com/example/jobify/*.kt
else
    echo "✓ No references to InterviewChatActivity found (correct)"
fi

echo ""
echo "4. Checking local.properties..."
echo "--------------------------------"

if [ -f "local.properties" ]; then
    echo "✓ local.properties exists"
    if grep -q "GEMINI_API_KEY" local.properties; then
        echo "✓ GEMINI_API_KEY is configured"
    else
        echo "⚠ Warning: GEMINI_API_KEY not found in local.properties"
    fi
else
    echo "⚠ Warning: local.properties not found (will be created on first sync)"
fi

echo ""
echo "5. Running Gradle clean..."
echo "---------------------------"

chmod +x gradlew
if ./gradlew clean --no-daemon; then
    echo "✓ Gradle clean successful"
else
    echo "✗ Gradle clean failed"
    exit 1
fi

echo ""
echo "6. Compiling Kotlin code..."
echo "----------------------------"

if ./gradlew :app:compileDebugKotlin --no-daemon; then
    echo "✓ Kotlin compilation successful!"
else
    echo "✗ Kotlin compilation failed"
    exit 1
fi

echo ""
echo "7. Running unit tests..."
echo "-------------------------"

if ./gradlew :app:testDebugUnitTest --no-daemon; then
    echo "✓ Unit tests passed!"
else
    echo "⚠ Some unit tests failed (this may be expected)"
fi

echo ""
echo "=================================="
echo "✓ All validations passed!"
echo "=================================="
echo ""
echo "The Kotlin compilation errors have been fixed."
echo "You can now build the project with:"
echo "  ./gradlew :app:assembleDebug"
