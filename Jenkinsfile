pipeline {
    agent any
	
	tools {
        maven 'MAVEN3.9'
        jdk 'JDK17'
    }

    environment {
        AFFECTED_SERVICES = ""
        // BRANCH_NAME = ""
    }

    stages {
        stage('Detect Branch') {
            steps {
                script {
                    // env.BRANCH_NAME = sh(script: 'git rev-parse --abbrev-ref HEAD', returnStdout: true).trim()
                    // env.BRANCH_NAME = env.GIT_BRANCH
                    echo "Current Branch: ${env.BRANCH_NAME}"
                    echo "Current git Branch: ${env.GIT_BRANCH}"
                }
            }
        }

        stage('Detect Affected Services') {
            when {
                expression { return env.BRANCH_NAME != 'main' }
            }
            steps {
                script {
                    // sh 'git fetch origin main'
                    def services = sh(script: "ls -d spring-petclinic*/ | cut -f1 -d'/'", returnStdout: true).trim().split("\n")
                    def changedFiles = sh(script: "git diff --name-only HEAD^ HEAD", returnStdout: true).trim().split("\n")
                    // def changedFiles = sh(script: 'git diff --name-only origin/main', returnStdout: true).trim().split("\n")
                    def affectedServices = []

                    for (file in changedFiles) {
                        for (service in services) {
                            if (file.startsWith("${service}/")) {
                                affectedServices << service
                            }
                        }
                    }

                    AFFECTED_SERVICES = affectedServices.unique().join(',')
                    echo "Affected Services: ${AFFECTED_SERVICES}"
                }
            }
        }

        stage('Build and Test') {
            steps {
                script {
                    if (env.BRANCH_NAME == 'main') {
                        echo "Building all services after merge..."
                        sh "mvn clean package"
                    } else if (AFFECTED_SERVICES?.trim()) {
                        def affectedServices = AFFECTED_SERVICES.split(',')
                        for (service in affectedServices) {
                            echo "Building ${service}..."
                            sh """
                                cd ${service}
                                mvn clean package
                            """
                        }
                    } else {
                        echo "No affected services, skipping build."
                    }
                }
            }
        }

        stage('Publish Test Results & Coverage') {
            steps {
                script {
                    if (env.BRANCH_NAME == 'main' || AFFECTED_SERVICES?.trim()) {
                        def affectedServices = env.BRANCH_NAME == 'main' ? sh(script: "ls -d spring-petclinic*/ | cut -f1 -d'/'", returnStdout: true).trim().split("\n") : AFFECTED_SERVICES.split(',')
                        
                        for (service in affectedServices) {
                            echo "Publishing test results and coverage for ${service}..."
                            
                            // Publish JUnit test results
                            junit "${service}/target/surefire-reports/*.xml"

                            // Publish JaCoCo coverage report
                            jacoco execPattern: "${service}/target/jacoco.exec"
                        }
                    } else {
                        echo "No affected services, skipping test result publishing."
                    }
                }
            }
        }
    }
}
