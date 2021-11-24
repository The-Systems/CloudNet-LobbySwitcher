pipeline {
    agent any
    tools {
        maven 'Maven'
        jdk 'JDK8'
    }
    options {
        buildDiscarder(logRotator(artifactNumToKeepStr: '5'))
    }
    stages {
        stage ('Build') {
            steps {
                sh 'mvn clean package'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'cloudnet2/target/CloudNet2-LobbySwitcher.jar', fingerprint: true
                    archiveArtifacts artifacts: 'cloudnet3/target/CloudNet3-LobbySwitcher.jar', fingerprint: true
                }
            }
        }
    }

    post {
        always {
            deleteDir()
        }
    }
}
