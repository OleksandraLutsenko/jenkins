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
            when {
                expression {
                    BRANCH_NAME == "jenkins-job"
                }
            }
            steps {
                script {
                    echo 'deploying docker image...'
                }
            }
        }
    }
}
