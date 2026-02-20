from django.urls import path
from . import views


app_name = 'odoo_config'

urlpatterns = [
    path('', views.config_view, name='config_view'),
]