# Запуск

```shell
 .\gradlew :akka-app:run
```

# Запуск через compose

```shell
.\gradlew composeUp # Запустить контейнеры
.\gradlew composeLogs # Забрать логи (см в akka-app/build/containers-logs)
.\gradlew composeDown # Остановить контейнеры с дальнейшим удалением
```

# Endpoints
[Страничка с gravatar](http://localhost:8080/gravatar?name=user)

[Получить gravatar](http://localhost:8081/monster/user?size=200)

[Kamon status page](http://localhost:5266/#/)

[Метрики для gravatar-app](http://localhost:9095/metrics)

[Prometheus status page](http://localhost:9090/)

[Grafana dashboard](http://localhost:3000/)
