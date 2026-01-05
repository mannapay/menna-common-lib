pipeline {
    agent any

    environment {
        JFROG_URL = credentials('jfrog-url')
        JFROG_CREDENTIALS = credentials('jfrog-credentials')
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    parameters {
        booleanParam(name: 'RELEASE', defaultValue: false, description: 'Perform a release build')
        string(name: 'RELEASE_VERSION', defaultValue: '', description: 'Release version (e.g., 1.0.0)')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    env.GIT_COMMIT_SHORT = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                }
            }
        }

        stage('Set Version') {
            when {
                expression { params.RELEASE && params.RELEASE_VERSION != '' }
            }
            steps {
                sh """
                    mvn versions:set -DnewVersion=${params.RELEASE_VERSION} -DgenerateBackupPoms=false
                """
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile -B'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test -B'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests -B'
            }
        }

        stage('Publish to JFrog') {
            steps {
                configFileProvider([configFile(fileId: 'maven-settings-jfrog', variable: 'MAVEN_SETTINGS')]) {
                    sh """
                        mvn deploy -DskipTests -B -s ${MAVEN_SETTINGS} \
                            -Djfrog.url=${JFROG_URL}
                    """
                }
            }
        }

        stage('Tag Release') {
            when {
                expression { params.RELEASE && params.RELEASE_VERSION != '' }
            }
            steps {
                sh """
                    git tag -a v${params.RELEASE_VERSION} -m "Release ${params.RELEASE_VERSION}"
                    git push origin v${params.RELEASE_VERSION}
                """
            }
        }
    }

    post {
        success {
            script {
                def version = params.RELEASE ? params.RELEASE_VERSION : "SNAPSHOT"
                echo "Successfully published menna-common-lib version ${version} to JFrog"
            }
        }
        failure {
            echo "Build failed! Check the logs for details."
        }
        always {
            cleanWs()
        }
    }
}
