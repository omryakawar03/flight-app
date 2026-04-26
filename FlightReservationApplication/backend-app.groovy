pipeline {
    agent any 
      tools { 
         
         jdk 'java'
     }
    stages {
        stage('Code-Pull'){
            steps{
                git branch: 'main', url: 'https://github.com/omryakawar03/flight-app.git'    
            }
        }
        stage('Code-Build'){
            steps{
                sh '''
                    cd FlightReservationApplication
                    mvn clean package 
                '''
            }
        }
        stage('QA-Test'){
            steps{
                withSonarQubeEnv(installationName: 'sonar', credentialsId: 'sonar-token') {
                 sh '''
                    cd FlightReservationApplication
                    mvn sonar:sonar -Dsonar.projectKey=back
                 '''
                }
            }
        }
        stage('Docker-Build'){
            steps{
                sh '''
                    cd FlightReservationApplication
                    docker build -t omryakawar/flight-reservation-pls-18:latest . 
                    docker push omryakawar/flight-reservation-pls-18:latest 
                    docker rmi omryakawar/flight-reservation-pls-18:latest 
                '''
            }
        }
        stage('Deploy'){
            steps{
                sh '''
                    cd FlightReservationApplication
                     kubectl apply -f k8s/deployment.yaml
                    kubectl apply -f k8s/service.yaml
                '''
            }
        }
    }
}