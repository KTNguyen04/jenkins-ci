pipeline {
    agent any
	
	tools {
        maven 'MAVEN3.9'
        jdk 'JDK17'
    }

    environment {
        AFFECTED_SERVICES = ""
        BRANCH_NAME = ""
    }

    stages {
        stage('Detect Branch') {
            steps {
                script {
                    env.BRANCH_NAME = sh(script: 'git rev-parse --abbrev-ref HEAD', returnStdout: true).trim()
                    echo "Current Branch: ${env.BRANCH_NAME}"
                }
            }
        }

        stage('Detect Affected Services') {
            when {
                expression { return env.BRANCH_NAME != 'main' }
            }
            steps {
                script {
                    def services = sh(script: "ls -d spring-petclinic*/ | cut -f1 -d'/'", returnStdout: true).trim().split("\n")
                    def changedFiles = sh(script: 'git diff --name-only origin/main', returnStdout: true).trim().split("\n")
                    def affectedServices = []

                    for (file in changedFiles) {
                        for (service in services) {
                            if (file.startsWith("${service}/")) {
                                affectedServices << service
                            }
                        }
                    }

                    env.AFFECTED_SERVICES = affectedServices.unique().join(',')
                    echo "Affected Services: ${env.AFFECTED_SERVICES}"
                }
            }
        }

        stage('Build and Test') {
            steps {
                script {
                    if (env.BRANCH_NAME == 'main') {
                        echo "Building all services after merge..."
                        sh "mvn clean package"
                    } else if (env.AFFECTED_SERVICES?.trim()) {
                        def affectedServices = env.AFFECTED_SERVICES.split(',')
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
                    if (env.BRANCH_NAME == 'main' || env.AFFECTED_SERVICES?.trim()) {
                        def affectedServices = env.BRANCH_NAME == 'main' ? sh(script: "ls -d spring-petclinic*/ | cut -f1 -d'/'", returnStdout: true).trim().split("\n") : env.AFFECTED_SERVICES.split(',')
                        
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
