from django.db import models
from django.db import models
# Create your models here.

class OdooConfig(models.Model):
    url = models.CharField(max_length=200)
    db = models.CharField(max_length=100)
    username = models.CharField(max_length=100)
    password = models.CharField(max_length=100)

    def __str__(self):
        return self.url