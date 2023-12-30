def updateVersion() {
    withCredentials([string(credentialsId: '36f55b3e-9fa3-4f20-bea3-8d815e74f9f2', variable: 'TOKEN')]) {
        sh 'git config --global user.email "jenkins@example.com"'
        sh 'git config --global user.name "jenkins"'

        sh 'git status'
        sh 'git branch'
        sh 'git config --list'

        // Use the token for authentication in the remote URL
        sh "git remote set-url origin https://${TOKEN}@github.com/OleksandraLutsenko/jenkins.git"

        sh 'git add .'
        sh 'git commit -m "ci: version bump"'
        sh 'git push origin HEAD:main'
    }
}

return this
