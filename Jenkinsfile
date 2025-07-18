pipeline {
    agent any

    environment {
        SONAR_TOKEN = credentials('6869b9a92ef03c3b0296b5bb25f7489c1ff29b5f') // Jenkins Credential ID
        SONAR_ORG = 'devsecops2025'                    // Replace with your SonarCloud org
        SONAR_PROJECT_KEY = 'devsecops2025'          // Replace with your SonarCloud project key
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Srik212/DevSecOps-Project.git'
            }
        }

        stage('Install Sonar Scanner') {
            steps {
                sh '''
                if ! command -v sonar-scanner >/dev/null 2>&1; then
                    echo "Downloading Sonar Scanner..."
                    wget https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-4.8.0.2856-linux.zip -O sonar-scanner.zip
                    unzip sonar-scanner.zip
                    export PATH=$PATH:$PWD/sonar-scanner-4.8.0.2856-linux/bin
                fi
                '''
            }
        }

        stage('SonarCloud Scan') {
            steps {
                sh """
                sonar-scanner \
                  -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                  -Dsonar.organization=${SONAR_ORG} \
                  -Dsonar.host.url=https://sonarcloud.io \
                  -Dsonar.login=${SONAR_TOKEN} \
                  -Dsonar.sources=. \
                  -Dsonar.language=py \
                  -Dsonar.python.version=3
                """
            }
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
