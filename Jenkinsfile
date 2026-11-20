
pipeline {
    agent any

    parameters {
        string(
            name: 'DEPLOY_HOST',
            defaultValue: '10.20.1.19',
            description: 'Private IP or hostname of the application server'
        )
    }

    stages {
        stage('Build and Test') {
            steps {
                sh './mvnw clean verify'
            }
        }

        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'target/employee-management-api.jar',
                                 fingerprint: true
            }
        }

        stage('SSH Connectivity Test') {
            steps {
               sshagent(credentials: ['employee-api-ssh-key']) {
                    sh '''
                        set -eu

                        if ! printf '%s' "$DEPLOY_HOST" | grep -Eq '^[0-9]{1,3}(\.[0-9]{1,3}){3}$'; then
                            echo "Invalid deployment host: expected an IPv4 address"
                            exit 1
                        fi

                        ssh -o StrictHostKeyChecking=accept-new \
                            "ubuntu@$DEPLOY_HOST" \
                            "hostname && whoami"
                    '''
                }
            }
        }
    }
}
