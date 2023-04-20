## Deployment
#### To stop the existing container
```
DOCKER_HOST="tcp://192.168.0.4:2375" docker-compose down
```

#### To build and deploy new container
```
DOCKER_HOST="tcp://192.168.0.4:2375" docker-compose up --build -d 
```
