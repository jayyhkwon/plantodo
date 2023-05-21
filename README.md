# PLANTODO
할 일 관리 웹 페이지

## Environments
- Language : java ("11.0.11" 2021-04-20 LTS)
- IDE : IntelliJ Ultimate 2021.2
- Framework : SpringBoot 2.5.6
- Database : MariaDB 10.11 (java-client 2.7.2)
- ORM : JPA (hibernate)

## Features
- 회원가입, 로그인
- 일정 관리
- 할 일 관리
- 메모 관리
- 푸시 알림

## Architecture

## Link
- 웹사이트

## Screenshots

## Quick Start
(230521 현재 .yml파일이 모두 gitignore되어 있어 실제로 사용은 불가능합니다.)

Ubuntu 기준 (Windows의 경우 WSL2 사용)

#### Local Environment (Docker X)
#### Local Environment (Docker, Docker Desktop O)
#### Local Environment (Docker, Docker Desktop X)
Prerequisite
- java, git이 설치되어 있어야 합니다.
```
// apt-get 업데이트
sudo apt-get update
sudo apt-get upgrade

// OpenJDK 설치
sudo apt install openjdk-11-jdk

// Git 설치
sudo yum install git
```

- docker engine, docker compose plugin이 설치되어 있어야 합니다.
```
sudo apt-get remove docker docker-engine docker.io containerd runc

// host machine에 처음 설치하는 경우 docker repository setup 필요
sudo apt-get update
sudo apt-get install ca-certificates curl gnupg

// official GPG key 받기
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg

// repository setup
echo \
  "deb [arch="$(dpkg --print-architecture)" signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  "$(. /etc/os-release && echo "$VERSION_CODENAME")" stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

// docker engine (+ docker compose plugin) 설치
sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```

2. git clone
```
git clone https://github.com/yeonleaf/plantodo.git
```

3. .jar 파일 생성
```
chmod +x gradlew
./gradlew clean build +x test
```

4. 디렉토리로 이동
```
cd plantodo
```

5. 실행
```
docker compose up --build
```

6. -> localhost:8080



