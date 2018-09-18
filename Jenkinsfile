pipeline {
    options {
        buildDiscarder(logRotator(numToKeepStr: '3'))
    }
    agent none
    stages {
        stage('Initialise') {
            agent any            
            steps {
                deleteDir()
            }
        }
        stage('Back-end') {
            agent {
                docker { image 'openjdk:10-jdk-slim' }
            }
            steps {
                sh 'javac --version'
            }
        }
        stage('Front-end') {
            agent {
                docker { image 'kotlin-jdk:latest'
                         //args '-v ${PWD}:/usr/src/messageBroker.app -w /usr/src/messageBroker.app' not needed as Jenkins automatically mounts the workspace
                         reuseNode true
                       }
            }
            steps {
                sh '$KOTLIN_HOME/bin/kotlinc -version'
                sh 'gradle --version'
                sh 'gradle build'
                sh 'ls -l build/libs'
            }
        }
    }
}
