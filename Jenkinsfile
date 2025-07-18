pipeline {
  agent any
  tools { 
        maven 'Maven_3_2_5'  
    }
   stages{
    stage('CompileandRunSonarAnalysis') {
            steps {	
		sh 'mvn clean verify sonar:sonar -Dsonar.projectKey=devsecops2025 -Dsonar.organization=devsecops2025 -Dsonar.host.url=https://sonarcloud.io -Dsonar.token=8ac096dc06b5461e775df71488018230abd54a81'
			}
        } 
  }
}