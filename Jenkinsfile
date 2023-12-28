def gv

pipeline {
    agent any
    tools {
        maven 'maven-3.9'
    }
    stages {
        stage('build app') {
            steps {
                script {
                    echo 'building the application...'
                    echo 'checking the integration...'
                    sh 'mvn clean package'
                }
            }
        }
        stage('build image') {
            steps {
                script {
                    echo "building the docker image..."
                    withCredentials([usernamePassword(credentialsId: 'docker-hub-repo', passwordVariable: 'PASS', usernameVariable: 'USER')]){
                        sh "docker build -t olekslutsenko23/demo-app:jma-2.0 ."
                        sh 'echo $PASS | docker login -u $USER --password-stdin'
                        sh "docker push olekslutsenko23/demo-app:jma-2.0"
                    }
                }
            }
        }
        stage('deploy') {
            steps {
                script {
                    echo 'deploying docker image...'
                }
            }
        }
        stage('commit version update'){
            steps {
                script {
                    echo "Updating version in repo..."
                    // gv.updateVersion()
                }
            }
        }
    }
}