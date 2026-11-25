
#!/bin/bash
set -e

echo "Starting Employee Management API..."

systemctl daemon-reload
systemctl enable employee-management-api
systemctl restart employee-management-api

echo "Application start command completed."
