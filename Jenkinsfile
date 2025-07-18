pipeline {
    agent any

    environment {
        SONAR_TOKEN = credentials('6869b9a92ef03c3b0296b5bb25f7489c1ff29b5f') // Jenkins Credential ID
        SONAR_ORG = 'devsecops2025'                    // Replace with your SonarCloud org
        SONAR_PROJECT_KEY = 'devsecops2025'  
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Srik212/DevSecOps-Project.git'
            }
        }

        
    stage('Debug') {
    steps {
        sh 'echo Hello from Jenkins'
    }
}

    post {
        always {
            echo 'Pipeline finished.'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}
