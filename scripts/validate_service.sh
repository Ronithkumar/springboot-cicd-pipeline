
#!/bin/bash
set -e

HEALTH_URL="http://localhost:8081/actuator/health"
MAX_ATTEMPTS=12
SLEEP_SECONDS=5

echo "Checking application health..."

for attempt in $(seq 1 "$MAX_ATTEMPTS"); do
    if curl --fail --silent --show-error "$HEALTH_URL"; then
        echo ""
        echo "Application is healthy."
        exit 0
    fi

    echo "Attempt $attempt/$MAX_ATTEMPTS failed. Waiting..."
    sleep "$SLEEP_SECONDS"
done

echo "Application health check failed."
systemctl status employee-management-api --no-pager || true
exit 1
