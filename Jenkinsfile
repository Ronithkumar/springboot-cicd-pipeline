
pipeline {
    agent any

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
                        ssh -o StrictHostKeyChecking=accept-new \
                            ubuntu@10.20.2.130 \
                            "hostname && whoami"
                    '''
                }
            }
        }
    }
}
