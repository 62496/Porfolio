from django.db import models
from django.conf import settings

# Create your models here.

class CandidateProfile(models.Model):
    user = models.OneToOneField(settings.AUTH_USER_MODEL, on_delete=models.CASCADE)
    odoo_applicant_id = models.IntegerField(null=True, blank=True, help_text='ID de l applicant dans Odoo')

    def __str__(self):
        return f"CandidateProfile(user={self.user.username}, odoo_applicant_id={self.odoo_applicant_id})"
