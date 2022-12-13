#!/usr/bin/env sh

set -e

JACOCO_VERSION="0.8.8"
ASM_VERSION="9.2"
ARGS4J_VERSION="2.0.28"

echo "⬇️  Downloading JaCoCo Agent"
curl -s https://repo1.maven.org/maven2/org/jacoco/org.jacoco.agent/${JACOCO_VERSION}/org.jacoco.agent-${JACOCO_VERSION}.jar --output org.jacoco.agent-${JACOCO_VERSION}.jar

echo "📦 Extracting JaCoCo agent"
unzip -qo -djacoco org.jacoco.agent-${JACOCO_VERSION}.jar
