
#!/bin/bash
set -e

echo "Stopping Employee Management API..."

systemctl stop employee-management-api || true

echo "Application stopped."
