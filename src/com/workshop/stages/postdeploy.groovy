#!/usr/bin/groovy
package com.workshop.stages
 
import com.workshop.Pipeline
import com.workshop.Config

// Health check apps with curl to apps
def healthcheck(Pipeline p) {
    def hostIp = sh script: "ip route show | awk '/default/ {print \$3}' | tr -d '\n'", returnStdout: true
 
    timeout(time: p.timeout_hc, unit: 'SECONDS'){
        waitUntil(quiet: true) {
            def response = sh script: "curl ${hostIp}:${p.app_port}/ping", returnStdout: true
            
            if (response != "pong!"){
                error("ERROR102 - Service is Unhealthy for last ${p.timeout_hc} Second")
            } else {
                println "Service is Healthy :D"
                return true
            }
        }
    }
}

def deleteImageBuild(Pipeline p){
    c = new Config()

    // withEnv(["PATH+DOCKER=${p.dockerTool}/bin"]){
    //     println "Docker Images"
    //     println $BUILD_NUMBER
    //     def response = sh script: "docker image ls &> /dev/null", returnStatus: true
    //     println response
    //     def danglingstatus = sh script: 'docker rmi $(docker images --filter "dangling=true" -q) ', returnStatus: true
    //     println danglingstatus
    //     // def image = docker.image("${p.docker_user}/${p.repository_name}:build-$BUILD_NUMBER")
    //     def rmi = sh script: "docker rmi ${p.docker_user}/${p.repository_name}:build-$BUILD_NUMBER"
    //     println rmi
    // }

    docker.withTool("${c.default_docker_jenkins_tool}") {
        println "Docker Container List"
        def containers = sh script: "docker ps -a &> /dev/null", returnStatus: true

        println "Stop Container ${p.docker_user}/${p.repository_name}:build-$BUILD_NUMBER"
        sh script: "docker rm ${p.docker_user}/${p.repository_name}:build-$BUILD_NUMBER &> /dev/null", returnStatus: true

        println "Docker Images"
        def images = sh script: "docker image ls &> /dev/null", returnStatus: true
        println images

        println "Docker Prune Dangling Images"
        def danglingstatus = sh script: 'docker rmi $(docker images --filter "dangling=true" -q) ', returnStatus: true
        println danglingstatus
        // def image = docker.image("${p.docker_user}/${p.repository_name}:build-$BUILD_NUMBER")
        println "Remove Image ${p.docker_user}/${p.repository_name}:build-$BUILD_NUMBER"
        def rmi = sh script: "docker rmi ${p.docker_user}/${p.repository_name}:build-$BUILD_NUMBER"
        println rmi   
    }
}
