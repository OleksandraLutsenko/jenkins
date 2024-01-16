#!/usr/bin/env groovy

library identifier: 'jenkins-shared-lib@master', retriever: modernSCM(
    [$class: 'GitSCMSource',
    remote: 'https://github.com/OleksandraLutsenko/jenkins-shared-lib.git',
    credentialsID: 'docker-hub-repo'
    ]
)

pipeline {
    agent any
    tools {
        maven 'maven-3.9'
    }
    stages {
        stage('increment version') {
            steps {
                script {
                    echo 'incrementing app version...'
                    sh 'mvn build-helper:parse-version versions:set \
                        -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} \
                        versions:commit'
                    def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
                    def version = matcher[0][1]
                    env.VERSION = "$version-$BUILD_NUMBER"
                    env.IMAGE_NAME = "olekslutsenko23/demo-app:${env.VERSION}"
                }
            }
        }
        stage('build app') {
            steps {
                echo 'building application jar...'
                buildJar()
            }
        }
        stage('build image') {
            steps {
                script {
                    echo 'building the docker image...'
                    buildImage(env.IMAGE_NAME)
                    dockerLogin()
                    dockerPush(env.IMAGE_NAME)
                }
            }
        } 
        stage("deploy") {
            steps {
                script {
                    echo 'deploying docker image to EC2...'
                    
                    // def shellCmd = "bash ./server-cmds.sh ${IMAGE_NAME}"
                    def ec2Instance = "ec2-user@3.125.46.71"

                    def dockerCmd = "docker run -p 8080:8080 -d ${IMAGE_NAME}"
                    sshagent(['ec2-server-key']) {
                        sh "ssh -o StrictHostKeyChecking=no ec2-user@3.125.46.71 ${dockerCmd}"
                        // sh "scp server-cmds.sh ${ec2Instance}:/home/ec2-user"
                        // sh "scp docker-compose.yaml ${ec2Instance}:/home/ec2-user"
                        // sh "ssh -o StrictHostKeyChecking=no ${ec2Instance} ${shellCmd}"
                    }
                }
            }               
        }
        stage('commit version update') {
            steps {
                script {
                    withCredentials([string(credentialsId: '36f55b3e-9fa3-4f20-bea3-8d815e74f9f2', variable: 'TOKEN')]) {

                    // Use the token for authentication in the remote URL
                        sh "git remote set-url origin https://${TOKEN}@github.com/OleksandraLutsenko/jenkins.git"
                        sh 'git add .'
                        sh 'git commit -m "ci: version bump"'
                        sh 'git push origin HEAD:jenkins-deploy'
                    }
                }
            }
        }
    }
    post {
        success {
            emailext (
                subject: "Your Jenkins Build Succeeded: ${currentBuild.fullDisplayName}",
                body: "The build of ${env.JOB_NAME} #${env.BUILD_NUMBER} is successful.",
                to: "aleksandra.lutsenko23@gmail.com"
            )
        }
        failure {
            emailext (
                subject: "Your Jenkins Build Failed: ${currentBuild.fullDisplayName}",
                body: "The build of ${env.JOB_NAME} #${env.BUILD_NUMBER} has failed.\n\nConsole Output:\n${currentBuild.rawBuild.getLog(maxLines = 500)}",
                to: "aleksandra.lutsenko23@gmail.com"
            )
        }
    }
}
