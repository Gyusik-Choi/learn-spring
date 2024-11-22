import random
from locust import task, FastHttpUser, stats

stats.PERCENTILES_TO_CHART = [0.95, 0.99]

class HelloWorld(FastHttpUser):
    connection_timeout = 10.0
    network_timeout = 10.0

    @task
    def hello(self):
        payload = {
            "couponId": 1,
            "userId": random.random(1, 10000000),
        }
        with self.rest("POST", "v1/issue", json=payload):
            pass
