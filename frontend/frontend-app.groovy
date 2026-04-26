pipeline{
    agent any
    stages{
        stage('Code-Pull'){
            steps{
                git branch: 'main', url: 'https://github.com/omryakawar03/flight-app.git'    
            }
        }
        stage('Code-Build'){
            steps{
                sh '''
                    cd frontend
                    npm install
                    npm run build
                '''
            }
        }
        stage('Deploy'){
            steps{
                sh '''
                cd frontend
                aws s3 sync dist/ s3://new-buck-for-static-site-flight/ 
                '''  
            }
        }
    }
}
