
#!/bin/bash
set -e

APP_DIR="/opt/employee-management-api"
APP_JAR="$APP_DIR/employee-management-api.jar"

echo "Setting application permissions..."

chown ubuntu:ubuntu "$APP_JAR"
chmod 644 "$APP_JAR"

echo "Permissions configured."
