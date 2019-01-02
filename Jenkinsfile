pipeline{
    agent any
    tools {
        maven 'maven3.6'
    }
    stages{
        stage('Checkout'){
            steps {
                checkout scm
                sh 'mvn clean'
            }
        }
        stage('Build'){
            steps {
                sh 'mvn compile'
		sh 'mvn check'
            }
        }
	stage('Integration test'){
            steps {
		sh 'mvn integration-test'
            }
        }
        stage('Deploy'){
            steps {
                sh 'mkdir -p /var/jenkins_home/download'
                sh 'cp ./target/*.jar /var/jenkins_home/download'
            }
        }
    }
    post {
        always {
            // Publish test results
            junit 'target/surefire-reports/*.xml'
        }
    }
}