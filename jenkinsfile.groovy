pipeline {
    agent any

    environment {
        TEST_APP_URL = 'http://localhost:5000'
        SONAR_TOKEN = credentials('a3831e332482b3bb0ed80875d1fe0b7d45d9e7b1')   // Jenkins credential ID
        //SNYK_TOKEN = credentials('snyk-token')          // Jenkins credential ID
        SONAR_ORG = 'DevSecOpsWebApp_2025'
        SONAR_PROJECT_KEY = 'devsecopswebapp-2025'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Srik212/DevSecOps-Project.git'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t flask-vulnerable-app .'
            }
        }

        stage('SonarCloud Scan') {
            steps {
                script {
                    // Install sonar scanner CLI if not present
                    sh '''
                    if ! command -v sonar-scanner; then
                      wget https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-4.8.0.2856-linux.zip -O sonar-scanner.zip
                      unzip sonar-scanner.zip
                      export PATH=$PATH:$PWD/sonar-scanner-4.8.0.2856-linux/bin
                    fi
                    '''

                    sh """
                    sonar-scanner \
                    -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                    -Dsonar.organization=${SONAR_ORG} \
                    -Dsonar.host.url=https://sonarcloud.io \
                    -Dsonar.login=${SONAR_TOKEN} \
                    -Dsonar.sources=. \
                    -Dsonar.language=py
                    """
                }
            }
        }

        /**stage('Snyk Scan') {
            steps {
                script {
                    // Install Snyk CLI if not present
                    sh '''
                    if ! command -v snyk; then
                      npm install -g snyk
                    fi
                    '''

                    // Authenticate snyk CLI with token
                    sh "snyk auth ${SNYK_TOKEN}"

                    // Test your project dependencies for vulnerabilities
                    sh 'snyk test --all-projects --json > snyk-test-report.json || true'

                    // Test Docker image for vulnerabilities
                    sh 'snyk container test flask-vulnerable-app --json > snyk-container-report.json || true'

                    archiveArtifacts artifacts: 'snyk-*.json'
                }
            }
        }**/

        stage('IaC Scan - Dockerfile scan with Trivy') {
            steps {
                script {
                    sh '''
                    if ! command -v trivy; then
                      wget https://github.com/aquasecurity/trivy/releases/latest/download/trivy_0.46.4_Linux-64bit.tar.gz -O trivy.tar.gz
                      tar zxvf trivy.tar.gz
                      sudo mv trivy /usr/local/bin/
                    fi
                    '''
                    sh 'trivy fs --severity HIGH,CRITICAL . > trivy-fs-report.txt || true'
                    sh 'trivy image --severity HIGH,CRITICAL flask-vulnerable-app > trivy-image-report.txt || true'
                    archiveArtifacts artifacts: 'trivy-*-report.txt'
                }
            }
        }

        stage('Deploy Container for DAST') {
            steps {
                script {
                    sh '''
                    docker rm -f flask-vuln-app || true
                    docker run -d -p 5000:5000 --name flask-vuln-app flask-vulnerable-app
                    '''
                    sleep 15
                }
            }
        }

        stage('DAST Scan - OWASP ZAP') {
            steps {
                script {
                    sh 'pip install zap-cli --quiet'
                    sh 'zap-cli start --start-options "-daemon -host 0.0.0.0 -port 8090"'
                    sleep 15
                    sh "zap-cli quick-scan $TEST_APP_URL"
                    sh 'zap-cli stop'
                }
            }
        }

        stage('Cleanup') {
            steps {
                sh 'docker rm -f flask-vuln-app || true'
            }
        }
    }

    post {
        always {
            echo 'Pipeline complete.'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
