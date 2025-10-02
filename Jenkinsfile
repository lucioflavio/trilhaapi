#!groovy

@Library("bnb-pipeline-library@test-sonar-step") _
import groovy.json.*;
import groovy.json.JsonSlurper
import java.net.URL
pipeline {
  agent {
    label 'jdk21'
  }
  options {
    disableConcurrentBuilds()
    parallelsAlwaysFailFast()
  }
  environment {
    CI = 'false'
  }
  stages {
    stage('1.Preparação do Ambiente') {
      steps {
        echo "$BRANCH_STREAM"
        echo "Ajustando valores de propriedades"
        script {
          currentBuild.displayName = "#${currentBuild.id} - $BRANCH_STREAM"
          env.setProperty("JOB_DIRETORIO", getProperty("team.scm.fetchDestination"))
          env.setProperty("JOB_CAMINHO_APLICACAO", getProperty("JOB_DIRETORIO") + "/" + getProperty("JOB_COMPONENTE"))
          if (getProperty("JOB_PASTA_APLICACAO") != "") {
            env.setProperty("JOB_CAMINHO_APLICACAO", getProperty("JOB_CAMINHO_APLICACAO") + "/" + getProperty("JOB_PASTA_APLICACAO"))
          }
          env.AMBIENTE = "desenvolvimento"
          if (getProperty("BRANCH_STREAM").contains("RELEASE")) {
            env.AMBIENTE = "release"
          }
          env.SXXX = getProperty("SXXX")
          env.APLICACAO = getProperty("APLICACAO")
          env.DEPLOY_DEV = "true"
          env.DEPLOY_TST = "false"
          env.DEPLOY_PRD = "false"
          env.STAGE_2 = "true"
          env.STAGE_3 = "true"
          env.STAGE_4 = "true"
          env.STAGE_5 = "true"
          if (getProperty("IMAGE_VERSION") != "") {
            if (getProperty("IMAGE_VERSION") != "0.0.0-0") {
              env.IMAGE_VERSION = getProperty("IMAGE_VERSION")
              env.DEPLOY_DEV = "false"
              env.STAGE_2 = "false"
              env.STAGE_3 = "false"
              env.STAGE_4 = "false"
              env.STAGE_5 = "false"
            }
            env.DEPLOY_TST = "true"
          }
          echo "Definindo propriedades da aplicacao"
          env.setProperty("NEXUS_IMAGE_NAME", env.SXXX + "/" + env.APLICACAO)
          env.setProperty("GIT_REPO_NAME", env.APLICACAO);
          env.setProperty("HC_VERSION", "")
          env.setProperty("HC_BUILDED", "")
          echo "GIT_REPO_NAME:" + env.APLICACAO + " \n" +
            "NEXUS_IMAGE_NAME:" + env.SXXX + "/" + env.APLICACAO + " \n" +
            "AMBIENTE:" + env.AMBIENTE + " \n" +
            "DEPLOY_DEV:" + env.DEPLOY_DEV + " \n" +
            "DEPLOY_TST:" + env.DEPLOY_TST + " \n"
        }
        checkout([
          $class: 'RTCScm',
          avoidUsingToolkit: false,
          buildType: [
            buildStream: "$BRANCH_STREAM",
            customizedSnapshotName: "$JOB_COMPONENTE $BUILD_TIMESTAMP (Construção - $BRANCH_STREAM - #${currentBuild.id})",
            overrideDefaultSnapshotName: true,
            buildSnapshotContext: [
              snapshotOwnerType: 'none',
              processAreaOfOwningStream: "$PROJECT_AREA",
              owningStream: "$BRANCH_STREAM"
            ],
            currentSnapshotOwnerType: 'none',
            value: 'buildStream',
            processArea: "$PROJECT_AREA",
            buildStream: "$BRANCH_STREAM",
            loadDirectory: "$JOB_DIRETORIO",
            clearLoadDirectory: true,
            loadPolicy: 'useComponentLoadConfig',
            createFoldersForComponents: true,
            componentLoadConfig: 'excludeSomeComponents',
            componentsToExclude: ''
          ],
          timeout: 480
        ])
      }
    }
    stage('2.Construção') {
      when {
        environment name: 'STAGE_2', value: 'true'
      }
      parallel {
        stage('2.1.Compilando') {
          steps {
            dir("../$env.JOB_BASE_NAME/$JOB_CAMINHO_APLICACAO") {
              sh "mvn -s /root/.m2/settings.xml -q -Duser.home=$JENKINS_HOME/workspace clean compile"
              script {
                try {
                  def versao = sh(script: 'mvn help:evaluate -Dexpression=project.version -Duser.home=$JENKINS_HOME/workspace -q -DforceStdout', returnStdout: true).trim()
                  env.setProperty("VERSAO_POM", versao)
                } catch (Exception ex) {
                  println("Erro ao obter versão do POM: ${ex}")
                }
                echo "Versão do POM: $VERSAO_POM"
              }
            }
          }
        }
        stage('2.2.Executando Testes Unitários') {
          steps {
            dir("../$env.JOB_BASE_NAME/$JOB_CAMINHO_APLICACAO") {
              sh "mvn -s /root/.m2/settings.xml -Duser.home=$JENKINS_HOME/workspace test -Djava.awt.headless=true"
            }
          }
        }
      }
    } /*
    stage('3.Verificando Sonar') {
      when {
        environment name: 'STAGE_3', value: 'true'
      }
      steps {
        realizarAvaliacaoSonar()
      }
    }
    */
    stage('4.Empacotando Aplicação') {
      when {
        environment name: 'STAGE_4', value: 'true'
      }
      steps {
        dir("../$env.JOB_BASE_NAME/$JOB_CAMINHO_APLICACAO") {
          sh "mvn -s /root/.m2/settings.xml -q -Duser.home=$JENKINS_HOME/workspace package -Dmaven.test.skip=true -DskipTests"
          script {
            try {
              def build_version = sh(script: 'cat target/classes/versao.properties | grep build.version | cut -c 15-20', returnStdout: true).trim()
              env.setProperty("HC_VERSION", build_version)
              def build_builded = sh(script: 'cat target/classes/versao.properties | grep build.builded | cut -c 15-100', returnStdout: true).trim()
              env.setProperty("HC_BUILDED", build_builded)
            } catch (Exception ex) {
              println("Erro ao obter Meta Dados do arquivo versao.properties")
            }
            echo "HC version: $HC_VERSION" + " \n" + "HC builded: $HC_BUILDED"
          }
        }
      }
    }
    stage('5.Gerando Imagem Nexus') {
      when {
        environment name: 'STAGE_2', value: 'true'
      }
      steps {
        script {
          currentBuild.displayName = "#${currentBuild.id} - ${VERSAO_POM}-${currentBuild.id} - $BRANCH_STREAM"
          env.setProperty("IMAGE_VERSION", getProperty("VERSAO_POM") + "-" + getProperty("BUILD_NUMBER"))
          echo "Gerando a imagem $NEXUS_IMAGE_NAME com a versão $VERSAO_POM-$currentBuild.id \n"
        }
        construirImagem(getProperty("NEXUS_IMAGE_NAME"), getProperty("VERSAO_POM"), getProperty("BUILD_NUMBER"))
      }
    }
    stage('6.Realizando Deploy DREADS') {
      when {
        environment name: 'AMBIENTE', value: 'desenvolvimento'
      }
      parallel {
        stage('6.1.Deploy DEV.OCP') {
          when {
            environment name: 'DEPLOY_DEV', value: 'true'
          }
          steps {
            script {
              deployServidorDREADS(getProperty("IMAGE_VERSION"), getProperty("GIT_REPO_NAME"), 'desenvolvimento')
            }
          }
        }
        stage('6.2.Deploy TST.OCP') {
          when {
            environment name: 'DEPLOY_TST', value: 'true'
          }
          steps {
            script {
              deployServidorDREADS(getProperty("IMAGE_VERSION"), getProperty("GIT_REPO_NAME"), 'testes-release')
            }
          }
        }
      }
    }
    stage('7.Realizando Deploy CAPGV HML.OCP') {
      when {
        environment name: 'AMBIENTE', value: 'release'
      }
      steps {
        script {
          deployServidorCAPGV(getProperty("VERSAO_POM"), getProperty("BUILD_NUMBER"), getProperty("GIT_REPO_NAME"), 'release')
        }
      }
    }
    //Realizando deploy capgv (servidor de produção)
    stage('8.Deploy PRD.OCP.CAPGV') {
      when { environment(name: 'DEPLOY_PRD', value: 'true') }
      steps {
        script{
          deployImagemCAPGV(getProperty("IMAGE_VERSION"),getProperty("GIT_REPO_NAME"))
        }
      }
    }
  }
}
def construirImagem(tag, versao, build) {
  dir("../$env.JOB_BASE_NAME/$JOB_DIRETORIO/$JOB_COMPONENTE") {
    def image
    echo "Registrando o docker no ambiente $ambiente"
    docker.withRegistry('https://ocp-registry.dreads.bnb', '26991b27-10da-47b2-bdc4-e7b6ae67dd7c') {
      echo "Construindo imagem"
      if (getProperty("BRANCH_STREAM").contains("RELEASE") || getProperty("AMBIENTE").equalsIgnoreCase("PRODUCAO")) {
        perimetro = 'release/'
      } else {
        perimetro = 'dev/'
      }
      sh "docker images"
      image = docker.build(perimetro + tag + ":" + versao + "-" + build, "--cpuset-cpus 0,1 --force-rm  -f ./Dockerfile ./")
      echo "Publicado no Nexus"
      image.push()
      sh "docker rmi " + perimetro + "" + tag + ":" + versao + "-" + build + ""
      sh "docker rmi ocp-registry.dreads.bnb/" + perimetro + "" + tag + ":" + versao + "-" + build + ""
    }
  }
}
def deployServidorDREADS(image_version, repositorio, branch) {
  script {
    def handle = triggerRemoteJob(
      remoteJenkinsName: 'jenkins-pipelines.tst.ocp.dreads.bnb',
      job: 'Dreads-Pipeline',
      parameters: 'tag_version=' + image_version + '\ngit_branch=' + branch + '\ngit_repo_name=' + repositorio + '-config' + '\ngit_repo_url=https://gitlab.dreads.bnb/openshift/' + repositorio + '-config.git'
    )
    def status = handle.getBuildStatus()
    def buildUrl = handle.getBuildUrl()
    echo buildUrl.toString() + " finished with " + status.toString()
  }
}
def deployServidorCAPGV(versao, build, repositorio, branch) {
  script {
    def handle = triggerRemoteJob(
      remoteJenkinsName: 'jenkins.hml.ocp.capgv.intra.bnb',
      job: 'Capgv-Pipeline',
      parameters: 'tag_version=' + versao + '-' + build + '\ngit_branch=' + branch + '\ngit_repo_name=' + repositorio + '-config' + '\ngit_repo_url=https://s1gitp01.capgv.intra.bnb/openshift/' + repositorio + '-config.git'
    )
    def status = handle.getBuildStatus()
    def buildUrl = handle.getBuildUrl()
    echo buildUrl.toString() + " finished with " + status.toString()
  }
}