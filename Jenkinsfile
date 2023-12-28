@Library('jenkins-shared-library')
def gv

pipeline {
    agent any
    tools {
        maven 'maven-3.9'
    }
    stages {
        stage('init') {
            steps {
                script {
                    gv = load "script.groovy"
                }
            }
        }
        stage('build app') {
            steps {
                script {
                    buildJar()
                }
            }
        }
        stage('build image') {
            steps {
                script {
                    buildImage('olekslutsenko23/demo-app:jma-3.0')
                    dockerLogin()
                    dockerPush('olekslutsenko23/demo-app:jma-3.0')
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
        stage('commit version update') {
            steps {
                script {
                    echo "Updating version in repo..."
                    // gv.updateVersion()
                }
            }
        }
    }
}

