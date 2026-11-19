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
    }
}