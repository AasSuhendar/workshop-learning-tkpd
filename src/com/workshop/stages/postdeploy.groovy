#!/usr/bin/groovy
package com.workshop.stages
 
import com.workshop.Pipeline

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
    withEnv(["PATH+DOCKER=${p.dockerTool}/bin"]){
        println "Docker Images"
        def response = sh script: "docker image ls &> /dev/null", returnStatus: true
        println response
    }
}
