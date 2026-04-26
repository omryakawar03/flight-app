pipeline {
    agent any
     tools { 
         
         jdk 'jdk-17'
     }
    stages {
        stage('Code-pull') {
            steps {
                git branch: 'main', url: 'https://github.com/omryakawar03/flight-app.git'
            }
        }
        stage('Builds') {
            steps {
                sh '''
                    cd FlightReservationApplication
                    mvn clean install -DskipTests
                '''
            }
        }
        stage('QA-Test') {
            steps {
                withSonarQubeEnv(installationName: 'sonar', credentialsId: 'sonar-token') {
                    sh '''
                        cd FlightReservationApplication
                        mvn sonar:sonar -Dsonar.projectKey=flight-reservation-backend 
                    '''
                
                }
            }
        }
        stage('Docker'){
            steps {
                sh '''
                    cd FlightReservationApplication
                    docker build -t omryakawar/flight-reservation-pls-19-20:latest . 
                    docker push omryakawar/flight-reservation-pls-19-20:latest
                    docker rmi omryakawar/flight-reservation-pls-19-20:latest
                '''
            }
        }
        stage('Deploy') {
            steps {
                sh '''
                    cd FlightReservationApplication
                    kubectl apply -f k8s/deployment.yaml
                    kubectl apply -f k8s/service.yaml
                '''
            }
        }
    }
}
