#!/usr/bin/env groovy

pipeline {
    agent any
    tools {
        maven 'Maven'
    }
    environment {
        // DOCKER_REPO_SERVER = '330673547330.dkr.ecr.eu-central-1.amazonaws.com'
        // DOCKER_REPO = "${DOCKER_REPO_SERVER}/java-maven-app"
        DOCKER_REPO = "olekslutsenko23/demo-app" 
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
                    env.IMAGE_NAME = "$version-$BUILD_NUMBER"
                }
            }
        }
        stage('build app') {
            steps {
                script {
                    echo 'building the application...'
                    sh 'mvn clean package'
                }
            }
        }
        stage('build image') {
            steps {
                script {
                    echo "building the docker image..."
                    withCredentials([usernamePassword(credentialsId: 'docker-hub-repo', passwordVariable: 'PASS', usernameVariable: 'USER')]){
                        sh "docker build -t ${DOCKER_REPO}:${IMAGE_NAME} ."
                        sh 'echo $PASS | docker login -u $USER --password-stdin ${DOCKER_REPO_SERVER}'
                        sh "docker push ${DOCKER_REPO}:${IMAGE_NAME}"
                    }
                }
            }
        }
        stage('deploy') {
            environment {
                AWS_ACCESS_KEY_ID = credentials('jenkins_aws_access_key_id')
                AWS_SECRET_ACCESS_KEY = credentials('aws_secret_access_key')
                APP_NAME = 'java-maven-app'
            }
            steps {
                script {
                   echo 'deploying docker image...'
                   sh 'envsubst < kubernetes/deployment.yaml | kubectl apply -f -'
                   sh 'envsubst < kubernetes/service.yaml | kubectl apply -f -'
                }
            }
        }
        // stage('commit version update') {
        //     steps {
        //         script {
        //             withCredentials([githubApp(credentialsId: 'Jenkins-lutsenko', appIdVariable: 'APP_ID', installationIdVariable: 'INSTALLATION_ID', privateKeyVariable: 'PRIVATE_KEY')]) {
        //                 sh '''
        //                 # Generate a JWT for GitHub App
        //                 JWT=$(ruby -rjson -ropenssl -securerandom -e '
        //                     payload = {
        //                     iat: Time.now.to_i - 60,
        //                     exp: Time.now.to_i + 600,
        //                     iss: ENV["APP_ID"]
        //                     }
        //                     key = OpenSSL::PKey::RSA.new(ENV["PRIVATE_KEY"])
        //                     puts JWT.encode(payload, key, "RS256")
        //                 ')

        //                 # Generate an installation token for the app
        //                 INSTALLATION_TOKEN=$(curl -s -X POST \
        //                     -H "Authorization: Bearer $JWT" \
        //                     -H "Accept: application/vnd.github+json" \
        //                     https://api.github.com/app/installations/$INSTALLATION_ID/access_tokens | jq -r .token)

        //                 # Save the token for Git operations
        //                 echo $INSTALLATION_TOKEN > token.txt
        //                 '''

        //                 // Read the token
        //                 def token = readFile('token.txt').trim()

        //                 sh 'git config --global user.email "jenkins@example.com"'
        //                 sh 'git config --global user.name "jenkins"'

        //                 // Use the installation token for authentication
        //                 sh "git remote set-url origin https://${token}@github.com/OleksandraLutsenko/jenkins.git"

        //                 sh 'git add .'
        //                 sh 'git commit -m "ci: version bump"'
        //                 sh 'git push origin HEAD:jenkins-jobs'
        //             }
        //         }
        //     }
        // }
    }
}
