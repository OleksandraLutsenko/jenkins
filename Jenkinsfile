pipeline {
    agent any
    stages {
        stage('test') {
            steps {
                script {
                    echo 'building the application...'
                    echo "Executing pipeline for branch $BRANCH_NAME"
                }
            }
        }

        stage('build') {
            when {
                expression {
                    BRANCH_NAME == "jenkins-job"
                }
            }
            steps {
                script {
                    echo "testing the application..."
                }
            }
        }

        stage('deploy') {
            steps {
                script {
                    def dockerCmd = 'docker run -p 3080:3080 -d olekslutsenko23/demo-app:jma-1.0'
                    sshagent(['ec2-server-key']) {
                        sh "ssh -o StrictHostKeyChecking=no ec2-user@3.79.186.218 ${dockerCmd}"
                    }
                }
            }
        }
    }
}
